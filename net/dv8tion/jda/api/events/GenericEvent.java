package net.dv8tion.jda.api.events;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.utils.data.DataObject;

public interface GenericEvent {
   @Nonnull
   JDA getJDA();

   long getResponseNumber();

   @Nullable
   DataObject getRawData();
}
