package net.dv8tion.jda.internal.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.exceptions.DetachedEntityException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.apache.commons.collections4.CollectionUtils;

public class PermissionUtil {
   private static final long ALL_PERMISSIONS = Permission.getRaw(Permission.values());
   private static final long ALL_CHANNEL_PERMISSIONS = Permission.getRaw((Collection)Arrays.stream(Permission.values()).filter(Permission::isChannel).collect(Collectors.toList()));

   public static boolean canInteract(Member issuer, Member target) {
      Checks.notNull(issuer, "Issuer Member");
      Checks.notNull(target, "Target Member");
      Guild guild = issuer.getGuild();
      if (!guild.equals(target.getGuild())) {
         throw new IllegalArgumentException("Provided members must both be Member objects of the same Guild!");
      } else if (issuer.isOwner()) {
         return true;
      } else if (target.isOwner()) {
         return false;
      } else {
         List<Role> issuerRoles = issuer.getRoles();
         List<Role> targetRoles = target.getRoles();
         return !issuerRoles.isEmpty() && (targetRoles.isEmpty() || canInteract((Role)issuerRoles.get(0), (Role)targetRoles.get(0)));
      }
   }

   public static boolean canInteract(Member issuer, Role target) {
      Checks.notNull(issuer, "Issuer Member");
      Checks.notNull(target, "Target Role");
      Guild guild = issuer.getGuild();
      if (!guild.equals(target.getGuild())) {
         throw new IllegalArgumentException("Provided Member issuer and Role target must be from the same Guild!");
      } else if (issuer.isOwner()) {
         return true;
      } else {
         List<Role> issuerRoles = issuer.getRoles();
         return !issuerRoles.isEmpty() && canInteract((Role)issuerRoles.get(0), target);
      }
   }

   public static boolean canInteract(Role issuer, Role target) {
      Checks.notNull(issuer, "Issuer Role");
      Checks.notNull(target, "Target Role");
      if (!issuer.getGuild().equals(target.getGuild())) {
         throw new IllegalArgumentException("The 2 Roles are not from same Guild!");
      } else {
         return target.compareTo(issuer) < 0;
      }
   }

   public static boolean canInteract(Member issuer, RichCustomEmoji emoji) {
      Checks.notNull(issuer, "Issuer Member");
      Checks.notNull(emoji, "Target Emoji");
      if (!issuer.getGuild().equals(emoji.getGuild())) {
         throw new IllegalArgumentException("The issuer and target are not in the same Guild");
      } else {
         return emoji.getRoles().isEmpty() || CollectionUtils.containsAny(issuer.getRoles(), emoji.getRoles());
      }
   }

