package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

@FunctionalInterface
public interface ChunkingFilter {
   ChunkingFilter ALL = (x) -> true;
   ChunkingFilter NONE = (x) -> false;

   boolean filter(long var1);

   @Nonnull
   static ChunkingFilter include(@Nonnull long... ids) {
      Checks.notNull(ids, "ID array");
      return ids.length == 0 ? NONE : (guild) -> {
         for(long id : ids) {
            if (id == guild) {
               return true;
            }
         }

         return false;
      };
   }

   @Nonnull
   static ChunkingFilter exclude(@Nonnull long... ids) {
      Checks.notNull(ids, "ID array");
      return ids.length == 0 ? ALL : (guild) -> {
         for(long id : ids) {
            if (id == guild) {
               return false;
            }
         }

         return true;
      };
   }
}
