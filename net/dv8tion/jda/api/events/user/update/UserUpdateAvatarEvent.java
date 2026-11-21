package net.dv8tion.jda.api.events.user.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class UserUpdateAvatarEvent extends GenericUserUpdateEvent<String> {
   public static final String IDENTIFIER = "avatar";

   public UserUpdateAvatarEvent(@Nonnull JDA api, long responseNumber, @Nonnull User user, @Nullable String oldAvatar) {
      super(api, responseNumber, user, oldAvatar, user.getAvatarId(), "avatar");
   }

   @Nullable
   public String getOldAvatarId() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getOldAvatarUrl() {
      return this.previous == null ? null : this.getOldAvatarUrl(((String)this.previous).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getOldAvatarUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getOldAvatar(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getOldAvatar() {
      String oldAvatarUrl = this.getOldAvatarUrl();
      return oldAvatarUrl == null ? null : new ImageProxy(oldAvatarUrl);
   }

   @Nullable
   public ImageProxy getOldAvatar(@Nonnull ImageFormat format) {
      return DiscordAssets.userAvatar(format, this.getUser().getId(), this.previous);
   }

   @Nullable
   public String getNewAvatarId() {
      return (String)this.getNewValue();
   }

   @Nullable
   public String getNewAvatarUrl() {
      return this.next == null ? null : this.getNewAvatarUrl(((String)this.next).startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public String getNewAvatarUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getNewAvatar(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getNewAvatar() {
      String newAvatarUrl = this.getNewAvatarUrl();
      return newAvatarUrl == null ? null : new ImageProxy(newAvatarUrl);
   }

   @Nullable
   public ImageProxy getNewAvatar(@Nonnull ImageFormat format) {
      return DiscordAssets.userAvatar(format, this.getUser().getId(), this.next);
   }
}
