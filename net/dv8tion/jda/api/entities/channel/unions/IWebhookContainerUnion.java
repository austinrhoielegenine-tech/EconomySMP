package net.dv8tion.jda.api.entities.channel.unions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public interface IWebhookContainerUnion extends IWebhookContainer {
   @Nonnull
   TextChannel asTextChannel();

   @Nonnull
   NewsChannel asNewsChannel();

   @Nonnull
   ForumChannel asForumChannel();

   @Nonnull
   MediaChannel asMediaChannel();

   @Nonnull
   IThreadContainer asThreadContainer();

   @Nonnull
   VoiceChannel asVoiceChannel();

   @Nonnull
   StageChannel asStageChannel();

   @Nonnull
   GuildMessageChannel asGuildMessageChannel();

   @Nonnull
   StandardGuildChannel asStandardGuildChannel();

   @Nonnull
   StandardGuildMessageChannel asStandardGuildMessageChannel();

   @Nonnull
   AudioChannel asAudioChannel();
}
