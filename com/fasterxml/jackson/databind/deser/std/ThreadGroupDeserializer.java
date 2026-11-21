package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public class ThreadGroupDeserializer extends StdNodeBasedDeserializer<ThreadGroup> {
   private static final long serialVersionUID = 1L;

   protected ThreadGroupDeserializer() {
      super(ThreadGroup.class);
   }

   public ThreadGroup convert(JsonNode root, DeserializationContext ctxt) throws IOException {
      String name = root.path("name").asText();
      if (name == null) {
         name = "";
      }

      return new ThreadGroup(name);
   }
}
