package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class AccountManagerImpl extends ManagerBase<AccountManager> implements AccountManager {
   protected final SelfUser selfUser;
   protected String name;
   protected Icon avatar;
   protected Icon banner;

   public AccountManagerImpl(SelfUser selfUser) {
      super(selfUser.getJDA(), Route.Self.MODIFY_SELF.compile());
      this.selfUser = selfUser;
   }

   @Nonnull
   public SelfUser getSelfUser() {
      return this.selfUser;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 2L) == 2L) {
         this.avatar = null;
      }

      if ((fields & 4L) == 4L) {
         this.banner = null;
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManagerImpl reset() {
      super.reset();
      this.avatar = null;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManagerImpl setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 32, "Name");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManagerImpl setAvatar(Icon avatar) {
      this.avatar = avatar;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public AccountManager setBanner(Icon banner) {
      this.banner = banner;
      this.set |= 4L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject body = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         body.put("username", this.name);
      }

      if (this.shouldUpdate(2L)) {
         body.put("avatar", this.avatar == null ? null : this.avatar.getEncoding());
      }

      if (this.shouldUpdate(4L)) {
         body.put("banner", this.banner == null ? null : this.banner.getEncoding());
      }

      this.reset();
      return this.getRequestBody(body);
   }

   protected void handleSuccess(Response response, Request<Void> request) {
      request.onSuccess((Object)null);
   }
}
