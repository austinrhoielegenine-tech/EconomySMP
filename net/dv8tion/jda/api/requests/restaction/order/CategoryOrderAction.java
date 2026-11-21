package net.dv8tion.jda.api.requests.restaction.order;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

public interface CategoryOrderAction extends ChannelOrderAction {
   @Nonnull
   Category getCategory();
}
