package net.dv8tion.jda.api.interactions.commands;

import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public interface CommandInteraction extends IReplyCallback, CommandInteractionPayload, IModalCallback {
}
