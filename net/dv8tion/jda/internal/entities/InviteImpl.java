package net.dv8tion.jda.internal.entities;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class InviteImpl implements Invite {
   private final JDAImpl api;
   private final Invite.Channel channel;
   private final String code;
   private final boolean expanded;
   private final Invite.Guild guild;
   private final Invite.Group group;
   private final Invite.InviteTarget target;
   private final User inviter;
   private final int maxAge;
   private final int maxUses;
   private final boolean temporary;
   private final boolean guest;
   private final OffsetDateTime timeCreated;
   private final int uses;
   private final Invite.InviteType type;

   public InviteImpl(JDAImpl api, String code, boolean expanded, User inviter, int maxAge, int maxUses, boolean temporary, boolean guest, OffsetDateTime timeCreated, int uses, Invite.Channel channel, Invite.Guild guild, Invite.Group group, Invite.InviteTarget target, Invite.InviteType type) {
      this.api = api;
      this.code = code;
      this.expanded = expanded;
      this.inviter = inviter;
      this.maxAge = maxAge;
      this.maxUses = maxUses;
      this.temporary = temporary;
      this.guest = guest;
      this.timeCreated = timeCreated;
      this.uses = uses;
      this.channel = channel;
      this.guild = guild;
      this.group = group;
      this.target = target;
      this.type = type;
   }

   public static RestAction<Invite> resolve(JDA api, String code, boolean withCounts) {
      Checks.notNull(code, "code");
      Checks.notNull(api, "api");
      Route.CompiledRoute route = Route.Invites.GET_INVITE.compile(code);
      if (withCounts) {
         route = route.withQueryParams("with_counts", "true");
      }

      JDAImpl jda = (JDAImpl)api;
      return new RestActionImpl<Invite>(api, route, (response, request) -> jda.getEntityBuilder().createInvite(response.getObject()));
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      Route.CompiledRoute route = Route.Invites.DELETE_INVITE.compile(this.code);
      return new AuditableRestActionImpl<Void>(this.api, route);
   }

   @Nonnull
   public RestAction<Invite> expand() {
      if (this.expanded) {
         return new CompletedRestAction<Invite>(this.getJDA(), this);
      } else if (this.type != Invite.InviteType.GUILD) {
         throw new IllegalStateException("Only guild invites can be expanded");
      } else {
         net.dv8tion.jda.api.entities.Guild guild = this.api.getGuildById(this.guild.getIdLong());
         if (guild == null) {
            throw new UnsupportedOperationException("You're not in the guild this invite points to");
         } else {
            Member member = guild.getSelfMember();
            GuildChannel channel = (GuildChannel)guild.getChannelById(GuildChannel.class, this.channel.getIdLong());
            if (channel == null) {
               throw new UnsupportedOperationException("Cannot expand invite without known channel. Channel ID: " + this.channel.getId());
            } else {
               Route.CompiledRoute route;
               if (member.hasPermission(channel, new Permission[]{Permission.MANAGE_CHANNEL})) {
                  route = Route.Invites.GET_CHANNEL_INVITES.compile(channel.getId());
               } else {
                  if (!member.hasPermission(new Permission[]{Permission.MANAGE_SERVER})) {
                     throw new InsufficientPermissionException(channel, Permission.MANAGE_CHANNEL, "You don't have the permission to view the full invite info");
                  }

                  route = Route.Invites.GET_GUILD_INVITES.compile(guild.getId());
               }

               return new RestActionImpl<Invite>(this.api, route, (response, request) -> {
                  EntityBuilder entityBuilder = this.api.getEntityBuilder();
                  DataArray array = response.getArray();

                  for(int i = 0; i < array.length(); ++i) {
                     DataObject object = array.getObject(i);
                     if (this.code.equals(object.getString("code"))) {
                        return entityBuilder.createInvite(object);
                     }
                  }

                  throw new IllegalStateException("Missing the invite in the channel/guild invite list");
               });
            }
         }
      }
   }

   @Nonnull
   public Invite.InviteType getType() {
      return this.type;
   }

   @Nonnull
   public Invite.TargetType getTargetType() {
      return this.target == null ? Invite.TargetType.NONE : this.target.getType();
   }

   public Invite.Channel getChannel() {
      return this.channel;
   }

   @Nonnull
   public String getCode() {
      return this.code;
   }

   public Invite.Guild getGuild() {
      return this.guild;
   }

   public Invite.Group getGroup() {
      return this.group;
   }

   @Nullable
   public Invite.InviteTarget getTarget() {
      return this.target;
   }

   public User getInviter() {
      return this.inviter;
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   public int getMaxAge() {
      if (!this.expanded) {
         throw new IllegalStateException("Only valid for expanded invites");
      } else {
         return this.maxAge;
      }
   }

   public int getMaxUses() {
      if (!this.expanded) {
         throw new IllegalStateException("Only valid for expanded invites");
      } else {
         return this.maxUses;
      }
   }

   @Nonnull
   public OffsetDateTime getTimeCreated() {
      if (!this.expanded) {
         throw new IllegalStateException("Only valid for expanded invites");
      } else {
         return this.timeCreated;
      }
   }

   public int getUses() {
      if (!this.expanded) {
         throw new IllegalStateException("Only valid for expanded invites");
      } else {
         return this.uses;
      }
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public boolean isTemporary() {
      if (!this.expanded) {
         throw new IllegalStateException("Only valid for expanded invites");
      } else {
         return this.temporary;
      }
   }

   public boolean isGuest() {
      return this.guest;
   }

   public int hashCode() {
      return this.code.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof InviteImpl)) {
         return false;
      } else {
         InviteImpl impl = (InviteImpl)obj;
         return impl.code.equals(this.code);
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("code", this.code).toString();
   }

   public static class ChannelImpl implements Invite.Channel {
      private final long id;
      private final String name;
      private final ChannelType type;

      public ChannelImpl(long id, String name, ChannelType type) {
         this.id = id;
         this.name = name;
         this.type = type;
      }

      public ChannelImpl(GuildChannel channel) {
         this(channel.getIdLong(), channel.getName(), channel.getType());
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public ChannelType getType() {
         return this.type;
      }

      public String toString() {
         return (new EntityString(this)).setType(this.getType()).setName(this.name).toString();
      }
   }

   public static class GuildImpl implements Invite.Guild {
      private final String vanityCode;
      private final String bannerId;
      private final String iconId;
      private final String name;
      private final String splashId;
      private final String description;
      private final int presenceCount;
      private final int memberCount;
      private final long id;
      private final net.dv8tion.jda.api.entities.Guild.VerificationLevel verificationLevel;
      private final net.dv8tion.jda.api.entities.Guild.NSFWLevel nsfwLevel;
      private final Set<String> features;
      private final GuildWelcomeScreen welcomeScreen;

      public GuildImpl(long id, String vanityCode, String bannerId, String iconId, String name, String splashId, String description, net.dv8tion.jda.api.entities.Guild.VerificationLevel verificationLevel, net.dv8tion.jda.api.entities.Guild.NSFWLevel nsfwLevel, int presenceCount, int memberCount, Set<String> features, GuildWelcomeScreen welcomeScreen) {
         this.id = id;
         this.vanityCode = vanityCode;
         this.bannerId = bannerId;
         this.iconId = iconId;
         this.name = name;
         this.splashId = splashId;
         this.description = description;
         this.verificationLevel = verificationLevel;
         this.nsfwLevel = nsfwLevel;
         this.presenceCount = presenceCount;
         this.memberCount = memberCount;
         this.features = features;
         this.welcomeScreen = welcomeScreen;
      }

      public GuildImpl(net.dv8tion.jda.api.entities.Guild guild) {
         this(guild.getIdLong(), guild.getVanityCode(), guild.getBannerId(), guild.getIconId(), guild.getName(), guild.getSplashId(), guild.getDescription(), guild.getVerificationLevel(), guild.getNSFWLevel(), -1, -1, guild.getFeatures(), (GuildWelcomeScreen)null);
      }

      @Nullable
      public String getVanityCode() {
         return this.vanityCode;
      }

      @Nullable
      public String getBannerId() {
         return this.bannerId;
      }

      @Nullable
      public String getDescription() {
         return this.description;
      }

      public String getIconId() {
         return this.iconId;
      }

      public String getIconUrl() {
         return this.getIconUrl(ImageFormat.PNG);
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      public String getSplashId() {
         return this.splashId;
      }

      public String getSplashUrl() {
         return this.getSplashUrl(ImageFormat.PNG);
      }

      @Nonnull
      public net.dv8tion.jda.api.entities.Guild.VerificationLevel getVerificationLevel() {
         return this.verificationLevel;
      }

      @Nonnull
      public net.dv8tion.jda.api.entities.Guild.NSFWLevel getNSFWLevel() {
         return this.nsfwLevel;
      }

      public int getOnlineCount() {
         return this.presenceCount;
      }

      public int getMemberCount() {
         return this.memberCount;
      }

      @Nonnull
      public Set<String> getFeatures() {
         return this.features;
      }

      @Nullable
      public GuildWelcomeScreen getWelcomeScreen() {
         return this.welcomeScreen;
      }

      public String toString() {
         return (new EntityString(this)).setName(this.name).toString();
      }
   }

   public static class GroupImpl implements Invite.Group {
      private final String iconId;
      private final String name;
      private final long id;
      private final List<String> users;

      public GroupImpl(String iconId, String name, long id, List<String> users) {
         this.iconId = iconId;
         this.name = name;
         this.id = id;
         this.users = users;
      }

      public String getIconId() {
         return this.iconId;
      }

      public String getIconUrl() {
         return this.getIconUrl(ImageFormat.PNG);
      }

      public String getName() {
         return this.name;
      }

      public long getIdLong() {
         return this.id;
      }

      public List<String> getUsers() {
         return this.users;
      }

      public String toString() {
         return (new EntityString(this)).setName(this.name).toString();
      }
   }

   public static class InviteTargetImpl implements Invite.InviteTarget {
      private final Invite.TargetType type;
      private final Invite.EmbeddedApplication targetApplication;
      private final User targetUser;

      public InviteTargetImpl(Invite.TargetType type, Invite.EmbeddedApplication targetApplication, User targetUser) {
         this.type = type;
         this.targetApplication = targetApplication;
         this.targetUser = targetUser;
      }

      @Nonnull
      public Invite.TargetType getType() {
         return this.type;
      }

      @Nonnull
      public String getId() {
         return this.getTargetEntity().getId();
      }

      public long getIdLong() {
         return this.getTargetEntity().getIdLong();
      }

      @Nullable
      public User getUser() {
         return this.targetUser;
      }

      @Nullable
      public Invite.EmbeddedApplication getApplication() {
         return this.targetApplication;
      }

      public String toString() {
         return (new EntityString(this)).setType(this.getType()).addMetadata("target", this.getTargetEntity()).toString();
      }

      @Nonnull
      private ISnowflake getTargetEntity() {
         if (this.targetUser != null) {
            return this.targetUser;
         } else if (this.targetApplication != null) {
            return this.targetApplication;
         } else {
            throw new IllegalStateException("No target entity");
         }
      }
   }

   public static class EmbeddedApplicationImpl implements Invite.EmbeddedApplication {
      private final String iconId;
      private final String name;
      private final String description;
      private final String summary;
      private final long id;
      private final int maxParticipants;

      public EmbeddedApplicationImpl(String iconId, String name, String description, String summary, long id, int maxParticipants) {
         this.iconId = iconId;
         this.name = name;
         this.description = description;
         this.summary = summary;
         this.id = id;
         this.maxParticipants = maxParticipants;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public String getDescription() {
         return this.description;
      }

      @Nullable
      public String getSummary() {
         return this.summary;
      }

      @Nullable
      public String getIconId() {
         return this.iconId;
      }

      @Nullable
      public String getIconUrl() {
         return this.getIconUrl(ImageFormat.PNG);
      }

      public int getMaxParticipants() {
         return this.maxParticipants;
      }

      public String toString() {
         return (new EntityString(this)).setName(this.name).toString();
      }
   }
}
