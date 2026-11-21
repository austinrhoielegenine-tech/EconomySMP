package net.dv8tion.jda.api.entities.channel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.internal.utils.EntityString;

public enum ChannelField {
   TYPE("type", AuditLogKey.CHANNEL_TYPE),
   NAME("name", AuditLogKey.CHANNEL_NAME),
   FLAGS("flags", AuditLogKey.CHANNEL_FLAGS),
   PARENT("parent", AuditLogKey.CHANNEL_PARENT),
   POSITION("position", (AuditLogKey)null),
   DEFAULT_THREAD_SLOWMODE("default_thread_slowmode", AuditLogKey.CHANNEL_DEFAULT_THREAD_SLOWMODE),
   DEFAULT_REACTION_EMOJI("default_reaction_emoji", AuditLogKey.CHANNEL_DEFAULT_REACTION_EMOJI),
   TOPIC("topic", AuditLogKey.CHANNEL_TOPIC),
   NSFW("nsfw", AuditLogKey.CHANNEL_NSFW),
   SLOWMODE("slowmode", AuditLogKey.CHANNEL_SLOWMODE),
   AVAILABLE_TAGS("available_tags", AuditLogKey.CHANNEL_AVAILABLE_TAGS),
   BITRATE("bitrate", AuditLogKey.CHANNEL_BITRATE),
   REGION("region", (AuditLogKey)null),
   USER_LIMIT("userlimit", AuditLogKey.CHANNEL_USER_LIMIT),
   VOICE_STATUS("status", AuditLogKey.CHANNEL_VOICE_STATUS),
   AUTO_ARCHIVE_DURATION("autoArchiveDuration", AuditLogKey.THREAD_AUTO_ARCHIVE_DURATION),
   ARCHIVED("archived", AuditLogKey.THREAD_ARCHIVED),
   ARCHIVED_TIMESTAMP("archiveTimestamp", (AuditLogKey)null),
   LOCKED("locked", AuditLogKey.THREAD_LOCKED),
   INVITABLE("invitable", AuditLogKey.THREAD_INVITABLE),
   APPLIED_TAGS("applied_tags", AuditLogKey.THREAD_APPLIED_TAGS),
   DEFAULT_FORUM_LAYOUT("default_forum_layout", AuditLogKey.DEFAULT_FORUM_LAYOUT),
   DEFAULT_SORT_ORDER("default_sort_order", AuditLogKey.CHANNEL_DEFAULT_SORT_ORDER);

   private final String fieldName;
   private final AuditLogKey auditLogKey;

   private ChannelField(String fieldName, AuditLogKey auditLogKey) {
      this.fieldName = fieldName;
      this.auditLogKey = auditLogKey;
   }

   @Nonnull
   public String getFieldName() {
      return this.fieldName;
   }

   @Nullable
   public AuditLogKey getAuditLogKey() {
      return this.auditLogKey;
   }

   @Nonnull
   public String toString() {
      return (new EntityString(this)).setType(this).addMetadata("fieldName", this.fieldName).toString();
   }

   // $FF: synthetic method
   private static ChannelField[] $values() {
      return new ChannelField[]{TYPE, NAME, FLAGS, PARENT, POSITION, DEFAULT_THREAD_SLOWMODE, DEFAULT_REACTION_EMOJI, TOPIC, NSFW, SLOWMODE, AVAILABLE_TAGS, BITRATE, REGION, USER_LIMIT, VOICE_STATUS, AUTO_ARCHIVE_DURATION, ARCHIVED, ARCHIVED_TIMESTAMP, LOCKED, INVITABLE, APPLIED_TAGS, DEFAULT_FORUM_LAYOUT, DEFAULT_SORT_ORDER};
   }
}
