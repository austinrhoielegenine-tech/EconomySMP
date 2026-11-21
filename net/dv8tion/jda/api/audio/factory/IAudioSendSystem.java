package net.dv8tion.jda.api.audio.factory;

import java.util.concurrent.ConcurrentMap;
import javax.annotation.CheckForNull;

public interface IAudioSendSystem {
   void start();

   void shutdown();

   default void setContextMap(@CheckForNull ConcurrentMap<String, String> contextMap) {
   }
}
