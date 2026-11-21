package net.dv8tion.jda.internal.interactions.modal;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.DeferrableInteractionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Helpers;

public class ModalInteractionImpl extends DeferrableInteractionImpl implements ModalInteraction {
   private final String modalId;
   private final List<ModalMapping> mappings;
   private final Message message;

   public ModalInteractionImpl(JDAImpl api, DataObject object) {
      super(api, object);
      DataObject data = object.getObject("data");
      this.modalId = data.getString("custom_id");
      DataObject resolved = (DataObject)data.optObject("resolved").orElseGet(DataObject::empty);
      this.mappings = (List)((DataArray)data.optArray("components").orElseGet(DataArray::empty)).stream(DataArray::getObject).map((component) -> this.getMapping(component, resolved)).filter(Objects::nonNull).collect(Helpers.toUnmodifiableList());
      this.message = (Message)object.optObject("message").map((o) -> api.getEntityBuilder().createMessageWithChannel(o, this.getMessageChannel(), false)).orElse((Object)null);
   }

   private ModalMapping getMapping(DataObject component, DataObject resolved) {
      Component.Type type = Component.Type.fromKey(component.getInt("type"));
      return type == Component.Type.LABEL ? new ModalMapping(this, resolved, component.getObject("component")) : null;
   }

   @Nonnull
   public String getModalId() {
      return this.modalId;
   }

   @Nonnull
   public List<ModalMapping> getValues() {
      return this.mappings;
   }

   public Message getMessage() {
      return this.message;
   }

   @Nonnull
   public ReplyCallbackAction deferReply() {
      return new ReplyCallbackActionImpl(this.hook);
   }

   @Nonnull
   public MessageEditCallbackAction deferEdit() {
      return new MessageEditCallbackActionImpl(this.hook);
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)super.getChannel();
   }
}
