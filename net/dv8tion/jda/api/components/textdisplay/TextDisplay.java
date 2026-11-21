package net.dv8tion.jda.api.components.textdisplay;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.SectionContentComponent;
import net.dv8tion.jda.internal.components.textdisplay.TextDisplayImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface TextDisplay extends Component, MessageTopLevelComponent, ModalTopLevelComponent, ContainerChildComponent, SectionContentComponent {
   @Nonnull
   static TextDisplay of(@Nonnull String content) {
      Checks.notBlank(content, "Content");
      return new TextDisplayImpl(content);
   }

   @Nonnull
   @FormatMethod
   static TextDisplay ofFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format string");
      Checks.notNull(args, "Format args");
      return of(String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   TextDisplay withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   TextDisplay withContent(@Nonnull String var1);

   @Nonnull
   String getContent();
}
