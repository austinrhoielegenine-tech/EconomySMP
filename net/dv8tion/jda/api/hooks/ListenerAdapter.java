package net.dv8tion.jda.api.hooks;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nonnull;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.events.StatusChangeEvent;
import net.dv8tion.jda.api.events.UpdateEvent;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleCreateEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleDeleteEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleUpdateEvent;
import net.dv8tion.jda.api.events.automod.GenericAutoModRuleEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.GenericChannelEvent;
import net.dv8tion.jda.api.events.channel.VoiceChannelEffectSendEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent;
import net.dv8tion.jda.api.events.channel.forum.GenericForumTagEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.forum.update.GenericForumTagUpdateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateAppliedTagsEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchiveTimestampEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateAutoArchiveDurationEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateBitrateEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultLayoutEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultReactionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultSortOrderEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateDefaultThreadSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateFlagsEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateInvitableEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateLockedEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateParentEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateRegionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTopicEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTypeEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateVoiceStatusEvent;
import net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.GenericEmojiEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.events.emoji.update.GenericEmojiUpdateEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementCreateEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementDeleteEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementUpdateEvent;
import net.dv8tion.jda.api.events.entitlement.GenericEntitlementEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildTimeoutEvent;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.invite.GenericGuildInviteEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GenericGuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateFlagsEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.override.GenericPermissionOverrideEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.GenericScheduledEventGatewayEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.GenericScheduledEventUserEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.GenericScheduledEventUpdateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateCoverImageEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateEndTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateImageEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateLocationEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.events.guild.update.GenericGuildUpdateEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateCommunityUpdatesChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateExplicitContentLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateFeaturesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateLocaleEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMFALevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxMembersEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateMaxPresencesEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNSFWLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNotificationLevelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateRulesChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSafetyAlertsChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentActionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSecurityIncidentDetectionsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSystemChannelFlagsEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVanityCodeEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceRequestToSpeakEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSuppressEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceVideoEvent;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.interaction.GenericAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.ApplicationCommandUpdatePrivilegesEvent;
import net.dv8tion.jda.api.events.interaction.command.ApplicationUpdatePrivilegesEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.GenericPrivilegeUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericSelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.poll.GenericMessagePollVoteEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.api.events.self.GenericSelfUpdateEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateGlobalNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.api.events.session.GenericSessionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.SessionInvalidateEvent;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.events.soundboard.GenericSoundboardSoundEvent;
import net.dv8tion.jda.api.events.soundboard.SoundboardSoundCreateEvent;
import net.dv8tion.jda.api.events.soundboard.SoundboardSoundDeleteEvent;
import net.dv8tion.jda.api.events.soundboard.update.GenericSoundboardSoundUpdateEvent;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateEmojiEvent;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateNameEvent;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateVolumeEvent;
import net.dv8tion.jda.api.events.stage.GenericStageInstanceEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;
import net.dv8tion.jda.api.events.stage.update.GenericStageInstanceUpdateEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent;
import net.dv8tion.jda.api.events.sticker.GenericGuildStickerEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GenericGuildStickerUpdateEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateAvailableEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateNameEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateTagsEvent;
import net.dv8tion.jda.api.events.thread.GenericThreadEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.events.thread.member.GenericThreadMemberEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserUpdateEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateFlagsEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateGlobalNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdatePrimaryGuildEvent;
import net.dv8tion.jda.internal.utils.ClassWalker;

public abstract class ListenerAdapter implements EventListener {
   private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
   private static final ConcurrentMap<Class<?>, MethodHandle> methods = new ConcurrentHashMap();
   private static final Set<Class<?>> unresolved = ConcurrentHashMap.newKeySet();

   public void onGenericEvent(@Nonnull GenericEvent event) {
   }

   public void onGenericUpdate(@Nonnull UpdateEvent<?, ?> event) {
   }

   public void onRawGateway(@Nonnull RawGatewayEvent event) {
   }

   public void onGatewayPing(@Nonnull GatewayPingEvent event) {
   }

