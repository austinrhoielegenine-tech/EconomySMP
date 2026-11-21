package net.dv8tion.jda.api.events.automod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModExecution;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.Event;

public class AutoModExecutionEvent extends Event implements AutoModExecution {
   private final AutoModExecution execution;

   public AutoModExecutionEvent(@Nonnull JDA api, long responseNumber, @Nonnull AutoModExecution execution) {
      super(api, responseNumber);
      this.execution = execution;
   }

   @Nonnull
   public Guild getGuild() {
      return this.execution.getGuild();
   }

   @Nullable
   public GuildMessageChannelUnion getChannel() {
      return this.execution.getChannel();
   }

   @Nonnull
   public AutoModResponse getResponse() {
      return this.execution.getResponse();
   }

   @Nonnull
   public AutoModTriggerType getTriggerType() {
      return this.execution.getTriggerType();
   }

   public long getUserIdLong() {
      return this.execution.getUserIdLong();
   }

   public long getRuleIdLong() {
      return this.execution.getRuleIdLong();
   }

   public long getMessageIdLong() {
      return this.execution.getMessageIdLong();
   }

   public long getAlertMessageIdLong() {
      return this.execution.getAlertMessageIdLong();
   }

   @Nonnull
   public String getContent() {
      return this.execution.getContent();
   }

   @Nullable
   public String getMatchedContent() {
      return this.execution.getMatchedContent();
   }

   @Nullable
   public String getMatchedKeyword() {
      return this.execution.getMatchedKeyword();
   }
}
