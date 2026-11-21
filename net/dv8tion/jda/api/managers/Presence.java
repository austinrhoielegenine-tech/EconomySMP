package net.dv8tion.jda.api.managers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public interface Presence {
   @Nonnull
   JDA getJDA();

   @Nonnull
   OnlineStatus getStatus();

   @Nullable
   Activity getActivity();

   boolean isIdle();

   void setStatus(@Nullable OnlineStatus var1);

   void setActivity(@Nullable Activity var1);

   void setIdle(boolean var1);

   void setPresence(@Nullable OnlineStatus var1, @Nullable Activity var2, boolean var3);

   void setPresence(@Nullable OnlineStatus var1, @Nullable Activity var2);

   void setPresence(@Nullable OnlineStatus var1, boolean var2);

   void setPresence(@Nullable Activity var1, boolean var2);
}
