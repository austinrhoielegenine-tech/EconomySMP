package net.dv8tion.jda.api.utils;

import java.io.IOException;

@FunctionalInterface
public interface IOConsumer<T> {
   void accept(T var1) throws IOException;
}
