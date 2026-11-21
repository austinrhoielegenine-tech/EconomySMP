package net.dv8tion.jda.internal.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class ActivityImpl implements Activity {
   protected final String name;
   protected final String url;
   protected final String state;
   protected final Activity.ActivityType type;
   protected final Activity.Timestamps timestamps;
   protected final EmojiUnion emoji;

   protected ActivityImpl(String name) {
      this(name, (String)null, Activity.ActivityType.PLAYING);
   }

   protected ActivityImpl(String name, String url) {
      this(name, url, Activity.ActivityType.STREAMING);
   }

   protected ActivityImpl(String name, String url, Activity.ActivityType type) {
      this(name, (String)null, url, type, (Activity.Timestamps)null, (EmojiUnion)null);
   }

   protected ActivityImpl(String name, String state, String url, Activity.ActivityType type) {
      this(name, state, url, type, (Activity.Timestamps)null, (EmojiUnion)null);
   }

   protected ActivityImpl(String name, String state, String url, Activity.ActivityType type, Activity.Timestamps timestamps, EmojiUnion emoji) {
      this.name = name;
      this.state = state;
      this.url = url;
      this.type = type;
      this.timestamps = timestamps;
      this.emoji = emoji;
   }

   public boolean isRich() {
      return false;
   }

   public RichPresence asRichPresence() {
      return null;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getState() {
      return this.state;
   }

   public String getUrl() {
      return this.url;
   }

   @Nonnull
   public Activity.ActivityType getType() {
      return this.type;
   }

   @Nullable
   public Activity.Timestamps getTimestamps() {
      return this.timestamps;
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return this.emoji;
   }

   @Nonnull
   public Activity withState(@Nullable String state) {
      if (state != null) {
         state = state.trim();
         if (state.isEmpty()) {
            state = null;
         } else {
            Checks.notLonger(state, 128, "State");
         }
      }

      return new ActivityImpl(this.name, state, this.url, this.type);
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ActivityImpl)) {
         return false;
      } else {
         ActivityImpl oGame = (ActivityImpl)o;
         return oGame.getType() == this.type && Objects.equals(this.name, oGame.getName()) && Objects.equals(this.state, oGame.state) && Objects.equals(this.url, oGame.getUrl()) && Objects.equals(this.timestamps, oGame.timestamps);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.state, this.type, this.url, this.timestamps});
   }

   public String toString() {
      EntityString entityString = (new EntityString(this)).setType(this.type).setName(this.name);
      if (this.url != null) {
         entityString.addMetadata("url", this.url);
      }

      return entityString.toString();
   }
}
