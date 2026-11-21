package net.dv8tion.jda.api.components.tree;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.internal.components.tree.ComponentTreeImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface ComponentTree<E extends Component> {
   @Nonnull
   static <E extends Component, T extends IComponentUnion> ComponentTree<T> of(@Nonnull Class<T> unionType, @Nonnull Collection<E> components) {
      Checks.notNull(unionType, "Component union type");
      Checks.noneNull(components, "Components");
      return new ComponentTreeImpl<T>(unionType, ComponentsUtil.membersToUnionWithUnknownType(components, unionType));
   }

   @Nonnull
   static ComponentTree<IComponentUnion> of(@Nonnull Collection<? extends Component> components) {
      Checks.noneNull(components, "Components");
      return new ComponentTreeImpl<IComponentUnion>(IComponentUnion.class, ComponentsUtil.membersToUnionWithUnknownType(components, IComponentUnion.class));
   }

   @Nonnull
   static MessageComponentTree forMessage(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return MessageComponentTree.of(components);
   }

   @Nonnull
   static ModalComponentTree forModal(@Nonnull Collection<? extends ModalTopLevelComponent> components) {
      return ModalComponentTree.of(components);
   }

   @Nonnull
   Type getType();

   @Nonnull
   @Unmodifiable List<E> getComponents();

   @Nonnull
   default <T extends Component> List<T> findAll(@Nonnull Class<T> type) {
      return this.<T>findAll(type, (c) -> true);
   }

   @Nonnull
   default <T extends Component> List<T> findAll(@Nonnull Class<T> type, @Nonnull Predicate<? super T> filter) {
      Checks.notNull(type, "Component type");
      Checks.notNull(filter, "Component filter");
      Stream var10000 = ComponentIterator.createStream(this.getComponents());
      Objects.requireNonNull(type);
      var10000 = var10000.filter(type::isInstance);
      Objects.requireNonNull(type);
      return (List)var10000.map(type::cast).filter(filter).collect(Collectors.toList());
   }

   @Nonnull
   default <T extends Component> Optional<T> find(@Nonnull Class<T> type, @Nonnull Predicate<? super T> filter) {
      Checks.notNull(type, "Component type");
      Checks.notNull(filter, "Component filter");
      Stream var10000 = ComponentIterator.createStream(this.getComponents());
      Objects.requireNonNull(type);
      var10000 = var10000.filter(type::isInstance);
      Objects.requireNonNull(type);
      return var10000.map(type::cast).filter(filter).findFirst();
   }

   @Nonnull
   @CheckReturnValue
   ComponentTree<E> replace(@Nonnull ComponentReplacer var1);

   @Nonnull
   @CheckReturnValue
   ComponentTree<E> withDisabled(boolean var1);

   @Nonnull
   @CheckReturnValue
   default ComponentTree<E> asDisabled() {
      return this.withDisabled(true);
   }

   @Nonnull
   @CheckReturnValue
   default ComponentTree<E> asEnabled() {
      return this.withDisabled(false);
   }

   public static enum Type {
      ANY,
      MESSAGE,
      MODAL;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{ANY, MESSAGE, MODAL};
      }
   }
}
