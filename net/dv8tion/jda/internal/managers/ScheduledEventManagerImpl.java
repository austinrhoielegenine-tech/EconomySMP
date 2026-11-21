package net.dv8tion.jda.internal.managers;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.ScheduledEventImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import okhttp3.RequestBody;

public class ScheduledEventManagerImpl extends ManagerBase<ScheduledEventManager> implements ScheduledEventManager {
   protected ScheduledEventImpl event;
   protected String name;
   protected String description;
   protected long channelId;
   protected String location;
   protected Icon image;
   protected OffsetDateTime startTime;
   protected OffsetDateTime endTime;
   protected ScheduledEvent.Type entityType;
   protected ScheduledEvent.Status status;

   public ScheduledEventManagerImpl(ScheduledEventImpl event) {
      super(event.getJDA(), Route.Guilds.MODIFY_SCHEDULED_EVENT.compile(event.getGuild().getId(), event.getId()));
      this.event = event;
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   protected boolean checkPermissions() {
      this.event.checkManagePermissions();
      return super.checkPermissions();
   }

   @Nonnull
   public ScheduledEvent getScheduledEvent() {
      ScheduledEvent realEvent = this.event.getGuild().getScheduledEventById(this.event.getIdLong());
      if (realEvent != null) {
         this.event = (ScheduledEventImpl)realEvent;
      }

      return this.event;
   }

   @Nonnull
   @CheckReturnValue
   public ScheduledEventManagerImpl setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setDescription(@Nullable String description) {
      Checks.notLonger(description, 1000, "Description");
      this.description = description;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setImage(@Nullable Icon icon) {
      this.image = icon;
      this.set |= 32L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setLocation(@Nonnull GuildChannel channel) {
      Checks.notNull(channel, "Channel");
      if (!channel.getGuild().equals(this.event.getGuild())) {
         throw new IllegalArgumentException("Invalid parameter: Channel has to be from the same guild as the scheduled event!");
      } else {
         if (channel instanceof StageChannel) {
            this.channelId = channel.getIdLong();
            this.entityType = ScheduledEvent.Type.STAGE_INSTANCE;
         } else {
            if (!(channel instanceof VoiceChannel)) {
               throw new IllegalArgumentException("Invalid parameter: Can only set location to Voice and Stage Channels!");
            }

            this.channelId = channel.getIdLong();
            this.entityType = ScheduledEvent.Type.VOICE;
         }

         this.set |= 4L;
         return this;
      }
   }

   @Nonnull
   public ScheduledEventManager setLocation(@Nonnull String location) {
      Checks.notBlank(location, "Location");
      Checks.notLonger(location, 100, "Location");
      this.location = location;
      this.entityType = ScheduledEvent.Type.EXTERNAL;
      this.set |= 4L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setStartTime(@Nonnull TemporalAccessor startTime) {
      Checks.notNull(startTime, "Start Time");
      OffsetDateTime offsetStartTime = Helpers.toOffsetDateTime(startTime);
      Checks.check(offsetStartTime.isAfter(OffsetDateTime.now()), "Cannot schedule event in the past!");
      Checks.check(offsetStartTime.isBefore(OffsetDateTime.now().plusYears(5L)), "Scheduled start and end times must be within five years.");
      this.startTime = offsetStartTime;
      this.set |= 8L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setEndTime(@Nonnull TemporalAccessor endTime) {
      Checks.notNull(endTime, "End Time");
      OffsetDateTime offsetEndTime = Helpers.toOffsetDateTime(endTime);
      Checks.check(offsetEndTime.isBefore(OffsetDateTime.now().plusYears(5L)), "Scheduled start and end times must be within five years.");
      this.endTime = offsetEndTime;
      this.set |= 16L;
      return this;
   }

   @Nonnull
   public ScheduledEventManager setStatus(@Nonnull ScheduledEvent.Status newStatus) {
      Checks.notNull(newStatus, "Status");
      switch (newStatus) {
         case ACTIVE:
         case CANCELED:
         case COMPLETED:
         default:
            ScheduledEvent.Status currentStatus = this.getScheduledEvent().getStatus();
            switch (currentStatus) {
               case ACTIVE:
                  Checks.check(newStatus == ScheduledEvent.Status.COMPLETED, "Cannot perform status updated! A scheduled event with status ACTIVE can only be set to COMPLETED status.");
                  break;
               case CANCELED:
               case COMPLETED:
               case UNKNOWN:
                  throw new IllegalArgumentException("Cannot perform status update! Event is " + currentStatus.name().toLowerCase(Locale.ROOT) + ".");
               case SCHEDULED:
                  Checks.check(newStatus == ScheduledEvent.Status.ACTIVE || newStatus == ScheduledEvent.Status.CANCELED, "Cannot perform status update! A scheduled event with status SCHEDULED can only be set to ACTIVE or CANCELED status.");
            }

            this.status = newStatus;
            this.set |= 64L;
            return this;
         case SCHEDULED:
         case UNKNOWN:
            throw new IllegalArgumentException("Cannot change scheduled event status to " + newStatus);
      }
   }

   protected RequestBody finalizeData() {
      this.preChecks();
      DataObject object = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         object.put("name", this.name);
      }

      if (this.shouldUpdate(2L)) {
         object.put("description", this.description);
      }

      if (this.shouldUpdate(4L)) {
         object.put("entity_type", this.entityType.getKey());
         switch (this.entityType) {
            case STAGE_INSTANCE:
            case VOICE:
               object.putNull("entity_metadata");
               object.put("channel_id", this.channelId);
               break;
            case EXTERNAL:
               object.put("entity_metadata", DataObject.empty().put("location", this.location));
               object.put("channel_id", (Object)null);
               break;
            default:
               throw new IllegalStateException("ScheduledEventType " + this.entityType + " is not supported!");
         }
      }

      if (this.shouldUpdate(8L)) {
         object.put("scheduled_start_time", this.startTime.format(DateTimeFormatter.ISO_DATE_TIME));
      }

      if (this.shouldUpdate(16L)) {
         object.put("scheduled_end_time", this.endTime.format(DateTimeFormatter.ISO_DATE_TIME));
      }

      if (this.shouldUpdate(32L)) {
         object.put("image", this.image != null ? this.image.getEncoding() : null);
      }

      if (this.shouldUpdate(64L)) {
         object.put("status", this.status.getKey());
      }

      return this.getRequestBody(object);
   }

   private void preChecks() {
      if (this.shouldUpdate(4L)) {
         Checks.check(this.getScheduledEvent().getStatus() == ScheduledEvent.Status.SCHEDULED || this.entityType == ScheduledEvent.Type.EXTERNAL && this.getScheduledEvent().getType() == ScheduledEvent.Type.EXTERNAL, "Cannot update location type or location channel of non-scheduled event.");
         if (this.entityType == ScheduledEvent.Type.EXTERNAL && this.endTime == null && this.getScheduledEvent().getEndTime() == null) {
            throw new IllegalStateException("Missing required parameter: End Time");
         }
      }

      if (this.shouldUpdate(8L)) {
         Checks.check(this.getScheduledEvent().getStatus() == ScheduledEvent.Status.SCHEDULED, "Cannot update start time of non-scheduled event!");
         Checks.check(this.endTime == null && this.getScheduledEvent().getEndTime() == null || (this.endTime == null ? this.getScheduledEvent().getEndTime() : this.endTime).isAfter(this.startTime), "Cannot schedule event to end before starting!");
      }

      if (this.shouldUpdate(16L)) {
         Checks.check((this.startTime == null ? this.getScheduledEvent().getStartTime() : this.startTime).isBefore(this.endTime), "Cannot schedule event to end before starting!");
      }

   }
}
