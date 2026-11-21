package net.dv8tion.jda.api.entities.channel.unions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public interface IPermissionContainerUnion extends IPermissionContainer {
   @Nonnull
   TextChannel asTextChannel();

   @Nonnull
   NewsChannel asNewsChannel();

   @Nonnull
   VoiceChannel asVoiceChannel();

   @Nonnull
   StageChannel asStageChannel();

   @Nonnull
   Category asCategory();

   @Nonnull
   ForumChannel asForumChannel();

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
