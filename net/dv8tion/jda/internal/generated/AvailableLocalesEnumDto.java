package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonValue;
import javax.annotation.Nonnull;

public enum AvailableLocalesEnumDto {
   AR("ar"),
   BG("bg"),
   CS("cs"),
   DA("da"),
   DE("de"),
   EL("el"),
   EN_GB("en-GB"),
   EN_US("en-US"),
   ES_419("es-419"),
   ES_ES("es-ES"),
   FI("fi"),
   FR("fr"),
   HE("he"),
   HI("hi"),
   HR("hr"),
   HU("hu"),
   ID("id"),
   IT("it"),
   JA("ja"),
   KO("ko"),
   LT("lt"),
   NL("nl"),
   NO("no"),
   PL("pl"),
   PT_BR("pt-BR"),
   RO("ro"),
   RU("ru"),
   SV_SE("sv-SE"),
   TH("th"),
   TR("tr"),
   UK("uk"),
   VI("vi"),
   ZH_CN("zh-CN"),
   ZH_TW("zh-TW");

   private final String id;

   private AvailableLocalesEnumDto(String id) {
      this.id = id;
   }

   @JsonValue
   @Nonnull
   public String getId() {
      return this.id;
   }

   // $FF: synthetic method
   private static AvailableLocalesEnumDto[] $values() {
      return new AvailableLocalesEnumDto[]{AR, BG, CS, DA, DE, EL, EN_GB, EN_US, ES_419, ES_ES, FI, FR, HE, HI, HR, HU, ID, IT, JA, KO, LT, NL, NO, PL, PT_BR, RO, RU, SV_SE, TH, TR, UK, VI, ZH_CN, ZH_TW};
   }
}
