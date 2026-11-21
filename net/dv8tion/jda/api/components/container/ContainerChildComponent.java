package net.dv8tion.jda.api.components.container;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface ContainerChildComponent extends Component {
   @Nonnull
   ContainerChildComponent withUniqueId(int var1);
}
