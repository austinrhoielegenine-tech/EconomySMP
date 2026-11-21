package net.dv8tion.jda.api.utils;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestRateLimiter;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class SessionControllerAdapter implements SessionController {
   protected static final Logger log = JDALogger.getLog(SessionControllerAdapter.class);
   protected final Object lock = new Object();
   protected Queue<SessionController.SessionConnectNode> connectQueue = new ConcurrentLinkedQueue();
   protected RestRateLimiter.GlobalRateLimit globalRatelimit = RestRateLimiter.GlobalRateLimit.create();
   protected Thread workerHandle;
   protected long lastConnect = 0L;

   public void appendSession(@Nonnull SessionController.SessionConnectNode node) {
      this.removeSession(node);
      this.connectQueue.add(node);
      this.runWorker();
   }

   public void removeSession(@Nonnull SessionController.SessionConnectNode node) {
      this.connectQueue.remove(node);
   }

   @Nonnull
   public RestRateLimiter.GlobalRateLimit getRateLimitHandle() {
      return this.globalRatelimit;
   }

   @Nonnull
   public SessionController.ShardedGateway getShardedGateway(@Nonnull JDA api) {
      return (SessionController.ShardedGateway)(new RestActionImpl<SessionController.ShardedGateway>(api, Route.Misc.GATEWAY_BOT.compile()) {
         public void handleResponse(@Nonnull Response response, @Nonnull Request<SessionController.ShardedGateway> request) {
            if (response.isOk()) {
               DataObject object = response.getObject();
               String url = object.getString("url");
               int shards = object.getInt("shards");
               int concurrency = object.getObject("session_start_limit").getInt("max_concurrency", 1);
               request.onSuccess(new SessionController.ShardedGateway(url, shards, concurrency));
            } else if (response.code == 401) {
               this.api.shutdownNow();
               request.onFailure((Throwable)(new InvalidTokenException()));
            } else {
               request.onFailure(response);
            }

         }
      }).priority().complete();
   }

   protected void runWorker() {
      synchronized(this.lock) {
         if (this.workerHandle == null) {
            this.workerHandle = new QueueWorker();
            this.workerHandle.start();
         }

      }
   }

   protected class QueueWorker extends Thread {
      protected final long delay;

      public QueueWorker() {
         this(5);
      }

      public QueueWorker(int delay) {
         this(TimeUnit.SECONDS.toMillis((long)delay));
      }

      public QueueWorker(long delay) {
         super("SessionControllerAdapter-Worker");
         this.delay = delay;
         super.setUncaughtExceptionHandler(this::handleFailure);
      }

      protected void handleFailure(Thread thread, Throwable exception) {
         SessionControllerAdapter.log.error("Worker has failed with throwable!", exception);
      }

      public void run() {
         try {
            if (this.delay > 0L) {
               long interval = System.currentTimeMillis() - SessionControllerAdapter.this.lastConnect;
               if (interval < this.delay) {
                  Thread.sleep(this.delay - interval);
               }
            }
         } catch (InterruptedException ex) {
            SessionControllerAdapter.log.error((String)"Unable to backoff", (Throwable)ex);
         }

         this.processQueue();
         synchronized(SessionControllerAdapter.this.lock) {
            SessionControllerAdapter.this.workerHandle = null;
            if (!SessionControllerAdapter.this.connectQueue.isEmpty()) {
               SessionControllerAdapter.this.runWorker();
            }

         }
      }

      protected void processQueue() {
         boolean isMultiple = SessionControllerAdapter.this.connectQueue.size() > 1;

         while(!SessionControllerAdapter.this.connectQueue.isEmpty()) {
            SessionController.SessionConnectNode node = (SessionController.SessionConnectNode)SessionControllerAdapter.this.connectQueue.poll();

            try {
               node.run(isMultiple && SessionControllerAdapter.this.connectQueue.isEmpty());
               isMultiple = true;
               SessionControllerAdapter.this.lastConnect = System.currentTimeMillis();
               if (SessionControllerAdapter.this.connectQueue.isEmpty()) {
                  break;
               }

               if (this.delay > 0L) {
                  Thread.sleep(this.delay);
               }
            } catch (IllegalStateException e) {
               Throwable t = e.getCause();
               if (t instanceof OpeningHandshakeException) {
                  SessionControllerAdapter.log.error((String)"Failed opening handshake, appending to queue. Message: {}", (Object)e.getMessage());
               } else if (t != null && !JDA.Status.RECONNECT_QUEUED.name().equals(t.getMessage())) {
                  SessionControllerAdapter.log.error((String)"Failed to establish connection for a node, appending to queue", (Throwable)e);
               } else {
                  SessionControllerAdapter.log.error((String)"Unexpected exception when running connect node", (Throwable)e);
               }

               SessionControllerAdapter.this.appendSession(node);
            } catch (InterruptedException e) {
               SessionControllerAdapter.log.error((String)"Failed to run node", (Throwable)e);
               SessionControllerAdapter.this.appendSession(node);
               return;
            }
         }

      }
   }
}
