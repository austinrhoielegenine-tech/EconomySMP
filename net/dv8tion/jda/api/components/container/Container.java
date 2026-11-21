package net.dv8tion.jda.api.components.container;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.attribute.IDisableable;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.replacer.IReplaceable;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.internal.components.container.ContainerImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface Container extends MessageTopLevelComponent, IReplaceable, IDisableable {
   @Nonnull
   static Container of(@Nonnull Collection<? extends ContainerChildComponent> components) {
      return ContainerImpl.validated(components);
   }

   @Nonnull
   static Container of(@Nonnull ContainerChildComponent component, @Nonnull ContainerChildComponent... components) {
      return of(Helpers.mergeVararg(component, components));
   }

   default boolean isMessageCompatible() {
      return !this.getType().isMessageCompatible() ? false : this.getComponents().stream().allMatch(Component::isMessageCompatible);
   }

   default boolean isModalCompatible() {
      return !this.getType().isModalCompatible() ? false : this.getComponents().stream().allMatch(Component::isModalCompatible);
   }

   @Nonnull
   Container replace(@Nonnull ComponentReplacer var1);

   @Nonnull
   @CheckReturnValue
   Container withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   Container withAccentColor(@Nullable Integer var1);

   @Nonnull
   @CheckReturnValue
   default Container withAccentColor(@Nullable Color accentColor) {
      return this.withAccentColor(accentColor == null ? null : accentColor.getRGB());
   }

   @Nonnull
   @CheckReturnValue
   Container withSpoiler(boolean var1);

   @Nonnull
   @CheckReturnValue
   Container withComponents(@Nonnull Collection<? extends ContainerChildComponent> var1);

   @Nonnull
   @CheckReturnValue
   default Container withComponents(@Nonnull ContainerChildComponent component, @Nonnull ContainerChildComponent... components) {
      Checks.notNull(component, "Component");
      Checks.notNull(components, "Components");
      return this.withComponents(Helpers.mergeVararg(component, components));
   }

   @Nonnull
   @CheckReturnValue
   default Container withDisabled(boolean disabled) {
      return this.replace(ComponentReplacer.of(IDisableable.class, (c) -> true, (c) -> c.withDisabled(disabled)));
   }

   @Nonnull
   @CheckReturnValue
   default Container asDisabled() {
      return (Container)IDisableable.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default Container asEnabled() {
      return (Container)IDisableable.super.asEnabled();
   }

   @Nonnull
   @Unmodifiable List<ContainerChildComponentUnion> getComponents();

   default boolean isDisabled() {
      Stream var10000 = ComponentIterator.createStream(this.getComponents());
      Objects.requireNonNull(IDisableable.class);
      var10000 = var10000.filter(IDisableable.class::isInstance);
      Objects.requireNonNull(IDisableable.class);
      return var10000.map(IDisableable.class::cast).allMatch(IDisableable::isDisabled);
   }

   default boolean isEnabled() {
      Stream var10000 = ComponentIterator.createStream(this.getComponents());
      Objects.requireNonNull(IDisableable.class);
      var10000 = var10000.filter(IDisableable.class::isInstance);
      Objects.requireNonNull(IDisableable.class);
      return var10000.map(IDisableable.class::cast).allMatch(IDisableable::isEnabled);
   }

   @Nullable
   default Color getAccentColor() {
      return this.getAccentColorRaw() != null ? new Color(this.getAccentColorRaw()) : null;
   }

   @Nullable
   Integer getAccentColorRaw();

   boolean isSpoiler();
}
