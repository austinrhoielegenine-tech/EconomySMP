package net.dv8tion.jda.api.utils;

import java.util.Iterator;

public interface ClosableIterator<T> extends Iterator<T>, AutoCloseable {
   void close();
}
