package net.dv8tion.jda.internal.audio;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.utils.data.DataArray;

public enum AudioEncryption {
   AEAD_AES256_GCM_RTPSIZE,
   AEAD_XCHACHA20_POLY1305_RTPSIZE;

   private final String key;

   private AudioEncryption() {
      this.key = this.name().toLowerCase(Locale.ROOT);
   }

   public String getKey() {
      return this.key;
   }

   public static AudioEncryption getPreferredMode(DataArray array) {
      AudioEncryption encryption = null;

      for(Object o : array) {
         try {
            String name = String.valueOf(o).toUpperCase(Locale.ROOT);
            AudioEncryption e = valueOf(name);
            if (encryption == null || e.ordinal() < encryption.ordinal()) {
               encryption = e;
            }
         } catch (IllegalArgumentException var6) {
         }
      }

      return encryption;
   }

   public static EnumSet<AudioEncryption> fromArray(DataArray modes) {
      return (EnumSet)modes.stream(DataArray::getString).map((mode) -> mode.toLowerCase(Locale.ROOT)).map(AudioEncryption::forMode).filter(Objects::nonNull).collect(Collectors.toCollection(() -> EnumSet.noneOf(AudioEncryption.class)));
   }

   public static AudioEncryption forMode(String mode) {
      switch (mode) {
         case "aead_aes256_gcm_rtpsize":
            return AEAD_AES256_GCM_RTPSIZE;
         case "aead_xchacha20_poly1305_rtpsize":
            return AEAD_XCHACHA20_POLY1305_RTPSIZE;
         default:
            return null;
      }
   }

   // $FF: synthetic method
   private static AudioEncryption[] $values() {
      return new AudioEncryption[]{AEAD_AES256_GCM_RTPSIZE, AEAD_XCHACHA20_POLY1305_RTPSIZE};
   }
}
