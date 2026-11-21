package net.dv8tion.jda.internal.components;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.checkbox.Checkbox;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.radiogroup.RadioGroup;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.UnionUtil;

public abstract class AbstractComponentImpl implements SerializableData {
   @Nonnull
   public ActionRow asActionRow() {
      return (ActionRow)this.toComponentType(ActionRow.class);
   }

   @Nonnull
   public Button asButton() {
      return (Button)this.toComponentType(Button.class);
   }

   @Nonnull
   public StringSelectMenu asStringSelectMenu() {
      return (StringSelectMenu)this.toComponentType(StringSelectMenu.class);
   }

   @Nonnull
   public EntitySelectMenu asEntitySelectMenu() {
      return (EntitySelectMenu)this.toComponentType(EntitySelectMenu.class);
   }

   @Nonnull
   public TextInput asTextInput() {
      return (TextInput)this.toComponentType(TextInput.class);
   }

   @Nonnull
   public Section asSection() {
      return (Section)this.toComponentType(Section.class);
   }

   @Nonnull
   public TextDisplay asTextDisplay() {
      return (TextDisplay)this.toComponentType(TextDisplay.class);
   }

   @Nonnull
   public MediaGallery asMediaGallery() {
      return (MediaGallery)this.toComponentType(MediaGallery.class);
   }

   @Nonnull
   public Thumbnail asThumbnail() {
      return (Thumbnail)this.toComponentType(Thumbnail.class);
   }

   @Nonnull
   public Separator asSeparator() {
      return (Separator)this.toComponentType(Separator.class);
   }

   @Nonnull
   public FileDisplay asFileDisplay() {
      return (FileDisplay)this.toComponentType(FileDisplay.class);
   }

   @Nonnull
   public Container asContainer() {
      return (Container)this.toComponentType(Container.class);
   }

   @Nonnull
   public Label asLabel() {
      return (Label)this.toComponentType(Label.class);
   }

   @Nonnull
   public AttachmentUpload asAttachmentUpload() {
      return (AttachmentUpload)this.toComponentType(AttachmentUpload.class);
   }

   @Nonnull
   public RadioGroup asRadioGroup() {
      return (RadioGroup)this.toComponentType(RadioGroup.class);
   }

   @Nonnull
   public CheckboxGroup asCheckboxGroup() {
      return (CheckboxGroup)this.toComponentType(CheckboxGroup.class);
   }

   @Nonnull
   public Checkbox asCheckbox() {
      return (Checkbox)this.toComponentType(Checkbox.class);
   }

   protected <T extends Component> T toComponentType(Class<T> type) {
      return (T)(UnionUtil.safeUnionCast("component", this, type));
   }
}
