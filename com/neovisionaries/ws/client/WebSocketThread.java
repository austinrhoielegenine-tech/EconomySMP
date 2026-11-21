package com.neovisionaries.ws.client;

abstract class WebSocketThread extends Thread {
   protected final WebSocket mWebSocket;
   private final ThreadType mThreadType;

   WebSocketThread(String name, WebSocket ws, ThreadType type) {
      super(name);
      this.mWebSocket = ws;
      this.mThreadType = type;
   }

   public void run() {
      ListenerManager lm = this.mWebSocket.getListenerManager();
      if (lm != null) {
         lm.callOnThreadStarted(this.mThreadType, this);
      }

      this.runMain();
      if (lm != null) {
         lm.callOnThreadStopping(this.mThreadType, this);
      }

   }

   public void callOnThreadCreated() {
      ListenerManager lm = this.mWebSocket.getListenerManager();
      if (lm != null) {
         lm.callOnThreadCreated(this.mThreadType, this);
      }

   }

   protected abstract void runMain();
}
