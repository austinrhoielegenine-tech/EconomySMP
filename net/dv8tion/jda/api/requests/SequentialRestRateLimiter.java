package net.dv8tion.jda.api.requests;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.JDALogger;
import okhttp3.Headers;
import org.slf4j.Logger;

public final class SequentialRestRateLimiter implements RestRateLimiter {
   private static final Logger log = JDALogger.getLog(RestRateLimiter.class);
   private static final String UNINIT_BUCKET = "uninit";
   private final CompletableFuture<?> shutdownHandle = new CompletableFuture();
   private final Future<?> cleanupWorker;
   private final RestRateLimiter.RateLimitConfig config;
   private boolean isStopped;
   private boolean isShutdown;
   private final ReentrantLock lock = new ReentrantLock();
   private final Set<Route> hitRatelimit = new HashSet(5);
   private final Map<Route, String> hashes = new HashMap();
   private final Map<String, Bucket> buckets = new HashMap();
   private final Map<Bucket, Future<?>> rateLimitQueue = new HashMap();

   public SequentialRestRateLimiter(@Nonnull RestRateLimiter.RateLimitConfig config) {
      this.config = config;
      this.cleanupWorker = config.getScheduler().scheduleAtFixedRate(this::cleanup, 30L, 30L, TimeUnit.SECONDS);
   }

   public void enqueue(@Nonnull RestRateLimiter.Work task) {
      MiscUtil.locked(this.lock, (Runnable)(() -> {
         Bucket bucket = this.getBucket(task.getRoute());
         bucket.enqueue(task);
         this.runBucket(bucket);
      }));
   }

   public void stop(boolean shutdown, @Nonnull Runnable callback) {
      MiscUtil.locked(this.lock, (Runnable)(() -> {
         boolean doShutdown = shutdown;
         if (!this.isStopped) {
            this.isStopped = true;
            this.shutdownHandle.thenRun(callback);
            if (!shutdown) {
               int count = this.buckets.values().stream().mapToInt((bucket) -> bucket.getRequests().size()).sum();
               if (count > 0) {
                  log.info((String)"Waiting for {} requests to finish.", (Object)count);
               }

               doShutdown = count == 0;
            }
         }

         if (doShutdown && !this.isShutdown) {
            this.shutdown();
         }

      }));
   }

   public boolean isStopped() {
      return this.isStopped;
   }

   public int cancelRequests() {
      return (Integer)MiscUtil.locked(this.lock, (Supplier)(() -> {
         int cancelled = (int)this.buckets.values().stream().map(Bucket::getRequests).flatMap(Collection::stream).filter((request) -> !request.isPriority() && !request.isCancelled()).peek(RestRateLimiter.Work::cancel).count();
         if (cancelled == 1) {
            log.warn("Cancelled 1 request!");
         } else if (cancelled > 1) {
            log.warn((String)"Cancelled {} requests!", (Object)cancelled);
         }

         return cancelled;
      }));
   }

   private void shutdown() {
      this.isShutdown = true;
      this.cleanupWorker.cancel(false);
      this.cleanup();
      this.shutdownHandle.complete((Object)null);
   }

   private void cleanup() {
      MiscUtil.locked(this.lock, (Runnable)(() -> {
         int size = this.buckets.size();
         Iterator<Map.Entry<String, Bucket>> entries = this.buckets.entrySet().iterator();

         while(entries.hasNext()) {
            Map.Entry<String, Bucket> entry = (Map.Entry)entries.next();
            Bucket bucket = (Bucket)entry.getValue();
            if (this.isShutdown) {
               bucket.requests.forEach(RestRateLimiter.Work::cancel);
            }

            bucket.requests.removeIf(RestRateLimiter.Work::isSkipped);
            if (bucket.requests.isEmpty() && !this.rateLimitQueue.containsKey(bucket)) {
               if (bucket.isUninit()) {
                  entries.remove();
               } else if (bucket.reset <= this.getNow()) {
                  entries.remove();
               } else if (this.isStopped) {
                  entries.remove();
               }
            }
         }

         size -= this.buckets.size();
         if (size > 0) {
            log.debug((String)"Removed {} expired buckets", (Object)size);
         } else if (this.isStopped && !this.isShutdown) {
            this.shutdown();
         }

      }));
   }

   private String getRouteHash(Route route) {
      return (String)this.hashes.getOrDefault(route, "uninit+" + route);
   }

   private Bucket getBucket(Route.CompiledRoute route) {
      return (Bucket)MiscUtil.locked(this.lock, (Supplier)(() -> {
         String hash = this.getRouteHash(route.getBaseRoute());
         String bucketId = hash + ":" + route.getMajorParameters();
         return (Bucket)this.buckets.computeIfAbsent(bucketId, (id) -> (Bucket)(route.getBaseRoute().isInteractionBucket() ? new InteractionBucket(id) : new ClassicBucket(id)));
      }));
   }

