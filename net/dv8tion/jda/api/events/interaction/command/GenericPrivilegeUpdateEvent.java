package net.dv8tion.jda.api.events.interaction.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.interactions.commands.privileges.PrivilegeTargetType;

public abstract class GenericPrivilegeUpdateEvent extends GenericGuildEvent {
   private final long targetId;
   private final long applicationId;
   private final List<IntegrationPrivilege> privileges;

   public GenericPrivilegeUpdateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, long targetId, long applicationId, @Nonnull List<IntegrationPrivilege> privileges) {
      super(api, responseNumber, guild);
      this.targetId = targetId;
      this.applicationId = applicationId;
      this.privileges = Collections.unmodifiableList(privileges);
   }

   @Nonnull
   public abstract PrivilegeTargetType getTargetType();

   public long getTargetIdLong() {
      return this.targetId;
   }

   @Nonnull
   public String getTargetId() {
      return Long.toUnsignedString(this.targetId);
   }

   public long getApplicationIdLong() {
      return this.applicationId;
   }

   @Nonnull
   public String getApplicationId() {
      return Long.toUnsignedString(this.applicationId);
   }

   @Nonnull
   public List<IntegrationPrivilege> getPrivileges() {
      return this.privileges;
   }
}
