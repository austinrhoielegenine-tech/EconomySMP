package net.dv8tion.jda.api.utils.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class AbstractMessageBuilder<T, R extends AbstractMessageBuilder<T, R>> implements MessageRequest<R> {
   protected static boolean isDefaultUseComponentsV2 = false;
   protected final List<MessageEmbed> embeds = new ArrayList(10);
   protected final List<MessageTopLevelComponentUnion> components = new ArrayList(5);
   protected final StringBuilder content = new StringBuilder(2000);
   protected AllowedMentionsData mentions = new AllowedMentionsData();
   protected int messageFlags;

   protected AbstractMessageBuilder() {
      this.useComponentsV2(isDefaultUseComponentsV2);
   }

   @Nonnull
   public R setContent(@Nullable String content) {
      if (content != null) {
         content = content.trim();
         Checks.notLonger(content, 2000, "Content");
         this.content.setLength(0);
         this.content.append(content);
      } else {
         this.content.setLength(0);
      }

      return (R)this;
   }

   @Nonnull
   public String getContent() {
      return this.content.toString();
   }

   @Nonnull
   public R mentionRepliedUser(boolean mention) {
      this.mentions.mentionRepliedUser(mention);
      return (R)this;
   }

   @Nonnull
   public R setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      this.mentions.setAllowedMentions(allowedMentions);
      return (R)this;
   }

   @Nonnull
   public R mention(@Nonnull Collection<? extends IMentionable> mentions) {
      this.mentions.mention(mentions);
      return (R)this;
   }

   @Nonnull
   public R mentionUsers(@Nonnull Collection<String> userIds) {
      this.mentions.mentionUsers(userIds);
      return (R)this;
   }

   @Nonnull
   public R mentionRoles(@Nonnull Collection<String> roleIds) {
      this.mentions.mentionRoles(roleIds);
      return (R)this;
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
   public R setEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      Checks.noneNull(embeds, "Embeds");
      Checks.check(embeds.size() <= 10, "Cannot send more than %d embeds in a message!", (int)10);
      this.embeds.clear();
      this.embeds.addAll(embeds);
      return (R)this;
   }

   @Nonnull
   public List<MessageEmbed> getEmbeds() {
      return Collections.unmodifiableList(this.embeds);
   }

   @Nonnull
   public R setComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "MessageTopLevelComponents");
      Checks.checkComponents("Provided component is invalid for messages!", components, Component::isMessageCompatible);
      List<MessageTopLevelComponentUnion> componentsAsUnions = ComponentsUtil.<MessageTopLevelComponentUnion>membersToUnion(components, MessageTopLevelComponentUnion.class);
      this.components.clear();
      this.components.addAll(componentsAsUnions);
      return (R)this;
   }

   @Nonnull
   public R useComponentsV2(boolean use) {
      int flag = Message.MessageFlag.IS_COMPONENTS_V2.getValue();
      if (use) {
         this.messageFlags |= flag;
      } else {
         this.messageFlags &= ~flag;
      }

      return (R)this;
   }

   @Nonnull
   public List<MessageTopLevelComponentUnion> getComponents() {
      return Collections.unmodifiableList(this.components);
   }

   public boolean isUsingComponentsV2() {
      return (this.messageFlags & Message.MessageFlag.IS_COMPONENTS_V2.getValue()) != 0;
   }

   @Nonnull
   public R setSuppressEmbeds(boolean suppress) {
      int flag = Message.MessageFlag.EMBEDS_SUPPRESSED.getValue();
      if (suppress) {
         this.messageFlags |= flag;
      } else {
         this.messageFlags &= ~flag;
      }

      return (R)this;
   }

   public boolean isSuppressEmbeds() {
      return (this.messageFlags & Message.MessageFlag.EMBEDS_SUPPRESSED.getValue()) != 0;
   }

   public long getMessageFlagsRaw() {
      return (long)this.messageFlags;
   }

   public abstract boolean isEmpty();

   public abstract boolean isValid();

   @Nonnull
   public abstract T build();

   @Nonnull
   public R clear() {
      this.embeds.clear();
      this.components.clear();
      this.content.setLength(0);
      this.mentions.clear();
      this.messageFlags = 0;
      return (R)this;
   }

   @Nonnull
   public abstract R closeFiles();
}
