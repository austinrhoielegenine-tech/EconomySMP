package com.austynzdevv.economysmp;

import java.io.File;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.austynzdevv.economysmp.data.PlayerDataStore;
import com.austynzdevv.economysmp.util.Tasks;
import com.austynzdevv.economysmp.util.TextUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class LinksModule implements Module {
   private final EconomySMPcore plugin;
   private final SecureRandom random = new SecureRandom();
   private final Map<String, UUID> pending = new ConcurrentHashMap();
   private final Set<String> usedCodes = ConcurrentHashMap.newKeySet();
   private final Map<UUID, String> linked = new ConcurrentHashMap();
   private final Map<String, UUID> discordToMinecraft = new ConcurrentHashMap();
   private FileConfiguration config;
   private File dataFile;
   private JDA jda;
   private String guildId;

   public LinksModule(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "links";
   }

   public void onEnable() {
      this.loadConfig();
      this.loadData();
      this.plugin.commands().registerHandler("link", this::linkCommand);
      this.plugin.commands().registerHandler("unlink", this::unlinkCommand);
      if (this.config.getBoolean("enabled", false)) {
         this.startDiscord();
      }

      Tasks.runAsyncTimer(this.plugin, this::syncLinkedPlayers, 1200L, 20L * (long)Math.max(30, this.config.getInt("sync-interval-seconds", 60)));
   }

   public void onDisable() {
      this.saveData();
      if (this.jda != null) {
         try {
            this.jda.shutdown();
         } catch (Exception var2) {
         }

         this.jda = null;
      }

   }

   public void reload() {
      this.loadConfig();
   }

   private void loadConfig() {
      File file = new File(this.plugin.getDataFolder(), "links.yml");
      if (!file.exists()) {
         try {
            this.plugin.saveResource("links.yml", false);
         } catch (Exception var3) {
         }
      }

      this.config = YamlConfiguration.loadConfiguration(file);
   }

   private void loadData() {
      this.dataFile = PlayerDataStore.get().file();
      FileConfiguration data = PlayerDataStore.get().config();
      ConfigurationSection linkedSection = data.getConfigurationSection("linked");
      if (linkedSection != null) {
         for(String key : linkedSection.getKeys(false)) {
            try {
               UUID uuid = UUID.fromString(key);
               String discord = linkedSection.getString(key + ".discord-id");
               if (discord != null && !discord.isBlank()) {
                  this.linked.put(uuid, discord);
                  this.discordToMinecraft.put(discord, uuid);
               }
            } catch (Exception var7) {
            }
         }
      }

      ConfigurationSection used = data.getConfigurationSection("used-codes");
      if (used != null) {
         used.getKeys(false).forEach((c) -> this.usedCodes.add(c.toUpperCase(Locale.ROOT)));
      }

   }

   private synchronized void saveData() {
      if (this.dataFile != null) {
         FileConfiguration data = PlayerDataStore.get().config();
         data.set("linked", (Object)null);

         for(Map.Entry<UUID, String> entry : this.linked.entrySet()) {
            data.set("linked." + String.valueOf(entry.getKey()) + ".discord-id", entry.getValue());
         }

         data.set("used-codes", (Object)null);

         for(String code : this.usedCodes) {
            data.set("used-codes." + code, true);
         }

         PlayerDataStore.get().save();
      }
   }

   private boolean linkCommand(CommandSender sender, String[] args) {
      if (!(sender instanceof Player player)) {
         sender.sendMessage("Players only.");
         return true;
      } else {
         boolean enabled = this.config.getBoolean("enabled", true);
         String token = this.config.getString("bot-token", this.config.getString("token", ""));
         if (token != null && !token.isBlank() && !token.contains("YOUR")) {
            if ((!enabled || this.jda == null) && this.jda == null && enabled) {
               try {
                  this.startDiscord();
               } catch (Exception var9) {
               }
            }

            if (enabled && this.jda != null) {
               if (this.linked.containsKey(player.getUniqueId())) {
                  TextUtil.send(player, "&7Your Minecraft account is already &#37BFF9linked&7.");
                  return true;
               } else {
                  String code = this.generateCode();
                  this.pending.put(code, player.getUniqueId());
                  long expiry = Math.max(60L, this.config.getLong("code-expire-seconds", 300L));
                  Tasks.runLater(this.plugin, () -> this.pending.remove(code), expiry * 20L);
                  TextUtil.send(player, "&7Your Discord link code is &#37BFF9" + code + "&7.");
                  TextUtil.send(player, "&7Open Discord and use &#37BFF9/link " + code + "&7.");
                  return true;
               }
            } else {
               TextUtil.send(player, "&cDiscord linking is currently disabled.");
               return true;
            }
         } else {
            TextUtil.send(player, "&cDiscord link bot token is not configured in links.yml.");
            return true;
         }
      }
   }

   private boolean unlinkCommand(CommandSender sender, String[] args) {
      if (!sender.hasPermission("economysmpcore.system.links.unlink") && !sender.isOp()) {
         TextUtil.send(sender, "&cNo permission.");
         return true;
      } else if (args.length < 1) {
         TextUtil.send(sender, "&cUsage: /unlink <username>");
         return true;
      } else {
         OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
         String discord = (String)this.linked.get(target.getUniqueId());
         if (discord == null) {
            TextUtil.send(sender, "&cThat player is not linked.");
            return true;
         } else {
            this.unlink(target.getUniqueId(), discord, true);
            String var10001 = target.getName() == null ? args[0] : target.getName();
            TextUtil.send(sender, "&7Unlinked &#37BFF9" + var10001 + "&7.");
            return true;
         }
      }
   }

   private String generateCode() {
      char[] chars = "ABCDEFGHJKLMNPQRSTUVWXYZ".toCharArray();

      String code;
      do {
         StringBuilder out = new StringBuilder(5);

         for(int i = 0; i < 5; ++i) {
            out.append(chars[this.random.nextInt(chars.length)]);
         }

         code = out.toString();
      } while(this.pending.containsKey(code) || this.usedCodes.contains(code));

      this.usedCodes.add(code);
      this.saveData();
      return code;
   }

   private void startDiscord() {
      String token = this.config.getString("token", "");
      this.guildId = this.config.getString("guild-id", "");
      if (token != null && !token.isBlank() && this.guildId != null && !this.guildId.isBlank()) {
         try {
            this.jda = JDABuilder.createDefault(token).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(new DiscordListener()).build();
            this.jda.awaitReady();
            this.jda.upsertCommand("link", "Link your Discord account to Minecraft").addOption(OptionType.STRING, "code", "The 5-letter link code", true).queue();
         } catch (Exception e) {
            this.plugin.getLogger().warning("Could not start Discord link bot: " + e.getMessage());
            this.jda = null;
         }

      } else {
         this.plugin.getLogger().warning("Discord links are enabled but links.yml is missing token or guild-id.");
      }
   }

   private void completeLink(String code, Member member, SlashCommandInteractionEvent event) {
      UUID uuid = (UUID)this.pending.remove(code.toUpperCase(Locale.ROOT));
      if (uuid == null) {
         event.reply("That link code is invalid or expired.").setEphemeral(true).queue();
      } else if (!member.getGuild().getId().equals(this.guildId)) {
         event.reply("This Discord server is not configured for linking.").setEphemeral(true).queue();
         this.pending.put(code.toUpperCase(Locale.ROOT), uuid);
      } else if (this.discordToMinecraft.containsKey(member.getId())) {
         event.reply("Your Discord account is already linked.").setEphemeral(true).queue();
      } else {
         RoleRank rank = this.findConfiguredRank(member);
         String group = rank == null ? this.config.getString("default-group", "default") : rank.group;
         this.applyGroup(uuid, group);
         this.linked.put(uuid, member.getId());
         this.discordToMinecraft.put(member.getId(), uuid);
         this.saveData();
         this.addLinkedRole(member);
         String var10001 = this.playerName(uuid);
         event.reply("Your account is now linked to " + var10001 + ".").setEphemeral(true).queue();
         Player player = Bukkit.getPlayer(uuid);
         if (player != null) {
            TextUtil.send(player, "&aYour Discord account has been successfully linked.");
         }

      }
   }

   private RoleRank findConfiguredRank(Member member) {
      ConfigurationSection roles = this.config.getConfigurationSection("roles");
      return roles == null ? null : (RoleRank)roles.getKeys(false).stream().<RoleRank>map((id) -> {
         Role role = member.getGuild().getRoleById(id);
         if (role != null && member.getRoles().contains(role)) {
            String group = roles.getString(id + ".luckperms-group", "default");
            return new RoleRank(role, group);
         } else {
            return null;
         }
      }).filter((r) -> r != null).max(Comparator.comparingInt((RoleRank r) -> r.role.getPositionRaw())).orElse(null);
   }

   private void syncLinkedPlayers() {
      if (this.jda != null && this.guildId != null) {
         Guild guild = this.jda.getGuildById(this.guildId);
         if (guild != null) {
            for(Map.Entry<UUID, String> entry : (new HashMap<>(this.linked)).entrySet()) {
               UUID uuid = (UUID)entry.getKey();
               String discord = (String)entry.getValue();

               try {
                  Member member = (Member)guild.retrieveMemberById(discord).submit().get(10L, TimeUnit.SECONDS);
                  RoleRank rank = this.findConfiguredRank(member);
                  String group = rank == null ? this.config.getString("default-group", "default") : rank.group;
                  this.applyGroup(uuid, group);
                  this.addLinkedRole(member);
               } catch (Exception e) {
                  if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.ROOT).contains("unknown member")) {
                     this.unlink(uuid, discord, true);
                  }
               }
            }

         }
      }
   }

   private void unlink(UUID uuid, String discord, boolean removeRole) {
      this.linked.remove(uuid);
      this.discordToMinecraft.remove(discord);
      this.applyGroup(uuid, this.config.getString("default-group", "default"));
      this.saveData();
      if (removeRole && this.jda != null && this.guildId != null) {
         Guild guild = this.jda.getGuildById(this.guildId);
         if (guild != null) {
            Member member = guild.getMemberById(discord);
            if (member != null) {
               this.removeLinkedRole(member);
            }
         }
      }

      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
         TextUtil.send(player, "&cYour Discord link has been removed.");
      }

   }

   private void applyGroup(UUID uuid, String group) {
      try {
         LuckPerms lp = LuckPermsProvider.get();
         lp.getUserManager().modifyUser(uuid, (user) -> {
            ConfigurationSection roles = this.config.getConfigurationSection("roles");
            if (roles != null) {
               for(String key : roles.getKeys(false)) {
                  String configured = roles.getString(key + ".luckperms-group");
                  if (configured != null && !configured.isBlank()) {
                     user.data().remove(Node.builder("group." + configured).build());
                  }
               }
            }

            if (group != null && !group.isBlank()) {
               user.data().add(Node.builder("group." + group).build());
            }

         }).join();
      } catch (Exception t) {
         this.plugin.getLogger().warning("Could not update LuckPerms group: " + t.getMessage());
      }

   }

   private void addLinkedRole(Member member) {
      String roleId = this.config.getString("linked-role-id", "");
      if (roleId != null && !roleId.isBlank()) {
         Role role = member.getGuild().getRoleById(roleId);
         if (role != null && !member.getRoles().contains(role)) {
            member.getGuild().addRoleToMember(member, role).queue();
         }

      }
   }

   private void removeLinkedRole(Member member) {
      String roleId = this.config.getString("linked-role-id", "");
      if (roleId != null && !roleId.isBlank()) {
         Role role = member.getGuild().getRoleById(roleId);
         if (role != null && member.getRoles().contains(role)) {
            member.getGuild().removeRoleFromMember(member, role).queue();
         }

      }
   }

   private String playerName(UUID uuid) {
      OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
      return p.getName() == null ? uuid.toString() : p.getName();
   }

   private void syncMember(Member member) {
      UUID uuid = (UUID)this.discordToMinecraft.get(member.getId());
      if (uuid != null) {
         RoleRank rank = this.findConfiguredRank(member);
         String group = rank == null ? this.config.getString("default-group", "default") : rank.group;
         this.applyGroup(uuid, group);
         this.addLinkedRole(member);
      }
   }

   private final class DiscordListener extends ListenerAdapter {
      public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
         if (event.getName().equals("link")) {
            if (event.getGuild() != null && event.getGuild().getId().equals(LinksModule.this.guildId)) {
               String code = event.getOption("code") == null ? "" : event.getOption("code").getAsString();
               LinksModule.this.completeLink(code, event.getMember(), event);
            }
         }
      }

      public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
         if (event.getGuild().getId().equals(LinksModule.this.guildId)) {
            UUID uuid = (UUID)LinksModule.this.discordToMinecraft.get(event.getUser().getId());
            if (uuid != null) {
               LinksModule.this.unlink(uuid, event.getUser().getId(), false);
            }

         }
      }

      public void onGuildMemberJoin(GuildMemberJoinEvent event) {
         if (event.getGuild().getId().equals(LinksModule.this.guildId)) {
            LinksModule.this.syncMember(event.getMember());
         }

      }

      public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
         if (event.getGuild().getId().equals(LinksModule.this.guildId)) {
            LinksModule.this.syncMember(event.getMember());
         }

      }

      public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
         if (event.getGuild().getId().equals(LinksModule.this.guildId)) {
            LinksModule.this.syncMember(event.getMember());
         }

      }
   }

   private static final class RoleRank {
      private final Role role;
      private final String group;

      private RoleRank(Role role, String group) {
         this.role = role;
         this.group = group;
      }
   }
}
