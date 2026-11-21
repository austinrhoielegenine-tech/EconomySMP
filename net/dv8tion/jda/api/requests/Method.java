package net.dv8tion.jda.api.requests;

public enum Method {
   DELETE,
   GET,
   HEAD,
   OPTIONS,
   POST,
   PUT,
   PATCH;

   public boolean requiresRequestBody() {
      return this == POST || this == PUT || this == PATCH;
   }

   // $FF: synthetic method
   private static Method[] $values() {
      return new Method[]{DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH};
   }
}
