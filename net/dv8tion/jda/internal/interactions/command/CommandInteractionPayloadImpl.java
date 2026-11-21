package net.dv8tion.jda.internal.interactions.command;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.UserImpl;
import net.dv8tion.jda.internal.interactions.InteractionImpl;

public class CommandInteractionPayloadImpl extends InteractionImpl implements CommandInteractionPayload {
   private final long commandId;
   private final List<OptionMapping> options = new ArrayList();
   private final TLongObjectMap<Object> resolved = new TLongObjectHashMap<Object>();
   private final String name;
   private final boolean isGuildCommand;
   private String subcommand;
   private String group;
   private final Command.Type commandType;

   public CommandInteractionPayloadImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      DataObject commandData = data.getObject("data");
      this.commandId = commandData.getUnsignedLong("id");
      this.name = commandData.getString("name");
      this.commandType = Command.Type.fromId(commandData.getInt("type", 1));
      this.isGuildCommand = !commandData.isNull("guild_id");
      DataArray options = (DataArray)commandData.optArray("options").orElseGet(DataArray::empty);
      DataObject resolveJson = (DataObject)commandData.optObject("resolved").orElseGet(DataObject::empty);
      if (options.length() == 1) {
         DataObject option = options.getObject(0);
         switch (OptionType.fromKey(option.getInt("type"))) {
            case SUB_COMMAND_GROUP:
               this.group = option.getString("name");
               options = option.getArray("options");
               option = options.getObject(0);
            case SUB_COMMAND:
               this.subcommand = option.getString("name");
               options = (DataArray)option.optArray("options").orElseGet(DataArray::empty);
         }
      }

      this.parseResolved(jda, resolveJson);
      this.parseOptions(options);
   }

   private void parseOptions(DataArray options) {
      Stream var10000 = options.stream(DataArray::getObject).map((json) -> new OptionMapping(json, this.resolved, this.getJDA(), this.getGuild()));
      List var10001 = this.options;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
   }

   private void parseResolved(JDAImpl jda, DataObject resolveJson) {
      EntityBuilder entityBuilder = jda.getEntityBuilder();
      resolveJson.optObject("users").ifPresent((users) -> users.keys().forEach((userId) -> {
            DataObject userJson = users.getObject(userId);
            UserImpl userArg = entityBuilder.createUser(userJson);
            this.resolved.put(userArg.getIdLong(), userArg);
         }));
      resolveJson.optObject("attachments").ifPresent((attachments) -> attachments.keys().forEach((id) -> {
            DataObject json = attachments.getObject(id);
            Message.Attachment file = entityBuilder.createMessageAttachment(json);
            this.resolved.put(file.getIdLong(), file);
         }));
      if (this.guild != null) {
         resolveJson.optObject("members").ifPresent((members) -> {
            DataObject users = resolveJson.getObject("users");
            members.keys().forEach((memberId) -> {
               DataObject memberJson = members.getObject(memberId);
               memberJson.put("user", users.getObject(memberId));
               Member optionMember = this.interactionEntityBuilder.createMember(this.guild, memberJson);
               if (this.member instanceof MemberImpl) {
                  entityBuilder.updateMemberCache((MemberImpl)optionMember);
               }

               this.resolved.put(optionMember.getIdLong(), optionMember);
            });
         });
         resolveJson.optObject("roles").ifPresent((roles) -> roles.keys().stream().map((roleId) -> !this.guild.isDetached() ? this.guild.getRoleById(roleId) : this.interactionEntityBuilder.createRole(this.guild, roles.getObject(roleId))).filter(Objects::nonNull).forEach((role) -> this.resolved.put(role.getIdLong(), role)));
         resolveJson.optObject("channels").ifPresent((channels) -> channels.keys().forEach((id) -> {
               ISnowflake channelObj = jda.getGuildChannelById(id);
               DataObject channelJson = channels.getObject(id);
               if (channelObj != null) {
                  this.resolved.put(channelObj.getIdLong(), channelObj);
               } else if (ChannelType.fromId(channelJson.getInt("type")).isThread()) {
                  this.resolved.put(Long.parseUnsignedLong(id), this.interactionEntityBuilder.createThreadChannel(this.guild, channelJson));
               } else {
                  this.resolved.put(Long.parseUnsignedLong(id), this.interactionEntityBuilder.createGuildChannel(this.guild, channelJson));
               }

            }));
      }

   }

   @Nullable
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)super.getChannel();
   }

   @Nonnull
   public Command.Type getCommandType() {
      return this.commandType;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public String getSubcommandName() {
      return this.subcommand;
   }

   public String getSubcommandGroup() {
      return this.group;
   }

   public long getCommandIdLong() {
      return this.commandId;
   }

   public boolean isGuildCommand() {
      return this.isGuildCommand;
   }

   @Nonnull
   public List<OptionMapping> getOptions() {
      return this.options;
   }
}
