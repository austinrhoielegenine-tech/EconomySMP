package net.dv8tion.jda.internal.entities;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.mentions.AbstractMentions;

public class MessageMentionsImpl extends AbstractMentions {
   private final TLongObjectMap<DataObject> userMentionMap;
   private final TLongSet roleMentionMap;

   public MessageMentionsImpl(JDAImpl jda, GuildImpl guild, String content, boolean mentionsEveryone, DataArray userMentions, DataArray roleMentions) {
      super(content, jda, guild, mentionsEveryone);
      this.userMentionMap = new TLongObjectHashMap<DataObject>(userMentions.length());
      this.roleMentionMap = new TLongHashSet((Collection)roleMentions.stream(DataArray::getUnsignedLong).collect(Collectors.toList()));
      userMentions.stream(DataArray::getObject).forEach((obj) -> {
         if (obj.isNull("member")) {
            this.userMentionMap.put(obj.getUnsignedLong("id"), obj.put("is_member", false));
         } else {
            DataObject member = obj.getObject("member");
            obj.remove("member");
            member.put("user", obj).put("is_member", true);
            this.userMentionMap.put(obj.getUnsignedLong("id"), member);
         }
      });
      this.getMembers();
   }

   @Nonnull
   public synchronized List<Member> getMembers() {
      if (this.guild == null) {
         return Collections.emptyList();
      } else if (this.mentionedMembers != null) {
         return this.mentionedMembers;
      } else {
         EntityBuilder entityBuilder = this.jda.getEntityBuilder();
         TLongSet unseen = new TLongHashSet(this.userMentionMap.keySet());
         List<Member> members = (List)this.processMentions(Message.MentionType.USER, false, (matcher) -> unseen.remove(Long.parseUnsignedLong(matcher.group(1))) ? this.matchMember(matcher) : null, Collectors.toCollection(ArrayList::new));
         TLongIterator iter = unseen.iterator();

         while(iter.hasNext()) {
            DataObject mention = this.userMentionMap.get(iter.next());
            if (mention.getBoolean("is_member")) {
               members.add(0, entityBuilder.createMember((GuildImpl)this.guild, mention));
            }
         }

         Stream var10000 = members.stream();
         Objects.requireNonNull(MemberImpl.class);
         var10000 = var10000.map(MemberImpl.class::cast);
         Objects.requireNonNull(entityBuilder);
         var10000.forEach(entityBuilder::updateMemberCache);
         return this.mentionedMembers = Collections.unmodifiableList(members);
      }
   }

   @Nonnull
   public synchronized List<User> getUsers() {
      if (this.mentionedUsers != null) {
         return this.mentionedUsers;
      } else {
         EntityBuilder entityBuilder = this.jda.getEntityBuilder();
         TLongSet unseen = new TLongHashSet(this.userMentionMap.keySet());
         List<User> users = (List)this.processMentions(Message.MentionType.USER, false, (matcher) -> unseen.remove(Long.parseUnsignedLong(matcher.group(1))) ? this.matchUser(matcher) : null, Collectors.toCollection(ArrayList::new));
         TLongIterator iter = unseen.iterator();

         while(iter.hasNext()) {
            DataObject mention = this.userMentionMap.get(iter.next());
            if (mention.getBoolean("is_member")) {
               users.add(0, entityBuilder.createUser(mention.getObject("user")));
            } else {
               users.add(0, entityBuilder.createUser(mention));
            }
         }

         return this.mentionedUsers = Collections.unmodifiableList(users);
      }
   }

   protected User matchUser(Matcher matcher) {
      long userId = MiscUtil.parseSnowflake(matcher.group(1));
      DataObject mention = this.userMentionMap.get(userId);
      if (mention == null) {
         return null;
      } else if (!mention.getBoolean("is_member")) {
         return this.jda.getEntityBuilder().createUser(mention);
      } else {
         Member member = this.matchMember(matcher);
         return member == null ? null : member.getUser();
      }
   }

   protected Member matchMember(Matcher matcher) {
      long id = Long.parseUnsignedLong(matcher.group(1));
      DataObject member = this.userMentionMap.get(id);
      return member != null && member.getBoolean("is_member") ? this.jda.getEntityBuilder().createMember((GuildImpl)this.guild, member) : null;
   }

   protected GuildChannel matchChannel(Matcher matcher) {
      long channelId = MiscUtil.parseSnowflake(matcher.group(1));
      return this.getJDA().getGuildChannelById(channelId);
   }

   protected Role matchRole(Matcher matcher) {
      long roleId = MiscUtil.parseSnowflake(matcher.group(1));
      if (!this.roleMentionMap.contains(roleId)) {
         return null;
      } else {
         return this.guild != null ? this.guild.getRoleById(roleId) : this.getJDA().getRoleById(roleId);
      }
   }

   protected boolean isUserMentioned(IMentionable mentionable) {
      return this.userMentionMap.containsKey(mentionable.getIdLong());
   }
}
