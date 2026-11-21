package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import java.io.IOException;
import java.io.Serializable;

public class DefaultPrettyPrinter implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable {
   private static final long serialVersionUID = 1L;
   /** @deprecated */
   @Deprecated
   public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
   protected Indenter _arrayIndenter;
   protected Indenter _objectIndenter;
   protected SerializableString _rootSeparator;
   /** @deprecated */
   @Deprecated
   protected boolean _spacesInObjectEntries;
   protected transient int _nesting;
   protected Separators _separators;
   protected String _objectFieldValueSeparatorWithSpaces;
   protected String _objectEntrySeparator;
   protected String _objectEmptySeparator;
   protected String _arrayValueSeparator;
   protected String _arrayEmptySeparator;

   public DefaultPrettyPrinter() {
      this(DEFAULT_SEPARATORS);
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter(String rootSeparator) {
      this((SerializableString)(rootSeparator == null ? null : new SerializedString(rootSeparator)));
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter(SerializableString rootSeparator) {
      this(DEFAULT_SEPARATORS.withRootSeparator(rootSeparator.getValue()));
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
      this._arrayIndenter = DefaultPrettyPrinter.FixedSpaceIndenter.instance;
      this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
      this._spacesInObjectEntries = true;
      this._arrayIndenter = base._arrayIndenter;
      this._objectIndenter = base._objectIndenter;
      this._spacesInObjectEntries = base._spacesInObjectEntries;
      this._nesting = base._nesting;
      this._separators = base._separators;
      this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
      this._objectEntrySeparator = base._objectEntrySeparator;
      this._objectEmptySeparator = base._objectEmptySeparator;
      this._arrayValueSeparator = base._arrayValueSeparator;
      this._arrayEmptySeparator = base._arrayEmptySeparator;
      this._rootSeparator = rootSeparator;
   }

   public DefaultPrettyPrinter(Separators separators) {
      this._arrayIndenter = DefaultPrettyPrinter.FixedSpaceIndenter.instance;
      this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
      this._spacesInObjectEntries = true;
      this._separators = separators;
      this._rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
      this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(separators.getObjectFieldValueSeparator());
      this._objectEntrySeparator = separators.getObjectEntrySpacing().apply(separators.getObjectEntrySeparator());
      this._objectEmptySeparator = separators.getObjectEmptySeparator();
      this._arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
      this._arrayEmptySeparator = separators.getArrayEmptySeparator();
   }

   public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
      this._arrayIndenter = DefaultPrettyPrinter.FixedSpaceIndenter.instance;
      this._objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
      this._spacesInObjectEntries = true;
      this._rootSeparator = base._rootSeparator;
      this._arrayIndenter = base._arrayIndenter;
      this._objectIndenter = base._objectIndenter;
      this._spacesInObjectEntries = base._spacesInObjectEntries;
      this._nesting = base._nesting;
      this._separators = base._separators;
      this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
      this._objectEntrySeparator = base._objectEntrySeparator;
      this._objectEmptySeparator = base._objectEmptySeparator;
      this._arrayValueSeparator = base._arrayValueSeparator;
      this._arrayEmptySeparator = base._arrayEmptySeparator;
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
      if (this._rootSeparator != rootSeparator && (rootSeparator == null || !rootSeparator.equals(this._rootSeparator))) {
         Separators separators = this._separators.withRootSeparator(rootSeparator == null ? null : rootSeparator.getValue());
         return (new DefaultPrettyPrinter(this)).withSeparators(separators);
      } else {
         return this;
      }
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
      return this.withRootSeparator((SerializableString)(rootSeparator == null ? null : new SerializedString(rootSeparator)));
   }

   public void indentArraysWith(Indenter i) {
      this._arrayIndenter = (Indenter)(i == null ? DefaultPrettyPrinter.NopIndenter.instance : i);
   }

   public void indentObjectsWith(Indenter i) {
      this._objectIndenter = (Indenter)(i == null ? DefaultPrettyPrinter.NopIndenter.instance : i);
   }

   public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
      if (i == null) {
         i = DefaultPrettyPrinter.NopIndenter.instance;
      }

      if (this._arrayIndenter == i) {
         return this;
      } else {
         DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
         pp._arrayIndenter = i;
         return pp;
      }
   }

