package net.dv8tion.jda.api.audit;

import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.EntityString;

public enum AuditLogOption {
   COUNT("count"),
   MESSAGE("message_id"),
   CHANNEL("channel_id"),
   USER("user_id"),
   ROLE("role_id"),
   ROLE_NAME("role_name"),
   TYPE("type"),
   ID("id"),
   DELETE_MEMBER_DAYS("delete_member_days"),
   MEMBERS_REMOVED("members_removed");

   private final String key;

   private AuditLogOption(String key) {
      this.key = key;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   public String toString() {
      return (new EntityString(this)).setType(this).addMetadata("key", this.key).toString();
   }

   // $FF: synthetic method
   private static AuditLogOption[] $values() {
      return new AuditLogOption[]{COUNT, MESSAGE, CHANNEL, USER, ROLE, ROLE_NAME, TYPE, ID, DELETE_MEMBER_DAYS, MEMBERS_REMOVED};
   }
}
