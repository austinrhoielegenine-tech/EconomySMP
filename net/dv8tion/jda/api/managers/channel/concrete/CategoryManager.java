package net.dv8tion.jda.api.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPositionableChannelManager;

public interface CategoryManager extends IPermissionContainerManager<Category, CategoryManager>, IPositionableChannelManager<Category, CategoryManager> {
}