   public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
      if (i == null) {
         i = DefaultPrettyPrinter.NopIndenter.instance;
      }

      if (this._objectIndenter == i) {
         return this;
      } else {
         DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
         pp._objectIndenter = i;
         return pp;
      }
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter withSpacesInObjectEntries() {
      return this._withSpaces(true);
   }

   /** @deprecated */
   @Deprecated
   public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
      return this._withSpaces(false);
   }

   protected DefaultPrettyPrinter _withSpaces(boolean state) {
      if (this._spacesInObjectEntries == state) {
         return this;
      } else {
         Separators copy = this._separators.withObjectFieldValueSpacing(state ? Separators.Spacing.BOTH : Separators.Spacing.NONE);
         DefaultPrettyPrinter pp = this.withSeparators(copy);
         pp._spacesInObjectEntries = state;
         return pp;
      }
   }

   public DefaultPrettyPrinter withSeparators(Separators separators) {
      DefaultPrettyPrinter result = new DefaultPrettyPrinter(this);
      result._separators = separators;
      result._rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
      result._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(separators.getObjectFieldValueSeparator());
      result._objectEntrySeparator = separators.getObjectEntrySpacing().apply(separators.getObjectEntrySeparator());
      result._objectEmptySeparator = separators.getObjectEmptySeparator();
      result._arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
      result._arrayEmptySeparator = separators.getArrayEmptySeparator();
      return result;
   }

   public DefaultPrettyPrinter createInstance() {
      if (this.getClass() != DefaultPrettyPrinter.class) {
         throw new IllegalStateException("Failed `createInstance()`: " + this.getClass().getName() + " does not override method; it has to");
      } else {
         return new DefaultPrettyPrinter(this);
      }
   }

   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
      if (this._rootSeparator != null) {
         g.writeRaw(this._rootSeparator);
      }

   }

   public void writeStartObject(JsonGenerator g) throws IOException {
      g.writeRaw('{');
      if (!this._objectIndenter.isInline()) {
         ++this._nesting;
      }

   }

   public void beforeObjectEntries(JsonGenerator g) throws IOException {
      this._objectIndenter.writeIndentation(g, this._nesting);
   }

   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
   }

   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._objectEntrySeparator);
      this._objectIndenter.writeIndentation(g, this._nesting);
   }

   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
      if (!this._objectIndenter.isInline()) {
         --this._nesting;
      }

      if (nrOfEntries > 0) {
         this._objectIndenter.writeIndentation(g, this._nesting);
      } else {
         g.writeRaw(this._objectEmptySeparator);
      }

      g.writeRaw('}');
   }

   public void writeStartArray(JsonGenerator g) throws IOException {
      if (!this._arrayIndenter.isInline()) {
         ++this._nesting;
      }

      g.writeRaw('[');
   }

   public void beforeArrayValues(JsonGenerator g) throws IOException {
      this._arrayIndenter.writeIndentation(g, this._nesting);
   }

   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
      g.writeRaw(this._arrayValueSeparator);
      this._arrayIndenter.writeIndentation(g, this._nesting);
   }

   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
      if (!this._arrayIndenter.isInline()) {
         --this._nesting;
      }

      if (nrOfValues > 0) {
         this._arrayIndenter.writeIndentation(g, this._nesting);
      } else {
         g.writeRaw(this._arrayEmptySeparator);
      }

      g.writeRaw(']');
   }

   public static class NopIndenter implements Indenter, Serializable {
      public static final NopIndenter instance = new NopIndenter();

      public void writeIndentation(JsonGenerator g, int level) throws IOException {
      }

      public boolean isInline() {
         return true;
      }
   }

   public static class FixedSpaceIndenter extends NopIndenter {
      public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();

      public void writeIndentation(JsonGenerator g, int level) throws IOException {
         g.writeRaw(' ');
      }

      public boolean isInline() {
         return true;
      }
   }

   public interface Indenter {
      void writeIndentation(JsonGenerator var1, int var2) throws IOException;

      boolean isInline();
   }
}
