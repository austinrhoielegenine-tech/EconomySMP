package net.dv8tion.jda.api.utils.messages;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachedFile;

public interface MessageData {
   @Nonnull
   String getContent();

   @Nonnull
   List<MessageEmbed> getEmbeds();

   @Nonnull
   List<MessageTopLevelComponentUnion> getComponents();

   @Nonnull
   default MessageComponentTree getComponentTree() {
      return MessageComponentTree.of(this.getComponents());
   }

   boolean isUsingComponentsV2();

   @Nonnull
   List<? extends AttachedFile> getAttachments();

   boolean isSuppressEmbeds();

   @Nonnull
   Set<String> getMentionedUsers();

   @Nonnull
   Set<String> getMentionedRoles();

   @Nonnull
   EnumSet<Message.MentionType> getAllowedMentions();

   boolean isMentionRepliedUser();
}
