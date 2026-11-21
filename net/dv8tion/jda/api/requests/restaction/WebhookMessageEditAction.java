package net.dv8tion.jda.api.requests.restaction;

import net.dv8tion.jda.api.utils.messages.MessageEditRequest;

public interface WebhookMessageEditAction<T> extends MessageEditRequest<WebhookMessageEditAction<T>>, AbstractWebhookMessageAction<T, WebhookMessageEditAction<T>> {
}
