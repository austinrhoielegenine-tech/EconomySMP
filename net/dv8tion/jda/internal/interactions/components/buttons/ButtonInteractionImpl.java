package net.dv8tion.jda.internal.interactions.components.buttons;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.components.ComponentInteractionImpl;

public class ButtonInteractionImpl extends ComponentInteractionImpl implements ButtonInteraction {
   private final Button button;

   public ButtonInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      if (this.message != null) {
         this.button = (Button)this.message.getComponentTree().find(Button.class, (b) -> this.customId.equals(b.getCustomId())).orElse((Object)null);
      } else {
         this.button = null;
      }

   }

   @Nonnull
   public Component.Type getComponentType() {
      return Component.Type.BUTTON;
   }

   @Nonnull
   public Button getButton() {
      return this.button;
   }
}
