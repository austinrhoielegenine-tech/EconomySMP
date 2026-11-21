package net.dv8tion.jda.internal.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.managers.channel.concrete.CategoryManager;
import net.dv8tion.jda.internal.managers.channel.ChannelManagerImpl;

public class CategoryManagerImpl extends ChannelManagerImpl<Category, CategoryManager> implements CategoryManager {
   public CategoryManagerImpl(Category channel) {
      super(channel);
   }
}
