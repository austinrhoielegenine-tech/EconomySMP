package net.dv8tion.jda.internal.entities.channel.concrete;

import gnu.trove.map.TLongObjectMap;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
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
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.MediaChannelMixin;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.managers.channel.concrete.MediaChannelManagerImpl;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public class MediaChannelImpl extends AbstractGuildChannelImpl<MediaChannelImpl> implements MediaChannel, GuildChannelUnion, MediaChannelMixin<MediaChannelImpl> {
   private final TLongObjectMap<PermissionOverride> overrides = MiscUtil.<PermissionOverride>newLongMap();
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

   public MediaChannelImpl(long id, GuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public GuildImpl getGuild() {
      return (GuildImpl)super.getGuild();
   }

   @Nonnull
   public MediaChannelManager getManager() {
      return new MediaChannelManagerImpl(this);
   }

   @Nonnull
   public List<Member> getMembers() {
      return (List)this.getGuild().getMembers().stream().filter((m) -> m.hasPermission(this, new Permission[]{Permission.VIEW_CHANNEL})).collect(Helpers.toUnmodifiableList());
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
      return this.overrides;
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

   public MediaChannelImpl setParentCategory(long parentCategoryId) {
      this.parentCategoryId = parentCategoryId;
      return this;
   }

   public MediaChannelImpl setPosition(int position) {
      this.position = position;
      return this;
   }

   public MediaChannelImpl setDefaultThreadSlowmode(int defaultThreadSlowmode) {
      this.defaultThreadSlowmode = defaultThreadSlowmode;
      return this;
   }

   public MediaChannelImpl setNSFW(boolean nsfw) {
      this.nsfw = nsfw;
      return this;
   }

   public MediaChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public MediaChannelImpl setTopic(String topic) {
      this.topic = topic;
      return this;
   }

   public MediaChannelImpl setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public MediaChannelImpl setDefaultReaction(DataObject emoji) {
      if (emoji != null && !emoji.isNull("emoji_id")) {
         this.defaultReaction = new CustomEmojiImpl("", emoji.getUnsignedLong("emoji_id"), false);
      } else if (emoji != null && !emoji.isNull("emoji_name")) {
         this.defaultReaction = Emoji.fromUnicode(emoji.getString("emoji_name"));
      } else {
         this.defaultReaction = null;
      }

      return this;
   }

   public MediaChannelImpl setDefaultSortOrder(int defaultSortOrder) {
      this.defaultSortOrder = defaultSortOrder;
      return this;
   }
}
