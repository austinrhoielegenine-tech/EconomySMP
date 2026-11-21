package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface Procedure<T> {
   boolean execute(@Nonnull T var1);
}
