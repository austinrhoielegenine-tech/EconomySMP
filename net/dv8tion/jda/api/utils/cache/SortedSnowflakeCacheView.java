package net.dv8tion.jda.api.utils.cache;

import java.util.NavigableSet;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;

public interface SortedSnowflakeCacheView<T extends Comparable<? super T> & ISnowflake> extends SnowflakeCacheView<T> {
   void forEachUnordered(@Nonnull Consumer<? super T> var1);

   @Nonnull
   NavigableSet<T> asSet();

   @Nonnull
   Stream<T> streamUnordered();

   @Nonnull
   Stream<T> parallelStreamUnordered();
}
