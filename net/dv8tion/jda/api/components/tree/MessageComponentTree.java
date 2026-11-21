package net.dv8tion.jda.api.components.tree;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.internal.components.tree.MessageComponentTreeImpl;

public interface MessageComponentTree extends ComponentTree<MessageTopLevelComponentUnion> {
   @Nonnull
   @CheckReturnValue
   MessageComponentTree replace(@Nonnull ComponentReplacer var1);

   @Nonnull
   @CheckReturnValue
   MessageComponentTree withDisabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   default MessageComponentTree asDisabled() {
      return (MessageComponentTree)ComponentTree.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default MessageComponentTree asEnabled() {
      return (MessageComponentTree)ComponentTree.super.asEnabled();
   }

   @Nonnull
   static MessageComponentTree of(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return MessageComponentTreeImpl.of(components);
   }

   @Nonnull
   static MessageComponentTree of(@Nonnull MessageTopLevelComponent... components) {
      return of(Arrays.asList(components));
   }
}
