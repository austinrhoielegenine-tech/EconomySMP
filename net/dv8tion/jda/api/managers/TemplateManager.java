package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TemplateManager extends Manager<TemplateManager> {
   long NAME = 1L;
   long DESCRIPTION = 2L;

   @Nonnull
   @CheckReturnValue
   TemplateManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   TemplateManager reset(@Nonnull long... var1);

   @Nonnull
   @CheckReturnValue
   TemplateManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   TemplateManager setDescription(@Nullable String var1);
}
