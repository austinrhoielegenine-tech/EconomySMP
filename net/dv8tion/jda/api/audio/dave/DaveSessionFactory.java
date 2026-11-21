package net.dv8tion.jda.api.audio.dave;

import javax.annotation.Nonnull;

public interface DaveSessionFactory {
   @Nonnull
   DaveSession createDaveSession(@Nonnull DaveProtocolCallbacks var1, long var2, long var4);
}
