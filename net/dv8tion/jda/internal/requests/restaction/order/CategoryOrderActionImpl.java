package net.dv8tion.jda.internal.requests.restaction.order;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.internal.utils.Checks;

public class CategoryOrderActionImpl extends ChannelOrderActionImpl implements CategoryOrderAction {
   protected final Category category;

   public CategoryOrderActionImpl(Category category, int bucket) {
      super(category.getGuild(), bucket, getChannelsOfType(category, bucket));
      this.category = category;
   }

   @Nonnull
   public Category getCategory() {
      return this.category;
   }

   protected void validateInput(GuildChannel entity) {
      Checks.notNull(entity, "Provided channel");
      Checks.check(entity instanceof ICategorizableChannel, "Provided channel is not an ICategorizableChannel");
      Checks.check(this.getCategory().equals(((ICategorizableChannel)entity).getParentCategory()), "Provided channel's Category is not this Category!");
      Checks.check(this.orderList.contains(entity), "Provided channel is not in the list of orderable channels!");
   }

   @Nonnull
   private static Collection<GuildChannel> getChannelsOfType(Category category, int bucket) {
      Checks.notNull(category, "Category");
      Stream var10000 = ChannelOrderActionImpl.getChannelsOfType(category.getGuild(), bucket).stream();
      Objects.requireNonNull(ICategorizableChannel.class);
      var10000 = var10000.filter(ICategorizableChannel.class::isInstance);
      Objects.requireNonNull(ICategorizableChannel.class);
      return (Collection)var10000.map(ICategorizableChannel.class::cast).filter((it) -> category.equals(it.getParentCategory())).sorted().collect(Collectors.toList());
   }
}
