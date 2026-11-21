package me.xyr0.astralsmpcore.moderation;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.StreamSupport;
import me.xyr0.astralsmpcore.AstralSMPcore;
import me.xyr0.astralsmpcore.CommandRegistry;
import me.xyr0.astralsmpcore.Module;
import me.xyr0.astralsmpcore.economy.CoreEconomy;
import me.xyr0.astralsmpcore.moderation.gui.SusMenus;
import me.xyr0.astralsmpcore.moderation.listener.CommandLogListener;
import me.xyr0.astralsmpcore.moderation.listener.SusGuiListener;
import me.xyr0.astralsmpcore.moderation.listener.SusListener;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import me.xyr0.astralsmpcore.util.TextUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class ModerationModule implements Module, Listener {
   private final AstralSMPcore plugin;
   private FileConfiguration cfg;
   private final Set<UUID> vanished = new HashSet();
   private final Set<UUID> staffChatToggle = new HashSet();
   private final List<Location> activeStashes = new ArrayList();
   private SusManager susManager;
   private SusListener susListener;
   private final Map<UUID, UUID> pendingWipe = new ConcurrentHashMap();
   private final Map<UUID, Integer> killConfirm = new ConcurrentHashMap();
   private static final String[] RANDOM_FAKE_NAMES = new String[]{"Steve_Miner", "AlexCraft", "NotchJr", "HerobrineX", "CreeperBob", "EnderKnight", "BlockyLuke", "CraftQueen", "RedstoneRex", "DiamondDan", "SkyWalkerMC", "LavaFish", "PiglinPete", "AxolotlAmy", "WardenWill", "SculkSam", "NetherNate", "Froglight", "AmethystAva", "CopperCarl"};

   public ModerationModule(AstralSMPcore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "moderation";
   }

   public void onEnable() {
      File f = new File(this.plugin.getDataFolder(), "moderation.yml");
      if (!f.exists()) {
         try {
            this.plugin.saveResource("moderation.yml", false);
         } catch (Exception var3) {
         }
      }

      this.cfg = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new CommandLogListener(this), this.plugin);
      this.susManager = new SusManager();
      this.loadSusConfig();
      if (this.cfg.getBoolean("sus.enabled", true)) {
         this.susListener = new SusListener(this.plugin, this.susManager);
         this.plugin.getServer().getPluginManager().registerEvents(this.susListener, this.plugin);
         this.plugin.getServer().getPluginManager().registerEvents(new SusGuiListener(this), this.plugin);
      }

      CommandRegistry reg = this.plugin.commands();
      reg.registerHandler("gmc", this::gmc);
      reg.registerHandler("gms", this::gms);
      reg.registerHandler("gmsp", this::gmsp);
      reg.registerHandler("gtp", this::gtp);
      reg.registerHandler("gtph", this::gtph);
      reg.registerHandler("vanish", this::vanish);
      reg.registerHandler("v", this::vanish);
      reg.registerHandler("findplayer", this::findPlayer);
      reg.registerHandler("fp", this::findPlayer);
      reg.registerHandler("ping", this::ping);
      reg.registerHandler("staffchat", this::staffChat);
      reg.registerHandler("st", this::staffChat);
      reg.registerHandler("wipe", this::wipe);
      reg.registerHandler("invsee", this::invsee);
      reg.registerHandler("killconfirm", this::killConfirm);
      reg.registerHandler("kill", this::killConfirm);
      reg.registerHandler("die", this::killConfirm);
      reg.registerHandler("spawnfakeplayer", this::spawnFakePlayer);
      reg.registerHandler("spawnfakestash", this::spawnFakeStash);
      reg.registerHandler("give", this::give);
      reg.registerHandler("enchant", this::enchant);
      reg.registerHandler("clear", this::clear);
      reg.registerHandler("effect", this::effect);
      reg.registerHandler("eco", this::eco);
      reg.registerTabCompleter("eco", this::tabEco);
      reg.registerHandler("broadcast", this::broadcast);
      reg.registerHandler("bc", this::broadcast);
      reg.registerHandler("sus", this::sus);
      reg.registerTabCompleter("give", this::tabGive);
      reg.registerTabCompleter("enchant", this::tabEnchant);
      reg.registerTabCompleter("clear", this::tabClear);
   }

   public void onDisable() {
      for(UUID id : new HashSet(this.vanished)) {
         Player p = Bukkit.getPlayer(id);
         if (p != null) {
            this.unvanish(p, false);
         }
      }

   }

   public void reload() {
      File f = new File(this.plugin.getDataFolder(), "moderation.yml");
      if (f.exists()) {
         this.cfg = YamlConfiguration.loadConfiguration(f);
      }

   }

   private void feedback(Player p, String msg) {
      TextUtil.send(p, msg);
      TextUtil.actionBar(p, msg);

      try {
         SoundUtil.play(p, p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.2F);
      } catch (Exception var4) {
      }

   }

   private boolean gmc(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.use") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else {
            p.setGameMode(GameMode.CREATIVE);
            this.feedback(p, this.cfg.getString("messages.gmc", "&7Gamemode changed to &#37BFF9Creative"));
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean gms(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.use") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else {
            p.setGameMode(GameMode.SURVIVAL);
            this.feedback(p, this.cfg.getString("messages.gms", "&7Gamemode changed to &#37BFF9Survival"));
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean gmsp(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.use") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else {
            p.setGameMode(GameMode.SPECTATOR);
            this.feedback(p, this.cfg.getString("messages.gmsp", "&7Gamemode changed to &#37BFF9Spectator"));
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean gtp(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.use") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else if (a.length < 1) {
            TextUtil.send(p, "&cUsage: /gtp <player>");
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(a[0]);
            if (t == null) {
               TextUtil.send(p, this.cfg.getString("messages.player-not-found", "&cPlayer not found."));
               return true;
            } else {
               p.teleport(t.getLocation());
               this.feedback(p, this.cfg.getString("messages.gtp", "&7Teleported to &#37BFF9%player%").replace("%player%", t.getName()));
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private boolean gtph(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.use") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else if (a.length < 1) {
            TextUtil.send(p, "&cUsage: /gtph <player>");
            return true;
         } else {
            Player t = Bukkit.getPlayerExact(a[0]);
            if (t == null) {
               TextUtil.send(p, this.cfg.getString("messages.player-not-found", "&cPlayer not found."));
               return true;
            } else {
               t.teleport(p.getLocation());
               this.feedback(p, this.cfg.getString("messages.gtph", "&7Teleported &#37BFF9%player% &7to you").replace("%player%", t.getName()));
               return true;
            }
         }
      } else {
         return true;
      }
   }

   private boolean vanish(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.vanish") && !p.isOp()) {
            TextUtil.send(p, this.cfg.getString("messages.no-permission", "&cNo permission."));
            return true;
         } else {
            if (this.vanished.contains(p.getUniqueId())) {
               this.unvanish(p, true);
            } else {
               this.doVanish(p);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void doVanish(Player p) {
      this.vanished.add(p.getUniqueId());

      for(Player other : Bukkit.getOnlinePlayers()) {
         if (!other.equals(p) && !other.hasPermission("astralsmpcore.system.moderation.vanish")) {
            other.hidePlayer(this.plugin, p);
         }
      }

      String leave = this.cfg.getString("messages.fake-leave", "&#37BFF9%player% &7left the server").replace("%player%", p.getName());

      for(Player other : Bukkit.getOnlinePlayers()) {
         if (!other.equals(p)) {
            TextUtil.send(other, leave);
         }
      }

      this.feedback(p, this.cfg.getString("messages.vanish-on", "&7You are now &#37BFF9vanished"));
   }

   private void unvanish(Player p, boolean announce) {
      this.vanished.remove(p.getUniqueId());

      for(Player other : Bukkit.getOnlinePlayers()) {
         other.showPlayer(this.plugin, p);
      }

      if (announce) {
         String join = this.cfg.getString("messages.fake-join", "&#37BFF9%player% &7joined the server").replace("%player%", p.getName());

         for(Player other : Bukkit.getOnlinePlayers()) {
            if (!other.equals(p)) {
               TextUtil.send(other, join);
            }
         }

         this.feedback(p, this.cfg.getString("messages.vanish-off", "&7You are no longer &#37BFF9vanished"));
      }

   }

   private boolean findPlayer(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         Player target = p;
         if (a.length > 0) {
            target = Bukkit.getPlayerExact(a[0]);
            if (target == null) {
               TextUtil.send(p, this.cfg.getString("messages.player-not-found", "&cPlayer not found."));
               return true;
            }
         }

         String world = target.getWorld() != null ? target.getWorld().getName() : "unknown";
         String msg;
         if (target.equals(p)) {
            msg = this.cfg.getString("messages.find-self", "&7Your world is &#37BFF9%world%").replace("%world%", world);
         } else {
            msg = this.cfg.getString("messages.find-other", "&7Player &#37BFF9%player% &7world is &#37BFF9%world%").replace("%player%", target.getName()).replace("%world%", world);
         }

         this.feedback(p, msg);
         return true;
      } else {
         return true;
      }
   }

   private boolean ping(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         Player target = p;
         if (a.length > 0) {
            target = Bukkit.getPlayerExact(a[0]);
            if (target == null) {
               TextUtil.send(p, this.cfg.getString("messages.player-not-found", "&cPlayer not found."));
               return true;
            }
         }

         int ping = target.getPing();
         String msg;
         if (target.equals(p)) {
            msg = this.cfg.getString("messages.ping-self", "&7Your ping is &#37BFF9%ping%ms").replace("%ping%", String.valueOf(ping));
         } else {
            msg = this.cfg.getString("messages.ping-other", "&7Player's ping is &#37BFF9%ping%ms").replace("%ping%", String.valueOf(ping)).replace("%player%", target.getName());
         }

         this.feedback(p, msg);
         return true;
      } else {
         return true;
      }
   }

   public boolean isVanished(UUID id) {
      return this.vanished.contains(id);
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onJoin(PlayerJoinEvent e) {
      Player joined = e.getPlayer();

      for(UUID id : this.vanished) {
         Player v = Bukkit.getPlayer(id);
         if (v != null && !joined.hasPermission("astralsmpcore.system.moderation.vanish")) {
            joined.hidePlayer(this.plugin, v);
         }
      }

      if (this.vanished.contains(joined.getUniqueId())) {
         e.setJoinMessage((String)null);
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent e) {
      if (this.vanished.contains(e.getPlayer().getUniqueId())) {
         e.setQuitMessage((String)null);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onChat(AsyncPlayerChatEvent e) {
      if (this.vanished.contains(e.getPlayer().getUniqueId())) {
         e.setCancelled(true);
         TextUtil.send(e.getPlayer(), this.cfg.getString("messages.vanish-chat-blocked", "&cYou cannot chat while vanished."));
      }

   }

   private boolean staffChat(CommandSender s, String[] a) {
      if (!(s instanceof Player p)) {
         return true;
      } else if (!p.hasPermission("astralsmpcore.system.staffchat.use") && !p.isOp()) {
         TextUtil.send(p, "&cNo permission.");
         return true;
      } else if (a.length == 0) {
         if (this.staffChatToggle.remove(p.getUniqueId())) {
            TextUtil.send(p, "&7Staff chat &cdisabled");
         } else {
            this.staffChatToggle.add(p.getUniqueId());
            TextUtil.send(p, "&7Staff chat &#37BFF9enabled");
         }

         return true;
      } else {
         String msg = String.join(" ", a);
         String format = this.cfg.getString("staffchat.format", "&8[&cSC&8] &#37BFF9%player%&7: &f%message%").replace("%player%", p.getName()).replace("%message%", msg);

         for(Player online : Bukkit.getOnlinePlayers()) {
            if (online.hasPermission("astralsmpcore.system.staffchat.use") || online.isOp()) {
               TextUtil.send(online, format);
            }
         }

         return true;
      }
   }

   private boolean wipe(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.wipe") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission.");
            return true;
         } else if (a.length < 1) {
            TextUtil.send(p, "&cUsage: /wipe <player>");
            return true;
         } else {
            OfflinePlayer target = Bukkit.getOfflinePlayer(a[0]);
            this.openWipeGui(p, target);
            return true;
         }
      } else {
         return true;
      }
   }

   private void openWipeGui(Player viewer, OfflinePlayer target) {
      String name = target.getName() == null ? "?" : target.getName();
      WipeHolder holder = new WipeHolder(target.getUniqueId());
      Inventory inv = Bukkit.createInventory(holder, 45, TextUtil.component("&8ᴡɪᴘᴇ &7" + name));

      for(int i = 0; i < 45; ++i) {
         inv.setItem(i, this.named(Material.BLACK_STAINED_GLASS_PANE, " "));
      }

      inv.setItem(4, this.namedLore(Material.WITHER_SKELETON_SKULL, "&#e00202ᴘᴇʀᴍᴀɴᴇɴᴛ ᴡɪᴘᴇ", "&7This action &ccannot be undone&f."));
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta sm = (SkullMeta)head.getItemMeta();
      if (sm != null) {
         sm.setOwningPlayer(target);
         sm.displayName(TextUtil.component("#00f986" + name));
         sm.lore(List.of(TextUtil.component("&7UUID"), TextUtil.component("&8" + String.valueOf(target.getUniqueId()))));
         head.setItemMeta(sm);
      }

      inv.setItem(13, head);
      inv.setItem(10, this.namedLore(Material.GOLD_INGOT, "#00f986ᴇᴄᴏɴᴏᴍʏ", "&7Balance data"));
      inv.setItem(16, this.namedLore(Material.IRON_HELMET, "#00f986ᴛᴇᴀᴍ", "&7Team data"));
      inv.setItem(19, this.namedLore(Material.RED_BED, "#00f986ʜᴏᴍᴇꜱ", "&7Homes data"));
      inv.setItem(22, this.namedLore(Material.CLOCK, "#00f986ᴀᴄᴛɪᴠɪᴛʏ", "&7Playtime / blocks"));
      inv.setItem(25, this.namedLore(Material.DIAMOND_SWORD, "#00f986ꜱᴛᴀᴛꜱ", "&7Kills / deaths"));
      inv.setItem(31, this.namedLore(Material.CHEST, "#00f986ᴏᴛʜᴇʀ", "&7Keys / settings / reports"));
      inv.setItem(38, this.namedLore(Material.LIME_STAINED_GLASS_PANE, "&#02de4fᴄᴀɴᴄᴇʟ", "&fClick to abort wipe"));
      inv.setItem(42, this.namedLore(Material.RED_STAINED_GLASS_PANE, "&#e00202ᴄᴏɴꜰɪʀᴍ ᴡɪᴘᴇ", "&fDeletes everything for &#e00202" + name));
      viewer.openInventory(inv);
      this.pendingWipe.put(viewer.getUniqueId(), target.getUniqueId());
   }

   private ItemStack named(Material mat, String name) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(TextUtil.component(name));
         item.setItemMeta(meta);
      }

      return item;
   }

   private boolean killConfirm(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         this.openKillGui(p);
         return true;
      } else {
         return true;
      }
   }

   private void openKillGui(Player p) {
      Inventory inv = Bukkit.createInventory(new KillHolder(), 27, TextUtil.component("Confirm Kill"));
      inv.setItem(11, this.namedLore(Material.RED_STAINED_GLASS_PANE, "&fCancel", "&7Click to cancel"));
      inv.setItem(13, this.namedLore(Material.SKELETON_SKULL, "#fc0000Confirm Death", "&fAre you certain you're ready to die?"));
      inv.setItem(15, this.namedLore(Material.LIME_STAINED_GLASS_PANE, "&fConfirm", "&7Click to confirm"));
      p.openInventory(inv);
      this.killConfirm.put(p.getUniqueId(), 0);
   }

   private ItemStack namedLore(Material mat, String name, String lore) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(TextUtil.component(name));
         meta.lore(List.of(TextUtil.component(lore)));
         item.setItemMeta(meta);
      }

      return item;
   }

   private boolean spawnFakePlayer(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.fakeplayer") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission.");
            return true;
         } else {
            String name;
            if (a != null && a.length > 0 && !a[0].isBlank()) {
               name = a[0].replaceAll("[^A-Za-z0-9_-]", "");
               if (name.isEmpty()) {
                  name = RANDOM_FAKE_NAMES[ThreadLocalRandom.current().nextInt(RANDOM_FAKE_NAMES.length)];
               }
            } else {
               name = RANDOM_FAKE_NAMES[ThreadLocalRandom.current().nextInt(RANDOM_FAKE_NAMES.length)];
            }

            if (name.length() > 16) {
               name = name.substring(0, 16);
            }

            Vector direction = p.getLocation().getDirection().clone();
            direction.setY(0);
            if (direction.lengthSquared() < 1.0E-4) {
               direction = new Vector(0, 0, 1);
            }

            Location loc = p.getLocation().clone().add(direction.normalize().multiply((double)2.0F));
            loc.setYaw(p.getLocation().getYaw() + 180.0F);
            loc.setPitch(0.0F);
            String display = name;

            try {
               Class<?> mannequinClass = Class.forName("org.bukkit.entity.Mannequin");
               LivingEntity spawned = (LivingEntity)loc.getWorld().spawn(loc, mannequinClass, (entity) -> this.configureMannequin(p, entity, display));
               int seconds = this.cfg.getInt("fakeplayer.duration-seconds", 12);
               Tasks.runAtEntityLater(this.plugin, spawned, () -> {
                  if (spawned.isValid()) {
                     spawned.remove();
                  }

               }, 20L * (long)Math.max(1, seconds));
               TextUtil.send(p, "&7Spawned fake player &#37BFF9" + display + " &7for " + seconds + "s");
            } catch (ClassNotFoundException var12) {
               TextUtil.send(p, "&cMannequin entity is not available on this server version.");
               this.plugin.getLogger().warning("spawnfakeplayer requires a Paper build with Mannequin support.");
            } catch (Exception t) {
               TextUtil.send(p, "&cCould not spawn fake player.");
               this.plugin.getLogger().warning("spawnfakeplayer failed: " + t.getMessage());
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void configureMannequin(Player source, LivingEntity entity, String display) {
      try {
         entity.customName(TextUtil.component("&7" + display));
         entity.setCustomNameVisible(true);
      } catch (Exception var15) {
         entity.setCustomName(TextUtil.color("&7" + display));
         entity.setCustomNameVisible(true);
      }

      invoke(entity, "setImmovable", new Class[]{Boolean.TYPE}, true);
      invoke(entity, "setSneaking", new Class[]{Boolean.TYPE}, true);

      try {
         Object pose = Enum.valueOf(Class.forName("org.bukkit.entity.Pose"), "SNEAKING");
         Method m = entity.getClass().getMethod("setPose", Class.forName("org.bukkit.entity.Pose"), Boolean.TYPE);
         m.invoke(entity, pose, true);
      } catch (Exception var14) {
      }

      try {
         Class<?> skinParts = Class.forName("com.destroystokyo.paper.SkinParts");
         Method all = skinParts.getMethod("allParts");
         Object parts = all.invoke((Object)null);
         Method setSkin = entity.getClass().getMethod("setSkinParts", skinParts);
         setSkin.invoke(entity, parts);
      } catch (Exception var13) {
      }

      try {
         Class<?> resolvable = Class.forName("io.papermc.paper.datacomponent.item.ResolvableProfile");
         Method builder = resolvable.getMethod("resolvableProfile");
         Object b = builder.invoke((Object)null);
         Method nameM = b.getClass().getMethod("name", String.class);
         Object named = nameM.invoke(b, display);
         Method build = named.getClass().getMethod("build");
         Object profile = build.invoke(named);
         Method setProfile = entity.getClass().getMethod("setProfile", resolvable);
         setProfile.invoke(entity, profile);
      } catch (Exception var12) {
      }

      entity.setAI(false);
      entity.setCanPickupItems(false);
      entity.setCollidable(false);
      entity.setGravity(false);
      entity.setInvulnerable(true);
      entity.setPersistent(false);
      entity.setRemoveWhenFarAway(false);
      entity.setSilent(true);
      EntityEquipment eq = entity.getEquipment();
      if (eq != null) {
         eq.setHelmet(copyItem(source.getInventory().getHelmet()), true);
         eq.setChestplate(copyItem(source.getInventory().getChestplate()), true);
         eq.setLeggings(copyItem(source.getInventory().getLeggings()), true);
         eq.setBoots(copyItem(source.getInventory().getBoots()), true);
         eq.setItemInMainHand(copyItem(source.getInventory().getItemInMainHand()), true);
         eq.setItemInOffHand(copyItem(source.getInventory().getItemInOffHand()), true);
      }

   }

   private static void invoke(Object target, String method, Class<?>[] types, Object... args) {
      try {
         Method m = target.getClass().getMethod(method, types);
         m.invoke(target, args);
      } catch (Exception var5) {
      }

   }

   private static ItemStack copyItem(ItemStack item) {
      return item != null && !item.getType().isAir() ? item.clone() : null;
   }

   private boolean spawnFakeStash(CommandSender s, String[] a) {
      if (!(s instanceof Player p)) {
         return true;
      } else if (!p.hasPermission("astralsmpcore.system.moderation.fakestash") && !p.isOp()) {
         TextUtil.send(p, "&cNo permission.");
         return true;
      } else {
         Location base = p.getLocation().getBlock().getLocation();
         Iterator<Location> it = this.activeStashes.iterator();

         while(it.hasNext()) {
            Location st = (Location)it.next();
            if (st.getWorld() != null && st.getWorld().equals(base.getWorld()) && st.distanceSquared(base) < (double)64.0F) {
               st.getBlock().setType(Material.AIR);
               st.clone().add((double)1.0F, (double)0.0F, (double)0.0F).getBlock().setType(Material.AIR);
               st.clone().add((double)0.0F, (double)0.0F, (double)1.0F).getBlock().setType(Material.AIR);
               st.clone().add((double)0.0F, (double)1.0F, (double)0.0F).getBlock().setType(Material.AIR);
               it.remove();
            }
         }

         List<String> layouts = this.cfg.getStringList("fakestash.layouts");
         if (layouts == null || layouts.isEmpty()) {
            layouts = List.of("spawner_chest", "spawner_shulker", "spawner_shulker_chest", "spawner_amethyst");
         }

         String layout = (String)layouts.get(ThreadLocalRandom.current().nextInt(layouts.size()));
         Location loc = base.clone().subtract((double)0.0F, (double)1.0F, (double)0.0F);
         loc.getBlock().setType(Material.SPAWNER);
         if (layout.contains("chest")) {
            loc.clone().add((double)1.0F, (double)0.0F, (double)0.0F).getBlock().setType(Material.CHEST);
         }

         if (layout.contains("shulker")) {
            loc.clone().add((double)0.0F, (double)0.0F, (double)1.0F).getBlock().setType(Material.SHULKER_BOX);
         }

         if (layout.contains("amethyst")) {
            loc.clone().add((double)0.0F, (double)1.0F, (double)0.0F).getBlock().setType(Material.AMETHYST_BLOCK);
         }

         this.activeStashes.add(loc.clone());
         TextUtil.send(p, "&7Spawned fake stash (&f" + layout + "&7). Stays until mined or replaced nearby.");
         return true;
      }
   }

   private void performFullWipe(Player admin, UUID targetId) {
      OfflinePlayer op = Bukkit.getOfflinePlayer(targetId);
      String name = op.getName() == null ? targetId.toString() : op.getName();
      Player online = op.getPlayer();

      try {
         CoreEconomy.setBalance(op, (double)0.0F);
      } catch (Exception var18) {
      }

      for(String path : new String[]{"shards/data.yml", "homes/data.yml", "stats/data.yml", "teams/data.yml", "settings/players.yml", "crates/data.yml", "auction/data.yml", "order/data.yml", "orders/data.yml", "bounties/data.yml", "sell/history.yml", "ec/data.yml", "msg/data.yml", "pay/data.yml"}) {
         try {
            File f = new File(this.plugin.getDataFolder(), path);
            if (f.exists()) {
               YamlConfiguration yc = YamlConfiguration.loadConfiguration(f);
               yc.set(targetId.toString(), (Object)null);
               yc.set("players." + String.valueOf(targetId), (Object)null);
               yc.set("player." + String.valueOf(targetId), (Object)null);
               yc.set(name, (Object)null);
               yc.save(f);
            }
         } catch (Exception var17) {
         }
      }

      try {
         Module mod = this.plugin.modules().get("stats");
         if (mod != null) {
         }
      } catch (Exception var16) {
      }

      try {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team admin disband " + name);
      } catch (Exception var15) {
      }

      try {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "teams leave " + name);
      } catch (Exception var14) {
      }

      for(String cmd : new String[]{"ah admin wipe " + name, "orders admin wipe " + name, "crates admin wipe " + name, "bounty admin wipe " + name}) {
         try {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
         } catch (Exception var13) {
         }
      }

      this.wipeStatsFiles(targetId, name);
      if (online != null && online.isOnline()) {
         online.getInventory().clear();
         online.getEnderChest().clear();
         online.getInventory().setArmorContents((ItemStack[])null);
         online.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
         online.setTotalExperience(0);
         online.setLevel(0);
         online.setExp(0.0F);

         try {
            online.setHealth(online.getMaxHealth());
         } catch (Exception var12) {
         }

         online.setFoodLevel(20);
         online.setSaturation(0.0F);
         Tasks.run(this.plugin, () -> {
            try {
               online.kick(LegacyComponentSerializer.legacyAmpersand().deserialize(TextUtil.color("&cYou have been wiped.\n&7Your stats, inventory, and data were reset.")));
            } catch (Exception var4) {
               try {
                  online.kickPlayer("You have been wiped.");
               } catch (Exception var3) {
               }
            }

         });
      }

      TextUtil.send(admin, "&cFully wiped &f" + name + " &c(money, shards, homes, stats, teams, keys, inv, echest, auctions/orders best-effort) and kicked if online.");
   }

   private boolean invsee(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.invsee") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission.");
            return true;
         } else if (a != null && a.length >= 1) {
            Player target = Bukkit.getPlayerExact(a[0]);
            if (target == null) {
               target = Bukkit.getPlayer(a[0]);
            }

            if (target != null && target.isOnline()) {
               p.openInventory(target.getInventory());
               TextUtil.send(p, "&7Viewing inventory of &#37BFF9" + target.getName());
               return true;
            } else {
               TextUtil.send(p, "&cPlayer must be online.");
               return true;
            }
         } else {
            TextUtil.send(p, "&cUsage: /invsee <player>");
            return true;
         }
      } else {
         s.sendMessage("Players only.");
         return true;
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onInvClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         Inventory top = e.getView().getTopInventory();
         if (top.getHolder() instanceof WipeHolder || top.getHolder() instanceof KillHolder) {
            e.setCancelled(true);
            e.setResult(Result.DENY);
         }

         if (top.getHolder() instanceof KillHolder) {
            int slot = e.getRawSlot();
            if (slot == 11) {
               p.closeInventory();
               this.killConfirm.remove(p.getUniqueId());
            } else {
               if (slot == 15 || slot == 13) {
                  int stage = (Integer)this.killConfirm.getOrDefault(p.getUniqueId(), 0);
                  if (stage < 1) {
                     this.killConfirm.put(p.getUniqueId(), 1);
                     TextUtil.send(p, "&cClick confirm again to die.");
                     return;
                  }

                  p.closeInventory();
                  this.killConfirm.remove(p.getUniqueId());
                  p.setHealth((double)0.0F);
               }

            }
         } else {
            InventoryHolder stage = top.getHolder();
            if (stage instanceof WipeHolder) {
               WipeHolder holder = (WipeHolder)stage;
               int slot = e.getRawSlot();
               if (slot == 38) {
                  p.closeInventory();
                  this.pendingWipe.remove(p.getUniqueId());
                  return;
               }

               if (slot == 42) {
                  UUID targetId = holder.getTarget();
                  this.pendingWipe.remove(p.getUniqueId());
                  p.closeInventory();
                  if (targetId == null) {
                     return;
                  }

                  this.performFullWipe(p, targetId);
               }
            }

         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInvDrag(InventoryDragEvent e) {
      Inventory top = e.getView().getTopInventory();
      if (top.getHolder() instanceof WipeHolder || top.getHolder() instanceof KillHolder) {
         e.setCancelled(true);
      }

   }

   private void wipeStatsFiles(UUID targetId, String name) {
      File data = this.plugin.getDataFolder();
      String[] rels = new String[]{"stats/" + String.valueOf(targetId) + ".yml", "stats/players/" + String.valueOf(targetId) + ".yml", "stats/" + String.valueOf(targetId) + ".yml", "lb/" + String.valueOf(targetId) + ".yml", "lb/" + String.valueOf(targetId) + ".yml", "stats/players.yml"};

      for(String rel : rels) {
         File f = new File(data, rel);
         if (f.isFile()) {
            try {
               YamlConfiguration y = YamlConfiguration.loadConfiguration(f);

               for(String k : new String[]{"kills", "deaths", "playtime", "play-time", "blocks-broken", "blocks-placed", "mobs-killed", "player-kills", "sell_earned", "shop_spent", "shards", "money"}) {
                  if (y.contains(k)) {
                     y.set(k, 0);
                  }
               }

               if (y.isConfigurationSection(targetId.toString())) {
                  ConfigurationSection sec = y.getConfigurationSection(targetId.toString());

                  for(String k : sec.getKeys(false)) {
                     if (k.toLowerCase().contains("kill") || k.toLowerCase().contains("death") || k.toLowerCase().contains("play") || k.toLowerCase().contains("time")) {
                        y.set(String.valueOf(targetId) + "." + k, 0);
                     }
                  }
               }

               y.save(f);
            } catch (Exception var22) {
               try {
                  f.delete();
               } catch (Exception var16) {
               }
            }
         }
      }

      try {
         Module mod = this.plugin.modules().get("stats");
         if (mod != null) {
            for(String mname : new String[]{"wipe", "reset", "clearPlayer"}) {
               try {
                  Method m = mod.getClass().getMethod(mname, UUID.class);
                  m.invoke(mod, targetId);
                  break;
               } catch (NoSuchMethodException var20) {
               }
            }
         }
      } catch (Exception var21) {
      }

      try {
         Module lb = this.plugin.modules().get("lb");
         if (lb == null) {
            lb = this.plugin.modules().get("leaderboards");
         }

         if (lb != null) {
            for(String mname : new String[]{"wipe", "reset", "clearPlayer", "resetPlayer"}) {
               try {
                  Method m = lb.getClass().getMethod(mname, UUID.class);
                  m.invoke(lb, targetId);
                  break;
               } catch (NoSuchMethodException var18) {
               }
            }
         }
      } catch (Exception var19) {
      }

      Player online = Bukkit.getPlayer(targetId);
      if (online != null) {
         try {
            for(Statistic st : Statistic.values()) {
               try {
                  if (st.getType() == Type.UNTYPED) {
                     online.setStatistic(st, 0);
                  }
               } catch (Exception var15) {
               }
            }
         } catch (Exception var17) {
         }
      }

      this.plugin.getLogger().info("Wiped stats files for " + name + " (" + String.valueOf(targetId) + ")");
   }

   private boolean give(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.give") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length < 2) {
         TextUtil.send(s, "&cUsage: /give <player> <item> [amount]");
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(a[0]);
         if (target == null) {
            TextUtil.send(s, "&cPlayer not found.");
            return true;
         } else {
            Material material;
            try {
               material = Material.valueOf(a[1].toUpperCase(Locale.ROOT));
            } catch (Exception var11) {
               TextUtil.send(s, "&cUnknown item: &f" + a[1]);
               return true;
            }

            int amount = 1;
            if (a.length > 2) {
               try {
                  amount = Math.max(1, Math.min(6400, Integer.parseInt(a[2])));
               } catch (Exception var10) {
                  TextUtil.send(s, "&cInvalid amount.");
                  return true;
               }
            }

            int max = material.getMaxStackSize();

            int give;
            for(int left = amount; left > 0; left -= give) {
               give = Math.min(max, left);
               Map<Integer, ItemStack> overflow = target.getInventory().addItem(new ItemStack[]{new ItemStack(material, give)});
               overflow.values().forEach((item) -> target.getWorld().dropItemNaturally(target.getLocation(), item));
            }

            String var10001 = target.getName();
            TextUtil.send(s, "&7Gave &#37BFF9" + var10001 + " &f" + amount + "x " + material.name().toLowerCase(Locale.ROOT) + "&7.");
            return true;
         }
      }
   }

   private boolean enchant(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.enchant") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length < 3) {
         TextUtil.send(s, "&cUsage: /enchant <player> <enchantment> <level>");
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(a[0]);
         if (target == null) {
            TextUtil.send(s, "&cPlayer not found.");
            return true;
         } else {
            ItemStack item = target.getInventory().getItemInMainHand();
            if (item.getType().isAir()) {
               TextUtil.send(s, "&cThat player is not holding an item.");
               return true;
            } else {
               Enchantment enchantment = (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(a[1].toLowerCase(Locale.ROOT)));
               if (enchantment == null) {
                  TextUtil.send(s, "&cUnknown enchantment: &f" + a[1]);
                  return true;
               } else {
                  int level;
                  try {
                     level = Math.max(1, Integer.parseInt(a[2]));
                  } catch (Exception var8) {
                     TextUtil.send(s, "&cInvalid level.");
                     return true;
                  }

                  item.addUnsafeEnchantment(enchantment, level);
                  TextUtil.send(s, "&7Applied &#37BFF9" + a[1] + " " + level + " &7to &#37BFF9" + target.getName() + "&7.");
                  return true;
               }
            }
         }
      }
   }

   private boolean clear(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.clear") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length < 1) {
         TextUtil.send(s, "&cUsage: /clear <player>");
         return true;
      } else {
         Player target = Bukkit.getPlayerExact(a[0]);
         if (target == null) {
            TextUtil.send(s, "&cPlayer not found.");
            return true;
         } else {
            target.getInventory().clear();
            target.getInventory().setArmorContents((ItemStack[])null);
            target.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            TextUtil.send(s, "&7Cleared the inventory of &#37BFF9" + target.getName() + "&7.");
            return true;
         }
      }
   }

   private boolean broadcast(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.broadcast") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length == 0) {
         TextUtil.send(s, "&cUsage: /broadcast <message>");
         return true;
      } else {
         String message = String.join(" ", a);

         for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(TextUtil.color(this.cfg.getString("broadcast.title", "&#37BFF9ʙʀᴏᴀᴅᴄᴀѕᴛ")), TextUtil.color(this.cfg.getString("broadcast.subtitle", "&f" + message).replace("%message%", message)), 10, 30, 15);

            try {
               SoundUtil.play(player, player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
            } catch (Exception var9) {
            }

            Location loc = player.getLocation().add((double)0.0F, (double)1.0F, (double)0.0F);

            try {
               player.getWorld().spawnParticle(Particle.END_ROD, loc, 80, (double)0.5F, (double)0.5F, (double)0.5F, 0.1);
               player.getWorld().spawnParticle(Particle.FIREWORK, loc, 40, (double)0.5F, (double)0.5F, (double)0.5F, 0.1);
            } catch (Exception var8) {
            }
         }

         String format = TextUtil.color(this.cfg.getString("broadcast.format", "&#37BFF9ʙʀᴏᴀᴅᴄᴀѕᴛ &8» &f%message%").replace("%message%", message));
         Bukkit.broadcastMessage("");
         Bukkit.broadcastMessage(format);
         Bukkit.broadcastMessage("");
         return true;
      }
   }

   private List<String> tabGive(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         return this.onlinePlayers(args[0]);
      } else if (args.length == 2) {
         return this.materials(args[1]);
      } else {
         return args.length == 3 ? List.of("1", "16", "32", "64", "128", "256", "512") : List.of();
      }
   }

   private List<String> tabEnchant(CommandSender sender, Command command, String alias, String[] args) {
      if (args.length == 1) {
         return this.onlinePlayers(args[0]);
      } else if (args.length == 2) {
         String input = args[1].toLowerCase(Locale.ROOT);
         return StreamSupport.stream(Registry.ENCHANTMENT.spliterator(), false).map((e) -> e.getKey().getKey()).filter((v) -> v.startsWith(input)).sorted().toList();
      } else {
         return args.length == 3 ? List.of("1", "2", "3", "4", "5", "10") : List.of();
      }
   }

   private List<String> tabClear(CommandSender sender, Command command, String alias, String[] args) {
      return args.length == 1 ? this.onlinePlayers(args[0]) : List.of();
   }

   private List<String> onlinePlayers(String input) {
      String value = input.toLowerCase(Locale.ROOT);
      return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((n) -> n.toLowerCase(Locale.ROOT).startsWith(value)).sorted().toList();
   }

   private List<String> materials(String input) {
      String value = input.toUpperCase(Locale.ROOT);
      return Arrays.stream(Material.values()).filter(Material::isItem).map(Enum::name).filter((n) -> n.startsWith(value)).limit(80L).toList();
   }

   private boolean effect(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.effect") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length < 2) {
         TextUtil.send(s, "&cUsage: /effect <give|clear> <player> [effect] [duration] [amplifier]");
         return true;
      } else {
         String sub = a[0].toLowerCase(Locale.ROOT);
         Player target = Bukkit.getPlayerExact(a[1]);
         if (target == null) {
            TextUtil.send(s, "&cPlayer not found.");
            return true;
         } else if (sub.equals("clear")) {
            target.getActivePotionEffects().forEach((pe) -> target.removePotionEffect(pe.getType()));
            TextUtil.send(s, "&aCleared effects for &f" + target.getName());
            return true;
         } else if (sub.equals("give") && a.length >= 3) {
            PotionEffectType type = (PotionEffectType)Registry.EFFECT.get(NamespacedKey.minecraft(a[2].toLowerCase(Locale.ROOT).replace(" ", "_")));
            if (type == null) {
               try {
                  type = PotionEffectType.getByName(a[2].toUpperCase(Locale.ROOT));
               } catch (Exception var11) {
               }
            }

            if (type == null) {
               TextUtil.send(s, "&cUnknown effect: &f" + a[2]);
               return true;
            } else {
               int seconds = 30;
               int amp = 0;

               try {
                  if (a.length >= 4) {
                     seconds = Integer.parseInt(a[3]);
                  }
               } catch (Exception var10) {
               }

               try {
                  if (a.length >= 5) {
                     amp = Integer.parseInt(a[4]);
                  }
               } catch (Exception var9) {
               }

               target.addPotionEffect(new PotionEffect(type, seconds * 20, amp));
               String var10001 = type.getKey().getKey();
               TextUtil.send(s, "&aGave &f" + var10001 + " &ato &f" + target.getName());
               return true;
            }
         } else {
            TextUtil.send(s, "&cUsage: /effect give <player> <effect> [seconds] [amplifier]");
            return true;
         }
      }
   }

   public FileConfiguration getConfig() {
      return this.cfg;
   }

   public AstralSMPcore getPlugin() {
      return this.plugin;
   }

   private boolean eco(CommandSender s, String[] a) {
      if (!s.hasPermission("astralsmpcore.system.moderation.eco") && !s.isOp()) {
         TextUtil.send(s, "&cNo permission.");
         return true;
      } else if (a.length < 3) {
         TextUtil.send(s, "&cUsage: /eco <give|take|set|bal> <player> [amount]");
         return true;
      } else {
         String sub = a[0].toLowerCase(Locale.ROOT);
         OfflinePlayer target = Bukkit.getOfflinePlayer(a[1]);
         if (target == null || target.getName() == null && !target.hasPlayedBefore()) {
            Player online = Bukkit.getPlayerExact(a[1]);
            if (online != null) {
               target = online;
            }
         }

         if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
            TextUtil.send(s, "&cPlayer not found.");
            return true;
         } else {
            switch (sub) {
               case "give":
               case "add":
                  if (a.length < 3) {
                     TextUtil.send(s, "&cUsage: /eco give <player> <amount>");
                     return true;
                  }

                  double amount = CoreEconomy.parseAmount(a[2]);
                  if (amount < (double)0.0F) {
                     TextUtil.send(s, "&cInvalid amount. Examples: 100, 5k, 1.5m, 2b");
                     return true;
                  }

                  CoreEconomy.give(target, amount);
                  String var16 = CoreEconomy.formatMoney(amount);
                  TextUtil.send(s, "&aGave &#37BFF9" + var16 + " &ato &#37BFF9" + (target.getName() != null ? target.getName() : a[1]));
                  if (target.isOnline() && target.getPlayer() != null) {
                     TextUtil.send(target.getPlayer(), "&aYou received &#37BFF9" + CoreEconomy.formatMoney(amount));
                  }
                  break;
               case "take":
               case "remove":
                  double amount = CoreEconomy.parseAmount(a[2]);
                  if (amount < (double)0.0F) {
                     TextUtil.send(s, "&cInvalid amount.");
                     return true;
                  }

                  double bal = CoreEconomy.balanceOf(target);
                  CoreEconomy.setBalance(target, Math.max((double)0.0F, bal - amount));
                  String var15 = CoreEconomy.formatMoney(amount);
                  TextUtil.send(s, "&aTook &#37BFF9" + var15 + " &afrom &#37BFF9" + (target.getName() != null ? target.getName() : a[1]));
                  break;
               case "set":
                  double amount = CoreEconomy.parseAmount(a[2]);
                  if (amount < (double)0.0F) {
                     TextUtil.send(s, "&cInvalid amount.");
                     return true;
                  }

                  CoreEconomy.setBalance(target, amount);
                  String var14 = target.getName() != null ? target.getName() : a[1];
                  TextUtil.send(s, "&aSet balance of &#37BFF9" + var14 + " &ato &#37BFF9" + CoreEconomy.formatMoney(amount));
                  break;
               case "bal":
               case "balance":
                  double bal = CoreEconomy.balanceOf(target);
                  String var10001 = target.getName() != null ? target.getName() : a[1];
                  TextUtil.send(s, "&7Balance of &#37BFF9" + var10001 + "&7: &#37BFF9" + CoreEconomy.formatMoney(bal));
                  break;
               default:
                  TextUtil.send(s, "&cUsage: /eco <give|take|set|bal> <player> [amount]");
            }

            return true;
         }
      }
   }

   private List<String> tabEco(CommandSender s, Command command, String alias, String[] a) {
      if (a.length == 1) {
         return List.of("give", "take", "set", "bal").stream().filter((x) -> x.startsWith(a[0].toLowerCase(Locale.ROOT))).toList();
      } else if (a.length == 2) {
         return null;
      } else {
         return a.length == 3 && !a[0].equalsIgnoreCase("bal") ? List.of("100", "1k", "5k", "1m", "10m", "1b") : List.of();
      }
   }

   public SusManager getSusManager() {
      return this.susManager;
   }

   public FileConfiguration getCfg() {
      return this.cfg;
   }

   private void loadSusConfig() {
      if (this.susManager == null) {
         this.susManager = new SusManager();
      }

      Map<String, Boolean> ac = new HashMap();
      ConfigurationSection sec = this.cfg.getConfigurationSection("sus.anticheats");
      if (sec != null) {
         for(String key : sec.getKeys(false)) {
            ac.put(key.toLowerCase(Locale.ROOT), sec.getBoolean(key, true));
         }
      }

      this.susManager.configure(this.cfg.getInt("sus.max-flags-stored", 500), this.cfg.getInt("sus.flag-expire-minutes", 60), ac);
   }

   private boolean sus(CommandSender s, String[] a) {
      if (s instanceof Player p) {
         if (!p.hasPermission("astralsmpcore.system.moderation.sus")) {
            TextUtil.send(p, this.cfg.getString("sus.messages.no-permission", this.cfg.getString("messages.no-permission", "&cNo permission.")));
            return true;
         } else if (!this.cfg.getBoolean("sus.enabled", true)) {
            TextUtil.send(p, this.cfg.getString("sus.messages.disabled", "&cSus system is disabled."));
            return true;
         } else {
            if (this.susManager == null) {
               this.loadSusConfig();
            }

            SusMenus.openMain(p, this.susManager, this.cfg, 0);
            return true;
         }
      } else {
         TextUtil.send(s, "&cPlayers only.");
         return true;
      }
   }

   public static final class WipeHolder implements InventoryHolder {
      private final UUID target;

      public WipeHolder(UUID target) {
         this.target = target;
      }

      public UUID getTarget() {
         return this.target;
      }

      public Inventory getInventory() {
         return null;
      }
   }

   public static final class KillHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
