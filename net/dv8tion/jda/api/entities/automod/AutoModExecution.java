package net.dv8tion.jda.api.entities.automod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

public interface AutoModExecution {
   @Nonnull
   Guild getGuild();

   @Nullable
   GuildMessageChannelUnion getChannel();

   @Nonnull
   AutoModResponse getResponse();

   @Nonnull
   AutoModTriggerType getTriggerType();

   long getUserIdLong();

   @Nonnull
   default String getUserId() {
      return Long.toUnsignedString(this.getUserIdLong());
   }

   long getRuleIdLong();

   @Nonnull
   default String getRuleId() {
      return Long.toUnsignedString(this.getRuleIdLong());
   }

   long getMessageIdLong();

   @Nullable
   default String getMessageId() {
      long id = this.getMessageIdLong();
      return id == 0L ? null : Long.toUnsignedString(this.getMessageIdLong());
   }

   long getAlertMessageIdLong();

   @Nullable
   default String getAlertMessageId() {
      long id = this.getAlertMessageIdLong();
      return id == 0L ? null : Long.toUnsignedString(this.getAlertMessageIdLong());
   }

   @Nonnull
   String getContent();

   @Nullable
   String getMatchedContent();

   @Nullable
   String getMatchedKeyword();
}
