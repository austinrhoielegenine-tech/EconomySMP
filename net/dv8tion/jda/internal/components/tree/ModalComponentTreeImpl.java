package net.dv8tion.jda.internal.components.tree;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.ModalComponentTree;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;

public class ModalComponentTreeImpl extends AbstractComponentTree<ModalTopLevelComponentUnion> implements ModalComponentTree {
   private ModalComponentTreeImpl(Collection<ModalTopLevelComponentUnion> components) {
      super(components);
   }

   @Nonnull
   public static ModalComponentTree of(@Nonnull Collection<? extends ModalTopLevelComponent> components) {
      Checks.notEmpty(components, "Components");
      Checks.noneNull(components, "Components");
      Collection<ModalTopLevelComponentUnion> componentUnions = ComponentsUtil.<ModalTopLevelComponentUnion>membersToUnionWithUnknownType(components, ModalTopLevelComponentUnion.class);
      return new ModalComponentTreeImpl(componentUnions);
   }

   @Nonnull
   public ComponentTree.Type getType() {
      return ComponentTree.Type.MODAL;
   }

   @Nonnull
   public ModalComponentTree replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      return (ModalComponentTree)ComponentsUtil.doReplace(ModalTopLevelComponent.class, this.components, replacer, ModalComponentTreeImpl::new);
   }

   @Nonnull
   public ModalComponentTree withDisabled(boolean disabled) {
      return (ModalComponentTree)super.withDisabled(disabled);
   }
}
