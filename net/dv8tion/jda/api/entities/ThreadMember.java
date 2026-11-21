package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public interface ThreadMember extends IMentionable {
   @Nonnull
   JDA getJDA();

   @Nonnull
   Guild getGuild();

   @Nonnull
   ThreadChannel getThread();

   @Nonnull
   User getUser();

   @Nonnull
   Member getMember();

   @Nonnull
   OffsetDateTime getTimeJoined();

   default boolean isThreadOwner() {
      return this.getThread().getOwnerIdLong() == this.getIdLong();
   }
}
