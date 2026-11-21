package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultLayoutEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultReactionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultSortOrderEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultThreadSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateFlagsEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateRegionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTypeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.ForumTagImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.PermissionOverrideImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.ForumChannelImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IAgeRestrictedChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ICategorizableChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPositionableChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPostContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IThreadContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ITopicChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.AudioChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.MessageChannelMixin;
import net.dv8tion.jda.internal.requests.WebSocketClient;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.ChannelCacheViewImpl;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public class ChannelUpdateHandler extends SocketHandler {
   public ChannelUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      ChannelType type = ChannelType.fromId(content.getInt("type"));
      if (type == ChannelType.GROUP) {
         WebSocketClient.LOG.warn("Ignoring CHANNEL_UPDATE for a group which we don't support");
         return null;
      } else {
         if (!content.isNull("guild_id")) {
            long guildId = content.getUnsignedLong("guild_id");
            if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
               return guildId;
            }
         }

         long channelId = content.getUnsignedLong("id");
         AbstractGuildChannelImpl<?> channel = (AbstractGuildChannelImpl)this.getJDA().getGuildChannelById(channelId);
         if (channel == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"CHANNEL_UPDATE attempted to update a channel that does not exist. JSON: {}", (Object)content);
            return null;
         } else {
            channel = this.handleChannelTypeChange(channel, content, type);
            if (channel == null) {
               return null;
            } else {
               String oldName = channel.getName();
               String name = content.getString("name", oldName);
               if (!Objects.equals(oldName, name)) {
                  channel.setName(name);
                  this.getJDA().handleEvent(new ChannelUpdateNameEvent(this.getJDA(), this.responseNumber, channel, oldName, name));
               }

               if (channel instanceof ITopicChannelMixin) {
                  this.handleTopic((ITopicChannelMixin)channel, content.getString("topic", (String)null));
               }

               if (channel instanceof ISlowmodeChannelMixin) {
                  this.handleSlowmode((ISlowmodeChannelMixin)channel, content.getInt("rate_limit_per_user", 0));
               }

               if (channel instanceof IAgeRestrictedChannelMixin) {
                  this.handleNsfw((IAgeRestrictedChannelMixin)channel, content.getBoolean("nsfw"));
               }

               if (channel instanceof ICategorizableChannelMixin) {
                  this.handleParentCategory((ICategorizableChannelMixin)channel, content.getUnsignedLong("parent_id", 0L));
               }

               if (channel instanceof IPositionableChannelMixin) {
                  this.handlePosition((IPositionableChannelMixin)channel, content.getInt("position", 0));
               }

               if (channel instanceof IThreadContainerMixin) {
                  this.handleThreadContainer((IThreadContainerMixin)channel, content);
               }

               if (channel instanceof AudioChannelMixin) {
                  this.handleAudioChannel((AudioChannelMixin)channel, content);
               }

               if (channel instanceof IPostContainerMixin) {
                  this.handlePostContainer((IPostContainerMixin)channel, content);
               }

               switch (type) {
                  case FORUM:
                     ForumChannelImpl forumChannel = (ForumChannelImpl)channel;
                     int layout = content.getInt("default_forum_layout", ((ForumChannelImpl)channel).getRawLayout());
                     int oldLayout = forumChannel.getRawLayout();
                     if (oldLayout != layout) {
                        forumChannel.setDefaultLayout(layout);
                        this.getJDA().handleEvent(new ChannelUpdateDefaultLayoutEvent(this.getJDA(), this.responseNumber, forumChannel, ForumChannel.Layout.fromKey(oldLayout), ForumChannel.Layout.fromKey(layout)));
                     }
                  case VOICE:
                  case TEXT:
                  case NEWS:
                  case STAGE:
                  case CATEGORY:
                     break;
                  default:
                     WebSocketClient.LOG.debug((String)"CHANNEL_UPDATE provided an unrecognized channel type JSON: {}", (Object)content);
               }

               DataArray permOverwrites = content.getArray("permission_overwrites");
               this.applyPermissions((IPermissionContainerMixin)channel, permOverwrites);
               boolean hasAccessToChannel = channel.getGuild().getSelfMember().hasPermission(channel, new Permission[]{Permission.VIEW_CHANNEL});
               if (channel instanceof IThreadContainer && !hasAccessToChannel) {
                  this.handleHideChildThreads((IThreadContainer)channel);
               }

               return null;
            }
         }
      }
   }

   private AbstractGuildChannelImpl<?> handleChannelTypeChange(AbstractGuildChannelImpl<?> channel, DataObject content, ChannelType newChannelType) {
      if (channel.getType() == newChannelType) {
         return channel;
      } else {
         EntityBuilder builder = this.getJDA().getEntityBuilder();
         GuildImpl guild = (GuildImpl)channel.getGuild();
         ChannelType oldType = channel.getType();
         EnumSet<ChannelType> expectedTypes = EnumSet.complementOf(EnumSet.of(ChannelType.PRIVATE, ChannelType.GROUP, ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PRIVATE_THREAD, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.UNKNOWN));
         if (expectedTypes.contains(oldType) && expectedTypes.contains(newChannelType)) {
            guild.uncacheChannel(channel, true);
            Channel newChannel = builder.createGuildChannel(guild, content);
            if (channel instanceof IThreadContainer) {
               if (newChannel instanceof IThreadContainer) {
                  guild.getThreadChannelCache().forEachUnordered(ThreadChannel::getParentChannel);
               } else {
                  WebSocketClient.LOG.error((String)"ThreadContainer channel transitioned into type that is not ThreadContainer? {} -> {}", (Object)channel.getType(), (Object)newChannel.getType());
               }
            }

            if (newChannel instanceof MessageChannelMixin && channel instanceof MessageChannel) {
               long latestMessageIdLong = ((MessageChannel)channel).getLatestMessageIdLong();
               ((MessageChannelMixin)channel).setLatestMessageIdLong(latestMessageIdLong);
            }

            this.getJDA().handleEvent(new ChannelUpdateTypeEvent(this.getJDA(), this.responseNumber, newChannel, oldType, newChannelType));
            return channel;
         } else {
            WebSocketClient.LOG.warn((String)"Unexpected channel type change {}->{}, discarding from cache.", (Object)channel.getType().getId(), (Object)content.getInt("type"));
            guild.uncacheChannel(channel, false);
            return null;
         }
      }
   }

   private void applyPermissions(IPermissionContainerMixin<?> channel, DataArray permOverwrites) {
      TLongObjectMap<PermissionOverride> currentOverrides = new TLongObjectHashMap<PermissionOverride>(channel.getPermissionOverrideMap());
      List<IPermissionHolder> changed = new ArrayList(currentOverrides.size());
      Guild guild = channel.getGuild();

      for(int i = 0; i < permOverwrites.length(); ++i) {
         DataObject overrideJson = permOverwrites.getObject(i);
         long id = overrideJson.getUnsignedLong("id", 0L);
         if (this.handlePermissionOverride(currentOverrides.remove(id), overrideJson, id, channel)) {
            this.addPermissionHolder(changed, guild, id);
         }
      }

      currentOverrides.forEachValue((override) -> {
         channel.getPermissionOverrideMap().remove(override.getIdLong());
         this.addPermissionHolder(changed, guild, override.getIdLong());
         this.api.handleEvent(new PermissionOverrideDeleteEvent(this.api, this.responseNumber, channel, override));
         return true;
      });
   }

   private void addPermissionHolder(List<IPermissionHolder> changed, Guild guild, long id) {
      IPermissionHolder holder = guild.getRoleById(id);
      if (holder == null) {
         holder = guild.getMemberById(id);
      }

      if (holder != null) {
         changed.add(holder);
      }

   }

   private boolean handlePermissionOverride(PermissionOverride currentOverride, DataObject override, long overrideId, IPermissionContainerMixin<?> channel) {
      long allow = override.getLong("allow");
      long deny = override.getLong("deny");
      int type = override.getInt("type");
      boolean isRole = type == 0;
      if (!isRole) {
         if (type != 1) {
            EntityBuilder.LOG.debug((String)"Ignoring unknown invite of type '{}'. JSON: {}", (Object)type, (Object)override);
            return false;
         }

         if (!this.api.isCacheFlagSet(CacheFlag.MEMBER_OVERRIDES) && overrideId != this.api.getSelfUser().getIdLong()) {
            return false;
         }
      }

      if (currentOverride != null) {
         long oldAllow = currentOverride.getAllowedRaw();
         long oldDeny = currentOverride.getDeniedRaw();
         PermissionOverrideImpl impl = (PermissionOverrideImpl)currentOverride;
         if (oldAllow == allow && oldDeny == deny) {
            return false;
         }

         if (overrideId == channel.getGuild().getIdLong() && (allow | deny) == 0L) {
            channel.getPermissionOverrideMap().remove(overrideId);
            this.api.handleEvent(new PermissionOverrideDeleteEvent(this.api, this.responseNumber, channel, currentOverride));
            return true;
         }

         impl.setAllow(allow);
         impl.setDeny(deny);
         this.api.handleEvent(new PermissionOverrideUpdateEvent(this.api, this.responseNumber, channel, currentOverride, oldAllow, oldDeny));
      } else {
         if (overrideId == channel.getGuild().getIdLong() && (allow | deny) == 0L) {
            return false;
         }

         PermissionOverrideImpl impl;
         currentOverride = impl = new PermissionOverrideImpl(channel, overrideId, isRole);
         impl.setAllow(allow);
         impl.setDeny(deny);
         channel.getPermissionOverrideMap().put(overrideId, currentOverride);
         this.api.handleEvent(new PermissionOverrideCreateEvent(this.api, this.responseNumber, channel, currentOverride));
      }

      return true;
   }

   private void handleHideChildThreads(IThreadContainer channel) {
      List<ThreadChannel> threads = channel.getThreadChannels();
      if (!threads.isEmpty()) {
         for(ThreadChannel thread : threads) {
            GuildImpl guild = (GuildImpl)channel.getGuild();
            ChannelCacheViewImpl<GuildChannel> guildThreadView = guild.getChannelView();
            ChannelCacheViewImpl<Channel> threadView = this.getJDA().getChannelsView();
            UnlockHook vlock = guildThreadView.writeLock();

            try {
               UnlockHook jlock = threadView.writeLock();

               try {
                  threadView.remove(thread.getType(), thread.getIdLong());
                  guildThreadView.remove(thread);
               } catch (Throwable var14) {
                  if (jlock != null) {
                     try {
                        jlock.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (jlock != null) {
                  jlock.close();
               }
            } catch (Throwable var15) {
               if (vlock != null) {
                  try {
                     vlock.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (vlock != null) {
               vlock.close();
            }
         }

         for(ThreadChannel thread : threads) {
            this.api.handleEvent(new ThreadHiddenEvent(this.api, this.responseNumber, thread));
         }

      }
   }

   private void handleTagsUpdate(IPostContainerMixin<?> channel, DataArray tags) {
      if (this.api.isCacheFlagSet(CacheFlag.FORUM_TAGS)) {
         EntityBuilder builder = this.api.getEntityBuilder();
         SortedSnowflakeCacheViewImpl<ForumTag> view = channel.getAvailableTagCache();
         UnlockHook hook = view.writeLock();

         try {
            TLongObjectMap<ForumTag> cache = view.getMap();
            TLongSet removedTags = new TLongHashSet(cache.keySet());

            for(int i = 0; i < tags.length(); ++i) {
               DataObject tagJson = tags.getObject(i);
               long id = tagJson.getUnsignedLong("id");
               if (removedTags.remove(id)) {
                  ForumTagImpl impl = (ForumTagImpl)cache.get(id);
                  if (impl != null) {
                     String name = tagJson.getString("name");
                     boolean moderated = tagJson.getBoolean("moderated");
                     String oldName = impl.getName();
                     EmojiUnion oldEmoji = impl.getEmoji();
                     impl.setEmoji(tagJson);
                     impl.setPosition(i);
                     if (!Objects.equals(oldEmoji, impl.getEmoji())) {
                        this.api.handleEvent(new ForumTagUpdateEmojiEvent(this.api, this.responseNumber, channel, impl, oldEmoji));
                     }

                     if (!name.equals(oldName)) {
                        impl.setName(name);
                        this.api.handleEvent(new ForumTagUpdateNameEvent(this.api, this.responseNumber, channel, impl, oldName));
                     }

                     if (moderated != impl.isModerated()) {
                        impl.setModerated(moderated);
                        this.api.handleEvent(new ForumTagUpdateModeratedEvent(this.api, this.responseNumber, channel, impl, moderated));
                     }
                  }
               } else {
                  ForumTag tag = builder.createForumTag(channel, tagJson, i);
                  cache.put(id, tag);
                  this.api.handleEvent(new ForumTagAddEvent(this.api, this.responseNumber, channel, tag));
               }
            }

            removedTags.forEach((idx) -> {
               ForumTag tag = cache.remove(idx);
               if (tag != null) {
                  this.api.handleEvent(new ForumTagRemoveEvent(this.api, this.responseNumber, channel, tag));
               }

               return true;
            });
         } catch (Throwable var18) {
            if (hook != null) {
               try {
                  hook.close();
               } catch (Throwable var17) {
                  var18.addSuppressed(var17);
               }
            }

            throw var18;
         }

         if (hook != null) {
            hook.close();
         }

      }
   }

   private void handleTopic(ITopicChannelMixin<?> channel, String topic) {
      String oldTopic = channel.getTopic();
      if (!Objects.equals(oldTopic, topic)) {
         channel.setTopic(topic);
         this.api.handleEvent(new ChannelUpdateTopicEvent(this.api, this.responseNumber, channel, oldTopic, topic));
      }
   }

   private void handleSlowmode(ISlowmodeChannelMixin<?> channel, int slowmode) {
      int oldSlowmode = channel.getSlowmode();
      if (oldSlowmode != slowmode) {
         channel.setSlowmode(slowmode);
         this.api.handleEvent(new ChannelUpdateSlowmodeEvent(this.api, this.responseNumber, channel, oldSlowmode, slowmode));
      }
   }

   private void handleNsfw(IAgeRestrictedChannelMixin<?> channel, boolean nsfw) {
      boolean oldNsfw = channel.isNSFW();
      if (oldNsfw != nsfw) {
         channel.setNSFW(nsfw);
         this.api.handleEvent(new ChannelUpdateNSFWEvent(this.api, this.responseNumber, channel, oldNsfw, nsfw));
      }
   }

   private void handleParentCategory(ICategorizableChannelMixin<?> channel, long parentId) {
      long oldParentId = channel.getParentCategoryIdLong();
      if (oldParentId != parentId) {
         Category oldParent = channel.getParentCategory();
         channel.setParentCategory(parentId);
         Category newParent = channel.getParentCategory();
         this.api.handleEvent(new ChannelUpdateParentEvent(this.api, this.responseNumber, channel, oldParent, newParent));
      }
   }

   private void handlePosition(IPositionableChannelMixin<?> channel, int position) {
      int oldPosition = channel.getPositionRaw();
      if (oldPosition != position) {
         channel.setPosition(position);
         this.api.handleEvent(new ChannelUpdatePositionEvent(this.api, this.responseNumber, channel, oldPosition, position));
      }
   }

   private void handleThreadContainer(IThreadContainerMixin<?> channel, DataObject content) {
      int oldDefaultThreadSlowmode = channel.getDefaultThreadSlowmode();
      int defaultThreadSlowmode = content.getInt("default_thread_rate_limit_per_user", 0);
      if (oldDefaultThreadSlowmode != defaultThreadSlowmode) {
         channel.setDefaultThreadSlowmode(defaultThreadSlowmode);
         this.api.handleEvent(new ChannelUpdateDefaultThreadSlowmodeEvent(this.api, this.responseNumber, channel, oldDefaultThreadSlowmode, defaultThreadSlowmode));
      }

   }

   private void handleAudioChannel(AudioChannelMixin<?> channel, DataObject content) {
      int oldBitrate = channel.getBitrate();
      int bitrate = content.getInt("bitrate");
      if (oldBitrate != bitrate) {
         channel.setBitrate(bitrate);
         this.api.handleEvent(new ChannelUpdateBitrateEvent(this.api, this.responseNumber, channel, oldBitrate, bitrate));
      }

      int userLimit = content.getInt("user_limit");
      int oldLimit = channel.getUserLimit();
      if (oldLimit != userLimit) {
         channel.setUserLimit(userLimit);
         this.getJDA().handleEvent(new ChannelUpdateUserLimitEvent(this.getJDA(), this.responseNumber, channel, oldLimit, userLimit));
      }

      String oldRegion = channel.getRegionRaw();
      String regionRaw = content.getString("rtc_region", (String)null);
      if (!Objects.equals(oldRegion, regionRaw)) {
         channel.setRegion(regionRaw);
         this.api.handleEvent(new ChannelUpdateRegionEvent(this.api, this.responseNumber, channel, Region.fromKey(oldRegion), Region.fromKey(regionRaw)));
      }

   }

   private void handlePostContainer(IPostContainerMixin<?> channel, DataObject content) {
      content.optArray("available_tags").ifPresent((array) -> this.handleTagsUpdate(channel, array));
      EmojiUnion defaultReaction = (EmojiUnion)content.optObject("default_reaction_emoji").map((json) -> EntityBuilder.createEmoji(json, "emoji_name", "emoji_id")).orElse((Object)null);
      EmojiUnion oldDefaultReaction = channel.getDefaultReaction();
      if (!Objects.equals(oldDefaultReaction, defaultReaction)) {
         channel.setDefaultReaction((DataObject)content.optObject("default_reaction_emoji").orElse((Object)null));
         this.getJDA().handleEvent(new ChannelUpdateDefaultReactionEvent(this.getJDA(), this.responseNumber, channel, oldDefaultReaction, defaultReaction));
      }

      int sortOrder = content.getInt("default_sort_order", channel.getRawSortOrder());
      int oldSortOrder = channel.getRawSortOrder();
      if (oldSortOrder != sortOrder) {
         channel.setDefaultSortOrder(sortOrder);
         this.getJDA().handleEvent(new ChannelUpdateDefaultSortOrderEvent(this.getJDA(), this.responseNumber, channel, IPostContainer.SortOrder.fromKey(oldSortOrder)));
      }

      int newFlags = content.getInt("flags", 0);
      int oldFlags = channel.getRawFlags();
      if (oldFlags != newFlags) {
         channel.setFlags(newFlags);
         this.getJDA().handleEvent(new ChannelUpdateFlagsEvent(this.getJDA(), this.responseNumber, channel, ChannelFlag.fromRaw(oldFlags), ChannelFlag.fromRaw(newFlags)));
      }

   }
}
