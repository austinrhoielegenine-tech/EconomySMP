package net.dv8tion.jda.api.utils;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.Checks;

public class MarkdownSanitizer {
   public static final int NORMAL = 0;
   public static final int BOLD = 1;
   public static final int ITALICS_U = 2;
   public static final int ITALICS_A = 4;
   public static final int MONO = 8;
   public static final int MONO_TWO = 16;
   public static final int BLOCK = 32;
   public static final int SPOILER = 64;
   public static final int UNDERLINE = 128;
   public static final int STRIKE = 256;
   public static final int QUOTE = 512;
   public static final int QUOTE_BLOCK = 1024;
   private static final int ESCAPED_BOLD = -2147483647;
   private static final int ESCAPED_ITALICS_U = -2147483646;
   private static final int ESCAPED_ITALICS_A = -2147483644;
   private static final int ESCAPED_MONO = -2147483640;
   private static final int ESCAPED_MONO_TWO = -2147483632;
   private static final int ESCAPED_BLOCK = -2147483616;
   private static final int ESCAPED_SPOILER = -2147483584;
   private static final int ESCAPED_UNDERLINE = -2147483520;
   private static final int ESCAPED_STRIKE = -2147483392;
   private static final int ESCAPED_QUOTE = -2147483136;
   private static final Pattern codeLanguage = Pattern.compile("^\\w+\n.*", 40);
   private static final Pattern quote = Pattern.compile("> +.*", 40);
   private static final Pattern quoteBlock = Pattern.compile(">>>\\s+\\S.*", 40);
   private static final TIntObjectMap<String> tokens = new TIntObjectHashMap<String>();
   private int ignored;
   private SanitizationStrategy strategy;

   public MarkdownSanitizer() {
      this.ignored = 0;
      this.strategy = MarkdownSanitizer.SanitizationStrategy.REMOVE;
   }

   public MarkdownSanitizer(int ignored, @Nullable SanitizationStrategy strategy) {
      this.ignored = ignored;
      this.strategy = strategy == null ? MarkdownSanitizer.SanitizationStrategy.REMOVE : strategy;
   }

   @Nonnull
   public static String sanitize(@Nonnull String sequence) {
      return sanitize(sequence, MarkdownSanitizer.SanitizationStrategy.REMOVE);
   }

   @Nonnull
   public static String sanitize(@Nonnull String sequence, @Nonnull SanitizationStrategy strategy) {
      Checks.notNull(sequence, "String");
      Checks.notNull(strategy, "Strategy");
      return (new MarkdownSanitizer()).withStrategy(strategy).compute(sequence);
   }

   @Nonnull
   public static String escape(@Nonnull String sequence) {
      return escape(sequence, 0);
   }

   @Nonnull
   public static String escape(@Nonnull String sequence, int ignored) {
      return (new MarkdownSanitizer()).withIgnored(ignored).withStrategy(MarkdownSanitizer.SanitizationStrategy.ESCAPE).compute(sequence);
   }

   @Nonnull
   public static String escape(@Nonnull String sequence, boolean single) {
      Checks.notNull(sequence, "Input");
      if (!single) {
         return escape(sequence);
      } else {
         StringBuilder builder = new StringBuilder();
         boolean escaped = false;
         boolean newline = true;

         for(int i = 0; i < sequence.length(); ++i) {
            char current = sequence.charAt(i);
            if (newline) {
               newline = Character.isWhitespace(current);
               if (current == '>') {
                  if (i + 1 < sequence.length() && Character.isWhitespace(sequence.charAt(i + 1))) {
                     builder.append("\\>");
                  } else if (i + 3 < sequence.length() && sequence.startsWith(">>>", i) && Character.isWhitespace(sequence.charAt(i + 3))) {
                     builder.append("\\>\\>\\>").append(sequence.charAt(i + 3));
                     i += 3;
                  } else {
                     builder.append(current);
                  }
                  continue;
               }
            }

            if (escaped) {
               builder.append(current);
               escaped = false;
            } else {
               switch (current) {
                  case '\n':
                     builder.append(current);
                     newline = true;
                     break;
                  case '*':
                  case '_':
                  case '`':
                     builder.append('\\').append(current);
                     break;
                  case '\\':
                     builder.append(current);
                     escaped = true;
                     break;
                  case '|':
                  case '~':
                     if (i + 1 < sequence.length() && sequence.charAt(i + 1) == current) {
                        builder.append('\\').append(current).append('\\').append(current);
                        ++i;
                     } else {
                        builder.append(current);
                     }
                     break;
                  default:
                     builder.append(current);
               }
            }
         }

         return builder.toString();
      }
   }

