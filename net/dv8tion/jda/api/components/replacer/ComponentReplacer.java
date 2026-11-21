package net.dv8tion.jda.api.components.replacer;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.internal.components.replacer.TypedComponentReplacerImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

@FunctionalInterface
public interface ComponentReplacer {
   @Nullable
   Component apply(@Nonnull Component var1);

   @Nonnull
   static ComponentReplacer all(@Nonnull Collection<? extends ComponentReplacer> replacers) {
      Checks.notEmpty(replacers, "ComponentReplacers");
      Checks.noneNull(replacers, "ComponentReplacers");
      return (oldComponent) -> {
         Iterator<? extends ComponentReplacer> iterator = replacers.iterator();
         Component newComponent = oldComponent;

         do {
            newComponent = ((ComponentReplacer)iterator.next()).apply(newComponent);
         } while(iterator.hasNext() && newComponent != null);

         return newComponent;
      };
   }

   @Nonnull
   static ComponentReplacer all(@Nonnull ComponentReplacer first, @Nonnull ComponentReplacer... others) {
      Checks.notNull(first, "ComponentReplacer");
      Checks.noneNull(others, "ComponentReplacers");
      return all(Helpers.mergeVararg(first, others));
   }

   @Nonnull
   static <T extends Component> ComponentReplacer of(@Nonnull Class<? super T> type, @Nonnull Predicate<? super T> filter, @Nonnull Function<? super T, Component> update) {
      Checks.notNull(type, "Component type");
      Checks.notNull(filter, "Component filter");
      Checks.notNull(update, "Component updater");
      return new TypedComponentReplacerImpl(type, filter, update);
   }

   @Nonnull
   static ComponentReplacer byUniqueId(@Nonnull Component oldComponent, @Nullable Component newComponent) {
      return byUniqueId(oldComponent.getUniqueId(), newComponent);
   }

   @Nonnull
   static ComponentReplacer byUniqueId(int id, @Nullable Component newComponent) {
      return of(Component.class, (component) -> component.getUniqueId() == id, (component) -> newComponent);
   }

   @Nonnull
   static ComponentReplacer byUniqueId(int id, @Nonnull Function<? super Component, Component> update) {
      Checks.notNull(update, "Component updater");
      return of(Component.class, (component) -> component.getUniqueId() == id, update);
   }
}
