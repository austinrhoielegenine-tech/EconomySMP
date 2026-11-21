package com.neovisionaries.ws.client;

import java.io.IOException;
import java.util.LinkedList;

class WritingThread extends WebSocketThread {
   private static final int SHOULD_SEND = 0;
   private static final int SHOULD_STOP = 1;
   private static final int SHOULD_CONTINUE = 2;
   private static final int SHOULD_FLUSH = 3;
   private static final int FLUSH_THRESHOLD = 1000;
   private final LinkedList<WebSocketFrame> mFrames = new LinkedList();
   private final PerMessageCompressionExtension mPMCE;
   private boolean mStopRequested;
   private WebSocketFrame mCloseFrame;
   private boolean mFlushNeeded;
   private boolean mStopped;

   public WritingThread(WebSocket websocket) {
      super("WritingThread", websocket, ThreadType.WRITING_THREAD);
      this.mPMCE = websocket.getPerMessageCompressionExtension();
   }

   public void runMain() {
      try {
         this.main();
      } catch (Throwable t) {
         WebSocketException cause = new WebSocketException(WebSocketError.UNEXPECTED_ERROR_IN_WRITING_THREAD, "An uncaught throwable was detected in the writing thread: " + t.getMessage(), t);
         ListenerManager manager = this.mWebSocket.getListenerManager();
         manager.callOnError(cause);
         manager.callOnUnexpectedError(cause);
      }

      synchronized(this) {
         this.mStopped = true;
         this.notifyAll();
      }

      this.notifyFinished();
   }

   private void main() {
      this.mWebSocket.onWritingThreadStarted();

      while(true) {
         int result = this.waitForFrames();
         if (result == 1) {
            break;
         }

         if (result == 3) {
            this.flushIgnoreError();
         } else if (result != 2) {
            try {
               this.sendFrames(false);
            } catch (WebSocketException var4) {
               break;
            }
         }
      }

      try {
         this.sendFrames(true);
      } catch (WebSocketException var3) {
      }

   }

   public void requestStop() {
      synchronized(this) {
         this.mStopRequested = true;
         this.notifyAll();
      }
   }

   public boolean queueFrame(WebSocketFrame frame) {
      synchronized(this) {
         while(true) {
            if (this.mStopped) {
               return false;
            }

            if (this.mStopRequested || this.mCloseFrame != null || frame.isControlFrame()) {
               break;
            }

            int queueSize = this.mWebSocket.getFrameQueueSize();
            if (queueSize == 0 || this.mFrames.size() < queueSize) {
               break;
            }

            try {
               this.wait();
            } catch (InterruptedException var6) {
            }
         }

         if (isHighPriorityFrame(frame)) {
            this.addHighPriorityFrame(frame);
         } else {
            this.mFrames.addLast(frame);
         }

         this.notifyAll();
         return true;
      }
   }

   private static boolean isHighPriorityFrame(WebSocketFrame frame) {
      return frame.isPingFrame() || frame.isPongFrame();
   }

   private void addHighPriorityFrame(WebSocketFrame frame) {
      int index = 0;

      for(WebSocketFrame f : this.mFrames) {
         if (!isHighPriorityFrame(f)) {
            break;
         }

         ++index;
      }

      this.mFrames.add(index, frame);
   }

   public void queueFlush() {
      synchronized(this) {
         this.mFlushNeeded = true;
         this.notifyAll();
      }
   }

   private void flushIgnoreError() {
      try {
         this.flush();
      } catch (IOException var2) {
      }

   }

   private void flush() throws IOException {
      this.mWebSocket.getOutput().flush();
   }

