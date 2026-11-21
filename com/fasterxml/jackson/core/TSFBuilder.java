package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.InputDecorator;
import com.fasterxml.jackson.core.io.OutputDecorator;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.JsonGeneratorDecorator;
import com.fasterxml.jackson.core.util.JsonRecyclerPools;
import com.fasterxml.jackson.core.util.RecyclerPool;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TSFBuilder<F extends JsonFactory, B extends TSFBuilder<F, B>> {
   protected static final int DEFAULT_FACTORY_FEATURE_FLAGS = JsonFactory.Feature.collectDefaults();
   protected static final int DEFAULT_PARSER_FEATURE_FLAGS = JsonParser.Feature.collectDefaults();
   protected static final int DEFAULT_GENERATOR_FEATURE_FLAGS = JsonGenerator.Feature.collectDefaults();
   protected int _factoryFeatures;
   protected int _streamReadFeatures;
   protected int _streamWriteFeatures;
   protected RecyclerPool<BufferRecycler> _recyclerPool;
   protected InputDecorator _inputDecorator;
   protected OutputDecorator _outputDecorator;
   protected StreamReadConstraints _streamReadConstraints;
   protected StreamWriteConstraints _streamWriteConstraints;
   protected ErrorReportConfiguration _errorReportConfiguration;
   protected List<JsonGeneratorDecorator> _generatorDecorators;

   protected TSFBuilder() {
      this(DEFAULT_FACTORY_FEATURE_FLAGS, DEFAULT_PARSER_FEATURE_FLAGS, DEFAULT_GENERATOR_FEATURE_FLAGS);
   }

   protected TSFBuilder(JsonFactory base) {
      this(base._factoryFeatures, base._parserFeatures, base._generatorFeatures);
      this._inputDecorator = base._inputDecorator;
      this._outputDecorator = base._outputDecorator;
      this._streamReadConstraints = base._streamReadConstraints;
      this._streamWriteConstraints = base._streamWriteConstraints;
      this._errorReportConfiguration = base._errorReportConfiguration;
      this._generatorDecorators = _copy(base._generatorDecorators);
   }

   protected TSFBuilder(int factoryFeatures, int parserFeatures, int generatorFeatures) {
      this._recyclerPool = JsonRecyclerPools.defaultPool();
      this._factoryFeatures = factoryFeatures;
      this._streamReadFeatures = parserFeatures;
      this._streamWriteFeatures = generatorFeatures;
      this._inputDecorator = null;
      this._outputDecorator = null;
      this._streamReadConstraints = StreamReadConstraints.defaults();
      this._streamWriteConstraints = StreamWriteConstraints.defaults();
      this._errorReportConfiguration = ErrorReportConfiguration.defaults();
      this._generatorDecorators = null;
   }

   protected static <T> List<T> _copy(List<T> src) {
      return (List<T>)(src == null ? src : new ArrayList(src));
   }

   public int factoryFeaturesMask() {
      return this._factoryFeatures;
   }

   public int streamReadFeatures() {
      return this._streamReadFeatures;
   }

   public int streamWriteFeatures() {
      return this._streamWriteFeatures;
   }

   public RecyclerPool<BufferRecycler> recyclerPool() {
      return this._recyclerPool;
   }

   public InputDecorator inputDecorator() {
      return this._inputDecorator;
   }

   public OutputDecorator outputDecorator() {
      return this._outputDecorator;
   }

   public B enable(JsonFactory.Feature f) {
      this._factoryFeatures |= f.getMask();
      return (B)this._this();
   }

   public B disable(JsonFactory.Feature f) {
      this._factoryFeatures &= ~f.getMask();
      return (B)this._this();
   }

   public B configure(JsonFactory.Feature f, boolean state) {
      return (B)(state ? this.enable(f) : this.disable(f));
   }

   public B enable(StreamReadFeature f) {
      this._streamReadFeatures |= f.mappedFeature().getMask();
      return (B)this._this();
   }

   public B enable(StreamReadFeature first, StreamReadFeature... other) {
      this._streamReadFeatures |= first.mappedFeature().getMask();

      for(StreamReadFeature f : other) {
         this._streamReadFeatures |= f.mappedFeature().getMask();
      }

      return (B)this._this();
   }

   public B disable(StreamReadFeature f) {
      this._streamReadFeatures &= ~f.mappedFeature().getMask();
      return (B)this._this();
   }

   public B disable(StreamReadFeature first, StreamReadFeature... other) {
      this._streamReadFeatures &= ~first.mappedFeature().getMask();

      for(StreamReadFeature f : other) {
         this._streamReadFeatures &= ~f.mappedFeature().getMask();
      }

      return (B)this._this();
   }

   public B configure(StreamReadFeature f, boolean state) {
      return (B)(state ? this.enable(f) : this.disable(f));
   }

   public B enable(StreamWriteFeature f) {
      this._streamWriteFeatures |= f.mappedFeature().getMask();
      return (B)this._this();
   }

   public B enable(StreamWriteFeature first, StreamWriteFeature... other) {
      this._streamWriteFeatures |= first.mappedFeature().getMask();

      for(StreamWriteFeature f : other) {
         this._streamWriteFeatures |= f.mappedFeature().getMask();
      }

      return (B)this._this();
   }

   public B disable(StreamWriteFeature f) {
      this._streamWriteFeatures &= ~f.mappedFeature().getMask();
      return (B)this._this();
   }

   public B disable(StreamWriteFeature first, StreamWriteFeature... other) {
      this._streamWriteFeatures &= ~first.mappedFeature().getMask();

      for(StreamWriteFeature f : other) {
         this._streamWriteFeatures &= ~f.mappedFeature().getMask();
      }

      return (B)this._this();
   }

   public B configure(StreamWriteFeature f, boolean state) {
      return (B)(state ? this.enable(f) : this.disable(f));
   }

   public B enable(JsonReadFeature f) {
      return (B)this._failNonJSON(f);
   }

   public B enable(JsonReadFeature first, JsonReadFeature... other) {
      return (B)this._failNonJSON(first);
   }

   public B disable(JsonReadFeature f) {
      return (B)this._failNonJSON(f);
   }

   public B disable(JsonReadFeature first, JsonReadFeature... other) {
      return (B)this._failNonJSON(first);
   }

   public B configure(JsonReadFeature f, boolean state) {
      return (B)this._failNonJSON(f);
   }

   private B _failNonJSON(Object feature) {
      throw new IllegalArgumentException("Feature " + feature.getClass().getName() + "#" + feature.toString() + " not supported for non-JSON backend");
   }

   public B enable(JsonWriteFeature f) {
      return (B)this._failNonJSON(f);
   }

   public B enable(JsonWriteFeature first, JsonWriteFeature... other) {
      return (B)this._failNonJSON(first);
   }

   public B disable(JsonWriteFeature f) {
      return (B)this._failNonJSON(f);
   }

   public B disable(JsonWriteFeature first, JsonWriteFeature... other) {
      return (B)this._failNonJSON(first);
   }

   public B configure(JsonWriteFeature f, boolean state) {
      return (B)this._failNonJSON(f);
   }

   public B recyclerPool(RecyclerPool<BufferRecycler> p) {
      this._recyclerPool = (RecyclerPool)Objects.requireNonNull(p);
      return (B)this._this();
   }

   public B inputDecorator(InputDecorator dec) {
      this._inputDecorator = dec;
      return (B)this._this();
   }

   public B outputDecorator(OutputDecorator dec) {
      this._outputDecorator = dec;
      return (B)this._this();
   }

   public B addDecorator(JsonGeneratorDecorator decorator) {
      if (this._generatorDecorators == null) {
         this._generatorDecorators = new ArrayList();
      }

      this._generatorDecorators.add(decorator);
      return (B)this._this();
   }

   public B streamReadConstraints(StreamReadConstraints streamReadConstraints) {
      this._streamReadConstraints = (StreamReadConstraints)Objects.requireNonNull(streamReadConstraints);
      return (B)this._this();
   }

   public B streamWriteConstraints(StreamWriteConstraints streamWriteConstraints) {
      this._streamWriteConstraints = (StreamWriteConstraints)Objects.requireNonNull(streamWriteConstraints);
      return (B)this._this();
   }

   public B errorReportConfiguration(ErrorReportConfiguration errorReportConfiguration) {
      this._errorReportConfiguration = (ErrorReportConfiguration)Objects.requireNonNull(errorReportConfiguration);
      return (B)this._this();
   }

   public abstract F build();

   protected final B _this() {
      return (B)this;
   }

   protected void _legacyEnable(JsonParser.Feature f) {
      if (f != null) {
         this._streamReadFeatures |= f.getMask();
      }

   }

   protected void _legacyDisable(JsonParser.Feature f) {
      if (f != null) {
         this._streamReadFeatures &= ~f.getMask();
      }

   }

   protected void _legacyEnable(JsonGenerator.Feature f) {
      if (f != null) {
         this._streamWriteFeatures |= f.getMask();
      }

   }

   protected void _legacyDisable(JsonGenerator.Feature f) {
      if (f != null) {
         this._streamWriteFeatures &= ~f.getMask();
      }

   }
}
