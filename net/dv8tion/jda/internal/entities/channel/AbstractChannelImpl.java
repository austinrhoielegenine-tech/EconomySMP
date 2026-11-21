package net.dv8tion.jda.internal.entities.channel;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.ChannelMixin;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import net.dv8tion.jda.internal.utils.EntityString;

public abstract class AbstractChannelImpl<T extends AbstractChannelImpl<T>> implements ChannelMixin<T> {
   protected final long id;
   protected final JDAImpl api;
   protected String name;

   public AbstractChannelImpl(long id, JDA api) {
      this.id = id;
      this.api = (JDAImpl)api;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public T setName(String name) {
      this.name = name;
      return (T)this;
   }

   @Nonnull
   public PrivateChannel asPrivateChannel() {
      return (PrivateChannel)ChannelUtil.safeChannelCast(this, PrivateChannel.class);
   }

   @Nonnull
   public GroupChannel asGroupChannel() {
      return (GroupChannel)ChannelUtil.safeChannelCast(this, GroupChannel.class);
   }

   @Nonnull
   public TextChannel asTextChannel() {
      return (TextChannel)ChannelUtil.safeChannelCast(this, TextChannel.class);
   }

   @Nonnull
   public NewsChannel asNewsChannel() {
      return (NewsChannel)ChannelUtil.safeChannelCast(this, NewsChannel.class);
   }

   @Nonnull
   public VoiceChannel asVoiceChannel() {
      return (VoiceChannel)ChannelUtil.safeChannelCast(this, VoiceChannel.class);
   }

   @Nonnull
   public StageChannel asStageChannel() {
      return (StageChannel)ChannelUtil.safeChannelCast(this, StageChannel.class);
   }

   @Nonnull
   public ThreadChannel asThreadChannel() {
      return (ThreadChannel)ChannelUtil.safeChannelCast(this, ThreadChannel.class);
   }

   @Nonnull
   public Category asCategory() {
      return (Category)ChannelUtil.safeChannelCast(this, Category.class);
   }

   @Nonnull
   public ForumChannel asForumChannel() {
      return (ForumChannel)ChannelUtil.safeChannelCast(this, ForumChannel.class);
   }

   @Nonnull
   public MediaChannel asMediaChannel() {
      return (MediaChannel)ChannelUtil.safeChannelCast(this, MediaChannel.class);
   }

   @Nonnull
   public MessageChannel asMessageChannel() {
      return (MessageChannel)ChannelUtil.safeChannelCast(this, MessageChannel.class);
   }

   @Nonnull
   public AudioChannel asAudioChannel() {
      return (AudioChannel)ChannelUtil.safeChannelCast(this, AudioChannel.class);
   }

   @Nonnull
   public IThreadContainer asThreadContainer() {
      return (IThreadContainer)ChannelUtil.safeChannelCast(this, IThreadContainer.class);
   }

   @Nonnull
   public GuildChannel asGuildChannel() {
      return (GuildChannel)ChannelUtil.safeChannelCast(this, GuildChannel.class);
   }

   @Nonnull
   public GuildMessageChannel asGuildMessageChannel() {
      return (GuildMessageChannel)ChannelUtil.safeChannelCast(this, GuildMessageChannel.class);
   }

   @Nonnull
   public StandardGuildChannel asStandardGuildChannel() {
      return (StandardGuildChannel)ChannelUtil.safeChannelCast(this, StandardGuildChannel.class);
   }

   @Nonnull
   public StandardGuildMessageChannel asStandardGuildMessageChannel() {
      return (StandardGuildMessageChannel)ChannelUtil.safeChannelCast(this, StandardGuildMessageChannel.class);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }
}
