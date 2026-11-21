package net.dv8tion.jda.internal.components.filedisplay;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class FileDisplayFileUpload extends AbstractComponentImpl implements FileDisplay, MessageTopLevelComponentUnion, ContainerChildComponentUnion, FileContainerMixin {
   private final int uniqueId;
   private final FileUpload file;
   private final boolean spoiler;

   public FileDisplayFileUpload(FileUpload file) {
      this(-1, file, false);
   }

   public FileDisplayFileUpload(int uniqueId, FileUpload file, boolean spoiler) {
      this.uniqueId = uniqueId;
      this.file = file;
      this.spoiler = spoiler;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.FILE_DISPLAY;
   }

   @Nonnull
   public FileDisplayFileUpload withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new FileDisplayFileUpload(uniqueId, this.file, this.spoiler);
   }

   @Nonnull
   public FileDisplay withSpoiler(boolean spoiler) {
      return new FileDisplayFileUpload(this.uniqueId, this.file, spoiler);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getUrl() {
      return "attachment://" + this.file.getName();
   }

   public Stream<FileUpload> getFiles() {
      return Stream.of(this.file);
   }

   @Nullable
   public ResolvedMedia getResolvedMedia() {
      return null;
   }

   public boolean isSpoiler() {
      return this.spoiler;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("file", DataObject.empty().put("url", this.getUrl())).put("spoiler", this.spoiler);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FileDisplayFileUpload)) {
         return false;
      } else {
         FileDisplayFileUpload fileDisplay = (FileDisplayFileUpload)o;
         return this.uniqueId == fileDisplay.uniqueId && this.spoiler == fileDisplay.spoiler && Objects.equals(this.file, fileDisplay.file);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.file, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("file", this.file).addMetadata("spoiler", this.spoiler).toString();
   }
}
