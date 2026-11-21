package com.fasterxml.jackson.databind.type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TypeParser implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final int MAX_TYPE_LENGTH = 64000;
   protected static final int MAX_TYPE_NESTING = 1000;
   protected final TypeFactory _factory;

   public TypeParser(TypeFactory f) {
      this._factory = f;
   }

   public TypeParser withFactory(TypeFactory f) {
      return f == this._factory ? this : new TypeParser(f);
   }

   public JavaType parse(String canonical) throws IllegalArgumentException {
      if (canonical.length() > 64000) {
         throw new IllegalArgumentException(String.format("Failed to parse type %s: too long (%d characters), maximum length allowed: %d", _quoteTruncated(canonical), canonical.length(), 64000));
      } else {
         MyTokenizer tokens = new MyTokenizer(canonical.trim());
         JavaType type = this.parseType(tokens, 1000);
         if (tokens.hasMoreTokens()) {
            throw this._problem(tokens, "Unexpected tokens after complete type");
         } else {
            return type;
         }
      }
   }

   protected JavaType parseType(MyTokenizer tokens, int nestingAllowed) throws IllegalArgumentException {
      if (!tokens.hasMoreTokens()) {
         throw this._problem(tokens, "Unexpected end-of-string");
      } else {
         Class<?> base = this.findClass(tokens.nextToken(), tokens);
         if (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if ("<".equals(token)) {
               List<JavaType> parameterTypes = this.parseTypes(tokens, nestingAllowed - 1);
               TypeBindings b = TypeBindings.create(base, parameterTypes);
               return this._factory._fromClass((ClassStack)null, base, b);
            }

            tokens.pushBack(token);
         }

         return this._factory._fromClass((ClassStack)null, base, TypeBindings.emptyBindings());
      }
   }

   protected List<JavaType> parseTypes(MyTokenizer tokens, int nestingAllowed) throws IllegalArgumentException {
      if (nestingAllowed < 0) {
         throw this._problem(tokens, "too deeply nested; exceeds maximum of 1000 nesting levels");
      } else {
         ArrayList<JavaType> types = new ArrayList();

         while(tokens.hasMoreTokens()) {
            types.add(this.parseType(tokens, nestingAllowed));
            if (!tokens.hasMoreTokens()) {
               break;
            }

            String token = tokens.nextToken();
            if (">".equals(token)) {
               return types;
            }

            if (!",".equals(token)) {
               throw this._problem(tokens, "Unexpected token '" + token + "', expected ',' or '>')");
            }
         }

         throw this._problem(tokens, "Unexpected end-of-string");
      }
   }

   protected Class<?> findClass(String className, MyTokenizer tokens) {
      try {
         return this._factory.findClass(className);
      } catch (Exception e) {
         ClassUtil.throwIfRTE(e);
         throw this._problem(tokens, "Cannot locate class '" + className + "', problem: " + e.getMessage());
      }
   }

   protected IllegalArgumentException _problem(MyTokenizer tokens, String msg) {
      return new IllegalArgumentException(String.format("Failed to parse type %s (remaining: %s): %s", _quoteTruncated(tokens.getAllInput()), _quoteTruncated(tokens.getRemainingInput()), msg));
   }

   private static String _quoteTruncated(String str) {
      return str.length() <= 1000 ? "'" + str + "'" : String.format("'%s...'[truncated %d charaters]", str.substring(0, 1000), str.length() - 1000);
   }

   protected static final class MyTokenizer extends StringTokenizer {
      protected final String _input;
      protected int _index;
      protected String _pushbackToken;

      public MyTokenizer(String str) {
         super(str, "<,>", true);
         this._input = str;
      }

      public boolean hasMoreTokens() {
         return this._pushbackToken != null || super.hasMoreTokens();
      }

      public String nextToken() {
         String token;
         if (this._pushbackToken != null) {
            token = this._pushbackToken;
            this._pushbackToken = null;
         } else {
            token = super.nextToken();
            this._index += token.length();
            token = token.trim();
         }

         return token;
      }

      public void pushBack(String token) {
         this._pushbackToken = token;
      }

      public String getAllInput() {
         return this._input;
      }

      public String getRemainingInput() {
         return this._input.substring(this._index);
      }
   }
}