   @Nonnull
   public MarkdownSanitizer withStrategy(@Nonnull SanitizationStrategy strategy) {
      Checks.notNull(strategy, "Strategy");
      this.strategy = strategy;
      return this;
   }

   @Nonnull
   public MarkdownSanitizer withIgnored(int ignored) {
      this.ignored |= ignored;
      return this;
   }

   private int getRegion(int index, @Nonnull String sequence) {
      if (sequence.length() - index >= 3) {
         switch (sequence.substring(index, index + 3)) {
            case "```":
               return this.doesEscape(index, sequence) ? -2147483616 : 32;
            case "***":
               return this.doesEscape(index, sequence) ? -2147483643 : 5;
         }
      }

      if (sequence.length() - index >= 2) {
         switch (sequence.substring(index, index + 2)) {
            case "**":
               return this.doesEscape(index, sequence) ? -2147483647 : 1;
            case "__":
               return this.doesEscape(index, sequence) ? -2147483520 : 128;
            case "~~":
               return this.doesEscape(index, sequence) ? -2147483392 : 256;
            case "``":
               return this.doesEscape(index, sequence) ? -2147483632 : 16;
            case "||":
               return this.doesEscape(index, sequence) ? -2147483584 : 64;
         }
      }

      char current = sequence.charAt(index);
      switch (current) {
         case '*':
            return this.doesEscape(index, sequence) ? -2147483644 : 4;
         case '_':
            return this.doesEscape(index, sequence) ? -2147483646 : 2;
         case '`':
            return this.doesEscape(index, sequence) ? -2147483640 : 8;
         default:
            return 0;
      }
   }

   private boolean hasCollision(int index, @Nonnull String sequence, char c) {
      if (index < 0) {
         return false;
      } else {
         return index < sequence.length() - 1 && sequence.charAt(index + 1) == c;
      }
   }

   private int findEndIndex(int afterIndex, int region, @Nonnull String sequence) {
      if (this.isEscape(region)) {
         return -1;
      } else {
         int lastMatch = afterIndex + this.getDelta(region) + 1;

         while(lastMatch != -1) {
            switch (region) {
               case 1:
                  lastMatch = sequence.indexOf("**", lastMatch);
                  if (lastMatch != -1 && this.hasCollision(lastMatch + 1, sequence, '*')) {
                     lastMatch += 3;
                     continue;
                  }
                  break;
               case 2:
                  lastMatch = sequence.indexOf(95, lastMatch);
                  if (lastMatch != -1 && this.hasCollision(lastMatch, sequence, '_')) {
                     lastMatch += 2;
                     continue;
                  }
                  break;
               case 4:
                  lastMatch = sequence.indexOf(42, lastMatch);
                  if (lastMatch != -1 && this.hasCollision(lastMatch, sequence, '*')) {
                     if (this.hasCollision(lastMatch + 1, sequence, '*')) {
                        lastMatch += 3;
                     } else {
                        lastMatch += 2;
                     }
                     continue;
                  }
                  break;
               case 5:
                  lastMatch = sequence.indexOf("***", lastMatch);
                  break;
               case 8:
                  lastMatch = sequence.indexOf(96, lastMatch);
                  if (lastMatch != -1 && this.hasCollision(lastMatch, sequence, '`')) {
                     if (this.hasCollision(lastMatch + 1, sequence, '`')) {
                        lastMatch += 3;
                     } else {
                        lastMatch += 2;
                     }
                     continue;
                  }
                  break;
               case 16:
                  lastMatch = sequence.indexOf("``", lastMatch);
                  if (lastMatch != -1 && this.hasCollision(lastMatch + 1, sequence, '`')) {
                     lastMatch += 3;
                     continue;
                  }
                  break;
               case 32:
                  lastMatch = sequence.indexOf("```", lastMatch);
                  break;
               case 64:
                  lastMatch = sequence.indexOf("||", lastMatch);
                  break;
               case 128:
                  lastMatch = sequence.indexOf("__", lastMatch);
                  break;
               case 256:
                  lastMatch = sequence.indexOf("~~", lastMatch);
                  break;
               default:
                  return -1;
            }

            if (lastMatch == -1 || !this.doesEscape(lastMatch, sequence)) {
               return lastMatch;
            }

            ++lastMatch;
         }

         return -1;
      }
   }

   @Nonnull
   private String handleRegion(int start, int end, @Nonnull String sequence, int region) {
      String resolved = sequence.substring(start, end);
      switch (region) {
         case 8:
         case 16:
         case 32:
            return resolved;
         default:
            return (new MarkdownSanitizer(this.ignored, this.strategy)).compute(resolved);
      }
   }

