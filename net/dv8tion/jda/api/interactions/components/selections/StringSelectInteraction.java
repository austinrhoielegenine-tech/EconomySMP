package net.dv8tion.jda.api.interactions.components.selections;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface StringSelectInteraction extends SelectMenuInteraction<String, StringSelectMenu> {
   @Nonnull
   @Unmodifiable List<String> getValues();

   @Nonnull
   default @Unmodifiable List<SelectOption> getSelectedOptions() {
      StringSelectMenu menu = (StringSelectMenu)this.getComponent();
      List<String> values = this.getValues();
      return (List)menu.getOptions().stream().filter((it) -> values.contains(it.getValue())).collect(Helpers.toUnmodifiableList());
   }
}
