package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.entities.UserSnowflakeImpl;

public interface UserSnowflake extends IMentionable {
   @Nonnull
   static UserSnowflake fromId(long id) {
      return new UserSnowflakeImpl(id);
   }

   @Nonnull
   static UserSnowflake fromId(@Nonnull String id) {
      return fromId(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   String getDefaultAvatarId();

   @Nonnull
   default String getDefaultAvatarUrl() {
      return this.getDefaultAvatar().getUrl();
   }

   @Nonnull
   default ImageProxy getDefaultAvatar() {
      return DiscordAssets.userDefaultAvatar(ImageFormat.PNG, this.getDefaultAvatarId());
   }
}
