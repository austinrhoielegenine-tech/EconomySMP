package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.managers.SelfMemberManager;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class SelfMemberManagerImpl extends ManagerBase<SelfMemberManager> implements SelfMemberManager {
   protected final SelfMember selfMember;
   protected String nickname;
   protected Icon avatar;
   protected Icon banner;
   protected String bio;

   public SelfMemberManagerImpl(@Nonnull SelfMember selfMember) {
      super(selfMember.getJDA(), Route.Guilds.MODIFY_SELF.compile(selfMember.getGuild().getId()));
      this.selfMember = selfMember;
   }

   @Nonnull
   public SelfMember getMember() {
      return this.selfMember;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.nickname = null;
      }

      if ((fields & 2L) == 2L) {
         this.avatar = null;
      }

      if ((fields & 4L) == 4L) {
         this.banner = null;
      }

      if ((fields & 8L) == 8L) {
         this.bio = null;
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl reset() {
      super.reset();
      this.nickname = null;
      this.avatar = null;
      this.banner = null;
      this.bio = null;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl setNickname(@Nullable String nickname) {
      if (nickname != null) {
         Checks.notLonger(nickname, 32, "Nickname");
      }

      this.nickname = nickname;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl setAvatar(@Nullable Icon avatar) {
      this.avatar = avatar;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl setBanner(@Nullable Icon banner) {
      this.banner = banner;
      this.set |= 4L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SelfMemberManagerImpl setBio(@Nullable String bio) {
      if (bio != null) {
         Checks.notLonger(bio, 190, "Bio");
      }

      this.bio = bio;
      this.set |= 8L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject body = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         body.put("nick", this.nickname);
      }

      if (this.shouldUpdate(2L)) {
         body.put("avatar", this.avatar == null ? null : this.avatar.getEncoding());
      }

      if (this.shouldUpdate(4L)) {
         body.put("banner", this.banner == null ? null : this.banner.getEncoding());
      }

      if (this.shouldUpdate(8L)) {
         body.put("bio", this.bio);
      }

      this.reset();
      return this.getRequestBody(body);
   }

   protected void handleSuccess(Response response, Request<Void> request) {
      request.onSuccess((Object)null);
   }
}
