package com.fasterxml.jackson.core.util;

import java.io.Serializable;
import java.util.Objects;

public class Separators implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String DEFAULT_ROOT_VALUE_SEPARATOR = " ";
   public static final String DEFAULT_OBJECT_EMPTY_SEPARATOR = " ";
   public static final String DEFAULT_ARRAY_EMPTY_SEPARATOR = " ";
   private final char objectFieldValueSeparator;
   private final Spacing objectFieldValueSpacing;
   private final char objectEntrySeparator;
   private final Spacing objectEntrySpacing;
   private final String objectEmptySeparator;
   private final char arrayValueSeparator;
   private final Spacing arrayValueSpacing;
   private final String arrayEmptySeparator;
   private final String rootSeparator;

   public static Separators createDefaultInstance() {
      return new Separators();
   }

   public Separators() {
      this(':', ',', ',');
   }

   public Separators(char objectFieldValueSeparator, char objectEntrySeparator, char arrayValueSeparator) {
      this(" ", objectFieldValueSeparator, Separators.Spacing.BOTH, objectEntrySeparator, Separators.Spacing.NONE, " ", arrayValueSeparator, Separators.Spacing.NONE, " ");
   }

   /** @deprecated */
   @Deprecated
   public Separators(String rootSeparator, char objectFieldValueSeparator, Spacing objectFieldValueSpacing, char objectEntrySeparator, Spacing objectEntrySpacing, char arrayValueSeparator, Spacing arrayValueSpacing) {
      this(rootSeparator, objectFieldValueSeparator, objectFieldValueSpacing, objectEntrySeparator, objectEntrySpacing, " ", arrayValueSeparator, arrayValueSpacing, " ");
   }

   public Separators(String rootSeparator, char objectFieldValueSeparator, Spacing objectFieldValueSpacing, char objectEntrySeparator, Spacing objectEntrySpacing, String objectEmptySeparator, char arrayValueSeparator, Spacing arrayValueSpacing, String arrayEmptySeparator) {
      this.rootSeparator = rootSeparator;
      this.objectFieldValueSeparator = objectFieldValueSeparator;
      this.objectFieldValueSpacing = objectFieldValueSpacing;
      this.objectEntrySeparator = objectEntrySeparator;
      this.objectEntrySpacing = objectEntrySpacing;
      this.objectEmptySeparator = objectEmptySeparator;
      this.arrayValueSeparator = arrayValueSeparator;
      this.arrayValueSpacing = arrayValueSpacing;
      this.arrayEmptySeparator = arrayEmptySeparator;
   }

   public Separators withRootSeparator(String sep) {
      return Objects.equals(this.rootSeparator, sep) ? this : new Separators(sep, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withObjectFieldValueSeparator(char sep) {
      return this.objectFieldValueSeparator == sep ? this : new Separators(this.rootSeparator, sep, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withObjectFieldValueSpacing(Spacing spacing) {
      return this.objectFieldValueSpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, spacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withObjectEntrySeparator(char sep) {
      return this.objectEntrySeparator == sep ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, sep, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withObjectEntrySpacing(Spacing spacing) {
      return this.objectEntrySpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, spacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withObjectEmptySeparator(String sep) {
      return Objects.equals(this.objectEmptySeparator, sep) ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, sep, this.arrayValueSeparator, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withArrayValueSeparator(char sep) {
      return this.arrayValueSeparator == sep ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, sep, this.arrayValueSpacing, this.arrayEmptySeparator);
   }

   public Separators withArrayValueSpacing(Spacing spacing) {
      return this.arrayValueSpacing == spacing ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, spacing, this.arrayEmptySeparator);
   }

   public Separators withArrayEmptySeparator(String sep) {
      return Objects.equals(this.arrayEmptySeparator, sep) ? this : new Separators(this.rootSeparator, this.objectFieldValueSeparator, this.objectFieldValueSpacing, this.objectEntrySeparator, this.objectEntrySpacing, this.objectEmptySeparator, this.arrayValueSeparator, this.arrayValueSpacing, sep);
   }

   public String getRootSeparator() {
      return this.rootSeparator;
   }

   public char getObjectFieldValueSeparator() {
      return this.objectFieldValueSeparator;
   }

   public Spacing getObjectFieldValueSpacing() {
      return this.objectFieldValueSpacing;
   }

   public char getObjectEntrySeparator() {
      return this.objectEntrySeparator;
   }

   public Spacing getObjectEntrySpacing() {
      return this.objectEntrySpacing;
   }

   public String getObjectEmptySeparator() {
      return this.objectEmptySeparator;
   }

   public char getArrayValueSeparator() {
      return this.arrayValueSeparator;
   }

   public Spacing getArrayValueSpacing() {
      return this.arrayValueSpacing;
   }

   public String getArrayEmptySeparator() {
      return this.arrayEmptySeparator;
   }

   public static enum Spacing {
      NONE("", ""),
      BEFORE(" ", ""),
      AFTER("", " "),
      BOTH(" ", " ");

      private final String spacesBefore;
      private final String spacesAfter;

      private Spacing(String spacesBefore, String spacesAfter) {
         this.spacesBefore = spacesBefore;
         this.spacesAfter = spacesAfter;
      }

      public String spacesBefore() {
         return this.spacesBefore;
      }

      public String spacesAfter() {
         return this.spacesAfter;
      }

      public String apply(char separator) {
         return this.spacesBefore + separator + this.spacesAfter;
      }
   }
}
