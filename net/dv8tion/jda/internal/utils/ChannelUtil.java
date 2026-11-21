package net.dv8tion.jda.internal.utils;

import java.util.EnumSet;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class ChannelUtil {
   public static final EnumSet<ChannelType> SLOWMODE_SUPPORTED;
   public static final EnumSet<ChannelType> NSFW_SUPPORTED;
   public static final EnumSet<ChannelType> TOPIC_SUPPORTED;
   public static final EnumSet<ChannelType> POST_CONTAINERS;
   public static final EnumSet<ChannelType> THREAD_CONTAINERS;

   public static <T extends Channel> T safeChannelCast(Object instance, Class<T> toObjectClass) {
      return (T)(UnionUtil.safeUnionCast("channel", instance, toObjectClass));
   }

   public static int compare(GuildChannel a, GuildChannel b) {
      Checks.notNull(b, "Channel");
      ThreadChannel thisThread = a instanceof ThreadChannel ? (ThreadChannel)a : null;
      ThreadChannel otherThread = b instanceof ThreadChannel ? (ThreadChannel)b : null;
      if (thisThread != null && otherThread == null) {
         return thisThread.getParentChannel().getIdLong() == b.getIdLong() ? 1 : thisThread.getParentChannel().compareTo(b);
      } else if (thisThread == null && otherThread != null) {
         return otherThread.getParentChannel().getIdLong() == a.getIdLong() ? -1 : a.compareTo(otherThread.getParentChannel());
      } else if (thisThread != null) {
         return thisThread.getParentChannel().getIdLong() == otherThread.getParentChannel().getIdLong() ? Long.compare(b.getIdLong(), a.getIdLong()) : thisThread.getParentChannel().compareTo(otherThread.getParentChannel());
      } else {
         Category thisParent = a instanceof ICategorizableChannel ? ((ICategorizableChannel)a).getParentCategory() : null;
         Category otherParent = b instanceof ICategorizableChannel ? ((ICategorizableChannel)b).getParentCategory() : null;
         if (thisParent != null && otherParent == null) {
            if (b instanceof Category) {
               return b.getIdLong() == thisParent.getIdLong() ? 1 : thisParent.compareTo(b);
            } else {
               return 1;
            }
         } else if (thisParent == null && otherParent != null) {
            if (a instanceof Category) {
               return a.getIdLong() == otherParent.getIdLong() ? -1 : a.compareTo(otherParent);
            } else {
               return -1;
            }
         } else if (thisParent != null && !thisParent.equals(otherParent)) {
            return thisParent.compareTo(otherParent);
         } else if (a.getType().getSortBucket() != b.getType().getSortBucket()) {
            return Integer.compare(a.getType().getSortBucket(), b.getType().getSortBucket());
         } else {
            if (b instanceof IPositionableChannel && a instanceof IPositionableChannel) {
               IPositionableChannel oPositionableChannel = (IPositionableChannel)b;
               IPositionableChannel thisPositionableChannel = (IPositionableChannel)a;
               if (thisPositionableChannel.getPositionRaw() != oPositionableChannel.getPositionRaw()) {
                  return Integer.compare(thisPositionableChannel.getPositionRaw(), oPositionableChannel.getPositionRaw());
               }
            }

            return Long.compareUnsigned(a.getIdLong(), b.getIdLong());
         }
      }
   }

   static {
      SLOWMODE_SUPPORTED = EnumSet.of(ChannelType.TEXT, ChannelType.FORUM, ChannelType.MEDIA, ChannelType.GUILD_PUBLIC_THREAD, ChannelType.GUILD_NEWS_THREAD, ChannelType.GUILD_PRIVATE_THREAD, ChannelType.STAGE, ChannelType.VOICE);
      NSFW_SUPPORTED = EnumSet.of(ChannelType.TEXT, ChannelType.VOICE, ChannelType.FORUM, ChannelType.MEDIA, ChannelType.NEWS, ChannelType.STAGE);
      TOPIC_SUPPORTED = EnumSet.of(ChannelType.TEXT, ChannelType.FORUM, ChannelType.MEDIA, ChannelType.NEWS);
      POST_CONTAINERS = EnumSet.of(ChannelType.FORUM, ChannelType.MEDIA);
      THREAD_CONTAINERS = EnumSet.of(ChannelType.TEXT, ChannelType.NEWS, ChannelType.FORUM, ChannelType.MEDIA);
   }
}
