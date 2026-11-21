package net.dv8tion.jda.internal.entities.mentions;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.commands.ICommandReference;
import net.dv8tion.jda.api.interactions.commands.SlashCommandReference;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.bag.HashBag;

public abstract class AbstractMentions implements Mentions {
   protected final String content;
   protected final JDAImpl jda;
   @Nullable
   protected final Guild guild;
   protected final boolean mentionsEveryone;
   protected List<User> mentionedUsers;
   protected List<Member> mentionedMembers;
   protected List<Role> mentionedRoles;
   protected List<GuildChannel> mentionedChannels;
   protected List<CustomEmoji> mentionedEmojis;
   protected List<SlashCommandReference> mentionedSlashCommands;

   public AbstractMentions(String content, JDAImpl jda, @Nullable Guild guild, boolean mentionsEveryone) {
      this.content = content;
      this.jda = jda;
      this.guild = guild;
      this.mentionsEveryone = mentionsEveryone;
   }

   @Nonnull
   public JDA getJDA() {
      return this.jda;
   }

   public boolean mentionsEveryone() {
      return this.mentionsEveryone;
   }

   @Nonnull
   public synchronized List<User> getUsers() {
      return this.mentionedUsers != null ? this.mentionedUsers : (this.mentionedUsers = (List)this.processMentions(Message.MentionType.USER, true, this::matchUser, Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public Bag<User> getUsersBag() {
      Bag<User> bag = (Bag)this.processMentions(Message.MentionType.USER, false, this::matchUser, toBag());

      for(User user : this.getUsers()) {
         if (!bag.contains(user)) {
            bag.add(user, 1);
         }
      }

      return bag;
   }

   @Nonnull
   public synchronized List<GuildChannel> getChannels() {
      return this.mentionedChannels != null ? this.mentionedChannels : (this.mentionedChannels = (List)this.processMentions(Message.MentionType.CHANNEL, true, this::matchChannel, Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public Bag<GuildChannel> getChannelsBag() {
      return (Bag)this.processMentions(Message.MentionType.CHANNEL, false, this::matchChannel, toBag());
   }

   @Nonnull
   public <T extends GuildChannel> List<T> getChannels(@Nonnull Class<T> clazz) {
      Checks.notNull(clazz, "clazz");
      Stream var10000 = this.getChannels().stream();
      Objects.requireNonNull(clazz);
      var10000 = var10000.filter(clazz::isInstance);
      Objects.requireNonNull(clazz);
      return (List)var10000.map(clazz::cast).collect(Collectors.toList());
   }

   @Nonnull
   public <T extends GuildChannel> Bag<T> getChannelsBag(@Nonnull Class<T> clazz) {
      Checks.notNull(clazz, "clazz");
      Function<Matcher, T> matchTypedChannel = (matcher) -> {
         GuildChannel channel = this.matchChannel(matcher);
         return clazz.isInstance(channel) ? (GuildChannel)clazz.cast(channel) : null;
      };
      return (Bag)this.processMentions(Message.MentionType.CHANNEL, false, matchTypedChannel, toBag());
   }

   @Nonnull
   public synchronized List<Role> getRoles() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else {
         return this.mentionedRoles != null ? this.mentionedRoles : (this.mentionedRoles = (List)this.processMentions(Message.MentionType.ROLE, true, this::matchRole, Helpers.toUnmodifiableList()));
      }
   }

   @Nonnull
   public Bag<Role> getRolesBag() {
      return (Bag<Role>)(this.guild == null ? new HashBag() : (Bag)this.processMentions(Message.MentionType.ROLE, false, this::matchRole, toBag()));
   }

   @Nonnull
   public synchronized List<CustomEmoji> getCustomEmojis() {
      return this.mentionedEmojis != null ? this.mentionedEmojis : (this.mentionedEmojis = (List)this.processMentions(Message.MentionType.EMOJI, true, this::matchEmoji, Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public Bag<CustomEmoji> getCustomEmojisBag() {
      return (Bag)this.processMentions(Message.MentionType.EMOJI, false, this::matchEmoji, toBag());
   }

   @Nonnull
   public synchronized List<Member> getMembers() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else {
         return this.mentionedMembers != null ? this.mentionedMembers : (this.mentionedMembers = (List)this.processMentions(Message.MentionType.USER, true, this::matchMember, Helpers.toUnmodifiableList()));
      }
   }

   @Nonnull
   public Bag<Member> getMembersBag() {
      if (this.guild == null) {
         return new HashBag<Member>();
      } else {
         Bag<Member> bag = (Bag)this.processMentions(Message.MentionType.USER, false, this::matchMember, toBag());

         for(Member member : this.getMembers()) {
            if (!bag.contains(member)) {
               bag.add(member, 1);
            }
         }

         return bag;
      }
   }

   @Nonnull
   public synchronized List<SlashCommandReference> getSlashCommands() {
      return this.mentionedSlashCommands != null ? this.mentionedSlashCommands : (this.mentionedSlashCommands = (List)this.processMentions(Message.MentionType.SLASH_COMMAND, true, this::matchSlashCommand, Helpers.toUnmodifiableList()));
   }

   @Nonnull
   public Bag<SlashCommandReference> getSlashCommandsBag() {
      return (Bag)this.processMentions(Message.MentionType.SLASH_COMMAND, false, this::matchSlashCommand, toBag());
   }

   @Nonnull
   public List<IMentionable> getMentions(@Nonnull Message.MentionType... types) {
      if (types != null && types.length != 0) {
         List<IMentionable> mentions = new ArrayList();

         for(Message.MentionType type : EnumSet.of(types[0], types)) {
            switch (type) {
               case CHANNEL:
                  mentions.addAll(this.getChannels());
                  break;
               case USER:
                  TLongObjectMap<IMentionable> set = new TLongObjectHashMap<IMentionable>();

                  for(User u : this.getUsers()) {
                     set.put(u.getIdLong(), u);
                  }

                  for(Member m : this.getMembers()) {
                     set.put(m.getIdLong(), m);
                  }

                  mentions.addAll(set.valueCollection());
                  break;
               case ROLE:
                  mentions.addAll(this.getRoles());
                  break;
               case EMOJI:
                  mentions.addAll(this.getCustomEmojis());
                  break;
               case SLASH_COMMAND:
                  mentions.addAll(this.getSlashCommands());
            }
         }

         mentions.sort(Comparator.comparingInt((it) -> this.content.indexOf(it.getId())));
         return Collections.unmodifiableList(mentions);
      } else {
         return this.getMentions(Message.MentionType.values());
      }
   }

   public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull Message.MentionType... types) {
      Checks.notNull(types, "Mention Types");
      if (types.length == 0) {
         return this.isMentioned(mentionable, Message.MentionType.values());
      } else {
         for(Message.MentionType type : types) {
            switch (type) {
               case CHANNEL:
                  if (mentionable instanceof GuildChannel && this.getChannels().contains(mentionable)) {
                     return true;
                  }
                  break;
               case USER:
                  if (this.isUserMentioned(mentionable)) {
                     return true;
                  }
                  break;
               case ROLE:
                  if (this.isRoleMentioned(mentionable)) {
                     return true;
                  }
                  break;
               case EMOJI:
                  if (mentionable instanceof CustomEmoji && this.getCustomEmojis().contains(mentionable)) {
                     return true;
                  }
                  break;
               case SLASH_COMMAND:
                  if (this.isSlashCommandMentioned(mentionable)) {
                     return true;
                  }
                  break;
               case HERE:
                  if (this.isMass("@here") && mentionable instanceof UserSnowflake) {
                     return true;
                  }
                  break;
               case EVERYONE:
                  if (this.isMass("@everyone") && mentionable instanceof UserSnowflake) {
                     return true;
                  }
            }
         }

         return false;
      }
   }

   protected <T, A, C extends Collection<T>> C processMentions(Message.MentionType type, boolean distinct, Function<Matcher, ? extends T> mapping, Collector<? super T, A, C> collector) {
      A accumulator = (A)collector.supplier().get();
      Matcher matcher = type.getPattern().matcher(this.content);
      Set<T> unique = distinct ? new HashSet() : null;

      while(matcher.find()) {
         try {
            T elem = (T)mapping.apply(matcher);
            if (elem != null && (unique == null || unique.add(elem))) {
               collector.accumulator().accept(accumulator, elem);
            }
         } catch (NumberFormatException var9) {
         }
      }

      return (C)(collector.finisher().apply(accumulator));
   }

   protected static <T> Collector<T, ?, HashBag<T>> toBag() {
      return Collectors.toCollection(HashBag::new);
   }

   protected abstract User matchUser(Matcher var1);

   protected abstract Member matchMember(Matcher var1);

   protected abstract GuildChannel matchChannel(Matcher var1);

   protected abstract Role matchRole(Matcher var1);

   protected CustomEmoji matchEmoji(Matcher m) {
      long emojiId = MiscUtil.parseSnowflake(m.group(2));
      String name = m.group(1);
      boolean animated = m.group(0).startsWith("<a:");
      CustomEmoji emoji = this.getJDA().getEmojiById(emojiId);
      if (emoji == null) {
         emoji = Emoji.fromCustom(name, emojiId, animated);
      }

      return emoji;
   }

   protected SlashCommandReference matchSlashCommand(Matcher matcher) {
      return new SlashCommandReference(matcher.group(1), matcher.group(2), matcher.group(3), Long.parseLong(matcher.group(4)));
   }

   protected abstract boolean isUserMentioned(IMentionable var1);

   protected boolean isRoleMentioned(IMentionable mentionable) {
      if (mentionable instanceof Role) {
         return this.getRoles().contains(mentionable);
      } else {
         Member member = null;
         if (mentionable instanceof Member) {
            member = (Member)mentionable;
         } else if (this.guild != null && mentionable instanceof User) {
            member = this.guild.getMember((User)mentionable);
         }

         return member != null && CollectionUtils.containsAny(this.getRoles(), member.getUnsortedRoles());
      }
   }

   protected boolean isSlashCommandMentioned(IMentionable mentionable) {
      if (mentionable instanceof ICommandReference) {
         ICommandReference reference = (ICommandReference)mentionable;

         for(SlashCommandReference r : this.getSlashCommands()) {
            if (r.getFullCommandName().equals(reference.getFullCommandName()) && r.getIdLong() == reference.getIdLong()) {
               return true;
            }
         }
      }

      return false;
   }

   protected boolean isMass(String s) {
      return this.mentionsEveryone && this.content.contains(s);
   }
}
