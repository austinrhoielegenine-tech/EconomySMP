package net.dv8tion.jda.internal.components.container;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.container.ContainerChildComponentUnion;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.utils.ComponentDeserializer;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class ContainerImpl extends AbstractComponentImpl implements Container, MessageTopLevelComponentUnion {
   private final int uniqueId;
   private final List<ContainerChildComponentUnion> components;
   private final boolean spoiler;
   private final Integer accentColor;

   public ContainerImpl(ComponentDeserializer deserializer, DataObject data) {
      this(data.getInt("id", -1), (Collection)deserializer.deserializeAs(ContainerChildComponentUnion.class, data.getArray("components")).collect(Collectors.toList()), data.getBoolean("spoiler", false), data.isNull("accent_color") ? null : data.getInt("accent_color"));
   }

   private ContainerImpl(Collection<ContainerChildComponentUnion> components) {
      this(-1, components, false, (Integer)null);
   }

   public ContainerImpl(int uniqueId, Collection<ContainerChildComponentUnion> components, boolean spoiler, Integer accentColor) {
      this.uniqueId = uniqueId;
      this.components = Helpers.<ContainerChildComponentUnion>copyAsUnmodifiableList(components);
      this.spoiler = spoiler;
      this.accentColor = accentColor;
   }

   public static Container validated(Collection<? extends ContainerChildComponent> components) {
      return validated(-1, components, false, (Integer)null);
   }

   public static Container validated(int uniqueId, Collection<? extends ContainerChildComponent> components, boolean spoiler, Integer accentColor) {
      Checks.noneNull(components, "Components");
      Checks.notEmpty(components, "Components");
      Collection<ContainerChildComponentUnion> componentUnions = ComponentsUtil.<ContainerChildComponentUnion>membersToUnion(components, ContainerChildComponentUnion.class);
      return new ContainerImpl(uniqueId, componentUnions, spoiler, accentColor);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.CONTAINER;
   }

   @Nonnull
   public ContainerImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new ContainerImpl(uniqueId, this.components, this.spoiler, this.accentColor);
   }

   @Nonnull
   public Container withSpoiler(boolean spoiler) {
      return new ContainerImpl(this.uniqueId, this.components, spoiler, this.accentColor);
   }

   @Nonnull
   public Container withAccentColor(@Nullable Integer accentColor) {
      return new ContainerImpl(this.uniqueId, this.components, this.spoiler, accentColor);
   }

   @Nonnull
   public Container withComponents(@Nonnull Collection<? extends ContainerChildComponent> components) {
      Checks.noneNull(components, "Components");
      return new ContainerImpl(this.uniqueId, ComponentsUtil.membersToUnion(components, ContainerChildComponentUnion.class), this.spoiler, this.accentColor);
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public Container replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      return (Container)ComponentsUtil.doReplace(ContainerChildComponent.class, this.getComponents(), replacer, (components) -> validated(this.uniqueId, components, this.spoiler, this.accentColor));
   }

   @Nonnull
   public List<ContainerChildComponentUnion> getComponents() {
      return this.components;
   }

   @Nullable
   public Integer getAccentColorRaw() {
      return this.accentColor;
   }

   public boolean isSpoiler() {
      return this.spoiler;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", this.getType().getKey()).put("components", DataArray.fromCollection(this.getComponents())).put("spoiler", this.spoiler);
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      if (this.accentColor != null) {
         json.put("accent_color", this.accentColor & 16777215);
      }

      return json;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ContainerImpl)) {
         return false;
      } else {
         ContainerImpl that = (ContainerImpl)o;
         return this.uniqueId == that.uniqueId && this.spoiler == that.spoiler && Objects.equals(this.components, that.components) && Objects.equals(this.accentColor, that.accentColor);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.uniqueId, this.components, this.spoiler, this.accentColor});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("accentColor", this.accentColor).addMetadata("spoiler", this.spoiler).addMetadata("components", this.components).toString();
   }
}
