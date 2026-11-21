package net.dv8tion.jda.api.entities;

import java.util.Formattable;
import java.util.Formatter;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.MiscUtil;

public interface IMentionable extends Formattable, ISnowflake {
   @Nonnull
   String getAsMention();

   default void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean leftJustified = (flags & 1) == 1;
      boolean upper = (flags & 2) == 2;
      String out = upper ? this.getAsMention().toUpperCase(formatter.locale()) : this.getAsMention();
      MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
   }
}
