package net.dv8tion.jda.api.components.label;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.checkbox.Checkbox;
import net.dv8tion.jda.api.components.checkboxgroup.CheckboxGroup;
import net.dv8tion.jda.api.components.radiogroup.RadioGroup;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;

public interface LabelChildComponentUnion extends LabelChildComponent, IComponentUnion {
   @Nonnull
   TextInput asTextInput();

   @Nonnull
   StringSelectMenu asStringSelectMenu();

   @Nonnull
   EntitySelectMenu asEntitySelectMenu();

   @Nonnull
   AttachmentUpload asAttachmentUpload();

   @Nonnull
   RadioGroup asRadioGroup();

   @Nonnull
   CheckboxGroup asCheckboxGroup();

   @Nonnull
   Checkbox asCheckbox();

   @Nonnull
   LabelChildComponentUnion withUniqueId(int var1);
}
