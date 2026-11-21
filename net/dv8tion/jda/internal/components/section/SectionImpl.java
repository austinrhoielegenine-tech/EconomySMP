package net.dv8tion.jda.internal.components.section;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponentUnion;
import net.dv8tion.jda.api.components.section.SectionContentComponent;
import net.dv8tion.jda.api.components.section.SectionContentComponentUnion;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public class SectionImpl extends AbstractComponentImpl implements Section, MessageTopLevelComponentUnion, ContainerChildComponentUnion {
   private final int uniqueId;
   private final List<SectionContentComponentUnion> components;
   private final SectionAccessoryComponentUnion accessory;

   public SectionImpl(ComponentDeserializer deserializer, DataObject data) {
      this(data.getInt("id", -1), (Collection)deserializer.deserializeAs(SectionContentComponentUnion.class, data.getArray("components")).collect(Collectors.toList()), (SectionAccessoryComponentUnion)deserializer.deserializeAs(SectionAccessoryComponentUnion.class, data.getObject("accessory")));
   }

   public SectionImpl(Collection<SectionContentComponentUnion> components, SectionAccessoryComponentUnion accessory) {
      this(-1, components, accessory);
   }

   public SectionImpl(int uniqueId, Collection<SectionContentComponentUnion> components, SectionAccessoryComponentUnion accessory) {
      Checks.notNull(accessory, "Accessory");
      this.uniqueId = uniqueId;
      this.components = Helpers.<SectionContentComponentUnion>copyAsUnmodifiableList(components);
      this.accessory = accessory;
   }

   public static Section validated(SectionAccessoryComponent accessory, Collection<? extends SectionContentComponent> components) {
      return validated(accessory, components, -1);
   }

   public static Section validated(SectionAccessoryComponent accessory, Collection<? extends SectionContentComponent> components, int uniqueId) {
      Checks.notNull(accessory, "Accessory");
      Checks.noneNull(components, "Components");
      Checks.notEmpty(components, "Components");
      Checks.check(components.size() <= 3, "A section can only contain %d components, provided: %d", 3, components.size());
      Collection<SectionContentComponentUnion> componentUnions = ComponentsUtil.<SectionContentComponentUnion>membersToUnion(components, SectionContentComponentUnion.class);
      SectionAccessoryComponentUnion accessoryUnion = (SectionAccessoryComponentUnion)ComponentsUtil.safeUnionCast("accessory", accessory, SectionAccessoryComponentUnion.class);
      return new SectionImpl(uniqueId, componentUnions, accessoryUnion);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.SECTION;
   }

   @Nonnull
   public SectionImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new SectionImpl(uniqueId, this.components, this.accessory);
   }

   @Nonnull
   public Section withContentComponents(@Nonnull Collection<? extends SectionContentComponent> components) {
      Checks.noneNull(components, "Components");
      return new SectionImpl(this.uniqueId, ComponentsUtil.membersToUnion(components, SectionContentComponentUnion.class), this.accessory);
   }

   @Nonnull
   public Section withAccessory(@Nonnull SectionAccessoryComponent accessory) {
      Checks.notNull(accessory, "Accessory");
      return new SectionImpl(this.uniqueId, this.components, (SectionAccessoryComponentUnion)ComponentsUtil.safeUnionCast("accessory", accessory, SectionAccessoryComponentUnion.class));
   }

   @Nonnull
   public Section replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      List<SectionContentComponentUnion> newContent = (List)ComponentsUtil.doReplace(SectionContentComponent.class, this.getContentComponents(), replacer, Function.identity());
      SectionAccessoryComponentUnion newAccessory = (SectionAccessoryComponentUnion)ComponentsUtil.doReplace(SectionAccessoryComponent.class, Collections.singletonList(this.accessory), replacer, (newAccessories) -> newAccessories.isEmpty() ? null : (SectionAccessoryComponentUnion)newAccessories.get(0));
      return validated(newAccessory, newContent, this.uniqueId);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public @Unmodifiable List<SectionContentComponentUnion> getContentComponents() {
      return this.components;
   }

   @Nonnull
   public SectionAccessoryComponentUnion getAccessory() {
      return this.accessory;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty();
      json.put("type", this.getType().getKey());
      json.put("accessory", this.accessory);
      json.put("components", DataArray.fromCollection(this.getContentComponents()));
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SectionImpl)) {
         return false;
      } else {
         SectionImpl that = (SectionImpl)o;
         return this.uniqueId == that.uniqueId && Objects.equals(this.components, that.components) && Objects.equals(this.accessory, that.accessory);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.components, this.accessory});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("components", this.components).toString();
   }
}