   public void onReady(@Nonnull ReadyEvent event) {
   }

   public void onSessionInvalidate(@Nonnull SessionInvalidateEvent event) {
   }

   public void onSessionDisconnect(@Nonnull SessionDisconnectEvent event) {
   }

   public void onSessionResume(@Nonnull SessionResumeEvent event) {
   }

   public void onSessionRecreate(@Nonnull SessionRecreateEvent event) {
   }

   public void onShutdown(@Nonnull ShutdownEvent event) {
   }

   public void onStatusChange(@Nonnull StatusChangeEvent event) {
   }

   public void onException(@Nonnull ExceptionEvent event) {
   }

   public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
   }

   public void onUserContextInteraction(@Nonnull UserContextInteractionEvent event) {
   }

   public void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event) {
   }

   public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
   }

   public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
   }

   public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
   }

   public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
   }

   public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
   }

   public void onUserUpdateName(@Nonnull UserUpdateNameEvent event) {
   }

   public void onUserUpdateGlobalName(@Nonnull UserUpdateGlobalNameEvent event) {
   }

   public void onUserUpdateDiscriminator(@Nonnull UserUpdateDiscriminatorEvent event) {
   }

   public void onUserUpdateAvatar(@Nonnull UserUpdateAvatarEvent event) {
   }

   public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
   }

   public void onUserUpdateActivityOrder(@Nonnull UserUpdateActivityOrderEvent event) {
   }

   public void onUserUpdateFlags(@Nonnull UserUpdateFlagsEvent event) {
   }

   public void onUserUpdatePrimaryGuild(@Nonnull UserUpdatePrimaryGuildEvent event) {
   }

   public void onUserTyping(@Nonnull UserTypingEvent event) {
   }

   public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
   }

   public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
   }

   public void onUserUpdateActivities(@Nonnull UserUpdateActivitiesEvent event) {
   }

   public void onSelfUpdateAvatar(@Nonnull SelfUpdateAvatarEvent event) {
   }

   public void onSelfUpdateMFA(@Nonnull SelfUpdateMFAEvent event) {
   }

   public void onSelfUpdateName(@Nonnull SelfUpdateNameEvent event) {
   }

   public void onSelfUpdateDiscriminator(@Nonnull SelfUpdateDiscriminatorEvent event) {
   }

   public void onSelfUpdateGlobalName(@Nonnull SelfUpdateGlobalNameEvent event) {
   }

   public void onSelfUpdateVerified(@Nonnull SelfUpdateVerifiedEvent event) {
   }

   public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
   }

   public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
   }

   public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
   }

   public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
   }

   public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
   }

   public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
   }

   public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
   }

   public void onMessageReactionRemoveEmoji(@Nonnull MessageReactionRemoveEmojiEvent event) {
   }

   public void onMessagePollVoteAdd(@Nonnull MessagePollVoteAddEvent event) {
   }

   public void onMessagePollVoteRemove(@Nonnull MessagePollVoteRemoveEvent event) {
   }

   public void onPermissionOverrideDelete(@Nonnull PermissionOverrideDeleteEvent event) {
   }

   public void onPermissionOverrideUpdate(@Nonnull PermissionOverrideUpdateEvent event) {
   }

   public void onPermissionOverrideCreate(@Nonnull PermissionOverrideCreateEvent event) {
   }

   public void onStageInstanceDelete(@Nonnull StageInstanceDeleteEvent event) {
   }

   public void onStageInstanceUpdateTopic(@Nonnull StageInstanceUpdateTopicEvent event) {
   }

   public void onStageInstanceUpdatePrivacyLevel(@Nonnull StageInstanceUpdatePrivacyLevelEvent event) {
   }

   public void onStageInstanceCreate(@Nonnull StageInstanceCreateEvent event) {
   }

   public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
   }

   public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
   }

   public void onChannelUpdateBitrate(@Nonnull ChannelUpdateBitrateEvent event) {
   }

   public void onChannelUpdateName(@Nonnull ChannelUpdateNameEvent event) {
   }

   public void onChannelUpdateFlags(@Nonnull ChannelUpdateFlagsEvent event) {
   }

   public void onChannelUpdateNSFW(@Nonnull ChannelUpdateNSFWEvent event) {
   }

   public void onChannelUpdateParent(@Nonnull ChannelUpdateParentEvent event) {
   }

   public void onChannelUpdatePosition(@Nonnull ChannelUpdatePositionEvent event) {
   }

   public void onChannelUpdateRegion(@Nonnull ChannelUpdateRegionEvent event) {
   }

   public void onChannelUpdateSlowmode(@Nonnull ChannelUpdateSlowmodeEvent event) {
   }

   public void onChannelUpdateDefaultThreadSlowmode(@Nonnull ChannelUpdateDefaultThreadSlowmodeEvent event) {
   }

   public void onChannelUpdateDefaultReaction(@Nonnull ChannelUpdateDefaultReactionEvent event) {
   }

   public void onChannelUpdateDefaultSortOrder(@Nonnull ChannelUpdateDefaultSortOrderEvent event) {
   }

   public void onChannelUpdateDefaultLayout(@Nonnull ChannelUpdateDefaultLayoutEvent event) {
   }

   public void onChannelUpdateTopic(@Nonnull ChannelUpdateTopicEvent event) {
   }

   public void onChannelUpdateVoiceStatus(@Nonnull ChannelUpdateVoiceStatusEvent event) {
   }

   public void onChannelUpdateType(@Nonnull ChannelUpdateTypeEvent event) {
   }

   public void onChannelUpdateUserLimit(@Nonnull ChannelUpdateUserLimitEvent event) {
   }

   public void onChannelUpdateArchived(@Nonnull ChannelUpdateArchivedEvent event) {
   }

   public void onChannelUpdateArchiveTimestamp(@Nonnull ChannelUpdateArchiveTimestampEvent event) {
   }

   public void onChannelUpdateAutoArchiveDuration(@Nonnull ChannelUpdateAutoArchiveDurationEvent event) {
   }

   public void onChannelUpdateLocked(@Nonnull ChannelUpdateLockedEvent event) {
   }

   public void onChannelUpdateInvitable(@Nonnull ChannelUpdateInvitableEvent event) {
   }

   public void onChannelUpdateAppliedTags(@Nonnull ChannelUpdateAppliedTagsEvent event) {
   }

   public void onForumTagAdd(@Nonnull ForumTagAddEvent event) {
   }

   public void onForumTagRemove(@Nonnull ForumTagRemoveEvent event) {
   }

   public void onForumTagUpdateName(@Nonnull ForumTagUpdateNameEvent event) {
   }

   public void onForumTagUpdateEmoji(@Nonnull ForumTagUpdateEmojiEvent event) {
   }

   public void onForumTagUpdateModerated(@Nonnull ForumTagUpdateModeratedEvent event) {
   }

   public void onThreadRevealed(@Nonnull ThreadRevealedEvent event) {
   }

   public void onThreadHidden(@Nonnull ThreadHiddenEvent event) {
   }

   public void onThreadMemberJoin(@Nonnull ThreadMemberJoinEvent event) {
   }

   public void onThreadMemberLeave(@Nonnull ThreadMemberLeaveEvent event) {
   }

   public void onGuildReady(@Nonnull GuildReadyEvent event) {
   }

   public void onGuildTimeout(@Nonnull GuildTimeoutEvent event) {
   }

   public void onGuildJoin(@Nonnull GuildJoinEvent event) {
   }

   public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
   }

   public void onGuildAvailable(@Nonnull GuildAvailableEvent event) {
   }

   public void onGuildUnavailable(@Nonnull GuildUnavailableEvent event) {
   }

   public void onUnavailableGuildJoined(@Nonnull UnavailableGuildJoinedEvent event) {
   }

   public void onUnavailableGuildLeave(@Nonnull UnavailableGuildLeaveEvent event) {
   }

   public void onGuildBan(@Nonnull GuildBanEvent event) {
   }

   public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
   }

   public void onGuildAuditLogEntryCreate(@Nonnull GuildAuditLogEntryCreateEvent event) {
   }

   public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
   }

   public void onGuildUpdateAfkChannel(@Nonnull GuildUpdateAfkChannelEvent event) {
   }

   public void onGuildUpdateSystemChannel(@Nonnull GuildUpdateSystemChannelEvent event) {
   }

   public void onGuildUpdateRulesChannel(@Nonnull GuildUpdateRulesChannelEvent event) {
   }

   public void onGuildUpdateCommunityUpdatesChannel(@Nonnull GuildUpdateCommunityUpdatesChannelEvent event) {
   }

   public void onGuildUpdateSafetyAlertsChannel(@Nonnull GuildUpdateSafetyAlertsChannelEvent event) {
   }

   public void onGuildUpdateAfkTimeout(@Nonnull GuildUpdateAfkTimeoutEvent event) {
   }

   public void onGuildUpdateSecurityIncidentActions(@Nonnull GuildUpdateSecurityIncidentActionsEvent event) {
   }

   public void onGuildUpdateSecurityIncidentDetections(@Nonnull GuildUpdateSecurityIncidentDetectionsEvent event) {
   }

   public void onGuildUpdateExplicitContentLevel(@Nonnull GuildUpdateExplicitContentLevelEvent event) {
   }

   public void onGuildUpdateIcon(@Nonnull GuildUpdateIconEvent event) {
   }

   public void onGuildUpdateMFALevel(@Nonnull GuildUpdateMFALevelEvent event) {
   }

   public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
   }

   public void onGuildUpdateNotificationLevel(@Nonnull GuildUpdateNotificationLevelEvent event) {
   }

   public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event) {
   }

   public void onGuildUpdateSplash(@Nonnull GuildUpdateSplashEvent event) {
   }

   public void onGuildUpdateVerificationLevel(@Nonnull GuildUpdateVerificationLevelEvent event) {
   }

   public void onGuildUpdateLocale(@Nonnull GuildUpdateLocaleEvent event) {
   }

   public void onGuildUpdateFeatures(@Nonnull GuildUpdateFeaturesEvent event) {
   }

   public void onGuildUpdateVanityCode(@Nonnull GuildUpdateVanityCodeEvent event) {
   }

   public void onGuildUpdateBanner(@Nonnull GuildUpdateBannerEvent event) {
   }

   public void onGuildUpdateDescription(@Nonnull GuildUpdateDescriptionEvent event) {
   }

   public void onGuildUpdateSystemChannelFlags(@Nonnull GuildUpdateSystemChannelFlagsEvent event) {
   }

   public void onGuildUpdateBoostTier(@Nonnull GuildUpdateBoostTierEvent event) {
   }

   public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
   }

   public void onGuildUpdateMaxMembers(@Nonnull GuildUpdateMaxMembersEvent event) {
   }

   public void onGuildUpdateMaxPresences(@Nonnull GuildUpdateMaxPresencesEvent event) {
   }

   public void onGuildUpdateNSFWLevel(@Nonnull GuildUpdateNSFWLevelEvent event) {
   }

   public void onScheduledEventUpdateDescription(@Nonnull ScheduledEventUpdateDescriptionEvent event) {
   }

   public void onScheduledEventUpdateEndTime(@Nonnull ScheduledEventUpdateEndTimeEvent event) {
   }

   public void onScheduledEventUpdateLocation(@Nonnull ScheduledEventUpdateLocationEvent event) {
   }

   public void onScheduledEventUpdateName(@Nonnull ScheduledEventUpdateNameEvent event) {
   }

   public void onScheduledEventUpdateStartTime(@Nonnull ScheduledEventUpdateStartTimeEvent event) {
   }

   public void onScheduledEventUpdateStatus(@Nonnull ScheduledEventUpdateStatusEvent event) {
   }

   /** @deprecated */
   @Deprecated
   @ReplaceWith("onScheduledEventUpdateCoverImage(ScheduledEventUpdateCoverImageEvent)")
   public void onScheduledEventUpdateImage(@Nonnull ScheduledEventUpdateImageEvent event) {
   }

   public void onScheduledEventUpdateCoverImage(@Nonnull ScheduledEventUpdateCoverImageEvent event) {
   }

   public void onScheduledEventCreate(@Nonnull ScheduledEventCreateEvent event) {
   }

   public void onScheduledEventDelete(@Nonnull ScheduledEventDeleteEvent event) {
   }

   public void onScheduledEventUserAdd(@Nonnull ScheduledEventUserAddEvent event) {
   }

   public void onScheduledEventUserRemove(@Nonnull ScheduledEventUserRemoveEvent event) {
   }

   public void onGuildInviteCreate(@Nonnull GuildInviteCreateEvent event) {
   }

   public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event) {
   }

   public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
   }

   public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
   }

   public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
   }

   public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event) {
   }

   public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
   }

   public void onGuildMemberUpdateAvatar(@Nonnull GuildMemberUpdateAvatarEvent event) {
   }

   public void onGuildMemberUpdateBoostTime(@Nonnull GuildMemberUpdateBoostTimeEvent event) {
   }

   public void onGuildMemberUpdatePending(@Nonnull GuildMemberUpdatePendingEvent event) {
   }

   public void onGuildMemberUpdateFlags(@Nonnull GuildMemberUpdateFlagsEvent event) {
   }

   public void onGuildMemberUpdateTimeOut(@Nonnull GuildMemberUpdateTimeOutEvent event) {
   }

   public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
   }

   public void onGuildVoiceMute(@Nonnull GuildVoiceMuteEvent event) {
   }

   public void onGuildVoiceDeafen(@Nonnull GuildVoiceDeafenEvent event) {
   }

   public void onGuildVoiceGuildMute(@Nonnull GuildVoiceGuildMuteEvent event) {
   }

   public void onGuildVoiceGuildDeafen(@Nonnull GuildVoiceGuildDeafenEvent event) {
   }

   public void onGuildVoiceSelfMute(@Nonnull GuildVoiceSelfMuteEvent event) {
   }

   public void onGuildVoiceSelfDeafen(@Nonnull GuildVoiceSelfDeafenEvent event) {
   }

   public void onGuildVoiceSuppress(@Nonnull GuildVoiceSuppressEvent event) {
   }

   public void onGuildVoiceStream(@Nonnull GuildVoiceStreamEvent event) {
   }

   public void onGuildVoiceVideo(@Nonnull GuildVoiceVideoEvent event) {
   }

   public void onGuildVoiceRequestToSpeak(@Nonnull GuildVoiceRequestToSpeakEvent event) {
   }

   public void onAutoModExecution(@Nonnull AutoModExecutionEvent event) {
   }

   public void onAutoModRuleCreate(@Nonnull AutoModRuleCreateEvent event) {
   }

   public void onAutoModRuleUpdate(@Nonnull AutoModRuleUpdateEvent event) {
   }

   public void onAutoModRuleDelete(@Nonnull AutoModRuleDeleteEvent event) {
   }

   public void onRoleCreate(@Nonnull RoleCreateEvent event) {
   }

   public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
   }

   /** @deprecated */
   @Deprecated
   @ReplaceWith("onRoleUpdateColors(event)")
   public void onRoleUpdateColor(@Nonnull RoleUpdateColorEvent event) {
   }

   public void onRoleUpdateColors(@Nonnull RoleUpdateColorsEvent event) {
   }

   public void onRoleUpdateHoisted(@Nonnull RoleUpdateHoistedEvent event) {
   }

   public void onRoleUpdateIcon(@Nonnull RoleUpdateIconEvent event) {
   }

   public void onRoleUpdateMentionable(@Nonnull RoleUpdateMentionableEvent event) {
   }

   public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {
   }

   public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {
   }

   public void onRoleUpdatePosition(@Nonnull RoleUpdatePositionEvent event) {
   }

   public void onEmojiAdded(@Nonnull EmojiAddedEvent event) {
   }

   public void onEmojiRemoved(@Nonnull EmojiRemovedEvent event) {
   }

   public void onEmojiUpdateName(@Nonnull EmojiUpdateNameEvent event) {
   }

   public void onEmojiUpdateRoles(@Nonnull EmojiUpdateRolesEvent event) {
   }

   public void onGenericPrivilegeUpdate(@Nonnull GenericPrivilegeUpdateEvent event) {
   }

   public void onApplicationCommandUpdatePrivileges(@Nonnull ApplicationCommandUpdatePrivilegesEvent event) {
   }

   public void onApplicationUpdatePrivileges(@Nonnull ApplicationUpdatePrivilegesEvent event) {
   }

   public void onGuildStickerAdded(@Nonnull GuildStickerAddedEvent event) {
   }

   public void onGuildStickerRemoved(@Nonnull GuildStickerRemovedEvent event) {
   }

   public void onGuildStickerUpdateName(@Nonnull GuildStickerUpdateNameEvent event) {
   }

   public void onGuildStickerUpdateTags(@Nonnull GuildStickerUpdateTagsEvent event) {
   }

   public void onGuildStickerUpdateDescription(@Nonnull GuildStickerUpdateDescriptionEvent event) {
   }

   public void onGuildStickerUpdateAvailable(@Nonnull GuildStickerUpdateAvailableEvent event) {
   }

   public void onGenericSoundboardSound(@Nonnull GenericSoundboardSoundEvent event) {
   }

   public void onSoundboardSoundCreate(@Nonnull SoundboardSoundCreateEvent event) {
   }

   public void onSoundboardSoundDelete(@Nonnull SoundboardSoundDeleteEvent event) {
   }

   public void onGenericSoundboardSoundUpdate(@Nonnull GenericSoundboardSoundUpdateEvent<?> event) {
   }

   public void onSoundboardSoundUpdateName(@Nonnull SoundboardSoundUpdateNameEvent event) {
   }

   public void onSoundboardSoundUpdateVolume(@Nonnull SoundboardSoundUpdateVolumeEvent event) {
   }

   public void onSoundboardSoundUpdateEmoji(@Nonnull SoundboardSoundUpdateEmojiEvent event) {
   }

   public void onVoiceChannelEffectSend(@Nonnull VoiceChannelEffectSendEvent event) {
   }

   public void onEntitlementCreate(@Nonnull EntitlementCreateEvent event) {
   }

   public void onEntitlementUpdate(@Nonnull EntitlementUpdateEvent event) {
   }

   public void onEntitlementDelete(@Nonnull EntitlementDeleteEvent event) {
   }

   public void onHttpRequest(@Nonnull HttpRequestEvent event) {
   }

   public void onGenericSession(@Nonnull GenericSessionEvent event) {
   }

   public void onGenericInteractionCreate(@Nonnull GenericInteractionCreateEvent event) {
   }

   public void onGenericAutoCompleteInteraction(@Nonnull GenericAutoCompleteInteractionEvent event) {
   }

   public void onGenericComponentInteractionCreate(@Nonnull GenericComponentInteractionCreateEvent event) {
   }

   public void onGenericCommandInteraction(@Nonnull GenericCommandInteractionEvent event) {
   }

   public void onGenericContextInteraction(@Nonnull GenericContextInteractionEvent<?> event) {
   }

   public void onGenericSelectMenuInteraction(@Nonnull GenericSelectMenuInteractionEvent<?, ?> event) {
   }

   public void onGenericMessage(@Nonnull GenericMessageEvent event) {
   }

   public void onGenericMessageReaction(@Nonnull GenericMessageReactionEvent event) {
   }

   public void onGenericMessagePollVote(@Nonnull GenericMessagePollVoteEvent event) {
   }

   public void onGenericUser(@Nonnull GenericUserEvent event) {
   }

   public void onGenericUserPresence(@Nonnull GenericUserPresenceEvent event) {
   }

   public void onGenericUserUpdate(@Nonnull GenericUserUpdateEvent<?> event) {
   }

   public void onGenericSelfUpdate(@Nonnull GenericSelfUpdateEvent<?> event) {
   }

   public void onGenericStageInstance(@Nonnull GenericStageInstanceEvent event) {
   }

   public void onGenericStageInstanceUpdate(@Nonnull GenericStageInstanceUpdateEvent<?> event) {
   }

   public void onGenericChannel(@Nonnull GenericChannelEvent event) {
   }

   public void onGenericChannelUpdate(@Nonnull GenericChannelUpdateEvent<?> event) {
   }

   public void onGenericThread(@Nonnull GenericThreadEvent event) {
   }

   public void onGenericThreadMember(@Nonnull GenericThreadMemberEvent event) {
   }

   public void onGenericGuild(@Nonnull GenericGuildEvent event) {
   }

   public void onGenericGuildUpdate(@Nonnull GenericGuildUpdateEvent<?> event) {
   }

   public void onGenericGuildInvite(@Nonnull GenericGuildInviteEvent event) {
   }

   public void onGenericGuildMember(@Nonnull GenericGuildMemberEvent event) {
   }

   public void onGenericGuildMemberUpdate(@Nonnull GenericGuildMemberUpdateEvent<?> event) {
   }

   public void onGenericGuildVoice(@Nonnull GenericGuildVoiceEvent event) {
   }

   public void onGenericAutoModRule(@Nonnull GenericAutoModRuleEvent event) {
   }

   public void onGenericRole(@Nonnull GenericRoleEvent event) {
   }

   public void onGenericRoleUpdate(@Nonnull GenericRoleUpdateEvent<?> event) {
   }

   public void onGenericEmoji(@Nonnull GenericEmojiEvent event) {
   }

   public void onGenericEmojiUpdate(@Nonnull GenericEmojiUpdateEvent<?> event) {
   }

   public void onGenericGuildSticker(@Nonnull GenericGuildStickerEvent event) {
   }

   public void onGenericGuildStickerUpdate(@Nonnull GenericGuildStickerUpdateEvent<?> event) {
   }

   public void onGenericEntitlement(@Nonnull GenericEntitlementEvent event) {
   }

   public void onGenericPermissionOverride(@Nonnull GenericPermissionOverrideEvent event) {
   }

   public void onGenericScheduledEventUpdate(@Nonnull GenericScheduledEventUpdateEvent<?> event) {
   }

   public void onGenericScheduledEventGateway(@Nonnull GenericScheduledEventGatewayEvent event) {
   }

   public void onGenericScheduledEventUser(@Nonnull GenericScheduledEventUserEvent event) {
   }

   public void onGenericForumTag(@Nonnull GenericForumTagEvent event) {
   }

   public void onGenericForumTagUpdate(@Nonnull GenericForumTagUpdateEvent<?> event) {
   }

   public final void onEvent(@Nonnull GenericEvent event) {
      this.onGenericEvent(event);
      if (event instanceof UpdateEvent) {
         this.onGenericUpdate((UpdateEvent)event);
      }

      for(Class<?> clazz : ClassWalker.range(event.getClass(), GenericEvent.class)) {
         if (!unresolved.contains(clazz)) {
            MethodHandle mh = (MethodHandle)methods.computeIfAbsent(clazz, ListenerAdapter::findMethod);
            if (mh == null) {
               unresolved.add(clazz);
            } else {
               try {
                  mh.invoke(this, event);
               } catch (Throwable throwable) {
                  if (throwable instanceof RuntimeException) {
                     throw (RuntimeException)throwable;
                  }

                  if (throwable instanceof Error) {
                     throw (Error)throwable;
                  }

                  throw new IllegalStateException(throwable);
               }
            }
         }
      }

   }

   private static MethodHandle findMethod(Class<?> clazz) {
      String name = clazz.getSimpleName();
      MethodType type = MethodType.methodType(Void.TYPE, clazz);

      try {
         name = "on" + name.substring(0, name.length() - "Event".length());
         return lookup.findVirtual(ListenerAdapter.class, name, type);
      } catch (IllegalAccessException | NoSuchMethodException var4) {
         return null;
      }
   }

   static {
      Collections.addAll(unresolved, new Class[]{Object.class, Event.class, UpdateEvent.class, GenericEvent.class});
   }
}
