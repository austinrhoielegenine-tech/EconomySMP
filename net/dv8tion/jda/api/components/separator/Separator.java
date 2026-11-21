package net.dv8tion.jda.api.components.separator;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.internal.components.separator.SeparatorImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface Separator extends Component, MessageTopLevelComponent, ContainerChildComponent {
   @Nonnull
   static Separator createDivider(@Nonnull Spacing spacing) {
      return create(true, spacing);
   }

   @Nonnull
   static Separator createInvisible(@Nonnull Spacing spacing) {
      return create(false, spacing);
   }

   @Nonnull
   static Separator create(boolean isDivider, @Nonnull Spacing spacing) {
      Checks.notNull(spacing, "Spacing");
      return new SeparatorImpl(spacing, isDivider);
   }

   @Nonnull
   @CheckReturnValue
   Separator withUniqueId(int var1);

   @Nonnull
   @CheckReturnValue
   Separator withDivider(boolean var1);

   @Nonnull
   @CheckReturnValue
   Separator withSpacing(@Nonnull Spacing var1);

   boolean isDivider();

   @Nonnull
   Spacing getSpacing();

   public static enum Spacing {
      UNKNOWN(-1),
      SMALL(1),
      LARGE(2);

      private final int key;

      private Spacing(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static Spacing fromKey(int type) {
         for(Spacing t : values()) {
            if (t.key == type) {
               return t;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Spacing[] $values() {
         return new Spacing[]{UNKNOWN, SMALL, LARGE};
      }
   }
}
