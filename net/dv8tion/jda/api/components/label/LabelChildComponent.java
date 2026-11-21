package net.dv8tion.jda.api.components.label;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface LabelChildComponent extends Component {
   @Nonnull
   LabelChildComponent withUniqueId(int var1);
}
