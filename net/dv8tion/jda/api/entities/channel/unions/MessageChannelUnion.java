package net.dv8tion.jda.api.entities.channel.unions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public interface MessageChannelUnion extends MessageChannel {
   @Nonnull
   PrivateChannel asPrivateChannel();

   @Nonnull
   GroupChannel asGroupChannel();

   @Nonnull
   TextChannel asTextChannel();

   @Nonnull
   NewsChannel asNewsChannel();

   @Nonnull
   ThreadChannel asThreadChannel();

   @Nonnull
   VoiceChannel asVoiceChannel();

   @Nonnull
   StageChannel asStageChannel();

   @Nonnull
   IThreadContainer asThreadContainer();

   @Nonnull
   GuildMessageChannel asGuildMessageChannel();

   @Nonnull
   AudioChannel asAudioChannel();
}
