package net.dv8tion.jda.api.events.self;

import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;

public class SelfUpdateGlobalNameEvent extends GenericSelfUpdateEvent<String> {
   public static final String IDENTIFIER = "global_name";

   public SelfUpdateGlobalNameEvent(JDA api, long responseNumber, String oldName) {
      super(api, responseNumber, oldName, api.getSelfUser().getGlobalName(), "global_name");
   }

   @Nullable
   public String getOldGlobalName() {
      return (String)this.getOldValue();
   }

   @Nullable
   public String getNewGlobalName() {
      return (String)this.getNewValue();
   }

   public String toString() {
      return "SelfUpdateGlobalName(" + (String)this.getOldValue() + "->" + (String)this.getNewValue() + ')';
   }
}
