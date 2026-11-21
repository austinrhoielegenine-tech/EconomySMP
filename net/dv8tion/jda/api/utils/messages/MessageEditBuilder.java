package net.dv8tion.jda.api.utils.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.IOUtil;

public class MessageEditBuilder extends AbstractMessageBuilder<MessageEditData, MessageEditBuilder> implements MessageEditRequest<MessageEditBuilder> {
   protected static final int CONTENT = 1;
   protected static final int EMBEDS = 2;
   protected static final int COMPONENTS = 4;
   protected static final int ATTACHMENTS = 8;
   protected static final int MENTIONS = 16;
   protected static final int FLAGS = 32;
   private boolean replace = false;
   private int configuredFields = 0;
   private final List<AttachedFile> attachments = new ArrayList(10);

   @Nonnull
   public static MessageEditBuilder from(@Nonnull MessageEditData data) {
      return (new MessageEditBuilder()).applyData(data);
   }

   @Nonnull
   public static MessageEditBuilder fromCreateData(@Nonnull MessageCreateData data) {
      return (MessageEditBuilder)(new MessageEditBuilder()).applyCreateData(data);
   }

   @Nonnull
   public static MessageEditBuilder fromMessage(@Nonnull Message message) {
      return (MessageEditBuilder)(new MessageEditBuilder()).applyMessage(message);
   }

   @Nonnull
   public MessageEditBuilder mentionRepliedUser(boolean mention) {
      super.mentionRepliedUser(mention);
      this.configuredFields |= 16;
      return this;
   }

