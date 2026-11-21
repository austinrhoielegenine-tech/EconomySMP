package net.dv8tion.jda.api.entities.channel.unions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public interface ChannelUnion extends Channel {
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
   ForumChannel asForumChannel();

   @Nonnull
   MediaChannel asMediaChannel();

   @Nonnull
   Category asCategory();

   @Nonnull
   MessageChannel asMessageChannel();

   @Nonnull
   GuildChannel asGuildChannel();

   @Nonnull
   GuildMessageChannel asGuildMessageChannel();

   @Nonnull
   AudioChannel asAudioChannel();

   IThreadContainer asThreadContainer();

   @Nonnull
   StandardGuildChannel asStandardGuildChannel();

   @Nonnull
   StandardGuildMessageChannel asStandardGuildMessageChannel();
}
