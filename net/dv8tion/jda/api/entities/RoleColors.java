package net.dv8tion.jda.api.entities;

import java.awt.Color;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.EntityString;

public class RoleColors implements SerializableData {
   public static final RoleColors DEFAULT_HOLOGRAPHIC = new RoleColors(11127295, 16759788, 16761760);
   public static final RoleColors DEFAULT = new RoleColors(536870911, 536870911, 536870911);
   private final int primary;
   private final int secondary;
   private final int tertiary;

   public RoleColors(int primary, int secondary, int tertiary) {
      this.primary = primary;
      this.secondary = secondary;
      this.tertiary = tertiary;
   }

   public boolean isDefault() {
      return this.primary == 536870911;
   }

   public boolean isSolid() {
      return this.primary != 536870911 && this.secondary == 536870911 && this.tertiary == 536870911;
   }

   public boolean isGradient() {
      return this.tertiary == 536870911 && this.secondary != 536870911 && this.primary != 536870911;
   }

   public boolean isHolographic() {
      return this.equals(DEFAULT_HOLOGRAPHIC);
   }

   @Nullable
   public Color getPrimary() {
      return this.primary == 536870911 ? null : new Color(this.primary);
   }

   public int getPrimaryRaw() {
      return this.primary;
   }

   @Nullable
   public Color getSecondary() {
      return this.secondary == 536870911 ? null : new Color(this.secondary);
   }

   public int getSecondaryRaw() {
      return this.secondary;
   }

   @Nullable
   public Color getTertiary() {
      return this.tertiary == 536870911 ? null : new Color(this.tertiary);
   }

   public int getTertiaryRaw() {
      return this.tertiary;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof RoleColors)) {
         return false;
      } else {
         RoleColors other = (RoleColors)obj;
         return this.primary == other.primary && this.secondary == other.secondary && this.tertiary == other.tertiary;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.primary, this.secondary, this.tertiary});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("primary", colorToHex(this.primary)).addMetadata("secondary", colorToHex(this.secondary)).addMetadata("tertiary", colorToHex(this.tertiary)).toString();
   }

   private static String colorToHex(int color) {
      return color == 536870911 ? null : Integer.toHexString(color);
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("primary_color", this.primary != 536870911 ? this.primary & 16777215 : 0).put("secondary_color", this.secondary != 536870911 ? this.secondary & 16777215 : null).put("tertiary_color", this.tertiary != 536870911 ? this.tertiary & 16777215 : null);
   }
}
