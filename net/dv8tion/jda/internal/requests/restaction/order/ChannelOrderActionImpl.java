package net.dv8tion.jda.internal.requests.restaction.order;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.order.ChannelOrderAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class ChannelOrderActionImpl extends OrderActionImpl<GuildChannel, ChannelOrderAction> implements ChannelOrderAction {
   protected final Guild guild;
   protected final int bucket;
   protected final TLongSet lockPermissions;
   protected final TLongLongMap parent;

   public ChannelOrderActionImpl(Guild guild, int bucket) {
      this(guild, bucket, getChannelsOfType(guild, bucket));
   }

   public ChannelOrderActionImpl(Guild guild, int bucket, Collection<? extends GuildChannel> channels) {
      super(guild.getJDA(), Route.Guilds.MODIFY_CHANNELS.compile(guild.getId()));
      this.lockPermissions = new TLongHashSet();
      this.parent = new TLongLongHashMap();
      Checks.notNull(channels, "Channels to order");
      Checks.notEmpty(channels, "Channels to order");
      Checks.check(channels.stream().allMatch((c) -> guild.equals(c.getGuild())), "One or more channels are not from the correct guild");
      Checks.check(channels.stream().allMatch((c) -> c.getType().getSortBucket() == bucket), "One or more channels did not match the expected bucket " + bucket);
      this.guild = guild;
      this.bucket = bucket;
      this.orderList.addAll(channels);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   public int getSortBucket() {
      return this.bucket;
   }

   @Nonnull
   public ChannelOrderAction setCategory(@Nullable Category category, boolean syncPermissions) {
      GuildChannel channel = (GuildChannel)this.getSelectedEntity();
      if (!(channel instanceof ICategorizableChannel) && category != null) {
         throw new IllegalStateException("Cannot move channel of type " + channel.getType() + " to category!");
      } else {
         if (category != null) {
            Checks.check(category.getGuild().equals(this.getGuild()), "Category is not from the same guild!");
         }

         long id = channel.getIdLong();
         this.parent.put(id, category == null ? 0L : category.getIdLong());
         if (syncPermissions) {
            this.lockPermissions.add(id);
         } else {
            this.lockPermissions.remove(id);
         }

         return this;
      }
   }

   protected RequestBody finalizeData() {
      Member self = this.guild.getSelfMember();
      if (!self.hasPermission(new Permission[]{Permission.MANAGE_CHANNEL})) {
         throw new InsufficientPermissionException(this.guild, Permission.MANAGE_CHANNEL);
      } else {
         DataArray array = DataArray.empty();

         for(int i = 0; i < this.orderList.size(); ++i) {
            GuildChannel chan = (GuildChannel)this.orderList.get(i);
            DataObject json = DataObject.empty().put("id", chan.getId()).put("position", i);
            if (this.parent.containsKey(chan.getIdLong())) {
               long parentId = this.parent.get(chan.getIdLong());
               json.put("parent_id", parentId == 0L ? null : parentId);
               json.put("lock_permissions", this.lockPermissions.contains(chan.getIdLong()));
            }

            array.add(json);
         }

         return this.getRequestBody(array);
      }
   }

   protected void validateInput(GuildChannel entity) {
      Checks.check(entity.getGuild().equals(this.guild), "Provided channel is not from this Guild!");
      Checks.check(this.orderList.contains(entity), "Provided channel is not in the list of orderable channels!");
   }

   protected static Collection<GuildChannel> getChannelsOfType(Guild guild, int bucket) {
      return (Collection)guild.getChannels().stream().filter((it) -> it.getType().getSortBucket() == bucket).sorted().collect(Collectors.toList());
   }
}
