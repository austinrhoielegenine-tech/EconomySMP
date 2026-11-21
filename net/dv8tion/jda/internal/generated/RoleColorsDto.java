package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleColorsDto {
   @JsonProperty("primary_color")
   private MaybeNull<Long> _primary_color;
   @JsonProperty("secondary_color")
   private MaybeNull<Long> _secondary_color;
   @JsonProperty("tertiary_color")
   private MaybeNull<Long> _tertiary_color;

   @JsonIgnore
   public Long getPrimaryColor() {
      return this._primary_color == null ? null : (Long)this._primary_color.value();
   }

   public RoleColorsDto setPrimaryColor(Long _primary_color) {
      this._primary_color = new MaybeNull<Long>(_primary_color);
      return this;
   }

   @JsonIgnore
   public Long getSecondaryColor() {
      return this._secondary_color == null ? null : (Long)this._secondary_color.value();
   }

   public RoleColorsDto setSecondaryColor(Long _secondary_color) {
      this._secondary_color = new MaybeNull<Long>(_secondary_color);
      return this;
   }

   @JsonIgnore
   public Long getTertiaryColor() {
      return this._tertiary_color == null ? null : (Long)this._tertiary_color.value();
   }

   public RoleColorsDto setTertiaryColor(Long _tertiary_color) {
      this._tertiary_color = new MaybeNull<Long>(_tertiary_color);
      return this;
   }
}
