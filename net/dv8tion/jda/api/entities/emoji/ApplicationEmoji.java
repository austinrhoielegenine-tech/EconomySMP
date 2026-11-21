package net.dv8tion.jda.api.entities.emoji;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.ApplicationEmojiManager;
import net.dv8tion.jda.api.requests.RestAction;

public interface ApplicationEmoji extends CustomEmoji {
   int MAX_APPLICATION_EMOJIS = 2000;

   @Nonnull
   JDA getJDA();

   @Nullable
   User getOwner();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   ApplicationEmojiManager getManager();
}
