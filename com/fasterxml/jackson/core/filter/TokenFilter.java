package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;

public class TokenFilter {
   public static final TokenFilter INCLUDE_ALL = new TokenFilter();

   protected TokenFilter() {
   }

   public TokenFilter filterStartObject() {
      return this;
   }

   public TokenFilter filterStartArray() {
      return this;
   }

   public void filterFinishObject() {
   }

   public void filterFinishArray() {
   }

   public TokenFilter includeProperty(String name) {
      return this;
   }

   public TokenFilter includeElement(int index) {
      return this;
   }

   public TokenFilter includeRootValue(int index) {
      return this;
   }

   public boolean includeEmptyArray(boolean contentsFiltered) {
      return false;
   }

   public boolean includeEmptyObject(boolean contentsFiltered) {
      return false;
   }

   public boolean includeValue(JsonParser p) throws IOException {
      return this._includeScalar();
   }

   public boolean includeBoolean(boolean value) {
      return this._includeScalar();
   }

   public boolean includeNull() {
      return this._includeScalar();
   }

   public boolean includeString(String value) {
      return this._includeScalar();
   }

   public boolean includeString(Reader r, int maxLen) {
      return this._includeScalar();
   }

   public boolean includeNumber(int value) {
      return this._includeScalar();
   }

   public boolean includeNumber(long value) {
      return this._includeScalar();
   }

   public boolean includeNumber(float value) {
      return this._includeScalar();
   }

   public boolean includeNumber(double value) {
      return this._includeScalar();
   }

   public boolean includeNumber(BigDecimal value) {
      return this._includeScalar();
   }

   public boolean includeNumber(BigInteger value) {
      return this._includeScalar();
   }

   public boolean includeBinary() {
      return this._includeScalar();
   }

   public boolean includeRawValue() {
      return this._includeScalar();
   }

   public boolean includeEmbeddedValue(Object value) {
      return this._includeScalar();
   }

   public String toString() {
      return this == INCLUDE_ALL ? "TokenFilter.INCLUDE_ALL" : super.toString();
   }

   protected boolean _includeScalar() {
      return true;
   }

   public static enum Inclusion {
      ONLY_INCLUDE_ALL,
      INCLUDE_ALL_AND_PATH,
      INCLUDE_NON_NULL;
   }
}