   @Nonnull
   public MessageEditBuilder setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      super.setAllowedMentions(allowedMentions);
      this.configuredFields |= 16;
      return this;
   }

   @Nonnull
   public MessageEditBuilder mention(@Nonnull Collection<? extends IMentionable> mentions) {
      super.mention(mentions);
      this.configuredFields |= 16;
      return this;
   }

   @Nonnull
   public MessageEditBuilder mentionUsers(@Nonnull Collection<String> userIds) {
      super.mentionUsers(userIds);
      this.configuredFields |= 16;
      return this;
   }

   @Nonnull
   public MessageEditBuilder mentionRoles(@Nonnull Collection<String> roleIds) {
      super.mentionRoles(roleIds);
      this.configuredFields |= 16;
      return this;
   }

   @Nonnull
   public MessageEditBuilder setAttachments(@Nullable Collection<? extends AttachedFile> attachments) {
      this.attachments.clear();
      this.configuredFields |= 8;
      if (attachments != null) {
         this.attachments.addAll(attachments);
      }

      return this;
   }

   @Nonnull
   public List<? extends AttachedFile> getAttachments() {
      return Collections.unmodifiableList(this.attachments);
   }

   @Nonnull
   public MessageEditBuilder setReplace(boolean isReplace) {
      this.replace = isReplace;
      return this;
   }

   public boolean isReplace() {
      return this.replace;
   }

   @Nonnull
   public MessageEditBuilder setContent(@Nullable String content) {
      super.setContent(content);
      this.configuredFields |= 1;
      return this;
   }

   @Nonnull
   public MessageEditBuilder setEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      super.setEmbeds(embeds);
      this.configuredFields |= 2;
      return this;
   }

   @Nonnull
   public MessageEditBuilder setComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      super.setComponents(components);
      this.configuredFields |= 4;
      return this;
   }

   @Nonnull
   public MessageEditBuilder useComponentsV2(boolean use) {
      super.useComponentsV2(use);
      this.configuredFields |= 32;
      return this;
   }

   @Nonnull
   public MessageEditBuilder setSuppressEmbeds(boolean suppress) {
      super.setSuppressEmbeds(suppress);
      this.configuredFields |= 32;
      return this;
   }

   @Nonnull
   public MessageEditBuilder applyData(@Nonnull MessageEditData data) {
      Checks.notNull(data, "Data");
      this.configuredFields |= data.getConfiguredFields();
      this.replace |= data.isReplace();
      if (data.isSet(1)) {
         this.setContent(data.getContent());
      }

      if (data.isSet(2)) {
         this.setEmbeds(data.getEmbeds());
      }

      if (data.isSet(4)) {
         this.setComponents(data.getComponents());
      }

      if (data.isSet(8)) {
         this.setAttachments(data.getAttachments());
      }

      if (data.isSet(16)) {
         this.mentions = data.mentions.copy();
      }

      if (data.isSet(32)) {
         this.messageFlags = data.getFlags();
      }

      return this;
   }

   public boolean isEmpty() {
      return !this.replace && this.configuredFields == 0;
   }

   public boolean isValid() {
      return this.isUsingComponentsV2() ? this.isV2Valid() : this.isV1Valid();
   }

   private boolean isV1Valid() {
      if (this.isSet(1) && Helpers.codePointLength(this.content) > 2000) {
         return false;
      } else if (this.isSet(2) && this.embeds.size() > 10) {
         return false;
      } else {
         return !this.isSet(4) || this.components.size() <= 5 && !ComponentsUtil.hasIllegalV1Components(this.components);
      }
   }

   private boolean isV2Valid() {
      if (this.isSet(2) && !this.embeds.isEmpty()) {
         return false;
      } else {
         if (this.isSet(4)) {
            if (ComponentsUtil.getComponentTreeSize(this.components) > 40L) {
               return false;
            }

            if (ComponentsUtil.getComponentTreeTextContentLength(this.components) > 4000L) {
               return false;
            }
         }

         if (this.isSet(1) && this.content.length() > 0) {
            return false;
         } else {
            return !this.isReplace() || !this.components.isEmpty();
         }
      }
   }

   private boolean isSet(int flag) {
      return this.replace || (this.configuredFields & flag) != 0;
   }

   @Nonnull
   public MessageEditData build() {
      return this.isUsingComponentsV2() ? this.buildV2() : this.buildV1();
   }

   @Nonnull
   private MessageEditData buildV1() {
      String content = this.content.toString().trim();
      List<MessageEmbed> embeds = new ArrayList(this.embeds);
      List<AttachedFile> attachments = new ArrayList(this.attachments);
      List<MessageTopLevelComponentUnion> components = new ArrayList(this.components);
      AllowedMentionsData mentions = this.mentions.copy();
      int length = this.isSet(1) ? Helpers.codePointLength(content) : 0;
      if (length > 2000) {
         throw new IllegalStateException("Message content is too long! Max length is 2000 characters, provided " + length);
      } else if (this.isSet(2) && embeds.size() > 10) {
         throw new IllegalStateException("Cannot build message with over 10 embeds, provided " + embeds.size());
      } else {
         if (this.isSet(4)) {
            if (components.size() > 5) {
               throw new IllegalStateException("Cannot build message with over 5 top-level components, provided " + components.size());
            }

            List<? extends Component> illegalComponents = ComponentsUtil.getIllegalV1Components(components);
            if (!illegalComponents.isEmpty()) {
               throw new IllegalStateException("Cannot build message with components other than ActionRow while using components V1, see #useComponentsV2, provided: " + illegalComponents);
            }
         }

         return new MessageEditData(this.configuredFields, this.messageFlags, this.replace, content, embeds, attachments, components, mentions);
      }
   }

   @Nonnull
   private MessageEditData buildV2() {
      List<AttachedFile> attachments = new ArrayList(this.attachments);
      List<MessageTopLevelComponentUnion> components = new ArrayList(this.components);
      AllowedMentionsData mentions = this.mentions.copy();
      if ((!this.isSet(1) || this.content.length() <= 0) && (!this.isSet(2) || this.embeds.isEmpty())) {
         if (this.isSet(4)) {
            if (components.isEmpty()) {
               throw new IllegalStateException("Cannot build message with no V2 components, or did you forget to disable them?");
            }

            long componentTreeSize = ComponentsUtil.getComponentTreeSize(components);
            if (componentTreeSize > 40L) {
               throw new IllegalStateException("Cannot build message with over 40 total components, provided " + componentTreeSize);
            }

            long componentTreeLength = ComponentsUtil.getComponentTreeTextContentLength(components);
            if (componentTreeLength > 4000L) {
               throw new IllegalStateException("Cannot build message with over 4000 total characters, provided " + componentTreeLength);
            }
         }

         return new MessageEditData(this.configuredFields, this.messageFlags, this.replace, "", Collections.emptyList(), attachments, components, mentions);
      } else {
         throw new IllegalStateException("Cannot build a message with components V2 enabled while having content or embeds");
      }
   }

   @Nonnull
   public MessageEditBuilder clear() {
      this.configuredFields = 0;
      this.attachments.clear();
      return (MessageEditBuilder)super.clear();
   }

   @Nonnull
   public MessageEditBuilder closeFiles() {
      this.attachments.forEach(IOUtil::silentClose);
      List var10000 = this.attachments;
      Objects.requireNonNull(FileUpload.class);
      var10000.removeIf(FileUpload.class::isInstance);
      return this;
   }
}
