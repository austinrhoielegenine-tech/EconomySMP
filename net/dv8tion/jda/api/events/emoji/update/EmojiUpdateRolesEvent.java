package net.dv8tion.jda.api.events.emoji.update;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

public class EmojiUpdateRolesEvent extends GenericEmojiUpdateEvent<List<Role>> {
   public static final String IDENTIFIER = "roles";

   public EmojiUpdateRolesEvent(@Nonnull JDA api, long responseNumber, @Nonnull RichCustomEmoji emoji, @Nonnull List<Role> oldRoles) {
      super(api, responseNumber, emoji, oldRoles, emoji.getRoles(), "roles");
   }

   @Nonnull
   public List<Role> getOldRoles() {
      return this.getOldValue();
   }

   @Nonnull
   public List<Role> getNewRoles() {
      return this.getNewValue();
   }

   @Nonnull
   public List<Role> getOldValue() {
      return (List)super.getOldValue();
   }

   @Nonnull
   public List<Role> getNewValue() {
      return (List)super.getNewValue();
   }
}
