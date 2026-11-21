package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChannelTypesDto {
   DM(1),
   GROUP_DM(3),
   GUILD_TEXT(0),
   GUILD_VOICE(2),
   GUILD_CATEGORY(4),
   GUILD_ANNOUNCEMENT(5),
   ANNOUNCEMENT_THREAD(10),
   PUBLIC_THREAD(11),
   PRIVATE_THREAD(12),
   GUILD_STAGE_VOICE(13),
   GUILD_DIRECTORY(14),
   GUILD_FORUM(15);

   private final int id;

   private ChannelTypesDto(int id) {
      this.id = id;
   }

   @JsonValue
   public int getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static ChannelTypesDto[] $values() {
      return new ChannelTypesDto[]{DM, GROUP_DM, GUILD_TEXT, GUILD_VOICE, GUILD_CATEGORY, GUILD_ANNOUNCEMENT, ANNOUNCEMENT_THREAD, PUBLIC_THREAD, PRIVATE_THREAD, GUILD_STAGE_VOICE, GUILD_DIRECTORY, GUILD_FORUM};
   }
}
