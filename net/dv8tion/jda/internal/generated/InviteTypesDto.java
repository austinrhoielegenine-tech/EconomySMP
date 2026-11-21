package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InviteTypesDto {
   GUILD(0),
   GROUP_DM(1),
   FRIEND(2);

   private final int id;

   private InviteTypesDto(int id) {
      this.id = id;
   }

   @JsonValue
   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static InviteTypesDto[] $values() {
      return new InviteTypesDto[]{GUILD, GROUP_DM, FRIEND};
   }
}