   private void scheduleElastic(Bucket bucket) {
      if (!this.isShutdown) {
         ExecutorService elastic = this.config.getElastic();
         ScheduledExecutorService scheduler = this.config.getScheduler();

         try {
            if (elastic == scheduler) {
               bucket.run();
            } else {
               elastic.execute(bucket);
            }
         } catch (RejectedExecutionException ex) {
            if (!this.isShutdown) {
               log.error((String)"Failed to execute bucket worker", (Throwable)ex);
            }
         } catch (Throwable t) {
            log.error("Caught throwable in bucket worker", t);
            if (t instanceof Error) {
               throw t;
            }
         }

      }
   }

   private void runBucket(Bucket bucket) {
      if (!this.isShutdown) {
         MiscUtil.locked(this.lock, (Supplier)(() -> (Future)this.rateLimitQueue.computeIfAbsent(bucket, (k) -> this.config.getScheduler().schedule(() -> this.scheduleElastic(bucket), bucket.getRateLimit(), TimeUnit.MILLISECONDS))));
      }
   }

   private long parseLong(String input) {
      return input == null ? 0L : Long.parseLong(input);
   }

   private long parseDouble(String input) {
      return input == null ? 0L : (long)(Double.parseDouble(input) * (double)1000.0F);
   }

   private long getNow() {
      return System.currentTimeMillis();
   }

   private Bucket updateBucket(Route.CompiledRoute route, okhttp3.Response response) {
      return (Bucket)MiscUtil.locked(this.lock, (Supplier)(() -> {
         try {
            Bucket bucket = this.getBucket(route);
            Headers headers = response.headers();
            boolean global = headers.get("X-RateLimit-Global") != null;
            boolean cloudflare = headers.get("via") == null;
            String hash = headers.get("X-RateLimit-Bucket");
            String scope = headers.get("X-RateLimit-Scope");
            long now = this.getNow();
            Route baseRoute = route.getBaseRoute();
            if (hash != null) {
               if (!this.hashes.containsKey(baseRoute)) {
                  this.hashes.put(baseRoute, hash);
                  log.debug((String)"Caching bucket hash {} -> {}", (Object)baseRoute, (Object)hash);
               }

               bucket = this.getBucket(route);
            }

            if (response.code() != 429) {
               if (hash == null) {
                  return bucket;
               } else {
                  String limitHeader = headers.get("X-RateLimit-Limit");
                  String remainingHeader = headers.get("X-RateLimit-Remaining");
                  String resetAfterHeader = headers.get("X-RateLimit-Reset-After");
                  String resetHeader = headers.get("X-RateLimit-Reset");
                  bucket.remaining = (int)this.parseLong(remainingHeader);
                  if (this.config.isRelative()) {
                     bucket.reset = now + this.parseDouble(resetAfterHeader);
                  } else {
                     bucket.reset = this.parseDouble(resetHeader);
                  }

                  log.trace("Updated bucket {} to ({}/{}, {})", bucket.bucketId, bucket.remaining, limitHeader, bucket.reset - now);
                  return bucket;
               }
            } else {
               String retryAfterHeader = headers.get("Retry-After");
               long retryAfter = this.parseLong(retryAfterHeader) * 1000L;
               if (global) {
                  this.config.getGlobalRateLimit().setClassic(now + retryAfter);
                  log.error((String)"Encountered global rate limit! Retry-After: {} ms Scope: {}", (Object)retryAfter, (Object)scope);
               } else if (cloudflare) {
                  this.config.getGlobalRateLimit().setCloudflare(now + retryAfter);
                  log.error((String)"Encountered cloudflare rate limit! Retry-After: {} s", (Object)(retryAfter / 1000L));
               } else {
                  boolean firstHit = this.hitRatelimit.add(baseRoute) && retryAfter < 60000L;
                  bucket.remaining = 0;
                  bucket.reset = now + retryAfter;
                  if (firstHit) {
                     log.debug("Encountered 429 on route {} with bucket {} Retry-After: {} ms Scope: {}", baseRoute, bucket.bucketId, retryAfter, scope);
                  } else {
                     log.warn("Encountered 429 on route {} with bucket {} Retry-After: {} ms Scope: {}", baseRoute, bucket.bucketId, retryAfter, scope);
                  }
               }

               log.trace((String)"Updated bucket {} to retry after {}", (Object)bucket.bucketId, (Object)(bucket.reset - now));
               return bucket;
            }
         } catch (Exception e) {
            Bucket bucket = this.getBucket(route);
            log.error("Encountered Exception while updating a bucket. Route: {} Bucket: {} Code: {} Headers:\n{}", route.getBaseRoute(), bucket, response.code(), response.headers(), e);
            return bucket;
         }
      }));
   }

   private abstract class Bucket implements Runnable {
      protected final String bucketId;
      protected final Deque<RestRateLimiter.Work> requests = new ConcurrentLinkedDeque();
      protected long reset = 0L;
      protected int remaining = 1;

      public Bucket(@Nonnull String bucketId) {
         this.bucketId = bucketId;
      }

