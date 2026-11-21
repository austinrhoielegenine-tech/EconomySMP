package net.dv8tion.jda.api.audio.hooks;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerProxy implements ConnectionListener {
   private static final Logger log = LoggerFactory.getLogger(ListenerProxy.class);
   private volatile ConnectionListener listener = null;

   public void onPing(long ping) {
      if (this.listener != null) {
         ConnectionListener listener = this.listener;

         try {
            if (listener != null) {
               listener.onPing(ping);
            }
         } catch (Throwable t) {
            log.error("The ConnectionListener encountered and uncaught exception", t);
            if (t instanceof Error) {
               throw (Error)t;
            }
         }

      }
   }

   public void onStatusChange(@Nonnull ConnectionStatus status) {
      if (this.listener != null) {
         ConnectionListener listener = this.listener;

         try {
            if (listener != null) {
               listener.onStatusChange(status);
            }
         } catch (Throwable t) {
            log.error("The ConnectionListener encountered and uncaught exception", t);
            if (t instanceof Error) {
               throw (Error)t;
            }
         }

      }
   }

   public void onUserSpeakingModeUpdate(@Nonnull UserSnowflake user, @Nonnull EnumSet<SpeakingMode> modes) {
      if (this.listener != null) {
         ConnectionListener listener = this.listener;

         try {
            if (listener != null) {
               listener.onUserSpeakingModeUpdate(user, modes);
               if (user instanceof User) {
                  listener.onUserSpeakingModeUpdate((User)user, modes);
               }
            }
         } catch (Throwable t) {
            log.error("The ConnectionListener encountered and uncaught exception", t);
            if (t instanceof Error) {
               throw (Error)t;
            }
         }

      }
   }

   public void setListener(@Nullable ConnectionListener listener) {
      this.listener = listener;
   }

   public ConnectionListener getListener() {
      return this.listener;
   }
}
