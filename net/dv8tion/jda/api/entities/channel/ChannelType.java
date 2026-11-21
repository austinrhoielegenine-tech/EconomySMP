package net.dv8tion.jda.api.entities.channel;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public enum ChannelType {
   TEXT(TextChannel.class, 0, 0, true),
   PRIVATE(PrivateChannel.class, 1, -1),
   VOICE(VoiceChannel.class, 2, 1, true),
   GROUP(GroupChannel.class, 3, -1),
   CATEGORY(Category.class, 4, 2, true),
   NEWS(NewsChannel.class, 5, 0, true),
   STAGE(StageChannel.class, 13, 1, true),
   GUILD_NEWS_THREAD(ThreadChannel.class, 10, -1, true),
   GUILD_PUBLIC_THREAD(ThreadChannel.class, 11, -1, true),
   GUILD_PRIVATE_THREAD(ThreadChannel.class, 12, -1, true),
   GUILD_DIRECTORY(Channel.class, 14, -1, false),
   FORUM(ForumChannel.class, 15, 0, true),
   MEDIA(MediaChannel.class, 16, 0, true),
   UNKNOWN(Channel.class, -1, -2);

   private final int sortBucket;
   private final int id;
   private final boolean isGuild;
   private final Class<? extends Channel> clazz;

   private ChannelType(Class<? extends Channel> clazz, int id, int sortBucket) {
      this(clazz, id, sortBucket, false);
   }

   private ChannelType(Class<? extends Channel> clazz, int id, int sortBucket, boolean isGuild) {
      this.clazz = clazz;
      this.id = id;
      this.sortBucket = sortBucket;
      this.isGuild = isGuild;
   }

   @Nonnull
   public Class<? extends Channel> getInterface() {
      return this.clazz;
   }

   public int getSortBucket() {
      return this.sortBucket;
   }

   public int getId() {
      return this.id;
   }

   public boolean isGuild() {
      return this.isGuild;
   }

   public boolean isAudio() {
      switch (this.ordinal()) {
         case 2:
         case 6:
            return true;
         default:
            return false;
      }
   }

   public boolean isMessage() {
      switch (this.ordinal()) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 5:
         case 6:
            return true;
         case 4:
         default:
            return this.isThread();
      }
   }

   public boolean isThread() {
      switch (this.ordinal()) {
         case 7:
         case 8:
         case 9:
            return true;
         default:
            return false;
      }
   }

   @Nonnull
   public static EnumSet<ChannelType> guildTypes() {
      return EnumSet.complementOf(EnumSet.of(PRIVATE, GROUP, UNKNOWN));
   }

   @Nonnull
   public static ChannelType fromId(int id) {
      for(ChannelType type : values()) {
         if (type.id == id) {
            return type;
         }
      }

      return UNKNOWN;
   }

   @Nonnull
   public static EnumSet<ChannelType> fromSortBucket(int bucket) {
      EnumSet<ChannelType> types = EnumSet.noneOf(ChannelType.class);

      for(ChannelType type : values()) {
         if (type.getSortBucket() == bucket) {
            types.add(type);
         }
      }

      return types;
   }

   // $FF: synthetic method
   private static ChannelType[] $values() {
      return new ChannelType[]{TEXT, PRIVATE, VOICE, GROUP, CATEGORY, NEWS, STAGE, GUILD_NEWS_THREAD, GUILD_PUBLIC_THREAD, GUILD_PRIVATE_THREAD, GUILD_DIRECTORY, FORUM, MEDIA, UNKNOWN};
   }
}
