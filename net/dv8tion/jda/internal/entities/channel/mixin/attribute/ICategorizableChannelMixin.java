package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import gnu.trove.map.TLongObjectMap;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;

public interface ICategorizableChannelMixin<T extends ICategorizableChannelMixin<T>> extends ICategorizableChannel, GuildChannelMixin<T>, IPermissionContainerMixin<T> {
   default boolean isSynced() {
      IPermissionContainerMixin<?> parent = (IPermissionContainerMixin)this.getParentCategory();
      if (parent == null) {
         return true;
      } else {
         TLongObjectMap<PermissionOverride> parentOverrides = parent.getPermissionOverrideMap();
         TLongObjectMap<PermissionOverride> overrides = this.getPermissionOverrideMap();
         if (parentOverrides.size() != overrides.size()) {
            return false;
         } else {
            for(PermissionOverride override : parentOverrides.valueCollection()) {
               PermissionOverride ourOverride = overrides.get(override.getIdLong());
               if (ourOverride == null) {
                  return false;
               }

               if (ourOverride.getAllowedRaw() != override.getAllowedRaw() || ourOverride.getDeniedRaw() != override.getDeniedRaw()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   T setParentCategory(long var1);
}
