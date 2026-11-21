package net.dv8tion.jda.api.entities;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.managers.SelfMemberManager;

public interface SelfMember extends Member {
   int MAX_BIO_LENGTH = 190;

   @Nonnull
   SelfUser getUser();

   @Nonnull
   @CheckReturnValue
   SelfMemberManager getManager();
}
