package net.dv8tion.jda.internal.utils.message;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import net.dv8tion.jda.api.utils.messages.MessagePollData;

public interface MessageCreateBuilderMixin<R extends MessageCreateRequest<R>> extends AbstractMessageBuilderMixin<R, MessageCreateBuilder>, MessageCreateRequest<R> {
   @Nonnull
   default R addContent(@Nonnull String content) {
      ((MessageCreateBuilder)this.getBuilder()).addContent(content);
      return (R)this;
   }

   @Nonnull
   default R addEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      ((MessageCreateBuilder)this.getBuilder()).addEmbeds(embeds);
      return (R)this;
   }

   @Nonnull
   default R addComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      ((MessageCreateBuilder)this.getBuilder()).addComponents(components);
      return (R)this;
   }

   @Nonnull
   default R addFiles(@Nonnull Collection<? extends FileUpload> files) {
      ((MessageCreateBuilder)this.getBuilder()).addFiles(files);
      return (R)this;
   }

   @Nullable
   default MessagePollData getPoll() {
      return ((MessageCreateBuilder)this.getBuilder()).getPoll();
   }

   @Nonnull
   default R setPoll(@Nullable MessagePollData poll) {
      ((MessageCreateBuilder)this.getBuilder()).setPoll(poll);
      return (R)this;
   }

   @Nonnull
   default R setTTS(boolean tts) {
      ((MessageCreateBuilder)this.getBuilder()).setTTS(tts);
      return (R)this;
   }

   @Nonnull
   default R setFiles(@Nullable Collection<? extends FileUpload> files) {
      ((MessageCreateBuilder)this.getBuilder()).setFiles(files);
      return (R)this;
   }

   @Nonnull
   default List<FileUpload> getAttachments() {
      return ((MessageCreateBuilder)this.getBuilder()).getAttachments();
   }

   @Nonnull
   default R setSuppressedNotifications(boolean suppressed) {
      ((MessageCreateBuilder)this.getBuilder()).setSuppressedNotifications(suppressed);
      return (R)this;
   }

   @Nonnull
   default R setVoiceMessage(boolean voiceMessage) {
      ((MessageCreateBuilder)this.getBuilder()).setVoiceMessage(voiceMessage);
      return (R)this;
   }
}
