package net.dv8tion.jda.api.components.attribute;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;

public interface ICustomId extends Component {
   @Nullable
   String getCustomId();

   @Nonnull
   ICustomId withUniqueId(int var1);
}
