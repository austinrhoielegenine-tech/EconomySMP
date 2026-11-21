package net.dv8tion.jda.api.utils.data;

import javax.annotation.Nonnull;

public interface SerializableData {
   @Nonnull
   DataObject toData();
}
