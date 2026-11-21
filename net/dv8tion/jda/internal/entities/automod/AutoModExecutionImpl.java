package net.dv8tion.jda.internal.entities.automod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModExecution;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.utils.data.DataObject;

public class AutoModExecutionImpl implements AutoModExecution {
   private final Guild guild;
   private final GuildMessageChannel channel;
   private final AutoModResponse response;
   private final AutoModTriggerType type;
   private final long userId;
   private final long ruleId;
   private final long messageId;
   private final long alertMessageId;
   private final String content;
   private final String matchedContent;
   private final String matchedKeyword;

   public AutoModExecutionImpl(Guild guild, DataObject json) {
      this.guild = guild;
      this.channel = (GuildMessageChannel)guild.getChannelById(GuildMessageChannel.class, json.getUnsignedLong("channel_id", 0L));
      this.response = new AutoModResponseImpl(guild, json.getObject("action"));
      this.type = AutoModTriggerType.fromKey(json.getInt("rule_trigger_type", -1));
      this.userId = json.getUnsignedLong("user_id");
      this.ruleId = json.getUnsignedLong("rule_id");
      this.messageId = json.getUnsignedLong("message_id", 0L);
      this.alertMessageId = json.getUnsignedLong("alert_system_message_id", 0L);
      this.content = json.getString("content", "");
      this.matchedContent = json.getString("matched_content", (String)null);
      this.matchedKeyword = json.getString("matched_keyword", (String)null);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nullable
   public GuildMessageChannelUnion getChannel() {
      return (GuildMessageChannelUnion)this.channel;
   }

   @Nonnull
   public AutoModResponse getResponse() {
      return this.response;
   }

   @Nonnull
   public AutoModTriggerType getTriggerType() {
      return this.type;
   }

   public long getUserIdLong() {
      return this.userId;
   }

   public long getRuleIdLong() {
      return this.ruleId;
   }

   public long getMessageIdLong() {
      return this.messageId;
   }

   public long getAlertMessageIdLong() {
      return this.alertMessageId;
   }

   @Nonnull
   public String getContent() {
      return this.content;
   }

   @Nullable
   public String getMatchedContent() {
      return this.matchedContent;
   }

   @Nullable
   public String getMatchedKeyword() {
      return this.matchedKeyword;
   }
}
