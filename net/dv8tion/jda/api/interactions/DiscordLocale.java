package net.dv8tion.jda.api.interactions;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;

public enum DiscordLocale {
   ARABIC("ar", "Arabic", "اَلْعَرَبِيَّةُ"),
   BULGARIAN("bg", "Bulgarian", "български"),
   CHINESE_CHINA("zh-CN", "Chinese, China", "中文"),
   CHINESE_TAIWAN("zh-TW", "Chinese, Taiwan", "繁體中文"),
   CROATIAN("hr", "Croatian", "Hrvatski"),
   CZECH("cs", "Czech", "Čeština"),
   DANISH("da", "Danish", "Dansk"),
   DUTCH("nl", "Dutch", "Nederlands"),
   ENGLISH_UK("en-GB", "English, UK", "English, UK"),
   ENGLISH_US("en-US", "English, US", "English, US"),
   FINNISH("fi", "Finnish", "Suomi"),
   FRENCH("fr", "French", "Français"),
   GERMAN("de", "German", "Deutsch"),
   GREEK("el", "Greek", "Ελληνικά"),
   HEBREW("he", "Hebrew", "עברית"),
   HINDI("hi", "Hindi", "हिन्दी"),
   HUNGARIAN("hu", "Hungarian", "Magyar"),
   INDONESIAN("id", "Indonesian", "Bahasa Indonesia"),
   ITALIAN("it", "Italian", "Italiano"),
   JAPANESE("ja", "Japanese", "日本語"),
   KOREAN("ko", "Korean", "한국어"),
   LITHUANIAN("lt", "Lithuanian", "Lietuviškai"),
   NORWEGIAN("no", "Norwegian", "Norsk"),
   POLISH("pl", "Polish", "Polski"),
   PORTUGUESE_BRAZILIAN("pt-BR", "Portuguese, Brazilian", "Português do Brasil"),
   ROMANIAN_ROMANIA("ro", "Romanian, Romania", "Română"),
   RUSSIAN("ru", "Russian", "Pусский"),
   SPANISH("es-ES", "Spanish", "Español"),
   SPANISH_LATAM("es-419", "Spanish, LATAM", "Español, LATAM"),
   SWEDISH("sv-SE", "Swedish", "Svenska"),
   THAI("th", "Thai", "ไทย"),
   TURKISH("tr", "Turkish", "Türkçe"),
   UKRAINIAN("uk", "Ukrainian", "Українська"),
   VIETNAMESE("vi", "Vietnamese", "Tiếng Việt"),
   UNKNOWN("unknown", "Unknown", "Unknown");

   private final String locale;
   private final String languageName;
   private final String nativeName;

   private DiscordLocale(String locale, String languageName, String nativeName) {
      this.locale = locale;
      this.languageName = languageName;
      this.nativeName = nativeName;
   }

   @Nonnull
   public String getLocale() {
      return this.locale;
   }

   @Nonnull
   public Locale toLocale() {
      return Locale.forLanguageTag(this.getLocale());
   }

   @Nonnull
   public String getLanguageName() {
      return this.languageName;
   }

   @Nonnull
   public String getNativeName() {
      return this.nativeName;
   }

   @Nonnull
   public static DiscordLocale from(@Nonnull String localeTag) {
      Checks.notNull(localeTag, "Locale tag");

      for(DiscordLocale discordLocale : values()) {
         if (discordLocale.locale.equals(localeTag)) {
            return discordLocale;
         }
      }

      return UNKNOWN;
   }

   @Nonnull
   public static DiscordLocale from(@Nonnull Locale locale) {
      Checks.notNull(locale, "Locale");
      return from(locale.toLanguageTag());
   }

   // $FF: synthetic method
   private static DiscordLocale[] $values() {
      return new DiscordLocale[]{ARABIC, BULGARIAN, CHINESE_CHINA, CHINESE_TAIWAN, CROATIAN, CZECH, DANISH, DUTCH, ENGLISH_UK, ENGLISH_US, FINNISH, FRENCH, GERMAN, GREEK, HEBREW, HINDI, HUNGARIAN, INDONESIAN, ITALIAN, JAPANESE, KOREAN, LITHUANIAN, NORWEGIAN, POLISH, PORTUGUESE_BRAZILIAN, ROMANIAN_ROMANIA, RUSSIAN, SPANISH, SPANISH_LATAM, SWEDISH, THAI, TURKISH, UKRAINIAN, VIETNAMESE, UNKNOWN};
   }
}
