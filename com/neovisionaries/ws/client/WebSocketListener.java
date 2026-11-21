package com.neovisionaries.ws.client;

import java.util.List;
import java.util.Map;

public interface WebSocketListener {
   void onStateChanged(WebSocket var1, WebSocketState var2) throws Exception;

   void onConnected(WebSocket var1, Map<String, List<String>> var2) throws Exception;

   void onConnectError(WebSocket var1, WebSocketException var2) throws Exception;

   void onDisconnected(WebSocket var1, WebSocketFrame var2, WebSocketFrame var3, boolean var4) throws Exception;

   void onFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onContinuationFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onTextFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onBinaryFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onCloseFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onPingFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onPongFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onTextMessage(WebSocket var1, String var2) throws Exception;

   void onTextMessage(WebSocket var1, byte[] var2) throws Exception;

   void onBinaryMessage(WebSocket var1, byte[] var2) throws Exception;

   void onSendingFrame(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onFrameSent(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onFrameUnsent(WebSocket var1, WebSocketFrame var2) throws Exception;

   void onThreadCreated(WebSocket var1, ThreadType var2, Thread var3) throws Exception;

   void onThreadStarted(WebSocket var1, ThreadType var2, Thread var3) throws Exception;

   void onThreadStopping(WebSocket var1, ThreadType var2, Thread var3) throws Exception;

   void onError(WebSocket var1, WebSocketException var2) throws Exception;

   void onFrameError(WebSocket var1, WebSocketException var2, WebSocketFrame var3) throws Exception;

   void onMessageError(WebSocket var1, WebSocketException var2, List<WebSocketFrame> var3) throws Exception;

   void onMessageDecompressionError(WebSocket var1, WebSocketException var2, byte[] var3) throws Exception;

   void onTextMessageError(WebSocket var1, WebSocketException var2, byte[] var3) throws Exception;

   void onSendError(WebSocket var1, WebSocketException var2, WebSocketFrame var3) throws Exception;

   void onUnexpectedError(WebSocket var1, WebSocketException var2) throws Exception;

   void handleCallbackError(WebSocket var1, Throwable var2) throws Exception;

   void onSendingHandshake(WebSocket var1, String var2, List<String[]> var3) throws Exception;
}
