package net.dv8tion.jda.api.utils.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.message.MessageUtil;

public class MessageCreateData implements MessageData, AutoCloseable, SerializableData {
   private final String content;
   private final List<MessageEmbed> embeds;
   private final List<FileUpload> files;
   private final Set<FileUpload> allDistinctFiles;
   private final List<MessageTopLevelComponentUnion> components;
   private final AllowedMentionsData mentions;
   private final MessagePollData poll;
   private final boolean tts;
   private final int flags;

   protected MessageCreateData(String content, List<MessageEmbed> embeds, List<FileUpload> files, List<MessageTopLevelComponentUnion> components, AllowedMentionsData mentions, MessagePollData poll, boolean tts, int flags) {
      this.content = content;
      this.embeds = Collections.unmodifiableList(embeds);
      this.files = Collections.unmodifiableList(files);
      this.allDistinctFiles = createAllDistinctFiles(files, components);
      this.components = Collections.unmodifiableList(components);
      this.mentions = mentions;
      this.poll = poll;
      this.tts = tts;
      this.flags = flags;
   }

   @Nonnull
   public static MessageCreateData fromContent(@Nonnull String content) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).setContent(content)).build();
   }

   @Nonnull
   public static MessageCreateData fromEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).setEmbeds(embeds)).build();
   }

   @Nonnull
   public static MessageCreateData fromEmbeds(@Nonnull MessageEmbed... embeds) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).setEmbeds(embeds)).build();
   }

   @Nonnull
   public static MessageCreateData fromFiles(@Nonnull Collection<? extends FileUpload> files) {
      return (new MessageCreateBuilder()).setFiles(files).build();
   }

   @Nonnull
   public static MessageCreateData fromFiles(@Nonnull FileUpload... files) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).setFiles(files)).build();
   }

   @Nonnull
   public static MessageCreateData fromMessage(@Nonnull Message message) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).applyMessage(message)).build();
   }

   @Nonnull
   public static MessageCreateData fromEditData(@Nonnull MessageEditData data) {
      return ((MessageCreateBuilder)(new MessageCreateBuilder()).applyEditData(data)).build();
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
      return (this.flags & Message.MessageFlag.IS_COMPONENTS_V2.getValue()) != 0;
   }

   @Nonnull
   public List<? extends FileUpload> getAttachments() {
      return this.getFiles();
   }

   @Nullable
   public MessagePollData getPoll() {
      return this.poll;
   }

   public boolean isSuppressEmbeds() {
      return (this.flags & Message.MessageFlag.EMBEDS_SUPPRESSED.getValue()) != 0;
   }

   public boolean isTTS() {
      return this.tts;
   }

   public boolean isSuppressedNotifications() {
      return (this.flags & Message.MessageFlag.NOTIFICATIONS_SUPPRESSED.getValue()) != 0;
   }

   public boolean isVoiceMessage() {
      return (this.flags & Message.MessageFlag.IS_VOICE_MESSAGE.getValue()) != 0;
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
   public DataObject toData() {
      DataObject json = DataObject.empty();
      if (!this.isUsingComponentsV2()) {
         json.put("content", this.content);
         json.put("poll", this.poll);
         json.put("embeds", DataArray.fromCollection(this.embeds));
      }

      json.put("components", DataArray.fromCollection(this.components));
      json.put("tts", this.tts);
      json.put("flags", this.flags);
      json.put("allowed_mentions", this.mentions);
      if (this.files != null && !this.allDistinctFiles.isEmpty()) {
         json.put("attachments", MessageUtil.getAttachmentsData(this.getAllDistinctFiles()));
      }

      return json;
   }

   @Nonnull
   public List<FileUpload> getFiles() {
      return this.files;
   }

   @Nonnull
   public Set<? extends FileUpload> getAllDistinctFiles() {
      return this.allDistinctFiles;
   }

   @Nonnull
   private static Set<FileUpload> createAllDistinctFiles(@Nonnull Collection<FileUpload> files, @Nonnull Collection<MessageTopLevelComponentUnion> components) {
      List<FileUpload> indirectFiles = MessageUtil.getIndirectFiles(components);
      Set<FileUpload> distinctFiles = new LinkedHashSet(files.size() + indirectFiles.size());
      distinctFiles.addAll(files);
      distinctFiles.addAll(indirectFiles);
      return Collections.unmodifiableSet(distinctFiles);
   }

   public void close() {
      this.files.forEach(IOUtil::silentClose);
   }
}
