package net.dv8tion.jda.api.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;

public class RoleIcon {
   /** @deprecated */
   @Deprecated
   public static final String ICON_URL = "https://cdn.discordapp.com/role-icons/%s/%s.png";
   private final String iconId;
   private final String emoji;
   private final long roleId;

   public RoleIcon(String iconId, String emoji, long roleId) {
      this.iconId = iconId;
      this.emoji = emoji;
      this.roleId = roleId;
   }

   @Nullable
   public String getIconId() {
      return this.iconId;
   }

   @Nullable
   public String getIconUrl() {
      String iconId = this.getIconId();
      return iconId == null ? null : this.getIconUrl(ImageFormat.PNG);
   }

   @Nullable
   public String getIconUrl(@Nonnull ImageFormat format) {
      ImageProxy proxy = this.getIcon(format);
      return proxy == null ? null : proxy.getUrl();
   }

   @Nullable
   public ImageProxy getIcon() {
      String iconUrl = this.getIconUrl();
      return iconUrl == null ? null : new ImageProxy(iconUrl);
   }

   @Nullable
   public ImageProxy getIcon(@Nonnull ImageFormat format) {
      return DiscordAssets.roleIcon(format, Long.toUnsignedString(this.roleId), this.iconId);
   }

   @Nullable
   public String getEmoji() {
      return this.emoji;
   }

   public boolean isEmoji() {
      return this.emoji != null;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof RoleIcon)) {
         return false;
      } else {
         RoleIcon icon = (RoleIcon)obj;
         return Objects.equals(icon.iconId, this.iconId) && Objects.equals(icon.emoji, this.emoji);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.iconId, this.emoji});
   }
}
