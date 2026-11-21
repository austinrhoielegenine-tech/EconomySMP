package net.dv8tion.jda.api.interactions.components.selections;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

public interface SelectMenuInteraction<T, S extends SelectMenu> extends ComponentInteraction {
   @Nonnull
   S getComponent();

   @Nonnull
   default S getSelectMenu() {
      return (S)this.getComponent();
   }

   @Nonnull
   List<T> getValues();

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> editSelectMenu(@Nullable SelectMenu newMenu) {
      Message message = this.getMessage();
      MessageComponentTree newTree = message.getComponentTree().replace(ComponentReplacer.byUniqueId(this.getSelectMenu(), newMenu));
      return this.isAcknowledged() ? ((WebhookMessageEditAction)this.getHook().editMessageComponentsById(message.getId(), newTree.getComponents()).useComponentsV2(message.isUsingComponentsV2())).map((it) -> null) : ((MessageEditCallbackAction)this.editComponents(newTree.getComponents()).useComponentsV2(message.isUsingComponentsV2())).map((it) -> null);
   }
}
