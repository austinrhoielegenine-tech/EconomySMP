package me.xyr0.astralsmpcore;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.auction.gui.MenuSession;
import me.xyr0.astralsmpcore.data.PlayerDataStore;
import me.xyr0.astralsmpcore.mobs.AstralMobs;
import me.xyr0.astralsmpcore.scoreboard.ScoreboardModule;
import me.xyr0.astralsmpcore.sell.AstralSell;
import me.xyr0.astralsmpcore.teams.AstralTeams;
import me.xyr0.astralsmpcore.tpa.AstralTPA;
import me.xyr0.astralsmpcore.util.Tasks;
import me.xyr0.astralsmpcore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SettingsModule implements Module, Listener {
   private final AstralSMPcore plugin;
   private FileConfiguration cfg;
   private FileConfiguration settingsData;
   private final Set<UUID> soundsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> chatOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> chainmailOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> fastCrystal = ConcurrentHashMap.newKeySet();
   private final Set<UUID> teamChat = ConcurrentHashMap.newKeySet();
   private final Set<UUID> teamInvitesOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> orderNotifOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> worthOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> serverChatOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> hotbarOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> bountyAlertOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> auctionAlertOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> explosionSoundsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> quickBuy = ConcurrentHashMap.newKeySet();
   private final Set<UUID> scoreboardOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> paymentsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> visibilityOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> mobsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> duelSongsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> duelRequestsOff = ConcurrentHashMap.newKeySet();
   private final Set<UUID> teamChatPref = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Map<String, Boolean>> playerSettings = new ConcurrentHashMap();
   private static SettingsModule instance;

   public SettingsModule(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "settings";
   }

   public void onEnable() {
      instance = this;

      try {
         this.plugin.commands().registerHandler("globaltoggle", (s, a) -> this.toggleKey(s, a, "public_chat", "&7Public chat"));
         this.plugin.commands().registerHandler("visibilitytoggle", (s, a) -> this.toggleKey(s, a, "player_visibility", "&7Player visibility"));
         this.plugin.commands().registerHandler("chattoggle", (s, a) -> this.toggleKey(s, a, "public_chat", "&7Public chat"));
      } catch (Exception var2) {
      }

      this.load();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("settings", this::open);
      reg.registerHandler("soundstoggle", this::toggleSounds);
      reg.registerHandler("fastcrystal", this::toggleCrystal);
      reg.registerHandler("teamchat", this::toggleTeamChat);
      reg.registerHandler("teamtoggle", this::toggleTeamInvites);
      reg.registerHandler("ordertoggle", this::toggleOrders);
      reg.registerHandler("worthtoggle", this::toggleWorth);
      reg.registerHandler("chattoggle", this::toggleChat);
      reg.registerHandler("chat", this::chatCommand);
      reg.registerHandler("cmtoggle", this::toggleChainmail);
      reg.registerHandler("chainmailtoggle", this::toggleChainmail);
      reg.registerHandler("cm", this::toggleChainmail);
      reg.registerHandler("chainmail", this::toggleChainmail);
      reg.registerHandler("servertoggle", this::toggleServerChat);
      reg.registerHandler("hotbartoggle", this::toggleHotbar);
      reg.registerHandler("bountytoggle", this::toggleBountyAlerts);
      reg.registerHandler("auctiontoggle", this::toggleAuctionAlerts);
      reg.registerHandler("explosionsoundtoggle", this::toggleExplosionSounds);
      reg.registerHandler("quickbuy", this::toggleQuickBuy);
      reg.registerHandler("visibilitytoggle", this::toggleVisibility);
      reg.registerHandler("playervisibility", this::toggleVisibility);
      reg.registerHandler("payalerttoggle", this::togglePayAlert);
      reg.registerHandler("tpatoggle", (s, a) -> {
         if (s instanceof Player p) {
            p.performCommand("tpatoggle");
         }

         return true;
      });
      reg.registerHandler("tpaheretoggle", (s, a) -> {
         if (s instanceof Player p) {
            p.performCommand("tpaheretoggle");
         }

         return true;
      });
      reg.registerHandler("tpauto", (s, a) -> {
         if (s instanceof Player p) {
            p.performCommand("tpauto");
         }

         return true;
      });
      reg.registerHandler("mobtoggle", this::toggleMobs);
      reg.registerHandler("tpaconfirmtoggle", this::toggleTpaConfirm);
      reg.registerHandler("duelsongtoggle", this::toggleDuelSongs);
      reg.registerHandler("duelrequesttoggle", this::toggleDuelRequests);
      reg.registerHandler("teamchattoggle", this::toggleTeamChat);
      reg.registerHandler("globaltoggle", this::toggleGlobalChat);
      reg.registerHandler("msgtoggle", this::toggleMsgProxy);
      reg.registerHandler("sb", this::toggleScoreboardProxy);
   }

   public void onDisable() {
   }

   public void reload() {
      this.load();
   }

   private void load() {
      File f = new File(this.plugin.getDataFolder(), "settings.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("settings.yml", false);
         } catch (Exception var3) {
         }
      }

      this.cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.settingsData = PlayerDataStore.get().config();
      this.loadPlayerSettings();
   }

   private void loadPlayerSettings() {
      this.soundsOff.clear();
      this.chatOff.clear();
      this.chainmailOff.clear();
      this.fastCrystal.clear();
      this.teamChat.clear();
      this.teamInvitesOff.clear();
      this.orderNotifOff.clear();
      this.worthOff.clear();
      this.serverChatOff.clear();
      this.hotbarOff.clear();
      this.bountyAlertOff.clear();
      this.auctionAlertOff.clear();
      this.explosionSoundsOff.clear();
      this.quickBuy.clear();
      this.scoreboardOff.clear();
      this.paymentsOff.clear();
      this.visibilityOff.clear();
      this.mobsOff.clear();
      this.duelSongsOff.clear();
      this.duelRequestsOff.clear();
      this.playerSettings.clear();
      if (this.settingsData != null) {
         ConfigurationSection root = this.settingsData.getConfigurationSection("players");
         if (root != null) {
            for(String raw : root.getKeys(false)) {
               try {
                  UUID id = UUID.fromString(raw);
                  Map<String, Boolean> values = new HashMap();
                  ConfigurationSection sec = root.getConfigurationSection(raw);
                  if (sec != null) {
                     for(String key : sec.getKeys(false)) {
                        values.put(key, sec.getBoolean(key));
                     }

                     this.playerSettings.put(id, values);

                     for(Map.Entry<String, Boolean> entry : values.entrySet()) {
                        this.applyStoredValue(id, (String)entry.getKey(), (Boolean)entry.getValue());
                     }
                  }
               } catch (IllegalArgumentException var9) {
               }
            }

         }
      }
   }

   private void applyStoredValue(UUID id, String key, boolean value) {
      switch (key) {
         case "fast_crystals":
            this.set(this.fastCrystal, id, value);
            break;
         case "sound_notifications":
            this.setOff(this.soundsOff, id, value);
            break;
         case "public_chat":
            this.setOff(this.chatOff, id, value);
            break;
         case "chainmail_on_respawn":
            this.setOff(this.chainmailOff, id, value);
            break;
         case "team_chat":
            this.set(this.teamChat, id, value);
            break;
         case "team_invites":
            this.setOff(this.teamInvitesOff, id, value);
            break;
         case "order_notifications":
            this.setOff(this.orderNotifOff, id, value);
            break;
         case "worth_display":
            this.setOff(this.worthOff, id, value);
            break;
         case "chat_server_messages":
            this.setOff(this.serverChatOff, id, value);
            break;
         case "hotbar_server_messages":
            this.setOff(this.hotbarOff, id, value);
            break;
         case "bounty_alerts":
            this.setOff(this.bountyAlertOff, id, value);
            break;
         case "auction_alerts":
            this.setOff(this.auctionAlertOff, id, value);
            break;
         case "explosion_sounds":
            this.setOff(this.explosionSoundsOff, id, value);
            break;
         case "quick_auction_buy":
            this.set(this.quickBuy, id, value);
            break;
         case "scoreboard":
            this.setOff(this.scoreboardOff, id, value);

            try {
               Module mod = this.plugin.modules().get("scoreboard");
               if (mod instanceof ScoreboardModule sm) {
                  sm.setHidden(id, !value);
               }
            } catch (Exception var8) {
            }
            break;
         case "payments":
         case "pay_alerts":
            this.setOff(this.paymentsOff, id, value);
            break;
         case "player_visibility":
            this.setOff(this.visibilityOff, id, value);
            break;
         case "mob_disable":
            this.setOff(this.mobsOff, id, value);
            break;
         case "after_duel_songs":
            this.setOff(this.duelSongsOff, id, value);
            break;
         case "duels_requests":
            this.setOff(this.duelRequestsOff, id, value);
      }

   }

   private void set(Set<UUID> set, UUID id, boolean value) {
      if (value) {
         set.add(id);
      } else {
         set.remove(id);
      }

   }

   private void setOff(Set<UUID> set, UUID id, boolean value) {
      if (value) {
         set.remove(id);
      } else {
         set.add(id);
      }

   }

   private void persist(Player p, String key, boolean value) {
      if (this.settingsData != null) {
         this.settingsData.set("players." + String.valueOf(p.getUniqueId()) + "." + key, value);
         PlayerDataStore.get().save();
         ((Map)this.playerSettings.computeIfAbsent(p.getUniqueId(), (k) -> new HashMap())).put(key, value);
      }
   }

   private Component toComponent(String s) {
      if (s == null) {
         s = "";
      }

      try {
         return TextUtil.component(s);
      } catch (Exception var3) {
         return LegacyComponentSerializer.legacyAmpersand().deserialize(TextUtil.color(s));
      }
   }

   private String color(String s) {
      return TextUtil.color(s == null ? "" : s);
   }

   private boolean open(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         this.openGui(p);
         return true;
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   private boolean chatCommand(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length > 0 && args[0].equalsIgnoreCase("toggle")) {
            return this.toggleChat(sender, args);
         } else {
            TextUtil.send(p, "&cUsage: /chat toggle");
            return true;
         }
      } else {
         return true;
      }
   }

   public void openGui(Player p) {
      int slots = this.cfg.getInt("settings-gui.gui-slots", 36);
      Component title = this.toComponent(this.cfg.getString("settings-gui.title", "&8Settings"));
      Inventory inv = Bukkit.createInventory(new SettingsHolder(), slots, title);
      ConfigurationSection section = this.cfg.getConfigurationSection("settings-gui.items");
      if (section != null) {
         for(String key : section.getKeys(false)) {
            ConfigurationSection s = section.getConfigurationSection(key);
            if (s != null) {
               int slot = s.getInt("slot", -1);

               Material mat;
               try {
                  mat = Material.valueOf(s.getString("material", "STONE").toUpperCase(Locale.ROOT));
               } catch (Exception var20) {
                  mat = Material.STONE;
               }

               ItemStack item = new ItemStack(mat);
               ItemMeta meta = item.getItemMeta();
               if (meta != null) {
                  meta.displayName(this.toComponent(s.getString("display-name", key)));
                  boolean on = this.isEnabled(p, key, s.getBoolean("default", true));
                  String statusOn = this.cfg.getString("settings-gui.status.on", "&#00FF00&lON");
                  String statusOff = this.cfg.getString("settings-gui.status.off", "&x&F&9&1&8&1&8&lOFF");
                  List<Component> lore = new ArrayList();
                  List<String> loreLines = s.getStringList("lore");
                  if (loreLines == null || loreLines.isEmpty()) {
                     String one = s.getString("lore", "&fCurrently: %status%");
                     loreLines = one != null && !one.isBlank() ? List.of(one) : List.of("&fCurrently: %status%");
                  }

                  for(String line : loreLines) {
                     if (line != null) {
                        line = line.replace("%status%", on ? statusOn : statusOff);
                        lore.add(this.toComponent(line));
                     }
                  }

                  meta.lore(lore);
                  item.setItemMeta(meta);
               }

               if (slot >= 0 && slot < inv.getSize()) {
                  inv.setItem(slot, item);
               }
            }
         }
      }

      p.openInventory(inv);
   }

   private boolean isEnabled(Player p, String key, boolean def) {
      UUID id = p.getUniqueId();
      if (key.equals("private_messages") || key.equals("msg") || key.equals("pm")) {
         try {
            Module mod = this.plugin.modules().get("msg");
            if (mod instanceof MsgModule) {
               MsgModule mm = (MsgModule)mod;
               return !mm.isMsgDisabled(id);
            }
         } catch (Exception var12) {
         }
      }

      if (!key.equals("pay_alerts") && !key.equals("payments")) {
         if (!key.equals("mob_disable") && !key.equals("disable_mob_spawns")) {
            Map<String, Boolean> map = (Map)this.playerSettings.get(id);
            if (map != null && map.containsKey(key)) {
               return (Boolean)map.get(key);
            } else {
               boolean var10000;
               switch (key) {
                  case "sounds_notifications":
                  case "sounds":
                  case "sound_notifications":
                     boolean var47 = !this.soundsOff.contains(id) && def;
                     var10000 = var47;
                     break;
                  case "explosion_sounds":
                     boolean var46 = !this.explosionSoundsOff.contains(id) && def;
                     var10000 = var46;
                     break;
                  case "fast_crystal":
                  case "fast_crystals":
                     boolean var45 = this.fastCrystal.contains(id) || def;
                     var10000 = var45;
                     break;
                  case "team_chat":
                     boolean var44;
                     label585: {
                        try {
                           AstralTeams teams = AstralTeams.get();
                           if (teams != null && teams.getTeams() != null) {
                              var44 = teams.getTeams().isChatToggled(id);
                              break label585;
                           }
                        } catch (Exception var19) {
                        }

                        var44 = this.teamChat.contains(id) || this.teamChatPref.contains(id) || def;
                        var10000 = var44;
                        break;
                     }

                     var10000 = var44;
                     break;
                  case "team_invites":
                     boolean var42 = !this.teamInvitesOff.contains(id) && def;
                     var10000 = var42;
                     break;
                  case "general_chat":
                  case "chat_toggle":
                  case "chat":
                  case "global_chat":
                  case "public_chat":
                     boolean var41 = !this.chatOff.contains(id) && def;
                     var10000 = var41;
                     break;
                  case "chainmail":
                  case "chainmail_kit":
                  case "chainmail_on_respawn":
                     boolean var40 = !this.chainmailOff.contains(id) && def;
                     var10000 = var40;
                     break;
                  case "order_notifications":
                     boolean var39 = !this.orderNotifOff.contains(id) && def;
                     var10000 = var39;
                     break;
                  case "worth_display":
                     boolean var38 = !this.worthOff.contains(id) && def;
                     var10000 = var38;
                     break;
                  case "chat_server_messages":
                     boolean var37 = !this.serverChatOff.contains(id) && def;
                     var10000 = var37;
                     break;
                  case "hotbar_server_messages":
                     boolean var36 = !this.hotbarOff.contains(id) && def;
                     var10000 = var36;
                     break;
                  case "bounty_alerts":
                     boolean var35 = !this.bountyAlertOff.contains(id) && def;
                     var10000 = var35;
                     break;
                  case "auction_alerts":
                     boolean var34 = !this.auctionAlertOff.contains(id) && def;
                     var10000 = var34;
                     break;
                  case "quick_auction_buy":
                     boolean var33 = this.quickBuy.contains(id) || def;
                     var10000 = var33;
                     break;
                  case "scoreboard":
                     if (this.scoreboardOff.contains(id)) {
                        boolean var31 = false;
                        var10000 = var31;
                     } else {
                        boolean var32;
                        label405: {
                           try {
                              Module mod = this.plugin.modules().get("scoreboard");
                              if (mod instanceof ScoreboardModule) {
                                 ScoreboardModule sm = (ScoreboardModule)mod;
                                 if (sm.isHidden(id)) {
                                    var32 = false;
                                    break label405;
                                 }
                              }
                           } catch (Exception var18) {
                           }

                           var10000 = def;
                           break;
                        }

                        var10000 = var32;
                     }
                     break;
                  case "payments":
                  case "pay_alerts":
                     boolean var30 = !this.paymentsOff.contains(id) && def;
                     var10000 = var30;
                     break;
                  case "player_visibility":
                  case "visibility":
                     boolean var29 = !this.visibilityOff.contains(id) && def;
                     var10000 = var29;
                     break;
                  case "private_messages":
                  case "msg":
                  case "pm":
                     boolean var28;
                     label388: {
                        try {
                           Module mod = this.plugin.modules().get("msg");
                           if (mod instanceof MsgModule) {
                              MsgModule mm = (MsgModule)mod;
                              var28 = !mm.isMsgDisabled(id);
                              break label388;
                           }
                        } catch (Exception var17) {
                        }

                        var10000 = def;
                        break;
                     }

                     var10000 = var28;
                     break;
                  case "disable_mob_spawns":
                  case "mob_disable":
                     boolean var27 = !this.mobsOff.contains(id) && def;
                     var10000 = var27;
                     break;
                  case "after_duel_songs":
                     boolean var26 = !this.duelSongsOff.contains(id) && def;
                     var10000 = var26;
                     break;
                  case "duels_requests":
                     boolean var25 = !this.duelRequestsOff.contains(id) && def;
                     var10000 = var25;
                     break;
                  case "tpa_confirm_menu":
                     boolean var24;
                     label367: {
                        try {
                           AstralTPA tpa = AstralTPA.get();
                           if (tpa != null && tpa.getTpa() != null) {
                              var24 = tpa.getTpa().isConfirmGuiEnabled(id);
                              break label367;
                           }
                        } catch (Exception var16) {
                        }

                        var10000 = def;
                        break;
                     }

                     var10000 = var24;
                     break;
                  case "tpa_requests":
                     boolean var23;
                     label360: {
                        try {
                           AstralTPA tpa = AstralTPA.get();
                           if (tpa != null && tpa.getTpa() != null) {
                              var23 = !tpa.getTpa().isTpaDisabled(id);
                              break label360;
                           }
                        } catch (Exception var15) {
                        }

                        var10000 = def;
                        break;
                     }

                     var10000 = var23;
                     break;
                  case "tpa_here_requests":
                     boolean var22;
                     label353: {
                        try {
                           AstralTPA tpa = AstralTPA.get();
                           if (tpa != null && tpa.getTpa() != null) {
                              var22 = !tpa.getTpa().isTpahereDisabled(id);
                              break label353;
                           }
                        } catch (Exception var14) {
                        }

                        var10000 = def;
                        break;
                     }

                     var10000 = var22;
                     break;
                  case "tpa_auto":
                     boolean var8;
                     label346: {
                        try {
                           AstralTPA tpa = AstralTPA.get();
                           if (tpa != null && tpa.getTpa() != null) {
                              var8 = tpa.getTpa().isTpAuto(id);
                              break label346;
                           }
                        } catch (Exception var13) {
                        }

                        var10000 = def;
                        break;
                     }

                     var10000 = var8;
                     break;
                  default:
                     var10000 = def;
               }

               return var10000;
            }
         } else {
            try {
               AstralMobs mobs = AstralMobs.getInstance();
               if (mobs != null && mobs.getPlayerDataManager() != null) {
                  return mobs.getPlayerDataManager().isMobSpawningEnabled(id);
               }
            } catch (Exception var11) {
            }

            return !this.mobsOff.contains(id);
         }
      } else {
         return !this.paymentsOff.contains(id) && def;
      }
   }

   private boolean isSettingsInv(Inventory inv) {
      return inv != null && inv.getHolder() instanceof SettingsHolder;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = false
   )
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         if (this.isSettingsInv(e.getView().getTopInventory()) || e.getInventory().getHolder() instanceof SettingsHolder) {
            e.setCancelled(true);
            e.setResult(Result.DENY);
            if (e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
               if (e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir()) {
                  ConfigurationSection section = this.cfg.getConfigurationSection("settings-gui.items");
                  if (section != null) {
                     for(String key : section.getKeys(false)) {
                        ConfigurationSection s = section.getConfigurationSection(key);
                        if (s != null && s.getInt("slot") == e.getRawSlot()) {
                           String cmd = s.getString("command", "");
                           this.isEnabled(p, key, s.getBoolean("default", true));
                           if (cmd != null && !cmd.isBlank()) {
                              if (cmd.startsWith("/")) {
                                 cmd = cmd.substring(1);
                              }

                              String run = cmd.trim();
                              String[] parts = run.split(" +");
                              String label = parts[0].toLowerCase(Locale.ROOT);
                              String[] args = parts.length > 1 ? (String[])Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
                              p.closeInventory();
                              Tasks.run(this.plugin, () -> {
                                 try {
                                    boolean ok = false;

                                    try {
                                       ok = this.plugin.commands().dispatch(p, label, args);
                                    } catch (Exception var11) {
                                    }

                                    if (!ok) {
                                       try {
                                          ok = p.performCommand(run);
                                       } catch (Exception var10) {
                                       }
                                    }

                                    if (!ok) {
                                       this.toggleLocal(p, key);
                                    }

                                    Map<String, Boolean> stored = (Map)this.playerSettings.get(p.getUniqueId());
                                    if (stored != null) {
                                       stored.remove(key);
                                    }

                                    boolean state = this.isEnabled(p, key, s.getBoolean("default", true));
                                    this.persist(p, key, state);
                                    Tasks.runLater(this.plugin, () -> {
                                       if (p.isOnline()) {
                                          this.openGui(p);
                                       }

                                    }, 1L);
                                    Tasks.runLater(this.plugin, () -> {
                                       if (p.isOnline()) {
                                          this.openGui(p);
                                       }

                                    }, 3L);
                                 } catch (Exception var12) {
                                    Tasks.runLater(this.plugin, () -> {
                                       if (p.isOnline()) {
                                          this.openGui(p);
                                       }

                                    }, 2L);
                                 }

                              });
                              break;
                           }

                           this.toggleLocal(p, key);
                           this.persist(p, key, this.isEnabled(p, key, s.getBoolean("default", true)));
                           Tasks.run(this.plugin, () -> {
                              if (p.isOnline()) {
                                 this.openGui(p);
                              }

                           });
                           break;
                        }
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = false
   )
   public void onDrag(InventoryDragEvent e) {
      if (this.isSettingsInv(e.getView().getTopInventory()) || e.getInventory().getHolder() instanceof SettingsHolder) {
         e.setCancelled(true);
         e.setResult(Result.DENY);
      }

   }

   private void toggleLocal(Player p, String key) {
      switch (key) {
         case "general_chat":
         case "chat_toggle":
         case "chat":
         case "public_chat":
            this.toggleChat(p, new String[0]);
            break;
         case "chainmail":
         case "chainmail_kit":
         case "chainmail_on_respawn":
            this.toggleChainmail(p, new String[0]);
            break;
         case "player_visibility":
         case "visibility":
            this.toggleVisibility(p, new String[0]);
            break;
         case "mob_disable":
         case "disable_mob_spawns":
            this.toggleMobs(p, new String[0]);
            break;
         case "after_duel_songs":
            this.toggleDuelSongs(p, new String[0]);
            break;
         case "duels_requests":
            this.toggleDuelRequests(p, new String[0]);
            break;
         case "team_chat":
            this.toggleTeamChat(p, new String[0]);
            break;
         case "tpa_confirm_menu":
            this.toggleTpaConfirm(p, new String[0]);
            break;
         case "sound_notifications":
         case "sounds":
            this.toggleSounds(p, new String[0]);
            break;
         case "worth_display":
            this.toggleWorth(p, new String[0]);
            break;
         case "scoreboard":
            this.toggleScoreboardProxy(p, new String[0]);
            break;
         case "fast_crystals":
            this.toggleCrystal(p, new String[0]);
            break;
         default:
            TextUtil.send(p, "&7Toggled &f" + key);
      }

   }

   private boolean flip(Set<UUID> set, UUID id) {
      if (set.remove(id)) {
         return true;
      } else {
         set.add(id);
         return false;
      }
   }

   private boolean toggleSounds(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         boolean on = this.soundsOff.remove(p.getUniqueId());
         if (on) {
            TextUtil.send(p, "&#00FF00Sounds ON.");
         } else {
            this.soundsOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Sounds OFF.");
         }

         this.persist(p, "sound_notifications", on);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleCrystal(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (args.length > 0 && args[0].equalsIgnoreCase("on")) {
            this.fastCrystal.add(p.getUniqueId());
            TextUtil.send(p, "&#00FF00Fast crystal ON.");
            return true;
         } else if (args.length > 0 && args[0].equalsIgnoreCase("off")) {
            this.fastCrystal.remove(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Fast crystal OFF.");
            return true;
         } else {
            boolean on = !this.fastCrystal.contains(p.getUniqueId());
            if (on) {
               this.fastCrystal.add(p.getUniqueId());
               TextUtil.send(p, "&#00FF00Fast crystal ON.");
            } else {
               this.fastCrystal.remove(p.getUniqueId());
               TextUtil.send(p, "&#FF0000Fast crystal OFF.");
            }

            this.persist(p, "fast_crystals", on);
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean toggleTeamChat(CommandSender sender, String[] args) {
      if (!(sender instanceof Player p)) {
         return true;
      } else {
         boolean nowOn;
         if (!this.teamChat.contains(p.getUniqueId()) && !this.teamChatPref.contains(p.getUniqueId())) {
            this.teamChat.add(p.getUniqueId());
            this.teamChatPref.add(p.getUniqueId());
            nowOn = true;
            TextUtil.send(p, "&#00FF00Team chat ON.");
         } else {
            this.teamChat.remove(p.getUniqueId());
            this.teamChatPref.remove(p.getUniqueId());
            nowOn = false;
            TextUtil.send(p, "&#FF0000Team chat OFF.");
         }

         try {
            AstralTeams teams = AstralTeams.get();
            if (teams != null && teams.getTeams() != null) {
               teams.getTeams().setChatToggled(p.getUniqueId(), nowOn);
            }
         } catch (Exception var6) {
         }

         this.persist(p, "team_chat", nowOn);
         return true;
      }
   }

   private boolean toggleTeamInvites(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         boolean on = this.teamInvitesOff.remove(p.getUniqueId());
         if (on) {
            TextUtil.send(p, "&#00FF00Team invites ON.");
         } else {
            this.teamInvitesOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Team invites OFF.");
         }

         this.persist(p, "team_invites", on);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleOrders(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         boolean on;
         if (this.orderNotifOff.remove(id)) {
            on = true;
            TextUtil.send(p, "&#00FF00Order notifications ON.");
         } else {
            this.orderNotifOff.add(id);
            on = false;
            TextUtil.send(p, "&#FF0000Order notifications OFF.");
         }

         this.persist(p, "order_notifications", on);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleWorth(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         boolean on;
         try {
            AstralSell sell = AstralSell.get();
            if (sell != null && sell.getWorthLore() != null) {
               on = sell.getWorthLore().toggleWorth(p);
            } else {
               on = this.worthOff.contains(p.getUniqueId());
               if (on) {
                  this.worthOff.remove(p.getUniqueId());
               } else {
                  this.worthOff.add(p.getUniqueId());
               }

               on = !this.worthOff.contains(p.getUniqueId());
            }
         } catch (Exception var6) {
            if (this.worthOff.remove(p.getUniqueId())) {
               on = true;
            } else {
               this.worthOff.add(p.getUniqueId());
               on = false;
            }
         }

         if (on) {
            this.worthOff.remove(p.getUniqueId());
            TextUtil.send(p, "&#00FF00Worth display ON.");
         } else {
            this.worthOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Worth display OFF.");
         }

         this.persist(p, "worth_display", on);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleChat(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.chatOff.contains(p.getUniqueId())) {
            this.chatOff.remove(p.getUniqueId());
            TextUtil.send(p, "&#00FF00General chat enabled.");
         } else {
            this.chatOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000General chat disabled.");
         }

         this.persist(p, "public_chat", this.isEnabled(p, "public_chat", this.cfg.getBoolean("settings-gui.items.public_chat.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleChainmail(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.chainmailOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Chainmail kit enabled.");
         } else {
            this.chainmailOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Chainmail kit disabled.");
         }

         this.persist(p, "chainmail_on_respawn", this.isEnabled(p, "chainmail_on_respawn", this.cfg.getBoolean("settings-gui.items.chainmail_on_respawn.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleServerChat(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.serverChatOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Server chat messages ON.");
         } else {
            this.serverChatOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Server chat messages OFF.");
         }

         this.persist(p, "chat_server_messages", this.isEnabled(p, "chat_server_messages", this.cfg.getBoolean("settings-gui.items.chat_server_messages.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleHotbar(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.hotbarOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Hotbar server messages ON.");
         } else {
            this.hotbarOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Hotbar server messages OFF.");
         }

         this.persist(p, "hotbar_server_messages", this.isEnabled(p, "hotbar_server_messages", this.cfg.getBoolean("settings-gui.items.hotbar_server_messages.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleBountyAlerts(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.bountyAlertOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Bounty alerts ON.");
         } else {
            this.bountyAlertOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Bounty alerts OFF.");
         }

         this.persist(p, "bounty_alerts", this.isEnabled(p, "bounty_alerts", this.cfg.getBoolean("settings-gui.items.bounty_alerts.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleAuctionAlerts(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.auctionAlertOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Auction alerts ON.");
         } else {
            this.auctionAlertOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Auction alerts OFF.");
         }

         this.persist(p, "auction_alerts", this.isEnabled(p, "auction_alerts", this.cfg.getBoolean("settings-gui.items.auction_alerts.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleExplosionSounds(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.explosionSoundsOff.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#00FF00Explosion sounds ON.");
         } else {
            this.explosionSoundsOff.add(p.getUniqueId());
            TextUtil.send(p, "&#FF0000Explosion sounds OFF.");
         }

         this.persist(p, "explosion_sounds", this.isEnabled(p, "explosion_sounds", this.cfg.getBoolean("settings-gui.items.explosion_sounds.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleQuickBuy(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (this.quickBuy.remove(p.getUniqueId())) {
            TextUtil.send(p, "&#FF0000Quick auction buy OFF.");

            try {
               MenuSession.FAST_BUY.put(p.getUniqueId(), false);
            } catch (Exception var6) {
            }
         } else {
            this.quickBuy.add(p.getUniqueId());
            TextUtil.send(p, "&#00FF00Quick auction buy ON.");

            try {
               MenuSession.FAST_BUY.put(p.getUniqueId(), true);
            } catch (Exception var5) {
            }
         }

         this.persist(p, "quick_auction_buy", this.isEnabled(p, "quick_auction_buy", this.cfg.getBoolean("settings-gui.items.quick_auction_buy.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleVisibility(CommandSender sender, String[] args) {
      if (!(sender instanceof Player p)) {
         return true;
      } else {
         UUID id = p.getUniqueId();
         if (this.visibilityOff.contains(id)) {
            this.visibilityOff.remove(id);

            for(Player other : Bukkit.getOnlinePlayers()) {
               if (!other.equals(p)) {
                  p.showPlayer(this.plugin, other);
               }
            }

            TextUtil.send(p, "&#00FF00Player visibility ON.");
         } else {
            this.visibilityOff.add(id);

            for(Player other : Bukkit.getOnlinePlayers()) {
               if (!other.equals(p)) {
                  p.hidePlayer(this.plugin, other);
               }
            }

            TextUtil.send(p, "&#FF0000Player visibility OFF (others hidden).");
         }

         this.persist(p, "player_visibility", this.isEnabled(p, "player_visibility", this.cfg.getBoolean("settings-gui.items.player_visibility.default", true)));
         return true;
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent e) {
      Player joined = e.getPlayer();
      Tasks.run(this.plugin, () -> {
         if (this.visibilityOff.contains(joined.getUniqueId())) {
            for(Player other : Bukkit.getOnlinePlayers()) {
               if (!other.equals(joined)) {
                  joined.hidePlayer(this.plugin, other);
               }
            }
         }

         for(Player other : Bukkit.getOnlinePlayers()) {
            if (this.visibilityOff.contains(other.getUniqueId()) && !other.equals(joined)) {
               other.hidePlayer(this.plugin, joined);
            }
         }

      });
   }

   public boolean isWorthOff(UUID id) {
      return this.worthOff.contains(id);
   }

   public boolean isFastCrystal(UUID id) {
      return this.fastCrystal.contains(id);
   }

   public boolean isSoundsOff(UUID id) {
      return this.soundsOff.contains(id);
   }

   public boolean isChatOff(UUID id) {
      return this.chatOff.contains(id);
   }

   public boolean isChainmailOff(UUID id) {
      return this.chainmailOff.contains(id);
   }

   public boolean isServerChatOff(UUID id) {
      return this.serverChatOff.contains(id);
   }

   public boolean isHotbarOff(UUID id) {
      return this.hotbarOff.contains(id);
   }

   public boolean isBountyAlertOff(UUID id) {
      return this.bountyAlertOff.contains(id);
   }

   public boolean isAuctionAlertOff(UUID id) {
      return this.auctionAlertOff.contains(id);
   }

   public boolean isPaymentsOff(UUID id) {
      return this.paymentsOff.contains(id);
   }

   public boolean isVisibilityOff(UUID id) {
      return this.visibilityOff.contains(id);
   }

   public boolean isTeamChat(UUID id) {
      return this.teamChat.contains(id);
   }

   public boolean isTeamInvitesOff(UUID id) {
      return this.teamInvitesOff.contains(id);
   }

   public boolean isOrderNotifOff(UUID id) {
      return this.orderNotifOff.contains(id);
   }

   public boolean toggleChainmailKit(UUID id) {
      if (this.chainmailOff.contains(id)) {
         this.chainmailOff.remove(id);
         return true;
      } else {
         this.chainmailOff.add(id);
         return false;
      }
   }

   public boolean toggleChatOff(UUID id) {
      if (this.chatOff.contains(id)) {
         this.chatOff.remove(id);
         return true;
      } else {
         this.chatOff.add(id);
         return false;
      }
   }

   public static SettingsModule get() {
      return instance;
   }

   public static boolean isAuctionAlertDisabled(UUID id) {
      try {
         return instance != null && instance.isAuctionAlertOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isBountyAlertDisabled(UUID id) {
      try {
         return instance != null && instance.isBountyAlertOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isOrderNotifDisabled(UUID id) {
      try {
         return instance != null && instance.isOrderNotifOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isServerChatDisabled(UUID id) {
      try {
         return instance != null && instance.isServerChatOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isHotbarDisabled(UUID id) {
      try {
         return instance != null && instance.isHotbarOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isSoundsDisabled(UUID id) {
      try {
         return instance != null && instance.isSoundsOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isExplosionSoundsDisabled(UUID id) {
      try {
         return instance != null && instance.isExplosionSoundsOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isDuelRequestsDisabled(UUID id) {
      try {
         return instance != null && instance.isDuelRequestsOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isDuelSongsDisabled(UUID id) {
      try {
         return instance != null && instance.isDuelSongsOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isTeamInvitesDisabled(UUID id) {
      try {
         return instance != null && instance.isTeamInvitesOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   public static boolean isWorthDisabled(UUID id) {
      try {
         return instance != null && instance.isWorthOff(id);
      } catch (Exception var2) {
         return false;
      }
   }

   private boolean toggleMobs(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();

         boolean enabled;
         try {
            AstralMobs mobs = AstralMobs.getInstance();
            if (mobs != null && mobs.getPlayerDataManager() != null) {
               boolean currentlyOn = mobs.getPlayerDataManager().isMobSpawningEnabled(id);
               enabled = !currentlyOn;
               mobs.getPlayerDataManager().setMobSpawning(id, enabled);
            } else {
               enabled = this.mobsOff.contains(id);
               if (enabled) {
                  this.mobsOff.remove(id);
               } else {
                  this.mobsOff.add(id);
               }
            }
         } catch (Exception var8) {
            if (this.mobsOff.remove(id)) {
               enabled = true;
            } else {
               this.mobsOff.add(id);
               enabled = false;
            }
         }

         if (enabled) {
            this.mobsOff.remove(id);
            TextUtil.send(p, "&#00FF00Mob spawning near you ON.");
         } else {
            this.mobsOff.add(id);
            TextUtil.send(p, "&#FF0000Mob spawning near you OFF.");
         }

         this.persist(p, "mob_disable", enabled);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleTpaConfirm(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         try {
            AstralTPA tpa = AstralTPA.get();
            if (tpa != null && tpa.getTpa() != null) {
               boolean on = tpa.getTpa().toggleConfirmGui(p.getUniqueId());
               TextUtil.send(p, on ? "&#00FF00TPA confirm menu ON." : "&#FF0000TPA confirm menu OFF.");
               return true;
            }
         } catch (Exception var6) {
         }

         TextUtil.send(p, "&cTPA system unavailable.");
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleDuelSongs(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         if (this.duelSongsOff.remove(id)) {
            TextUtil.send(p, "&#00FF00After duel songs ON.");
         } else {
            this.duelSongsOff.add(id);
            TextUtil.send(p, "&#FF0000After duel songs OFF.");
         }

         this.persist(p, "after_duel_songs", this.isEnabled(p, "after_duel_songs", this.cfg.getBoolean("settings-gui.items.after_duel_songs.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleDuelRequests(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         if (this.duelRequestsOff.remove(id)) {
            TextUtil.send(p, "&#00FF00Duel requests ON.");
         } else {
            this.duelRequestsOff.add(id);
            TextUtil.send(p, "&#FF0000Duel requests OFF.");
         }

         this.persist(p, "duels_requests", this.isEnabled(p, "duels_requests", this.cfg.getBoolean("settings-gui.items.duels_requests.default", true)));
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleGlobalChat(CommandSender sender, String[] args) {
      return this.toggleChat(sender, args);
   }

   private boolean toggleMsgProxy(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         try {
            Module mod = this.plugin.modules().get("msg");
            if (mod instanceof MsgModule mm) {
               mm.togglePlayer(p);
               return true;
            }
         } catch (Exception var6) {
         }

         return true;
      } else {
         return true;
      }
   }

   private boolean togglePayAlert(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         boolean on;
         if (this.paymentsOff.remove(id)) {
            on = true;
            TextUtil.send(p, "&#00FF00Pay alerts ON.");
         } else {
            this.paymentsOff.add(id);
            on = false;
            TextUtil.send(p, "&#FF0000Pay alerts OFF.");
         }

         this.persist(p, "pay_alerts", on);
         return true;
      } else {
         return true;
      }
   }

   private boolean toggleScoreboardProxy(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         UUID id = p.getUniqueId();
         boolean currentlyOff = this.scoreboardOff.contains(id);

         try {
            Module mod = this.plugin.modules().get("scoreboard");
            if (mod instanceof ScoreboardModule sm) {
               currentlyOff = sm.isHidden(id) || currentlyOff;
            }
         } catch (Exception var10) {
         }

         if (currentlyOff) {
            this.scoreboardOff.remove(id);

            try {
               Module mod = this.plugin.modules().get("scoreboard");
               if (mod instanceof ScoreboardModule) {
                  ScoreboardModule sm = (ScoreboardModule)mod;
                  sm.setHidden(id, false);
               }
            } catch (Exception var9) {
            }

            TextUtil.send(p, "&#00FF00Scoreboard ON.");
            this.persist(p, "scoreboard", true);
         } else {
            this.scoreboardOff.add(id);

            try {
               Module mod = this.plugin.modules().get("scoreboard");
               if (mod instanceof ScoreboardModule) {
                  ScoreboardModule sm = (ScoreboardModule)mod;
                  sm.setHidden(id, true);
               } else {
                  p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
               }
            } catch (Exception var8) {
               p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }

            TextUtil.send(p, "&#FF0000Scoreboard OFF.");
            this.persist(p, "scoreboard", false);
         }

         return true;
      } else {
         return true;
      }
   }

   public boolean isDuelSongsOff(UUID id) {
      return this.duelSongsOff.contains(id);
   }

   public boolean isDuelRequestsOff(UUID id) {
      return this.duelRequestsOff.contains(id);
   }

   public boolean isMobsOff(UUID id) {
      return this.mobsOff.contains(id);
   }

   public void setScoreboardEnabled(UUID id, boolean enabled) {
      if (enabled) {
         this.scoreboardOff.remove(id);
      } else {
         this.scoreboardOff.add(id);
      }

   }

   public boolean isScoreboardOff(UUID id) {
      return this.scoreboardOff.contains(id);
   }

   public boolean isExplosionSoundsOff(UUID id) {
      return this.explosionSoundsOff.contains(id);
   }

   private boolean toggleKey(CommandSender sender, String[] args, String key, String label) {
      if (sender instanceof Player p) {
         boolean cur = this.isEnabled(p, key, true);
         this.toggleLocal(p, key);
         TextUtil.send(p, (cur ? "&#FF0000" : "&#00FF00") + label + (cur ? " OFF" : " ON") + ".");
         return true;
      } else {
         return true;
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onMobSpawn(CreatureSpawnEvent e) {
      if (e.getEntityType().isAlive()) {
         Location loc = e.getLocation();
         if (loc.getWorld() != null) {
            for(Player p : loc.getWorld().getPlayers()) {
               if (this.mobsOff.contains(p.getUniqueId()) && p.getLocation().distanceSquared(loc) <= (double)2304.0F) {
                  e.setCancelled(true);
                  return;
               }
            }

         }
      }
   }

   public static class SettingsHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
