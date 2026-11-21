package net.dv8tion.jda.internal.interactions.components.selections;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectInteraction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.SelectMenuMentions;

public class EntitySelectInteractionImpl extends SelectMenuInteractionImpl<IMentionable, EntitySelectMenu> implements EntitySelectInteraction {
   private final Mentions mentions;

   public EntitySelectInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, EntitySelectMenu.class, data);
      DataObject content = data.getObject("data");
      this.mentions = new SelectMenuMentions(jda, this.interactionEntityBuilder, this.getGuild(), (DataObject)content.optObject("resolved").orElseGet(DataObject::empty), (DataArray)content.optArray("values").orElseGet(DataArray::empty));
   }

   @Nonnull
   public Mentions getMentions() {
      return this.mentions;
   }

   @Nonnull
   public List<IMentionable> getValues() {
      return this.mentions.getMentions();
   }
}
