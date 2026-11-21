package net.dv8tion.jda.api.components;

import javax.annotation.Nonnull;

public interface UnknownComponent extends Component {
   @Nonnull
   default Component.Type getType() {
      return Component.Type.UNKNOWN;
   }

   @Nonnull
   UnknownComponent withUniqueId(int var1);
}
