package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookTypesDto {
   GUILD_INCOMING(1),
   CHANNEL_FOLLOWER(2),
   APPLICATION_INCOMING(3);

   private final int id;

   private WebhookTypesDto(int id) {
      this.id = id;
   }

   @JsonValue
   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static WebhookTypesDto[] $values() {
      return new WebhookTypesDto[]{GUILD_INCOMING, CHANNEL_FOLLOWER, APPLICATION_INCOMING};
   }
}
