package net.dv8tion.jda.api.entities;

import java.time.OffsetDateTime;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ScheduledEventMembersPaginationAction;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public interface ScheduledEvent extends ISnowflake, Comparable<ScheduledEvent> {
   String JUMP_URL = "https://discord.com/events/%s/%s";
   int MAX_NAME_LENGTH = 100;
   int MAX_DESCRIPTION_LENGTH = 1000;
   int MAX_LOCATION_LENGTH = 100;
   /** @deprecated */
   @Deprecated
   String IMAGE_URL = "https://cdn.discordapp.com/guild-events/%s/%s.%s";

   @Nonnull
   String getName();

   @Nullable
   String getDescription();

   @Nullable
   String getCoverImageId();

   @Nullable
   String getImageUrl();

   @Nullable
   default String getCoverImageUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getCoverImage(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   default ImageProxy getImage() {
      String imageUrl = this.getImageUrl();
      return imageUrl == null ? null : new ImageProxy(imageUrl);
   }

   @Nullable
   default ImageProxy getCoverImage(@Nonnull ImageFormat format) {
      return DiscordAssets.scheduledEventCoverImage(format, this.getId(), this.getCoverImageId());
   }

   @Nullable
   User getCreator();

   long getCreatorIdLong();

   @Nullable
   default String getCreatorId() {
      return this.getCreatorIdLong() == 0L ? null : Long.toUnsignedString(this.getCreatorIdLong());
   }

   @Nonnull
   Status getStatus();

   @Nonnull
   Type getType();

   @Nonnull
   OffsetDateTime getStartTime();

   @Nullable
   OffsetDateTime getEndTime();

   @Nullable
   GuildChannelUnion getChannel();

   @Nonnull
   String getLocation();

   @Nonnull
   String getJumpUrl();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   ScheduledEventMembersPaginationAction retrieveInterestedMembers();

   int getInterestedUserCount();

   @Nonnull
   Guild getGuild();

   @Nonnull
   default JDA getJDA() {
      return this.getGuild().getJDA();
   }

   @Nonnull
   @CheckReturnValue
   ScheduledEventManager getManager();

   int compareTo(@Nonnull ScheduledEvent var1);

   public static enum Status {
      UNKNOWN(-1),
      SCHEDULED(1),
      ACTIVE(2),
      COMPLETED(3),
      CANCELED(4);

      private final int key;

      private Status(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static Status fromKey(int key) {
         for(Status status : values()) {
            if (status.getKey() == key) {
               return status;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{UNKNOWN, SCHEDULED, ACTIVE, COMPLETED, CANCELED};
      }
   }

   public static enum Type {
      UNKNOWN(-1),
      STAGE_INSTANCE(1),
      VOICE(2),
      EXTERNAL(3);

      private final int key;

      private Type(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      public boolean isChannel() {
         return this == STAGE_INSTANCE || this == VOICE;
      }

      @Nonnull
      public static Type fromKey(int key) {
         for(Type type : values()) {
            if (type.getKey() == key) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{UNKNOWN, STAGE_INSTANCE, VOICE, EXTERNAL};
      }
   }
}
