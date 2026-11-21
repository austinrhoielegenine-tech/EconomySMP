package com.neovisionaries.ws.client;

class StateManager {
   private WebSocketState mState;
   private CloseInitiator mCloseInitiator;

   public StateManager() {
      this.mCloseInitiator = StateManager.CloseInitiator.NONE;
      this.mState = WebSocketState.CREATED;
   }

   public WebSocketState getState() {
      return this.mState;
   }

   public void setState(WebSocketState state) {
      this.mState = state;
   }

   public void changeToClosing(CloseInitiator closeInitiator) {
      this.mState = WebSocketState.CLOSING;
      if (this.mCloseInitiator == StateManager.CloseInitiator.NONE) {
         this.mCloseInitiator = closeInitiator;
      }

   }

   public boolean getClosedByServer() {
      return this.mCloseInitiator == StateManager.CloseInitiator.SERVER;
   }

   static enum CloseInitiator {
      NONE,
      SERVER,
      CLIENT;
   }
}
