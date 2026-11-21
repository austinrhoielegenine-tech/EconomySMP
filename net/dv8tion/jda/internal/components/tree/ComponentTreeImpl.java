package net.dv8tion.jda.internal.components.tree;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;

public final class ComponentTreeImpl<E extends Component> extends AbstractComponentTree<E> {
   private final Class<? extends Component> componentType;

   public ComponentTreeImpl(Class<? extends Component> componentType, Collection<E> components) {
      super(components);
      this.componentType = componentType;
   }

   @Nonnull
   public ComponentTree.Type getType() {
      return ComponentTree.Type.ANY;
   }

   @Nonnull
   public ComponentTree<E> replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      return (ComponentTree)ComponentsUtil.doReplace(this.componentType, this.components, replacer, (newComponents) -> new ComponentTreeImpl(this.componentType, newComponents));
   }
}
