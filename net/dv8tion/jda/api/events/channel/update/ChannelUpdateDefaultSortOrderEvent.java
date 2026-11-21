package net.dv8tion.jda.api.events.channel.update;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;

public class ChannelUpdateDefaultSortOrderEvent extends GenericChannelUpdateEvent<IPostContainer.SortOrder> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;

   public ChannelUpdateDefaultSortOrderEvent(@Nonnull JDA api, long responseNumber, IPostContainer channel, IPostContainer.SortOrder oldValue) {
      super(api, responseNumber, channel, ChannelField.DEFAULT_SORT_ORDER, oldValue, channel.getDefaultSortOrder());
   }

   @Nonnull
   public IPostContainer.SortOrder getOldValue() {
      return (IPostContainer.SortOrder)super.getOldValue();
   }

   @Nonnull
   public IPostContainer.SortOrder getNewValue() {
      return (IPostContainer.SortOrder)super.getNewValue();
   }

   static {
      FIELD = ChannelField.DEFAULT_SORT_ORDER;
      IDENTIFIER = FIELD.getFieldName();
   }
}
