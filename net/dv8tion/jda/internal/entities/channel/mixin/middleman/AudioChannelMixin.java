package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.exceptions.MissingAccessException;

public interface AudioChannelMixin<T extends AudioChannelMixin<T>> extends AudioChannelUnion, StandardGuildChannelMixin<T> {
   T setBitrate(int var1);

   T setUserLimit(int var1);

   T setRegion(String var1);

   default void checkCanAccess() {
      this.checkAttached();
      if (!this.hasPermission(Permission.VIEW_CHANNEL)) {
         throw new MissingAccessException(this, Permission.VIEW_CHANNEL);
      } else if (!this.hasPermission(Permission.VOICE_CONNECT)) {
         throw new MissingAccessException(this, Permission.VOICE_CONNECT);
      }
   }
}
