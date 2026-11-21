package net.dv8tion.jda.api.audio;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.dave.DaveSessionFactory;
import net.dv8tion.jda.api.audio.dave.PassthroughDaveSessionFactory;
import net.dv8tion.jda.api.audio.factory.DefaultSendFactory;
import net.dv8tion.jda.api.audio.factory.IAudioSendFactory;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;

public class AudioModuleConfig {
   private DaveSessionFactory daveSessionFactory = new PassthroughDaveSessionFactory();
   private IAudioSendFactory audioSendFactory = new DefaultSendFactory();

   @Nonnull
   public DaveSessionFactory getDaveSessionFactory() {
      return this.daveSessionFactory;
   }

   @Nonnull
   @CheckReturnValue
   @Contract("_ -> new")
   public AudioModuleConfig withDaveSessionFactory(@Nonnull DaveSessionFactory daveSessionFactory) {
      Checks.notNull(daveSessionFactory, "Factory");
      AudioModuleConfig newConfig = this.copy();
      newConfig.daveSessionFactory = daveSessionFactory;
      return newConfig;
   }

   @Nonnull
   public IAudioSendFactory getAudioSendFactory() {
      return this.audioSendFactory;
   }

   @Nonnull
   @CheckReturnValue
   @Contract("_ -> new")
   public AudioModuleConfig withAudioSendFactory(@Nonnull IAudioSendFactory audioSendFactory) {
      Checks.notNull(audioSendFactory, "Factory");
      AudioModuleConfig newConfig = this.copy();
      newConfig.audioSendFactory = audioSendFactory;
      return newConfig;
   }

   @Nonnull
   private AudioModuleConfig copy() {
      AudioModuleConfig config = new AudioModuleConfig();
      config.daveSessionFactory = this.daveSessionFactory;
      config.audioSendFactory = this.audioSendFactory;
      return config;
   }
}
