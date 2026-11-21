package net.dv8tion.jda.api.entities.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.channel.attribute.ICategorizableChannelManager;

public interface ICategorizableChannel extends GuildChannel, IPermissionContainer, IPositionableChannel {
   @Nonnull
   @CheckReturnValue
   ICategorizableChannelManager<?, ?> getManager();

   default int getPositionInCategory() {
      Category parent = this.getParentCategory();
      return parent == null ? -1 : parent.getChannels().indexOf(this);
   }

   long getParentCategoryIdLong();

   @Nullable
   default String getParentCategoryId() {
      long parentID = this.getParentCategoryIdLong();
      return parentID == 0L ? null : Long.toUnsignedString(parentID);
   }

   @Nullable
   default Category getParentCategory() {
      return this.getGuild().getCategoryById(this.getParentCategoryIdLong());
   }

   boolean isSynced();
}
