package net.dv8tion.jda.internal.components.tree;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.attribute.IDisableable;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.internal.utils.Helpers;

public abstract class AbstractComponentTree<E extends Component> implements ComponentTree<E> {
   protected final List<E> components;

   protected AbstractComponentTree(Collection<E> components) {
      this.components = Helpers.<E>copyAsUnmodifiableList(components);
   }

   @Nonnull
   public List<E> getComponents() {
      return this.components;
   }

   @Nonnull
   public ComponentTree<E> withDisabled(boolean disabled) {
      return this.replace(ComponentReplacer.of(IDisableable.class, (c) -> true, (c) -> c.withDisabled(disabled)));
   }
}
