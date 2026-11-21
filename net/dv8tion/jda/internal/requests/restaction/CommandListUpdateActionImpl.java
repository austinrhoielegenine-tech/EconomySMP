package net.dv8tion.jda.internal.requests.restaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class CommandListUpdateActionImpl extends RestActionImpl<List<Command>> implements CommandListUpdateAction {
   private final List<CommandData> commands = new ArrayList();
   private final GuildImpl guild;
   private int slash;
   private int user;
   private int message;

   public CommandListUpdateActionImpl(JDA api, GuildImpl guild, Route.CompiledRoute route) {
      super(api, route);
      this.guild = guild;
   }

   @Nonnull
   public CommandListUpdateAction timeout(long timeout, @Nonnull TimeUnit unit) {
      return (CommandListUpdateAction)super.timeout(timeout, unit);
   }

   @Nonnull
   public CommandListUpdateAction addCheck(@Nonnull BooleanSupplier checks) {
      return (CommandListUpdateAction)super.addCheck(checks);
   }

   @Nonnull
   public CommandListUpdateAction setCheck(BooleanSupplier checks) {
      return (CommandListUpdateAction)super.setCheck(checks);
   }

   @Nonnull
   public CommandListUpdateAction deadline(long timestamp) {
      return (CommandListUpdateAction)super.deadline(timestamp);
   }

   @Nonnull
   public CommandListUpdateAction addCommands(@Nonnull Collection<? extends CommandData> commands) {
      Checks.noneNull(commands, "Command");
      int newSlash = 0;
      int newUser = 0;
      int newMessage = 0;

      for(CommandData command : commands) {
         switch (command.getType()) {
            case SLASH:
               ++newSlash;
               break;
            case MESSAGE:
               ++newMessage;
               break;
            case USER:
               ++newUser;
               break;
            case UNKNOWN:
               throw new IllegalArgumentException("Provided command of unknown type");
         }
      }

      Checks.check(this.slash + newSlash <= 100, "Cannot have more than %d slash commands! Try using subcommands instead.", (int)100);
      Checks.check(this.user + newUser <= 15, "Cannot have more than %d user context commands!", (int)15);
      Checks.check(this.message + newMessage <= 15, "Cannot have more than %d message context commands!", (int)15);
      Checks.checkUnique(Stream.concat(commands.stream(), this.commands.stream()).map((c) -> c.getType() + " " + c.getName()), "Cannot have multiple commands of the same type with identical names. Name: \"%s\" with type %s appeared %d times!", (count, value) -> {
         String[] tuple = value.split(" ", 2);
         return new Object[]{tuple[1], tuple[0], count};
      });
      this.slash += newSlash;
      this.user += newUser;
      this.message += newMessage;
      this.commands.addAll(commands);
      return this;
   }

   protected RequestBody finalizeData() {
      DataArray json = DataArray.empty();
      json.addAll(this.commands);
      return this.getRequestBody(json);
   }

   protected void handleSuccess(Response response, Request<List<Command>> request) {
      List<Command> commands = (List)response.getArray().stream(DataArray::getObject).map((obj) -> new CommandImpl(this.api, this.guild, obj)).collect(Collectors.toList());
      request.onSuccess(commands);
   }
}
