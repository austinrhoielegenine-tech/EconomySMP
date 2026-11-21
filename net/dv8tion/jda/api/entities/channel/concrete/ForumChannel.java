package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ForumChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface ForumChannel extends StandardGuildChannel, IPostContainer, IWebhookContainer, IAgeRestrictedChannel, ISlowmodeChannel {
   int MAX_FORUM_TOPIC_LENGTH = 4096;
   int MAX_POST_TAGS = 5;

   @Nonnull
   default ChannelType getType() {
      return ChannelType.FORUM;
   }

   @Nonnull
   @CheckReturnValue
   ForumChannelManager getManager();

   @Nonnull
   @CheckReturnValue
   ChannelAction<ForumChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<ForumChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   Layout getDefaultLayout();

   public static enum Layout {
      DEFAULT_VIEW(0),
      LIST_VIEW(1),
      GALLERY_VIEW(2),
      UNKNOWN(-1);

      private final int key;

      private Layout(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static Layout fromKey(int key) {
         for(Layout layout : values()) {
            if (layout.key == key) {
               return layout;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Layout[] $values() {
         return new Layout[]{DEFAULT_VIEW, LIST_VIEW, GALLERY_VIEW, UNKNOWN};
      }
   }
}
