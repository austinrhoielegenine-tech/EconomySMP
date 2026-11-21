package com.neovisionaries.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class ReadingThread extends WebSocketThread {
   private boolean mStopRequested;
   private WebSocketFrame mCloseFrame;
   private List<WebSocketFrame> mContinuation = new ArrayList();
   private final PerMessageCompressionExtension mPMCE;
   private Object mCloseLock = new Object();
   private Timer mCloseTimer;
   private CloseTask mCloseTask;
   private long mCloseDelay;
   private boolean mNotWaitForCloseFrame;

   public ReadingThread(WebSocket websocket) {
      super("ReadingThread", websocket, ThreadType.READING_THREAD);
      this.mPMCE = websocket.getPerMessageCompressionExtension();
   }

   public void runMain() {
      try {
         this.main();
      } catch (Throwable t) {
         WebSocketException cause = new WebSocketException(WebSocketError.UNEXPECTED_ERROR_IN_READING_THREAD, "An uncaught throwable was detected in the reading thread: " + t.getMessage(), t);
         ListenerManager manager = this.mWebSocket.getListenerManager();
         manager.callOnError(cause);
         manager.callOnUnexpectedError(cause);
      }

      this.notifyFinished();
   }

   private void main() {
      this.mWebSocket.onReadingThreadStarted();

      boolean keepReading;
      do {
         synchronized(this) {
            if (this.mStopRequested) {
               break;
            }
         }

         WebSocketFrame frame = this.readFrame();
         if (frame == null) {
            break;
         }

         keepReading = this.handleFrame(frame);
      } while(keepReading);

      this.waitForCloseFrame();
      this.cancelClose();
   }

   void requestStop(long closeDelay) {
      synchronized(this) {
         if (this.mStopRequested) {
            return;
         }

         this.mStopRequested = true;
      }

      this.interrupt();
      this.mCloseDelay = closeDelay;
      this.scheduleClose();
   }

   private void callOnFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnFrame(frame);
   }

   private void callOnContinuationFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnContinuationFrame(frame);
   }

   private void callOnTextFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnTextFrame(frame);
   }

   private void callOnBinaryFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnBinaryFrame(frame);
   }

   private void callOnCloseFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnCloseFrame(frame);
   }

   private void callOnPingFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnPingFrame(frame);
   }

   private void callOnPongFrame(WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnPongFrame(frame);
   }

   private void callOnTextMessage(byte[] data) {
      if (this.mWebSocket.isDirectTextMessage()) {
         this.mWebSocket.getListenerManager().callOnTextMessage(data);
      } else {
         try {
            String message = Misc.toStringUTF8(data);
            this.callOnTextMessage(message);
         } catch (Throwable t) {
            WebSocketException wse = new WebSocketException(WebSocketError.TEXT_MESSAGE_CONSTRUCTION_ERROR, "Failed to convert payload data into a string: " + t.getMessage(), t);
            this.callOnError(wse);
            this.callOnTextMessageError(wse, data);
         }

      }
   }

   private void callOnTextMessage(String message) {
      this.mWebSocket.getListenerManager().callOnTextMessage(message);
   }

   private void callOnBinaryMessage(byte[] message) {
      this.mWebSocket.getListenerManager().callOnBinaryMessage(message);
   }

   private void callOnError(WebSocketException cause) {
      this.mWebSocket.getListenerManager().callOnError(cause);
   }

   private void callOnFrameError(WebSocketException cause, WebSocketFrame frame) {
      this.mWebSocket.getListenerManager().callOnFrameError(cause, frame);
   }

   private void callOnMessageError(WebSocketException cause, List<WebSocketFrame> frames) {
      this.mWebSocket.getListenerManager().callOnMessageError(cause, frames);
   }

   private void callOnMessageDecompressionError(WebSocketException cause, byte[] compressed) {
      this.mWebSocket.getListenerManager().callOnMessageDecompressionError(cause, compressed);
   }

   private void callOnTextMessageError(WebSocketException cause, byte[] data) {
      this.mWebSocket.getListenerManager().callOnTextMessageError(cause, data);
   }

   private WebSocketFrame readFrame() {
      WebSocketFrame frame = null;
      WebSocketException wse = null;

      try {
         frame = this.mWebSocket.getInput().readFrame();
         this.verifyFrame(frame);
         return frame;
      } catch (InterruptedIOException e) {
         if (this.mStopRequested) {
            return null;
         }

         wse = new WebSocketException(WebSocketError.INTERRUPTED_IN_READING, "Interruption occurred while a frame was being read from the web socket: " + e.getMessage(), e);
      } catch (IOException e) {
         if (this.mStopRequested && this.isInterrupted()) {
            return null;
         }

         wse = new WebSocketException(WebSocketError.IO_ERROR_IN_READING, "An I/O error occurred while a frame was being read from the web socket: " + e.getMessage(), e);
      } catch (WebSocketException e) {
         wse = e;
      }

      boolean error = true;
      if (wse instanceof NoMoreFrameException) {
         this.mNotWaitForCloseFrame = true;
         if (this.mWebSocket.isMissingCloseFrameAllowed()) {
            error = false;
         }
      }

      if (error) {
         this.callOnError(wse);
         this.callOnFrameError(wse, frame);
      }

      WebSocketFrame closeFrame = this.createCloseFrame(wse);
      this.mWebSocket.sendFrame(closeFrame);
      return null;
   }

   private void verifyFrame(WebSocketFrame frame) throws WebSocketException {
      this.verifyReservedBits(frame);
      this.verifyFrameOpcode(frame);
      this.verifyFrameMask(frame);
      this.verifyFrameFragmentation(frame);
      this.verifyFrameSize(frame);
   }

   private void verifyReservedBits(WebSocketFrame frame) throws WebSocketException {
      if (!this.mWebSocket.isExtended()) {
         this.verifyReservedBit1(frame);
         this.verifyReservedBit2(frame);
         this.verifyReservedBit3(frame);
      }
   }

   private void verifyReservedBit1(WebSocketFrame frame) throws WebSocketException {
      if (this.mPMCE != null) {
         boolean verified = this.verifyReservedBit1ForPMCE(frame);
         if (verified) {
            return;
         }
      }

      if (frame.getRsv1()) {
         throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV1 bit of a frame is set unexpectedly.");
      }
   }

   private boolean verifyReservedBit1ForPMCE(WebSocketFrame frame) throws WebSocketException {
      return frame.isTextFrame() || frame.isBinaryFrame();
   }

   private void verifyReservedBit2(WebSocketFrame frame) throws WebSocketException {
      if (frame.getRsv2()) {
         throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV2 bit of a frame is set unexpectedly.");
      }
   }

   private void verifyReservedBit3(WebSocketFrame frame) throws WebSocketException {
      if (frame.getRsv3()) {
         throw new WebSocketException(WebSocketError.UNEXPECTED_RESERVED_BIT, "The RSV3 bit of a frame is set unexpectedly.");
      }
   }

   private void verifyFrameOpcode(WebSocketFrame frame) throws WebSocketException {
      switch (frame.getOpcode()) {
         case 0:
         case 1:
         case 2:
         case 8:
         case 9:
         case 10:
            return;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            if (!this.mWebSocket.isExtended()) {
               throw new WebSocketException(WebSocketError.UNKNOWN_OPCODE, "A frame has an unknown opcode: 0x" + Integer.toHexString(frame.getOpcode()));
            }
      }
   }

   private void verifyFrameMask(WebSocketFrame frame) throws WebSocketException {
      if (frame.getMask()) {
         throw new WebSocketException(WebSocketError.FRAME_MASKED, "A frame from the server is masked.");
      }
   }

   private void verifyFrameFragmentation(WebSocketFrame frame) throws WebSocketException {
      if (frame.isControlFrame()) {
         if (!frame.getFin()) {
            throw new WebSocketException(WebSocketError.FRAGMENTED_CONTROL_FRAME, "A control frame is fragmented.");
         }
      } else {
         boolean continuationExists = this.mContinuation.size() != 0;
         if (frame.isContinuationFrame()) {
            if (!continuationExists) {
               throw new WebSocketException(WebSocketError.UNEXPECTED_CONTINUATION_FRAME, "A continuation frame was detected although a continuation had not started.");
            }
         } else if (continuationExists) {
            throw new WebSocketException(WebSocketError.CONTINUATION_NOT_CLOSED, "A non-control frame was detected although the existing continuation had not been closed.");
         }
      }
   }

   private void verifyFrameSize(WebSocketFrame frame) throws WebSocketException {
      if (frame.isControlFrame()) {
         byte[] payload = frame.getPayload();
         if (payload != null) {
            if (125 < payload.length) {
               throw new WebSocketException(WebSocketError.TOO_LONG_CONTROL_FRAME_PAYLOAD, "The payload size of a control frame exceeds the maximum size (125 bytes): " + payload.length);
            }
         }
      }
   }

   private WebSocketFrame createCloseFrame(WebSocketException wse) {
      int closeCode;
      switch (wse.getError()) {
         case INSUFFICENT_DATA:
         case INVALID_PAYLOAD_LENGTH:
         case NO_MORE_FRAME:
            closeCode = 1002;
            break;
         case TOO_LONG_PAYLOAD:
         case INSUFFICIENT_MEMORY_FOR_PAYLOAD:
            closeCode = 1009;
            break;
         case NON_ZERO_RESERVED_BITS:
         case UNEXPECTED_RESERVED_BIT:
         case UNKNOWN_OPCODE:
         case FRAME_MASKED:
         case FRAGMENTED_CONTROL_FRAME:
         case UNEXPECTED_CONTINUATION_FRAME:
         case CONTINUATION_NOT_CLOSED:
         case TOO_LONG_CONTROL_FRAME_PAYLOAD:
            closeCode = 1002;
            break;
         case INTERRUPTED_IN_READING:
         case IO_ERROR_IN_READING:
            closeCode = 1008;
            break;
         default:
            closeCode = 1008;
      }

      return WebSocketFrame.createCloseFrame(closeCode, wse.getMessage());
   }

   private boolean handleFrame(WebSocketFrame frame) {
      this.callOnFrame(frame);
      switch (frame.getOpcode()) {
         case 0:
            return this.handleContinuationFrame(frame);
         case 1:
            return this.handleTextFrame(frame);
         case 2:
            return this.handleBinaryFrame(frame);
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            return true;
         case 8:
            return this.handleCloseFrame(frame);
         case 9:
            return this.handlePingFrame(frame);
         case 10:
            return this.handlePongFrame(frame);
      }
   }

   private boolean handleContinuationFrame(WebSocketFrame frame) {
      this.callOnContinuationFrame(frame);
      this.mContinuation.add(frame);
      if (!frame.getFin()) {
         return true;
      } else {
         byte[] data = this.getMessage(this.mContinuation);
         if (data == null) {
            return false;
         } else {
            if (((WebSocketFrame)this.mContinuation.get(0)).isTextFrame()) {
               this.callOnTextMessage(data);
            } else {
               this.callOnBinaryMessage(data);
            }

            this.mContinuation.clear();
            return true;
         }
      }
   }

   private byte[] getMessage(List<WebSocketFrame> frames) {
      byte[] data = this.concatenatePayloads(this.mContinuation);
      if (data == null) {
         return null;
      } else {
         if (this.mPMCE != null && ((WebSocketFrame)frames.get(0)).getRsv1()) {
            data = this.decompress(data);
         }

         return data;
      }
   }

   private byte[] concatenatePayloads(List<WebSocketFrame> frames) {
      Throwable cause;
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         for(WebSocketFrame frame : frames) {
            byte[] payload = frame.getPayload();
            if (payload != null && payload.length != 0) {
               baos.write(payload);
            }
         }

         return baos.toByteArray();
      } catch (IOException e) {
         cause = e;
      } catch (OutOfMemoryError e) {
         cause = e;
      }

      WebSocketException wse = new WebSocketException(WebSocketError.MESSAGE_CONSTRUCTION_ERROR, "Failed to concatenate payloads of multiple frames to construct a message: " + cause.getMessage(), cause);
      this.callOnError(wse);
      this.callOnMessageError(wse, frames);
      WebSocketFrame frame = WebSocketFrame.createCloseFrame(1009, wse.getMessage());
      this.mWebSocket.sendFrame(frame);
      return null;
   }

   private byte[] getMessage(WebSocketFrame frame) {
      byte[] payload = frame.getPayload();
      if (this.mPMCE != null && frame.getRsv1()) {
         payload = this.decompress(payload);
      }

      return payload;
   }

   private byte[] decompress(byte[] input) {
      try {
         return this.mPMCE.decompress(input);
      } catch (WebSocketException e) {
         this.callOnError(e);
         this.callOnMessageDecompressionError(e, input);
         WebSocketFrame frame = WebSocketFrame.createCloseFrame(1003, e.getMessage());
         this.mWebSocket.sendFrame(frame);
         return null;
      }
   }

   private boolean handleTextFrame(WebSocketFrame frame) {
      this.callOnTextFrame(frame);
      if (!frame.getFin()) {
         this.mContinuation.add(frame);
         return true;
      } else {
         byte[] payload = this.getMessage(frame);
         this.callOnTextMessage(payload);
         return true;
      }
   }

   private boolean handleBinaryFrame(WebSocketFrame frame) {
      this.callOnBinaryFrame(frame);
      if (!frame.getFin()) {
         this.mContinuation.add(frame);
         return true;
      } else {
         byte[] payload = this.getMessage(frame);
         this.callOnBinaryMessage(payload);
         return true;
      }
   }

   private boolean handleCloseFrame(WebSocketFrame frame) {
      StateManager manager = this.mWebSocket.getStateManager();
      this.mCloseFrame = frame;
      boolean stateChanged = false;
      synchronized(manager) {
         WebSocketState state = manager.getState();
         if (state != WebSocketState.CLOSING && state != WebSocketState.CLOSED) {
            manager.changeToClosing(StateManager.CloseInitiator.SERVER);
            this.mWebSocket.sendFrame(frame);
            stateChanged = true;
         }
      }

      if (stateChanged) {
         this.mWebSocket.getListenerManager().callOnStateChanged(WebSocketState.CLOSING);
      }

      this.callOnCloseFrame(frame);
      return false;
   }

   private boolean handlePingFrame(WebSocketFrame frame) {
      this.callOnPingFrame(frame);
      WebSocketFrame pong = WebSocketFrame.createPongFrame(frame.getPayload());
      this.mWebSocket.sendFrame(pong);
      return true;
   }

   private boolean handlePongFrame(WebSocketFrame frame) {
      this.callOnPongFrame(frame);
      return true;
   }

   private void waitForCloseFrame() {
      if (!this.mNotWaitForCloseFrame) {
         if (this.mCloseFrame == null) {
            WebSocketFrame frame = null;
            this.scheduleClose();

            do {
               try {
                  frame = this.mWebSocket.getInput().readFrame();
               } catch (Throwable var3) {
                  break;
               }

               if (frame.isCloseFrame()) {
                  this.mCloseFrame = frame;
                  break;
               }
            } while(!this.isInterrupted());

         }
      }
   }

   private void notifyFinished() {
      this.mWebSocket.onReadingThreadFinished(this.mCloseFrame);
   }

   private void scheduleClose() {
      synchronized(this.mCloseLock) {
         this.cancelCloseTask();
         this.scheduleCloseTask();
      }
   }

   private void scheduleCloseTask() {
      this.mCloseTask = new CloseTask();
      this.mCloseTimer = new Timer("ReadingThreadCloseTimer");
      this.mCloseTimer.schedule(this.mCloseTask, this.mCloseDelay);
   }

   private void cancelClose() {
      synchronized(this.mCloseLock) {
         this.cancelCloseTask();
      }
   }

   private void cancelCloseTask() {
      if (this.mCloseTimer != null) {
         this.mCloseTimer.cancel();
         this.mCloseTimer = null;
      }

      if (this.mCloseTask != null) {
         this.mCloseTask.cancel();
         this.mCloseTask = null;
      }

   }

   private class CloseTask extends TimerTask {
      private CloseTask() {
      }

      public void run() {
         try {
            Socket socket = ReadingThread.this.mWebSocket.getSocket();
            if (socket != null) {
               socket.close();
            }
         } catch (Throwable var2) {
         }

      }
   }
}