   public static boolean canInteract(User issuer, RichCustomEmoji emoji, MessageChannel channel, boolean botOverride) {
      Checks.notNull(issuer, "Issuer Member");
      Checks.notNull(emoji, "Target Emoji");
      Checks.notNull(channel, "Target Channel");
      if (emoji.getGuild() != null && emoji.getGuild().isMember(issuer)) {
         Member member = emoji.getGuild().getMemberById(issuer.getIdLong());
         if (!canInteract(member, emoji)) {
            return false;
         } else {
            boolean external = emoji.isManaged() || issuer.isBot() && botOverride;
            switch (channel.getType()) {
               case TEXT:
                  TextChannel text = (TextChannel)channel;
                  member = text.getGuild().getMemberById(issuer.getIdLong());
                  return emoji.getGuild().equals(text.getGuild()) || external && member != null && member.hasPermission(text, new Permission[]{Permission.MESSAGE_EXT_EMOJI});
               default:
                  return external;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean canInteract(User issuer, RichCustomEmoji emoji, MessageChannel channel) {
      return canInteract(issuer, emoji, channel, true);
   }

   public static boolean checkPermission(Member member, Permission... permissions) {
      Checks.notNull(member, "Member");
      Checks.notNull(permissions, "Permissions");
      long effectivePerms = getEffectivePermission(member);
      return isApplied(effectivePerms, Permission.ADMINISTRATOR.getRawValue()) || isApplied(effectivePerms, Permission.getRaw(permissions));
   }

   public static boolean checkPermission(IPermissionContainer channel, Member member, Permission... permissions) {
      Checks.notNull(channel, "Channel");
      Checks.notNull(member, "Member");
      Checks.notNull(permissions, "Permissions");
      checkGuild(channel.getGuild(), member.getGuild(), "Member");
      long effectivePerms = getEffectivePermission(channel, (Member)member);
      return isApplied(effectivePerms, Permission.getRaw(permissions));
   }

   public static void requireAnyPermission(Member member, Permission... permissions) {
      Checks.notNull(member, "Member");
      Checks.notEmpty((Object[])permissions, "Permissions");

      for(Permission permission : permissions) {
         if (member.hasPermission(new Permission[]{permission})) {
            return;
         }
      }

      String reason = (String)Stream.of(permissions).map(Enum::name).collect(Collectors.joining(" or "));
      throw new InsufficientPermissionException(member.getGuild(), permissions[0], "You need the " + reason + " permission to perform this action!");
   }

   public static long getEffectivePermission(Member member) {
      Checks.notNull(member, "Member");
      if (member.isDetached()) {
         throw new DetachedEntityException("Cannot get the effective permissions of a detached member without a channel. Instead, please use the Member methods while supplying a GuildChannel");
      } else if (member.isOwner()) {
         return ALL_PERMISSIONS;
      } else {
         long permission = member.getGuild().getPublicRole().getPermissionsRaw();

         for(Role role : member.getUnsortedRoles()) {
            permission |= role.getPermissionsRaw();
            if (isApplied(permission, Permission.ADMINISTRATOR.getRawValue())) {
               return ALL_PERMISSIONS;
            }
         }

         if (member.isTimedOut()) {
            permission &= Permission.VIEW_CHANNEL.getRawValue() | Permission.MESSAGE_HISTORY.getRawValue();
         }

         return permission;
      }
   }

   public static long getEffectivePermission(GuildChannel channel, Member member) {
      Checks.notNull(channel, "Channel");
      Checks.notNull(member, "Member");
      Checks.check(channel.getGuild().equals(member.getGuild()), "Provided channel and provided member are not of the same guild!");
      if (member.isDetached()) {
         throw new DetachedEntityException("Cannot get the effective permissions of a detached member. Instead, please use the Member methods while supplying a GuildChannel");
      } else if (member.isOwner()) {
         return ALL_PERMISSIONS;
      } else {
         long permission = getEffectivePermission(member);
         long admin = Permission.ADMINISTRATOR.getRawValue();
         if (isApplied(permission, admin)) {
            return ALL_PERMISSIONS;
         } else {
            if (channel instanceof ICategorizableChannel) {
               ICategorizableChannel categorizableChannel = (ICategorizableChannel)channel;
               if (categorizableChannel.getParentCategory() != null && checkPermission(categorizableChannel.getParentCategory(), member, Permission.MANAGE_CHANNEL)) {
                  permission |= Permission.MANAGE_CHANNEL.getRawValue();
               }
            }

            AtomicLong allow = new AtomicLong(0L);
            AtomicLong deny = new AtomicLong(0L);
            getExplicitOverrides(channel, member, allow, deny);
            permission = apply(permission, allow.get(), deny.get());
            long viewChannel = Permission.VIEW_CHANNEL.getRawValue();
            long connectChannel = Permission.VOICE_CONNECT.getRawValue();
            boolean hasConnect = !channel.getType().isAudio() || isApplied(permission, connectChannel);
            boolean hasAccess = isApplied(permission, viewChannel) && hasConnect;
            if (member.isTimedOut()) {
               permission &= viewChannel | Permission.MESSAGE_HISTORY.getRawValue();
            }

            return hasAccess ? permission : 0L;
         }
      }
   }

   public static long getEffectivePermission(GuildChannel channel, Role role) {
      Checks.notNull(channel, "Channel");
      Checks.notNull(role, "Role");
      if (!channel.getGuild().equals(role.getGuild())) {
         throw new IllegalArgumentException("Provided channel and role are not of the same guild!");
      } else {
         long permissions = getExplicitPermission(channel, role);
         if (isApplied(permissions, Permission.ADMINISTRATOR.getRawValue())) {
            return ALL_CHANNEL_PERMISSIONS;
         } else {
            return !isApplied(permissions, Permission.VIEW_CHANNEL.getRawValue()) ? 0L : permissions;
         }
      }
   }

   public static long getExplicitPermission(Member member) {
      Checks.notNull(member, "Member");
      if (member.isDetached()) {
         throw new DetachedEntityException("Cannot get the explicit permissions of a detached member without a channel. Instead, please use the Member methods while supplying a GuildChannel");
      } else {
         Guild guild = member.getGuild();
         long permission = guild.getPublicRole().getPermissionsRaw();

         for(Role role : member.getUnsortedRoles()) {
            permission |= role.getPermissionsRaw();
         }

         return permission;
      }
   }

   public static long getExplicitPermission(GuildChannel channel, Member member) {
      return getExplicitPermission(channel, member, true);
   }

   public static long getExplicitPermission(GuildChannel channel, Member member, boolean includeRoles) {
      Checks.notNull(channel, "Channel");
      Checks.notNull(member, "Member");
      checkGuild(channel.getGuild(), member.getGuild(), "Member");
      if (member.isDetached()) {
         throw new DetachedEntityException("Cannot get the explicit permissions of a detached member. Instead, please use the Member methods while supplying a GuildChannel");
      } else {
         long permission = includeRoles ? getExplicitPermission(member) : 0L;
         AtomicLong allow = new AtomicLong(0L);
         AtomicLong deny = new AtomicLong(0L);
         getExplicitOverrides(channel, member, allow, deny);
         return apply(permission, allow.get(), deny.get());
      }
   }

   public static long getExplicitPermission(GuildChannel channel, Role role) {
      return getExplicitPermission(channel, role, true);
   }

   public static long getExplicitPermission(GuildChannel channel, Role role, boolean includeRoles) {
      Checks.notNull(channel, "Channel");
      Checks.notNull(role, "Role");
      if (role.isDetached()) {
         throw new DetachedEntityException("Cannot get the explicit permissions of a detached role");
      } else {
         IPermissionContainer permsChannel = channel.getPermissionContainer();
         Guild guild = role.getGuild();
         checkGuild(channel.getGuild(), guild, "Role");
         long permission = includeRoles ? role.getPermissionsRaw() | guild.getPublicRole().getPermissionsRaw() : 0L;
         PermissionOverride override = permsChannel.getPermissionOverride(guild.getPublicRole());
         if (override != null) {
            permission = apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
         }

         if (role.isPublicRole()) {
            return permission;
         } else {
            override = permsChannel.getPermissionOverride(role);
            return override == null ? permission : apply(permission, override.getAllowedRaw(), override.getDeniedRaw());
         }
      }
   }

   private static void getExplicitOverrides(GuildChannel channel, Member member, AtomicLong allow, AtomicLong deny) {
      IPermissionContainer permsChannel = channel.getPermissionContainer();
      PermissionOverride override = permsChannel.getPermissionOverride(member.getGuild().getPublicRole());
      long allowRaw = 0L;
      long denyRaw = 0L;
      if (override != null) {
         denyRaw = override.getDeniedRaw();
         allowRaw = override.getAllowedRaw();
      }

      long allowRole = 0L;
      long denyRole = 0L;

      for(Role role : member.getUnsortedRoles()) {
         override = permsChannel.getPermissionOverride(role);
         if (override != null) {
            denyRole |= override.getDeniedRaw();
            allowRole |= override.getAllowedRaw();
         }
      }

      allowRaw = allowRaw & ~denyRole | allowRole;
      denyRaw = denyRaw & ~allowRole | denyRole;
      override = permsChannel.getPermissionOverride(member);
      if (override != null) {
         long oDeny = override.getDeniedRaw();
         long oAllow = override.getAllowedRaw();
         allowRaw = allowRaw & ~oDeny | oAllow;
         denyRaw = denyRaw & ~oAllow | oDeny;
      }

      allow.set(allowRaw);
      deny.set(denyRaw);
   }

   private static boolean isApplied(long permissions, long perms) {
      return (permissions & perms) == perms;
   }

   private static long apply(long permission, long allow, long deny) {
      permission &= ~deny;
      permission |= allow;
      return permission;
   }

   private static void checkGuild(Guild o1, Guild o2, String name) {
      Checks.check(o1.equals(o2), "Specified %s is not in the same guild! (%s / %s)", name, o1, o2);
   }
}
