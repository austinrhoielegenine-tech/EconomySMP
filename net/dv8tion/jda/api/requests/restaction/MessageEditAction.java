package net.dv8tion.jda.api.requests.restaction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageEditRequest;

public interface MessageEditAction extends MessageEditRequest<MessageEditAction>, FluentRestAction<Message, MessageEditAction> {
}
