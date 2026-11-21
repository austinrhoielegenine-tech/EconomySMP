package net.dv8tion.jda.api.utils.messages;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.utils.Checks;

public interface MessageRequest<R extends MessageRequest<R>> extends MessageData {
   static void setDefaultMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      AllowedMentionsData.setDefaultMentions(allowedMentions);
   }

   @Nonnull
   static EnumSet<Message.MentionType> getDefaultMentions() {
      return AllowedMentionsData.getDefaultMentions();
   }

   static void setDefaultMentionRepliedUser(boolean mention) {
      AllowedMentionsData.setDefaultMentionRepliedUser(mention);
   }

   static void setDefaultUseComponentsV2(boolean use) {
      AbstractMessageBuilder.isDefaultUseComponentsV2 = use;
   }

   static boolean isDefaultUseComponentsV2() {
      return AbstractMessageBuilder.isDefaultUseComponentsV2;
   }

   static boolean isDefaultMentionRepliedUser() {
      return AllowedMentionsData.isDefaultMentionRepliedUser();
   }

   @Nonnull
   R setContent(@Nullable String var1);

   @Nonnull
   R setEmbeds(@Nonnull Collection<? extends MessageEmbed> var1);

   @Nonnull
   default R setEmbeds(@Nonnull MessageEmbed... embeds) {
      return (R)this.setEmbeds(Arrays.asList(embeds));
   }

   @Nonnull
   R setComponents(@Nonnull Collection<? extends MessageTopLevelComponent> var1);

   @Nonnull
   default R setComponents(@Nonnull MessageTopLevelComponent... components) {
      return (R)this.setComponents(Arrays.asList(components));
   }

   @Nonnull
   default R setComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return (R)this.setComponents(tree.getComponents());
   }

   @Nonnull
   R useComponentsV2(boolean var1);

   @Nonnull
   default R useComponentsV2() {
      return (R)this.useComponentsV2(true);
   }

   @Nonnull
   R setSuppressEmbeds(boolean var1);

   @Nonnull
   R setFiles(@Nullable Collection<? extends FileUpload> var1);

   @Nonnull
   default R setFiles(@Nonnull FileUpload... files) {
      Checks.noneNull(files, "Files");
      return (R)this.setFiles(Arrays.asList(files));
   }

   @Nonnull
   @CheckReturnValue
   R mentionRepliedUser(boolean var1);

   @Nonnull
   @CheckReturnValue
   R setAllowedMentions(@Nullable Collection<Message.MentionType> var1);

   @Nonnull
   @CheckReturnValue
   R mention(@Nonnull Collection<? extends IMentionable> var1);

   @Nonnull
   @CheckReturnValue
   default R mention(@Nonnull IMentionable... mentions) {
      Checks.notNull(mentions, "Mentions");
      return (R)this.mention(Arrays.asList(mentions));
   }

   @Nonnull
   @CheckReturnValue
   R mentionUsers(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   default R mentionUsers(@Nonnull String... userIds) {
      Checks.notNull(userIds, "User IDs");
      return (R)this.mentionUsers(Arrays.asList(userIds));
   }

   @Nonnull
   @CheckReturnValue
   default R mentionUsers(@Nonnull long... userIds) {
      Checks.notNull(userIds, "UserId array");
      String[] stringIds = new String[userIds.length];

      for(int i = 0; i < userIds.length; ++i) {
         stringIds[i] = Long.toUnsignedString(userIds[i]);
      }

      return (R)this.mentionUsers(stringIds);
   }

   @Nonnull
   @CheckReturnValue
   R mentionRoles(@Nonnull Collection<String> var1);

   @Nonnull
   @CheckReturnValue
   default R mentionRoles(@Nonnull String... roleIds) {
      Checks.notNull(roleIds, "Role IDs");
      return (R)this.mentionRoles(Arrays.asList(roleIds));
   }

   @Nonnull
   @CheckReturnValue
   default R mentionRoles(@Nonnull long... roleIds) {
      Checks.notNull(roleIds, "RoleId array");
      String[] stringIds = new String[roleIds.length];

      for(int i = 0; i < roleIds.length; ++i) {
         stringIds[i] = Long.toUnsignedString(roleIds[i]);
      }

      return (R)this.mentionRoles(stringIds);
   }

   @Nonnull
   default R applyMessage(@Nonnull Message message) {
      Checks.notNull(message, "Message");
      Checks.check(!message.getType().isSystem(), "Cannot copy a system message");
      List<MessageEmbed> embeds = (List)message.getEmbeds().stream().filter((e) -> e.getType() == EmbedType.RICH).collect(Collectors.toList());
      return (R)this.setContent(message.getContentRaw()).setEmbeds(embeds).setComponents(message.getComponents()).useComponentsV2(message.isUsingComponentsV2()).setSuppressEmbeds(message.isSuppressedEmbeds());
   }
}
