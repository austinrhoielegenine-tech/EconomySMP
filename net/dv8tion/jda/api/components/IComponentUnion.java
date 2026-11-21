package net.dv8tion.jda.api.components;

import javax.annotation.Nonnull;

public interface IComponentUnion extends Component {
   default boolean isUnknownComponent() {
      return this.getType() == Component.Type.UNKNOWN;
   }

   @Nonnull
   IComponentUnion withUniqueId(int var1);
}
