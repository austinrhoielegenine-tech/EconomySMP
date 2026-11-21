package net.dv8tion.jda.api.audio;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;

public interface AudioSendHandler {
   AudioFormat INPUT_FORMAT = new AudioFormat(48000.0F, 16, 2, true, true);

   boolean canProvide();

   @Nullable
   ByteBuffer provide20MsAudio();

   default boolean isOpus() {
      return false;
   }
}
