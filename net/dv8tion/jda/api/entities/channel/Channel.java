package net.dv8tion.jda.api.entities.channel;

import java.util.EnumSet;
import java.util.Formatter;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MiscUtil;

public interface Channel extends IMentionable, IDetachableEntity {
   int MAX_NAME_LENGTH = 100;

   @Nonnull
   default EnumSet<ChannelFlag> getFlags() {
      return EnumSet.noneOf(ChannelFlag.class);
   }

   @Nonnull
   String getName();

   @Nonnull
   ChannelType getType();

   @Nonnull
   JDA getJDA();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> delete();

   @Nonnull
   String getJumpUrl();

   @Nonnull
   default String getAsMention() {
      return "<#" + this.getId() + '>';
   }

   default void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean leftJustified = (flags & 1) == 1;
      boolean upper = (flags & 2) == 2;
      boolean alt = (flags & 4) == 4;
      String out;
      if (alt) {
         out = "#" + (upper ? this.getName().toUpperCase(formatter.locale()) : this.getName());
      } else {
         out = this.getAsMention();
      }

      MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
   }
}
