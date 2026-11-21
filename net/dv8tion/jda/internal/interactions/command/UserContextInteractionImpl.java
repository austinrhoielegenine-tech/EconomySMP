package net.dv8tion.jda.internal.interactions.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;

public class UserContextInteractionImpl extends ContextInteractionImpl<User> implements UserContextInteraction {
   private Member targetMember;

   public UserContextInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
   }

   protected User parse(DataObject interaction, DataObject resolved) {
      DataObject users = resolved.getObject("users");
      DataObject user = users.getObject((String)users.keys().iterator().next());
      resolved.optObject("members").filter((m) -> !m.keys().isEmpty()).ifPresent((members) -> {
         DataObject member = members.getObject((String)members.keys().iterator().next());
         this.targetMember = this.interactionEntityBuilder.createMember(this.guild, member);
         if (this.targetMember instanceof MemberImpl) {
            this.api.getEntityBuilder().updateMemberCache((MemberImpl)this.targetMember);
         }

      });
      return this.api.getEntityBuilder().createUser(user);
   }

   public Member getTargetMember() {
      return this.targetMember;
   }
}
