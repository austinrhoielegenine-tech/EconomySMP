package net.dv8tion.jda.api.utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.DataObjectParsingException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.etf.ExTermDecoder;
import net.dv8tion.jda.api.utils.data.etf.ExTermEncoder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.SerializationUtil;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataObject implements SerializableData {
   private static final Logger log = LoggerFactory.getLogger(DataObject.class);
   protected final Map<String, Object> data;

   protected DataObject(@Nonnull Map<String, Object> data) {
      this.data = data;
   }

   @Nonnull
   public static DataObject empty() {
      return new DataObject(new HashMap());
   }

   @Nonnull
   public static DataObject fromJson(@Nonnull byte[] data) {
      Map<String, Object> map = (Map)SerializationUtil.fromJson(SerializationUtil.getMapType(), (byte[])data);
      return new DataObject(map);
   }

   @Nonnull
   public static DataObject fromJson(@Nonnull String json) {
      Map<String, Object> map = (Map)SerializationUtil.fromJson(SerializationUtil.getMapType(), (String)json);
      return new DataObject(map);
   }

   @Nonnull
   public static DataObject fromJson(@Nonnull InputStream stream) {
      Map<String, Object> map = (Map)SerializationUtil.fromJson(SerializationUtil.getMapType(), (InputStream)stream);
      return new DataObject(map);
   }

   @Nonnull
   public static DataObject fromJson(@Nonnull Reader stream) {
      Map<String, Object> map = (Map)SerializationUtil.fromJson(SerializationUtil.getMapType(), (Reader)stream);
      return new DataObject(map);
   }

   @Nonnull
   public static DataObject fromETF(@Nonnull byte[] data) {
      Checks.notNull(data, "Data");

      try {
         Map<String, Object> map = ExTermDecoder.unpackMap(ByteBuffer.wrap(data));
         return new DataObject(map);
      } catch (Exception ex) {
         log.error((String)"Failed to parse ETF data {}", (Object)Arrays.toString(data), (Object)ex);
         throw new ParsingException(ex);
      }
   }

   public boolean hasKey(@Nonnull String key) {
      return this.data.containsKey(key);
   }

   public boolean isNull(@Nonnull String key) {
      return this.data.get(key) == null;
   }

   public boolean isType(@Nonnull String key, @Nonnull DataType type) {
      return type.isType(this.data.get(key));
   }

   @Nonnull
   public DataObject getObject(@Nonnull String key) {
      return (DataObject)this.optObject(key).orElseThrow(() -> this.valueError(key, "DataObject"));
   }

   @Nonnull
   public Optional<DataObject> optObject(@Nonnull String key) {
      Map<String, Object> child = null;

      try {
         child = (Map)this.get(Map.class, key);
      } catch (ClassCastException ex) {
         log.error((String)"Unable to extract child data", (Throwable)ex);
      }

      return child == null ? Optional.empty() : Optional.of(new DataObject(child));
   }

   @Nonnull
   public DataArray getArray(@Nonnull String key) {
      return (DataArray)this.optArray(key).orElseThrow(() -> this.valueError(key, "DataArray"));
   }

   @Nonnull
   public Optional<DataArray> optArray(@Nonnull String key) {
      List<Object> child = null;

      try {
         child = (List)this.get(List.class, key);
      } catch (ClassCastException ex) {
         log.error((String)"Unable to extract child data", (Throwable)ex);
      }

      return child == null ? Optional.empty() : Optional.of(new DataArray(child));
   }

   @Nonnull
   public Optional<Object> opt(@Nonnull String key) {
      return Optional.ofNullable(this.data.get(key));
   }

   @Nonnull
   public Object get(@Nonnull String key) {
      Object value = this.data.get(key);
      if (value == null) {
         throw this.valueError(key, "any");
      } else {
         return value;
      }
   }

   @Nonnull
   public String getString(@Nonnull String key) {
      String value = this.getString(key, (String)null);
      if (value == null) {
         throw this.valueError(key, "String");
      } else {
         return value;
      }
   }

   @Contract("_, !null -> !null")
   public String getString(@Nonnull String key, @Nullable String defaultValue) {
      String value = (String)this.get(String.class, key, UnaryOperator.identity(), String::valueOf);
      return value == null ? defaultValue : value;
   }

   public boolean getBoolean(@Nonnull String key) {
      return this.getBoolean(key, false);
   }

   public boolean getBoolean(@Nonnull String key, boolean defaultValue) {
      Boolean value = (Boolean)this.get(Boolean.class, key, Boolean::parseBoolean, (Function)null);
      return value == null ? defaultValue : value;
   }

   public long getLong(@Nonnull String key) {
      Long value = (Long)this.get(Long.class, key, MiscUtil::parseLong, Number::longValue);
      if (value == null) {
         throw this.valueError(key, "long");
      } else {
         return value;
      }
   }

   public long getLong(@Nonnull String key, long defaultValue) {
      Long value = (Long)this.get(Long.class, key, Long::parseLong, Number::longValue);
      return value == null ? defaultValue : value;
   }

   public long getUnsignedLong(@Nonnull String key) {
      Long value = (Long)this.get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
      if (value == null) {
         throw this.valueError(key, "unsigned long");
      } else {
         return value;
      }
   }

   public long getUnsignedLong(@Nonnull String key, long defaultValue) {
      Long value = (Long)this.get(Long.class, key, Long::parseUnsignedLong, Number::longValue);
      return value == null ? defaultValue : value;
   }

   public int getInt(@Nonnull String key) {
      Integer value = (Integer)this.get(Integer.class, key, Integer::parseInt, Number::intValue);
      if (value == null) {
         throw this.valueError(key, "int");
      } else {
         return value;
      }
   }

   public int getInt(@Nonnull String key, int defaultValue) {
      Integer value = (Integer)this.get(Integer.class, key, Integer::parseInt, Number::intValue);
      return value == null ? defaultValue : value;
   }

   public int getUnsignedInt(@Nonnull String key) {
      Integer value = (Integer)this.get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
      if (value == null) {
         throw this.valueError(key, "unsigned int");
      } else {
         return value;
      }
   }

   public int getUnsignedInt(@Nonnull String key, int defaultValue) {
      Integer value = (Integer)this.get(Integer.class, key, Integer::parseUnsignedInt, Number::intValue);
      return value == null ? defaultValue : value;
   }

   public double getDouble(@Nonnull String key) {
      Double value = (Double)this.get(Double.class, key, Double::parseDouble, Number::doubleValue);
      if (value == null) {
         throw this.valueError(key, "double");
      } else {
         return value;
      }
   }

   public double getDouble(@Nonnull String key, double defaultValue) {
      Double value = (Double)this.get(Double.class, key, Double::parseDouble, Number::doubleValue);
      return value == null ? defaultValue : value;
   }

   @Nonnull
   public OffsetDateTime getOffsetDateTime(@Nonnull String key) {
      OffsetDateTime value = this.getOffsetDateTime(key, (OffsetDateTime)null);
      if (value == null) {
         throw this.valueError(key, "OffsetDateTime");
      } else {
         return value;
      }
   }

   @Contract("_, !null -> !null")
   public OffsetDateTime getOffsetDateTime(@Nonnull String key, @Nullable OffsetDateTime defaultValue) {
      OffsetDateTime value;
      try {
         value = (OffsetDateTime)this.get(OffsetDateTime.class, key, OffsetDateTime::parse, (Function)null);
      } catch (DateTimeParseException e) {
         throw new ParsingException(Helpers.format("Cannot parse value for %s into an OffsetDateTime object. Try double checking that %s is a valid ISO8601 timestmap", key, e.getParsedString()));
      }

      return value == null ? defaultValue : value;
   }

   @Nonnull
   public DataObject remove(@Nonnull String key) {
      this.data.remove(key);
      return this;
   }

   @Nonnull
   public DataObject putNull(@Nonnull String key) {
      this.data.put(key, (Object)null);
      return this;
   }

   @Nonnull
   public DataObject put(@Nonnull String key, @Nullable Object value) {
      if (value instanceof SerializableData) {
         this.data.put(key, ((SerializableData)value).toData().data);
      } else if (value instanceof SerializableArray) {
         this.data.put(key, ((SerializableArray)value).toDataArray().data);
      } else {
         this.data.put(key, value);
      }

      return this;
   }

   @Nonnull
   public DataObject rename(@Nonnull String key, @Nonnull String newKey) {
      Checks.notNull(key, "Key");
      Checks.notNull(newKey, "Key");
      if (!this.data.containsKey(key)) {
         return this;
      } else {
         this.data.put(newKey, this.data.remove(key));
         return this;
      }
   }

   @Nonnull
   public Collection<Object> values() {
      return this.data.values();
   }

   @Nonnull
   public Set<String> keys() {
      return this.data.keySet();
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
   public Map<String, Object> toMap() {
      return this.data;
   }

   @Nonnull
   public DataObject toData() {
      return this;
   }

   private ParsingException valueError(String key, String expectedType) {
      return !this.hasKey(key) ? new DataObjectParsingException(this, "Missing value for key '" + key + "' with expected type " + expectedType) : new DataObjectParsingException(this, "Unable to resolve value with key '" + key + "' to type " + expectedType + ": " + this.data.get(key));
   }

   @Nullable
   private <T> T get(@Nonnull Class<T> type, @Nonnull String key) {
      return (T)this.get(type, key, (Function)null, (Function)null);
   }

   @Nullable
   private <T> T get(@Nonnull Class<T> type, @Nonnull String key, @Nullable Function<String, T> stringParse, @Nullable Function<Number, T> numberParse) {
      Object value = this.data.get(key);
      if (value == null) {
         return null;
      } else if (type.isInstance(value)) {
         return (T)type.cast(value);
      } else if (type == String.class) {
         return (T)type.cast(value.toString());
      } else if (value instanceof Number && numberParse != null) {
         return (T)numberParse.apply((Number)value);
      } else if (value instanceof String && stringParse != null) {
         return (T)stringParse.apply((String)value);
      } else {
         throw new ParsingException(Helpers.format("Cannot parse value for %s into type %s: %s instance of %s", key, type.getSimpleName(), value, value.getClass().getSimpleName()));
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof DataObject) ? false : ((DataObject)obj).toMap().equals(this.toMap());
      }
   }

   public int hashCode() {
      return this.toMap().hashCode();
   }
}
