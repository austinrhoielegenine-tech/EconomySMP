package net.dv8tion.jda.api.exceptions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.data.DataObject;

public class DataObjectParsingException extends ParsingException {
   private final DataObject data;

   public DataObjectParsingException(@Nonnull DataObject data, @Nonnull String message) {
      super(message + "\n" + data.toShallowString());
      this.data = data;
   }

   @Nonnull
   public DataObject getData() {
      return this.data;
   }
}
