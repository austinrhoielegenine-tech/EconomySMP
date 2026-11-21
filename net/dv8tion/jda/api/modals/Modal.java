package net.dv8tion.jda.api.modals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.components.attribute.IDisableable;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.ModalComponentTree;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.modals.ModalImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface Modal extends SerializableData {
   int MAX_COMPONENTS = 5;
   int MAX_ID_LENGTH = 100;
   int MAX_TITLE_LENGTH = 45;

   @Nonnull
   String getId();

   @Nonnull
   String getTitle();

   @Nonnull
   List<ModalTopLevelComponentUnion> getComponents();

   @Nonnull
   default ModalComponentTree getComponentTree() {
      return ModalComponentTree.of(this.getComponents());
   }

   @Nonnull
   default Builder createCopy() {
      List<ModalTopLevelComponent> c = (List)this.getComponents().stream().map((c2) -> c2).collect(Collectors.toList());
      return (new Builder(this.getId(), this.getTitle())).addComponents((Collection)c);
   }

   @Nonnull
   @CheckReturnValue
   static Builder create(@Nonnull String customId, @Nonnull String title) {
      return new Builder(customId, title);
   }

   public static class Builder {
      private final List<ModalTopLevelComponentUnion> components = new ArrayList(5);
      private String id;
      private String title;

      protected Builder(@Nonnull String customId, @Nonnull String title) {
         this.setId(customId);
         this.setTitle(title);
      }

      @Nonnull
      public Builder setId(@Nonnull String customId) {
         Checks.notBlank(customId, "ID");
         Checks.notLonger(customId, 100, "ID");
         this.id = customId;
         return this;
      }

      @Nonnull
      public Builder setTitle(@Nonnull String title) {
         Checks.notBlank(title, "Title");
         Checks.notLonger(title, 45, "Title");
         this.title = title;
         return this;
      }

      @Nonnull
      public Builder addComponents(@Nonnull ModalTopLevelComponent... components) {
         Checks.noneNull(components, "Components");
         return this.addComponents((Collection)Arrays.asList(components));
      }

      @Nonnull
      public Builder addComponents(@Nonnull Collection<? extends ModalTopLevelComponent> components) {
         Checks.noneNull(components, "Components");
         Checks.checkComponents("Some components are incompatible with Modals", components, Component::isModalCompatible);
         this.components.addAll(membersToUnion(components));
         return this;
      }

      @Nonnull
      public Builder addComponents(@Nonnull ComponentTree<? extends ModalTopLevelComponent> tree) {
         Checks.notNull(tree, "ModalComponentTree");
         return this.addComponents((Collection)tree.getComponents());
      }

      @Nonnull
      public List<ModalTopLevelComponentUnion> getComponents() {
         return this.components;
      }

      @Nonnull
      public String getTitle() {
         return this.title;
      }

      @Nonnull
      public String getId() {
         return this.id;
      }

      @Nonnull
      public Modal build() {
         Checks.check(!this.components.isEmpty(), "Cannot make a modal without components!");
         Checks.check(this.components.size() <= 5, "Cannot make a modal with more than 5 components!");
         Checks.checkComponents("Components cannot be disabled in Modals", this.components, Builder::componentIsEnabled);
         return new ModalImpl(this.id, this.title, this.components);
      }

      private static Collection<ModalTopLevelComponentUnion> membersToUnion(Collection<? extends ModalTopLevelComponent> members) {
         return ComponentsUtil.<ModalTopLevelComponentUnion>membersToUnion(members, ModalTopLevelComponentUnion.class);
      }

      private static boolean componentIsEnabled(Component component) {
         return !(component instanceof IDisableable) || ((IDisableable)component).isEnabled();
      }
   }
}
