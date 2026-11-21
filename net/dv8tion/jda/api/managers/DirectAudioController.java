package net.dv8tion.jda.api.managers;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public interface DirectAudioController {
   @Nonnull
   JDA getJDA();

   void connect(@Nonnull AudioChannel var1);

   void disconnect(@Nonnull Guild var1);

   void reconnect(@Nonnull AudioChannel var1);
}
