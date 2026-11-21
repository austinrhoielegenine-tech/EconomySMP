package net.dv8tion.jda.api.components.selections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.components.selections.EntitySelectMenuImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface EntitySelectMenu extends SelectMenu {
   @Nonnull
   default EntitySelectMenu asDisabled() {
      return (EntitySelectMenu)SelectMenu.super.asDisabled();
   }

   @Nonnull
   default EntitySelectMenu asEnabled() {
      return (EntitySelectMenu)SelectMenu.super.asEnabled();
   }

   @Nonnull
   default EntitySelectMenu withDisabled(boolean disabled) {
      return ((Builder)this.createCopy().setDisabled(disabled)).build();
   }

   @Nonnull
   EntitySelectMenu withUniqueId(int var1);

   @Nonnull
   EnumSet<SelectTarget> getEntityTypes();

   @Nonnull
   EnumSet<ChannelType> getChannelTypes();

   @Nonnull
   @Unmodifiable List<DefaultValue> getDefaultValues();

   @Nonnull
   @CheckReturnValue
   default Builder createCopy() {
      Builder builder = create(this.getCustomId(), this.getEntityTypes());
      EnumSet<ChannelType> channelTypes = this.getChannelTypes();
      if (!channelTypes.isEmpty()) {
         builder.setChannelTypes(channelTypes);
      }

      builder.setRequiredRange(this.getMinValues(), this.getMaxValues());
      builder.setPlaceholder(this.getPlaceholder());
      builder.setDisabled(this.isDisabled());
      builder.setDefaultValues((Collection)this.getDefaultValues());
      if (this.getUniqueId() > 0) {
         builder.setUniqueId(this.getUniqueId());
      }

      return builder;
   }

   @Nonnull
   @CheckReturnValue
   static Builder create(@Nonnull String customId, @Nonnull Collection<SelectTarget> types) {
      return (new Builder(customId)).setEntityTypes(types);
   }

   @Nonnull
   @CheckReturnValue
   static Builder create(@Nonnull String customId, @Nonnull SelectTarget type, @Nonnull SelectTarget... types) {
      Checks.notNull(type, "Type");
      Checks.noneNull(types, "Types");
      return create(customId, EnumSet.of(type, types));
   }

   public static enum SelectTarget {
      USER,
      ROLE,
      CHANNEL;

      // $FF: synthetic method
      private static SelectTarget[] $values() {
         return new SelectTarget[]{USER, ROLE, CHANNEL};
      }
   }

   public static class DefaultValue implements ISnowflake, SerializableData {
      private final long id;
      private final SelectTarget type;

      protected DefaultValue(long id, @Nonnull SelectTarget type) {
         this.id = id;
         this.type = type;
      }

      @Nonnull
      public static DefaultValue fromData(@Nonnull DataObject object) {
         Checks.notNull(object, "DataObject");
         long id = object.getUnsignedLong("id");
         switch (object.getString("type")) {
            case "role":
               return role(id);
            case "user":
               return user(id);
            case "channel":
               return channel(id);
            default:
               throw new IllegalArgumentException("Unknown value type '" + object.getString("type", (String)null) + "'");
         }
      }

      @Nonnull
      public static DefaultValue from(@Nonnull UserSnowflake user) {
         Checks.notNull(user, "User");
         return user(user.getIdLong());
      }

      @Nonnull
      public static DefaultValue from(@Nonnull Role role) {
         Checks.notNull(role, "Role");
         return role(role.getIdLong());
      }

      @Nonnull
      public static DefaultValue from(@Nonnull GuildChannel channel) {
         Checks.notNull(channel, "Channel");
         return channel(channel.getIdLong());
      }

      @Nonnull
      public static DefaultValue role(long id) {
         return new DefaultValue(id, EntitySelectMenu.SelectTarget.ROLE);
      }

      @Nonnull
      public static DefaultValue role(@Nonnull String id) {
         return new DefaultValue(MiscUtil.parseSnowflake(id), EntitySelectMenu.SelectTarget.ROLE);
      }

      @Nonnull
      public static DefaultValue user(long id) {
         return new DefaultValue(id, EntitySelectMenu.SelectTarget.USER);
      }

      @Nonnull
      public static DefaultValue user(@Nonnull String id) {
         return new DefaultValue(MiscUtil.parseSnowflake(id), EntitySelectMenu.SelectTarget.USER);
      }

      @Nonnull
      public static DefaultValue channel(long id) {
         return new DefaultValue(id, EntitySelectMenu.SelectTarget.CHANNEL);
      }

      @Nonnull
      public static DefaultValue channel(@Nonnull String id) {
         return new DefaultValue(MiscUtil.parseSnowflake(id), EntitySelectMenu.SelectTarget.CHANNEL);
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public SelectTarget getType() {
         return this.type;
      }

      @Nonnull
      public DataObject toData() {
         return DataObject.empty().put("type", this.type.name().toLowerCase(Locale.ROOT)).put("id", this.getId());
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.type, this.id});
      }

      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof DefaultValue)) {
            return false;
         } else {
            DefaultValue other = (DefaultValue)obj;
            return this.id == other.id && this.type == other.type;
         }
      }

      public String toString() {
         return (new EntityString(this)).setType(this.type).toString();
      }
   }

   public static class Builder extends SelectMenu.Builder<EntitySelectMenu, Builder> {
      protected Component.Type componentType;
      protected EnumSet<ChannelType> channelTypes = EnumSet.noneOf(ChannelType.class);
      protected List<DefaultValue> defaultValues = new ArrayList();

      protected Builder(@Nonnull String customId) {
         super(customId);
      }

      @Nonnull
      public Builder setEntityTypes(@Nonnull Collection<SelectTarget> types) {
         Checks.notEmpty(types, "Types");
         Checks.noneNull(types, "Types");
         EnumSet<SelectTarget> set = Helpers.<SelectTarget>copyEnumSet(SelectTarget.class, types);
         if (set.size() == 1) {
            if (set.contains(EntitySelectMenu.SelectTarget.CHANNEL)) {
               this.componentType = Component.Type.CHANNEL_SELECT;
            } else if (set.contains(EntitySelectMenu.SelectTarget.ROLE)) {
               this.componentType = Component.Type.ROLE_SELECT;
            } else if (set.contains(EntitySelectMenu.SelectTarget.USER)) {
               this.componentType = Component.Type.USER_SELECT;
            }
         } else {
            if (set.size() != 2) {
               throw new IllegalArgumentException("The provided combination of select targets is not supported. Provided: " + set);
            }

            if (!set.contains(EntitySelectMenu.SelectTarget.USER) || !set.contains(EntitySelectMenu.SelectTarget.ROLE)) {
               throw new IllegalArgumentException("The provided combination of select targets is not supported. Provided: " + set);
            }

            this.componentType = Component.Type.MENTIONABLE_SELECT;
         }

         return this;
      }

      @Nonnull
      public Builder setEntityTypes(@Nonnull SelectTarget type, @Nonnull SelectTarget... types) {
         Checks.notNull(type, "Type");
         Checks.noneNull(types, "Types");
         return this.setEntityTypes(EnumSet.of(type, types));
      }

      @Nonnull
      public Builder setChannelTypes(@Nonnull Collection<ChannelType> types) {
         Checks.noneNull(types, "Types");

         for(ChannelType type : types) {
            Checks.check(type.isGuild(), "Only guild channel types are allowed! Provided: %s", (Object)type);
         }

         this.channelTypes = Helpers.<ChannelType>copyEnumSet(ChannelType.class, types);
         return this;
      }

      @Nonnull
      public Builder setChannelTypes(@Nonnull ChannelType... types) {
         return this.setChannelTypes(Arrays.asList(types));
      }

      @Nonnull
      public Builder setDefaultValues(@Nonnull DefaultValue... values) {
         Checks.noneNull(values, "Default Values");
         return this.setDefaultValues((Collection)Arrays.asList(values));
      }

      @Nonnull
      public Builder setDefaultValues(@Nonnull Collection<? extends DefaultValue> values) {
         Checks.noneNull(values, "Default Values");
         Checks.check(values.size() <= 25, "Cannot add more than %d default values to a select menu!", (int)25);

         for(DefaultValue value : values) {
            SelectTarget type = value.getType();
            String error = "The select menu supports types %s, but provided default value has type SelectTarget.%s!";
            switch (this.componentType) {
               case ROLE_SELECT:
                  Checks.check(type == EntitySelectMenu.SelectTarget.ROLE, error, "SelectTarget.ROLE", type);
                  break;
               case USER_SELECT:
                  Checks.check(type == EntitySelectMenu.SelectTarget.USER, error, "SelectTarget.USER", type);
                  break;
               case CHANNEL_SELECT:
                  Checks.check(type == EntitySelectMenu.SelectTarget.CHANNEL, error, "SelectTarget.CHANNEL", type);
                  break;
               case MENTIONABLE_SELECT:
                  Checks.check(type == EntitySelectMenu.SelectTarget.ROLE || type == EntitySelectMenu.SelectTarget.USER, error, "SelectTarget.ROLE and SelectTarget.USER", type);
                  break;
               default:
                  throw new IllegalStateException("Unexpected component type: " + this.componentType);
            }
         }

         this.defaultValues.clear();
         this.defaultValues.addAll(values);
         return this;
      }

      @Nonnull
      public EntitySelectMenu build() {
         Checks.check(this.minValues <= this.maxValues, "Min values cannot be greater than max values!");
         if (this.required != null && this.required) {
            Checks.check(this.minValues > 0, "A select menu cannot have min values set to 0 and be required at the same time");
         }

         EnumSet<ChannelType> channelTypes = this.componentType == Component.Type.CHANNEL_SELECT ? this.channelTypes : EnumSet.noneOf(ChannelType.class);
         List<DefaultValue> defaultValues = new ArrayList(this.defaultValues);
         return new EntitySelectMenuImpl(this.customId, this.uniqueId, this.placeholder, this.minValues, this.maxValues, this.disabled, this.componentType, channelTypes, defaultValues, this.required);
      }
   }
}
