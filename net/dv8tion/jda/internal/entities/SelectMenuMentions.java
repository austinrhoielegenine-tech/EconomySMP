package net.dv8tion.jda.internal.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
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
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandReference;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BagUtils;
import org.apache.commons.collections4.bag.HashBag;

public class SelectMenuMentions implements Mentions {
   private final DataObject resolved;
   private final JDAImpl jda;
   private final InteractionEntityBuilder interactionEntityBuilder;
   private final Guild guild;
   private final List<String> values;
   private List<User> cachedUsers;
   private List<Member> cachedMembers;
   private List<Role> cachedRoles;
   private List<GuildChannel> cachedChannels;

   public SelectMenuMentions(JDAImpl jda, InteractionEntityBuilder interactionEntityBuilder, @Nullable Guild guild, DataObject resolved, DataArray values) {
      this.jda = jda;
      this.interactionEntityBuilder = interactionEntityBuilder;
      this.guild = guild;
      this.resolved = resolved;
      this.values = (List)values.stream(DataArray::getString).collect(Collectors.toList());
   }

   @Nonnull
   public JDA getJDA() {
      return this.jda;
   }

   public boolean mentionsEveryone() {
      return false;
   }

   @Nonnull
   public List<User> getUsers() {
      if (this.cachedUsers != null) {
         return this.cachedUsers;
      } else {
         DataObject userMap = (DataObject)this.resolved.optObject("users").orElseGet(DataObject::empty);
         EntityBuilder builder = this.jda.getEntityBuilder();
         Stream var10001 = this.values.stream().map((id) -> (DataObject)userMap.optObject(id).orElse((Object)null)).filter(Objects::nonNull);
         Objects.requireNonNull(builder);
         return this.cachedUsers = (List)var10001.map(builder::createUser).collect(Helpers.toUnmodifiableList());
      }
   }

   @Nonnull
   public Bag<User> getUsersBag() {
      return new HashBag<User>(this.getUsers());
   }

