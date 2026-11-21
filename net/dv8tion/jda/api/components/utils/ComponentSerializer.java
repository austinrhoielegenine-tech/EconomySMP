package net.dv8tion.jda.api.components.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.message.MessageUtil;

public class ComponentSerializer {
   @Nonnull
   public DataObject serialize(@Nonnull Component component) {
      Checks.notNull(component, "Component");
      Checks.check(component instanceof SerializableData, "Component is not serializable");
      return ((SerializableData)component).toData();
   }

   @Nonnull
   public List<DataObject> serializeAll(@Nonnull Collection<? extends Component> components) {
      Checks.noneNull(components, "Components");
      Stream var10000 = components.stream();
      Objects.requireNonNull(SerializableData.class);
      return (List)var10000.map(SerializableData.class::cast).map(SerializableData::toData).collect(Collectors.toList());
   }

   @Nonnull
   public List<FileUpload> getFileUploads(@Nonnull Component component) {
      Checks.notNull(component, "Component");
      return MessageUtil.getIndirectFiles(Collections.singletonList(component));
   }

   @Nonnull
   public List<FileUpload> getFileUploads(@Nonnull Collection<? extends Component> components) {
      Checks.noneNull(components, "Components");
      return MessageUtil.getIndirectFiles(components);
   }
}
