package net.dv8tion.jda.api.components.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.components.tree.ModalComponentTree;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.UnknownComponentImpl;
import net.dv8tion.jda.internal.components.actionrow.ActionRowImpl;
import net.dv8tion.jda.internal.components.attachmentupload.AttachmentUploadImpl;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;
import net.dv8tion.jda.internal.components.checkbox.CheckboxImpl;
import net.dv8tion.jda.internal.components.checkboxgroup.CheckboxGroupImpl;
import net.dv8tion.jda.internal.components.container.ContainerImpl;
import net.dv8tion.jda.internal.components.filedisplay.FileDisplayFileUpload;
import net.dv8tion.jda.internal.components.filedisplay.FileDisplayImpl;
import net.dv8tion.jda.internal.components.label.LabelImpl;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryImpl;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryItemFileUpload;
import net.dv8tion.jda.internal.components.mediagallery.MediaGalleryItemImpl;
import net.dv8tion.jda.internal.components.radiogroup.RadioGroupImpl;
import net.dv8tion.jda.internal.components.section.SectionImpl;
import net.dv8tion.jda.internal.components.selections.EntitySelectMenuImpl;
import net.dv8tion.jda.internal.components.selections.StringSelectMenuImpl;
import net.dv8tion.jda.internal.components.separator.SeparatorImpl;
import net.dv8tion.jda.internal.components.textdisplay.TextDisplayImpl;
import net.dv8tion.jda.internal.components.textinput.TextInputImpl;
import net.dv8tion.jda.internal.components.thumbnail.ThumbnailFileUpload;
import net.dv8tion.jda.internal.components.thumbnail.ThumbnailImpl;
import net.dv8tion.jda.internal.components.utils.ComponentsUtil;
import net.dv8tion.jda.internal.utils.Checks;

public class ComponentDeserializer {
   private static final String ATTACHMENT_SCHEMA = "attachment://";
   private final Map<String, FileUpload> files;

   public ComponentDeserializer(@Nonnull Collection<? extends FileUpload> files) {
      this.files = new LinkedHashMap(files.size());

      for(FileUpload file : files) {
         this.files.put(file.getName(), file);
      }

   }

   @Nonnull
   public List<IComponentUnion> deserializeAll(@Nonnull List<DataObject> components) {
      Checks.noneNull(components, "Components");
      return (List)components.stream().map(this::parseComponent).collect(Collectors.toList());
   }

   @Nonnull
   public <T extends Component> Stream<T> deserializeAs(@Nonnull Class<T> type, @Nonnull List<DataObject> components) {
      Checks.notNull(type, "Type");
      Checks.noneNull(components, "Components");
      return components.stream().map(this::parseComponent).map((component) -> ComponentsUtil.safeUnionCastWithUnknownType("component", component, type));
   }

   @Nonnull
   public <T extends Component> Stream<T> deserializeAs(@Nonnull Class<T> type, @Nonnull DataArray components) {
      Checks.notNull(type, "Type");
      Checks.notNull(components, "Components");
      return components.stream(DataArray::getObject).map(this::parseComponent).map((component) -> ComponentsUtil.safeUnionCastWithUnknownType("component", component, type));
   }

   @Nonnull
   public <T extends Component> T deserializeAs(@Nonnull Class<T> type, @Nonnull DataObject component) {
      Checks.notNull(type, "Type");
      Checks.notNull(component, "Component");
      IComponentUnion componentUnion = this.parseComponent(component);
      return (T)ComponentsUtil.safeUnionCastWithUnknownType("component", componentUnion, type);
   }

   @Nonnull
   public <T extends ComponentTree<?>> T deserializeAsTree(@Nonnull Class<T> treeType, @Nonnull List<DataObject> components) {
      Checks.notNull(components, "Components");
      return (T)this.deserializeAsTree(treeType, DataArray.fromCollection(components));
   }

