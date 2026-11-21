package net.dv8tion.jda.api.utils.cache;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.Checks;

public class LRUMemberCachePolicy implements MemberCachePolicy {
   private static final long EPOCH_SECONDS = System.currentTimeMillis() / 1000L;
   private final int maxMembers;
   private final TObjectIntMap<Member> counters;
   private final ArrayDeque<MemberNode> queue;
   private LinkedHashMap<Member, Integer> activeMemberCache;
   private MemberCachePolicy subPolicy;
   private int useActiveMemberCache;

   public LRUMemberCachePolicy(int maxMembers) {
      this(maxMembers, MemberCachePolicy.NONE);
   }

   private LRUMemberCachePolicy(int maxMembers, @Nonnull MemberCachePolicy subPolicy) {
      Checks.positive(maxMembers, "Max members");
      Checks.notNull(subPolicy, "MemberCachePolicy");
      this.maxMembers = maxMembers;
      this.counters = new TObjectIntHashMap<Member>(maxMembers);
      this.queue = new ArrayDeque(maxMembers);
      this.useActiveMemberCache = Math.max(10, this.maxMembers / 10);
      this.activeMemberCache = new LinkedHashMap();
      this.subPolicy = subPolicy;
   }

   @Nonnull
   public LRUMemberCachePolicy unloadUnless(@Nonnull MemberCachePolicy subPolicy) {
      Checks.notNull(subPolicy, "MemberCachePolicy");
      this.subPolicy = subPolicy;
      return this;
   }

   @Nonnull
   public synchronized LRUMemberCachePolicy withActiveMemberCache(boolean enabled) {
      return this.withActiveMemberCache(enabled ? this.maxMembers / 10 : 0);
   }

   @Nonnull
   public synchronized LRUMemberCachePolicy withActiveMemberCache(int activityCount) {
      this.useActiveMemberCache = activityCount;
      if (this.useActiveMemberCache < 1) {
         Set<Member> moved = this.activeMemberCache.keySet();
         moved.forEach(this::cacheMember);
      }

      this.activeMemberCache = new LinkedHashMap();
      return this;
   }

   public synchronized boolean cacheMember(@Nonnull Member member) {
      int currentCount = this.counters.adjustOrPutValue(member, 1, 1);
      if (this.useActiveMemberCache > 0) {
         if (this.activeMemberCache.containsKey(member)) {
            this.activeMemberCache.put(member, now());
            return true;
         }

         if (currentCount > this.useActiveMemberCache) {
            this.queue.removeIf((node) -> member.equals(node.member));
            this.counters.remove(member);
            this.activeMemberCache.put(member, now());
            return true;
         }
      }

      this.queue.add(new MemberNode(member));
      this.evictOldest();
      this.trimQueue();
      return true;
   }

   private void evictOldest() {
      Member unloadable = null;

      while(this.counters.size() + this.activeMemberCache.size() > this.maxMembers) {
         Iterator<Map.Entry<Member, Integer>> activeMemberIterator = this.activeMemberCache.entrySet().iterator();
         Map.Entry<Member, Integer> oldestActive = activeMemberIterator.hasNext() ? (Map.Entry)activeMemberIterator.next() : null;
         MemberNode removed = (MemberNode)this.queue.poll();
         if (removed == null || oldestActive != null && (Integer)oldestActive.getValue() < removed.insertionTime) {
            activeMemberIterator.remove();
            unloadable = (Member)oldestActive.getKey();
            if (removed != null) {
               this.queue.addFirst(removed);
            }
         } else if (this.counters.get(removed.member) <= 1) {
            this.counters.remove(removed.member);
            unloadable = removed.member;
         } else {
            this.counters.adjustValue(removed.member, -1);
         }

         if (unloadable != null && !this.subPolicy.cacheMember(unloadable)) {
            unloadable.getGuild().unloadMember(unloadable.getIdLong());
         }
      }

   }

   private void trimQueue() {
      while(true) {
         if (!this.queue.isEmpty()) {
            MemberNode head = (MemberNode)this.queue.peek();
            if (this.counters.get(head) > 1) {
               this.counters.adjustValue(head.member, -1);
               this.queue.poll();
               continue;
            }
         }

         return;
      }
   }

   private static int now() {
      return (int)(System.currentTimeMillis() / 1000L - EPOCH_SECONDS);
   }

   private static class MemberNode {
      private final int insertionTime;
      private final Member member;

      private MemberNode(Member member) {
         this.member = member;
         this.insertionTime = LRUMemberCachePolicy.now();
      }
   }
}
