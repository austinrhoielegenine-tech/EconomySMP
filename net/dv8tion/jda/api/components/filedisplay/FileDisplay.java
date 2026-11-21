package net.dv8tion.jda.api.components.filedisplay;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.components.filedisplay.FileDisplayFileUpload;
import net.dv8tion.jda.internal.components.filedisplay.FileDisplayImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface FileDisplay extends Component, MessageTopLevelComponent, ContainerChildComponent {
   @Nonnull
   static FileDisplay fromFile(@Nonnull FileUpload file) {
      Checks.notNull(file, "FileUpload");
      return new FileDisplayFileUpload(file);
   }

   @Nonnull
   static FileDisplay fromFileName(@Nonnull String fileName) {
      Checks.notNull(fileName, "File name");
      return new FileDisplayImpl("attachment://" + fileName);
   }

   @Nonnull
   @CheckReturnValue
   FileDisplay withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   FileDisplay withSpoiler(boolean var1);

   @Nonnull
   String getUrl();

   @Nullable
   ResolvedMedia getResolvedMedia();

   boolean isSpoiler();
}
