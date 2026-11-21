package me.xyr0.economysmpcore.baltop.listener;

import java.io.File;
import me.xyr0.economysmpcore.baltop.AstralBaltop;
import me.xyr0.economysmpcore.baltop.gui.BaltopMenus;
import me.xyr0.economysmpcore.baltop.gui.MenuSession;
import me.xyr0.economysmpcore.baltop.util.InputUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class GuiListener implements Listener {
   private final AstralBaltop plugin;

   public GuiListener(AstralBaltop plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (MenuSession.VIEW.get(player.getUniqueId()) == MenuSession.View.MAIN) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               int slot = event.getRawSlot();
               File file = new File(this.plugin.getDataFolder(), "gui/main-gui.yml");
               FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
               ConfigurationSection items = cfg.getConfigurationSection("menu.items");
               if (items != null) {
                  int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
                  int back = items.getConfigurationSection("back") != null ? items.getConfigurationSection("back").getInt("slot", 45) : 45;
                  int next = items.getConfigurationSection("next") != null ? items.getConfigurationSection("next").getInt("slot", 53) : 53;
                  int refresh = items.getConfigurationSection("refresh") != null ? items.getConfigurationSection("refresh").getInt("slot", 49) : 49;
                  int search = items.getConfigurationSection("search") != null ? items.getConfigurationSection("search").getInt("slot", 50) : 50;
                  SoundUtil.playConfigured(player, AstralBaltop.get().getCore(), "sounds.click");
                  if (slot == back) {
                     BaltopMenus.open(player, page - 1);
                  } else if (slot == next) {
                     BaltopMenus.open(player, page + 1);
                  } else if (slot == refresh) {
                     MenuSession.SEARCH.remove(player.getUniqueId());
                     this.plugin.getCache().forceRefresh(() -> {
                        if (player.isOnline()) {
                           BaltopMenus.open(player, 0);
                        }

                     });
                  } else {
                     if (slot == search) {
                        InputUtil.requestSearch(player);
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (!MenuSession.SWITCHING.contains(player.getUniqueId())) {
            if (!Boolean.TRUE.equals(MenuSession.CHAT_SEARCH.get(player.getUniqueId()))) {
               if (!Boolean.TRUE.equals(MenuSession.SIGN_SEARCH.get(player.getUniqueId()))) {
                  if (MenuSession.VIEW.get(player.getUniqueId()) == MenuSession.View.MAIN) {
                     MenuSession.clear(player.getUniqueId());
                  }

               }
            }
         }
      }
   }
}
