package net.dv8tion.jda.internal.components.selections;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.label.LabelChildComponentUnion;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Helpers;

public class EntitySelectMenuImpl extends SelectMenuImpl implements EntitySelectMenu, LabelChildComponentUnion {
   protected final Component.Type type;
   protected final EnumSet<ChannelType> channelTypes;
   protected final List<EntitySelectMenu.DefaultValue> defaultValues;

   public EntitySelectMenuImpl(DataObject data) {
      super(data);
      this.type = Component.Type.fromKey(data.getInt("type"));
      this.channelTypes = Helpers.<ChannelType>copyEnumSet(ChannelType.class, (Collection)data.optArray("channel_types").map((arr) -> (List)arr.stream(DataArray::getInt).map(ChannelType::fromId).collect(Collectors.toList())).orElse((Object)null));
      this.defaultValues = (List)data.optArray("default_values").map((array) -> (List)array.stream(DataArray::getObject).map(EntitySelectMenu.DefaultValue::fromData).collect(Helpers.toUnmodifiableList())).orElse(Collections.emptyList());
   }

   public EntitySelectMenuImpl(String id, int uniqueId, String placeholder, int minValues, int maxValues, boolean disabled, Component.Type type, EnumSet<ChannelType> channelTypes, List<EntitySelectMenu.DefaultValue> defaultValues, Boolean required) {
      super(id, uniqueId, placeholder, minValues, maxValues, disabled, required);
      this.type = type;
      this.channelTypes = channelTypes;
      this.defaultValues = defaultValues;
   }

   @Nonnull
   public Component.Type getType() {
      return this.type;
   }

   @Nonnull
   public EntitySelectMenuImpl withUniqueId(int uniqueId) {
      return (EntitySelectMenuImpl)((EntitySelectMenu.Builder)this.createCopy().setUniqueId(uniqueId)).build();
   }

   @Nonnull
   public EnumSet<EntitySelectMenu.SelectTarget> getEntityTypes() {
      switch (this.type) {
         case ROLE_SELECT:
            return EnumSet.of(EntitySelectMenu.SelectTarget.ROLE);
         case USER_SELECT:
            return EnumSet.of(EntitySelectMenu.SelectTarget.USER);
         case CHANNEL_SELECT:
            return EnumSet.of(EntitySelectMenu.SelectTarget.CHANNEL);
         case MENTIONABLE_SELECT:
            return EnumSet.of(EntitySelectMenu.SelectTarget.ROLE, EntitySelectMenu.SelectTarget.USER);
         default:
            throw new IllegalStateException("Unsupported type: " + this.type);
      }
   }

   @Nonnull
   public EnumSet<ChannelType> getChannelTypes() {
      return this.channelTypes;
   }

   @Nonnull
   public List<EntitySelectMenu.DefaultValue> getDefaultValues() {
      return this.defaultValues;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = super.toData().put("type", this.type.getKey());
      if (this.type == Component.Type.CHANNEL_SELECT && !this.channelTypes.isEmpty()) {
         json.put("channel_types", DataArray.fromCollection((Collection)this.channelTypes.stream().map(ChannelType::getId).collect(Collectors.toList())));
      }

      if (!this.defaultValues.isEmpty()) {
         json.put("default_values", DataArray.fromCollection(this.defaultValues));
      }

      return json;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.placeholder, this.minValues, this.maxValues, this.disabled, this.type, this.channelTypes, this.defaultValues});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof EntitySelectMenu)) {
         return false;
      } else {
         EntitySelectMenu other = (EntitySelectMenu)obj;
         return Objects.equals(this.id, other.getCustomId()) && Objects.equals(this.placeholder, other.getPlaceholder()) && this.minValues == other.getMinValues() && this.maxValues == other.getMaxValues() && this.disabled == other.isDisabled() && this.type == other.getType() && this.channelTypes.equals(other.getChannelTypes()) && this.defaultValues.equals(other.getDefaultValues());
      }
   }
}
