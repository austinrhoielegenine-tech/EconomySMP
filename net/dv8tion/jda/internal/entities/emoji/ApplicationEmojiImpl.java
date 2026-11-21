package net.dv8tion.jda.internal.entities.emoji;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.managers.ApplicationEmojiManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.ApplicationEmojiManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;

public class ApplicationEmojiImpl implements ApplicationEmoji, EmojiUnion {
   private final long id;
   private final JDAImpl api;
   private final User owner;
   boolean animated = false;
   private String name;

   public ApplicationEmojiImpl(long id, JDAImpl api, User owner) {
      this.id = id;
      this.api = api;
      this.owner = owner;
   }

   @Nonnull
   public Emoji.Type getType() {
      return Emoji.Type.CUSTOM;
   }

   @Nonnull
   public String getAsReactionCode() {
      return this.name + ":" + this.id;
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("name", this.name).put("animated", this.animated).put("id", this.id);
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nullable
   public User getOwner() {
      return this.owner;
   }

   @Nonnull
   public ApplicationEmojiManager getManager() {
      return new ApplicationEmojiManagerImpl(this);
   }

   public boolean isAnimated() {
      return this.animated;
   }

   @Nonnull
   public RestAction<Void> delete() {
      Route.CompiledRoute route = Route.Applications.DELETE_APPLICATION_EMOJI.compile(this.getJDA().getSelfUser().getApplicationId(), this.getId());
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   public ApplicationEmojiImpl setName(String name) {
      this.name = name;
      return this;
   }

   public ApplicationEmojiImpl setAnimated(boolean animated) {
      this.animated = animated;
      return this;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ApplicationEmojiImpl)) {
         return false;
      } else {
         ApplicationEmojiImpl other = (ApplicationEmojiImpl)obj;
         return this.id == other.getIdLong();
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }

   @Nonnull
   public UnicodeEmoji asUnicode() {
      throw new IllegalStateException("Cannot convert ApplicationEmoji to UnicodeEmoji!");
   }

   @Nonnull
   public CustomEmoji asCustom() {
      return this;
   }

   @Nonnull
   public RichCustomEmoji asRich() {
      throw new IllegalStateException("Cannot convert ApplicationEmoji to RichCustomEmoji!");
   }

   @Nonnull
   public ApplicationEmoji asApplication() {
      return this;
   }
}
