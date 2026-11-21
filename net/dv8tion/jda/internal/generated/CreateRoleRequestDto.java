package net.dv8tion.jda.internal.generated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRoleRequestDto {
   @JsonProperty("name")
   private MaybeNull<String> _name;
   @JsonProperty("permissions")
   private MaybeNull<Long> _permissions;
   @JsonProperty("color")
   private MaybeNull<Long> _color;
   @JsonProperty("colors")
   private MaybeNull<RoleColorsDto> _colors;
   @JsonProperty("hoist")
   private MaybeNull<Boolean> _hoist;
   @JsonProperty("mentionable")
   private MaybeNull<Boolean> _mentionable;
   @JsonProperty("icon")
   private MaybeNull<String> _icon;
   @JsonProperty("unicode_emoji")
   private MaybeNull<String> _unicode_emoji;

   @JsonIgnore
   public String getName() {
      return this._name == null ? null : (String)this._name.value();
   }

   public CreateRoleRequestDto setName(String _name) {
      this._name = new MaybeNull<String>(_name);
      return this;
   }

   @JsonIgnore
   public Long getPermissions() {
      return this._permissions == null ? null : (Long)this._permissions.value();
   }

   public CreateRoleRequestDto setPermissions(Long _permissions) {
      this._permissions = new MaybeNull<Long>(_permissions);
      return this;
   }

   @JsonIgnore
   public Long getColor() {
      return this._color == null ? null : (Long)this._color.value();
   }

   public CreateRoleRequestDto setColor(Long _color) {
      this._color = new MaybeNull<Long>(_color);
      return this;
   }

   @JsonIgnore
   public RoleColorsDto getColors() {
      return this._colors == null ? null : (RoleColorsDto)this._colors.value();
   }

   public CreateRoleRequestDto setColors(RoleColorsDto _colors) {
      this._colors = new MaybeNull<RoleColorsDto>(_colors);
      return this;
   }

   @JsonIgnore
   public Boolean isHoist() {
      return this._hoist == null ? null : (Boolean)this._hoist.value();
   }

   public CreateRoleRequestDto setHoist(Boolean _hoist) {
      this._hoist = new MaybeNull<Boolean>(_hoist);
      return this;
   }

   @JsonIgnore
   public Boolean isMentionable() {
      return this._mentionable == null ? null : (Boolean)this._mentionable.value();
   }

   public CreateRoleRequestDto setMentionable(Boolean _mentionable) {
      this._mentionable = new MaybeNull<Boolean>(_mentionable);
      return this;
   }

   @JsonIgnore
   public String getIcon() {
      return this._icon == null ? null : (String)this._icon.value();
   }

   public CreateRoleRequestDto setIcon(String _icon) {
      this._icon = new MaybeNull<String>(_icon);
      return this;
   }

   @JsonIgnore
   public String getUnicodeEmoji() {
      return this._unicode_emoji == null ? null : (String)this._unicode_emoji.value();
   }

   public CreateRoleRequestDto setUnicodeEmoji(String _unicode_emoji) {
      this._unicode_emoji = new MaybeNull<String>(_unicode_emoji);
      return this;
   }
}