   private int getDelta(int region) {
      switch (region) {
         case -2147483647:
         case -2147483632:
         case -2147483584:
         case -2147483520:
         case -2147483392:
         case 1:
         case 16:
         case 64:
         case 128:
         case 256:
            return 2;
         case -2147483646:
         case -2147483644:
         case -2147483640:
         case -2147483136:
         case 2:
         case 4:
         case 8:
            return 1;
         case -2147483643:
         case -2147483616:
         case 5:
         case 32:
            return 3;
         default:
            return 0;
      }
   }

   private void applyStrategy(int region, @Nonnull String seq, @Nonnull StringBuilder builder) {
      if (this.strategy == MarkdownSanitizer.SanitizationStrategy.REMOVE) {
         if (codeLanguage.matcher(seq).matches()) {
            builder.append(seq.substring(seq.indexOf("\n") + 1));
         } else {
            builder.append(seq);
         }

      } else {
         String token = tokens.get(region);
         if (token == null) {
            throw new IllegalStateException("Found illegal region for strategy ESCAPE '" + region + "' with no known format token!");
         } else {
            if (region == 128) {
               token = "_\\_";
            } else if (region == 1) {
               token = "*\\*";
            } else if (region == 5) {
               token = "*\\*\\*";
            }

            builder.append("\\").append(token).append(seq).append("\\").append(token);
         }
      }
   }

   private boolean doesEscape(int index, @Nonnull String seq) {
      int backslashes = 0;

      for(int i = index - 1; i > -1 && seq.charAt(i) == '\\'; --i) {
         ++backslashes;
      }

      return backslashes % 2 != 0;
   }

   private boolean isEscape(int region) {
      return (Integer.MIN_VALUE & region) != 0;
   }

   private boolean isIgnored(int nextRegion) {
      return (nextRegion & this.ignored) == nextRegion;
   }

   @Nonnull
   public String compute(@Nonnull String sequence) {
      Checks.notNull(sequence, "Input");
      StringBuilder builder = new StringBuilder();
      String end = this.handleQuote(sequence);
      if (end != null) {
         return end;
      } else {
         boolean onlySpacesSinceNewLine = true;
         int i = 0;

         while(i < sequence.length()) {
            int nextRegion = this.getRegion(i, sequence);
            char c = sequence.charAt(i);
            boolean isNewLine = c == '\n';
            boolean isSpace = c == ' ';
            onlySpacesSinceNewLine = isNewLine || onlySpacesSinceNewLine && isSpace;
            if (nextRegion == 0) {
               builder.append(sequence.charAt(i++));
               if ((isNewLine || isSpace && onlySpacesSinceNewLine) && i < sequence.length()) {
                  String result = this.handleQuote(sequence.substring(i));
                  if (result != null) {
                     return builder.append(result).toString();
                  }
               }
            } else {
               int endRegion = this.findEndIndex(i, nextRegion, sequence);
               if (!this.isIgnored(nextRegion) && endRegion != -1) {
                  int delta = this.getDelta(nextRegion);
                  this.applyStrategy(nextRegion, this.handleRegion(i + delta, endRegion, sequence, nextRegion), builder);
                  i = endRegion + delta;
               } else {
                  int delta = this.getDelta(nextRegion);

                  for(int j = 0; j < delta; ++j) {
                     builder.append(sequence.charAt(i++));
                  }
               }
            }
         }

         return builder.toString();
      }
   }

   private String handleQuote(@Nonnull String sequence) {
      if (!this.isIgnored(512) && quote.matcher(sequence).matches()) {
         int start = sequence.indexOf(62);
         if (start < 0) {
            start = 0;
         }

         StringBuilder builder = new StringBuilder(this.compute(sequence.substring(start + 2)));
         if (this.strategy == MarkdownSanitizer.SanitizationStrategy.ESCAPE) {
            builder.insert(0, "\\> ");
         }

         return builder.toString();
      } else if (!this.isIgnored(1024) && quoteBlock.matcher(sequence).matches()) {
         return this.strategy == MarkdownSanitizer.SanitizationStrategy.ESCAPE ? this.compute("\\".concat(sequence)) : this.compute(sequence.substring(4));
      } else {
         return null;
      }
   }

   static {
      tokens.put(0, "");
      tokens.put(1, "**");
      tokens.put(2, "_");
      tokens.put(4, "*");
      tokens.put(5, "***");
      tokens.put(8, "`");
      tokens.put(16, "``");
      tokens.put(32, "```");
      tokens.put(64, "||");
      tokens.put(128, "__");
      tokens.put(256, "~~");
   }

   public static enum SanitizationStrategy {
      REMOVE,
      ESCAPE;

      // $FF: synthetic method
      private static SanitizationStrategy[] $values() {
         return new SanitizationStrategy[]{REMOVE, ESCAPE};
      }
   }
}
