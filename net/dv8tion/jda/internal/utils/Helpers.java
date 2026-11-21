package net.dv8tion.jda.internal.utils;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.Result;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Unmodifiable;

public final class Helpers {
   private static final ZoneOffset OFFSET = ZoneOffset.of("+00:00");
   private static final Consumer EMPTY_CONSUMER = (v) -> {
   };

   public static <T> Consumer<T> emptyConsumer() {
      return EMPTY_CONSUMER;
   }

   public static OffsetDateTime toOffset(long instant) {
      return OffsetDateTime.ofInstant(Instant.ofEpochMilli(instant), OFFSET);
   }

   public static long toTimestamp(String iso8601String) {
      TemporalAccessor joinedAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(iso8601String);
      return Instant.from(joinedAt).toEpochMilli();
   }

   public static OffsetDateTime toOffsetDateTime(@Nullable TemporalAccessor temporal) {
      if (temporal == null) {
         return null;
      } else if (temporal instanceof OffsetDateTime) {
         return (OffsetDateTime)temporal;
      } else {
         ZoneOffset offset;
         try {
            offset = ZoneOffset.from(temporal);
         } catch (DateTimeException var6) {
            offset = ZoneOffset.UTC;
         }

         try {
            LocalDateTime ldt = LocalDateTime.from(temporal);
            return OffsetDateTime.of(ldt, offset);
         } catch (DateTimeException var5) {
            try {
               Instant instant = Instant.from(temporal);
               return OffsetDateTime.ofInstant(instant, offset);
            } catch (DateTimeException ex) {
               throw new DateTimeException("Unable to obtain OffsetDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
            }
         }
      }
   }

   @FormatMethod
   public static String format(@FormatString String format, Object... args) {
      return String.format(Locale.ROOT, format, args);
   }

   public static boolean isEmpty(CharSequence seq) {
      return seq == null || seq.length() == 0;
   }

   public static boolean containsWhitespace(CharSequence seq) {
      if (isEmpty(seq)) {
         return false;
      } else {
         for(int i = 0; i < seq.length(); ++i) {
            if (Character.isWhitespace(seq.charAt(i))) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isBlank(CharSequence seq) {
      if (isEmpty(seq)) {
         return true;
      } else {
         for(int i = 0; i < seq.length(); ++i) {
            if (!Character.isWhitespace(seq.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int countMatches(CharSequence seq, char c) {
      if (isEmpty(seq)) {
         return 0;
      } else {
         int count = 0;

         for(int i = 0; i < seq.length(); ++i) {
            if (seq.charAt(i) == c) {
               ++count;
            }
         }

         return count;
      }
   }

   public static String truncate(String input, int maxWidth) {
      if (input == null) {
         return null;
      } else {
         Checks.notNegative(maxWidth, "maxWidth");
         if (input.length() <= maxWidth) {
            return input;
         } else {
            return maxWidth == 0 ? "" : input.substring(0, maxWidth);
         }
      }
   }

   public static String rightPad(String input, int size) {
      int pads = size - input.length();
      if (pads <= 0) {
         return input;
      } else {
         StringBuilder out = new StringBuilder(input);

         for(int i = pads; i > 0; --i) {
            out.append(' ');
         }

         return out.toString();
      }
   }

   public static String leftPad(String input, int size) {
      int pads = size - input.length();
      if (pads <= 0) {
         return input;
      } else {
         StringBuilder out = new StringBuilder();

         for(int i = pads; i > 0; --i) {
            out.append(' ');
         }

         return out.append(input).toString();
      }
   }

   public static boolean isNumeric(String input) {
      if (isEmpty(input)) {
         return false;
      } else {
         for(int i = 0; i < input.length(); ++i) {
            if (!Character.isDigit(input.charAt(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public static int codePointLength(CharSequence string) {
      return (int)string.codePoints().count();
   }

   public static String[] split(String input, String match) {
      List<String> out = new ArrayList();

      int j;
      for(int i = 0; i < input.length(); i = j + match.length()) {
         j = input.indexOf(match, i);
         if (j == -1) {
            out.add(input.substring(i));
            break;
         }

         out.add(input.substring(i, j));
      }

      return (String[])out.toArray(new String[0]);
   }

   public static boolean equals(String a, String b, boolean ignoreCase) {
      return ignoreCase ? a == b || a != null && b != null && a.equalsIgnoreCase(b) : Objects.equals(a, b);
   }

   public static boolean deepEquals(Collection<?> first, Collection<?> second) {
      if (first == second) {
         return true;
      } else if (first != null && second != null && first.size() == second.size()) {
         Iterator<?> itFirst = first.iterator();
         Iterator<?> itSecond = second.iterator();

         while(itFirst.hasNext()) {
            Object elementFirst = itFirst.next();
            Object elementSecond = itSecond.next();
            if (!Objects.equals(elementFirst, elementSecond)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean deepEqualsUnordered(Collection<?> first, Collection<?> second) {
      if (first == second) {
         return true;
      } else if (first != null && second != null) {
         return first.size() == second.size() && second.containsAll(first);
      } else {
         return false;
      }
   }

   public static <E extends Enum<E>> EnumSet<E> copyEnumSet(Class<E> clazz, Collection<E> col) {
      return col != null && !col.isEmpty() ? EnumSet.copyOf(col) : EnumSet.noneOf(clazz);
   }

   @SafeVarargs
   public static <T> Set<T> setOf(T... elements) {
      Set<T> set = new HashSet(elements.length);
      Collections.addAll(set, elements);
      return set;
   }

   @SafeVarargs
   public static <T> List<T> listOf(T... elements) {
      return Collections.unmodifiableList(Arrays.asList(elements));
   }

   public static TLongObjectMap<DataObject> convertToMap(ToLongFunction<DataObject> getId, DataArray array) {
      TLongObjectMap<DataObject> map = new TLongObjectHashMap<DataObject>();

      for(int i = 0; i < array.length(); ++i) {
         DataObject obj = array.getObject(i);
         long objId = getId.applyAsLong(obj);
         map.put(objId, obj);
      }

      return map;
   }

   public static <I, O> Function<I, Result<O>> tryMap(Function<I, O> mapper) {
      return (element) -> Result.defer(() -> mapper.apply(element));
   }

   public static <I, O> Stream<O> mapGracefully(Stream<I> stream, Function<I, O> mapper, String errorDescription) {
      return stream.map(tryMap(mapper)).peek((result) -> {
         if (result.isFailure()) {
            JDAImpl.LOG.error(errorDescription, result.getFailure());
         }

      }).filter(Result::isSuccess).map(Result::get);
   }

   public static <T extends Throwable> T appendCause(T throwable, Throwable cause) {
      Throwable t = throwable;

      for(int i = 0; i < 5; ++i) {
         if (t.getCause() == null) {
            t.initCause(cause);
            return throwable;
         }

         t = t.getCause();
      }

      throwable.addSuppressed(cause);
      return throwable;
   }

   public static boolean hasCause(Throwable throwable, Class<? extends Throwable> cause) {
      for(Throwable cursor = throwable; cursor != null; cursor = cursor.getCause()) {
         if (cause.isInstance(cursor)) {
            return true;
         }
      }

      return false;
   }

   public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
      return Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList);
   }

   public static <E extends Enum<E>> Collector<E, ?, Set<E>> toUnmodifiableEnumSet(Class<E> enumType) {
      return Collectors.collectingAndThen(Collectors.toCollection(() -> EnumSet.noneOf(enumType)), Collections::unmodifiableSet);
   }

   @SafeVarargs
   public static <E extends Enum<E>> Set<E> unmodifiableEnumSet(E first, E... rest) {
      return Collections.unmodifiableSet(EnumSet.of(first, rest));
   }

   @Nonnull
   public static <E> @Unmodifiable List<E> copyAsUnmodifiableList(Collection<? extends E> items) {
      return items != null && !items.isEmpty() ? Collections.unmodifiableList(new ArrayList(items)) : Collections.emptyList();
   }

   @Nonnull
   @SafeVarargs
   public static <E> List<E> mergeVararg(@Nonnull E first, @Nonnull E... other) {
      List<E> list = new ArrayList(other.length + 1);
      list.add(first);
      Collections.addAll(list, other);
      return list;
   }

   public static <T> Collector<T, ?, DataArray> toDataArray() {
      return Collector.of(DataArray::empty, DataArray::add, DataArray::addAll);
   }

   public static String durationToString(Duration duration, TimeUnit resolutionUnit) {
      long actual = resolutionUnit.convert(duration.getSeconds(), TimeUnit.SECONDS);
      String raw = actual + " " + resolutionUnit.toString().toLowerCase(Locale.ROOT);
      long days = duration.toDays();
      long hours = duration.toHours() % 24L;
      long minutes = duration.toMinutes() % 60L;
      long seconds = duration.getSeconds() - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
      StringJoiner joiner = new StringJoiner(" ");
      if (days > 0L) {
         joiner.add(days + " days");
      }

      if (hours > 0L) {
         joiner.add(hours + " hours");
      }

      if (minutes > 0L) {
         joiner.add(minutes + " minutes");
      }

      if (seconds > 0L) {
         joiner.add(seconds + " seconds");
      }

      return raw + " (" + joiner + ")";
   }

   @Nonnull
   public static String getLastPathSegment(@Nonnull String url) {
      HttpUrl parsedUrl = HttpUrl.parse(url);
      Checks.check(parsedUrl != null, "URL '%s' is invalid", (Object)url);
      List<String> segments = parsedUrl.pathSegments();
      return (String)segments.get(segments.size() - 1);
   }
}
