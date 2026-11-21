package net.dv8tion.jda.api.interactions.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public class PrivilegeConfig {
   private final Guild guild;
   private final Map<String, List<IntegrationPrivilege>> privileges;

   public PrivilegeConfig(@Nonnull Guild guild, @Nonnull Map<String, List<IntegrationPrivilege>> privileges) {
      this.guild = guild;
      this.privileges = Collections.unmodifiableMap(privileges);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public JDA getJDA() {
      return this.guild.getJDA();
   }

   @Nullable
   public @Unmodifiable List<IntegrationPrivilege> getApplicationPrivileges() {
      return this.getCommandPrivileges(this.getJDA().getSelfUser().getApplicationId());
   }

   @Nullable
   public @Unmodifiable List<IntegrationPrivilege> getCommandPrivileges(@Nonnull String id) {
      Checks.notNull(id, "Id");
      return (List)this.privileges.get(id);
   }

   @Nullable
   public @Unmodifiable List<IntegrationPrivilege> getCommandPrivileges(@Nonnull Command command) {
      Checks.notNull(command, "Command");
      return (List)this.privileges.get(command.getId());
   }

   @Nonnull
   public Map<String, List<IntegrationPrivilege>> getAsMap() {
      return this.privileges;
   }
}
