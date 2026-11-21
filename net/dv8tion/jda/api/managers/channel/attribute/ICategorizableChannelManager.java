package net.dv8tion.jda.api.managers.channel.attribute;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.managers.channel.ChannelManager;

public interface ICategorizableChannelManager<T extends ICategorizableChannel, M extends ICategorizableChannelManager<T, M>> extends ChannelManager<T, M>, IPermissionContainerManager<T, M>, IPositionableChannelManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setParent(@Nullable Category var1);

   @Nonnull
   @CheckReturnValue
   default M sync() {
      ICategorizableChannel categorizableChannel = (ICategorizableChannel)this.getChannel();
      if (categorizableChannel.getParentCategory() == null) {
         throw new IllegalStateException("sync() requires a parent category");
      } else {
         return (M)this.sync(categorizableChannel.getParentCategory());
      }
   }

   @Nonnull
   @CheckReturnValue
   M sync(@Nonnull IPermissionContainer var1);
}
