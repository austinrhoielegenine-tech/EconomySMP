package com.fasterxml.jackson.core.filter;

import com.fasterxml.jackson.core.JsonPointer;

public class JsonPointerBasedFilter extends TokenFilter {
   protected final JsonPointer _pathToMatch;
   protected final boolean _includeAllElements;

   public JsonPointerBasedFilter(String ptrExpr) {
      this(JsonPointer.compile(ptrExpr), false);
   }

   public JsonPointerBasedFilter(JsonPointer pathToMatch) {
      this(pathToMatch, false);
   }

   public JsonPointerBasedFilter(JsonPointer pathToMatch, boolean includeAllElements) {
      this._pathToMatch = pathToMatch;
      this._includeAllElements = includeAllElements;
   }

   protected JsonPointerBasedFilter construct(JsonPointer pathToMatch, boolean includeAllElements) {
      return new JsonPointerBasedFilter(pathToMatch, includeAllElements);
   }

   public TokenFilter includeElement(int index) {
      JsonPointer next;
      if (this._includeAllElements && !this._pathToMatch.mayMatchElement()) {
         next = this._pathToMatch.tail();
      } else {
         next = this._pathToMatch.matchElement(index);
      }

      if (next == null) {
         return null;
      } else {
         return (TokenFilter)(next.matches() ? TokenFilter.INCLUDE_ALL : this.construct(next, this._includeAllElements));
      }
   }

   public TokenFilter includeProperty(String name) {
      JsonPointer next = this._pathToMatch.matchProperty(name);
      if (next == null) {
         return null;
      } else {
         return (TokenFilter)(next.matches() ? TokenFilter.INCLUDE_ALL : this.construct(next, this._includeAllElements));
      }
   }

   public TokenFilter filterStartArray() {
      return this;
   }

   public TokenFilter filterStartObject() {
      return this;
   }

   protected boolean _includeScalar() {
      return this._pathToMatch.matches();
   }

   public String toString() {
      return "[JsonPointerFilter at: " + this._pathToMatch + "]";
   }
}
