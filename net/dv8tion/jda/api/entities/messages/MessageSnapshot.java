package net.dv8tion.jda.api.entities.messages;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import org.jetbrains.annotations.Unmodifiable;

public class MessageSnapshot {
   private final Object mutex = new Object();
   private final MessageType type;
   private final Mentions mentions;
   private final OffsetDateTime editTime;
   private final String content;
   private final List<Message.Attachment> attachments;
   private final List<MessageEmbed> embeds;
   private final List<MessageTopLevelComponentUnion> components;
   private final List<StickerItem> stickers;
   private final long flags;
   private List<String> invites;

   public MessageSnapshot(MessageType type, Mentions mentions, OffsetDateTime editTime, String content, List<Message.Attachment> attachments, List<MessageEmbed> embeds, List<MessageTopLevelComponentUnion> components, List<StickerItem> stickers, long flags) {
      this.type = type;
      this.mentions = mentions;
      this.editTime = editTime;
      this.content = content;
      this.attachments = Collections.unmodifiableList(attachments);
      this.embeds = Collections.unmodifiableList(embeds);
      this.components = Collections.unmodifiableList(components);
      this.stickers = Collections.unmodifiableList(stickers);
      this.flags = flags;
   }

   @Nonnull
   public MessageType getType() {
      return this.type;
   }

   @Nonnull
   public Mentions getMentions() {
      return this.mentions;
   }

   public boolean isEdited() {
      return this.editTime != null;
   }

   @Nullable
   public OffsetDateTime getTimeEdited() {
      return this.editTime;
   }

   @Nonnull
   public String getContentRaw() {
      return this.content;
   }

   @Nonnull
   public @Unmodifiable List<String> getInvites() {
      if (this.invites != null) {
         return this.invites;
      } else {
         synchronized(this.mutex) {
            if (this.invites != null) {
               return this.invites;
            } else {
               this.invites = new ArrayList();
               Matcher m = Message.INVITE_PATTERN.matcher(this.getContentRaw());

               while(m.find()) {
                  this.invites.add(m.group(1));
               }

               return this.invites = Collections.unmodifiableList(this.invites);
            }
         }
      }
   }

   @Nonnull
   public @Unmodifiable List<Message.Attachment> getAttachments() {
      return this.attachments;
   }

   @Nonnull
   public @Unmodifiable List<MessageEmbed> getEmbeds() {
      return this.embeds;
   }

   @Nonnull
   public @Unmodifiable List<MessageTopLevelComponentUnion> getComponents() {
      return this.components;
   }

   @Nonnull
   public MessageComponentTree getComponentTree() {
      return MessageComponentTree.of(this.components);
   }

   @Nonnull
   public @Unmodifiable List<StickerItem> getStickers() {
      return this.stickers;
   }

   public long getFlagsRaw() {
      return this.flags;
   }

   @Nonnull
   public EnumSet<Message.MessageFlag> getFlags() {
      return Message.MessageFlag.fromBitField((int)this.getFlagsRaw());
   }
}
