package net.dv8tion.jda.internal.components.actionrow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;
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

public class ActionRowImpl extends AbstractComponentImpl implements ActionRow, MessageTopLevelComponentUnion, ContainerChildComponentUnion {
   private final int uniqueId;
   private final List<ActionRowChildComponentUnion> components;

   public ActionRowImpl(ComponentDeserializer deserializer, DataObject data) {
      this((Collection)deserializer.deserializeAs(ActionRowChildComponentUnion.class, data.getArray("components")).collect(Collectors.toList()), data.getInt("id", -1));
   }

   public ActionRowImpl(Collection<ActionRowChildComponentUnion> components, int uniqueId) {
      this.uniqueId = uniqueId;
      this.components = Helpers.<ActionRowChildComponentUnion>copyAsUnmodifiableList(components);
   }

   @Nonnull
   public static ActionRow validated(@Nonnull Collection<? extends ActionRowChildComponent> components) {
      return validated(components, -1);
   }

   @Nonnull
   public static ActionRow validated(@Nonnull Collection<? extends ActionRowChildComponent> components, int uniqueId) {
      Checks.notEmpty(components, "Row");
      Checks.noneNull(components, "Components");
      checkIsValid(components);
      Collection<ActionRowChildComponentUnion> componentUnions = ComponentsUtil.<ActionRowChildComponentUnion>membersToUnion(components, ActionRowChildComponentUnion.class);
      return new ActionRowImpl(componentUnions, uniqueId);
   }

   @Nonnull
   public static List<ActionRow> partitionOf(@Nonnull Collection<? extends ActionRowChildComponent> components) {
      Checks.noneNull(components, "Components");
      Checks.notEmpty(components, "Components");
      Collection<ActionRowChildComponentUnion> componentUnions = ComponentsUtil.<ActionRowChildComponentUnion>membersToUnion(components, ActionRowChildComponentUnion.class);
      List<ActionRow> rows = new ArrayList();
      List<ActionRowChildComponentUnion> currentRow = new ArrayList();
      Component.Type type = null;

      for(ActionRowChildComponentUnion current : componentUnions) {
         if (type != null && type != current.getType() || currentRow.size() == ActionRow.getMaxAllowed(current.getType())) {
            rows.add(ActionRow.of(currentRow));
            currentRow.clear();
         }

         type = current.getType();
         currentRow.add(current);
      }

      rows.add(ActionRow.of(currentRow));
      return rows;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public List<ActionRowChildComponentUnion> getComponents() {
      return this.components;
   }

   @Nonnull
   public ActionRow replace(@Nonnull ComponentReplacer replacer) {
      Checks.notNull(replacer, "ComponentReplacer");
      return (ActionRow)ComponentsUtil.doReplace(ActionRowChildComponent.class, this.components, replacer, (newComponents) -> validated(newComponents, this.uniqueId));
   }

   @Nonnull
   public ActionRowImpl withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return new ActionRowImpl(this.components, uniqueId);
   }

   @Nonnull
   public ActionRow withComponents(@Nonnull Collection<? extends ActionRowChildComponent> components) {
      Checks.noneNull(components, "Components");
      return new ActionRowImpl(ComponentsUtil.membersToUnion(components, ActionRowChildComponentUnion.class), this.uniqueId);
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.ACTION_ROW;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty().put("type", 1).put("components", DataArray.fromCollection(this.components));
      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   private static void checkIsValid(Collection<? extends ActionRowChildComponent> components) {
      Map<Component.Type, List<ActionRowChildComponent>> groups = (Map)components.stream().collect(Collectors.groupingBy(Component::getType));
      if (groups.size() > 1) {
         throw new IllegalArgumentException("Cannot create action row containing different component types! Provided: " + groups.keySet());
      } else {
         for(Map.Entry<Component.Type, List<ActionRowChildComponent>> entry : groups.entrySet()) {
            Component.Type type = (Component.Type)entry.getKey();
            List<ActionRowChildComponent> list = (List)entry.getValue();
            int maxAllowed = ActionRow.getMaxAllowed(type);
            Checks.check(list.size() <= maxAllowed, "Cannot create an action row with more than %d %s! Provided: %d", maxAllowed, type.name(), list.size());
         }

      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.uniqueId).addMetadata("components", this.components).toString();
   }

   public int hashCode() {
      return this.components.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof ActionRowImpl) ? false : this.components.equals(((ActionRowImpl)obj).components);
      }
   }
}
