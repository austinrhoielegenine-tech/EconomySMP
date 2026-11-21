package net.dv8tion.jda.api.entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class MessageActivity {
   private final ActivityType type;
   private final String partyId;
   private final Application application;

   public MessageActivity(ActivityType type, String partyId, Application application) {
      this.type = type;
      this.partyId = partyId;
      this.application = application;
   }

   @Nonnull
   public ActivityType getType() {
      return this.type;
   }

   @Nullable
   public String getPartyId() {
      return this.partyId;
   }

   @Nullable
   public Application getApplication() {
      return this.application;
   }

   public static class Application implements ISnowflake {
      private final String name;
      private final String description;
      private final String iconId;
      private final String coverId;
      private final long id;

      public Application(String name, String description, String iconId, String coverId, long id) {
         this.name = name;
         this.description = description;
         this.iconId = iconId;
         this.coverId = coverId;
         this.id = id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public String getDescription() {
         return this.description;
      }

      @Nullable
      public String getIconId() {
         return this.iconId;
      }

      @Nullable
      public String getIconUrl() {
         return this.getIconUrl(ImageFormat.PNG);
      }

      @Nullable
      public String getIconUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getIcon(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      public ImageProxy getIcon() {
         String iconUrl = this.getIconUrl();
         return iconUrl == null ? null : new ImageProxy(iconUrl);
      }

      @Nullable
      public ImageProxy getIcon(@Nonnull ImageFormat format) {
         return DiscordAssets.applicationIcon(format, this.getId(), this.getIconId());
      }

      @Nullable
      public String getCoverId() {
         return this.coverId;
      }

      @Nullable
      public String getCoverUrl() {
         return this.getCoverUrl(ImageFormat.PNG);
      }

      @Nullable
      public String getCoverUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getCover(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      public ImageProxy getCover() {
         String coverUrl = this.getCoverUrl();
         return coverUrl == null ? null : new ImageProxy(coverUrl);
      }

      @Nullable
      public ImageProxy getCover(@Nonnull ImageFormat format) {
         return DiscordAssets.applicationCover(format, this.getId(), this.getCoverId());
      }

      public long getIdLong() {
         return this.id;
      }
   }

   public static enum ActivityType {
      JOIN(1),
      SPECTATE(2),
      LISTENING(3),
      JOIN_REQUEST(5),
      UNKNOWN(-1);

      private final int id;

      private ActivityType(int id) {
         this.id = id;
      }

      public int getId() {
         return this.id;
      }

      @Nonnull
      public static ActivityType fromId(int id) {
         for(ActivityType activityType : values()) {
            if (activityType.id == id) {
               return activityType;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static ActivityType[] $values() {
         return new ActivityType[]{JOIN, SPECTATE, LISTENING, JOIN_REQUEST, UNKNOWN};
      }
   }
}
