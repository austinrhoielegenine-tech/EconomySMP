package net.dv8tion.jda.api.entities.templates;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.jetbrains.annotations.Unmodifiable;

public class TemplateGuild implements ISnowflake {
   private final long id;
   private final String name;
   private final String description;
   private final String iconId;
   private final Guild.VerificationLevel verificationLevel;
   private final Guild.NotificationLevel notificationLevel;
   private final Guild.ExplicitContentLevel explicitContentLevel;
   private final DiscordLocale locale;
   private final Guild.Timeout afkTimeout;
   private final TemplateChannel afkChannel;
   private final TemplateChannel systemChannel;
   private final List<TemplateRole> roles;
   private final List<TemplateChannel> channels;

   public TemplateGuild(long id, String name, String description, String iconId, Guild.VerificationLevel verificationLevel, Guild.NotificationLevel notificationLevel, Guild.ExplicitContentLevel explicitContentLevel, DiscordLocale locale, Guild.Timeout afkTimeout, TemplateChannel afkChannel, TemplateChannel systemChannel, List<TemplateRole> roles, List<TemplateChannel> channels) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.iconId = iconId;
      this.verificationLevel = verificationLevel;
      this.notificationLevel = notificationLevel;
      this.explicitContentLevel = explicitContentLevel;
      this.locale = locale;
      this.afkTimeout = afkTimeout;
      this.afkChannel = afkChannel;
      this.systemChannel = systemChannel;
      this.roles = Collections.unmodifiableList(roles);
      this.channels = Collections.unmodifiableList(channels);
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nullable
   public String getIconId() {
      return this.iconId;
   }

   @Nullable
   public String getIconUrl() {
      return this.iconId == null ? null : this.getIconUrl(this.iconId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getIcon(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getIcon() {
      String iconUrl = this.getIconUrl();
      return iconUrl == null ? null : new ImageProxy(iconUrl);
   }

   @Nullable
   public ImageProxy getIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.guildIcon(format, Long.toUnsignedString(this.id), this.iconId);
   }

   @Nonnull
   public Guild.VerificationLevel getVerificationLevel() {
      return this.verificationLevel;
   }

   @Nonnull
   public Guild.NotificationLevel getDefaultNotificationLevel() {
      return this.notificationLevel;
   }

   @Nonnull
   public Guild.ExplicitContentLevel getExplicitContentLevel() {
      return this.explicitContentLevel;
   }

   @Nonnull
   public DiscordLocale getLocale() {
      return this.locale;
   }

   @Nonnull
   public Guild.Timeout getAfkTimeout() {
      return this.afkTimeout;
   }

   @Nullable
   public TemplateChannel getAfkChannel() {
      return this.afkChannel;
   }

   @Nullable
   public TemplateChannel getSystemChannel() {
      return this.systemChannel;
   }

   @Nonnull
   public @Unmodifiable List<TemplateRole> getRoles() {
      return this.roles;
   }

   @Nonnull
   public @Unmodifiable List<TemplateChannel> getChannels() {
      return this.channels;
   }
}
