package me.xyr0.astralsmpcore.tools.listener;

import me.xyr0.astralsmpcore.tools.AstralTools;
import me.xyr0.astralsmpcore.tools.ToolDefinition;
import me.xyr0.astralsmpcore.tools.gui.ToolsGui;
import me.xyr0.astralsmpcore.tools.util.MessageUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
   private final AstralTools plugin;

   public GuiListener(AstralTools plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      if (event.getInventory().getHolder() instanceof ToolsGui) {
         event.setCancelled(true);
         HumanEntity var3 = event.getWhoClicked();
         if (var3 instanceof Player) {
            Player player = (Player)var3;
            if (event.getClickedInventory() != null) {
               if (event.getClickedInventory().getHolder() == event.getInventory().getHolder()) {
                  ItemStack clicked = event.getCurrentItem();
                  if (clicked != null && !clicked.getType().isAir()) {
                     int closeSlot = this.plugin.getGuiConfig().getInt("close.slot", -1);
                     if (event.getSlot() == closeSlot) {
                        player.closeInventory();
                     } else {
                        String type = this.plugin.getToolFactory().getType(clicked);
                        if (type != null) {
                           if (!player.hasPermission("astralsmpcore.system.tools.admin")) {
                              MessageUtil.send(player, "no-permission");
                           } else {
                              ToolDefinition def = this.plugin.getToolManager().get(type);
                              if (def != null) {
                                 ItemStack give = this.plugin.getToolFactory().create(def);
                                 if (player.getInventory().firstEmpty() == -1) {
                                    MessageUtil.send(player, "inventory-full");
                                 } else {
                                    player.getInventory().addItem(new ItemStack[]{give});
                                    MessageUtil.send(player, "tool-received", "%tool%", def.getId());
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
