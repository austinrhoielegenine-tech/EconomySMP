package net.dv8tion.jda.api.components;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class Components {
   @Nonnull
   public static <T extends Component> T parseComponent(@Nonnull Class<T> componentType, @Nonnull DataObject data) {
      return (T)EntityBuilder.DEFAULT_COMPONENT_DESERIALIZER.deserializeAs(componentType, data);
   }

   @Nonnull
   public static <T extends Component> List<T> parseComponents(@Nonnull Class<T> componentType, @Nonnull DataArray data) {
      return (List)EntityBuilder.DEFAULT_COMPONENT_DESERIALIZER.deserializeAs(componentType, data).collect(Collectors.toList());
   }

   @Nonnull
   public static <T extends ComponentTree<?>> T parseTree(@Nonnull Class<T> treeType, @Nonnull DataArray data) {
      return (T)EntityBuilder.DEFAULT_COMPONENT_DESERIALIZER.deserializeAsTree(treeType, data);
   }
}
