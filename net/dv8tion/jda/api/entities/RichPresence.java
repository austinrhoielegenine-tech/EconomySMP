package net.dv8tion.jda.api.entities;

import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;

public interface RichPresence extends Activity {
   long getApplicationIdLong();

   @Nonnull
   String getApplicationId();

   @Nullable
   String getSessionId();

   @Nullable
   String getSyncId();

   int getFlags();

   @Nonnull
   EnumSet<ActivityFlag> getFlagSet();

   @Nullable
   String getDetails();

   @Nullable
   Party getParty();

   @Nullable
   Image getLargeImage();

   @Nullable
   Image getSmallImage();

   public static class Image {
      protected final String key;
      protected final String text;
      protected final String applicationId;

      public Image(long applicationId, String key, String text) {
         this.applicationId = Long.toUnsignedString(applicationId);
         this.key = key;
         this.text = text;
      }

      @Nonnull
      public String getKey() {
         return this.key;
      }

      @Nullable
      public String getText() {
         return this.text;
      }

      @Nonnull
      public String getUrl() {
         if (this.key.startsWith("spotify:")) {
            return "https://i.scdn.co/image/" + this.key.substring("spotify:".length());
         } else {
            return this.key.startsWith("twitch:") ? String.format("https://static-cdn.jtvnw.net/previews-ttv/live_user_%s-1920x1080.png", this.key.substring("twitch:".length())) : "https://cdn.discordapp.com/app-assets/" + this.applicationId + "/" + this.key + ".png";
         }
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("key", this.getKey()).addMetadata("text", this.getText()).toString();
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Image)) {
            return false;
         } else {
            Image i = (Image)obj;
            return Objects.equals(this.key, i.key) && Objects.equals(this.text, i.text);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.key, this.text});
      }
   }

   public static class Party {
      protected final String id;
      protected final long size;
      protected final long max;

      public Party(String id, long size, long max) {
         this.id = id;
         this.size = size;
         this.max = max;
      }

      @Nullable
      public String getId() {
         return this.id;
      }

      public long getSize() {
         return this.size;
      }

      public long getMax() {
         return this.max;
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("id", this.getId()).addMetadata("size", this.getSize()).addMetadata("max", this.getMax()).toString();
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Party)) {
            return false;
         } else {
            Party p = (Party)obj;
            return this.size == p.size && this.max == p.max && Objects.equals(this.id, p.id);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.id, this.size, this.max});
      }
   }
}
