package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import gnu.trove.map.TLongObjectMap;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.managers.channel.concrete.MediaChannelManager;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.MediaChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public class DetachedMediaChannelImpl extends AbstractGuildChannelImpl<DetachedMediaChannelImpl> implements MediaChannel, GuildChannelUnion, MediaChannelMixin<DetachedMediaChannelImpl>, IInteractionPermissionMixin<DetachedMediaChannelImpl> {
   private ChannelInteractionPermissions interactionPermissions;
   private final SortedSnowflakeCacheViewImpl<ForumTag> tagCache = new SortedSnowflakeCacheViewImpl<ForumTag>(ForumTag.class, BaseForumTag::getName, Comparator.naturalOrder());
   private Emoji defaultReaction;
   private String topic;
   private long parentCategoryId;
   private boolean nsfw = false;
   private int position;
   private int flags;
   private int slowmode;
   private int defaultSortOrder;
   protected int defaultThreadSlowmode;

   public DetachedMediaChannelImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public MediaChannelManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   @Nonnull
   public EnumSet<ChannelFlag> getFlags() {
      return ChannelFlag.fromRaw(this.flags);
   }

   @Nonnull
   public SortedSnowflakeCacheViewImpl<ForumTag> getAvailableTagCache() {
      return this.tagCache;
   }

   public TLongObjectMap<PermissionOverride> getPermissionOverrideMap() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public boolean isNSFW() {
      return this.nsfw;
   }

   public int getPositionRaw() {
      return this.position;
   }

   public long getParentCategoryIdLong() {
      return this.parentCategoryId;
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   public String getTopic() {
      return this.topic;
   }

   public EmojiUnion getDefaultReaction() {
      return (EmojiUnion)this.defaultReaction;
   }

   public int getDefaultThreadSlowmode() {
      return this.defaultThreadSlowmode;
   }

   @Nonnull
   public IPostContainer.SortOrder getDefaultSortOrder() {
      return IPostContainer.SortOrder.fromKey(this.defaultSortOrder);
   }

   public int getRawFlags() {
      return this.flags;
   }

   public int getRawSortOrder() {
      return this.defaultSortOrder;
   }

   public DetachedMediaChannelImpl setParentCategory(long parentCategoryId) {
      this.parentCategoryId = parentCategoryId;
      return this;
   }

   public DetachedMediaChannelImpl setPosition(int position) {
      this.position = position;
      return this;
   }

   public DetachedMediaChannelImpl setDefaultThreadSlowmode(int defaultThreadSlowmode) {
      this.defaultThreadSlowmode = defaultThreadSlowmode;
      return this;
   }

   public DetachedMediaChannelImpl setNSFW(boolean nsfw) {
      this.nsfw = nsfw;
      return this;
   }

   public DetachedMediaChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public DetachedMediaChannelImpl setTopic(String topic) {
      this.topic = topic;
      return this;
   }

   public DetachedMediaChannelImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public DetachedMediaChannelImpl setDefaultReaction(DataObject emoji) {
      if (emoji != null && !emoji.isNull("emoji_id")) {
         this.defaultReaction = new CustomEmojiImpl("", emoji.getUnsignedLong("emoji_id"), false);
      } else if (emoji != null && !emoji.isNull("emoji_name")) {
         this.defaultReaction = Emoji.fromUnicode(emoji.getString("emoji_name"));
      } else {
         this.defaultReaction = null;
      }

      return this;
   }

   public DetachedMediaChannelImpl setDefaultSortOrder(int defaultSortOrder) {
      this.defaultSortOrder = defaultSortOrder;
      return this;
   }

   @Nonnull
   public DetachedMediaChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
