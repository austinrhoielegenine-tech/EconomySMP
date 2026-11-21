package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.cfg.DatatypeFeature;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.TypeWrappedSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class ObjectWriter implements Versioned, Serializable {
   private static final long serialVersionUID = 1L;
   protected static final PrettyPrinter NULL_PRETTY_PRINTER = new MinimalPrettyPrinter();
   protected final SerializationConfig _config;
   protected final DefaultSerializerProvider _serializerProvider;
   protected final SerializerFactory _serializerFactory;
   protected final JsonFactory _generatorFactory;
   protected final GeneratorSettings _generatorSettings;
   protected final Prefetch _prefetch;

   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, JavaType rootType, PrettyPrinter pp) {
      this._config = config;
      this._serializerProvider = mapper._serializerProvider;
      this._serializerFactory = mapper._serializerFactory;
      this._generatorFactory = mapper._jsonFactory;
      this._generatorSettings = pp == null ? ObjectWriter.GeneratorSettings.empty : new GeneratorSettings(pp, (FormatSchema)null, (CharacterEscapes)null, (SerializableString)null);
      if (rootType == null) {
         this._prefetch = ObjectWriter.Prefetch.empty;
      } else if (rootType.hasRawClass(Object.class)) {
         this._prefetch = ObjectWriter.Prefetch.empty.forRootType(this, rootType);
      } else {
         this._prefetch = ObjectWriter.Prefetch.empty.forRootType(this, rootType.withStaticTyping());
      }

   }

   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config) {
      this._config = config;
      this._serializerProvider = mapper._serializerProvider;
      this._serializerFactory = mapper._serializerFactory;
      this._generatorFactory = mapper._jsonFactory;
      this._generatorSettings = ObjectWriter.GeneratorSettings.empty;
      this._prefetch = ObjectWriter.Prefetch.empty;
   }

   protected ObjectWriter(ObjectMapper mapper, SerializationConfig config, FormatSchema s) {
      this._config = config;
      this._serializerProvider = mapper._serializerProvider;
      this._serializerFactory = mapper._serializerFactory;
      this._generatorFactory = mapper._jsonFactory;
      this._generatorSettings = s == null ? ObjectWriter.GeneratorSettings.empty : new GeneratorSettings((PrettyPrinter)null, s, (CharacterEscapes)null, (SerializableString)null);
      this._prefetch = ObjectWriter.Prefetch.empty;
   }

   protected ObjectWriter(ObjectWriter base, SerializationConfig config, GeneratorSettings genSettings, Prefetch prefetch) {
      this._config = config;
      this._serializerProvider = base._serializerProvider;
      this._serializerFactory = base._serializerFactory;
      this._generatorFactory = base._generatorFactory;
      this._generatorSettings = genSettings;
      this._prefetch = prefetch;
   }

   protected ObjectWriter(ObjectWriter base, SerializationConfig config) {
      this._config = config;
      this._serializerProvider = base._serializerProvider;
      this._serializerFactory = base._serializerFactory;
      this._generatorFactory = base._generatorFactory;
      this._generatorSettings = base._generatorSettings;
      this._prefetch = base._prefetch;
   }

   protected ObjectWriter(ObjectWriter base, JsonFactory f) {
      this._config = (SerializationConfig)base._config.with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, f.requiresPropertyOrdering());
      this._serializerProvider = base._serializerProvider;
      this._serializerFactory = base._serializerFactory;
      this._generatorFactory = f;
      this._generatorSettings = base._generatorSettings;
      this._prefetch = base._prefetch;
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   protected ObjectWriter _new(ObjectWriter base, JsonFactory f) {
      return new ObjectWriter(base, f);
   }

   protected ObjectWriter _new(ObjectWriter base, SerializationConfig config) {
      return config == this._config ? this : new ObjectWriter(base, config);
   }

   protected ObjectWriter _new(GeneratorSettings genSettings, Prefetch prefetch) {
      return this._generatorSettings == genSettings && this._prefetch == prefetch ? this : new ObjectWriter(this, this._config, genSettings, prefetch);
   }

   protected SequenceWriter _newSequenceWriter(boolean wrapInArray, JsonGenerator gen, boolean managedInput) throws IOException {
      return (new SequenceWriter(this._serializerProvider(), this._configureGenerator(gen), managedInput, this._prefetch)).init(wrapInArray);
   }

   public ObjectWriter with(SerializationFeature feature) {
      return this._new(this, this._config.with(feature));
   }

   public ObjectWriter with(SerializationFeature first, SerializationFeature... other) {
      return this._new(this, this._config.with(first, other));
   }

   public ObjectWriter withFeatures(SerializationFeature... features) {
      return this._new(this, this._config.withFeatures(features));
   }

   public ObjectWriter without(SerializationFeature feature) {
      return this._new(this, this._config.without(feature));
   }

   public ObjectWriter without(SerializationFeature first, SerializationFeature... other) {
      return this._new(this, this._config.without(first, other));
   }

   public ObjectWriter withoutFeatures(SerializationFeature... features) {
      return this._new(this, this._config.withoutFeatures(features));
   }

   public ObjectWriter with(DatatypeFeature feature) {
      return this._new(this, (SerializationConfig)this._config.with((DatatypeFeature)feature));
   }

   public ObjectWriter withFeatures(DatatypeFeature... features) {
      return this._new(this, (SerializationConfig)this._config.withFeatures((DatatypeFeature[])features));
   }

   public ObjectWriter without(DatatypeFeature feature) {
      return this._new(this, (SerializationConfig)this._config.without((DatatypeFeature)feature));
   }

   public ObjectWriter withoutFeatures(DatatypeFeature... features) {
      return this._new(this, (SerializationConfig)this._config.withoutFeatures((DatatypeFeature[])features));
   }

   public ObjectWriter with(JsonGenerator.Feature feature) {
      return this._new(this, this._config.with(feature));
   }

   public ObjectWriter withFeatures(JsonGenerator.Feature... features) {
      return this._new(this, this._config.withFeatures(features));
   }

   public ObjectWriter without(JsonGenerator.Feature feature) {
      return this._new(this, this._config.without(feature));
   }

   public ObjectWriter withoutFeatures(JsonGenerator.Feature... features) {
      return this._new(this, this._config.withoutFeatures(features));
   }

   public ObjectWriter with(StreamWriteFeature feature) {
      return this._new(this, this._config.with(feature.mappedFeature()));
   }

   public ObjectWriter without(StreamWriteFeature feature) {
      return this._new(this, this._config.without(feature.mappedFeature()));
   }

   public ObjectWriter with(FormatFeature feature) {
      return this._new(this, this._config.with(feature));
   }

   public ObjectWriter withFeatures(FormatFeature... features) {
      return this._new(this, this._config.withFeatures(features));
   }

   public ObjectWriter without(FormatFeature feature) {
      return this._new(this, this._config.without(feature));
   }

   public ObjectWriter withoutFeatures(FormatFeature... features) {
      return this._new(this, this._config.withoutFeatures(features));
   }

   public ObjectWriter forType(JavaType rootType) {
      return this._new(this._generatorSettings, this._prefetch.forRootType(this, rootType));
   }

   public ObjectWriter forType(Class<?> rootType) {
      return this.forType(this._config.constructType(rootType));
   }

   public ObjectWriter forType(TypeReference<?> rootType) {
      return this.forType(this._config.getTypeFactory().constructType(rootType.getType()));
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter withType(JavaType rootType) {
      return this.forType(rootType);
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter withType(Class<?> rootType) {
      return this.forType(rootType);
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter withType(TypeReference<?> rootType) {
      return this.forType(rootType);
   }

   public ObjectWriter with(DateFormat df) {
      return this._new(this, this._config.with(df));
   }

   public ObjectWriter withDefaultPrettyPrinter() {
      return this.with(this._config.getDefaultPrettyPrinter());
   }

   public ObjectWriter with(FilterProvider filterProvider) {
      return filterProvider == this._config.getFilterProvider() ? this : this._new(this, this._config.withFilters(filterProvider));
   }

   public ObjectWriter with(PrettyPrinter pp) {
      return this._new(this._generatorSettings.with(pp), this._prefetch);
   }

   public ObjectWriter withRootName(String rootName) {
      return this._new(this, (SerializationConfig)this._config.withRootName((String)rootName));
   }

   public ObjectWriter withRootName(PropertyName rootName) {
      return this._new(this, this._config.withRootName(rootName));
   }

   public ObjectWriter withoutRootName() {
      return this._new(this, this._config.withRootName(PropertyName.NO_NAME));
   }

   public ObjectWriter with(FormatSchema schema) {
      this._verifySchemaType(schema);
      return this._new(this._generatorSettings.with(schema), this._prefetch);
   }

   /** @deprecated */
   @Deprecated
   public ObjectWriter withSchema(FormatSchema schema) {
      return this.with(schema);
   }

   public ObjectWriter withView(Class<?> view) {
      return this._new(this, this._config.withView(view));
   }

   public ObjectWriter with(Locale l) {
      return this._new(this, (SerializationConfig)this._config.with((Locale)l));
   }

   public ObjectWriter with(TimeZone tz) {
      return this._new(this, (SerializationConfig)this._config.with((TimeZone)tz));
   }

   public ObjectWriter with(Base64Variant b64variant) {
      return this._new(this, (SerializationConfig)this._config.with((Base64Variant)b64variant));
   }

   public ObjectWriter with(CharacterEscapes escapes) {
      return this._new(this._generatorSettings.with(escapes), this._prefetch);
   }

   public ObjectWriter with(JsonFactory f) {
      return f == this._generatorFactory ? this : this._new(this, f);
   }

   public ObjectWriter with(ContextAttributes attrs) {
      return this._new(this, this._config.with(attrs));
   }

   public ObjectWriter withAttributes(Map<?, ?> attrs) {
      return this._new(this, (SerializationConfig)this._config.withAttributes(attrs));
   }

   public ObjectWriter withAttribute(Object key, Object value) {
      return this._new(this, (SerializationConfig)this._config.withAttribute(key, value));
   }

   public ObjectWriter withoutAttribute(Object key) {
      return this._new(this, (SerializationConfig)this._config.withoutAttribute(key));
   }

   public ObjectWriter withRootValueSeparator(String sep) {
      return this._new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
   }

   public ObjectWriter withRootValueSeparator(SerializableString sep) {
      return this._new(this._generatorSettings.withRootValueSeparator(sep), this._prefetch);
   }

   public JsonGenerator createGenerator(OutputStream out) throws IOException {
      this._assertNotNull("out", out);
      return this._configureGenerator(this._generatorFactory.createGenerator(out, JsonEncoding.UTF8));
   }

   public JsonGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
      this._assertNotNull("out", out);
      return this._configureGenerator(this._generatorFactory.createGenerator(out, enc));
   }

   public JsonGenerator createGenerator(Writer w) throws IOException {
      this._assertNotNull("w", w);
      return this._configureGenerator(this._generatorFactory.createGenerator(w));
   }

   public JsonGenerator createGenerator(File outputFile, JsonEncoding enc) throws IOException {
      this._assertNotNull("outputFile", outputFile);
      return this._configureGenerator(this._generatorFactory.createGenerator(outputFile, enc));
   }

   public JsonGenerator createGenerator(DataOutput out) throws IOException {
      this._assertNotNull("out", out);
      return this._configureGenerator(this._generatorFactory.createGenerator(out));
   }

   public SequenceWriter writeValues(File out) throws IOException {
      return this._newSequenceWriter(false, this.createGenerator(out, JsonEncoding.UTF8), true);
   }

   public SequenceWriter writeValues(JsonGenerator g) throws IOException {
      this._assertNotNull("g", g);
      return this._newSequenceWriter(false, this._configureGenerator(g), false);
   }

   public SequenceWriter writeValues(Writer out) throws IOException {
      return this._newSequenceWriter(false, this.createGenerator(out), true);
   }

   public SequenceWriter writeValues(OutputStream out) throws IOException {
      return this._newSequenceWriter(false, this.createGenerator(out, JsonEncoding.UTF8), true);
   }

   public SequenceWriter writeValues(DataOutput out) throws IOException {
      return this._newSequenceWriter(false, this.createGenerator(out), true);
   }

   public SequenceWriter writeValuesAsArray(File out) throws IOException {
      return this._newSequenceWriter(true, this.createGenerator(out, JsonEncoding.UTF8), true);
   }

   public SequenceWriter writeValuesAsArray(JsonGenerator gen) throws IOException {
      this._assertNotNull("gen", gen);
      return this._newSequenceWriter(true, gen, false);
   }

   public SequenceWriter writeValuesAsArray(Writer out) throws IOException {
      return this._newSequenceWriter(true, this.createGenerator(out), true);
   }

   public SequenceWriter writeValuesAsArray(OutputStream out) throws IOException {
      return this._newSequenceWriter(true, this.createGenerator(out, JsonEncoding.UTF8), true);
   }

   public SequenceWriter writeValuesAsArray(DataOutput out) throws IOException {
      return this._newSequenceWriter(true, this.createGenerator(out), true);
   }

   public boolean isEnabled(SerializationFeature f) {
      return this._config.isEnabled(f);
   }

   public boolean isEnabled(MapperFeature f) {
      return this._config.isEnabled((MapperFeature)f);
   }

   public boolean isEnabled(DatatypeFeature f) {
      return this._config.isEnabled(f);
   }

   /** @deprecated */
   @Deprecated
   public boolean isEnabled(JsonParser.Feature f) {
      return this._generatorFactory.isEnabled(f);
   }

   public boolean isEnabled(JsonGenerator.Feature f) {
      return this._generatorFactory.isEnabled(f);
   }

   public boolean isEnabled(StreamWriteFeature f) {
      return this._generatorFactory.isEnabled(f);
   }

   public SerializationConfig getConfig() {
      return this._config;
   }

   public JsonFactory getFactory() {
      return this._generatorFactory;
   }

   public TypeFactory getTypeFactory() {
      return this._config.getTypeFactory();
   }

   public boolean hasPrefetchedSerializer() {
      return this._prefetch.hasSerializer();
   }

   public ContextAttributes getAttributes() {
      return this._config.getAttributes();
   }

   public void writeValue(JsonGenerator g, Object value) throws IOException {
      this._assertNotNull("g", g);
      this._configureGenerator(g);
      if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         Closeable toClose = (Closeable)value;

         try {
            this._prefetch.serialize(g, value, this._serializerProvider());
            if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
               g.flush();
            }
         } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE((JsonGenerator)null, toClose, e);
            return;
         }

         toClose.close();
      } else {
         this._prefetch.serialize(g, value, this._serializerProvider());
         if (this._config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
            g.flush();
         }
      }

   }

   public void writeValue(File resultFile, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(resultFile, JsonEncoding.UTF8), value);
   }

   public void writeValue(OutputStream out, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(out, JsonEncoding.UTF8), value);
   }

   public void writeValue(Writer w, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(w), value);
   }

   public void writeValue(DataOutput out, Object value) throws IOException, StreamWriteException, DatabindException {
      this._writeValueAndClose(this.createGenerator(out), value);
   }

   public String writeValueAsString(Object value) throws JsonProcessingException {
      BufferRecycler br = this._generatorFactory._getBufferRecycler();

      Object var5;
      try {
         SegmentedStringWriter sw = new SegmentedStringWriter(br);
         Throwable var4 = null;

         try {
            this._writeValueAndClose(this.createGenerator((Writer)sw), value);
            var5 = sw.getAndClear();
         } catch (Throwable var25) {
            var5 = var25;
            var4 = var25;
            throw var25;
         } finally {
            if (sw != null) {
               if (var4 != null) {
                  try {
                     sw.close();
                  } catch (Throwable var24) {
                     var4.addSuppressed(var24);
                  }
               } else {
                  sw.close();
               }
            }

         }
      } catch (JsonProcessingException e) {
         throw e;
      } catch (IOException e) {
         throw JsonMappingException.fromUnexpectedIOE(e);
      } finally {
         br.releaseToPool();
      }

      return (String)var5;
   }

   public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
      BufferRecycler br = this._generatorFactory._getBufferRecycler();

      Object var5;
      try {
         ByteArrayBuilder bb = new ByteArrayBuilder(br);
         Throwable var4 = null;

         try {
            this._writeValueAndClose(this.createGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
            var5 = bb.getClearAndRelease();
         } catch (Throwable var25) {
            var5 = var25;
            var4 = var25;
            throw var25;
         } finally {
            if (bb != null) {
               if (var4 != null) {
                  try {
                     bb.close();
                  } catch (Throwable var24) {
                     var4.addSuppressed(var24);
                  }
               } else {
                  bb.close();
               }
            }

         }
      } catch (JsonProcessingException e) {
         throw e;
      } catch (IOException e) {
         throw JsonMappingException.fromUnexpectedIOE(e);
      } finally {
         br.releaseToPool();
      }

      return (byte[])var5;
   }

   public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      this._assertNotNull("type", type);
      this._assertNotNull("visitor", visitor);
      this._serializerProvider().acceptJsonFormatVisitor(type, visitor);
   }

   public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
      this._assertNotNull("type", type);
      this._assertNotNull("visitor", visitor);
      this.acceptJsonFormatVisitor(this._config.constructType(type), visitor);
   }

   /** @deprecated */
   @Deprecated
   public boolean canSerialize(Class<?> type) {
      this._assertNotNull("type", type);
      return this._serializerProvider().hasSerializerFor(type, (AtomicReference)null);
   }

   /** @deprecated */
   @Deprecated
   public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
      this._assertNotNull("type", type);
      return this._serializerProvider().hasSerializerFor(type, cause);
   }

   protected DefaultSerializerProvider _serializerProvider() {
      return this._serializerProvider.createInstance(this._config, this._serializerFactory);
   }

   protected void _verifySchemaType(FormatSchema schema) {
      if (schema != null && !this._generatorFactory.canUseSchema(schema)) {
         throw new IllegalArgumentException("Cannot use FormatSchema of type " + schema.getClass().getName() + " for format " + this._generatorFactory.getFormatName());
      }
   }

   protected final void _writeValueAndClose(JsonGenerator gen, Object value) throws IOException {
      if (this._config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
         this._writeCloseable(gen, value);
      } else {
         try {
            this._prefetch.serialize(gen, value, this._serializerProvider());
         } catch (Exception e) {
            ClassUtil.closeOnFailAndThrowAsIOE(gen, e);
            return;
         }

         gen.close();
      }
   }

   private final void _writeCloseable(JsonGenerator gen, Object value) throws IOException {
      Closeable toClose = (Closeable)value;

      try {
         this._prefetch.serialize(gen, value, this._serializerProvider());
         Closeable tmpToClose = toClose;
         toClose = null;
         tmpToClose.close();
      } catch (Exception e) {
         ClassUtil.closeOnFailAndThrowAsIOE(gen, toClose, e);
         return;
      }

      gen.close();
   }

   protected final JsonGenerator _configureGenerator(JsonGenerator gen) {
      this._config.initialize(gen);
      this._generatorSettings.initialize(gen);
      return gen;
   }

   protected final void _assertNotNull(String paramName, Object src) {
      if (src == null) {
         throw new IllegalArgumentException(String.format("argument \"%s\" is null", paramName));
      }
   }

   public static final class GeneratorSettings implements Serializable {
      private static final long serialVersionUID = 1L;
      public static final GeneratorSettings empty = new GeneratorSettings((PrettyPrinter)null, (FormatSchema)null, (CharacterEscapes)null, (SerializableString)null);
      public final PrettyPrinter prettyPrinter;
      public final FormatSchema schema;
      public final CharacterEscapes characterEscapes;
      public final SerializableString rootValueSeparator;

      public GeneratorSettings(PrettyPrinter pp, FormatSchema sch, CharacterEscapes esc, SerializableString rootSep) {
         this.prettyPrinter = pp;
         this.schema = sch;
         this.characterEscapes = esc;
         this.rootValueSeparator = rootSep;
      }

      public GeneratorSettings with(PrettyPrinter pp) {
         if (pp == null) {
            pp = ObjectWriter.NULL_PRETTY_PRINTER;
         }

         return pp == this.prettyPrinter ? this : new GeneratorSettings(pp, this.schema, this.characterEscapes, this.rootValueSeparator);
      }

      public GeneratorSettings with(FormatSchema sch) {
         return this.schema == sch ? this : new GeneratorSettings(this.prettyPrinter, sch, this.characterEscapes, this.rootValueSeparator);
      }

      public GeneratorSettings with(CharacterEscapes esc) {
         return this.characterEscapes == esc ? this : new GeneratorSettings(this.prettyPrinter, this.schema, esc, this.rootValueSeparator);
      }

      public GeneratorSettings withRootValueSeparator(String sep) {
         if (sep == null) {
            return this.rootValueSeparator == null ? this : new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, (SerializableString)null);
         } else {
            return sep.equals(this._rootValueSeparatorAsString()) ? this : new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, new SerializedString(sep));
         }
      }

      public GeneratorSettings withRootValueSeparator(SerializableString sep) {
         if (sep == null) {
            return this.rootValueSeparator == null ? this : new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, (SerializableString)null);
         } else {
            return sep.equals(this.rootValueSeparator) ? this : new GeneratorSettings(this.prettyPrinter, this.schema, this.characterEscapes, sep);
         }
      }

      private final String _rootValueSeparatorAsString() {
         return this.rootValueSeparator == null ? null : this.rootValueSeparator.getValue();
      }

      public void initialize(JsonGenerator gen) {
         PrettyPrinter pp = this.prettyPrinter;
         if (this.prettyPrinter != null) {
            if (pp == ObjectWriter.NULL_PRETTY_PRINTER) {
               gen.setPrettyPrinter((PrettyPrinter)null);
            } else {
               if (pp instanceof Instantiatable) {
                  pp = (PrettyPrinter)((Instantiatable)pp).createInstance();
               }

               gen.setPrettyPrinter(pp);
            }
         }

         if (this.characterEscapes != null) {
            gen.setCharacterEscapes(this.characterEscapes);
         }

         if (this.schema != null) {
            gen.setSchema(this.schema);
         }

         if (this.rootValueSeparator != null) {
            gen.setRootValueSeparator(this.rootValueSeparator);
         }

      }
   }

   public static final class Prefetch implements Serializable {
      private static final long serialVersionUID = 1L;
      public static final Prefetch empty = new Prefetch((JavaType)null, (JsonSerializer)null, (TypeSerializer)null);
      private final JavaType rootType;
      private final JsonSerializer<Object> valueSerializer;
      private final TypeSerializer typeSerializer;

      private Prefetch(JavaType rootT, JsonSerializer<Object> ser, TypeSerializer typeSer) {
         this.rootType = rootT;
         this.valueSerializer = ser;
         this.typeSerializer = typeSer;
      }

      public Prefetch forRootType(ObjectWriter parent, JavaType newType) {
         if (newType == null) {
            return this.rootType != null && this.valueSerializer != null ? new Prefetch((JavaType)null, (JsonSerializer)null, (TypeSerializer)null) : this;
         } else if (newType.equals(this.rootType)) {
            return this;
         } else if (newType.isJavaLangObject()) {
            DefaultSerializerProvider prov = parent._serializerProvider();

            TypeSerializer typeSer;
            try {
               typeSer = prov.findTypeSerializer(newType);
            } catch (JsonMappingException e) {
               throw new RuntimeJsonMappingException(e);
            }

            return new Prefetch((JavaType)null, (JsonSerializer)null, typeSer);
         } else {
            if (parent.isEnabled(SerializationFeature.EAGER_SERIALIZER_FETCH)) {
               DefaultSerializerProvider prov = parent._serializerProvider();

               try {
                  JsonSerializer<Object> ser = prov.findTypedValueSerializer(newType, true, (BeanProperty)null);
                  if (ser instanceof TypeWrappedSerializer) {
                     return new Prefetch(newType, (JsonSerializer)null, ((TypeWrappedSerializer)ser).typeSerializer());
                  }

                  return new Prefetch(newType, ser, (TypeSerializer)null);
               } catch (DatabindException var7) {
               }
            }

            return new Prefetch(newType, (JsonSerializer)null, this.typeSerializer);
         }
      }

      public final JsonSerializer<Object> getValueSerializer() {
         return this.valueSerializer;
      }

      public final TypeSerializer getTypeSerializer() {
         return this.typeSerializer;
      }

      public boolean hasSerializer() {
         return this.valueSerializer != null || this.typeSerializer != null;
      }

      public void serialize(JsonGenerator gen, Object value, DefaultSerializerProvider prov) throws IOException {
         if (this.typeSerializer != null) {
            prov.serializePolymorphic(gen, value, this.rootType, this.valueSerializer, this.typeSerializer);
         } else if (this.valueSerializer != null) {
            prov.serializeValue(gen, value, this.rootType, this.valueSerializer);
         } else if (this.rootType != null) {
            prov.serializeValue(gen, value, this.rootType);
         } else {
            prov.serializeValue(gen, value);
         }

      }
   }
}
