package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IPositionableChannelManager;

public interface IPositionableChannel extends GuildChannel {
   @Nonnull
   @CheckReturnValue
   IPositionableChannelManager<?, ?> getManager();

   default int getPosition() {
      int position = this.getGuild().getChannels().indexOf(this);
      if (position > -1) {
         return position;
      } else {
         throw new IllegalStateException("Somehow when determining position we never found the " + this.getType().name() + " in the Guild's channels? wtf?");
      }
   }

   int getPositionRaw();
}
