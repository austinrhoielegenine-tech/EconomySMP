package net.dv8tion.jda.internal.utils.message;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.entities.FileContainerMixin;

public class MessageUtil {
   @Nonnull
   public static List<FileUpload> getIndirectFiles(@Nonnull Collection<? extends Component> components) {
      Stream var10000 = ComponentIterator.createStream(components);
      Objects.requireNonNull(FileContainerMixin.class);
      var10000 = var10000.filter(FileContainerMixin.class::isInstance);
      Objects.requireNonNull(FileContainerMixin.class);
      return (List)var10000.map(FileContainerMixin.class::cast).flatMap(FileContainerMixin::getFiles).collect(Collectors.toList());
   }

   public static DataArray getAttachmentsData(@Nonnull Collection<? extends AttachedFile> files) {
      DataArray attachments = DataArray.empty();
      int i = 0;

      for(AttachedFile file : files) {
         attachments.add(file.toAttachmentData(i));
         ++i;
      }

      return attachments;
   }
}
