package net.dv8tion.jda.api.utils.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.IOUtil;

public class MessageCreateBuilder extends AbstractMessageBuilder<MessageCreateData, MessageCreateBuilder> implements MessageCreateRequest<MessageCreateBuilder> {
   private final List<FileUpload> files = new ArrayList(10);
   private MessagePollData poll;
   private boolean tts;

   @Nonnull
   public static MessageCreateBuilder from(@Nonnull MessageCreateData data) {
      return (MessageCreateBuilder)(new MessageCreateBuilder()).applyData(data);
   }

   @Nonnull
   public static MessageCreateBuilder fromEditData(@Nonnull MessageEditData data) {
      return (MessageCreateBuilder)(new MessageCreateBuilder()).applyEditData(data);
   }

   @Nonnull
   public static MessageCreateBuilder fromMessage(@Nonnull Message message) {
      return (MessageCreateBuilder)(new MessageCreateBuilder()).applyMessage(message);
   }

   @Nonnull
   public MessageCreateBuilder addContent(@Nonnull String content) {
      Checks.notNull(content, "Content");
      Checks.check(Helpers.codePointLength(this.content) + Helpers.codePointLength(content) <= 2000, "Cannot have content longer than %d characters", (int)2000);
      this.content.append(content);
      return this;
   }

