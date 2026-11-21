package net.dv8tion.jda.api.utils.cache;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.MiscUtil;
import org.jetbrains.annotations.Unmodifiable;

public interface UnifiedMemberCacheView extends CacheView<Member> {
   @Nonnull
   List<Member> getElementsById(long var1);

   @Nonnull
   default List<Member> getElementsById(@Nonnull String id) {
      return this.getElementsById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @Unmodifiable List<Member> getElementsByUsername(@Nonnull String var1, boolean var2);

   @Nonnull
   default @Unmodifiable List<Member> getElementsByUsername(@Nonnull String name) {
      return this.getElementsByUsername(name, false);
   }

   @Nonnull
   @Unmodifiable List<Member> getElementsByNickname(@Nullable String var1, boolean var2);

   @Nonnull
   default @Unmodifiable List<Member> getElementsByNickname(@Nullable String name) {
      return this.getElementsByNickname(name, false);
   }

   @Nonnull
   @Unmodifiable List<Member> getElementsWithRoles(@Nonnull Role... var1);

   @Nonnull
   @Unmodifiable List<Member> getElementsWithRoles(@Nonnull Collection<Role> var1);
}
