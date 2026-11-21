package net.dv8tion.jda.api.entities;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Formattable;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.entities.messages.MessageSnapshot;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PollVotersPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.NamedAttachmentProxy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.MessageChannelMixin;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.PollVotersPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.MultipartBody;
import org.jetbrains.annotations.Unmodifiable;

public interface Message extends ISnowflake, Formattable {
   String JUMP_URL = "https://discord.com/channels/%s/%s/%s";
   int MAX_FILE_SIZE = 10485760;
   int MAX_FILE_AMOUNT = 10;
   int MAX_CONTENT_LENGTH = 2000;
   int MAX_CONTENT_LENGTH_COMPONENT_V2 = 4000;
   int MAX_REACTIONS = 20;
   int MAX_EMBED_COUNT = 10;
   int MAX_STICKER_COUNT = 3;
   int MAX_COMPONENT_COUNT = 5;
   int MAX_COMPONENT_COUNT_IN_COMPONENT_TREE = 40;
   int MAX_NONCE_LENGTH = 25;
   Pattern INVITE_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:(?:app)?\\.com/invite[/\\\\]|\\.gg/)(?<code>[a-z0-9-]+)(?:\\?\\S*)?(?:#\\S*)?", 2);
   Pattern JUMP_URL_PATTERN = Pattern.compile("(?:https?://)?(?:\\w+\\.)?discord(?:app)?\\.com/channels/(?<guild>\\d+)/(?<channel>\\d+)/(?<message>\\d+)(?:\\?\\S*)?(?:#\\S*)?", 2);

   static void suppressContentIntentWarning() {
      ReceivedMessage.didContentIntentWarning = true;
   }

   @Nullable
   MessageReference getMessageReference();

   @Nullable
   default Message getReferencedMessage() {
      return this.getMessageReference() != null ? this.getMessageReference().getMessage() : null;
   }

   @Nonnull
   Mentions getMentions();

   boolean isEdited();

   @Nullable
   OffsetDateTime getTimeEdited();

   @Nonnull
   User getAuthor();

   @Nullable
   Member getMember();

   int getApproximatePosition();

   @Nonnull
   String getJumpUrl();

   @Nonnull
   String getContentDisplay();

   @Nonnull
   String getContentRaw();

   @Nonnull
   String getContentStripped();

   @Nonnull
   @Unmodifiable List<String> getInvites();

   @Nullable
   String getNonce();

   boolean isFromType(@Nonnull ChannelType var1);

   boolean isFromGuild();

   @Nonnull
   ChannelType getChannelType();

   boolean isWebhookMessage();

   @Nullable
   default String getApplicationId() {
      return this.getApplicationIdLong() == 0L ? null : Long.toUnsignedString(this.getApplicationIdLong());
   }

   long getApplicationIdLong();

   boolean hasChannel();

   long getChannelIdLong();

   @Nonnull
   default String getChannelId() {
      return Long.toUnsignedString(this.getChannelIdLong());
   }

   @Nonnull
   MessageChannelUnion getChannel();

   @Nonnull
   GuildMessageChannelUnion getGuildChannel();

   @Nullable
   Category getCategory();

   boolean hasGuild();

   long getGuildIdLong();

   @Nullable
   default String getGuildId() {
      return this.isFromGuild() ? Long.toUnsignedString(this.getGuildIdLong()) : null;
   }

   @Nonnull
   Guild getGuild();

   @Nonnull
   @Unmodifiable List<Attachment> getAttachments();

   @Nonnull
   @Unmodifiable List<MessageEmbed> getEmbeds();

   @Nonnull
   @Unmodifiable List<MessageTopLevelComponentUnion> getComponents();

   boolean isUsingComponentsV2();

   @Nonnull
   default MessageComponentTree getComponentTree() {
      return MessageComponentTree.of(this.getComponents());
   }

   @Nullable
   MessagePoll getPoll();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Message> endPoll();

   @Nonnull
   @CheckReturnValue
   default PollVotersPaginationAction retrievePollVoters(long answerId) {
      return new PollVotersPaginationActionImpl(this.getJDA(), this.getChannelId(), this.getId(), answerId);
   }

   @Nonnull
   @Unmodifiable List<MessageReaction> getReactions();

   @Nonnull
   @Unmodifiable List<StickerItem> getStickers();

   @Nonnull
   @Unmodifiable List<MessageSnapshot> getMessageSnapshots();

   boolean isTTS();

   @Nullable
   MessageActivity getActivity();

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessage(@Nonnull CharSequence var1);

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessage(@Nonnull MessageEditData var1);

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> var1);

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageEmbeds(@Nonnull MessageEmbed... embeds) {
      Checks.noneNull(embeds, "MessageEmbeds");
      return this.editMessageEmbeds(Arrays.asList(embeds));
   }

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> var1);

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponents(@Nonnull MessageTopLevelComponent... components) {
      Checks.noneNull(components, "Components");
      return this.editMessageComponents(Arrays.asList(components));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editMessageComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessageFormat(@Nonnull String var1, @Nonnull Object... var2);

   @Nonnull
   @CheckReturnValue
   MessageEditAction editMessageAttachments(@Nonnull Collection<? extends AttachedFile> var1);

   @Nonnull
   @CheckReturnValue
   default MessageEditAction editMessageAttachments(@Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return this.editMessageAttachments(Arrays.asList(attachments));
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyStickers(@Nonnull Collection<? extends StickerSnowflake> stickers) {
      return this.getGuildChannel().sendStickers(stickers).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyStickers(@Nonnull StickerSnowflake... stickers) {
      return this.getGuildChannel().sendStickers(stickers).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction reply(@Nonnull CharSequence content) {
      return this.getChannel().sendMessage(content).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction reply(@Nonnull MessageCreateData msg) {
      return this.getChannel().sendMessage(msg).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyPoll(@Nonnull MessagePollData poll) {
      return this.getChannel().sendMessagePoll(poll).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... other) {
      Checks.notNull(embed, "MessageEmbeds");
      Checks.noneNull(other, "MessageEmbeds");
      List<MessageEmbed> embeds = new ArrayList(1 + other.length);
      embeds.add(embed);
      Collections.addAll(embeds, other);
      return this.replyEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return this.getChannel().sendMessageEmbeds(embeds).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "MessageTopLevelComponents");
      return this.getChannel().sendMessageComponents(components).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyComponents(@Nonnull MessageTopLevelComponent component, @Nonnull MessageTopLevelComponent... other) {
      Checks.notNull(component, "MessageTopLevelComponents");
      Checks.noneNull(other, "MessageTopLevelComponents");
      List<MessageTopLevelComponent> components = new ArrayList(1 + other.length);
      components.add(component);
      Collections.addAll(components, other);
      return this.replyComponents(components);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.replyComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default MessageCreateAction replyFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      return this.getChannel().sendMessageFormat(format, args).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyFiles(@Nonnull FileUpload... files) {
      return this.getChannel().sendFiles(files).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction replyFiles(@Nonnull Collection<? extends FileUpload> files) {
      return this.getChannel().sendFiles(files).setMessageReference(this);
   }

   @Nonnull
   @CheckReturnValue
   default MessageCreateAction forwardTo(@Nonnull MessageChannel channel) {
      Checks.notNull(channel, "Target channel");
      if (channel instanceof MessageChannelMixin) {
         ((MessageChannelMixin)channel).checkCanSendMessage();
      }

      return (new MessageCreateActionImpl(channel)).setMessageReference(MessageReference.MessageReferenceType.FORWARD, this);
   }

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   JDA getJDA();

   boolean isPinned();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> pin();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> unpin();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> addReaction(@Nonnull Emoji var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> clearReactions();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> clearReactions(@Nonnull Emoji var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> removeReaction(@Nonnull Emoji var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Void> removeReaction(@Nonnull Emoji var1, @Nonnull User var2);

   @Nonnull
   @CheckReturnValue
   default ReactionPaginationAction retrieveReactionUsers(@Nonnull Emoji emoji) {
      return this.retrieveReactionUsers(emoji, MessageReaction.ReactionType.NORMAL);
   }

   @Nonnull
   @CheckReturnValue
   ReactionPaginationAction retrieveReactionUsers(@Nonnull Emoji var1, @Nonnull MessageReaction.ReactionType var2);

   @Nullable
   @CheckReturnValue
   MessageReaction getReaction(@Nonnull Emoji var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> suppressEmbeds(boolean var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Message> crosspost();

   boolean isSuppressedEmbeds();

   @Nonnull
   EnumSet<MessageFlag> getFlags();

   long getFlagsRaw();

   boolean isEphemeral();

   boolean isSuppressedNotifications();

   boolean isVoiceMessage();

   @Nullable
   ThreadChannel getStartedThread();

   @Nonnull
   MessageType getType();

   /** @deprecated */
   @Nullable
   @Deprecated
   Interaction getInteraction();

   @Nullable
   InteractionMetadata getInteractionMetadata();

   @Nonnull
   @CheckReturnValue
   ThreadChannelAction createThreadChannel(@Nonnull String var1);

   public static enum MentionType {
      USER("<@!?(\\d+)>", "users"),
      ROLE("<@&(\\d+)>", "roles"),
      CHANNEL("<#(\\d+)>", (String)null),
      EMOJI("<a?:([a-zA-Z0-9_]+):([0-9]+)>", (String)null),
      HERE("@here", "everyone"),
      EVERYONE("@everyone", "everyone"),
      SLASH_COMMAND("</([\\w-]+)(?> ([\\w-]+))??(?> ([\\w-]+))?:(\\d+)>", (String)null);

      private final Pattern pattern;
      private final String parseKey;

      private MentionType(String regex, String parseKey) {
         this.pattern = Pattern.compile(regex);
         this.parseKey = parseKey;
      }

      @Nonnull
      public Pattern getPattern() {
         return this.pattern;
      }

      @Nullable
      public String getParseKey() {
         return this.parseKey;
      }

      // $FF: synthetic method
      private static MentionType[] $values() {
         return new MentionType[]{USER, ROLE, CHANNEL, EMOJI, HERE, EVERYONE, SLASH_COMMAND};
      }
   }

   public static enum MessageFlag {
      CROSSPOSTED(0),
      IS_CROSSPOST(1),
      EMBEDS_SUPPRESSED(2),
      SOURCE_MESSAGE_DELETED(3),
      URGENT(4),
      EPHEMERAL(6),
      LOADING(7),
      NOTIFICATIONS_SUPPRESSED(12),
      IS_VOICE_MESSAGE(13),
      IS_COMPONENTS_V2(15);

      private final int value;

      private MessageFlag(int offset) {
         this.value = 1 << offset;
      }

      public int getValue() {
         return this.value;
      }

      @Nonnull
      public static EnumSet<MessageFlag> fromBitField(int bitfield) {
         Set<MessageFlag> set = (Set)Arrays.stream(values()).filter((e) -> (e.value & bitfield) > 0).collect(Collectors.toSet());
         return set.isEmpty() ? EnumSet.noneOf(MessageFlag.class) : EnumSet.copyOf(set);
      }

      public static int toBitField(@Nonnull Collection<MessageFlag> coll) {
         Checks.notNull(coll, "Collection");
         int flags = 0;

         for(MessageFlag messageFlag : coll) {
            flags |= messageFlag.value;
         }

         return flags;
      }

      // $FF: synthetic method
      private static MessageFlag[] $values() {
         return new MessageFlag[]{CROSSPOSTED, IS_CROSSPOST, EMBEDS_SUPPRESSED, SOURCE_MESSAGE_DELETED, URGENT, EPHEMERAL, LOADING, NOTIFICATIONS_SUPPRESSED, IS_VOICE_MESSAGE, IS_COMPONENTS_V2};
      }
   }

   public static class Attachment implements ISnowflake, AttachedFile {
      private static final Set<String> IMAGE_EXTENSIONS = new HashSet(Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "tiff", "svg", "apng"));
      private static final Set<String> VIDEO_EXTENSIONS = new HashSet(Arrays.asList("webm", "flv", "vob", "avi", "mov", "wmv", "amv", "mp4", "mpg", "mpeg", "gifv"));
      private final long id;
      private final String url;
      private final String proxyUrl;
      private final String fileName;
      private final String contentType;
      private final String description;
      private final int size;
      private final int height;
      private final int width;
      private final boolean ephemeral;
      private final String waveform;
      private final double duration;
      private final JDAImpl jda;

      public Attachment(long id, String url, String proxyUrl, String fileName, String contentType, String description, int size, int height, int width, boolean ephemeral, String waveform, double duration, JDAImpl jda) {
         this.id = id;
         this.url = url;
         this.proxyUrl = proxyUrl;
         this.fileName = fileName;
         this.contentType = contentType;
         this.description = description;
         this.size = size;
         this.height = height;
         this.width = width;
         this.ephemeral = ephemeral;
         this.waveform = waveform;
         this.duration = duration;
         this.jda = jda;
      }

      @Nonnull
      public JDA getJDA() {
         return this.jda;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getUrl() {
         return this.url;
      }

      @Nonnull
      public String getProxyUrl() {
         return this.proxyUrl;
      }

      @Nonnull
      public NamedAttachmentProxy getProxy() {
         return new NamedAttachmentProxy(this.width > 0 && this.height > 0 ? this.proxyUrl : this.url, this.fileName);
      }

      @Nonnull
      public String getFileName() {
         return this.fileName;
      }

      @Nullable
      public String getFileExtension() {
         int index = this.fileName.lastIndexOf(46) + 1;
         return index != 0 && index != this.fileName.length() ? this.fileName.substring(index) : null;
      }

      @Nullable
      public String getContentType() {
         return this.contentType;
      }

      @Nullable
      public String getDescription() {
         return this.description;
      }

      public int getSize() {
         return this.size;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      public boolean isEphemeral() {
         return this.ephemeral;
      }

      @Nullable
      public byte[] getWaveform() {
         return this.waveform == null ? null : Base64.getDecoder().decode(this.waveform);
      }

      public double getDuration() {
         return this.duration;
      }

      public boolean isImage() {
         if (this.width < 0) {
            return false;
         } else {
            String extension = this.getFileExtension();
            return extension != null && IMAGE_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT));
         }
      }

      public boolean isVideo() {
         if (this.width < 0) {
            return false;
         } else {
            String extension = this.getFileExtension();
            return extension != null && VIDEO_EXTENSIONS.contains(extension.toLowerCase(Locale.ROOT));
         }
      }

      public boolean isSpoiler() {
         return this.getFileName().startsWith("SPOILER_");
      }

      public void close() {
      }

      public void forceClose() {
      }

      public void addPart(@Nonnull MultipartBody.Builder builder, int index) {
      }

      @Nonnull
      public DataObject toAttachmentData(int index) {
         return DataObject.empty().put("id", this.id);
      }
   }

   /** @deprecated */
   @Deprecated
   public static class Interaction implements ISnowflake {
      private final long id;
      private final int type;
      private final String name;
      private final User user;
      private final Member member;

      public Interaction(long id, int type, String name, User user, Member member) {
         this.id = id;
         this.type = type;
         this.name = name;
         this.user = user;
         this.member = member;
      }

      public long getIdLong() {
         return this.id;
      }

      public int getTypeRaw() {
         return this.type;
      }

      @Nonnull
      public InteractionType getType() {
         return InteractionType.fromKey(this.getTypeRaw());
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public User getUser() {
         return this.user;
      }

      @Nullable
      public Member getMember() {
         return this.member;
      }
   }

   public static class InteractionMetadata implements ISnowflake {
      private final long id;
      private final int type;
      private final User user;
      private final IntegrationOwners integrationOwners;
      private final long originalResponseMessageId;
      private final long interactedMessageId;
      private final InteractionMetadata triggeringInteraction;
      private final User targetUser;
      private final long targetMessageId;

      public InteractionMetadata(long id, int type, User user, IntegrationOwners integrationOwners, long originalResponseMessageId, long interactedMessageId, InteractionMetadata triggeringInteraction, User targetUser, long targetMessageId) {
         this.id = id;
         this.type = type;
         this.user = user;
         this.integrationOwners = integrationOwners;
         this.originalResponseMessageId = originalResponseMessageId;
         this.interactedMessageId = interactedMessageId;
         this.triggeringInteraction = triggeringInteraction;
         this.targetUser = targetUser;
         this.targetMessageId = targetMessageId;
      }

      public long getIdLong() {
         return this.id;
      }

      public int getTypeRaw() {
         return this.type;
      }

      @Nonnull
      public InteractionType getType() {
         return InteractionType.fromKey(this.type);
      }

      @Nonnull
      public User getUser() {
         return this.user;
      }

      @Nonnull
      public IntegrationOwners getIntegrationOwners() {
         return this.integrationOwners;
      }

      public long getOriginalResponseMessageIdLong() {
         return this.originalResponseMessageId;
      }

      @Nullable
      public String getOriginalResponseMessageId() {
         return this.originalResponseMessageId == 0L ? null : Long.toUnsignedString(this.originalResponseMessageId);
      }

      public long getInteractedMessageIdLong() {
         return this.interactedMessageId;
      }

      @Nullable
      public String getInteractedMessageId() {
         return this.interactedMessageId == 0L ? null : Long.toUnsignedString(this.interactedMessageId);
      }

      @Nullable
      public InteractionMetadata getTriggeringInteraction() {
         return this.triggeringInteraction;
      }

      @Nullable
      public User getTargetUser() {
         return this.targetUser;
      }

      public long getTargetMessageIdLong() {
         return this.targetMessageId;
      }

      @Nullable
      public String getTargetMessageId() {
         return this.targetMessageId == 0L ? null : Long.toUnsignedString(this.targetMessageId);
      }
   }
}
