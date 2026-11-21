package net.dv8tion.jda.api.interactions.modals;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.ICustomIdInteraction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface ModalInteraction extends IReplyCallback, IMessageEditCallback, ICustomIdInteraction {
   @Nonnull
   default String getCustomId() {
      return this.getModalId();
   }

   @Nonnull
   String getModalId();

   @Nonnull
   @Unmodifiable List<ModalMapping> getValues();

   @Nullable
   default ModalMapping getValue(@Nonnull String customId) {
      Checks.notNull(customId, "ID");
      return (ModalMapping)this.getValues().stream().filter((mapping) -> mapping.getCustomId().equals(customId)).findFirst().orElse((Object)null);
   }

   @Nullable
   default ModalMapping getValueByUniqueId(int id) {
      return (ModalMapping)this.getValues().stream().filter((mapping) -> mapping.getUniqueId() == id).findFirst().orElse((Object)null);
   }

   @Nullable
   Message getMessage();

   @Nonnull
   MessageChannelUnion getChannel();

   @Nonnull
   default GuildMessageChannelUnion getGuildChannel() {
      return (GuildMessageChannelUnion)IReplyCallback.super.getGuildChannel();
   }
}
