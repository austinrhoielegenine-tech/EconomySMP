package net.dv8tion.jda.internal.entities.channel.middleman;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.StandardGuildMessageChannelMixin;

public abstract class AbstractStandardGuildMessageChannelImpl<T extends AbstractStandardGuildMessageChannelImpl<T>> extends AbstractStandardGuildChannelImpl<T> implements StandardGuildMessageChannelMixin<T> {
   protected String topic;
   protected boolean nsfw;
   protected long latestMessageId;
   protected int defaultThreadSlowmode;

   public AbstractStandardGuildMessageChannelImpl(long id, Guild guild) {
      super(id, guild);
   }

   @Nullable
   public String getTopic() {
      return this.topic;
   }

   public boolean isNSFW() {
      return this.nsfw;
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   public int getDefaultThreadSlowmode() {
      return this.defaultThreadSlowmode;
   }

   public T setTopic(String topic) {
      this.topic = topic;
      return (T)this;
   }

   public T setNSFW(boolean nsfw) {
      this.nsfw = nsfw;
      return (T)this;
   }

   public T setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return (T)this;
   }

   public T setDefaultThreadSlowmode(int defaultThreadSlowmode) {
      this.defaultThreadSlowmode = defaultThreadSlowmode;
      return (T)this;
   }
}
