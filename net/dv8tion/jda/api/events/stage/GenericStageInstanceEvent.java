package net.dv8tion.jda.api.events.stage;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

public abstract class GenericStageInstanceEvent extends GenericGuildEvent {
   protected final StageInstance instance;

   public GenericStageInstanceEvent(@Nonnull JDA api, long responseNumber, @Nonnull StageInstance stageInstance) {
      super(api, responseNumber, stageInstance.getGuild());
      this.instance = stageInstance;
   }

   @Nonnull
   public StageInstance getInstance() {
      return this.instance;
   }

   @Nonnull
   public StageChannel getChannel() {
      return this.instance.getChannel();
   }
}
