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
import net.dv8tion.jda.internal.components.ResolvedMediaImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.entities.FileContainerMixin;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class FileDisplayImpl extends AbstractComponentImpl implements FileDisplay, MessageTopLevelComponentUnion, ContainerChildComponentUnion, FileContainerMixin {
   private final int uniqueId;
   private final String url;
   private final ResolvedMedia media;
   private final boolean spoiler;

   public FileDisplayImpl(DataObject data) {
      this(data.getInt("id", -1), data.getObject("file").getString("url"), new ResolvedMediaImpl(data.getObject("file")), data.getBoolean("spoiler", false));
   }

   public FileDisplayImpl(String url) {
      this(-1, url, (ResolvedMedia)null, false);
   }

   private FileDisplayImpl(int uniqueId, String url, ResolvedMedia media, boolean spoiler) {
      this.uniqueId = uniqueId;
      this.url = url;
      this.media = media;
      this.spoiler = spoiler;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.FILE_DISPLAY;
   }

   @Nonnull
   public FileDisplayImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new FileDisplayImpl(uniqueId, this.url, this.media, this.spoiler);
   }

   @Nonnull
   public FileDisplay withSpoiler(boolean spoiler) {
      return new FileDisplayImpl(this.uniqueId, this.url, this.media, spoiler);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getUrl() {
      return this.url;
   }

   @Nullable
   public ResolvedMedia getResolvedMedia() {
      return this.media;
   }

   public Stream<FileUpload> getFiles() {
      return ComponentsUtil.getFilesFromMedia(this.media);
   }

   public boolean isSpoiler() {
      return this.spoiler;
   }

   @Nonnull
   public DataObject toData() {
      String outputUrl = ComponentsUtil.getMediaUrl(this.media, this.url);
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("file", DataObject.empty().put("url", outputUrl)).put("spoiler", this.spoiler);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof FileDisplayImpl)) {
         return false;
      } else {
         FileDisplayImpl fileDisplay = (FileDisplayImpl)o;
         return this.uniqueId == fileDisplay.uniqueId && this.spoiler == fileDisplay.spoiler && Objects.equals(this.url, fileDisplay.url);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.url, this.spoiler});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("url", this.url).addMetadata("media", this.media).addMetadata("spoiler", this.spoiler).toString();
   }
}
