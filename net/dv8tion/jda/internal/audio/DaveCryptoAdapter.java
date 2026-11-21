package net.dv8tion.jda.internal.audio;

import java.nio.ByteBuffer;
import javax.annotation.concurrent.NotThreadSafe;
import net.dv8tion.jda.api.audio.dave.DaveSession;
import net.dv8tion.jda.internal.utils.ResizingByteBuffer;

@NotThreadSafe
public class DaveCryptoAdapter implements CryptoAdapter {
   protected final CryptoAdapter transportCryptoAdapter;
   protected final DaveSession daveSession;
   protected final int ssrc;
   protected ResizingByteBuffer encryptBuffer = new ResizingByteBuffer(ByteBuffer.allocateDirect(512));
   protected ResizingByteBuffer decryptBuffer = null;

   public DaveCryptoAdapter(CryptoAdapter transportCryptoAdapter, DaveSession daveSession, int ssrc) {
      this.transportCryptoAdapter = transportCryptoAdapter;
      this.daveSession = daveSession;
      this.ssrc = ssrc;
   }

   public AudioEncryption getMode() {
      return this.transportCryptoAdapter.getMode();
   }

   public void encrypt(ResizingByteBuffer output, ByteBuffer audio) {
      int maxSize = this.daveSession.getMaxEncryptedFrameSize(DaveSession.MediaType.AUDIO, audio.remaining());
      output.buffer().mark();
      this.encryptBuffer.prepareWrite(maxSize);
      if (this.daveSession.encrypt(DaveSession.MediaType.AUDIO, this.ssrc, audio, this.encryptBuffer.buffer())) {
         this.transportCryptoAdapter.encrypt(output, this.encryptBuffer.buffer());
      } else {
         throw new IllegalStateException("Failed to encrypt audio");
      }
   }

   public boolean decrypt(short extensionLength, long userId, ByteBuffer packet, ResizingByteBuffer decrypted) {
      if (this.decryptBuffer == null) {
         this.decryptBuffer = new ResizingByteBuffer(ByteBuffer.allocateDirect(1024));
      }

      boolean success = this.transportCryptoAdapter.decrypt(extensionLength, userId, packet, this.decryptBuffer);
      if (!success) {
         return false;
      } else {
         this.handleRTPHeaderExtension(this.decryptBuffer.buffer(), extensionLength);
         int outputSize = this.daveSession.getMaxDecryptedFrameSize(DaveSession.MediaType.AUDIO, userId, this.decryptBuffer.buffer().remaining());
         decrypted.prepareWrite(outputSize);
         return this.daveSession.decrypt(DaveSession.MediaType.AUDIO, userId, this.decryptBuffer.buffer(), decrypted.buffer());
      }
   }

   private void handleRTPHeaderExtension(ByteBuffer decrypted, short extensionLength) {
      if (extensionLength != 0) {
         int length = extensionLength & '\uffff';
         int position = decrypted.position();
         int offset = position + 4 * length;
         decrypted.position(Math.min(offset, decrypted.limit() - 1));
      }
   }
}