   @Nonnull
   public <T extends ComponentTree<?>> T deserializeAsTree(@Nonnull Class<T> treeType, @Nonnull DataArray components) {
      Checks.notNull(treeType, "Tree type");
      Checks.notNull(components, "Components");
      if (MessageComponentTree.class.isAssignableFrom(treeType)) {
         return (T)MessageComponentTree.of((Collection)this.deserializeAs(MessageTopLevelComponent.class, components).collect(Collectors.toList()));
      } else if (ModalComponentTree.class.isAssignableFrom(treeType)) {
         return (T)ModalComponentTree.of((Collection)this.deserializeAs(ModalTopLevelComponent.class, components).collect(Collectors.toList()));
      } else if (ComponentTree.class.isAssignableFrom(treeType)) {
         return (T)ComponentTree.of((Collection)this.deserializeAs(Component.class, components).collect(Collectors.toList()));
      } else {
         throw new UnsupportedOperationException("Cannot deserialize to tree of type " + treeType.getName());
      }
   }

   @Nonnull
   private IComponentUnion parseComponent(@Nonnull DataObject data) {
      switch (Component.Type.fromKey(data.getInt("type"))) {
         case ACTION_ROW:
            return new ActionRowImpl(this, data);
         case BUTTON:
            return new ButtonImpl(data);
         case STRING_SELECT:
            return new StringSelectMenuImpl(data);
         case TEXT_INPUT:
            return new TextInputImpl(data);
         case USER_SELECT:
         case ROLE_SELECT:
         case MENTIONABLE_SELECT:
         case CHANNEL_SELECT:
            return new EntitySelectMenuImpl(data);
         case SECTION:
            return new SectionImpl(this, data);
         case TEXT_DISPLAY:
            return new TextDisplayImpl(data);
         case THUMBNAIL:
            return (IComponentUnion)this.toThumbnail(data);
         case MEDIA_GALLERY:
            return (IComponentUnion)this.toMediaGallery(data);
         case FILE_DISPLAY:
            return (IComponentUnion)this.toFileDisplay(data);
         case SEPARATOR:
            return new SeparatorImpl(data);
         case CONTAINER:
            return new ContainerImpl(this, data);
         case LABEL:
            return new LabelImpl(this, data);
         case FILE_UPLOAD:
            return new AttachmentUploadImpl(data);
         case RADIO_GROUP:
            return new RadioGroupImpl(data);
         case CHECKBOX_GROUP:
            return new CheckboxGroupImpl(data);
         case CHECKBOX:
            return new CheckboxImpl(data);
         default:
            return new UnknownComponentImpl(data);
      }
   }

   @Nonnull
   private Thumbnail toThumbnail(@Nonnull DataObject data) {
      String url = data.getObject("media").getString("url");
      return (Thumbnail)(url.startsWith("attachment://") ? new ThumbnailFileUpload(data.getInt("id", -1), this.getFileByUri(url), data.getString("description", (String)null), data.getBoolean("spoiler")) : new ThumbnailImpl(data));
   }

   @Nonnull
   private FileDisplay toFileDisplay(@Nonnull DataObject data) {
      String url = data.getObject("file").getString("url");
      return (FileDisplay)(url.startsWith("attachment://") ? new FileDisplayFileUpload(data.getInt("id", -1), this.getFileByUri(url), data.getBoolean("spoiler")) : new FileDisplayImpl(data));
   }

   @Nonnull
   private MediaGallery toMediaGallery(@Nonnull DataObject data) {
      return new MediaGalleryImpl(data.getInt("id", -1), (Collection)data.getArray("items").stream(DataArray::getObject).map(this::toMediaGalleryItem).collect(Collectors.toList()));
   }

   @Nonnull
   private MediaGalleryItem toMediaGalleryItem(@Nonnull DataObject data) {
      String url = data.getObject("media").getString("url");
      return (MediaGalleryItem)(url.startsWith("attachment://") ? new MediaGalleryItemFileUpload(this.getFileByUri(url), data.getString("description", (String)null), data.getBoolean("spoiler")) : new MediaGalleryItemImpl(data));
   }

   @Nonnull
   private FileUpload getFileByUri(@Nonnull String uri) {
      String name = uri.substring("attachment://".length());
      FileUpload file = (FileUpload)this.files.get(name);
      Checks.check(file != null, "File for URI %s is missing", (Object)uri);
      return file;
   }
}
