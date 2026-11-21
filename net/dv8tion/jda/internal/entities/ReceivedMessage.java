package net.dv8tion.jda.internal.entities;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.entities.messages.MessageSnapshot;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.ErrorMapper;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageEditActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.ReactionPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class ReceivedMessage implements Message {
   public static boolean didContentIntentWarning = false;
   private final Object mutex = new Object();
   protected final JDAImpl api;
   protected final long id;
   protected final long channelId;
   protected final long guildId;
   protected final long applicationId;
   protected final int flags;
   protected final int position;
   protected final boolean fromWebhook;
   protected final boolean pinned;
   protected final boolean isTTS;
   protected final MessageType type;
   protected final MessageChannel channel;
   protected final Guild guild;
   protected final MessageReference messageReference;
   protected final User author;
   protected final Member member;
   protected final String content;
   protected final String nonce;
   protected final MessageActivity activity;
   protected final MessagePoll poll;
   protected final OffsetDateTime editedTime;
   protected final Mentions mentions;
   protected final Message.Interaction interaction;
   protected final Message.InteractionMetadata interactionMetadata;
   protected final ThreadChannel startedThread;
   protected final List<MessageReaction> reactions;
   protected final List<Message.Attachment> attachments;
   protected final List<MessageEmbed> embeds;
   protected final List<StickerItem> stickers;
   protected final List<MessageTopLevelComponentUnion> components;
   protected final List<MessageSnapshot> messageSnapshots;
   protected WebhookClient<Message> webhook;
   protected String altContent = null;
   protected String strippedContent = null;
   protected List<String> invites = null;

   public ReceivedMessage(long id, long channelId, long guildId, JDA jda, Guild guild, MessageChannel channel, MessageType type, MessageReference messageReference, boolean fromWebhook, long applicationId, boolean tts, boolean pinned, String content, String nonce, User author, Member member, MessageActivity activity, MessagePoll poll, OffsetDateTime editTime, Mentions mentions, List<MessageReaction> reactions, List<Message.Attachment> attachments, List<MessageEmbed> embeds, List<StickerItem> stickers, List<MessageTopLevelComponentUnion> components, List<MessageSnapshot> messageSnapshots, int flags, Message.Interaction interaction, Message.InteractionMetadata interactionMetadata, ThreadChannel startedThread, int position) {
      this.id = id;
      this.channelId = channelId;
      this.channel = channel;
      this.guildId = guildId;
      this.guild = guild;
      this.messageReference = messageReference;
      this.type = type;
      this.api = (JDAImpl)jda;
      this.fromWebhook = fromWebhook;
      this.applicationId = applicationId;
      this.pinned = pinned;
      this.content = content;
      this.nonce = nonce;
      this.isTTS = tts;
      this.author = author;
      this.member = member;
      this.activity = activity;
      this.editedTime = editTime;
      this.mentions = mentions;
      this.reactions = Collections.unmodifiableList(reactions);
      this.attachments = Collections.unmodifiableList(attachments);
      this.embeds = Collections.unmodifiableList(embeds);
      this.stickers = Collections.unmodifiableList(stickers);
      this.components = Collections.unmodifiableList(components);
      this.messageSnapshots = Collections.unmodifiableList(messageSnapshots);
      this.flags = flags;
      this.interaction = interaction;
      this.interactionMetadata = interactionMetadata;
      this.startedThread = startedThread;
      this.position = position;
      this.poll = poll;
   }

   private void checkSystem(String comment) {
      if (this.type.isSystem()) {
         throw new IllegalStateException("Cannot " + comment + " a system message!");
      }
   }

   private void checkUser() {
      if (!this.getJDA().getSelfUser().equals(this.getAuthor())) {
         throw new IllegalStateException("Attempted to update message that was not sent by this account. You cannot modify other User's messages!");
      }
   }

   private void checkIntent() {
      if (!didContentIntentWarning && !this.api.isIntent(GatewayIntent.MESSAGE_CONTENT)) {
         SelfUser selfUser = this.api.getSelfUser();
         boolean isBotOwnedWebhookMessage = selfUser.getApplicationIdLong() == this.getApplicationIdLong() && this.isWebhookMessage();
         if (!Objects.equals(selfUser, this.author) && !this.mentions.getUsers().contains(selfUser) && this.isFromGuild() && !isBotOwnedWebhookMessage && !this.hasPrivilegedContent()) {
            didContentIntentWarning = true;
            JDAImpl.LOG.warn("Attempting to access message content without GatewayIntent.MESSAGE_CONTENT.\nDiscord now requires to explicitly enable access to this using the MESSAGE_CONTENT intent.\nUseful resources to learn more:\n\t- https://support-dev.discord.com/hc/en-us/articles/4404772028055-Message-Content-Privileged-Intent-FAQ\n\t- https://jda.wiki/using-jda/gateway-intents-and-member-cache-policy/\n\t- https://jda.wiki/using-jda/troubleshooting/#cannot-get-message-content-attempting-to-access-message-content-without-gatewayintent\nOr suppress this warning if this is intentional with Message.suppressContentIntentWarning()");
         }
      }

   }

   private boolean hasPrivilegedContent() {
      return !this.content.isEmpty() || !this.embeds.isEmpty() || !this.attachments.isEmpty() || !this.components.isEmpty() || this.poll != null;
   }

   public ReceivedMessage withHook(WebhookClient<Message> hook) {
      this.webhook = hook;
      return this;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nullable
   public MessageReference getMessageReference() {
      return this.messageReference;
   }

   public boolean isPinned() {
      return this.pinned;
   }

   @Nonnull
   public AuditableRestAction<Void> pin() {
      this.checkSystem("pin");
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot pin ephemeral messages.");
      } else if (this.hasChannel()) {
         return this.getChannel().pinMessageById(this.getIdLong());
      } else {
         Route.CompiledRoute route = Route.Messages.PIN_MESSAGE.compile(this.getChannelId(), this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public AuditableRestAction<Void> unpin() {
      this.checkSystem("unpin");
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot unpin ephemeral messages.");
      } else if (this.hasChannel()) {
         return this.getChannel().unpinMessageById(this.getIdLong());
      } else {
         Route.CompiledRoute route = Route.Messages.UNPIN_MESSAGE.compile(this.getChannelId(), this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public RestAction<Void> addReaction(@Nonnull Emoji emoji) {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot add reactions to ephemeral messages.");
      } else {
         Checks.notNull(emoji, "Emoji");
         if (this.hasChannel()) {
            boolean missingReaction = this.reactions.stream().map(MessageReaction::getEmoji).noneMatch((r) -> r.getAsReactionCode().equals(emoji.getAsReactionCode()));
            if (missingReaction && emoji instanceof RichCustomEmoji) {
               Checks.check(((RichCustomEmoji)emoji).canInteract(this.getJDA().getSelfUser(), this.getChannel()), "Cannot react with the provided emoji because it is not available in the current getChannel().");
            }

            return this.getChannel().addReactionById(this.getId(), emoji);
         } else {
            String encoded = EncodingUtil.encodeReaction(emoji.getAsReactionCode());
            Route.CompiledRoute route = Route.Messages.ADD_REACTION.compile(this.getChannelId(), this.getId(), encoded, "@me");
            return new RestActionImpl<Void>(this.getJDA(), route);
         }
      }
   }

   @Nonnull
   public RestAction<Void> clearReactions() {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot clear reactions from ephemeral messages.");
      } else if (!this.isFromGuild()) {
         throw new IllegalStateException("Cannot clear reactions from a message in a Group or PrivateChannel.");
      } else if (this.channel instanceof GuildMessageChannel) {
         return ((GuildMessageChannel)this.channel).clearReactionsById(this.getId());
      } else {
         Route.CompiledRoute route = Route.Messages.REMOVE_ALL_REACTIONS.compile(this.getChannelId(), this.getId());
         return new RestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public RestAction<Void> clearReactions(@Nonnull Emoji emoji) {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot clear reactions from ephemeral messages.");
      } else if (!this.isFromGuild()) {
         throw new IllegalStateException("Cannot clear reactions from a message in a Group or PrivateChannel.");
      } else if (this.channel instanceof GuildMessageChannel) {
         return ((GuildMessageChannel)this.channel).clearReactionsById(this.getId(), emoji);
      } else {
         String encoded = EncodingUtil.encodeReaction(emoji.getAsReactionCode());
         Route.CompiledRoute route = Route.Messages.CLEAR_EMOJI_REACTIONS.compile(this.getChannelId(), this.getId(), encoded);
         return new RestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public RestAction<Void> removeReaction(@Nonnull Emoji emoji) {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot remove reactions from ephemeral messages.");
      } else if (this.hasChannel()) {
         return this.getChannel().removeReactionById(this.getId(), emoji);
      } else {
         String encoded = EncodingUtil.encodeReaction(emoji.getAsReactionCode());
         Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(this.getChannelId(), this.getId(), encoded, "@me");
         return new RestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public RestAction<Void> removeReaction(@Nonnull Emoji emoji, @Nonnull User user) {
      Checks.notNull(user, "User");
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot remove reactions from ephemeral messages.");
      } else if (user.equals(this.getJDA().getSelfUser())) {
         return this.removeReaction(emoji);
      } else if (!this.isFromGuild()) {
         throw new IllegalStateException("Cannot remove reactions of others from a message in a Group or PrivateChannel.");
      } else if (this.channel instanceof GuildMessageChannel) {
         return ((GuildMessageChannel)this.channel).removeReactionById(this.getIdLong(), emoji, user);
      } else {
         String encoded = EncodingUtil.encodeReaction(emoji.getAsReactionCode());
         Route.CompiledRoute route = Route.Messages.REMOVE_REACTION.compile(this.getChannelId(), this.getId(), encoded, user.getId());
         return new RestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public ReactionPaginationAction retrieveReactionUsers(@Nonnull Emoji emoji, @Nonnull MessageReaction.ReactionType type) {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot retrieve reactions on ephemeral messages.");
      } else if (this.hasChannel()) {
         return this.getChannel().retrieveReactionUsersById(this.id, emoji, type);
      } else {
         Checks.notNull(type, "ReactionType");
         Checks.notNull(emoji, "Emoji");
         return new ReactionPaginationActionImpl(this, emoji.getAsReactionCode(), type);
      }
   }

   @Nullable
   public MessageReaction getReaction(@Nonnull Emoji emoji) {
      Checks.notNull(emoji, "Emoji");
      String code = emoji.getAsReactionCode();
      return (MessageReaction)this.reactions.stream().filter((r) -> code.equals(r.getEmoji().getAsReactionCode())).findFirst().orElse((Object)null);
   }

   @Nonnull
   public MessageType getType() {
      return this.type;
   }

   @Nullable
   public Message.Interaction getInteraction() {
      return this.interaction;
   }

   @Nullable
   public Message.InteractionMetadata getInteractionMetadata() {
      return this.interactionMetadata;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getJumpUrl() {
      return Helpers.format("https://discord.com/channels/%s/%s/%s", this.isFromGuild() ? this.getGuildId() : "@me", this.getChannelId(), this.getId());
   }

   public boolean isEdited() {
      return this.editedTime != null;
   }

   public OffsetDateTime getTimeEdited() {
      return this.editedTime;
   }

   @Nonnull
   public User getAuthor() {
      return this.author;
   }

   public Member getMember() {
      return this.member;
   }

   public int getApproximatePosition() {
      if (!this.getChannelType().isThread()) {
         throw new IllegalStateException("This message was not sent in a thread.");
      } else {
         return this.position;
      }
   }

   @Nonnull
   public String getContentStripped() {
      if (this.strippedContent != null) {
         return this.strippedContent;
      } else {
         synchronized(this.mutex) {
            return this.strippedContent != null ? this.strippedContent : (this.strippedContent = MarkdownSanitizer.sanitize(this.getContentDisplay()));
         }
      }
   }

   @Nonnull
   public String getContentDisplay() {
      if (this.altContent != null) {
         return this.altContent;
      } else {
         synchronized(this.mutex) {
            if (this.altContent != null) {
               return this.altContent;
            } else {
               String tmp = this.getContentRaw();

               for(User user : this.mentions.getUsers()) {
                  String name;
                  if (this.hasGuild() && this.getGuild().isMember(user)) {
                     name = this.getGuild().getMember(user).getEffectiveName();
                  } else {
                     name = user.getName();
                  }

                  tmp = tmp.replaceAll("<@!?" + Pattern.quote(user.getId()) + '>', '@' + Matcher.quoteReplacement(name));
               }

               for(CustomEmoji emoji : this.mentions.getCustomEmojis()) {
                  tmp = tmp.replace(emoji.getAsMention(), ":" + emoji.getName() + ":");
               }

               for(GuildChannel mentionedChannel : this.mentions.getChannels()) {
                  tmp = tmp.replace(mentionedChannel.getAsMention(), '#' + mentionedChannel.getName());
               }

               for(Role mentionedRole : this.mentions.getRoles()) {
                  tmp = tmp.replace(mentionedRole.getAsMention(), '@' + mentionedRole.getName());
               }

               return this.altContent = tmp;
            }
         }
      }
   }

   @Nonnull
   public String getContentRaw() {
      this.checkIntent();
      return this.content;
   }

   @Nonnull
   public List<String> getInvites() {
      if (this.invites != null) {
         return this.invites;
      } else {
         synchronized(this.mutex) {
            if (this.invites != null) {
               return this.invites;
            } else {
               this.invites = new ArrayList();
               Matcher m = INVITE_PATTERN.matcher(this.getContentRaw());

               while(m.find()) {
                  this.invites.add(m.group(1));
               }

               return this.invites = Collections.unmodifiableList(this.invites);
            }
         }
      }
   }

   public String getNonce() {
      return this.nonce;
   }

   public boolean isFromType(@Nonnull ChannelType type) {
      return this.getChannelType() == type;
   }

   public boolean isFromGuild() {
      return this.guildId != 0L;
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channel == null ? ChannelType.UNKNOWN : this.getChannel().getType();
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      if (this.channel != null) {
         return (MessageChannelUnion)this.channel;
      } else {
         throw new IllegalStateException("Channel is unavailable in this context. Use getChannelIdLong() instead!");
      }
   }

   @Nonnull
   public GuildMessageChannelUnion getGuildChannel() {
      if (this.channel != null && !(this.channel instanceof GuildMessageChannelUnion)) {
         throw new IllegalStateException("This message was not sent in a guild.");
      } else {
         return (GuildMessageChannelUnion)this.getChannel();
      }
   }

   public Category getCategory() {
      Channel channel = this.channel;
      if (channel instanceof ThreadChannel) {
         channel = ((ThreadChannel)channel).getParentChannel();
      }

      return channel instanceof ICategorizableChannel ? ((ICategorizableChannel)channel).getParentCategory() : null;
   }

   public boolean hasGuild() {
      return this.guild != null;
   }

   public long getGuildIdLong() {
      return this.guildId;
   }

   @Nonnull
   public Guild getGuild() {
      if (this.guild == null) {
         ChannelType channelType = this.getChannelType();
         if (channelType != ChannelType.UNKNOWN && !channelType.isGuild()) {
            throw new IllegalStateException("This message was not sent in a guild");
         } else {
            throw new IllegalStateException("This message instance does not provide a guild instance! Use getGuildId() instead.");
         }
      } else {
         return this.guild;
      }
   }

   @Nonnull
   public List<Message.Attachment> getAttachments() {
      this.checkIntent();
      return this.attachments;
   }

   @Nonnull
   public List<MessageEmbed> getEmbeds() {
      this.checkIntent();
      return this.embeds;
   }

   @Nonnull
   public List<MessageTopLevelComponentUnion> getComponents() {
      this.checkIntent();
      return this.components;
   }

   public boolean isUsingComponentsV2() {
      return (this.flags & Message.MessageFlag.IS_COMPONENTS_V2.getValue()) != 0;
   }

   public MessagePoll getPoll() {
      this.checkIntent();
      return this.poll;
   }

   @Nonnull
   public AuditableRestAction<Message> endPoll() {
      this.checkUser();
      if (this.poll == null) {
         throw new IllegalStateException("This message does not contain a poll");
      } else {
         return new AuditableRestActionImpl<Message>(this.getJDA(), Route.Messages.END_POLL.compile(this.getChannelId(), this.getId()), (response, request) -> {
            JDAImpl jda = (JDAImpl)this.getJDA();
            EntityBuilder entityBuilder = jda.getEntityBuilder();
            return this.hasChannel() ? entityBuilder.createMessageWithChannel(response.getObject(), this.channel, false) : entityBuilder.createMessageFromWebhook(response.getObject(), this.hasGuild() ? this.getGuild() : null);
         });
      }
   }

   @Nonnull
   public Mentions getMentions() {
      return this.mentions;
   }

   @Nonnull
   public List<MessageReaction> getReactions() {
      return this.reactions;
   }

   @Nonnull
   public List<StickerItem> getStickers() {
      return this.stickers;
   }

   @Nonnull
   public List<MessageSnapshot> getMessageSnapshots() {
      return this.messageSnapshots;
   }

   public boolean isWebhookMessage() {
      return this.fromWebhook;
   }

   public long getApplicationIdLong() {
      return this.applicationId;
   }

   public boolean hasChannel() {
      return this.channel != null;
   }

   public long getChannelIdLong() {
      return this.channelId;
   }

   public boolean isTTS() {
      return this.isTTS;
   }

   @Nullable
   public MessageActivity getActivity() {
      return this.activity;
   }

   @Nonnull
   public MessageEditAction editMessage(@Nonnull CharSequence newContent) {
      MessageEditActionImpl action = this.editRequest();
      action.setContent(newContent.toString());
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return (MessageEditAction)action.setContent(newContent.toString());
      }
   }

   @Nonnull
   public MessageEditAction editMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      MessageEditActionImpl action = this.editRequest();
      action.setEmbeds(embeds);
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return action;
      }
   }

   @Nonnull
   public MessageEditAction editMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      MessageEditActionImpl action = this.editRequest();
      action.setComponents(components);
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return action;
      }
   }

   @Nonnull
   @FormatMethod
   public MessageEditAction editMessageFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      MessageEditActionImpl action = this.editRequest();
      action.setContent(String.format(format, args));
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return action;
      }
   }

   @Nonnull
   public MessageEditAction editMessageAttachments(@Nonnull Collection<? extends AttachedFile> attachments) {
      MessageEditActionImpl action = this.editRequest();
      action.setAttachments(attachments);
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return action;
      }
   }

   @Nonnull
   public MessageEditAction editMessage(@Nonnull MessageEditData newContent) {
      MessageEditActionImpl action = this.editRequest();
      action.applyData(newContent);
      if (this.isWebhookRequest()) {
         return action.withHook(this.webhook, this.getChannelType(), this.channelId);
      } else {
         this.checkSystem("edit");
         this.checkUser();
         return action;
      }
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      if (!this.type.canDelete()) {
         throw new IllegalStateException("Cannot delete messages of type " + this.type);
      } else if (this.isWebhookRequest()) {
         Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK_DELETE.compile(this.webhook.getId(), this.webhook.getToken(), this.getId());
         route = this.withThreadContext(route);
         AuditableRestActionImpl<Void> action = new AuditableRestActionImpl<Void>(this.getJDA(), route);
         action.setErrorMapper(this.getUnknownWebhookErrorMapper());
         return action;
      } else {
         SelfUser self = this.getJDA().getSelfUser();
         boolean isSelfAuthored = self.equals(this.getAuthor());
         if (!isSelfAuthored && !this.isFromGuild()) {
            throw new IllegalStateException("Cannot delete another User's messages in a PrivateChannel.");
         } else if (this.isEphemeral()) {
            throw new IllegalStateException("Cannot delete ephemeral messages.");
         } else {
            if (this.channel instanceof GuildMessageChannel && !isSelfAuthored) {
               GuildMessageChannel gChan = (GuildMessageChannel)this.channel;
               Member sMember = this.getGuild().getSelfMember();
               Checks.checkAccess(sMember, gChan);
               if (!sMember.hasPermission(gChan, new Permission[]{Permission.MESSAGE_MANAGE})) {
                  throw new InsufficientPermissionException(gChan, Permission.MESSAGE_MANAGE);
               }
            }

            Route.CompiledRoute route = Route.Messages.DELETE_MESSAGE.compile(this.getChannelId(), this.getId());
            return new AuditableRestActionImpl<Void>(this.getJDA(), route);
         }
      }
   }

   @Nonnull
   public AuditableRestAction<Void> suppressEmbeds(boolean suppressed) {
      SelfUser self = this.api.getSelfUser();
      Route.CompiledRoute route;
      if (this.isWebhookRequest()) {
         route = Route.Webhooks.EXECUTE_WEBHOOK_EDIT.compile(this.webhook.getId(), this.webhook.getToken(), this.getId());
         route = this.withThreadContext(route);
      } else {
         if (this.isEphemeral()) {
            throw new IllegalStateException("Cannot suppress embeds on ephemeral messages.");
         }

         if (!self.equals(this.getAuthor())) {
            if (!this.isFromGuild()) {
               throw new PermissionException("Cannot suppress embeds of others in a PrivateChannel.");
            }

            if (this.hasChannel()) {
               GuildMessageChannel gChan = this.getGuildChannel();
               if (!this.getGuild().getSelfMember().hasPermission(gChan, new Permission[]{Permission.MESSAGE_MANAGE})) {
                  throw new InsufficientPermissionException(gChan, Permission.MESSAGE_MANAGE);
               }
            }
         }

         route = Route.Messages.EDIT_MESSAGE.compile(this.getChannelId(), this.getId());
      }

      int newFlags = this.flags;
      int suppressionValue = Message.MessageFlag.EMBEDS_SUPPRESSED.getValue();
      if (suppressed) {
         newFlags |= suppressionValue;
      } else {
         newFlags &= ~suppressionValue;
      }

      DataObject body = DataObject.empty().put("flags", newFlags);
      AuditableRestActionImpl<Void> action = new AuditableRestActionImpl<Void>(this.api, route, body);
      action.setErrorMapper(this.getUnknownWebhookErrorMapper());
      return action;
   }

   @Nonnull
   public RestAction<Message> crosspost() {
      if (this.isEphemeral()) {
         throw new IllegalStateException("Cannot crosspost ephemeral messages.");
      } else if (this.getFlags().contains(Message.MessageFlag.CROSSPOSTED)) {
         return new CompletedRestAction<Message>(this.getJDA(), this);
      } else if (!this.hasChannel()) {
         Route.CompiledRoute route = Route.Messages.CROSSPOST_MESSAGE.compile(this.getChannelId(), this.getId());
         return new RestActionImpl<Message>(this.api, route, (response, request) -> this.api.getEntityBuilder().createMessageFromWebhook(response.getObject(), this.guild));
      } else {
         MessageChannelUnion channel = this.getChannel();
         if (!(channel instanceof NewsChannel)) {
            throw new IllegalStateException("This message was not sent in a news channel");
         } else {
            NewsChannel newsChannel = (NewsChannel)channel;
            Checks.checkAccess(this.getGuild().getSelfMember(), newsChannel);
            if (!this.getAuthor().equals(this.getJDA().getSelfUser()) && !this.getGuild().getSelfMember().hasPermission(newsChannel, new Permission[]{Permission.MESSAGE_MANAGE})) {
               throw new InsufficientPermissionException(newsChannel, Permission.MESSAGE_MANAGE);
            } else {
               return newsChannel.crosspostMessageById(this.getId());
            }
         }
      }
   }

   public boolean isSuppressedEmbeds() {
      return (this.flags & Message.MessageFlag.EMBEDS_SUPPRESSED.getValue()) > 0;
   }

   @Nonnull
   public EnumSet<Message.MessageFlag> getFlags() {
      return Message.MessageFlag.fromBitField(this.flags);
   }

   public long getFlagsRaw() {
      return (long)this.flags;
   }

   public boolean isEphemeral() {
      return (this.flags & Message.MessageFlag.EPHEMERAL.getValue()) != 0;
   }

   public boolean isSuppressedNotifications() {
      return (this.flags & Message.MessageFlag.NOTIFICATIONS_SUPPRESSED.getValue()) != 0;
   }

   public boolean isVoiceMessage() {
      return (this.flags & Message.MessageFlag.IS_VOICE_MESSAGE.getValue()) != 0;
   }

   @Nullable
   public ThreadChannel getStartedThread() {
      return this.startedThread;
   }

   public ThreadChannelAction createThreadChannel(@Nonnull String name) {
      return this.getGuildChannel().asThreadContainer().createThreadChannel(name, this.getIdLong());
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ReceivedMessage)) {
         return false;
      } else {
         ReceivedMessage oMsg = (ReceivedMessage)o;
         return this.id == oMsg.id;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("author", this.author.getDiscriminator().equals("0000") ? this.author.getName() : this.author.getAsTag()).addMetadata("content", String.format("%.20s ...", this)).toString();
   }

   public void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean upper = (flags & 2) == 2;
      boolean leftJustified = (flags & 1) == 1;
      boolean alt = (flags & 4) == 4;
      String out = alt ? this.getContentRaw() : this.getContentDisplay();
      if (upper) {
         out = out.toUpperCase(formatter.locale());
      }

      try {
         Appendable appendable = formatter.out();
         if (precision > -1 && out.length() > precision) {
            appendable.append(Helpers.truncate(out, precision - 3)).append("...");
         } else {
            if (leftJustified) {
               appendable.append(Helpers.rightPad(out, width));
            } else {
               appendable.append(Helpers.leftPad(out, width));
            }

         }
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   private boolean isWebhookRequest() {
      return this.webhook != null && (!(this.webhook instanceof InteractionHook) || !((InteractionHook)this.webhook).isExpired());
   }

   @Nonnull
   private MessageEditActionImpl editRequest() {
      MessageEditActionImpl messageEditAction = this.hasChannel() ? new MessageEditActionImpl(this.getChannel(), this.getId()) : new MessageEditActionImpl(this.getJDA(), this.hasGuild() ? this.getGuild() : null, this.getChannelId(), this.getId());
      messageEditAction.setErrorMapper(this.getUnknownWebhookErrorMapper());
      return messageEditAction;
   }

   private Route.CompiledRoute withThreadContext(Route.CompiledRoute route) {
      return this.getChannelType().isThread() && !(this.webhook instanceof InteractionHook) ? route.withQueryParams("thread_id", this.getChannelId()) : route;
   }

   private ErrorMapper getUnknownWebhookErrorMapper() {
      return !this.isWebhookRequest() ? null : (response, request, exception) -> this.webhook instanceof InteractionHookImpl && !((InteractionHookImpl)this.webhook).isAck() && exception.getErrorResponse() == ErrorResponse.UNKNOWN_WEBHOOK ? new IllegalStateException("Sending a webhook request requires the interaction to be acknowledged before expiration", exception) : null;
   }
}
