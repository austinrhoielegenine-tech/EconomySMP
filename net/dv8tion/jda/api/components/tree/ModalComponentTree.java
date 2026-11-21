package net.dv8tion.jda.api.components.tree;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.internal.components.tree.ModalComponentTreeImpl;

public interface ModalComponentTree extends ComponentTree<ModalTopLevelComponentUnion> {
   @Nonnull
   ModalComponentTree replace(@Nonnull ComponentReplacer var1);

   @Nonnull
   @CheckReturnValue
   ModalComponentTree withDisabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   default ModalComponentTree asDisabled() {
      return (ModalComponentTree)ComponentTree.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default ModalComponentTree asEnabled() {
      return (ModalComponentTree)ComponentTree.super.asEnabled();
   }

   @Nonnull
   static ModalComponentTree of(@Nonnull Collection<? extends ModalTopLevelComponent> components) {
      return ModalComponentTreeImpl.of(components);
   }

   @Nonnull
   static ModalComponentTree of(@Nonnull ModalTopLevelComponent... components) {
      return of(Arrays.asList(components));
   }
}
