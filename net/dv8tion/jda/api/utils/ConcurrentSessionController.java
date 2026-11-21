package net.dv8tion.jda.api.utils;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.internal.utils.Helpers;

public class ConcurrentSessionController extends SessionControllerAdapter implements SessionController {
   private Worker[] workers = new Worker[1];

   public void setConcurrency(int level) {
      assert level > 0 && level < Integer.MAX_VALUE;

      this.workers = new Worker[level];
   }

   public void appendSession(@Nonnull SessionController.SessionConnectNode node) {
      this.getWorker(node).enqueue(node);
   }

   public void removeSession(@Nonnull SessionController.SessionConnectNode node) {
      this.getWorker(node).dequeue(node);
   }

   private synchronized Worker getWorker(SessionController.SessionConnectNode node) {
      int i = node.getShardInfo().getShardId() % this.workers.length;
      Worker worker = this.workers[i];
      if (worker == null) {
         log.debug((String)"Creating new worker handle for shard pool {}", (Object)i);
         this.workers[i] = worker = new Worker(i);
      }

      return worker;
   }

   private static class Worker implements Runnable {
      private final Queue<SessionController.SessionConnectNode> queue;
      private final int id;
      private Thread thread;

      private Worker(int id) {
         this.queue = new ConcurrentLinkedQueue();
         this.id = id;
      }

      private synchronized void start() {
         if (this.thread == null) {
            this.thread = new Thread(this, "ConcurrentSessionController-Worker-" + this.id);
            SessionControllerAdapter.log.debug("Running worker");
            this.thread.start();
         }

      }

      private synchronized void stop() {
         this.thread = null;
         if (!this.queue.isEmpty()) {
            this.start();
         }

      }

      private void enqueue(@Nonnull SessionController.SessionConnectNode node) {
         SessionControllerAdapter.log.trace((String)"Appending node to queue {}", (Object)node.getShardInfo());
         this.queue.add(node);
         this.start();
      }

      private void dequeue(@Nonnull SessionController.SessionConnectNode node) {
         SessionControllerAdapter.log.trace((String)"Removing node from queue {}", (Object)node.getShardInfo());
         this.queue.remove(node);
      }

      public void run() {
         try {
            while(!this.queue.isEmpty()) {
               this.processQueue();
               TimeUnit.SECONDS.sleep(5L);
            }
         } catch (InterruptedException ex) {
            SessionControllerAdapter.log.error((String)"Worker failed to process queue", (Throwable)ex);
         } finally {
            this.stop();
         }

      }

      private void processQueue() throws InterruptedException {
         SessionController.SessionConnectNode node = null;

         try {
            node = (SessionController.SessionConnectNode)this.queue.remove();
            SessionControllerAdapter.log.debug((String)"Running connect node for shard {}", (Object)node.getShardInfo());
            node.run(false);
         } catch (NoSuchElementException var3) {
         } catch (InterruptedException e) {
            this.queue.add(node);
            throw e;
         } catch (ErrorResponseException | IllegalStateException var5) {
            if (Helpers.hasCause(var5, OpeningHandshakeException.class)) {
               SessionControllerAdapter.log.error((String)"Failed opening handshake, appending to queue. Message: {}", (Object)((RuntimeException)var5).getMessage());
            } else if (!(var5 instanceof ErrorResponseException) || !(((RuntimeException)var5).getCause() instanceof IOException)) {
               if (Helpers.hasCause(var5, UnknownHostException.class)) {
                  SessionControllerAdapter.log.error((String)"DNS resolution failed: {}", (Object)((RuntimeException)var5).getMessage());
               } else if (((RuntimeException)var5).getCause() != null && !JDA.Status.RECONNECT_QUEUED.name().equals(((RuntimeException)var5).getCause().getMessage())) {
                  SessionControllerAdapter.log.error((String)"Failed to establish connection for a node, appending to queue", (Throwable)var5);
               } else {
                  SessionControllerAdapter.log.error((String)"Unexpected exception when running connect node", (Throwable)var5);
               }
            }

            if (node != null) {
               this.queue.add(node);
            }
         }

      }
   }
}
