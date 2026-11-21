package net.dv8tion.jda.internal.components.textinput;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class TextInputImpl extends AbstractComponentImpl implements TextInput, LabelChildComponentUnion {
   private final String id;
   private final int uniqueId;
   private final TextInputStyle style;
   private final int minLength;
   private final int maxLength;
   private final boolean required;
   private final String value;
   private final String placeholder;

   public TextInputImpl(DataObject object) {
      this(object.getString("custom_id"), object.getInt("id", -1), TextInputStyle.fromKey(object.getInt("style", -1)), object.getInt("min_length", -1), object.getInt("max_length", -1), object.getBoolean("required", true), object.getString("value", (String)null), object.getString("placeholder", (String)null));
   }

   public TextInputImpl(String id, int uniqueId, TextInputStyle style, int minLength, int maxLength, boolean required, String value, String placeholder) {
      this.id = id;
      this.uniqueId = uniqueId;
      this.style = style;
      this.minLength = minLength;
      this.maxLength = maxLength;
      this.required = required;
      this.value = value;
      this.placeholder = placeholder;
   }

   @Nonnull
   public TextInputImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new TextInputImpl(this.id, uniqueId, this.style, this.minLength, this.maxLength, this.required, this.value, this.placeholder);
   }

   @Nonnull
   public TextInputStyle getStyle() {
      return this.style;
   }

   @Nonnull
   public String getCustomId() {
      return this.id;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   public int getMinLength() {
      return this.minLength;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public boolean isRequired() {
      return this.required;
   }

   @Nullable
   public String getValue() {
      return this.value;
   }

   @Nullable
   public String getPlaceHolder() {
      return this.placeholder;
   }

   @Nonnull
   public DataObject toData() {
      DataObject obj = DataObject.empty().put("type", this.getType().getKey()).put("custom_id", this.id).put("style", this.style.getRaw()).put("required", this.required);
      if (this.uniqueId >= 0) {
         obj.put("id", this.uniqueId);
      }

      if (this.minLength != -1) {
         obj.put("min_length", this.minLength);
      }

      if (this.maxLength != -1) {
         obj.put("max_length", this.maxLength);
      }

      if (this.value != null) {
         obj.put("value", this.value);
      }

      if (this.placeholder != null) {
         obj.put("placeholder", this.placeholder);
      }

      return obj;
   }

   public final boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof TextInputImpl)) {
         return false;
      } else {
         TextInputImpl that = (TextInputImpl)o;
         return this.uniqueId == that.uniqueId && this.minLength == that.minLength && this.maxLength == that.maxLength && this.required == that.required && this.id.equals(that.id) && this.style == that.style && Objects.equals(this.value, that.value) && Objects.equals(this.placeholder, that.placeholder);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.uniqueId, this.style, this.minLength, this.maxLength, this.required, this.value, this.placeholder});
   }

   public String toString() {
      return (new EntityString(this)).setType(this.style).addMetadata("id", this.id).addMetadata("value", this.value).toString();
   }
}
