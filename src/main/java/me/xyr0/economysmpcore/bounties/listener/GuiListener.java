package me.xyr0.economysmpcore.bounties.listener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.xyr0.economysmpcore.SettingsModule;
import me.xyr0.economysmpcore.bounties.AstralBounties;
import me.xyr0.economysmpcore.bounties.BountyManager;
import me.xyr0.economysmpcore.bounties.gui.BountyMenus;
import me.xyr0.economysmpcore.bounties.gui.MenuSession;
import me.xyr0.economysmpcore.bounties.util.InputUtil;
import me.xyr0.economysmpcore.economy.CoreEconomy;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.HotbarUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class GuiListener implements Listener {
   private final AstralBounties plugin;
   private final Map<UUID, Boolean> searching = new HashMap();

   public GuiListener(AstralBounties plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
         if (view != null) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               this.play(player, "gui-click");
               int slot = event.getRawSlot();
               if (view == MenuSession.View.MAIN) {
                  this.handleMain(player, slot);
               } else if (view == MenuSession.View.CONFIRM) {
                  this.handleConfirm(player, slot);
               } else if (view == MenuSession.View.ADMIN) {
                  this.handleAdmin(player, slot);
               }

            }
         }
      }
   }

   private void handleMain(Player player, int slot) {
      FileConfiguration gui = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "gui/main.yml"));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
         BountyManager.Sort sort = (BountyManager.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), BountyManager.Sort.HIGHEST);
         if (this.isSlot(items, "previous-page", slot)) {
            BountyMenus.openMain(player, Math.max(0, page - 1), sort);
         } else if (this.isSlot(items, "next-page", slot)) {
            BountyMenus.openMain(player, page + 1, sort);
         } else if (this.isSlot(items, "refresh", slot)) {
            BountyMenus.openMain(player, page, sort);
         } else if (this.isSlot(items, "sort", slot)) {
            BountyMenus.openMain(player, 0, BountyManager.nextSort(sort));
         } else if (this.isSlot(items, "search", slot)) {
            this.searching.put(player.getUniqueId(), true);
            InputUtil.requestSearch(player);
         } else {
            if (slot >= 0 && slot < 45 && player.hasPermission("economysmpcore.system.bounties.admin")) {
               List<BountyManager.Bounty> list = this.plugin.getBounties().list(sort);
               int index = page * 45 + slot;
               if (index >= 0 && index < list.size()) {
                  OfflinePlayer target = Bukkit.getOfflinePlayer(((BountyManager.Bounty)list.get(index)).target());
                  BountyMenus.openAdmin(player, target);
               }
            }

         }
      }
   }

   private void handleConfirm(Player player, int slot) {
      FileConfiguration gui = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "gui/confirm.yml"));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         if (this.isSlot(items, "cancel", slot)) {
            player.closeInventory();
            MenuSession.clear(player.getUniqueId());
         } else if (this.isSlot(items, "confirm", slot)) {
            UUID targetId = (UUID)MenuSession.TARGET.get(player.getUniqueId());
            Double amount = (Double)MenuSession.AMOUNT.get(player.getUniqueId());
            if (targetId != null && amount != null && !(amount <= (double)0.0F)) {
               OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
               if (!CoreEconomy.hasFunds(player, amount)) {
                  this.play(player, "no-perm");
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("not-enough-balance", "&cYou do not have enough balance to add this bounty.")));
               } else {
                  CoreEconomy.take(player, amount);
                  String name = target.getName() != null ? target.getName() : "Unknown";
                  this.plugin.getBounties().addBounty(targetId, name, amount);
                  String fmt = BountyManager.formatMoney(amount);
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("bounty-added", "&fYou added &#fb0000$%amount_format% &fto &#00a3fb%target_name%'s &fbounty.").replace("%amount_format%", fmt).replace("%target_name%", name)));
                  String bar = this.plugin.getMessages().get("actionbar-bounty-added", "");
                  if (bar != null && !bar.isBlank()) {
                     HotbarUtil.send(player, ColorUtil.parse(bar.replace("%amount_format%", fmt).replace("%target_name%", name)));
                  }

                  if (target.isOnline() && target.getPlayer() != null) {
                     Player t = target.getPlayer();
                     t.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("bounty-added-on-you", "&#00a3fb%user_name% &fadded &#fb0000$%amount_format% &fto your bounty.").replace("%user_name%", player.getName()).replace("%amount_format%", fmt)));
                     String bar2 = this.plugin.getMessages().get("actionbar-bounty-added-on-you", "");
                     if (bar2 != null && !bar2.isBlank()) {
                        HotbarUtil.send(t, ColorUtil.parse(bar2.replace("%user_name%", player.getName()).replace("%amount_format%", fmt)));
                     }
                  }

                  String broadcast = this.plugin.getMessages().get("bounty-broadcast", "&#00a3fb%user% &fplaced &#fb0000$%amount% &fon &#00a3fb%target%&f.");
                  if (broadcast != null && !broadcast.isBlank()) {
                     String out = broadcast.replace("%user%", player.getName()).replace("%amount%", fmt).replace("%target%", name);

                     for(Player op : Bukkit.getOnlinePlayers()) {
                        if (!op.equals(player) && !SettingsModule.isBountyAlertDisabled(op.getUniqueId())) {
                           op.sendMessage(ColorUtil.parse(out));
                        }
                     }
                  }

                  this.play(player, "bounty-added");
                  BountyMenus.openMain(player, 0, BountyManager.Sort.HIGHEST);
               }
            }
         }
      }
   }

   private void handleAdmin(Player player, int slot) {
      FileConfiguration gui = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "gui/admin.yml"));
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         if (this.isSlot(items, "back", slot)) {
            BountyMenus.openMain(player, (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0), (BountyManager.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), BountyManager.Sort.HIGHEST));
         } else if (this.isSlot(items, "remove", slot)) {
            UUID targetId = (UUID)MenuSession.TARGET.get(player.getUniqueId());
            if (targetId != null) {
               double amount = this.plugin.getBounties().clearBounty(targetId);
               OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
               String name = target.getName() != null ? target.getName() : "Unknown";
               player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("bounty-removed", "&fRemoved bounty of &#fb0000$%amount_format% &ffrom &#00a3fb%target_name%&f.").replace("%amount_format%", BountyManager.formatMoney(amount)).replace("%target_name%", name)));
               BountyMenus.openMain(player, 0, BountyManager.Sort.HIGHEST);
            }
         }
      }
   }

   private boolean isSlot(ConfigurationSection items, String key, int slot) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec == null) {
         return false;
      } else if (sec.getInt("slot", -1) == slot) {
         return true;
      } else {
         return sec.isList("slots") && sec.getIntegerList("slots").contains(slot);
      }
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (Boolean.TRUE.equals(this.searching.get(player.getUniqueId()))) {
         if (!InputUtil.isSignInput(player.getUniqueId())) {
            event.setCancelled(true);
            this.searching.remove(player.getUniqueId());
            String msg = event.getMessage().trim();
            BountyManager.Sort sort = (BountyManager.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), BountyManager.Sort.HIGHEST);
            Tasks.run(this.plugin.getCore(), () -> this.handleSearchResult(player, sort, msg));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      if (InputUtil.isSignInput(player.getUniqueId()) || Boolean.TRUE.equals(this.searching.get(player.getUniqueId()))) {
         event.setCancelled(true);
         String line = "";

         try {
            Component component = event.line(0);
            if (component != null) {
               line = PlainTextComponentSerializer.plainText().serialize(component);
            }
         } catch (Exception var6) {
            String legacy = event.getLine(0);
            if (legacy != null) {
               line = legacy;
            }
         }

         String finalLine = line.trim();
         this.searching.remove(player.getUniqueId());
         BountyManager.Sort sort = (BountyManager.Sort)MenuSession.SORT.getOrDefault(player.getUniqueId(), BountyManager.Sort.HIGHEST);
         Tasks.run(this.plugin.getCore(), () -> {
            InputUtil.cleanup(player);
            this.handleSearchResult(player, sort, finalLine);
         });
      }
   }

   private void handleSearchResult(Player player, BountyManager.Sort sort, String msg) {
      if (msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("cancel")) {
         int page = this.plugin.getBounties().findPage(msg, sort, 45);
         if (page < 0) {
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found!")));
            BountyMenus.openMain(player, 0, sort);
         } else {
            BountyMenus.openMain(player, page, sort);
         }
      } else {
         player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("search-cancelled", "&cSearch cancelled!")));
         BountyMenus.openMain(player, 0, sort);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      InputUtil.cleanup(event.getPlayer());
      this.searching.remove(event.getPlayer().getUniqueId());
      MenuSession.clear(event.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (MenuSession.VIEW.get(player.getUniqueId()) != null) {
            if (!Boolean.TRUE.equals(this.searching.get(player.getUniqueId())) && !InputUtil.isSignInput(player.getUniqueId())) {
               Tasks.run(this.plugin.getCore(), () -> {
                  if (!player.isOnline()) {
                     MenuSession.clear(player.getUniqueId());
                  } else if (player.getOpenInventory().getTopInventory().getSize() <= 5 || !MenuSession.VIEW.containsKey(player.getUniqueId())) {
                     if (!Boolean.TRUE.equals(this.searching.get(player.getUniqueId())) && !InputUtil.isSignInput(player.getUniqueId())) {
                        MenuSession.clear(player.getUniqueId());
                     }
                  }
               });
            }
         }
      }
   }

   private void play(Player player, String key) {
      try {
         String path = "sounds." + key;
         if (!this.plugin.getConfig().getBoolean(path + ".enabled", true)) {
            return;
         }

         String sound = this.plugin.getConfig().getString(path + ".sound", "UI_BUTTON_CLICK");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var5) {
      }

   }
}
