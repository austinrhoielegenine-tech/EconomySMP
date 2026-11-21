package net.dv8tion.jda.api.audio.factory;

import javax.annotation.Nonnull;

public interface IAudioSendFactory {
   @Nonnull
   IAudioSendSystem createSendSystem(@Nonnull IPacketProvider var1);
}
