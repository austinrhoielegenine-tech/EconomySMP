package net.dv8tion.jda.internal.utils.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;
import net.dv8tion.jda.internal.utils.Checks;

public class MemberCacheViewImpl extends SnowflakeCacheViewImpl<Member> implements MemberCacheView {
   public MemberCacheViewImpl() {
      super(Member.class, Member::getEffectiveName);
   }

   public Member getElementById(long id) {
      return (Member)this.get(id);
   }

   @Nonnull
   public List<Member> getElementsByUsername(@Nonnull String name, boolean ignoreCase) {
      Checks.notEmpty(name, "Name");
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Member> members = new ArrayList();
         this.forEach((member) -> {
            String nick = member.getUser().getName();
            if (this.equals(ignoreCase, nick, name)) {
               members.add(member);
            }

         });
         return Collections.unmodifiableList(members);
      }
   }

   @Nonnull
   public List<Member> getElementsByNickname(@Nullable String name, boolean ignoreCase) {
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Member> members = new ArrayList();
         this.forEach((member) -> {
            String nick = member.getNickname();
            if (nick == null) {
               if (name == null) {
                  members.add(member);
               }

            } else {
               if (this.equals(ignoreCase, nick, name)) {
                  members.add(member);
               }

            }
         });
         return Collections.unmodifiableList(members);
      }
   }

   @Nonnull
   public List<Member> getElementsWithRoles(@Nonnull Role... roles) {
      Checks.notNull(roles, "Roles");
      return this.getElementsWithRoles((Collection)Arrays.asList(roles));
   }

   @Nonnull
   public List<Member> getElementsWithRoles(@Nonnull Collection<Role> roles) {
      Checks.noneNull(roles, "Roles");
      if (this.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Role> rolesWithoutPublicRole = (List)roles.stream().filter((role) -> !role.isPublicRole()).collect(Collectors.toList());
         if (rolesWithoutPublicRole.isEmpty()) {
            return this.asList();
         } else {
            List<Member> members = new ArrayList();
            this.forEach((member) -> {
               if (member.getUnsortedRoles().containsAll(rolesWithoutPublicRole)) {
                  members.add(member);
               }

            });
            return Collections.unmodifiableList(members);
         }
      }
   }
}