   @Nonnull
   public List<GuildChannel> getChannels() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else if (this.cachedChannels != null) {
         return this.cachedChannels;
      } else {
         DataObject channelMap = (DataObject)this.resolved.optObject("channels").orElseGet(DataObject::empty);
         return this.cachedChannels = (List)this.values.stream().map((id) -> (DataObject)channelMap.optObject(id).orElse((Object)null)).filter(Objects::nonNull).map((json) -> {
            ChannelType channelType = ChannelType.fromId(json.getInt("type", -1));
            if (!this.guild.isDetached()) {
               return this.guild.getGuildChannelById(channelType, json.getUnsignedLong("id"));
            } else {
               return (GuildChannel)(channelType.isThread() ? this.interactionEntityBuilder.createThreadChannel(this.guild, json) : this.interactionEntityBuilder.createGuildChannel(this.guild, json));
            }
         }).filter(Objects::nonNull).collect(Helpers.toUnmodifiableList());
      }
   }

   @Nonnull
   public Bag<GuildChannel> getChannelsBag() {
      return new HashBag<GuildChannel>(this.getChannels());
   }

   @Nonnull
   public <T extends GuildChannel> List<T> getChannels(@Nonnull Class<T> clazz) {
      Stream var10000 = this.getChannels().stream();
      Objects.requireNonNull(clazz);
      var10000 = var10000.filter(clazz::isInstance);
      Objects.requireNonNull(clazz);
      return (List)var10000.map(clazz::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public <T extends GuildChannel> Bag<T> getChannelsBag(@Nonnull Class<T> clazz) {
      return new HashBag<T>(this.getChannels(clazz));
   }

   @Nonnull
   public List<Role> getRoles() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else if (this.cachedRoles != null) {
         return this.cachedRoles;
      } else {
         DataObject roleMap = (DataObject)this.resolved.optObject("roles").orElseGet(DataObject::empty);
         Stream var10001 = this.values.stream();
         Objects.requireNonNull(roleMap);
         var10001 = var10001.filter(roleMap::hasKey);
         Objects.requireNonNull(roleMap);
         return this.cachedRoles = (List)var10001.map(roleMap::getObject).map((json) -> !this.guild.isDetached() ? this.guild.getRoleById(json.getUnsignedLong("id")) : this.interactionEntityBuilder.createRole(this.guild, json)).filter(Objects::nonNull).collect(Helpers.toUnmodifiableList());
      }
   }

   @Nonnull
   public Bag<Role> getRolesBag() {
      return new HashBag<Role>(this.getRoles());
   }

   @Nonnull
   public List<CustomEmoji> getCustomEmojis() {
      return Collections.emptyList();
   }

   @Nonnull
   public Bag<CustomEmoji> getCustomEmojisBag() {
      return BagUtils.<CustomEmoji>emptyBag();
   }

   @Nonnull
   public List<SlashCommandReference> getSlashCommands() {
      return Collections.emptyList();
   }

   @Nonnull
   public Bag<SlashCommandReference> getSlashCommandsBag() {
      return BagUtils.<SlashCommandReference>emptyBag();
   }

   @Nonnull
   public List<Member> getMembers() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else if (this.cachedMembers != null) {
         return this.cachedMembers;
      } else {
         DataObject memberMap = (DataObject)this.resolved.optObject("members").orElseGet(DataObject::empty);
         DataObject userMap = (DataObject)this.resolved.optObject("users").orElseGet(DataObject::empty);
         return this.cachedMembers = (List)this.values.stream().map((id) -> (DataObject)memberMap.optObject(id).map((m) -> m.put("id", id)).orElse((Object)null)).filter(Objects::nonNull).map((json) -> json.put("user", userMap.getObject(json.getString("id")))).map((json) -> this.interactionEntityBuilder.createMember(this.guild, json)).filter(Objects::nonNull).filter((member) -> {
            if (!member.isDetached()) {
               this.jda.getEntityBuilder().updateMemberCache((MemberImpl)member);
            }

            return true;
         }).collect(Helpers.toUnmodifiableList());
      }
   }

   @Nonnull
   public Bag<Member> getMembersBag() {
      return new HashBag<Member>(this.getMembers());
   }

   @Nonnull
   public List<IMentionable> getMentions(@Nonnull Message.MentionType... types) {
      if (types.length == 0) {
         return this.getMentions(Message.MentionType.values());
      } else {
         List<IMentionable> mentions = new ArrayList();

         for(Message.MentionType type : EnumSet.of(types[0], types)) {
            switch (type) {
               case USER:
                  List<Member> members = this.getMembers();
                  List<User> users = this.getUsers();
                  mentions.addAll(members);
                  Stream var10000 = users.stream().filter((u) -> members.stream().noneMatch((m) -> m.getIdLong() == u.getIdLong()));
                  Objects.requireNonNull(mentions);
                  var10000.forEach(mentions::add);
                  break;
               case ROLE:
                  mentions.addAll(this.getRoles());
                  break;
               case CHANNEL:
                  mentions.addAll(this.getChannels());
            }
         }

         mentions.sort(Comparator.comparingInt((it) -> this.values.indexOf(it.getId())));
         return Collections.unmodifiableList(mentions);
      }
   }

   public boolean isMentioned(@Nonnull IMentionable mentionable, @Nonnull Message.MentionType... types) {
      Checks.notNull(types, "Mention Types");
      if (types.length == 0) {
         return this.isMentioned(mentionable, Message.MentionType.values());
      } else {
         String id = mentionable.getId();

         for(Message.MentionType type : types) {
            switch (type) {
               case USER:
                  if (mentionable instanceof UserSnowflake) {
                     boolean mentioned = (Boolean)this.resolved.optObject("users").map((obj) -> obj.hasKey(id)).orElse(false);
                     if (mentioned) {
                        return true;
                     }
                  }
                  break;
               case ROLE:
                  if (mentionable instanceof Member) {
                     boolean mentioned = ((Member)mentionable).getUnsortedRoles().stream().anyMatch((role) -> this.isMentioned(role, Message.MentionType.ROLE));
                     if (mentioned) {
                        return true;
                     }
                  } else if (mentionable instanceof User) {
                     boolean mentioned = (Boolean)this.getMembers().stream().filter((it) -> it.getIdLong() == mentionable.getIdLong()).findFirst().map((member) -> this.isMentioned(member, Message.MentionType.ROLE)).orElse(false);
                     if (mentioned) {
                        return true;
                     }
                  } else if (mentionable instanceof Role) {
                     boolean mentioned = (Boolean)this.resolved.optObject("roles").map((obj) -> obj.hasKey(id)).orElse(false);
                     if (mentioned) {
                        return true;
                     }
                  }
                  break;
               case CHANNEL:
                  if (mentionable instanceof GuildChannel && this.getChannels().contains(mentionable)) {
                     return true;
                  }
            }
         }

         return false;
      }
   }
}
