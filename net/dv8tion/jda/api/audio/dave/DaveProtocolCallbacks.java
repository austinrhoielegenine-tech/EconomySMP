package net.dv8tion.jda.api.audio.dave;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

public interface DaveProtocolCallbacks {
   void sendMLSKeyPackage(@Nonnull ByteBuffer var1);

   void sendDaveProtocolReadyForTransition(int var1);

   void sendMLSCommitWelcome(@Nonnull ByteBuffer var1);

   void sendMLSInvalidCommitWelcome(int var1);
}
