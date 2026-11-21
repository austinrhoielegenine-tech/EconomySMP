package com.neovisionaries.ws.client;

import java.util.Timer;
import java.util.TimerTask;

abstract class PeriodicalFrameSender {
   private final WebSocket mWebSocket;
   private String mTimerName;
   private Timer mTimer;
   private boolean mScheduled;
   private long mInterval;
   private PayloadGenerator mGenerator;

   public PeriodicalFrameSender(WebSocket webSocket, String timerName, PayloadGenerator generator) {
      this.mWebSocket = webSocket;
      this.mTimerName = timerName;
      this.mGenerator = generator;
   }

   public void start() {
      this.setInterval(this.getInterval());
   }

   public void stop() {
      synchronized(this) {
         if (this.mTimer != null) {
            this.mScheduled = false;
            this.mTimer.cancel();
         }
      }
   }

   public long getInterval() {
      synchronized(this) {
         return this.mInterval;
      }
   }

   public void setInterval(long interval) {
      if (interval < 0L) {
         interval = 0L;
      }

      synchronized(this) {
         this.mInterval = interval;
      }

      if (interval != 0L) {
         if (this.mWebSocket.isOpen()) {
            synchronized(this) {
               if (this.mTimer == null) {
                  if (this.mTimerName == null) {
                     this.mTimer = new Timer();
                  } else {
                     this.mTimer = new Timer(this.mTimerName);
                  }
               }

               if (!this.mScheduled) {
                  this.mScheduled = schedule(this.mTimer, new Task(), interval);
               }

            }
         }
      }
   }

   public PayloadGenerator getPayloadGenerator() {
      synchronized(this) {
         return this.mGenerator;
      }
   }

   public void setPayloadGenerator(PayloadGenerator generator) {
      synchronized(this) {
         this.mGenerator = generator;
      }
   }

   public String getTimerName() {
      return this.mTimerName;
   }

   public void setTimerName(String timerName) {
      synchronized(this) {
         this.mTimerName = timerName;
      }
   }

   private void doTask() {
      synchronized(this) {
         if (this.mInterval != 0L && this.mWebSocket.isOpen()) {
            this.mWebSocket.sendFrame(this.createFrame());
            this.mScheduled = schedule(this.mTimer, new Task(), this.mInterval);
         } else {
            this.mScheduled = false;
         }
      }
   }

   private WebSocketFrame createFrame() {
      byte[] payload = this.generatePayload();
      return this.createFrame(payload);
   }

   private byte[] generatePayload() {
      if (this.mGenerator == null) {
         return null;
      } else {
         try {
            return this.mGenerator.generate();
         } catch (Throwable var2) {
            return null;
         }
      }
   }

   private static boolean schedule(Timer timer, Task task, long interval) {
      try {
         timer.schedule(task, interval);
         return true;
      } catch (RuntimeException var5) {
         return false;
      }
   }

   protected abstract WebSocketFrame createFrame(byte[] var1);

   private final class Task extends TimerTask {
      private Task() {
      }

      public void run() {
         PeriodicalFrameSender.this.doTask();
      }
   }
}
