package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.cache.LRUMemberCachePolicy;
import net.dv8tion.jda.internal.utils.Checks;

@FunctionalInterface
public interface MemberCachePolicy {
   MemberCachePolicy NONE = (member) -> false;
   MemberCachePolicy ALL = (member) -> true;
   MemberCachePolicy OWNER = Member::isOwner;
   MemberCachePolicy ONLINE = (member) -> member.getOnlineStatus() != OnlineStatus.OFFLINE && member.getOnlineStatus() != OnlineStatus.UNKNOWN;
   MemberCachePolicy VOICE = (member) -> {
      GuildVoiceState voiceState = member.getVoiceState();
      return voiceState != null && voiceState.getChannel() != null;
   };
   MemberCachePolicy BOOSTER = Member::isBoosting;
   @Incubating
   MemberCachePolicy PENDING = Member::isPending;
   MemberCachePolicy DEFAULT = VOICE.or(OWNER);

   boolean cacheMember(@Nonnull Member var1);

   @Nonnull
   default MemberCachePolicy or(@Nonnull MemberCachePolicy policy) {
      Checks.notNull(policy, "Policy");
      return (member) -> this.cacheMember(member) || policy.cacheMember(member);
   }

   @Nonnull
   default MemberCachePolicy and(@Nonnull MemberCachePolicy policy) {
      return (member) -> this.cacheMember(member) && policy.cacheMember(member);
   }

   @Nonnull
   static MemberCachePolicy any(@Nonnull MemberCachePolicy policy, @Nonnull MemberCachePolicy... policies) {
      Checks.notNull(policy, "Policy");
      Checks.notNull(policies, "Policy");

      for(MemberCachePolicy p : policies) {
         policy = policy.or(p);
      }

      return policy;
   }

   @Nonnull
   static MemberCachePolicy all(@Nonnull MemberCachePolicy policy, @Nonnull MemberCachePolicy... policies) {
      Checks.notNull(policy, "Policy");
      Checks.notNull(policies, "Policy");

      for(MemberCachePolicy p : policies) {
         policy = policy.and(p);
      }

      return policy;
   }

   @Nonnull
   static LRUMemberCachePolicy lru(int maxSize) {
      return new LRUMemberCachePolicy(maxSize);
   }
}
