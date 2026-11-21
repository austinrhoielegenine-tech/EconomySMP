package net.dv8tion.jda.internal.handle;

import java.time.OffsetDateTime;
import java.util.Objects;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateCoverImageEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateEndTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateImageEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateLocationEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.ScheduledEventImpl;

public class ScheduledEventUpdateHandler extends SocketHandler {
   public ScheduledEventUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.SCHEDULED_EVENTS)) {
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               EventCache.LOG.debug((String)"Caching SCHEDULED_EVENT_UPDATE for uncached guild with id {}", (Object)guildId);
               this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               ScheduledEventImpl event = (ScheduledEventImpl)guild.getScheduledEventById(content.getUnsignedLong("id"));
               if (event == null) {
                  this.api.getEntityBuilder().createScheduledEvent(guild, content);
                  return null;
               } else {
                  String name = content.getString("name");
                  String description = content.getString("description", (String)null);
                  OffsetDateTime startTime = content.getOffsetDateTime("scheduled_start_time");
                  OffsetDateTime endTime = content.getOffsetDateTime("scheduled_end_time", (OffsetDateTime)null);
                  ScheduledEvent.Status status = ScheduledEvent.Status.fromKey(content.getInt("status", -1));
                  String imageId = content.getString("image", (String)null);
                  String location = content.getString("channel_id", (String)null);
                  GuildChannel channel = null;
                  String oldLocation = event.getLocation();
                  if (location != null) {
                     channel = guild.getGuildChannelById(location);
                  } else {
                     location = (String)content.optObject("entity_metadata").map((o) -> o.getString("location", "")).orElse("");
                  }

                  if (!Objects.equals(name, event.getName())) {
                     String oldName = event.getName();
                     event.setName(name);
                     this.getJDA().handleEvent(new ScheduledEventUpdateNameEvent(this.getJDA(), this.responseNumber, event, oldName));
                  }

                  if (!Objects.equals(description, event.getDescription())) {
                     String oldDescription = event.getDescription();
                     event.setDescription(description);
                     this.getJDA().handleEvent(new ScheduledEventUpdateDescriptionEvent(this.getJDA(), this.responseNumber, event, oldDescription));
                  }

                  if (!Objects.equals(startTime, event.getStartTime())) {
                     OffsetDateTime oldStartTime = event.getStartTime();
                     event.setStartTime(startTime);
                     this.getJDA().handleEvent(new ScheduledEventUpdateStartTimeEvent(this.getJDA(), this.responseNumber, event, oldStartTime));
                  }

                  if (!Objects.equals(endTime, event.getEndTime())) {
                     OffsetDateTime oldEndTime = event.getEndTime();
                     event.setEndTime(endTime);
                     this.getJDA().handleEvent(new ScheduledEventUpdateEndTimeEvent(this.getJDA(), this.responseNumber, event, oldEndTime));
                  }

                  if (!Objects.equals(status, event.getStatus())) {
                     ScheduledEvent.Status oldStatus = event.getStatus();
                     event.setStatus(status);
                     this.getJDA().handleEvent(new ScheduledEventUpdateStatusEvent(this.getJDA(), this.responseNumber, event, oldStatus));
                  }

                  if (channel == null && !location.equals(event.getLocation())) {
                     event.setLocation(location);
                     event.setType(ScheduledEvent.Type.EXTERNAL);
                     this.getJDA().handleEvent(new ScheduledEventUpdateLocationEvent(this.getJDA(), this.responseNumber, event, oldLocation));
                  }

                  if (channel instanceof StageChannel && !location.equals(event.getLocation())) {
                     event.setLocation(channel.getId());
                     event.setType(ScheduledEvent.Type.STAGE_INSTANCE);
                     this.getJDA().handleEvent(new ScheduledEventUpdateLocationEvent(this.getJDA(), this.responseNumber, event, oldLocation));
                  }

                  if (channel instanceof VoiceChannel && !location.equals(event.getLocation())) {
                     event.setLocation(channel.getId());
                     event.setType(ScheduledEvent.Type.VOICE);
                     this.getJDA().handleEvent(new ScheduledEventUpdateLocationEvent(this.getJDA(), this.responseNumber, event, oldLocation));
                  }

                  if (!Objects.equals(imageId, event.getCoverImageId())) {
                     String oldCoverImageId = event.getCoverImageId();
                     String oldImageUrl = event.getImageUrl();
                     event.setCoverImage(imageId);
                     this.getJDA().handleEvent(new ScheduledEventUpdateCoverImageEvent(this.getJDA(), this.responseNumber, event, oldCoverImageId));
                     this.getJDA().handleEvent(new ScheduledEventUpdateImageEvent(this.getJDA(), this.responseNumber, event, oldImageUrl));
                  }

                  return null;
               }
            }
         }
      }
   }
}
