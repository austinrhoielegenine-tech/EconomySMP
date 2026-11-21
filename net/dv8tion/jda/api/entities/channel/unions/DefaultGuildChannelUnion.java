package net.dv8tion.jda.api.entities.channel.unions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;

public interface DefaultGuildChannelUnion extends StandardGuildChannel {
   @Nonnull
   TextChannel asTextChannel();

   @Nonnull
   NewsChannel asNewsChannel();

   @Nonnull
   IThreadContainer asThreadContainer();

   @Nonnull
   StandardGuildMessageChannel asStandardGuildMessageChannel();
}
