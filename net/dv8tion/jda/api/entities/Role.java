package net.dv8tion.jda.api.entities;

import java.awt.Color;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.ReplaceWith;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.detached.IDetachableEntity;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

public interface Role extends IMentionable, IPermissionHolder, IDetachableEntity, Comparable<Role> {
   int DEFAULT_COLOR_RAW = 536870911;

   int getPosition();

   int getPositionRaw();

   @Nonnull
   String getName();

   boolean isManaged();

   boolean isHoisted();

   boolean isMentionable();

   long getPermissionsRaw();

   @Nonnull
   RoleColors getColors();

   /** @deprecated */
   @Nullable
   @Deprecated
   @ReplaceWith("getColors().getPrimary()")
   default Color getColor() {
      return this.getColors().getPrimary();
   }

   /** @deprecated */
   @Deprecated
   @ReplaceWith("getColors().getPrimaryRaw()")
   default int getColorRaw() {
      return this.getColors().getPrimaryRaw();
   }

   boolean isPublicRole();

   boolean canInteract(@Nonnull Role var1);

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   RoleAction createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default RoleAction createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   @CheckReturnValue
   RoleManager getManager();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   JDA getJDA();

   @Nonnull
   RoleTags getTags();

   @Nullable
   RoleIcon getIcon();

   public interface RoleTags {
      boolean isBot();

      long getBotIdLong();

      @Nullable
      default String getBotId() {
         return this.isBot() ? Long.toUnsignedString(this.getBotIdLong()) : null;
      }

      boolean isBoost();

      boolean isIntegration();

      long getIntegrationIdLong();

      @Nullable
      default String getIntegrationId() {
         return this.isIntegration() ? Long.toUnsignedString(this.getIntegrationIdLong()) : null;
      }

      default boolean hasSubscriptionListing() {
         return this.getSubscriptionIdLong() != 0L;
      }

      long getSubscriptionIdLong();

      @Nullable
      default String getSubscriptionId() {
         return this.hasSubscriptionListing() ? Long.toUnsignedString(this.getSubscriptionIdLong()) : null;
      }

      boolean isAvailableForPurchase();

      boolean isLinkedRole();
   }
}
