package net.dv8tion.jda.api;

public enum GatewayEncoding {
   JSON,
   ETF;

   // $FF: synthetic method
   private static GatewayEncoding[] $values() {
      return new GatewayEncoding[]{JSON, ETF};
   }
}
