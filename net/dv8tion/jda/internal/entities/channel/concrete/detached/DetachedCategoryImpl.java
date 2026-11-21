package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import gnu.trove.map.TLongObjectMap;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.concrete.CategoryManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.CategoryMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;

public class DetachedCategoryImpl extends AbstractGuildChannelImpl<DetachedCategoryImpl> implements Category, CategoryMixin<DetachedCategoryImpl>, IInteractionPermissionMixin<DetachedCategoryImpl> {
   private ChannelInteractionPermissions interactionPermissions;
   private int position;

   public DetachedCategoryImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.CATEGORY;
   }

   public int getPositionRaw() {
      return this.position;
   }

   @Nonnull
   public ChannelAction<TextChannel> createTextChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<NewsChannel> createNewsChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<StageChannel> createStageChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<ForumChannel> createForumChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<MediaChannel> createMediaChannel(@Nonnull String name) {
      throw this.detachedException();
   }

   @Nonnull
   public CategoryOrderAction modifyTextChannelPositions() {
      throw this.detachedException();
   }

   @Nonnull
   public CategoryOrderAction modifyVoiceChannelPositions() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelAction<Category> createCopy() {
      throw this.detachedException();
   }

   @Nonnull
   public CategoryManager getManager() {
      throw this.detachedException();
   }

   public TLongObjectMap<PermissionOverride> getPermissionOverrideMap() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public DetachedCategoryImpl setPosition(int position) {
      this.position = position;
      return this;
   }

   @Nonnull
   public DetachedCategoryImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
