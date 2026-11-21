package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface ApplicationTeam extends ISnowflake {
   /** @deprecated */
   @Deprecated
   String ICON_URL = "https://cdn.discordapp.com/team-icons/%s/%s.png";

   @Nullable
   default TeamMember getOwner() {
      return this.getMemberById(this.getOwnerIdLong());
   }

   @Nonnull
   default String getOwnerId() {
      return Long.toUnsignedString(this.getOwnerIdLong());
   }

   long getOwnerIdLong();

   @Nullable
   String getIconId();

   @Nullable
   default String getIconUrl() {
      return this.getIconUrl(ImageFormat.PNG);
   }

   @Nullable
   default String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy icon = this.getIcon(format);
      return icon == null ? null : icon.getUrl();
   }

   @Nullable
   default ImageProxy getIcon() {
      String iconUrl = this.getIconUrl();
      return iconUrl == null ? null : new ImageProxy(iconUrl);
   }

   @Nullable
   default ImageProxy getIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.applicationTeamIcon(format, this.getId(), this.getIconId());
   }

   @Nonnull
   @Unmodifiable List<TeamMember> getMembers();

   default boolean isMember(@Nonnull User user) {
      return this.getMember(user) != null;
   }

   @Nullable
   default TeamMember getMember(@Nonnull User user) {
      Checks.notNull(user, "User");
      return this.getMemberById(user.getIdLong());
   }

   @Nullable
   default TeamMember getMemberById(@Nonnull String userId) {
      return this.getMemberById(MiscUtil.parseSnowflake(userId));
   }

   @Nullable
   default TeamMember getMemberById(long userId) {
      for(TeamMember member : this.getMembers()) {
         if (member.getUser().getIdLong() == userId) {
            return member;
         }
      }

      return null;
   }
}
