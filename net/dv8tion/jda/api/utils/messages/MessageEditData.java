package net.dv8tion.jda.api.utils.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.message.MessageUtil;

public class MessageEditData implements MessageData, AutoCloseable, SerializableData {
   protected final AllowedMentionsData mentions;
   private final String content;
   private final List<MessageEmbed> embeds;
   private final List<AttachedFile> files;
   private final Set<AttachedFile> allDistinctFiles;
   private final List<MessageTopLevelComponentUnion> components;
   private final int messageFlags;
   private final boolean isReplace;
   private final int configuredFields;

   protected MessageEditData(int configuredFields, int messageFlags, boolean isReplace, String content, List<MessageEmbed> embeds, List<AttachedFile> files, List<MessageTopLevelComponentUnion> components, AllowedMentionsData mentions) {
      this.content = content;
      this.embeds = Collections.unmodifiableList(embeds);
      this.files = Collections.unmodifiableList(files);
      this.allDistinctFiles = createAllDistinctFiles(files, components);
      this.components = Collections.unmodifiableList(components);
      this.mentions = mentions;
      this.messageFlags = messageFlags;
      this.isReplace = isReplace;
      this.configuredFields = configuredFields;
   }

   @Nonnull
   public static MessageEditData fromContent(@Nonnull String content) {
      return (new MessageEditBuilder()).setContent(content).build();
   }

   @Nonnull
   public static MessageEditData fromEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return (new MessageEditBuilder()).setEmbeds(embeds).build();
   }

   @Nonnull
   public static MessageEditData fromEmbeds(@Nonnull MessageEmbed... embeds) {
      return ((MessageEditBuilder)(new MessageEditBuilder()).setEmbeds(embeds)).build();
   }

   @Nonnull
   public static MessageEditData fromFiles(@Nonnull Collection<? extends FileUpload> files) {
      return ((MessageEditBuilder)(new MessageEditBuilder()).setFiles(files)).build();
   }

   @Nonnull
   public static MessageEditData fromFiles(@Nonnull FileUpload... files) {
      return ((MessageEditBuilder)(new MessageEditBuilder()).setFiles(files)).build();
   }

   @Nonnull
   public static MessageEditData fromMessage(@Nonnull Message message) {
      return ((MessageEditBuilder)(new MessageEditBuilder()).applyMessage(message)).build();
   }

   @Nonnull
   public static MessageEditData fromCreateData(@Nonnull MessageCreateData data) {
      return ((MessageEditBuilder)(new MessageEditBuilder()).applyCreateData(data)).build();
   }

   protected boolean isReplace() {
      return this.isReplace;
   }

   protected int getConfiguredFields() {
      return this.configuredFields;
   }

   protected int getFlags() {
      return this.messageFlags;
   }

   @Nonnull
   public String getContent() {
      return this.content;
   }

   @Nonnull
   public List<MessageEmbed> getEmbeds() {
      return this.embeds;
   }

   @Nonnull
   public List<MessageTopLevelComponentUnion> getComponents() {
      return this.components;
   }

   public boolean isUsingComponentsV2() {
      return (this.messageFlags & Message.MessageFlag.IS_COMPONENTS_V2.getValue()) != 0;
   }

   @Nonnull
   public List<AttachedFile> getAttachments() {
      return this.files;
   }

   public boolean isSuppressEmbeds() {
      return this.isSet(Message.MessageFlag.EMBEDS_SUPPRESSED.getValue());
   }

   @Nonnull
   public Set<String> getMentionedUsers() {
      return this.mentions.getMentionedUsers();
   }

   @Nonnull
   public Set<String> getMentionedRoles() {
      return this.mentions.getMentionedRoles();
   }

   @Nonnull
   public EnumSet<Message.MentionType> getAllowedMentions() {
      return this.mentions.getAllowedMentions();
   }

   public boolean isMentionRepliedUser() {
      return this.mentions.isMentionRepliedUser();
   }

   @Nonnull
   public synchronized DataObject toData() {
      DataObject json = DataObject.empty();
      if (this.isSet(1)) {
         json.put("content", this.content);
      }

      if (this.isSet(2)) {
         json.put("embeds", DataArray.fromCollection(this.embeds));
      }

      if (this.isSet(4)) {
         json.put("components", DataArray.fromCollection(this.components));
      }

      if (this.isSet(16)) {
         json.put("allowed_mentions", this.mentions);
      }

      if (this.isSet(32)) {
         json.put("flags", this.messageFlags);
      }

      List<FileUpload> additionalFiles = MessageUtil.getIndirectFiles(this.components);
      if (this.isSet(8) || !additionalFiles.isEmpty()) {
         json.put("attachments", MessageUtil.getAttachmentsData(this.allDistinctFiles));
      }

      return json;
   }

   @Nonnull
   public synchronized List<FileUpload> getFiles() {
      Stream var10000 = this.files.stream();
      Objects.requireNonNull(FileUpload.class);
      var10000 = var10000.filter(FileUpload.class::isInstance);
      Objects.requireNonNull(FileUpload.class);
      return (List)var10000.map(FileUpload.class::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public Set<? extends AttachedFile> getAllDistinctFiles() {
      return this.allDistinctFiles;
   }

   @Nonnull
   public static Set<AttachedFile> createAllDistinctFiles(@Nullable Collection<AttachedFile> files, @Nonnull Collection<MessageTopLevelComponentUnion> components) {
      List<FileUpload> indirectFiles = MessageUtil.getIndirectFiles(components);
      Set<AttachedFile> distinctFiles = new LinkedHashSet((files == null ? 0 : files.size()) + indirectFiles.size());
      if (files != null) {
         distinctFiles.addAll(files);
      }

      distinctFiles.addAll(indirectFiles);
      return Collections.unmodifiableSet(distinctFiles);
   }

   public synchronized void close() {
      this.files.forEach(IOUtil::silentClose);
   }

   protected boolean isSet(int flag) {
      return this.isReplace || (this.configuredFields & flag) != 0;
   }
}
