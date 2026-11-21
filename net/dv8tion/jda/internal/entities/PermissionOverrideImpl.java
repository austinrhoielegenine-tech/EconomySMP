package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.unions.IPermissionContainerUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class PermissionOverrideImpl implements PermissionOverride {
   private final long id;
   private final boolean isRole;
   private final JDAImpl api;
   private IPermissionContainer channel;
   private long allow;
   private long deny;

   public PermissionOverrideImpl(IPermissionContainer channel, long id, boolean isRole) {
      this.isRole = isRole;
      this.api = (JDAImpl)channel.getJDA();
      this.channel = channel;
      this.id = id;
   }

   public long getAllowedRaw() {
      return this.allow;
   }

   public long getInheritRaw() {
      return ~(this.allow | this.deny);
   }

   public long getDeniedRaw() {
      return this.deny;
   }

   @Nonnull
   public EnumSet<Permission> getAllowed() {
      return Permission.getPermissions(this.allow);
   }

   @Nonnull
   public EnumSet<Permission> getInherit() {
      return Permission.getPermissions(this.getInheritRaw());
   }

   @Nonnull
   public EnumSet<Permission> getDenied() {
      return Permission.getPermissions(this.deny);
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   public IPermissionHolder getPermissionHolder() {
      return (IPermissionHolder)(this.isRole ? this.getRole() : this.getMember());
   }

   public Member getMember() {
      return this.getGuild().getMemberById(this.id);
   }

   public Role getRole() {
      return this.getGuild().getRoleById(this.id);
   }

   @Nonnull
   public IPermissionContainerUnion getChannel() {
      IPermissionContainer realChannel = (IPermissionContainer)this.api.getChannelById(IPermissionContainer.class, this.channel.getIdLong());
      if (realChannel != null) {
         this.channel = realChannel;
      }

      return (IPermissionContainerUnion)this.channel;
   }

   @Nonnull
   public Guild getGuild() {
      return this.getChannel().getGuild();
   }

   public boolean isMemberOverride() {
      return !this.isRole;
   }

   public boolean isRoleOverride() {
      return this.isRole;
   }

   @Nonnull
   public PermissionOverrideAction getManager() {
      this.checkPermissions();
      return (new PermissionOverrideActionImpl(this)).setOverride(false);
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      this.checkPermissions();
      Route.CompiledRoute route = Route.Channels.DELETE_PERM_OVERRIDE.compile(this.channel.getId(), this.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   public long getIdLong() {
      return this.id;
   }

   public PermissionOverrideImpl setAllow(long allow) {
      this.allow = allow;
      return this;
   }

   public PermissionOverrideImpl setDeny(long deny) {
      this.deny = deny;
      return this;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof PermissionOverrideImpl)) {
         return false;
      } else {
         PermissionOverrideImpl oPerm = (PermissionOverrideImpl)o;
         return this.id == oPerm.id && this.channel.getIdLong() == oPerm.channel.getIdLong();
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.channel.getIdLong()});
   }

   public String toString() {
      return (new EntityString(this)).setType(this.isMemberOverride() ? "MEMBER" : "ROLE").addMetadata("channel", this.channel).toString();
   }

   private void checkPermissions() {
      Member selfMember = this.getGuild().getSelfMember();
      IPermissionContainer channel = this.getChannel();
      Checks.checkAccess(selfMember, channel);
      if (!selfMember.hasPermission(channel, new Permission[]{Permission.MANAGE_PERMISSIONS})) {
         throw new InsufficientPermissionException(channel, Permission.MANAGE_PERMISSIONS);
      }
   }
}
