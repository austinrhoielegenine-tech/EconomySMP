package net.dv8tion.jda.internal.modals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.ModalTopLevelComponentUnion;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class ModalImpl implements Modal {
   private final String id;
   private final String title;
   private final List<ModalTopLevelComponentUnion> components;

   public ModalImpl(DataObject object) {
      this.id = object.getString("custom_id");
      this.title = object.getString("title");
      this.components = (List)object.optArray("components").map((arr) -> (List)EntityBuilder.DEFAULT_COMPONENT_DESERIALIZER.deserializeAs(ModalTopLevelComponentUnion.class, arr).collect(Helpers.toUnmodifiableList())).orElseGet(Collections::emptyList);
   }

   public ModalImpl(String id, String title, List<ModalTopLevelComponentUnion> components) {
      this.id = id;
      this.title = title;
      this.components = Collections.unmodifiableList(components);
   }

   @Nonnull
   public String getId() {
      return this.id;
   }

   @Nonnull
   public String getTitle() {
      return this.title;
   }

   @Nonnull
   public List<ModalTopLevelComponentUnion> getComponents() {
      return this.components;
   }

   @Nonnull
   public DataObject toData() {
      DataObject object = DataObject.empty().put("custom_id", this.id).put("title", this.title);
      Stream var10002 = this.components.stream();
      Objects.requireNonNull(AbstractComponentImpl.class);
      object.put("components", DataArray.fromCollection((Collection)var10002.map(AbstractComponentImpl.class::cast).map(SerializableData::toData).collect(Collectors.toList())));
      return object;
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("id", this.id).addMetadata("title", this.title).toString();
   }
}
