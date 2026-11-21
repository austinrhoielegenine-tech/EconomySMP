package net.dv8tion.jda.api.audio;

import club.minnced.opus.util.OpusLibrary;
import java.io.IOException;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public final class AudioNatives {
   private static final Logger LOG = JDALogger.getLog(AudioNatives.class);
   private static boolean initialized;
   private static boolean audioSupported;

   private AudioNatives() {
   }

   public static boolean isAudioSupported() {
      return audioSupported;
   }

   public static boolean isInitialized() {
      return initialized;
   }

   public static synchronized boolean ensureOpus() {
      if (initialized) {
         return audioSupported;
      } else {
         initialized = true;

         boolean var0;
         try {
            if (!OpusLibrary.isInitialized()) {
               audioSupported = OpusLibrary.loadFromJar();
               return audioSupported;
            }

            audioSupported = true;
            var0 = true;
         } catch (Throwable e) {
            handleException(e);
            return audioSupported;
         } finally {
            if (audioSupported) {
               LOG.info("Audio System successfully setup!");
            } else {
               LOG.info("Audio System encountered problems while loading, thus, is disabled.");
            }

         }

         return var0;
      }
   }

   private static void handleException(Throwable e) {
      if (e instanceof UnsupportedOperationException) {
         LOG.error((String)"Sorry, JDA's audio system doesn't support this system.\n{}", (Object)e.getMessage());
      } else if (e instanceof NoClassDefFoundError) {
         LOG.error("Missing opus dependency, unable to initialize audio!");
      } else if (e instanceof IOException) {
         LOG.error("There was an IO Exception when setting up the temp files for audio.", e);
      } else if (e instanceof UnsatisfiedLinkError) {
         LOG.error("JDA encountered a problem when attempting to load the Native libraries. Contact a DEV.", e);
      } else {
         if (e instanceof Error) {
            throw (Error)e;
         }

         LOG.error("An unknown exception occurred while attempting to setup JDA's audio system!", e);
      }

   }
}
