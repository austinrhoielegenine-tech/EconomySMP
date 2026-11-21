package net.dv8tion.jda.internal.components.replacer;

import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;

public class TypedComponentReplacerImpl<T extends Component> implements ComponentReplacer {
   private final Class<? super T> type;
   private final Predicate<? super T> filter;
   private final Function<? super T, ? extends Component> updater;

   public TypedComponentReplacerImpl(Class<? super T> type, Predicate<? super T> filter, Function<? super T, ? extends Component> updater) {
      this.type = type;
      this.filter = filter;
      this.updater = updater;
   }

   @Nullable
   public Component apply(@Nonnull Component oldComponent) {
      if (!this.type.isInstance(oldComponent)) {
         return oldComponent;
      } else {
         return this.filter.test(oldComponent) ? (Component)this.updater.apply(oldComponent) : oldComponent;
      }
   }
}
