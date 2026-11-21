package net.dv8tion.jda.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.DataArrayParsingException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.data.etf.ExTermDecoder;
import net.dv8tion.jda.api.utils.data.etf.ExTermEncoder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.SerializationUtil;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataArray implements Iterable<Object>, SerializableArray {
   private static final Logger log = LoggerFactory.getLogger(DataObject.class);
   protected final List<Object> data;

   protected DataArray(List<Object> data) {
      this.data = data;
   }

   @Nonnull
   public static DataArray empty() {
      return new DataArray(new ArrayList());
   }

   @Nonnull
   public static DataArray fromCollection(@Nonnull Collection<?> col) {
      return empty().addAll(col);
   }

   @Nonnull
   public static DataArray fromJson(@Nonnull String json) {
      return new DataArray((List)SerializationUtil.fromJson(SerializationUtil.getListType(), (String)json));
   }

   @Nonnull
   public static DataArray fromJson(@Nonnull InputStream json) {
      return new DataArray((List)SerializationUtil.fromJson(SerializationUtil.getListType(), (InputStream)json));
   }

   @Nonnull
   public static DataArray fromJson(@Nonnull Reader json) {
      return new DataArray((List)SerializationUtil.fromJson(SerializationUtil.getListType(), (Reader)json));
   }

   @Nonnull
   public static DataArray fromETF(@Nonnull byte[] data) {
      Checks.notNull(data, "Data");

      try {
         List<Object> list = ExTermDecoder.unpackList(ByteBuffer.wrap(data));
         return new DataArray(list);
      } catch (Exception ex) {
         log.error((String)"Failed to parse ETF data {}", (Object)Arrays.toString(data), (Object)ex);
         throw new ParsingException(ex);
      }
   }

   public boolean isNull(int index) {
      return index >= this.length() || this.data.get(index) == null;
   }

   public boolean isType(int index, @Nonnull DataType type) {
      return type.isType(this.data.get(index));
   }

   public int length() {
      return this.data.size();
   }

   public boolean isEmpty() {
      return this.data.isEmpty();
   }

   @Nonnull
   public DataObject getObject(int index) {
      Map<String, Object> child = null;

      try {
         child = (Map)this.get(Map.class, index);
      } catch (ClassCastException ex) {
         log.error((String)"Unable to extract child data", (Throwable)ex);
      }

      if (child == null) {
         throw this.valueError(index, "DataObject");
      } else {
         return new DataObject(child);
      }
   }

   @Nonnull
   public DataArray getArray(int index) {
      List<Object> child = null;

      try {
         child = (List)this.get(List.class, index);
      } catch (ClassCastException ex) {
         log.error((String)"Unable to extract child data", (Throwable)ex);
      }

      if (child == null) {
         throw this.valueError(index, "DataArray");
      } else {
         return new DataArray(child);
      }
   }

   @Nonnull
   public String getString(int index) {
      String value = (String)this.get(String.class, index, UnaryOperator.identity(), String::valueOf);
      if (value == null) {
         throw this.valueError(index, "String");
      } else {
         return value;
      }
   }

   @Contract("_, !null -> !null")
   public String getString(int index, @Nullable String defaultValue) {
      String value = (String)this.get(String.class, index, UnaryOperator.identity(), String::valueOf);
      return value == null ? defaultValue : value;
   }

   public boolean getBoolean(int index) {
      return this.getBoolean(index, false);
   }

   public boolean getBoolean(int index, boolean defaultValue) {
      Boolean value = (Boolean)this.get(Boolean.class, index, Boolean::parseBoolean, (Function)null);
      return value == null ? defaultValue : value;
   }

   public int getInt(int index) {
      Integer value = (Integer)this.get(Integer.class, index, Integer::parseInt, Number::intValue);
      if (value == null) {
         throw this.valueError(index, "int");
      } else {
         return value;
      }
   }

   public int getInt(int index, int defaultValue) {
      Integer value = (Integer)this.get(Integer.class, index, Integer::parseInt, Number::intValue);
      return value == null ? defaultValue : value;
   }

   public int getUnsignedInt(int index) {
      Integer value = (Integer)this.get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
      if (value == null) {
         throw this.valueError(index, "unsigned int");
      } else {
         return value;
      }
   }

   public int getUnsignedInt(int index, int defaultValue) {
      Integer value = (Integer)this.get(Integer.class, index, Integer::parseUnsignedInt, Number::intValue);
      return value == null ? defaultValue : value;
   }

   public long getLong(int index) {
      Long value = (Long)this.get(Long.class, index, Long::parseLong, Number::longValue);
      if (value == null) {
         throw this.valueError(index, "long");
      } else {
         return value;
      }
   }

   public long getLong(int index, long defaultValue) {
      Long value = (Long)this.get(Long.class, index, Long::parseLong, Number::longValue);
      return value == null ? defaultValue : value;
   }

   public long getUnsignedLong(int index) {
      Long value = (Long)this.get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
      if (value == null) {
         throw this.valueError(index, "unsigned long");
      } else {
         return value;
      }
   }

   @Nonnull
   public OffsetDateTime getOffsetDateTime(int index) {
      OffsetDateTime value = this.getOffsetDateTime(index, (OffsetDateTime)null);
      if (value == null) {
         throw this.valueError(index, "OffsetDateTime");
      } else {
         return value;
      }
   }

   @Contract("_, !null -> !null")
   public OffsetDateTime getOffsetDateTime(int index, @Nullable OffsetDateTime defaultValue) {
      OffsetDateTime value;
      try {
         value = (OffsetDateTime)this.get(OffsetDateTime.class, index, OffsetDateTime::parse, (Function)null);
      } catch (DateTimeParseException e) {
         throw new ParsingException(Helpers.format("Cannot parse value for index %d into an OffsetDateTime object. Try double checking that %s is a valid ISO8601 timestamp", index, e.getParsedString()));
      }

      return value == null ? defaultValue : value;
   }

   public long getUnsignedLong(int index, long defaultValue) {
      Long value = (Long)this.get(Long.class, index, Long::parseUnsignedLong, Number::longValue);
      return value == null ? defaultValue : value;
   }

   public double getDouble(int index) {
      Double value = (Double)this.get(Double.class, index, Double::parseDouble, Number::doubleValue);
      if (value == null) {
         throw this.valueError(index, "double");
      } else {
         return value;
      }
   }

   public double getDouble(int index, double defaultValue) {
      Double value = (Double)this.get(Double.class, index, Double::parseDouble, Number::doubleValue);
      return value == null ? defaultValue : value;
   }

   @Nonnull
   public DataArray add(@Nullable Object value) {
      if (value instanceof SerializableData) {
         this.data.add(((SerializableData)value).toData().data);
      } else if (value instanceof SerializableArray) {
         this.data.add(((SerializableArray)value).toDataArray().data);
      } else {
         this.data.add(value);
      }

      return this;
   }

   @Nonnull
   public DataArray addAll(@Nonnull Collection<?> values) {
      values.forEach(this::add);
      return this;
   }

   @Nonnull
   public DataArray addAll(@Nonnull DataArray array) {
      return this.addAll(array.data);
   }

   @Nonnull
   public DataArray insert(int index, @Nullable Object value) {
      if (value instanceof SerializableData) {
         this.data.add(index, ((SerializableData)value).toData().data);
      } else if (value instanceof SerializableArray) {
         this.data.add(index, ((SerializableArray)value).toDataArray().data);
      } else {
         this.data.add(index, value);
      }

      return this;
   }

   @Nonnull
   public DataArray remove(int index) {
      this.data.remove(index);
      return this;
   }

   @Nonnull
   public DataArray remove(@Nullable Object value) {
      this.data.remove(value);
      return this;
   }

   @Nonnull
   public byte[] toJson() {
      return SerializationUtil.toJson(this.data);
   }

   @Nonnull
   public byte[] toETF() {
      ByteBuffer buffer = ExTermEncoder.pack(this.data);
      return Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + buffer.limit());
   }

   public String toString() {
      return SerializationUtil.toJsonString(this.data, false);
   }

   @Nonnull
   public String toPrettyString() {
      return SerializationUtil.toJsonString(this.data, true);
   }

   @Nonnull
   public String toShallowString() {
      try {
         return SerializationUtil.toShallowJsonString(this.data);
      } catch (JsonProcessingException e) {
         throw new ParsingException(e);
      }
   }

   @Nonnull
   public List<Object> toList() {
      return this.data;
   }

   private ParsingException valueError(int index, String expectedType) {
      return new DataArrayParsingException(this, "Unable to resolve value at " + index + " to type " + expectedType + ": " + this.data.get(index));
   }

   @Nullable
   private <T> T get(@Nonnull Class<T> type, int index) {
      return (T)this.get(type, index, (Function)null, (Function)null);
   }

   @Nullable
   private <T> T get(@Nonnull Class<T> type, int index, @Nullable Function<String, T> stringMapper, @Nullable Function<Number, T> numberMapper) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index out of range: " + index);
      } else {
         Object value = index < this.data.size() ? this.data.get(index) : null;
         if (value == null) {
            return null;
         } else if (type.isInstance(value)) {
            return (T)type.cast(value);
         } else if (type == String.class) {
            return (T)type.cast(value.toString());
         } else if (stringMapper != null && value instanceof String) {
            return (T)stringMapper.apply((String)value);
         } else if (numberMapper != null && value instanceof Number) {
            return (T)numberMapper.apply((Number)value);
         } else {
            throw new ParsingException(Helpers.format("Cannot parse value for index %d into type %s: %s instance of %s", index, type.getSimpleName(), value, value.getClass().getSimpleName()));
         }
      }
   }

   @Nonnull
   public Iterator<Object> iterator() {
      return this.data.iterator();
   }

   @Nonnull
   public <T> Stream<T> stream(@Nonnull BiFunction<? super DataArray, Integer, ? extends T> mapper) {
      return IntStream.range(0, this.length()).mapToObj((index) -> mapper.apply(this, index));
   }

   @Nonnull
   public DataArray toDataArray() {
      return this;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof DataArray)) {
         return false;
      } else {
         DataArray objects = (DataArray)o;
         return Objects.equals(this.data, objects.data);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.data});
   }
}
