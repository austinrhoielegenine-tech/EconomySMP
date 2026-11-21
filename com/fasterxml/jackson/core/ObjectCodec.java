package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Iterator;

public abstract class ObjectCodec extends TreeCodec implements Versioned {
   protected ObjectCodec() {
   }

   public abstract Version version();

   public abstract <T> T readValue(JsonParser var1, Class<T> var2) throws IOException;

   public abstract <T> T readValue(JsonParser var1, TypeReference<T> var2) throws IOException;

   public abstract <T> T readValue(JsonParser var1, ResolvedType var2) throws IOException;

   public abstract <T> Iterator<T> readValues(JsonParser var1, Class<T> var2) throws IOException;

   public abstract <T> Iterator<T> readValues(JsonParser var1, TypeReference<T> var2) throws IOException;

   public abstract <T> Iterator<T> readValues(JsonParser var1, ResolvedType var2) throws IOException;

   public abstract void writeValue(JsonGenerator var1, Object var2) throws IOException;

   public abstract <T extends TreeNode> T readTree(JsonParser var1) throws IOException;

   public abstract void writeTree(JsonGenerator var1, TreeNode var2) throws IOException;

   public abstract TreeNode createObjectNode();

   public abstract TreeNode createArrayNode();

   public abstract JsonParser treeAsTokens(TreeNode var1);

   public abstract <T> T treeToValue(TreeNode var1, Class<T> var2) throws JsonProcessingException;

   /** @deprecated */
   @Deprecated
   public JsonFactory getJsonFactory() {
      return this.getFactory();
   }

   public JsonFactory getFactory() {
      return this.getJsonFactory();
   }
}
