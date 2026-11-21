package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.PrivateChannelImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class UserImpl extends UserSnowflakeImpl implements User {
   protected final JDAImpl api;
   protected short discriminator;
   protected String name;
   protected String globalName;
   protected String avatarId;
   protected User.Profile profile;
   protected long privateChannelId = 0L;
   protected boolean bot;
   protected boolean system;
   protected int flags;
   protected User.PrimaryGuild primaryGuild;

   public UserImpl(long id, JDAImpl api) {
      super(id);
      this.api = api;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getGlobalName() {
      return this.globalName;
   }

   @Nonnull
   public String getDiscriminator() {
      return this.discriminator == 0 ? "0000" : Helpers.format("%04d", this.discriminator);
   }

   @Nullable
   public String getAvatarId() {
      return this.avatarId;
   }

   @Nonnull
   public CacheRestAction<User.Profile> retrieveProfile() {
      return new DeferredRestAction(this.getJDA(), User.Profile.class, this::getProfile, () -> {
         Route.CompiledRoute route = Route.Users.GET_USER.compile(this.getId());
         return new RestActionImpl(this.getJDA(), route, (response, request) -> {
            DataObject json = response.getObject();
            String bannerId = json.getString("banner", (String)null);
            int accentColor = json.getInt("accent_color", 536870911);
            return new User.Profile(this.getIdLong(), bannerId, accentColor);
         });
      });
   }

   public User.Profile getProfile() {
      return this.profile;
   }

   @Nonnull
   public String getDefaultAvatarId() {
      return this.discriminator != 0 ? String.valueOf(this.discriminator % 5) : super.getDefaultAvatarId();
   }

   @Nonnull
   public String getAsTag() {
      return this.getName() + '#' + this.getDiscriminator();
   }

   public boolean hasPrivateChannel() {
      return this.privateChannelId != 0L;
   }

   @Nonnull
   public CacheRestAction<PrivateChannel> openPrivateChannel() {
      return new DeferredRestAction(this.getJDA(), PrivateChannel.class, this::getPrivateChannel, () -> {
         Route.CompiledRoute route = Route.Self.CREATE_PRIVATE_CHANNEL.compile();
         DataObject body = DataObject.empty().put("recipient_id", this.getId());
         return new RestActionImpl(this.getJDA(), route, body, (response, request) -> {
            PrivateChannel priv = this.api.getEntityBuilder().createPrivateChannel(response.getObject(), this);
            this.privateChannelId = priv.getIdLong();
            return priv;
         });
      });
   }

   public PrivateChannel getPrivateChannel() {
      if (!this.hasPrivateChannel()) {
         return null;
      } else {
         PrivateChannel channel = this.getJDA().getPrivateChannelById(this.privateChannelId);
         return (PrivateChannel)(channel != null ? channel : new PrivateChannelImpl(this.getJDA(), this.privateChannelId, this));
      }
   }

   @Nonnull
   public List<Guild> getMutualGuilds() {
      return this.getJDA().getMutualGuilds(this);
   }

   public boolean isBot() {
      return this.bot;
   }

   public boolean isSystem() {
      return this.system;
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   @Nonnull
   public EnumSet<User.UserFlag> getFlags() {
      return User.UserFlag.getFlags(this.flags);
   }

   public int getFlagsRaw() {
      return this.flags;
   }

   @Nullable
   public User.PrimaryGuild getPrimaryGuild() {
      return this.primaryGuild;
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }

   public UserImpl setName(String name) {
      this.name = name;
      return this;
   }

   public UserImpl setGlobalName(String globalName) {
      this.globalName = globalName;
      return this;
   }

   public UserImpl setDiscriminator(short discriminator) {
      this.discriminator = discriminator;
      return this;
   }

   public UserImpl setAvatarId(String avatarId) {
      this.avatarId = avatarId;
      return this;
   }

   public UserImpl setProfile(User.Profile profile) {
      this.profile = profile;
      return this;
   }

   public UserImpl setPrivateChannel(PrivateChannel privateChannel) {
      if (privateChannel != null) {
         this.privateChannelId = privateChannel.getIdLong();
      }

      return this;
   }

   public UserImpl setBot(boolean bot) {
      this.bot = bot;
      return this;
   }

   public UserImpl setSystem(boolean system) {
      this.system = system;
      return this;
   }

   public UserImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public UserImpl setPrimaryGuild(User.PrimaryGuild primaryGuild) {
      this.primaryGuild = primaryGuild;
      return this;
   }

   public short getDiscriminatorInt() {
      return this.discriminator;
   }

   public void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean alt = (flags & 4) == 4;
      boolean upper = (flags & 2) == 2;
      boolean leftJustified = (flags & 1) == 1;
      String out;
      if (!alt) {
         out = this.getAsMention();
      } else if (this.discriminator == 0 && upper) {
         out = this.getName().toUpperCase(formatter.locale());
      } else if (this.discriminator == 0) {
         out = this.getName();
      } else if (upper) {
         out = this.getAsTag().toUpperCase(formatter.locale());
      } else {
         out = this.getAsTag();
      }

      MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
   }
}
