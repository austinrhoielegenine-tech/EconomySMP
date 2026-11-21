package net.dv8tion.jda.internal.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.ScheduledEventManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ScheduledEventMembersPaginationAction;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.internal.managers.ScheduledEventManagerImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.ScheduledEventMembersPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class ScheduledEventImpl implements ScheduledEvent {
   private final long id;
   private final Guild guild;
   private String name;
   private String description;
   private OffsetDateTime startTime;
   private OffsetDateTime endTime;
   private String coverImage;
   private ScheduledEvent.Status status;
   private ScheduledEvent.Type type;
   private User creator;
   private long creatorId;
   private int interestedUserCount;
   private String location;

   public ScheduledEventImpl(long id, Guild guild) {
      this.id = id;
      this.guild = guild;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nullable
   public String getCoverImageId() {
      return this.coverImage;
   }

   @Nullable
   public String getImageUrl() {
      return this.coverImage == null ? null : this.getCoverImageUrl(this.coverImage.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
   }

   @Nullable
   public User getCreator() {
      return this.creator;
   }

   public long getCreatorIdLong() {
      return this.creatorId;
   }

   @Nonnull
   public ScheduledEvent.Status getStatus() {
      return this.status;
   }

   @Nonnull
   public ScheduledEvent.Type getType() {
      return this.type;
   }

   @Nonnull
   public OffsetDateTime getStartTime() {
      return this.startTime;
   }

   @Nullable
   public OffsetDateTime getEndTime() {
      return this.endTime;
   }

   @Nullable
   public GuildChannelUnion getChannel() {
      return this.type.isChannel() ? (GuildChannelUnion)this.guild.getGuildChannelById(this.location) : null;
   }

   @Nonnull
   public String getLocation() {
      return this.location;
   }

   @Nonnull
   public String getJumpUrl() {
      return Helpers.format("https://discord.com/events/%s/%s", this.getGuild().getId(), this.getId());
   }

   public int getInterestedUserCount() {
      return this.interestedUserCount;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public ScheduledEventManager getManager() {
      return new ScheduledEventManagerImpl(this);
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      this.checkManagePermissions();
      Route.CompiledRoute route = Route.Guilds.DELETE_SCHEDULED_EVENT.compile(this.guild.getId(), this.getId());
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   public void checkManagePermissions() {
      SelfMember selfMember = this.guild.getSelfMember();
      if (this.creatorId == selfMember.getIdLong()) {
         if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_EVENTS}) && !selfMember.hasPermission(new Permission[]{Permission.CREATE_SCHEDULED_EVENTS})) {
            throw new InsufficientPermissionException(this.guild, Permission.MANAGE_EVENTS, "Deleting your own scheduled events requires the MANAGE_EVENTS or CREATE_SCHEDULED_EVENTS permission");
         }
      } else if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_EVENTS})) {
         throw new InsufficientPermissionException(this.guild, Permission.MANAGE_EVENTS);
      }

   }

   @Nonnull
   public ScheduledEventMembersPaginationAction retrieveInterestedMembers() {
      return new ScheduledEventMembersPaginationActionImpl(this);
   }

   public ScheduledEventImpl setName(String name) {
      this.name = name;
      return this;
   }

   public ScheduledEventImpl setType(ScheduledEvent.Type type) {
      this.type = type;
      return this;
   }

   public ScheduledEventImpl setLocation(String location) {
      this.location = location;
      return this;
   }

   public ScheduledEventImpl setDescription(String description) {
      this.description = description;
      return this;
   }

   public ScheduledEventImpl setCoverImage(String image) {
      this.coverImage = image;
      return this;
   }

   public ScheduledEventImpl setCreatorId(long creatorId) {
      this.creatorId = creatorId;
      return this;
   }

   public ScheduledEventImpl setCreator(User creator) {
      this.creator = creator;
      return this;
   }

   public ScheduledEventImpl setStatus(ScheduledEvent.Status status) {
      this.status = status;
      return this;
   }

   public ScheduledEventImpl setStartTime(OffsetDateTime startTime) {
      this.startTime = startTime;
      return this;
   }

   public ScheduledEventImpl setEndTime(OffsetDateTime endTime) {
      this.endTime = endTime;
      return this;
   }

   public ScheduledEventImpl setInterestedUserCount(int interestedUserCount) {
      this.interestedUserCount = interestedUserCount;
      return this;
   }

   public int compareTo(@Nonnull ScheduledEvent scheduledEvent) {
      Checks.notNull(scheduledEvent, "Scheduled Event");
      Checks.check(this.getGuild().equals(scheduledEvent.getGuild()), "Cannot compare two Scheduled Events belonging to seperate guilds!");
      int startTimeComparison = OffsetDateTime.timeLineOrder().compare(this.getStartTime(), scheduledEvent.getStartTime());
      return startTimeComparison == 0 ? Long.compare(this.getIdLong(), scheduledEvent.getIdLong()) : startTimeComparison;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ScheduledEventImpl)) {
         return false;
      } else {
         return this.id == ((ScheduledEventImpl)o).id;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return "ScheduledEvent:" + this.getName() + '(' + this.id + ')';
   }
}
