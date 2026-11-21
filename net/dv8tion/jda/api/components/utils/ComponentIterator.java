package net.dv8tion.jda.api.components.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.section.Section;

public class ComponentIterator implements Iterator<Component> {
   private final Deque<Iterator<? extends Component>> stack = new ArrayDeque();

   protected ComponentIterator(Collection<? extends Component> components) {
      this.stack.push(components.iterator());
   }

   @Nonnull
   public static ComponentIterator create(@Nonnull Collection<? extends Component> components) {
      return new ComponentIterator(components);
   }

   @Nonnull
   public static Stream<Component> createStream(@Nonnull Collection<? extends Component> components) {
      Spliterator<Component> spliterator = Spliterators.spliteratorUnknownSize(create(components), 16);
      return StreamSupport.stream(spliterator, false);
   }

   public boolean hasNext() {
      this.ensureNestedIteratorHasNext();
      return !this.stack.isEmpty();
   }

   @Nonnull
   public Component next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Iterator<? extends Component> iterator = (Iterator)this.stack.peek();
         Component component = (Component)iterator.next();
         Iterator<? extends Component> childrenIterator = getIteratorForComponent(component);
         if (childrenIterator != null) {
            this.stack.push(childrenIterator);
         }

         return component;
      }
   }

   private void ensureNestedIteratorHasNext() {
      while(!this.stack.isEmpty() && !((Iterator)this.stack.peek()).hasNext()) {
         this.stack.pop();
      }

   }

   @Nullable
   private static Iterator<? extends Component> getIteratorForComponent(Component component) {
      if (component instanceof Container) {
         Container container = (Container)component;
         return container.getComponents().iterator();
      } else if (component instanceof ActionRow) {
         ActionRow actionRow = (ActionRow)component;
         return actionRow.getComponents().iterator();
      } else if (component instanceof Section) {
         Section section = (Section)component;
         List<Component> sectionComponents = new ArrayList(section.getContentComponents());
         sectionComponents.add(section.getAccessory());
         return sectionComponents.iterator();
      } else if (component instanceof Label) {
         Label label = (Label)component;
         return Collections.singleton(label.getChild()).iterator();
      } else {
         return null;
      }
   }
}