      public boolean isUninit() {
         return this.bucketId.startsWith("uninit");
      }

      public void enqueue(@Nonnull RestRateLimiter.Work request) {
         this.requests.addLast(request);
      }

      public void retry(@Nonnull RestRateLimiter.Work request) {
         if (!this.moveRequest(request)) {
            this.requests.addFirst(request);
         }

      }

      public long getReset() {
         return this.reset;
      }

      public int getRemaining() {
         return this.remaining;
      }

      public abstract long getGlobalRateLimit(long var1);

      public long getRateLimit() {
         long now = SequentialRestRateLimiter.this.getNow();
         long global = this.getGlobalRateLimit(now);
         if (this.reset <= now) {
            this.remaining = 1;
         }

         return Math.max(global, this.remaining < 1 ? this.reset - now : 0L);
      }

      protected boolean isGlobalRateLimit() {
         return this.getGlobalRateLimit(SequentialRestRateLimiter.this.getNow()) > 0L;
      }

      protected void backoff() {
         MiscUtil.locked(SequentialRestRateLimiter.this.lock, (Runnable)(() -> {
            SequentialRestRateLimiter.this.rateLimitQueue.remove(this);
            if (!this.requests.isEmpty()) {
               SequentialRestRateLimiter.this.runBucket(this);
            } else if (SequentialRestRateLimiter.this.isStopped) {
               SequentialRestRateLimiter.this.buckets.remove(this.bucketId);
            }

            if (SequentialRestRateLimiter.this.isStopped && SequentialRestRateLimiter.this.buckets.isEmpty()) {
               SequentialRestRateLimiter.this.shutdown();
            }

         }));
      }

      @Nonnull
      public Queue<RestRateLimiter.Work> getRequests() {
         return this.requests;
      }

      protected boolean moveRequest(@Nonnull RestRateLimiter.Work request) {
         return (Boolean)MiscUtil.locked(SequentialRestRateLimiter.this.lock, (Supplier)(() -> {
            Bucket bucket = SequentialRestRateLimiter.this.getBucket(request.getRoute());
            if (bucket != this) {
               bucket.enqueue(request);
               SequentialRestRateLimiter.this.runBucket(bucket);
            }

            return bucket != this;
         }));
      }

      protected boolean execute(@Nonnull RestRateLimiter.Work request) {
         try {
            okhttp3.Response response = request.execute();
            if (response != null) {
               SequentialRestRateLimiter.this.updateBucket(request.getRoute(), response);
            }

            if (!request.isDone()) {
               this.retry(request);
            }

            return false;
         } catch (Throwable ex) {
            SequentialRestRateLimiter.log.error("Encountered exception trying to execute request", ex);
            if (ex instanceof Error) {
               throw (Error)ex;
            } else {
               return true;
            }
         }
      }

      public void run() {
         SequentialRestRateLimiter.log.trace((String)"Bucket {} is running {} requests", (Object)this.bucketId, (Object)this.requests.size());

         while(!this.requests.isEmpty()) {
            long rateLimit = this.getRateLimit();
            if (rateLimit > 0L) {
               RestRateLimiter.Work request = (RestRateLimiter.Work)this.requests.peekFirst();
               String baseRoute = request != null ? request.getRoute().getBaseRoute().toString() : "N/A";
               if (!this.isGlobalRateLimit() && rateLimit >= 1800000L) {
                  SequentialRestRateLimiter.log.warn((String)"Encountered long {} minutes Rate-Limit on route {}", (Object)TimeUnit.MILLISECONDS.toMinutes(rateLimit), (Object)baseRoute);
               }

               SequentialRestRateLimiter.log.debug("Backing off {} ms for bucket {} on route {}", rateLimit, this.bucketId, baseRoute);
               break;
            }

            RestRateLimiter.Work request = (RestRateLimiter.Work)this.requests.removeFirst();
            if (!request.isSkipped() && (!this.isUninit() || !this.moveRequest(request)) && this.execute(request)) {
               break;
            }
         }

         this.backoff();
      }

      public String toString() {
         return this.bucketId;
      }

      public int hashCode() {
         return this.bucketId.hashCode();
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else {
            return !(obj instanceof Bucket) ? false : this.bucketId.equals(((Bucket)obj).bucketId);
         }
      }
   }

   private class ClassicBucket extends Bucket {
      public ClassicBucket(String bucketId) {
         super(bucketId);
      }

      public long getGlobalRateLimit(long now) {
         RestRateLimiter.GlobalRateLimit holder = SequentialRestRateLimiter.this.config.getGlobalRateLimit();
         long global = Math.max(holder.getClassic(), holder.getCloudflare());
         return global - now;
      }
   }

   private class InteractionBucket extends Bucket {
      public InteractionBucket(@Nonnull String bucketId) {
         super(bucketId);
      }

      public long getGlobalRateLimit(long now) {
         return SequentialRestRateLimiter.this.config.getGlobalRateLimit().getCloudflare() - now;
      }
   }
}
