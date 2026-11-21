package net.dv8tion.jda.internal.entities.automod;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.automod.AutoModEventType;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.entities.automod.AutoModTriggerType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class AutoModRuleImpl implements AutoModRule {
   private final long id;
   private Guild guild;
   private long ownerId;
   private String name = "";
   private AutoModEventType eventType;
   private AutoModTriggerType triggerType;
   private boolean enabled;
   private TLongList exemptRoles;
   private TLongList exemptChannels;
   private List<AutoModResponse> actions;
   private List<String> filteredKeywords;
   private List<String> filteredRegex;
   private EnumSet<AutoModRule.KeywordPreset> filteredPresets;
   private List<String> allowlist;
   private int mentionLimit;
   private boolean isMentionRaidProtectionEnabled;

   public AutoModRuleImpl(Guild guild, long id) {
      this.eventType = AutoModEventType.UNKNOWN;
      this.triggerType = AutoModTriggerType.UNKNOWN;
      this.enabled = false;
      this.exemptRoles = new TLongArrayList();
      this.exemptChannels = new TLongArrayList();
      this.actions = Collections.emptyList();
      this.filteredKeywords = Collections.emptyList();
      this.filteredRegex = Collections.emptyList();
      this.filteredPresets = EnumSet.noneOf(AutoModRule.KeywordPreset.class);
      this.allowlist = Collections.emptyList();
      this.mentionLimit = -1;
      this.isMentionRaidProtectionEnabled = false;
      this.id = id;
      this.guild = guild;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public Guild getGuild() {
      Guild realGuild = this.guild.getJDA().getGuildById(this.guild.getIdLong());
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   public long getCreatorIdLong() {
      return this.ownerId;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public AutoModEventType getEventType() {
      return this.eventType;
   }

   @Nonnull
   public AutoModTriggerType getTriggerType() {
      return this.triggerType;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   @Nonnull
   public List<Role> getExemptRoles() {
      List<Role> roles = new ArrayList(this.exemptRoles.size());

      for(int i = 0; i < this.exemptRoles.size(); ++i) {
         long roleId = this.exemptRoles.get(i);
         Role role = this.guild.getRoleById(roleId);
         if (role != null) {
            roles.add(role);
         }
      }

      return Collections.unmodifiableList(roles);
   }

   @Nonnull
   public List<GuildChannel> getExemptChannels() {
      List<GuildChannel> channels = new ArrayList(this.exemptChannels.size());

      for(int i = 0; i < this.exemptChannels.size(); ++i) {
         long channelId = this.exemptChannels.get(i);
         GuildChannel channel = this.guild.getGuildChannelById(channelId);
         if (channel != null) {
            channels.add(channel);
         }
      }

      return Collections.unmodifiableList(channels);
   }

   @Nonnull
   public List<AutoModResponse> getActions() {
      return this.actions;
   }

   @Nonnull
   public List<String> getFilteredKeywords() {
      return this.filteredKeywords;
   }

   @Nonnull
   public List<String> getFilteredRegex() {
      return this.filteredRegex;
   }

   @Nonnull
   public EnumSet<AutoModRule.KeywordPreset> getFilteredPresets() {
      return Helpers.<AutoModRule.KeywordPreset>copyEnumSet(AutoModRule.KeywordPreset.class, this.filteredPresets);
   }

   @Nonnull
   public List<String> getAllowlist() {
      return this.allowlist;
   }

   public int getMentionLimit() {
      return this.mentionLimit;
   }

   public boolean isMentionRaidProtectionEnabled() {
      return this.isMentionRaidProtectionEnabled;
   }

   public AutoModRuleImpl setName(String name) {
      this.name = name;
      return this;
   }

   public AutoModRuleImpl setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public AutoModRuleImpl setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   public AutoModRuleImpl setEventType(AutoModEventType eventType) {
      this.eventType = eventType;
      return this;
   }

   public AutoModRuleImpl setTriggerType(AutoModTriggerType triggerType) {
      this.triggerType = triggerType;
      return this;
   }

   public AutoModRuleImpl setExemptRoles(TLongList exemptRoles) {
      this.exemptRoles = exemptRoles;
      return this;
   }

   public AutoModRuleImpl setExemptChannels(TLongList exemptChannels) {
      this.exemptChannels = exemptChannels;
      return this;
   }

   public AutoModRuleImpl setActions(List<AutoModResponse> actions) {
      this.actions = actions;
      return this;
   }

   public AutoModRuleImpl setFilteredKeywords(List<String> filteredKeywords) {
      this.filteredKeywords = filteredKeywords;
      return this;
   }

   public AutoModRuleImpl setFilteredRegex(List<String> filteredRegex) {
      this.filteredRegex = filteredRegex;
      return this;
   }

   public AutoModRuleImpl setFilteredPresets(EnumSet<AutoModRule.KeywordPreset> filteredPresets) {
      this.filteredPresets = filteredPresets;
      return this;
   }

   public AutoModRuleImpl setAllowlist(List<String> allowlist) {
      this.allowlist = allowlist;
      return this;
   }

   public AutoModRuleImpl setMentionLimit(int mentionLimit) {
      this.mentionLimit = mentionLimit;
      return this;
   }

   public AutoModRuleImpl setMentionRaidProtectionEnabled(boolean mentionRaidProtectionEnabled) {
      this.isMentionRaidProtectionEnabled = mentionRaidProtectionEnabled;
      return this;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AutoModRuleImpl)) {
         return false;
      } else {
         AutoModRuleImpl oRule = (AutoModRuleImpl)obj;
         return this.id == oRule.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).setType(this.triggerType).setName(this.name).addMetadata("id", this.getId()).toString();
   }

   public static AutoModRuleImpl fromData(Guild guild, DataObject data) {
      long id = data.getUnsignedLong("id");
      AutoModRuleImpl rule = new AutoModRuleImpl(guild, id);
      rule.setName(data.getString("name")).setEnabled(data.getBoolean("enabled", true)).setOwnerId(data.getUnsignedLong("creator_id", 0L)).setEventType(AutoModEventType.fromKey(data.getInt("event_type", -1))).setTriggerType(AutoModTriggerType.fromKey(data.getInt("trigger_type", -1)));
      data.optArray("exempt_roles").ifPresent((array) -> rule.setExemptRoles(parseList(array)));
      data.optArray("exempt_channels").ifPresent((array) -> rule.setExemptChannels(parseList(array)));
      data.optArray("actions").ifPresent((array) -> rule.setActions((List)array.stream(DataArray::getObject).map((obj) -> new AutoModResponseImpl(guild, obj)).collect(Helpers.toUnmodifiableList())));
      data.optObject("trigger_metadata").ifPresent((metadata) -> {
         metadata.optArray("keyword_filter").ifPresent((array) -> rule.setFilteredKeywords((List)array.stream(DataArray::getString).collect(Helpers.toUnmodifiableList())));
         metadata.optArray("regex_patterns").ifPresent((array) -> rule.setFilteredRegex((List)array.stream(DataArray::getString).collect(Helpers.toUnmodifiableList())));
         metadata.optArray("allow_list").ifPresent((array) -> rule.setAllowlist((List)array.stream(DataArray::getString).collect(Helpers.toUnmodifiableList())));
         metadata.optArray("presets").ifPresent((array) -> rule.setFilteredPresets((EnumSet)array.stream(DataArray::getInt).map(AutoModRule.KeywordPreset::fromKey).collect(Collectors.toCollection(() -> EnumSet.noneOf(AutoModRule.KeywordPreset.class)))));
         rule.setMentionLimit(metadata.getInt("mention_total_limit", 0));
         rule.setMentionRaidProtectionEnabled(metadata.getBoolean("mention_raid_protection_enabled"));
      });
      return rule;
   }

   private static TLongList parseList(DataArray array) {
      TLongList list = new TLongArrayList(array.length());

      for(int i = 0; i < array.length(); ++i) {
         list.add(array.getUnsignedLong(i));
      }

      return list;
   }
}
