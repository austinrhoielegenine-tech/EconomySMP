package net.dv8tion.jda.api.components.section;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;

public interface SectionContentComponent extends Component {
   @Nonnull
   SectionContentComponent withUniqueId(int var1);
}
