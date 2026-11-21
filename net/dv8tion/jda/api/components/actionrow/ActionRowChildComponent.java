package net.dv8tion.jda.api.components.actionrow;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface ActionRowChildComponent extends Component {
   @Nonnull
   ActionRowChildComponent withUniqueId(int var1);
}
