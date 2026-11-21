package net.dv8tion.jda.api.events.channel.update;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelField;
import net.dv8tion.jda.internal.utils.Helpers;

public class ChannelUpdateArchiveTimestampEvent extends GenericChannelUpdateEvent<OffsetDateTime> {
   public static final ChannelField FIELD;
   public static final String IDENTIFIER;
   private final long oldTimestamp;
   private final long newTimestamp;

   public ChannelUpdateArchiveTimestampEvent(@Nonnull JDA api, long responseNumber, Channel channel, long oldValue, long newValue) {
      super(api, responseNumber, channel, FIELD, (Object)null, (Object)null);
      this.oldTimestamp = oldValue;
      this.newTimestamp = newValue;
   }

   @Nonnull
   public OffsetDateTime getOldValue() {
      return Helpers.toOffset(this.oldTimestamp);
   }

   @Nonnull
   public OffsetDateTime getNewValue() {
      return Helpers.toOffset(this.newTimestamp);
   }

   static {
      FIELD = ChannelField.ARCHIVED_TIMESTAMP;
      IDENTIFIER = FIELD.getFieldName();
   }
}
