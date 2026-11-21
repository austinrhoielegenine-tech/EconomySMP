package net.dv8tion.jda.internal.requests;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.utils.Helpers;

public class MemberChunkManager {
   private static final long MAX_CHUNK_AGE = 10000L;
   private final WebSocketClient client;
   private final ReentrantLock lock = new ReentrantLock();
   private final TLongObjectMap<ChunkRequest> requests = new TLongObjectHashMap<ChunkRequest>();
   private Future<?> timeoutHandle;

   public MemberChunkManager(WebSocketClient client) {
      this.client = client;
   }

   public static boolean isLastChunk(DataObject chunk) {
      return chunk.getInt("chunk_index") + 1 == chunk.getInt("chunk_count");
   }

   public void clear() {
      ReentrantLock var10000 = this.lock;
      TLongObjectMap var10001 = this.requests;
      Objects.requireNonNull(var10001);
      MiscUtil.locked(var10000, var10001::clear);
   }

   private void init() {
      MiscUtil.locked(this.lock, (Runnable)(() -> {
         if (this.timeoutHandle == null) {
            this.timeoutHandle = this.client.getJDA().getGatewayPool().scheduleAtFixedRate(new TimeoutHandler(), 5L, 5L, TimeUnit.SECONDS);
         }

      }));
   }

   public void shutdown() {
      if (this.timeoutHandle != null) {
         this.timeoutHandle.cancel(false);
      }

   }

   public ChunkRequest chunkGuild(GuildImpl guild, boolean presence, BiConsumer<Boolean, List<Member>> handler) {
      this.init();
      DataObject request = DataObject.empty().put("guild_id", guild.getId()).put("presences", presence).put("limit", 0).put("query", "");
      ChunkRequest chunkRequest = new ChunkRequest(handler, guild, request);
      this.makeRequest(chunkRequest);
      return chunkRequest;
   }

   public ChunkRequest chunkGuild(GuildImpl guild, String query, int limit, BiConsumer<Boolean, List<Member>> handler) {
      this.init();
      DataObject request = DataObject.empty().put("guild_id", guild.getId()).put("limit", Math.min(100, Math.max(1, limit))).put("query", query);
      ChunkRequest chunkRequest = new ChunkRequest(handler, guild, request);
      this.makeRequest(chunkRequest);
      return chunkRequest;
   }

   public ChunkRequest chunkGuild(GuildImpl guild, boolean presence, long[] userIds, BiConsumer<Boolean, List<Member>> handler) {
      this.init();
      DataObject request = DataObject.empty().put("guild_id", guild.getId()).put("presences", presence).put("user_ids", userIds);
      ChunkRequest chunkRequest = new ChunkRequest(handler, guild, request);
      this.makeRequest(chunkRequest);
      return chunkRequest;
   }

   public boolean handleChunk(long guildId, DataObject response) {
      return (Boolean)MiscUtil.locked(this.lock, (Supplier)(() -> {
         String nonce = response.getString("nonce", (String)null);
         if (nonce != null && !nonce.isEmpty()) {
            long key = Long.parseLong(nonce);
            ChunkRequest request = this.requests.get(key);
            if (request == null) {
               return false;
            } else {
               boolean lastChunk = isLastChunk(response);
               request.handleChunk(lastChunk, response);
               if (lastChunk || request.isCancelled()) {
                  this.requests.remove(key);
                  request.complete((Object)null);
               }

               return true;
            }
         } else {
            return false;
         }
      }));
   }

   public void cancelRequest(ChunkRequest request) {
      MiscUtil.locked(this.lock, (Runnable)(() -> this.requests.remove(request.nonce)));
   }

   private void makeRequest(ChunkRequest request) {
      MiscUtil.locked(this.lock, (Runnable)(() -> {
         this.requests.put(request.nonce, request);
         this.sendChunkRequest(request.getRequest());
      }));
   }

   private void sendChunkRequest(DataObject request) {
      this.client.sendChunkRequest(request);
   }

   public class ChunkRequest extends CompletableFuture<Void> {
      private final BiConsumer<Boolean, List<Member>> handler;
      private final GuildImpl guild;
      private final DataObject request;
      private final long nonce;
      private long startTime;
      private long timeout = 10000L;

      public ChunkRequest(BiConsumer<Boolean, List<Member>> handler, GuildImpl guild, DataObject request) {
         this.handler = handler;
         this.guild = guild;
         this.nonce = ThreadLocalRandom.current().nextLong() & -2L;
         this.request = request.put("nonce", this.getNonce());
      }

      public ChunkRequest setTimeout(long timeout) {
         this.timeout = timeout;
         return this;
      }

      public boolean isNonce(String nonce) {
         return this.nonce == Long.parseLong(nonce);
      }

      public String getNonce() {
         return String.valueOf(this.nonce);
      }

      public long getAge() {
         return this.startTime <= 0L ? 0L : System.currentTimeMillis() - this.startTime;
      }

      public boolean isExpired() {
         return this.getAge() > this.timeout;
      }

      public DataObject getRequest() {
         this.startTime = System.currentTimeMillis();
         return this.request;
      }

      private List<Member> toMembers(DataObject chunk) {
         EntityBuilder builder = this.guild.getJDA().getEntityBuilder();
         DataArray memberArray = chunk.getArray("members");
         TLongObjectMap<DataObject> presences = (TLongObjectMap)chunk.optArray("presences").map((it) -> Helpers.convertToMap((o) -> o.getObject("user").getUnsignedLong("id"), it)).orElseGet(TLongObjectHashMap::new);
         List<Member> collect = new ArrayList(memberArray.length());

         for(int i = 0; i < memberArray.length(); ++i) {
            DataObject json = memberArray.getObject(i);
            long userId = json.getObject("user").getUnsignedLong("id");
            DataObject presence = presences.get(userId);
            MemberImpl member = builder.createMember(this.guild, json, (DataObject)null, presence);
            builder.updateMemberCache(member);
            collect.add(member);
         }

         return collect;
      }

      public void handleChunk(boolean last, DataObject chunk) {
         try {
            if (!this.isDone()) {
               this.handler.accept(last, this.toMembers(chunk));
            }
         } catch (Throwable ex) {
            this.completeExceptionally(ex);
            if (ex instanceof Error) {
               throw (Error)ex;
            }
         }

      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         MemberChunkManager.this.client.cancelChunkRequest(this.getNonce());
         MemberChunkManager.this.cancelRequest(this);
         return super.cancel(mayInterruptIfRunning);
      }
   }

   private class TimeoutHandler implements Runnable {
      private TimeoutHandler() {
      }

      public void run() {
         MiscUtil.locked(MemberChunkManager.this.lock, (Runnable)(() -> {
            MemberChunkManager.this.requests.forEachValue((request) -> {
               if (request.isExpired()) {
                  request.completeExceptionally(new TimeoutException());
               }

               return true;
            });
            MemberChunkManager.this.requests.valueCollection().removeIf(CompletableFuture::isDone);
         }));
      }
   }
}
