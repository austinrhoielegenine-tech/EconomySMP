package net.dv8tion.jda.internal.requests.restaction;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class CommandEditActionImpl extends RestActionImpl<Command> implements CommandEditAction {
   private static final String UNDEFINED = "undefined";
   private static final int NAME_SET = 1;
   private static final int DESCRIPTION_SET = 2;
   private static final int OPTIONS_SET = 4;
   private static final int PERMISSIONS_SET = 8;
   private static final int NSFW_SET = 16;
   private static final int INTERACTION_CONTEXTS_SET = 32;
   private static final int INTEGRATION_TYPES_SET = 64;
   private final Guild guild;
   private int mask;
   private CommandDataImpl data;

   public CommandEditActionImpl(JDA api, Command.Type type, String id) {
      super(api, Route.Interactions.EDIT_COMMAND.compile(api.getSelfUser().getApplicationId(), id));
      this.guild = null;
      this.data = CommandDataImpl.of(type, "undefined", "undefined");
      this.reset();
   }

   public CommandEditActionImpl(Guild guild, Command.Type type, String id) {
      super(guild.getJDA(), Route.Interactions.EDIT_GUILD_COMMAND.compile(guild.getJDA().getSelfUser().getApplicationId(), guild.getId(), id));
      this.guild = guild;
      this.data = CommandDataImpl.of(type, "undefined", "undefined");
      this.reset();
   }

   @Nonnull
   public CommandEditAction setCheck(BooleanSupplier checks) {
      return (CommandEditAction)super.setCheck(checks);
   }

   @Nonnull
   public CommandEditAction deadline(long timestamp) {
      return (CommandEditAction)super.deadline(timestamp);
   }

   @Nonnull
   public CommandEditAction apply(@Nonnull CommandData commandData) {
      Checks.notNull(commandData, "Command Data");
      this.mask = 127;
      this.data = (CommandDataImpl)commandData;
      return this;
   }

   @Nonnull
   public CommandEditAction addCheck(@Nonnull BooleanSupplier checks) {
      return (CommandEditAction)super.addCheck(checks);
   }

   @Nonnull
   public CommandEditAction timeout(long timeout, @Nonnull TimeUnit unit) {
      return (CommandEditAction)super.timeout(timeout, unit);
   }

   @Nonnull
   public CommandEditAction setName(@Nullable String name) {
      if (name == null) {
         this.mask &= -2;
         return this;
      } else {
         this.data.setName(name);
         this.mask |= 1;
         return this;
      }
   }

   @Nonnull
   public CommandEditAction setContexts(@Nonnull Collection<InteractionContextType> contexts) {
      this.data.setContexts(contexts);
      this.mask |= 32;
      return this;
   }

   @Nonnull
   public CommandEditAction setIntegrationTypes(@Nonnull Collection<IntegrationType> integrationTypes) {
      this.data.setIntegrationTypes(integrationTypes);
      this.mask |= 64;
      return this;
   }

   @Nonnull
   public CommandEditAction setNSFW(boolean nsfw) {
      this.data.setNSFW(nsfw);
      this.mask |= 16;
      return this;
   }

   @Nonnull
   public CommandEditAction setDefaultPermissions(@Nonnull DefaultMemberPermissions permission) {
      this.data.setDefaultPermissions(permission);
      this.mask |= 8;
      return this;
   }

   @Nonnull
   public CommandEditAction setDescription(@Nullable String description) {
      if (description == null) {
         this.mask &= -3;
         return this;
      } else {
         this.data.setDescription(description);
         this.mask |= 2;
         return this;
      }
   }

   @Nonnull
   public CommandEditAction clearOptions() {
      this.data.removeAllOptions();
      this.mask |= 4;
      return this;
   }

   @Nonnull
   public CommandEditAction addOptions(@Nonnull OptionData... options) {
      this.data.addOptions(options);
      this.mask |= 4;
      return this;
   }

   @Nonnull
   public CommandEditAction addSubcommands(@Nonnull SubcommandData... subcommands) {
      this.data.addSubcommands(subcommands);
      this.mask |= 4;
      return this;
   }

   @Nonnull
   public CommandEditAction addSubcommandGroups(@Nonnull SubcommandGroupData... groups) {
      this.data.addSubcommandGroups(groups);
      this.mask |= 4;
      return this;
   }

   private boolean isUnchanged(int flag) {
      return (this.mask & flag) != flag;
   }

   protected RequestBody finalizeData() {
      DataObject json = this.data.toData();
      if (this.isUnchanged(1)) {
         json.remove("name");
      }

      if (this.isUnchanged(2)) {
         json.remove("description");
      }

      if (this.isUnchanged(4)) {
         json.remove("options");
      }

      if (this.isUnchanged(8)) {
         json.remove("default_member_permissions");
      }

      if (this.isUnchanged(16)) {
         json.remove("nsfw");
      }

      if (this.isUnchanged(32)) {
         json.remove("contexts");
      }

      if (this.isUnchanged(64)) {
         json.remove("integration_types");
      }

      this.reset();
      return this.getRequestBody(json);
   }

   protected void handleSuccess(Response response, Request<Command> request) {
      DataObject json = response.getObject();
      request.onSuccess(new CommandImpl(this.api, this.guild, json));
   }

   private void reset() {
      this.mask = 0;
      this.data = CommandDataImpl.of(this.data.getType(), "undefined", "undefined");
   }
}
