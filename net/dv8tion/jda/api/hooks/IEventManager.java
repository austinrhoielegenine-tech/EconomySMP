package net.dv8tion.jda.api.hooks;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;

public interface IEventManager {
   void register(@Nonnull Object var1);

   void unregister(@Nonnull Object var1);

   void handle(@Nonnull GenericEvent var1);

   @Nonnull
   List<Object> getRegisteredListeners();
}
