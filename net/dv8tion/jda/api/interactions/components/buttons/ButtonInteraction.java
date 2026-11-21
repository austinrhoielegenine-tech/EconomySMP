package net.dv8tion.jda.api.interactions.components.buttons;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

public interface ButtonInteraction extends ComponentInteraction {
   @Nonnull
   default Button getComponent() {
      return this.getButton();
   }

   @Nonnull
   Button getButton();

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> editButton(@Nullable Button newButton) {
      Message message = this.getMessage();
      MessageComponentTree newTree = message.getComponentTree().replace(ComponentReplacer.byUniqueId(this.getButton(), newButton));
      return this.isAcknowledged() ? ((WebhookMessageEditAction)this.getHook().editMessageComponentsById(message.getId(), newTree.getComponents()).useComponentsV2(message.isUsingComponentsV2())).map((it) -> null) : ((MessageEditCallbackAction)this.editComponents(newTree.getComponents()).useComponentsV2(message.isUsingComponentsV2())).map((it) -> null);
   }
}
