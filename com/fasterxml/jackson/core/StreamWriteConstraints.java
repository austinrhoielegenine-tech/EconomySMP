package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import java.io.Serializable;

public class StreamWriteConstraints implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final int DEFAULT_MAX_DEPTH = 1000;
   protected final int _maxNestingDepth;
   private static StreamWriteConstraints DEFAULT = new StreamWriteConstraints(1000);

   public static void overrideDefaultStreamWriteConstraints(StreamWriteConstraints streamWriteConstraints) {
      if (streamWriteConstraints == null) {
         DEFAULT = new StreamWriteConstraints(1000);
      } else {
         DEFAULT = streamWriteConstraints;
      }

   }

   protected StreamWriteConstraints(int maxNestingDepth) {
      this._maxNestingDepth = maxNestingDepth;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static StreamWriteConstraints defaults() {
      return DEFAULT;
   }

   public Builder rebuild() {
      return new Builder(this);
   }

   public int getMaxNestingDepth() {
      return this._maxNestingDepth;
   }

   public void validateNestingDepth(int depth) throws StreamConstraintsException {
      if (depth > this._maxNestingDepth) {
         throw this._constructException("Document nesting depth (%d) exceeds the maximum allowed (%d, from %s)", depth, this._maxNestingDepth, this._constrainRef("getMaxNestingDepth"));
      }
   }

   protected StreamConstraintsException _constructException(String msgTemplate, Object... args) throws StreamConstraintsException {
      throw new StreamConstraintsException(String.format(msgTemplate, args));
   }

   protected String _constrainRef(String method) {
      return "`StreamWriteConstraints." + method + "()`";
   }

   public static final class Builder {
      private int maxNestingDepth;

      public Builder maxNestingDepth(int maxNestingDepth) {
         if (maxNestingDepth < 0) {
            throw new IllegalArgumentException("Cannot set maxNestingDepth to a negative value");
         } else {
            this.maxNestingDepth = maxNestingDepth;
            return this;
         }
      }

      Builder() {
         this(1000);
      }

      Builder(int maxNestingDepth) {
         this.maxNestingDepth = maxNestingDepth;
      }

      Builder(StreamWriteConstraints src) {
         this.maxNestingDepth = src._maxNestingDepth;
      }

      public StreamWriteConstraints build() {
         return new StreamWriteConstraints(this.maxNestingDepth);
      }
   }
}
