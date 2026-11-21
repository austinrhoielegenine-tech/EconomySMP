package net.dv8tion.jda.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;

public enum Region {
   BRAZIL("brazil", "Brazil", "\ud83c\udde7\ud83c\uddf7", false),
   HONG_KONG("hongkong", "Hong Kong", "\ud83c\udded\ud83c\uddf0", false),
   INDIA("india", "India", "\ud83c\uddee\ud83c\uddf3", false),
   JAPAN("japan", "Japan", "\ud83c\uddef\ud83c\uddf5", false),
   MILAN("milan", "Milan", "\ud83c\uddee\ud83c\uddf9", false),
   ROTTERDAM("rotterdam", "Rotterdam", "\ud83c\uddf3\ud83c\uddf1", false),
   RUSSIA("russia", "Russia", "\ud83c\uddf7\ud83c\uddfa", false),
   SINGAPORE("singapore", "Singapore", "\ud83c\uddf8\ud83c\uddec", false),
   SOUTH_AFRICA("southafrica", "South Africa", "\ud83c\uddff\ud83c\udde6", false),
   SOUTH_KOREA("south-korea", "South Korea", "\ud83c\uddf0\ud83c\uddf7", false),
   SYDNEY("sydney", "Sydney", "\ud83c\udde6\ud83c\uddfa", false),
   US_CENTRAL("us-central", "US Central", "\ud83c\uddfa\ud83c\uddf8", false),
   US_EAST("us-east", "US East", "\ud83c\uddfa\ud83c\uddf8", false),
   US_SOUTH("us-south", "US South", "\ud83c\uddfa\ud83c\uddf8", false),
   US_WEST("us-west", "US West", "\ud83c\uddfa\ud83c\uddf8", false),
   VIP_BRAZIL("vip-brazil", "Brazil (VIP)", "\ud83c\udde7\ud83c\uddf7", true),
   VIP_HONG_KONG("vip-hongkong", "Hong Kong (VIP)", "\ud83c\udded\ud83c\uddf0", true),
   VIP_INDIA("vip-india", "India (VIP)", "\ud83c\uddee\ud83c\uddf3", true),
   VIP_JAPAN("vip-japan", "Japan (VIP)", "\ud83c\uddef\ud83c\uddf5", true),
   VIP_MILAN("vip-milan", "Milan (VIP)", "\ud83c\uddee\ud83c\uddf9", true),
   VIP_ROTTERDAM("vip-rotterdam", "Rotterdam (VIP)", "\ud83c\uddf3\ud83c\uddf1", true),
   VIP_RUSSIA("vip-russia", "Russia (VIP)", "\ud83c\uddf7\ud83c\uddfa", true),
   VIP_SINGAPORE("vip-singapore", "Singapore (VIP)", "\ud83c\uddf8\ud83c\uddec", true),
   VIP_SOUTH_AFRICA("vip-southafrica", "South Africa (VIP)", "\ud83c\uddff\ud83c\udde6", true),
   VIP_SOUTH_KOREA("vip-south-korea", "South Korea (VIP)", "\ud83c\uddf0\ud83c\uddf7", true),
   VIP_SYDNEY("vip-sydney", "Sydney (VIP)", "\ud83c\udde6\ud83c\uddfa", true),
   VIP_US_CENTRAL("vip-us-central", "US Central (VIP)", "\ud83c\uddfa\ud83c\uddf8", true),
   VIP_US_EAST("vip-us-east", "US East (VIP)", "\ud83c\uddfa\ud83c\uddf8", true),
   VIP_US_SOUTH("vip-us-south", "US South (VIP)", "\ud83c\uddfa\ud83c\uddf8", true),
   VIP_US_WEST("vip-us-west", "US West (VIP)", "\ud83c\uddfa\ud83c\uddf8", true),
   UNKNOWN("", "Unknown Region", (String)null, false),
   AUTOMATIC("automatic", "Automatic", (String)null, false);

   private final String key;
   private final String name;
   private final String emoji;
   private final boolean vip;

   private Region(String key, String name, String emoji, boolean vip) {
      this.key = key;
      this.name = name;
      this.emoji = emoji;
      this.vip = vip;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   @Nullable
   public String getEmoji() {
      return this.emoji;
   }

   public boolean isVip() {
      return this.vip;
   }

   @Nonnull
   public static Region fromKey(@Nullable String key) {
      for(Region region : values()) {
         if (region.getKey().equals(key)) {
            return region;
         }
      }

      return UNKNOWN;
   }

   public String toString() {
      return (new EntityString(this)).setType(this).toString();
   }

   // $FF: synthetic method
   private static Region[] $values() {
      return new Region[]{BRAZIL, HONG_KONG, INDIA, JAPAN, MILAN, ROTTERDAM, RUSSIA, SINGAPORE, SOUTH_AFRICA, SOUTH_KOREA, SYDNEY, US_CENTRAL, US_EAST, US_SOUTH, US_WEST, VIP_BRAZIL, VIP_HONG_KONG, VIP_INDIA, VIP_JAPAN, VIP_MILAN, VIP_ROTTERDAM, VIP_RUSSIA, VIP_SINGAPORE, VIP_SOUTH_AFRICA, VIP_SOUTH_KOREA, VIP_SYDNEY, VIP_US_CENTRAL, VIP_US_EAST, VIP_US_SOUTH, VIP_US_WEST, UNKNOWN, AUTOMATIC};
   }
}
