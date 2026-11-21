package net.dv8tion.jda.api.utils.messages;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

class AllowedMentionsData implements SerializableData {
   private static EnumSet<Message.MentionType> defaultParse = EnumSet.allOf(Message.MentionType.class);
   private static boolean defaultMentionRepliedUser = true;
   private EnumSet<Message.MentionType> mentionParse = getDefaultMentions();
   private final Set<String> mentionUsers = new HashSet();
   private final Set<String> mentionRoles = new HashSet();
   private boolean mentionRepliedUser;

   AllowedMentionsData() {
      this.mentionRepliedUser = defaultMentionRepliedUser;
   }

   public static void setDefaultMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      defaultParse = allowedMentions == null ? EnumSet.allOf(Message.MentionType.class) : Helpers.copyEnumSet(Message.MentionType.class, allowedMentions);
   }

   @Nonnull
   public static EnumSet<Message.MentionType> getDefaultMentions() {
      return defaultParse.clone();
   }

   public static void setDefaultMentionRepliedUser(boolean mention) {
      defaultMentionRepliedUser = mention;
   }

   public static boolean isDefaultMentionRepliedUser() {
      return defaultMentionRepliedUser;
   }

   public void clear() {
      this.mentionParse = getDefaultMentions();
      this.mentionUsers.clear();
      this.mentionRoles.clear();
      this.mentionRepliedUser = defaultMentionRepliedUser;
   }

   @Nonnull
   public AllowedMentionsData copy() {
      AllowedMentionsData copy = new AllowedMentionsData();
      copy.mentionParse = this.mentionParse;
      copy.mentionUsers.addAll(this.mentionUsers);
      copy.mentionRoles.addAll(this.mentionRoles);
      copy.mentionRepliedUser = this.mentionRepliedUser;
      return copy;
   }

   public void mentionRepliedUser(boolean mention) {
      this.mentionRepliedUser = mention;
   }

   public void setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      this.mentionParse = allowedMentions == null ? EnumSet.allOf(Message.MentionType.class) : Helpers.copyEnumSet(Message.MentionType.class, allowedMentions);
   }

   public void mention(@Nonnull Collection<? extends IMentionable> mentions) {
      Checks.noneNull(mentions, "Mentionables");

      for(IMentionable mentionable : mentions) {
         if (mentionable instanceof UserSnowflake) {
            this.mentionUsers.add(mentionable.getId());
         } else if (mentionable instanceof Role) {
            this.mentionRoles.add(mentionable.getId());
         }
      }

   }

   public void mentionUsers(@Nonnull Collection<String> userIds) {
      Checks.noneNull(userIds, "User Id");
      this.mentionUsers.addAll(userIds);
   }

   public void mentionRoles(@Nonnull Collection<String> roleIds) {
      Checks.noneNull(roleIds, "Role Id");
      this.mentionRoles.addAll(roleIds);
   }

   @Nonnull
   public Set<String> getMentionedUsers() {
      return Collections.unmodifiableSet(new HashSet(this.mentionUsers));
   }

   @Nonnull
   public Set<String> getMentionedRoles() {
      return Collections.unmodifiableSet(new HashSet(this.mentionRoles));
   }

   @Nonnull
   public EnumSet<Message.MentionType> getAllowedMentions() {
      return this.mentionParse.clone();
   }

   public boolean isMentionRepliedUser() {
      return this.mentionRepliedUser;
   }

   @Nonnull
   public DataObject toData() {
      DataObject allowedMentionsObj = DataObject.empty();
      DataArray parsable = DataArray.empty();
      if (this.mentionParse != null) {
         Stream var10000 = this.mentionParse.stream().map(Message.MentionType::getParseKey).filter(Objects::nonNull).distinct();
         Objects.requireNonNull(parsable);
         var10000.forEach(parsable::add);
      }

      if (!this.mentionUsers.isEmpty()) {
         parsable.remove(Message.MentionType.USER.getParseKey());
         allowedMentionsObj.put("users", DataArray.fromCollection(this.mentionUsers));
      }

      if (!this.mentionRoles.isEmpty()) {
         parsable.remove(Message.MentionType.ROLE.getParseKey());
         allowedMentionsObj.put("roles", DataArray.fromCollection(this.mentionRoles));
      }

      allowedMentionsObj.put("replied_user", this.mentionRepliedUser);
      return allowedMentionsObj.put("parse", parsable);
   }
}
