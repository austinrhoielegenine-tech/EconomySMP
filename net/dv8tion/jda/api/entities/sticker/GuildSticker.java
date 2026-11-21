package net.dv8tion.jda.api.entities.sticker;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import org.jetbrains.annotations.Contract;

public interface GuildSticker extends RichSticker {
   @Nonnull
   default Sticker.Type getType() {
      return Sticker.Type.GUILD;
   }

   boolean isAvailable();

   long getGuildIdLong();

   @Nonnull
   default String getGuildId() {
      return Long.toUnsignedString(this.getGuildIdLong());
   }

   @Nullable
   Guild getGuild();

   @Nullable
   User getOwner();

   @Nonnull
   @CheckReturnValue
   CacheRestAction<User> retrieveOwner();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   @Contract("->new")
   GuildStickerManager getManager();
}
