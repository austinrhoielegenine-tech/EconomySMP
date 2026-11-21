package net.dv8tion.jda.api.components.label;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.internal.components.label.LabelImpl;

public interface Label extends ModalTopLevelComponent {
   int LABEL_MAX_LENGTH = 45;
   int DESCRIPTION_MAX_LENGTH = 100;

   @Nonnull
   static Label of(@Nonnull String label, @Nullable String description, @Nonnull LabelChildComponent child) {
      return LabelImpl.validated(label, description, child);
   }

   @Nonnull
   static Label of(@Nonnull String label, @Nonnull LabelChildComponent child) {
      return of(label, (String)null, child);
   }

   @Nonnull
   Label withLabel(@Nonnull String var1);

   @Nonnull
   Label withDescription(@Nullable String var1);

   @Nonnull
   Label withChild(@Nonnull LabelChildComponent var1);

   @Nonnull
   String getLabel();

   @Nullable
   String getDescription();

   @Nonnull
   LabelChildComponentUnion getChild();

   @Nonnull
   @CheckReturnValue
   Label withUniqueId(int var1);
}
