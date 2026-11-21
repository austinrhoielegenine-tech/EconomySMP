package com.austynzdevv.novacore;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.austynzdevv.novacore.data.PlayerDataStore;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import com.austynzdevv.novacore.util.TextUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class VoiceChatModule implements Module, Listener {
   private final NovaCore plugin;
   private FileConfiguration config;
   private File dataFile;
   private final Set<UUID> accepted = new HashSet();
   private final Set<UUID> required = new HashSet();
   private static VoiceChatModule instance;

   public VoiceChatModule(NovaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "voicechat";
   }

   public void onEnable() {
      instance = this;
      this.loadConfig();
      this.loadData();
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("voicechat", this::command);
      this.plugin.commands().registerHandler("vc", this::command);
   }

   public void onDisable() {
      this.saveData();
      if (instance == this) {
         instance = null;
      }

   }

   public static VoiceChatModule getInstance() {
      return instance;
   }

   public void openFirstJoin(Player player) {
      if (player != null && player.isOnline() && !this.accepted.contains(player.getUniqueId())) {
         this.required.add(player.getUniqueId());
         this.open(player);
      }
   }

   public void reload() {
      this.loadConfig();
   }

   private void loadConfig() {
      File file = new File(this.plugin.getDataFolder(), "VoiceChat.yml");
      if (!file.exists()) {
         try {
            this.plugin.saveResource("VoiceChat.yml", false);
         } catch (Exception var3) {
         }
      }

      this.config = YamlConfiguration.loadConfiguration(file);
   }

   private void loadData() {
      this.dataFile = PlayerDataStore.get().file();
      FileConfiguration data = PlayerDataStore.get().config();

      for(String id : data.getStringList("accepted")) {
         try {
            this.accepted.add(UUID.fromString(id));
         } catch (Exception var5) {
         }
      }

   }

   private void saveData() {
      if (this.dataFile != null) {
         FileConfiguration data = PlayerDataStore.get().config();
         data.set("accepted", this.accepted.stream().map(UUID::toString).toList());
         PlayerDataStore.get().save();
      }
   }

   private boolean command(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length > 0 && args[0].equalsIgnoreCase("revoke")) {
            this.accepted.remove(player.getUniqueId());
            this.saveData();
            this.setPermissions(player, false);
            TextUtil.send(player, this.config.getString("messages.revoked", "&cVoice Chat has been revoked."));
            this.sound(player, "sounds.revoke");
            return true;
         } else if (this.accepted.contains(player.getUniqueId())) {
            TextUtil.send(player, this.config.getString("messages.already-enabled", "&#37BFF9You already accepted voice chat."));
            return true;
         } else {
            this.open(player);
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      UUID id = player.getUniqueId();
      if (this.accepted.contains(id)) {
         this.setPermissions(player, true);
      } else {
         this.setPermissions(player, false);
      }

   }

   private void open(Player player) {
      this.required.add(player.getUniqueId());
      int rows = Math.max(1, Math.min(6, this.config.getInt("gui.rows", 3)));
      Inventory inv = Bukkit.createInventory(new Holder(), rows * 9, TextUtil.component(this.config.getString("gui.name", "&8ᴄᴏɴꜰɪʀᴍ ᴠᴏɪᴄᴇ ᴄʜᴀᴛ")));
      this.setItem(inv, "cancel");
      this.setItem(inv, "info");
      this.setItem(inv, "accept");
      player.openInventory(inv);
   }

   private void setItem(Inventory inv, String key) {
      String materialName = this.config.getString("gui." + key + ".material", "STONE");

      Material material;
      try {
         material = Material.valueOf(materialName.toUpperCase());
      } catch (Exception var8) {
         material = Material.STONE;
      }

      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(TextUtil.component(this.config.getString("gui." + key + ".name", "")));
         meta.lore(this.config.getStringList("gui." + key + ".lore").stream().map(TextUtil::component).toList());
         item.setItemMeta(meta);
      }

      int slot = this.config.getInt("gui." + key + ".slot", 13);
      if (slot >= 0 && slot < inv.getSize()) {
         inv.setItem(slot, item);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (event.getView().getTopInventory().getHolder() instanceof Holder) {
            event.setCancelled(true);
            if (event.getRawSlot() == this.config.getInt("gui.cancel.slot", 11)) {
               UUID id = player.getUniqueId();
               this.accepted.remove(id);
               this.required.remove(id);
               this.saveData();
               this.setPermissions(player, false);
               player.closeInventory();
               TextUtil.send(player, this.config.getString("messages.refused", "&cVoice Chat denied."));
               this.sound(player, "sounds.cancel");
            } else {
               if (event.getRawSlot() == this.config.getInt("gui.accept.slot", 15)) {
                  UUID id = player.getUniqueId();
                  this.accepted.add(id);
                  this.required.remove(id);
                  this.saveData();
                  this.setPermissions(player, true);
                  player.closeInventory();
                  TextUtil.send(player, this.config.getString("messages.enabled", "&aVoice Chat enabled."));
                  this.sound(player, "sounds.accept");
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (event.getView().getTopInventory().getHolder() instanceof Holder) {
            UUID id = player.getUniqueId();
            if (this.required.contains(id) && !this.accepted.contains(id)) {
               Tasks.run(this.plugin, () -> {
                  if (player.isOnline() && this.required.contains(id) && !this.accepted.contains(id)) {
                     this.open(player);
                  }

               });
            }
         }
      }
   }

   private void setPermissions(Player player, boolean enabled) {
      try {
         LuckPerms lp = LuckPermsProvider.get();
         String prefix = "voicechat.";
         lp.getUserManager().modifyUser(player.getUniqueId(), (user) -> {
            user.data().remove(Node.builder(prefix + "speak").build());
            user.data().remove(Node.builder(prefix + "listen").build());
            user.data().remove(Node.builder(prefix + "groups").build());
            if (enabled) {
               user.data().add(Node.builder(prefix + "speak").value(true).build());
               user.data().add(Node.builder(prefix + "listen").value(true).build());
               user.data().add(Node.builder(prefix + "groups").value(true).build());
            } else {
               user.data().add(Node.builder(prefix + "speak").value(false).build());
               user.data().add(Node.builder(prefix + "listen").value(false).build());
               user.data().add(Node.builder(prefix + "groups").value(false).build());
            }

         });
      } catch (Exception t) {
         if (this.plugin.configs().getMain().getBoolean("settings.debug", false)) {
            this.plugin.getLogger().warning("Could not update voice chat permissions: " + t.getMessage());
         }
      }

   }

   private void sound(Player player, String path) {
      try {
         Sound sound = Sound.valueOf(this.config.getString(path + ".sound", "ENTITY_PLAYER_LEVELUP"));
         SoundUtil.play(player, player.getLocation(), sound, (float)this.config.getDouble(path + ".volume", (double)1.0F), (float)this.config.getDouble(path + ".pitch", (double)1.0F));
      } catch (Exception var4) {
      }

   }

   private static final class Holder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
