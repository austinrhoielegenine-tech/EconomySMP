package net.dv8tion.jda.api.entities.channel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

public class VoiceChannelEffect {
   private final VoiceChannel channel;
   private final long userId;
   private final EmojiUnion emoji;
   private final Animation animation;
   private final SoundboardSound soundboardSound;
   private final double soundVolume;

   public VoiceChannelEffect(VoiceChannel channel, long userId, EmojiUnion emoji, Animation animation, SoundboardSound soundboardSound, double soundVolume) {
      this.channel = channel;
      this.userId = userId;
      this.emoji = emoji;
      this.animation = animation;
      this.soundboardSound = soundboardSound;
      this.soundVolume = soundVolume;
   }

   @Nonnull
   public VoiceChannel getChannel() {
      return this.channel;
   }

   @Nonnull
   public String getUserId() {
      return Long.toUnsignedString(this.userId);
   }

   public long getUserIdLong() {
      return this.userId;
   }

   @Nullable
   public User getUser() {
      return this.channel.getJDA().getUserById(this.userId);
   }

   @Nullable
   public Member getMember() {
      return this.channel.getGuild().getMemberById(this.userId);
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return this.emoji;
   }

   @Nullable
   public Animation getAnimation() {
      return this.animation;
   }

   @Nullable
   public SoundboardSound getSoundboardSound() {
      return this.soundboardSound;
   }

   public double getSoundVolume() {
      return this.soundVolume;
   }

   public static class Animation implements ISnowflake {
      private final long id;
      private final Type type;

      public Animation(long id, Type type) {
         this.id = id;
         this.type = type;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public Type getType() {
         return this.type;
      }

      public static enum Type {
         UNKNOWN(-1),
         PREMIUM(0),
         BASIC(1);

         private final int value;

         private Type(int value) {
            this.value = value;
         }

         public int getValue() {
            return this.value;
         }

         @Nonnull
         public static Type fromValue(int value) {
            for(Type type : values()) {
               if (type.value == value) {
                  return type;
               }
            }

            return UNKNOWN;
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{UNKNOWN, PREMIUM, BASIC};
         }
      }
   }
}
