package net.dv8tion.jda.api.components.replacer;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface IReplaceable {
   @Nonnull
   Component replace(@Nonnull ComponentReplacer var1);
}
