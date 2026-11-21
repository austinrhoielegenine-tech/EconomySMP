package net.dv8tion.jda.internal.interactions.components.selections;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectInteraction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class StringSelectInteractionImpl extends SelectMenuInteractionImpl<String, StringSelectMenu> implements StringSelectInteraction {
   private final List<String> values;

   public StringSelectInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, StringSelectMenu.class, data);
      this.values = Collections.unmodifiableList(this.parseValues(data.getObject("data")));
   }

   protected List<String> parseValues(DataObject data) {
      return (List)data.optArray("values").map((arr) -> (List)arr.stream(DataArray::getString).collect(Collectors.toList())).orElse(Collections.emptyList());
   }

   @Nonnull
   public List<String> getValues() {
      return this.values;
   }
}
