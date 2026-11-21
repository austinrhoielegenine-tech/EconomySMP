package net.dv8tion.jda.internal.requests.restaction;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationMap;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.interactions.command.CommandImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import okhttp3.RequestBody;

public class CommandCreateActionImpl extends RestActionImpl<Command> implements CommandCreateAction {
   private final Guild guild;
   private final CommandDataImpl data;

   public CommandCreateActionImpl(JDAImpl api, CommandDataImpl command) {
      super(api, Route.Interactions.CREATE_COMMAND.compile(api.getSelfUser().getApplicationId()));
      this.guild = null;
      this.data = command;
   }

   public CommandCreateActionImpl(Guild guild, CommandDataImpl command) {
      super(guild.getJDA(), Route.Interactions.CREATE_GUILD_COMMAND.compile(guild.getJDA().getSelfUser().getApplicationId(), guild.getId()));
      this.guild = guild;
      this.data = command;
   }

   @Nonnull
   public CommandCreateAction addCheck(@Nonnull BooleanSupplier checks) {
      return (CommandCreateAction)super.addCheck(checks);
   }

   @Nonnull
   public CommandCreateAction setCheck(BooleanSupplier checks) {
      return (CommandCreateAction)super.setCheck(checks);
   }

   @Nonnull
   public CommandCreateAction deadline(long timestamp) {
      return (CommandCreateAction)super.deadline(timestamp);
   }

   @Nonnull
   public CommandCreateAction setDefaultPermissions(@Nonnull DefaultMemberPermissions permission) {
      this.data.setDefaultPermissions(permission);
      return this;
   }

   @Nonnull
   public CommandCreateAction setContexts(@Nonnull Collection<InteractionContextType> contexts) {
      this.data.setContexts(contexts);
      return this;
   }

   @Nonnull
   public CommandCreateAction setIntegrationTypes(@Nonnull Collection<IntegrationType> integrationTypes) {
      this.data.setIntegrationTypes(integrationTypes);
      return this;
   }

   @Nonnull
   public CommandCreateAction setNSFW(boolean nsfw) {
      this.data.setNSFW(nsfw);
      return this;
   }

   @Nonnull
   public CommandCreateAction setLocalizationFunction(@Nonnull LocalizationFunction localizationFunction) {
      this.data.setLocalizationFunction(localizationFunction);
      return this;
   }

   @Nonnull
   public String getName() {
      return this.data.getName();
   }

   @Nonnull
   public LocalizationMap getNameLocalizations() {
      return this.data.getNameLocalizations();
   }

   @Nonnull
   public Command.Type getType() {
      return this.data.getType();
   }

   @Nonnull
   public DefaultMemberPermissions getDefaultPermissions() {
      return this.data.getDefaultPermissions();
   }

   @Nonnull
   public Set<InteractionContextType> getContexts() {
      return this.data.getContexts();
   }

   @Nonnull
   public Set<IntegrationType> getIntegrationTypes() {
      return this.data.getIntegrationTypes();
   }

   public boolean isNSFW() {
      return this.data.isNSFW();
   }

   @Nonnull
   public CommandCreateAction timeout(long timeout, @Nonnull TimeUnit unit) {
      return (CommandCreateAction)super.timeout(timeout, unit);
   }

   @Nonnull
   public CommandCreateAction setName(@Nonnull String name) {
      this.data.setName(name);
      return this;
   }

   @Nonnull
   public CommandCreateAction setNameLocalization(@Nonnull DiscordLocale locale, @Nonnull String name) {
      this.data.setNameLocalization(locale, name);
      return this;
   }

   @Nonnull
   public CommandCreateAction setNameLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.data.setNameLocalizations(map);
      return this;
   }

   @Nonnull
   public CommandCreateAction setDescription(@Nonnull String description) {
      this.data.setDescription(description);
      return this;
   }

   @Nonnull
   public CommandCreateAction setDescriptionLocalization(@Nonnull DiscordLocale locale, @Nonnull String description) {
      this.data.setDescriptionLocalization(locale, description);
      return this;
   }

   @Nonnull
   public CommandCreateAction setDescriptionLocalizations(@Nonnull Map<DiscordLocale, String> map) {
      this.data.setDescriptionLocalizations(map);
      return this;
   }

   @Nonnull
   public String getDescription() {
      return this.data.getDescription();
   }

   @Nonnull
   public LocalizationMap getDescriptionLocalizations() {
      return this.data.getDescriptionLocalizations();
   }

   public boolean removeOptions(@Nonnull Predicate<? super OptionData> condition) {
      return this.data.removeOptions(condition);
   }

   public boolean removeSubcommands(@Nonnull Predicate<? super SubcommandData> condition) {
      return this.data.removeSubcommands(condition);
   }

   public boolean removeSubcommandGroups(@Nonnull Predicate<? super SubcommandGroupData> condition) {
      return this.data.removeSubcommandGroups(condition);
   }

   @Nonnull
   public List<SubcommandData> getSubcommands() {
      return this.data.getSubcommands();
   }

   @Nonnull
   public List<SubcommandGroupData> getSubcommandGroups() {
      return this.data.getSubcommandGroups();
   }

   @Nonnull
   public List<OptionData> getOptions() {
      return this.data.getOptions();
   }

   @Nonnull
   public CommandCreateAction addOptions(@Nonnull OptionData... options) {
      this.data.addOptions(options);
      return this;
   }

   @Nonnull
   public CommandCreateAction addSubcommands(@Nonnull SubcommandData... subcommand) {
      this.data.addSubcommands(subcommand);
      return this;
   }

   @Nonnull
   public CommandCreateAction addSubcommandGroups(@Nonnull SubcommandGroupData... group) {
      this.data.addSubcommandGroups(group);
      return this;
   }

   public RequestBody finalizeData() {
      return this.getRequestBody(this.data.toData());
   }

   protected void handleSuccess(Response response, Request<Command> request) {
      DataObject json = response.getObject();
      request.onSuccess(new CommandImpl(this.api, this.guild, json));
   }

   @Nonnull
   public DataObject toData() {
      return this.data.toData();
   }
}
