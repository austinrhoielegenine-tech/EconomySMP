package net.dv8tion.jda.api.entities.automod.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.automod.AutoModEventType;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Checks;

public class AutoModRuleData implements SerializableData {
   protected final AutoModEventType eventType;
   protected String name;
   protected boolean enabled = true;
   protected TriggerConfig triggerMetadata;
   protected final EnumMap<AutoModResponse.Type, AutoModResponse> actions = new EnumMap(AutoModResponse.Type.class);
   protected final Collection<String> exemptChannels = new ArrayList();
   protected final Collection<String> exemptRoles = new ArrayList();

   protected AutoModRuleData(AutoModEventType eventType, String name, TriggerConfig triggerMetadata) {
      this.eventType = eventType;
      this.setName(name);
      this.setTriggerConfig(triggerMetadata);
   }

   @Nonnull
   public static AutoModRuleData onMessage(@Nonnull String name, @Nonnull TriggerConfig triggerConfig) {
      return new AutoModRuleData(AutoModEventType.MESSAGE_SEND, name, triggerConfig);
   }

   @Nonnull
   public static AutoModRuleData onMemberProfile(@Nonnull String name, @Nonnull TriggerConfig triggerConfig) {
      return (new AutoModRuleData(AutoModEventType.MEMBER_UPDATE, name, triggerConfig)).putResponses(AutoModResponse.blockMemberInteraction());
   }

   @Nonnull
   public AutoModRuleData setName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      return this;
   }

   @Nonnull
   public AutoModRuleData setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   @Nonnull
   public AutoModRuleData putResponses(@Nonnull AutoModResponse... responses) {
      Checks.noneNull(responses, "Responses");

      for(AutoModResponse response : responses) {
         AutoModResponse.Type type = response.getType();
         Checks.check(type != AutoModResponse.Type.UNKNOWN, "Cannot create response with unknown response type");
         this.actions.put(type, response);
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData putResponses(@Nonnull Collection<? extends AutoModResponse> responses) {
      Checks.noneNull(responses, "Responses");

      for(AutoModResponse response : responses) {
         AutoModResponse.Type type = response.getType();
         Checks.check(type != AutoModResponse.Type.UNKNOWN, "Cannot create response with unknown response type");
         this.actions.put(type, response);
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData setResponses(@Nonnull Collection<? extends AutoModResponse> responses) {
      Checks.noneNull(responses, "Responses");
      this.actions.clear();
      if (this.eventType == AutoModEventType.MEMBER_UPDATE) {
         this.actions.put(AutoModResponse.Type.BLOCK_MEMBER_INTERACTION, AutoModResponse.blockMemberInteraction());
      }

      for(AutoModResponse response : responses) {
         AutoModResponse.Type type = response.getType();
         Checks.check(type != AutoModResponse.Type.UNKNOWN, "Cannot create response with unknown response type");
         this.actions.put(type, response);
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData addExemptRoles(@Nonnull Role... roles) {
      Checks.noneNull(roles, "Roles");
      Checks.check(roles.length + this.exemptRoles.size() <= 20, "Cannot add more than %d roles", (int)20);

      for(Role role : roles) {
         this.exemptRoles.add(role.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData addExemptRoles(@Nonnull Collection<? extends Role> roles) {
      Checks.noneNull(roles, "Roles");
      Checks.check(roles.size() + this.exemptRoles.size() <= 20, "Cannot add more than %d roles", (int)20);

      for(Role role : roles) {
         this.exemptRoles.add(role.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData setExemptRoles(@Nonnull Collection<? extends Role> roles) {
      Checks.noneNull(roles, "Roles");
      Checks.check(roles.size() <= 20, "Cannot add more than %d roles", (int)20);
      this.exemptRoles.clear();

      for(Role role : roles) {
         this.exemptRoles.add(role.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData addExemptChannels(@Nonnull GuildChannel... channels) {
      Checks.noneNull(channels, "Channels");
      Checks.check(channels.length + this.exemptChannels.size() <= 50, "Cannot add more than %d channels", (int)50);

      for(GuildChannel channel : channels) {
         this.exemptChannels.add(channel.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData addExemptChannels(@Nonnull Collection<? extends GuildChannel> channels) {
      Checks.noneNull(channels, "Channels");
      Checks.check(channels.size() + this.exemptChannels.size() <= 50, "Cannot add more than %d channels", (int)50);

      for(GuildChannel channel : channels) {
         this.exemptChannels.add(channel.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData setExemptChannels(@Nonnull Collection<? extends GuildChannel> channels) {
      Checks.noneNull(channels, "Channels");
      Checks.check(channels.size() <= 50, "Cannot add more than %d channels", (int)50);
      this.exemptChannels.clear();

      for(GuildChannel channel : channels) {
         this.exemptChannels.add(channel.getId());
      }

      return this;
   }

   @Nonnull
   public AutoModRuleData setTriggerConfig(@Nonnull TriggerConfig config) {
      Checks.notNull(config, "TriggerConfig");
      Checks.check(config.getType().isEventTypeSupported(this.eventType), "Cannot use trigger type %s with event type %s", config.getType(), this.eventType);
      this.triggerMetadata = config;
      return this;
   }

   @Nonnull
   public EnumSet<Permission> getRequiredPermissions() {
      return this.actions.containsKey(AutoModResponse.Type.TIMEOUT) ? EnumSet.of(Permission.MANAGE_SERVER, Permission.MODERATE_MEMBERS) : EnumSet.of(Permission.MANAGE_SERVER);
   }

   @Nonnull
   public DataObject toData() {
      AutoModTriggerType triggerType = this.triggerMetadata.getType();
      if (this.eventType == AutoModEventType.MEMBER_UPDATE) {
         if (triggerType != AutoModTriggerType.KEYWORD) {
            throw new IllegalStateException("Cannot create rule of trigger type " + triggerType + " with event type " + this.eventType);
         }

         triggerType = AutoModTriggerType.MEMBER_PROFILE_KEYWORD;
      }

      for(AutoModResponse response : this.actions.values()) {
         if (!response.getType().isSupportedTrigger(triggerType)) {
            throw new IllegalStateException("Cannot create a rule of trigger type " + triggerType + " with response type " + response.getType());
         }
      }

      if (this.actions.isEmpty()) {
         throw new IllegalStateException("Cannot create a rule with no responses. Add at least one response with putResponses(...)");
      } else {
         DataObject data = DataObject.empty().put("name", this.name).put("enabled", this.enabled).put("event_type", this.eventType.getKey());
         data.put("actions", DataArray.fromCollection(this.actions.values()));
         data.put("exempt_roles", DataArray.fromCollection(this.exemptRoles));
         data.put("exempt_channels", DataArray.fromCollection(this.exemptChannels));
         data.put("trigger_type", triggerType.getKey());
         data.put("trigger_metadata", this.triggerMetadata.toData());
         return data;
      }
   }
}
