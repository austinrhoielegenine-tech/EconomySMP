package net.dv8tion.jda.internal.components.tree;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;

public class MessageComponentTreeImpl extends AbstractComponentTree<MessageTopLevelComponentUnion> implements MessageComponentTree {
   private MessageComponentTreeImpl(Collection<MessageTopLevelComponentUnion> components) {
      super(components);
   }

   @Nonnull
   public static MessageComponentTree of(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "Components");
      Collection<MessageTopLevelComponentUnion> componentUnions = ComponentsUtil.<MessageTopLevelComponentUnion>membersToUnionWithUnknownType(components, MessageTopLevelComponentUnion.class);
      return new MessageComponentTreeImpl(componentUnions);
   }

   @Nonnull
   public ComponentTree.Type getType() {
      return ComponentTree.Type.MESSAGE;
   }

   @Nonnull
   public MessageComponentTree replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      return (MessageComponentTree)ComponentsUtil.doReplace(MessageTopLevelComponent.class, this.components, replacer, MessageComponentTreeImpl::new);
   }

   @Nonnull
   public MessageComponentTree withDisabled(boolean disabled) {
      return (MessageComponentTree)super.withDisabled(disabled);
   }
}