   private int waitForFrames() {
      synchronized(this) {
         if (this.mStopRequested) {
            return 1;
         } else if (this.mCloseFrame != null) {
            return 1;
         } else {
            if (this.mFrames.size() == 0) {
               if (this.mFlushNeeded) {
                  this.mFlushNeeded = false;
                  return 3;
               }

               try {
                  this.wait();
               } catch (InterruptedException var4) {
               }
            }

            if (this.mStopRequested) {
               return 1;
            } else if (this.mFrames.size() == 0) {
               if (this.mFlushNeeded) {
                  this.mFlushNeeded = false;
                  return 3;
               } else {
                  return 2;
               }
            } else {
               return 0;
            }
         }
      }
   }

   private void sendFrames(boolean last) throws WebSocketException {
      long lastFlushAt = System.currentTimeMillis();

      while(true) {
         WebSocketFrame frame;
         synchronized(this) {
            frame = (WebSocketFrame)this.mFrames.poll();
            this.notifyAll();
            if (frame == null) {
               break;
            }
         }

         this.sendFrame(frame);
         if (!frame.isPingFrame() && !frame.isPongFrame()) {
            if (this.isFlushNeeded(last)) {
               lastFlushAt = this.flushIfLongInterval(lastFlushAt);
            }
         } else {
            this.doFlush();
            lastFlushAt = System.currentTimeMillis();
         }
      }

      if (this.isFlushNeeded(last)) {
         this.doFlush();
      }

   }

   private boolean isFlushNeeded(boolean last) {
      return last || this.mWebSocket.isAutoFlush() || this.mFlushNeeded || this.mCloseFrame != null;
   }

   private long flushIfLongInterval(long lastFlushAt) throws WebSocketException {
      long current = System.currentTimeMillis();
      if (1000L < current - lastFlushAt) {
         this.doFlush();
         return current;
      } else {
         return lastFlushAt;
      }
   }

   private void doFlush() throws WebSocketException {
      try {
         this.flush();
         synchronized(this) {
            this.mFlushNeeded = false;
         }
      } catch (IOException e) {
         WebSocketException cause = new WebSocketException(WebSocketError.FLUSH_ERROR, "Flushing frames to the server failed: " + e.getMessage(), e);
         ListenerManager manager = this.mWebSocket.getListenerManager();
         manager.callOnError(cause);
         manager.callOnSendError(cause, (WebSocketFrame)null);
         throw cause;
      }
   }

   private void sendFrame(WebSocketFrame frame) throws WebSocketException {
      frame = WebSocketFrame.compressFrame(frame, this.mPMCE);
      this.mWebSocket.getListenerManager().callOnSendingFrame(frame);
      boolean unsent = false;
      if (this.mCloseFrame != null) {
         unsent = true;
      } else if (frame.isCloseFrame()) {
         this.mCloseFrame = frame;
      }

      if (unsent) {
         this.mWebSocket.getListenerManager().callOnFrameUnsent(frame);
      } else {
         if (frame.isCloseFrame()) {
            this.changeToClosing();
         }

         try {
            this.mWebSocket.getOutput().write(frame);
         } catch (IOException e) {
            WebSocketException cause = new WebSocketException(WebSocketError.IO_ERROR_IN_WRITING, "An I/O error occurred when a frame was tried to be sent: " + e.getMessage(), e);
            ListenerManager manager = this.mWebSocket.getListenerManager();
            manager.callOnError(cause);
            manager.callOnSendError(cause, frame);
            throw cause;
         }

         this.mWebSocket.getListenerManager().callOnFrameSent(frame);
      }
   }

   private void changeToClosing() {
      StateManager manager = this.mWebSocket.getStateManager();
      boolean stateChanged = false;
      synchronized(manager) {
         WebSocketState state = manager.getState();
         if (state != WebSocketState.CLOSING && state != WebSocketState.CLOSED) {
            manager.changeToClosing(StateManager.CloseInitiator.CLIENT);
            stateChanged = true;
         }
      }

      if (stateChanged) {
         this.mWebSocket.getListenerManager().callOnStateChanged(WebSocketState.CLOSING);
      }

   }

   private void notifyFinished() {
      this.mWebSocket.onWritingThreadFinished(this.mCloseFrame);
   }
}
