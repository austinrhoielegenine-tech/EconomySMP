package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import java.util.regex.Matcher;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.mentions.AbstractMentions;

public class InteractionMentions extends AbstractMentions {
   protected final TLongObjectMap<Object> resolved;

   public InteractionMentions(String content, TLongObjectMap<Object> resolved, JDAImpl jda, @Nullable Guild guild) {
      super(content, jda, guild, false);
      this.resolved = resolved;
   }

   protected User matchUser(Matcher matcher) {
      long userId = MiscUtil.parseSnowflake(matcher.group(1));
      Object it = this.resolved.get(userId);
      return it instanceof User ? (User)it : (it instanceof Member ? ((Member)it).getUser() : null);
   }

   protected Member matchMember(Matcher matcher) {
      long userId = MiscUtil.parseSnowflake(matcher.group(1));
      Object it = this.resolved.get(userId);
      return it instanceof Member ? (Member)it : null;
   }

   protected GuildChannel matchChannel(Matcher matcher) {
      long channelId = MiscUtil.parseSnowflake(matcher.group(1));
      Object it = this.resolved.get(channelId);
      return it instanceof GuildChannel ? (GuildChannel)it : null;
   }

   protected Role matchRole(Matcher matcher) {
      long roleId = MiscUtil.parseSnowflake(matcher.group(1));
      Object it = this.resolved.get(roleId);
      return it instanceof Role ? (Role)it : null;
   }

   protected boolean isUserMentioned(IMentionable mentionable) {
      return this.resolved.containsKey(mentionable.getIdLong()) && (this.content.contains("<@!" + mentionable.getId() + ">") || this.content.contains("<@" + mentionable.getId() + ">"));
   }
}
