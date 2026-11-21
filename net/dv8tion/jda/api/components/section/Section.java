package net.dv8tion.jda.api.components.section;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.attribute.IDisableable;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.replacer.IReplaceable;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.internal.components.section.SectionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface Section extends MessageTopLevelComponent, ContainerChildComponent, IReplaceable, IDisableable {
   int MAX_COMPONENTS = 3;

   @Nonnull
   static Section of(@Nonnull SectionAccessoryComponent accessory, @Nonnull Collection<? extends SectionContentComponent> components) {
      return SectionImpl.validated(accessory, components);
   }

   @Nonnull
   static Section of(@Nonnull SectionAccessoryComponent accessory, @Nonnull SectionContentComponent component, @Nonnull SectionContentComponent... components) {
      Checks.notNull(component, "Component");
      Checks.noneNull(components, "Components");
      return of(accessory, Helpers.mergeVararg(component, components));
   }

   default boolean isMessageCompatible() {
      if (!this.getType().isMessageCompatible()) {
         return false;
      } else {
         return this.getContentComponents().stream().allMatch(Component::isMessageCompatible) && this.getAccessory().isMessageCompatible();
      }
   }

   default boolean isModalCompatible() {
      if (!this.getType().isModalCompatible()) {
         return false;
      } else {
         return this.getContentComponents().stream().allMatch(Component::isModalCompatible) && this.getAccessory().isModalCompatible();
      }
   }

   @Nonnull
   Section replace(@Nonnull ComponentReplacer var1);

   @Nonnull
   @CheckReturnValue
   Section withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   default Section withDisabled(boolean disabled) {
      return this.replace(ComponentReplacer.of(IDisableable.class, (c) -> true, (c) -> c.withDisabled(disabled)));
   }

   @Nonnull
   @CheckReturnValue
   default Section asDisabled() {
      return (Section)IDisableable.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default Section asEnabled() {
      return (Section)IDisableable.super.asEnabled();
   }

   @Nonnull
   @CheckReturnValue
   Section withContentComponents(@Nonnull Collection<? extends SectionContentComponent> var1);

   @Nonnull
   @CheckReturnValue
   default Section withContentComponents(@Nonnull SectionContentComponent component, @Nonnull SectionContentComponent... components) {
      Checks.notNull(component, "Component");
      Checks.notNull(components, "Components");
      return this.withContentComponents(Helpers.mergeVararg(component, components));
   }

   @Nonnull
   @CheckReturnValue
   Section withAccessory(@Nonnull SectionAccessoryComponent var1);

   @Nonnull
   @Unmodifiable List<SectionContentComponentUnion> getContentComponents();

   @Nonnull
   SectionAccessoryComponentUnion getAccessory();

   default boolean isDisabled() {
      SectionAccessoryComponentUnion accessory = this.getAccessory();
      if (accessory instanceof IDisableable && ((IDisableable)accessory).isEnabled()) {
         return false;
      } else {
         Stream var10000 = ComponentIterator.createStream(this.getContentComponents());
         Objects.requireNonNull(IDisableable.class);
         var10000 = var10000.filter(IDisableable.class::isInstance);
         Objects.requireNonNull(IDisableable.class);
         return var10000.map(IDisableable.class::cast).allMatch(IDisableable::isDisabled);
      }
   }

   default boolean isEnabled() {
      SectionAccessoryComponentUnion accessory = this.getAccessory();
      if (accessory instanceof IDisableable && ((IDisableable)accessory).isDisabled()) {
         return false;
      } else {
         Stream var10000 = ComponentIterator.createStream(this.getContentComponents());
         Objects.requireNonNull(IDisableable.class);
         var10000 = var10000.filter(IDisableable.class::isInstance);
         Objects.requireNonNull(IDisableable.class);
         return var10000.map(IDisableable.class::cast).allMatch(IDisableable::isEnabled);
      }
   }
}
