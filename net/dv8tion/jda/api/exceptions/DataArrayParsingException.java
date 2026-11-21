package net.dv8tion.jda.api.exceptions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.utils.data.DataArray;

public class DataArrayParsingException extends ParsingException {
   private final DataArray data;

   public DataArrayParsingException(@Nonnull DataArray data, @Nonnull String message) {
      super(message + "\n" + data.toShallowString());
      this.data = data;
   }

   @Nonnull
   public DataArray getData() {
      return this.data;
   }
}
