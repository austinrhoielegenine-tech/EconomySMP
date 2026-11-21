package net.dv8tion.jda.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

public class SplitUtil {
   @Nonnull
   public static List<String> split(@Nonnull String input, int limit, @Nonnull Strategy... strategies) {
      return split(input, limit, false, strategies);
   }

   @Nonnull
   public static List<String> split(@Nonnull String input, int limit, boolean trim, @Nonnull Strategy... strategies) {
      Checks.notNull(input, "Input string");
      if (!input.isEmpty() && input.length() > limit) {
         if (strategies.length == 0) {
            strategies = new Strategy[]{SplitUtil.Strategy.ANYWHERE};
         }

         int offset = 0;
         List<String> chunks = new ArrayList();

         while(offset < input.length()) {
            String chunk = null;
            if (input.length() - offset <= limit) {
               chunk = input.substring(offset);
               offset = input.length();
            } else {
               for(Strategy strategy : strategies) {
                  int newOffset = strategy.apply(input, offset, limit);
                  if (newOffset > offset) {
                     newOffset = Math.min(newOffset, input.length());
                     chunk = input.substring(offset, newOffset);
                     offset = newOffset;
                     break;
                  }
               }
            }

            if (chunk == null) {
               throw new IllegalStateException("None of the strategies successfully split the string. Try adding Strategy.ANYWHERE to the end of your strategy list.");
            }

            if (trim) {
               chunk = chunk.trim();
            }

            if (!chunk.isEmpty()) {
               chunks.add(chunk);
            }
         }

         return chunks;
      } else {
         return Collections.singletonList(input);
      }
   }

   public interface Strategy {
      Strategy ANYWHERE = (string, offset, limit) -> offset + limit;
      Strategy NEWLINE = onChar('\n');
      Strategy WHITESPACE = onChar(Character::isWhitespace);

      int apply(@Nonnull String var1, int var2, int var3);

      @Nonnull
      static Strategy onChar(char c) {
         return (string, offset, limit) -> string.lastIndexOf(c, offset + limit);
      }

      @Nonnull
      static Strategy onChar(@Nonnull Predicate<Character> predicate) {
         Checks.notNull(predicate, "Predicate");
         return (string, offset, limit) -> {
            for(int i = offset + limit; i > offset; --i) {
               if (predicate.test(string.charAt(i))) {
                  return i;
               }
            }

            return -1;
         };
      }
   }
}
