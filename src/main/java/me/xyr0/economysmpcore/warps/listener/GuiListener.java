package me.xyr0.economysmpcore.warps.listener;

import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.util.Tasks;
import me.xyr0.economysmpcore.warps.AstralWarps;
import me.xyr0.economysmpcore.warps.NamedLocation;
import me.xyr0.economysmpcore.warps.gui.MenuSession;
import me.xyr0.economysmpcore.warps.gui.WarpMenus;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class GuiListener implements Listener {
   private final AstralWarps plugin;

   public GuiListener(AstralWarps plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               MenuSession.Type mode = (MenuSession.Type)MenuSession.MODE.get(player.getUniqueId());
               if (mode != null) {
                  FileConfiguration gui = mode == MenuSession.Type.SPAWN ? WarpMenus.getSpawnGui() : WarpMenus.getAfkGui();
                  ConfigurationSection root = mode == MenuSession.Type.SPAWN ? gui.getConfigurationSection("spawn-gui") : gui.getConfigurationSection("afk-gui");
                  if (root == null) {
                     root = gui;
                  }

                  ConfigurationSection settings = root.getConfigurationSection("gui-settings");
                  int slot = event.getRawSlot();
                  int page = (Integer)MenuSession.PAGE.getOrDefault(player.getUniqueId(), 0);
                  this.playClick(player);
                  if (settings != null) {
                     ConfigurationSection random = settings.getConfigurationSection(mode == MenuSession.Type.SPAWN ? "random-spawn" : "random-afk");
                     if (random != null && random.getInt("slot", 49) == slot) {
                        NamedLocation loc = mode == MenuSession.Type.SPAWN ? this.plugin.getWarps().randomSpawn() : this.plugin.getWarps().randomAfk();
                        player.closeInventory();
                        MenuSession.clear(player.getUniqueId());
                        if (loc == null) {
                           player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get(mode == MenuSession.Type.SPAWN ? "no-spawns" : "no-afk-areas", "&cNo locations have been set yet!")));
                           return;
                        }

                        this.plugin.getTeleports().start(player, loc, mode == MenuSession.Type.SPAWN ? "spawn" : "afk");
                        return;
                     }

                     ConfigurationSection back = settings.getConfigurationSection("back");
                     if (back != null && back.getInt("slot", 45) == slot && page > 0) {
                        if (mode == MenuSession.Type.SPAWN) {
                           WarpMenus.openSpawn(player, page - 1);
                        } else {
                           WarpMenus.openAfk(player, page - 1);
                        }

                        return;
                     }

                     ConfigurationSection next = settings.getConfigurationSection("next");
                     if (next != null && next.getInt("slot", 53) == slot) {
                        if (mode == MenuSession.Type.SPAWN) {
                           WarpMenus.openSpawn(player, page + 1);
                        } else {
                           WarpMenus.openAfk(player, page + 1);
                        }

                        return;
                     }
                  }

                  String name = WarpMenus.locationNameAt(player, slot);
                  if (name != null) {
                     NamedLocation loc = mode == MenuSession.Type.SPAWN ? this.plugin.getWarps().getSpawn(name) : this.plugin.getWarps().getAfk(name);
                     player.closeInventory();
                     MenuSession.clear(player.getUniqueId());
                     if (loc != null) {
                        if (loc.isFull() && !player.hasPermission("economysmpcore.system.warps.bypass")) {
                           player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get(mode == MenuSession.Type.SPAWN ? "spawn-full" : "afk-full", "&cThis area is already full!")));
                        } else {
                           this.plugin.getTeleports().start(player, loc, mode == MenuSession.Type.SPAWN ? "spawn" : "afk");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void playClick(Player player) {
      try {
         if (!this.plugin.getConfig().getBoolean("sounds.enable", true)) {
            return;
         }

         String sound = this.plugin.getConfig().getString("sounds.click", "UI_BUTTON_CLICK");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase().replace('.', '_')), 1.0F, 1.0F);
      } catch (Exception var3) {
      }

   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            Tasks.run(this.plugin.getCore(), () -> {
               if (!player.isOnline()) {
                  MenuSession.clear(player.getUniqueId());
               } else if (player.getOpenInventory().getTopInventory().getSize() <= 5 || !Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
                  MenuSession.clear(player.getUniqueId());
               }
            });
         }
      }
   }
}
