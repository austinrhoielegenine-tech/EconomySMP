package net.dv8tion.jda.internal.handle;

import java.util.Objects;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateDiscriminatorEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateGlobalNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.SelfUserImpl;

public class UserUpdateHandler extends SocketHandler {
   public UserUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      SelfUserImpl self = (SelfUserImpl)this.getJDA().getSelfUser();
      String name = content.getString("username");
      String discriminator = content.getString("discriminator");
      String globalName = content.getString("global_name", (String)null);
      String avatarId = content.getString("avatar", (String)null);
      Boolean verified = content.hasKey("verified") ? content.getBoolean("verified") : null;
      Boolean mfaEnabled = content.hasKey("mfa_enabled") ? content.getBoolean("mfa_enabled") : null;
      if (!Objects.equals(name, self.getName())) {
         String oldName = self.getName();
         self.setName(name);
         this.getJDA().handleEvent(new SelfUpdateNameEvent(this.getJDA(), this.responseNumber, oldName));
      }

      if (!Objects.equals(discriminator, self.getDiscriminator())) {
         String oldDiscriminator = self.getDiscriminator();
         self.setDiscriminator(Short.parseShort(discriminator));
         this.getJDA().handleEvent(new SelfUpdateDiscriminatorEvent(this.getJDA(), this.responseNumber, oldDiscriminator));
      }

      if (!Objects.equals(globalName, self.getGlobalName())) {
         String oldGlobalName = self.getGlobalName();
         self.setGlobalName(globalName);
         this.getJDA().handleEvent(new SelfUpdateGlobalNameEvent(this.getJDA(), this.responseNumber, oldGlobalName));
      }

      if (!Objects.equals(avatarId, self.getAvatarId())) {
         String oldAvatarId = self.getAvatarId();
         self.setAvatarId(avatarId);
         this.getJDA().handleEvent(new SelfUpdateAvatarEvent(this.getJDA(), this.responseNumber, oldAvatarId));
      }

      if (verified != null && verified != self.isVerified()) {
         boolean wasVerified = self.isVerified();
         self.setVerified(verified);
         this.getJDA().handleEvent(new SelfUpdateVerifiedEvent(this.getJDA(), this.responseNumber, wasVerified));
      }

      if (mfaEnabled != null && mfaEnabled != self.isMfaEnabled()) {
         boolean wasMfaEnabled = self.isMfaEnabled();
         self.setMfaEnabled(mfaEnabled);
         this.getJDA().handleEvent(new SelfUpdateMFAEvent(this.getJDA(), this.responseNumber, wasMfaEnabled));
      }

      return null;
   }
}
