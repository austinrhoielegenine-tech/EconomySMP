package net.dv8tion.jda.api.components.utils;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.section.Section;
import org.apache.commons.collections4.iterators.SingletonIterator;

public class ComponentPathIterator implements Iterator<ComponentWithPath> {
   private final Deque<Iterator<ComponentWithPath>> stack = new ArrayDeque();

   protected ComponentPathIterator(String rootName, Collection<? extends Component> components) {
      this.stack.push(new CollectionAttributeIterator(rootName, "components", components));
   }

   @Nonnull
   public static ComponentPathIterator create(@Nonnull String rootName, @Nonnull Collection<? extends Component> components) {
      return new ComponentPathIterator(rootName, components);
   }

   @Nonnull
   public static Stream<ComponentWithPath> createStream(@Nonnull String rootName, @Nonnull Collection<? extends Component> components) {
      Spliterator<ComponentWithPath> spliterator = Spliterators.spliteratorUnknownSize(create(rootName, components), 16);
      return StreamSupport.stream(spliterator, false);
   }

   public boolean hasNext() {
      this.ensureNestedIteratorHasNext();
      return !this.stack.isEmpty();
   }

   @Nonnull
   public ComponentWithPath next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Iterator<ComponentWithPath> iterator = (Iterator)this.stack.peek();
         ComponentWithPath componentWithPath = (ComponentWithPath)iterator.next();
         Component component = componentWithPath.component;
         if (component instanceof Container) {
            Container container = (Container)component;
            this.stack.push(new CollectionAttributeIterator(componentWithPath.path, "components", container.getComponents()));
         } else if (component instanceof ActionRow) {
            ActionRow actionRow = (ActionRow)component;
            this.stack.push(new CollectionAttributeIterator(componentWithPath.path, "components", actionRow.getComponents()));
         } else if (component instanceof Section) {
            Section section = (Section)component;
            this.stack.push(new CollectionAttributeIterator(componentWithPath.path, "components", section.getContentComponents()));
            this.stack.push(singleAttributeIterator(componentWithPath.path, "accessory", section.getAccessory()));
         } else if (component instanceof Label) {
            Label label = (Label)component;
            this.stack.push(singleAttributeIterator(componentWithPath.path, "component", label.getChild()));
         }

         return componentWithPath;
      }
   }

   private void ensureNestedIteratorHasNext() {
      while(!this.stack.isEmpty() && !((Iterator)this.stack.peek()).hasNext()) {
         this.stack.pop();
      }

   }

   private static SingletonIterator<ComponentWithPath> singleAttributeIterator(String parentPath, String attributePath, Component component) {
      return new SingletonIterator<ComponentWithPath>(makeComponentWithPath(parentPath, attributePath, component));
   }

   private static ComponentWithPath makeComponentWithPath(String parentPath, String attributePath, Component component) {
      String path = String.format("%s.%s<%s>", parentPath, attributePath, component.getType());
      return new ComponentWithPath(component, path);
   }

   public static class ComponentWithPath {
      private final Component component;
      private final String path;

      ComponentWithPath(Component component, String path) {
         this.component = component;
         this.path = path;
      }

      @Nonnull
      public Component getComponent() {
         return this.component;
      }

      @Nonnull
      public String getPath() {
         return this.path;
      }
   }

   private static class CollectionAttributeIterator implements Iterator<ComponentWithPath> {
      private final Iterator<? extends Component> nestedIterator;
      private final String parentPath;
      private final String listName;
      private int index = 0;

      CollectionAttributeIterator(String parentPath, String listName, Collection<? extends Component> collection) {
         this.nestedIterator = collection.iterator();
         this.parentPath = parentPath;
         this.listName = listName;
      }

      public boolean hasNext() {
         return this.nestedIterator.hasNext();
      }

      @Nonnull
      public ComponentWithPath next() {
         Component component = (Component)this.nestedIterator.next();
         String listIndexPath = this.listName + "[" + this.index++ + "]";
         return ComponentPathIterator.makeComponentWithPath(this.parentPath, listIndexPath, component);
      }
   }
}
