package net.dv8tion.jda.internal.handle;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.CacheConsumer;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class EventCache {
   public static final Logger LOG = JDALogger.getLog(EventCache.class);
   public static final long TIMEOUT_AMOUNT = 100L;
   private final EnumMap<Type, TLongObjectMap<List<CacheNode>>> eventCache = new EnumMap(Type.class);

   public synchronized void timeout(long responseTotal) {
      if (!this.eventCache.isEmpty()) {
         AtomicInteger count = new AtomicInteger();
         this.eventCache.forEach((type, map) -> {
            if (!map.isEmpty()) {
               TLongObjectIterator<List<CacheNode>> iterator = map.iterator();

               while(iterator.hasNext()) {
                  iterator.advance();
                  long triggerId = iterator.key();
                  List<CacheNode> cache = iterator.value();
                  cache.removeIf((node) -> {
                     boolean remove = responseTotal - node.responseTotal > 100L;
                     if (remove) {
                        count.incrementAndGet();
                        LOG.trace("Removing type {}/{} from event cache with payload {}", type, triggerId, node.event);
                     }

                     return remove;
                  });
                  if (cache.isEmpty()) {
                     iterator.remove();
                  }
               }

            }
         });
         int amount = count.get();
         if (amount > 0) {
            LOG.debug((String)"Removed {} events from cache that were too old to be recycled", (Object)amount);
         }

      }
   }

   public synchronized void cache(Type type, long triggerId, long responseTotal, DataObject event, CacheConsumer handler) {
      TLongObjectMap<List<CacheNode>> triggerCache = (TLongObjectMap)this.eventCache.computeIfAbsent(type, (k) -> new TLongObjectHashMap());
      List<CacheNode> items = triggerCache.get(triggerId);
      if (items == null) {
         items = new ArrayList();
         triggerCache.put(triggerId, items);
      }

      items.add(new CacheNode(responseTotal, event, handler));
   }

   public synchronized void playbackCache(Type type, long triggerId) {
      TLongObjectMap<List<CacheNode>> typeCache = (TLongObjectMap)this.eventCache.get(type);
      if (typeCache != null) {
         List<CacheNode> items = typeCache.remove(triggerId);
         if (items != null && !items.isEmpty()) {
            LOG.debug("Replaying {} events from the EventCache for type {} with id: {}", items.size(), type, triggerId);

            for(CacheNode item : items) {
               item.execute();
            }
         }

      }
   }

   public synchronized int size() {
      return (int)this.eventCache.values().stream().mapToLong((typeMap) -> typeMap.valueCollection().stream().mapToLong(List::size).sum()).sum();
   }

   public synchronized void clear() {
      this.eventCache.clear();
   }

   public synchronized void clear(Type type, long id) {
      TLongObjectMap<List<CacheNode>> typeCache = (TLongObjectMap)this.eventCache.get(type);
      if (typeCache != null) {
         List<CacheNode> events = typeCache.remove(id);
         if (events != null) {
            LOG.debug("Clearing cache for type {} with ID {} (Size: {})", type, id, events.size());
         }

      }
   }

   public static enum Type {
      USER,
      MEMBER,
      GUILD,
      CHANNEL,
      ROLE,
      SOUNDBOARD_SOUND,
      RELATIONSHIP,
      CALL,
      SCHEDULED_EVENT;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{USER, MEMBER, GUILD, CHANNEL, ROLE, SOUNDBOARD_SOUND, RELATIONSHIP, CALL, SCHEDULED_EVENT};
      }
   }

   private static class CacheNode {
      private final long responseTotal;
      private final DataObject event;
      private final CacheConsumer callback;

      public CacheNode(long responseTotal, DataObject event, CacheConsumer callback) {
         this.responseTotal = responseTotal;
         this.event = event;
         this.callback = callback;
      }

      void execute() {
         this.callback.execute(this.responseTotal, this.event);
      }
   }
}