   @Nonnull
   public MessageCreateBuilder addEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      Checks.noneNull(embeds, "Embeds");
      Checks.check(this.embeds.size() + embeds.size() <= 10, "Cannot add more than %d embeds", (int)10);
      this.embeds.addAll(embeds);
      return this;
   }

   @Nonnull
   public MessageCreateBuilder addComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "MessageTopLevelComponents");
      Checks.checkComponents("Provided component is invalid for messages!", components, Component::isMessageCompatible);
      List<MessageTopLevelComponentUnion> componentsAsUnions = ComponentsUtil.<MessageTopLevelComponentUnion>membersToUnion(components, MessageTopLevelComponentUnion.class);
      this.components.addAll(componentsAsUnions);
      return this;
   }

   @Nonnull
   public MessageCreateBuilder setFiles(@Nullable Collection<? extends FileUpload> files) {
      if (files != null) {
         Checks.noneNull(files, "Files");
      }

      this.files.clear();
      if (files != null) {
         this.files.addAll(files);
         this.setVoiceMessageIfApplicable(files);
      }

      return this;
   }

   @Nonnull
   public List<FileUpload> getAttachments() {
      return Collections.unmodifiableList(this.files);
   }

   @Nullable
   public MessagePollData getPoll() {
      return this.poll;
   }

   @Nonnull
   public MessageCreateBuilder setPoll(@Nullable MessagePollData poll) {
      this.poll = poll;
      return this;
   }

   @Nonnull
   public MessageCreateBuilder addFiles(@Nonnull Collection<? extends FileUpload> files) {
      Checks.noneNull(files, "Files");
      this.files.addAll(files);
      this.setVoiceMessageIfApplicable(files);
      return this;
   }

   @Nonnull
   public MessageCreateBuilder setTTS(boolean tts) {
      this.tts = tts;
      return this;
   }

   @Nonnull
   public MessageCreateBuilder setSuppressedNotifications(boolean suppressed) {
      if (suppressed) {
         this.messageFlags |= Message.MessageFlag.NOTIFICATIONS_SUPPRESSED.getValue();
      } else {
         this.messageFlags &= ~Message.MessageFlag.NOTIFICATIONS_SUPPRESSED.getValue();
      }

      return this;
   }

   @Nonnull
   public MessageCreateBuilder setVoiceMessage(boolean voiceMessage) {
      if (voiceMessage) {
         this.messageFlags |= Message.MessageFlag.IS_VOICE_MESSAGE.getValue();
      } else {
         this.messageFlags &= ~Message.MessageFlag.IS_VOICE_MESSAGE.getValue();
      }

      return this;
   }

   public boolean isEmpty() {
      return Helpers.isBlank(this.content) && this.embeds.isEmpty() && this.files.isEmpty() && this.components.isEmpty() && this.poll == null;
   }

   public boolean isValid() {
      return this.isUsingComponentsV2() ? this.isV2Valid() : this.isV1Valid();
   }

   private boolean isV1Valid() {
      return !this.isEmpty() && this.embeds.size() <= 10 && this.components.size() <= 5 && !ComponentsUtil.hasIllegalV1Components(this.components) && Helpers.codePointLength(this.content) <= 2000;
   }

   private boolean isV2Valid() {
      return this.content.length() == 0 && this.embeds.isEmpty() && this.poll == null && !this.components.isEmpty() && ComponentsUtil.getComponentTreeSize(this.components) <= 40L && ComponentsUtil.getComponentTreeTextContentLength(this.components) <= 4000L;
   }

   @Nonnull
   public MessageCreateData build() {
      return this.isUsingComponentsV2() ? this.buildV2() : this.buildV1();
   }

   @Nonnull
   private MessageCreateData buildV1() {
      String content = this.content.toString().trim();
      List<MessageEmbed> embeds = new ArrayList(this.embeds);
      List<FileUpload> files = new ArrayList(this.files);
      List<MessageTopLevelComponentUnion> components = new ArrayList(this.components);
      AllowedMentionsData mentions = this.mentions.copy();
      if (content.isEmpty() && embeds.isEmpty() && files.isEmpty() && components.isEmpty() && this.poll == null) {
         throw new IllegalStateException("Cannot build an empty message. You need at least one of content, embeds, components, poll, or files");
      } else {
         int length = Helpers.codePointLength(content);
         if (length > 2000) {
            throw new IllegalStateException("Message content is too long! Max length is 2000 characters, provided " + length);
         } else if (embeds.size() > 10) {
            throw new IllegalStateException("Cannot build message with over 10 embeds, provided " + embeds.size());
         } else if (components.size() > 5) {
            throw new IllegalStateException("Cannot build message with over 5 top-level components, provided " + components.size());
         } else {
            List<? extends Component> illegalV1Components = ComponentsUtil.getIllegalV1Components(components);
            if (!illegalV1Components.isEmpty()) {
               throw new IllegalStateException("Cannot build message with components other than ActionRow while using components V1, see #useComponentsV2, provided: " + illegalV1Components);
            } else {
               return new MessageCreateData(content, embeds, files, components, mentions, this.poll, this.tts, this.messageFlags);
            }
         }
      }
   }

   @Nonnull
   private MessageCreateData buildV2() {
      List<FileUpload> files = new ArrayList(this.files);
      List<MessageTopLevelComponentUnion> components = new ArrayList(this.components);
      AllowedMentionsData mentions = this.mentions.copy();
      if (this.content.length() <= 0 && this.embeds.isEmpty() && this.poll == null) {
         if (components.isEmpty()) {
            throw new IllegalStateException("Cannot build message with no V2 components, or did you forget to disable them?");
         } else {
            long componentTreeSize = ComponentsUtil.getComponentTreeSize(components);
            if (componentTreeSize > 40L) {
               throw new IllegalStateException("Cannot build message with over 40 total components, provided " + componentTreeSize);
            } else {
               long componentTreeLength = ComponentsUtil.getComponentTreeTextContentLength(components);
               if (componentTreeLength > 4000L) {
                  throw new IllegalStateException("Cannot build message with over 4000 total characters, provided " + componentTreeLength);
               } else {
                  return new MessageCreateData("", Collections.emptyList(), files, components, mentions, this.poll, this.tts, this.messageFlags);
               }
            }
         }
      } else {
         throw new IllegalStateException("Cannot build a message with components V2 enabled while having content, embeds, or poll");
      }
   }

   @Nonnull
   public MessageCreateBuilder clear() {
      super.clear();
      this.files.clear();
      this.tts = false;
      return this;
   }

   @Nonnull
   public MessageCreateBuilder closeFiles() {
      this.files.forEach(IOUtil::silentClose);
      this.files.clear();
      return this;
   }

   private void setVoiceMessageIfApplicable(@Nonnull Collection<? extends FileUpload> files) {
      if (files.stream().anyMatch(FileUpload::isVoiceMessage)) {
         this.setVoiceMessage(true);
      }

   }
}
