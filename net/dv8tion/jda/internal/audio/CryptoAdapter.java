package net.dv8tion.jda.internal.audio;

import com.google.crypto.tink.aead.internal.InsecureNonceAesGcmJce;
import com.google.crypto.tink.aead.internal.InsecureNonceXChaCha20Poly1305;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.EnumSet;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.ResizingByteBuffer;

public interface CryptoAdapter {
   String AES_GCM_NO_PADDING = "AES_256/GCM/NOPADDING";

   AudioEncryption getMode();

   void encrypt(ResizingByteBuffer var1, ByteBuffer var2);

   boolean decrypt(short var1, long var2, ByteBuffer var4, ResizingByteBuffer var5);

   static AudioEncryption negotiate(EnumSet<AudioEncryption> supportedModes) {
      for(AudioEncryption mode : AudioEncryption.values()) {
         if (supportedModes.contains(mode) && isModeSupported(mode)) {
            return mode;
         }
      }

      return null;
   }

   static boolean isModeSupported(AudioEncryption mode) {
      switch (mode) {
         case AEAD_AES256_GCM_RTPSIZE:
            return Security.getAlgorithms("Cipher").contains("AES_256/GCM/NOPADDING");
         case AEAD_XCHACHA20_POLY1305_RTPSIZE:
            return true;
         default:
            return false;
      }
   }

   static CryptoAdapter getAdapter(AudioEncryption mode, byte[] secretKey) {
      switch (mode) {
         case AEAD_AES256_GCM_RTPSIZE:
            return new AES_GCM_Adapter(secretKey);
         case AEAD_XCHACHA20_POLY1305_RTPSIZE:
            return new XChaCha20Poly1305Adapter(secretKey);
         default:
            throw new IllegalStateException("Unsupported encryption mode: " + mode);
      }
   }

   public abstract static class AbstractAaedAdapter implements CryptoAdapter {
      protected static final int nonceBytes = 4;
      protected static final SecureRandom random = new SecureRandom();
      protected final byte[] secretKey;
      protected final byte[] nonceBuffer;
      protected final int tagBytes;
      protected final int paddedNonceBytes;
      protected int encryptCounter;

      protected AbstractAaedAdapter(byte[] secretKey, int tagBytes, int paddedNonceBytes) {
         this.secretKey = secretKey;
         this.tagBytes = tagBytes;
         this.paddedNonceBytes = paddedNonceBytes;
         this.nonceBuffer = new byte[paddedNonceBytes];
         this.encryptCounter = Math.abs(random.nextInt()) % 513 + 1;
      }

      public void encrypt(ResizingByteBuffer output, ByteBuffer audio) {
         int minimumOutputSize = audio.remaining() + this.tagBytes + 4;
         output.ensureRemaining(minimumOutputSize);
         IOUtil.setIntBigEndian(this.nonceBuffer, 0, this.encryptCounter);

         try {
            this.encryptInternally(output.buffer(), audio, this.nonceBuffer);
            output.buffer().putInt(this.encryptCounter++);
            output.buffer().flip();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      public boolean decrypt(short extensionLength, long userId, ByteBuffer packet, ResizingByteBuffer decrypted) {
         try {
            int headerLength = packet.position();
            packet.position(0);
            byte[] associatedData = new byte[headerLength];
            packet.get(associatedData);
            byte[] cipherText = new byte[packet.remaining() - 4];
            packet.get(cipherText);
            byte[] nonce = new byte[this.paddedNonceBytes];
            packet.get(nonce, 0, 4);
            byte[] output = this.decryptInternally(cipherText, associatedData, nonce);
            decrypted.replace(ByteBuffer.wrap(output));
            return true;
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      protected abstract void encryptInternally(ByteBuffer var1, ByteBuffer var2, byte[] var3) throws Exception;

      protected abstract byte[] decryptInternally(byte[] var1, byte[] var2, byte[] var3) throws Exception;

      protected byte[] getAssociatedData(ByteBuffer output) {
         byte[] ad = new byte[output.position()];
         output.position(0);
         output.get(ad);
         return ad;
      }

      protected byte[] getPlaintextCopy(ByteBuffer audio) {
         byte[] plaintext = new byte[audio.remaining()];
         audio.get(plaintext);
         return plaintext;
      }
   }

   public static class AES_GCM_Adapter extends AbstractAaedAdapter implements CryptoAdapter {
      public AES_GCM_Adapter(byte[] secretKey) {
         super(secretKey, 16, 12);
      }

      public AudioEncryption getMode() {
         return AudioEncryption.AEAD_AES256_GCM_RTPSIZE;
      }

      protected void encryptInternally(ByteBuffer output, ByteBuffer audio, byte[] nonce) throws Exception {
         InsecureNonceAesGcmJce cipher = this.getCipher();
         byte[] input = this.getPlaintextCopy(audio);
         byte[] associatedData = this.getAssociatedData(output);
         output.put(cipher.encrypt(nonce, input, associatedData));
      }

      public byte[] decryptInternally(byte[] cipherText, byte[] associatedData, byte[] nonce) throws Exception {
         InsecureNonceAesGcmJce cipher = this.getCipher();
         return cipher.decrypt(nonce, cipherText, associatedData);
      }

      private InsecureNonceAesGcmJce getCipher() throws GeneralSecurityException {
         return new InsecureNonceAesGcmJce(this.secretKey);
      }
   }

   public static class XChaCha20Poly1305Adapter extends AbstractAaedAdapter implements CryptoAdapter {
      public XChaCha20Poly1305Adapter(byte[] secretKey) {
         super(secretKey, 16, 24);
      }

      public AudioEncryption getMode() {
         return AudioEncryption.AEAD_XCHACHA20_POLY1305_RTPSIZE;
      }

      public void encryptInternally(ByteBuffer output, ByteBuffer audio, byte[] nonce) throws Exception {
         InsecureNonceXChaCha20Poly1305 cipher = this.getCipher();
         byte[] input = this.getPlaintextCopy(audio);
         byte[] associatedData = this.getAssociatedData(output);
         output.put(cipher.encrypt(nonce, input, associatedData));
      }

      public byte[] decryptInternally(byte[] cipherText, byte[] associatedData, byte[] nonce) throws Exception {
         InsecureNonceXChaCha20Poly1305 cipher = this.getCipher();
         return cipher.decrypt(nonce, cipherText, associatedData);
      }

      private InsecureNonceXChaCha20Poly1305 getCipher() throws GeneralSecurityException {
         return new InsecureNonceXChaCha20Poly1305(this.secretKey);
      }
   }
}
