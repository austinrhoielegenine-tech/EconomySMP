package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.ISoundboardSoundChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.managers.SoundboardSoundManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

public interface SoundboardSound extends SoundboardSoundSnowflake {
   @Nonnull
   JDA getJDA();

   @Nonnull
   String getName();

   double getVolume();

   @Nullable
   EmojiUnion getEmoji();

   @Nullable
   Guild getGuild();

   boolean isAvailable();

   @Nullable
   User getUser();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> sendTo(@Nonnull ISoundboardSoundChannel var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   SoundboardSoundManager getManager();
}
