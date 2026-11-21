package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.interactions.command.CommandAutoCompleteInteractionImpl;
import net.dv8tion.jda.internal.interactions.command.MessageContextInteractionImpl;
import net.dv8tion.jda.internal.interactions.command.SlashCommandInteractionImpl;
import net.dv8tion.jda.internal.interactions.command.UserContextInteractionImpl;
import net.dv8tion.jda.internal.interactions.components.buttons.ButtonInteractionImpl;
import net.dv8tion.jda.internal.interactions.components.selections.EntitySelectInteractionImpl;
import net.dv8tion.jda.internal.interactions.components.selections.StringSelectInteractionImpl;
import net.dv8tion.jda.internal.interactions.modal.ModalInteractionImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class InteractionCreateHandler extends SocketHandler {
   public InteractionCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      int type = content.getInt("type");
      int version = content.getInt("version", 1);
      if (version != 1) {
         WebSocketClient.LOG.debug((String)"Received interaction with version {}. This version is currently unsupported by this version of JDA. Consider updating!", (Object)version);
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id", 0L);
         if (this.api.getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            DataObject channelJson = content.getObject("channel");
            ChannelType channelType = ChannelType.fromId(channelJson.getInt("type"));
            if (!channelType.isMessage()) {
               WebSocketClient.LOG.debug((String)"Discarding INTERACTION_CREATE event from unexpected channel type. Channel: {}", (Object)channelJson);
               return null;
            } else {
               switch (InteractionType.fromKey(type)) {
                  case COMMAND:
                     this.handleCommand(content);
                     break;
                  case COMPONENT:
                     this.handleAction(content);
                     break;
                  case COMMAND_AUTOCOMPLETE:
                     this.api.handleEvent(new CommandAutoCompleteInteractionEvent(this.api, this.responseNumber, new CommandAutoCompleteInteractionImpl(this.api, content)));
                     break;
                  case MODAL_SUBMIT:
                     this.api.handleEvent(new ModalInteractionEvent(this.api, this.responseNumber, new ModalInteractionImpl(this.api, content)));
                     break;
                  default:
                     this.api.handleEvent(new GenericInteractionCreateEvent(this.api, this.responseNumber, new InteractionImpl(this.api, content)));
               }

               return null;
            }
         }
      }
   }

   private void handleCommand(DataObject content) {
      int type = content.getObject("data").getInt("type");
      switch (Command.Type.fromId(type)) {
         case SLASH:
            this.api.handleEvent(new SlashCommandInteractionEvent(this.api, this.responseNumber, new SlashCommandInteractionImpl(this.api, content)));
            break;
         case MESSAGE:
            this.api.handleEvent(new MessageContextInteractionEvent(this.api, this.responseNumber, new MessageContextInteractionImpl(this.api, content)));
            break;
         case USER:
            this.api.handleEvent(new UserContextInteractionEvent(this.api, this.responseNumber, new UserContextInteractionImpl(this.api, content)));
            break;
         case UNKNOWN:
            WebSocketClient.LOG.debug((String)"Received interaction with unknown command type {}", (Object)type);
      }

   }

   private void handleAction(DataObject content) {
      int type = content.getObject("data").getInt("component_type");
      switch (Component.Type.fromKey(type)) {
         case BUTTON:
            this.api.handleEvent(new ButtonInteractionEvent(this.api, this.responseNumber, new ButtonInteractionImpl(this.api, content)));
            break;
         case STRING_SELECT:
            this.api.handleEvent(new StringSelectInteractionEvent(this.api, this.responseNumber, new StringSelectInteractionImpl(this.api, content)));
            break;
         case USER_SELECT:
         case ROLE_SELECT:
         case MENTIONABLE_SELECT:
         case CHANNEL_SELECT:
            this.api.handleEvent(new EntitySelectInteractionEvent(this.api, this.responseNumber, new EntitySelectInteractionImpl(this.api, content)));
            break;
         default:
            WebSocketClient.LOG.debug((String)"Received interaction with unknown component type {}", (Object)type);
      }

   }
}
