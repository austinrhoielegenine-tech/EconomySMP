package net.dv8tion.jda.internal.interactions.components.selections;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.components.ComponentInteractionImpl;

public abstract class SelectMenuInteractionImpl<T, S extends SelectMenu> extends ComponentInteractionImpl implements SelectMenuInteraction<T, S> {
   private final S menu;

   public SelectMenuInteractionImpl(JDAImpl jda, Class<S> type, DataObject data) {
      super(jda, data);
      if (this.message != null) {
         this.menu = (S)(this.message.getComponentTree().find(type, (s) -> this.customId.equals(s.getCustomId())).orElse((Object)null));
      } else {
         this.menu = null;
      }

   }

   @Nonnull
   public S getComponent() {
      return this.menu;
   }

   @Nonnull
   public Component.Type getComponentType() {
      return this.menu.getType();
   }
}
