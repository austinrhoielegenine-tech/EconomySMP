package com.austynzdevv.economysmp.lb.listener;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.austynzdevv.economysmp.lb.EconomyLeaderboards;
import com.austynzdevv.economysmp.lb.StatsManager;
import com.austynzdevv.economysmp.lb.gui.LeaderboardMenus;
import com.austynzdevv.economysmp.lb.gui.MenuSession;
import com.austynzdevv.economysmp.lb.util.InputUtil;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import com.austynzdevv.economysmp.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
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
   private final EconomyLeaderboards plugin;
   private final Map<UUID, Boolean> searching = new HashMap();

   public GuiListener(EconomyLeaderboards plugin) {
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
               this.playClick(player);
               int slot = event.getRawSlot();
               if (view == MenuSession.View.MAIN) {
                  this.handleMain(player, slot);
               } else if (view == MenuSession.View.BOARD) {
                  this.handleBoard(player, slot);
               }

            }
         }
      }
   }

   private void handleMain(Player player, int slot) {
      File guiFile = new File(this.plugin.getDataFolder(), "gui/main.yml");
      FileConfiguration gui = YamlConfiguration.loadConfiguration(guiFile);
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         for(String key : items.getKeys(false)) {
            ConfigurationSection sec = items.getConfigurationSection(key);
            if (sec != null && sec.getBoolean("enabled", true) && sec.getInt("slot", -1) == slot) {
               StatsManager.Board board = StatsManager.fromId(key);
               if (board == null) {
                  return;
               }

               this.plugin.getStats().refreshCache();
               LeaderboardMenus.openBoard(player, board, 0);
               return;
            }
         }

      }
   }

   private void handleBoard(Player player, int slot) {
      StatsManager.Board board = (StatsManager.Board)MenuSession.BOARD.get(player.getUniqueId());
      if (board != null) {
         int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
         String id = StatsManager.toId(board);
         File guiFile = new File(this.plugin.getDataFolder(), "gui/" + id + ".yml");
         FileConfiguration gui = YamlConfiguration.loadConfiguration(guiFile);
         ConfigurationSection items = gui.getConfigurationSection("items");
         if (items != null) {
            if (this.isSlot(items, "back", slot)) {
               if (page > 0) {
                  LeaderboardMenus.openBoard(player, board, page - 1);
               } else {
                  LeaderboardMenus.openMain(player);
               }

            } else if (this.isSlot(items, "next", slot)) {
               LeaderboardMenus.openBoard(player, board, page + 1);
            } else if (this.isSlot(items, "refresh", slot)) {
               this.plugin.getStats().refreshCache();
               LeaderboardMenus.openBoard(player, board, page);
               player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("refreshed", "&aLeaderboard refreshed.")));
            } else if (this.isSlot(items, "search", slot)) {
               this.searching.put(player.getUniqueId(), true);
               InputUtil.requestSearch(player);
            } else {
               ConfigurationSection playerSec = items.getConfigurationSection("player");
               String cmd = playerSec != null ? playerSec.getString("command", "") : "";
               if (cmd != null && !cmd.isBlank() && slot >= 0 && slot < 45) {
                  int index = page * 45 + slot;
                  List<StatsManager.Entry> list = this.plugin.getStats().getBoard(board);
                  if (index >= 0 && index < list.size()) {
                     String run = cmd.replace("%player%", ((StatsManager.Entry)list.get(index)).name());
                     player.closeInventory();
                     Bukkit.dispatchCommand(player, run);
                  }
               }

            }
         }
      }
   }

   private boolean isSlot(ConfigurationSection items, String key, int slot) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      return sec != null && sec.getInt("slot", -1) == slot;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (Boolean.TRUE.equals(this.searching.get(player.getUniqueId()))) {
         if (!InputUtil.isSignInput(player.getUniqueId())) {
            event.setCancelled(true);
            this.searching.remove(player.getUniqueId());
            String msg = event.getMessage().trim();
            StatsManager.Board board = (StatsManager.Board)MenuSession.BOARD.get(player.getUniqueId());
            Tasks.run(this.plugin.getCore(), () -> this.handleSearchResult(player, board, msg));
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
         StatsManager.Board board = (StatsManager.Board)MenuSession.BOARD.get(player.getUniqueId());
         Tasks.run(this.plugin.getCore(), () -> {
            InputUtil.cleanup(player);
            this.handleSearchResult(player, board, finalLine);
         });
      }
   }

   private void handleSearchResult(Player player, StatsManager.Board board, String msg) {
      if (msg != null && !msg.isEmpty() && !msg.equalsIgnoreCase("cancel")) {
         if (board == null) {
            LeaderboardMenus.openMain(player);
         } else {
            int page = this.plugin.getStats().findPage(board, msg, 45);
            if (page < 0) {
               player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found")));
               LeaderboardMenus.openBoard(player, board, 0);
            } else {
               LeaderboardMenus.openBoard(player, board, page);
            }
         }
      } else {
         player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("search-cancelled", "&cSearch cancelled!")));
         if (board != null) {
            LeaderboardMenus.openBoard(player, board, (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0));
         } else {
            LeaderboardMenus.openMain(player);
         }

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
                  } else {
                     int topSize = player.getOpenInventory().getTopInventory().getSize();
                     if (topSize <= 5 || !MenuSession.VIEW.containsKey(player.getUniqueId())) {
                        if (!Boolean.TRUE.equals(this.searching.get(player.getUniqueId())) && !InputUtil.isSignInput(player.getUniqueId())) {
                           MenuSession.clear(player.getUniqueId());
                        }
                     }
                  }
               });
            }
         }
      }
   }

   private void playClick(Player player) {
      try {
         String name = this.plugin.getConfig().getString("sounds.click-sound", "UI_BUTTON_CLICK");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(name.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var3) {
      }

   }
}
