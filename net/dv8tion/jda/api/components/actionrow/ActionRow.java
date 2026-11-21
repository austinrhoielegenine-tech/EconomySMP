package net.dv8tion.jda.api.components.actionrow;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.attribute.IDisableable;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.replacer.IReplaceable;
import net.dv8tion.jda.internal.components.actionrow.ActionRowImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface ActionRow extends MessageTopLevelComponent, ContainerChildComponent, IReplaceable, IDisableable {
   @Nonnull
   static ActionRow of(@Nonnull Collection<? extends ActionRowChildComponent> components) {
      return ActionRowImpl.validated(components);
   }

   @Nonnull
   static ActionRow of(@Nonnull ActionRowChildComponent component, @Nonnull ActionRowChildComponent... components) {
      Checks.notNull(component, "Component");
      Checks.notNull(components, "Components");
      return of(Helpers.mergeVararg(component, components));
   }

   @Nonnull
   static List<ActionRow> partitionOf(@Nonnull Collection<? extends ActionRowChildComponent> components) {
      return ActionRowImpl.partitionOf(components);
   }

   @Nonnull
   static List<ActionRow> partitionOf(@Nonnull ActionRowChildComponent component, @Nonnull ActionRowChildComponent... components) {
      Checks.notNull(component, "Component");
      Checks.notNull(components, "Components");
      return partitionOf(Helpers.mergeVararg(component, components));
   }

   static int getMaxAllowed(@Nonnull Component.Type type) {
      switch (type) {
         case BUTTON:
            return 5;
         case STRING_SELECT:
         case USER_SELECT:
         case ROLE_SELECT:
         case MENTIONABLE_SELECT:
         case CHANNEL_SELECT:
            return 1;
         default:
            return 0;
      }
   }

   @Nonnull
   @CheckReturnValue
   ActionRow withUniqueId(int var1);

   @Nonnull
   @Unmodifiable List<ActionRowChildComponentUnion> getComponents();

   @Nonnull
   default @Unmodifiable List<ActionComponent> getActionComponents() {
      Stream var10000 = this.getComponents().stream();
      Objects.requireNonNull(ActionComponent.class);
      var10000 = var10000.filter(ActionComponent.class::isInstance);
      Objects.requireNonNull(ActionComponent.class);
      return (List)var10000.map(ActionComponent.class::cast).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   default @Unmodifiable List<Button> getButtons() {
      Stream var10000 = this.getComponents().stream();
      Objects.requireNonNull(Button.class);
      var10000 = var10000.filter(Button.class::isInstance);
      Objects.requireNonNull(Button.class);
      return (List)var10000.map(Button.class::cast).collect(Helpers.toUnmodifiableList());
   }

   default boolean isMessageCompatible() {
      return !this.getType().isMessageCompatible() ? false : this.getComponents().stream().allMatch(Component::isMessageCompatible);
   }

   default boolean isModalCompatible() {
      return !this.getType().isModalCompatible() ? false : this.getComponents().stream().allMatch(Component::isModalCompatible);
   }

   @Nonnull
   @CheckReturnValue
   ActionRow replace(@Nonnull ComponentReplacer var1);

   default boolean isDisabled() {
      return this.getActionComponents().stream().allMatch(ActionComponent::isDisabled);
   }

   default boolean isEnabled() {
      return this.getActionComponents().stream().noneMatch(ActionComponent::isDisabled);
   }

   @Nonnull
   @CheckReturnValue
   default ActionRow withDisabled(boolean disabled) {
      return this.replace(ComponentReplacer.of(IDisableable.class, (c) -> true, (c) -> c.withDisabled(disabled)));
   }

   @Nonnull
   default ActionRow asDisabled() {
      return (ActionRow)IDisableable.super.asDisabled();
   }

   @Nonnull
   default ActionRow asEnabled() {
      return (ActionRow)IDisableable.super.asEnabled();
   }

   @Nonnull
   @CheckReturnValue
   ActionRow withComponents(@Nonnull Collection<? extends ActionRowChildComponent> var1);

   @Nonnull
   @CheckReturnValue
   default ActionRow withComponents(@Nonnull ActionRowChildComponent component, @Nonnull ActionRowChildComponent... components) {
      Checks.notNull(component, "Component");
      Checks.notNull(components, "Components");
      return this.withComponents(Helpers.mergeVararg(component, components));
   }
}
