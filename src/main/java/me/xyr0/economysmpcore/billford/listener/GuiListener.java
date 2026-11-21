package me.xyr0.economysmpcore.billford.listener;

import java.util.HashMap;
import me.xyr0.economysmpcore.billford.AstralBillford;
import me.xyr0.economysmpcore.billford.BillfordManager;
import me.xyr0.economysmpcore.billford.gui.BillfordMenus;
import me.xyr0.economysmpcore.billford.gui.MenuSession;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import me.xyr0.economysmpcore.util.Tasks;
import org.bukkit.Material;
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
import org.bukkit.inventory.ItemStack;

public final class GuiListener implements Listener {
   private final AstralBillford plugin;

   public GuiListener(AstralBillford plugin) {
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
               this.play(player, "click");
               FileConfiguration gui = BillfordMenus.loadGui(this.plugin);
               ConfigurationSection items = gui.getConfigurationSection("items");
               if (items == null) {
                  items = gui.getConfigurationSection("Items");
               }

               if (items != null) {
                  ConfigurationSection confirm = items.getConfigurationSection("trade-confirm-item");
                  if (confirm != null) {
                     if (event.getRawSlot() == confirm.getInt("slot", 23)) {
                        BillfordManager mgr = this.plugin.getBillford();
                        BillfordManager.Preset preset = mgr.getCurrentPreset();
                        if (preset != null) {
                           long leftMs = mgr.getCooldownLeft(player);
                           if (leftMs > 0L) {
                              this.play(player, "error");
                              long secs = (leftMs + 999L) / 1000L;
                              player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("cooldown", "&cPlease wait %time% seconds!").replace("%time%", String.valueOf(secs))));
                           } else if (!mgr.hasRequired(player, preset)) {
                              this.play(player, "error");
                              player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-required-items", "&cYou dont have the required items to trade!")));
                           } else {
                              new ItemStack(preset.tradeItem(), Math.min(64, preset.tradeAmount()));
                              if (preset.tradeAmount() > 64) {
                                 new ItemStack(preset.tradeItem(), 64);
                              }

                              if (player.getInventory().firstEmpty() == -1) {
                                 ItemStack test = new ItemStack(preset.tradeItem(), 1);
                                 if (!player.getInventory().containsAtLeast(test, 1) || this.freeSpaceFor(player, preset.tradeItem()) < preset.tradeAmount()) {
                                    this.play(player, "error");
                                    player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("inventory-full", "&cYour inventory is full!")));
                                    return;
                                 }
                              }

                              mgr.removeRequired(player, preset);
                              this.giveReward(player, preset.tradeItem(), preset.tradeAmount());
                              mgr.setCooldown(player);
                              this.play(player, "trade-success");
                              String var10001 = this.plugin.getMessages().get("trade-success", "&fYou have successfully traded your items for &#7afc00%trade_item%&f!");
                              int var10003 = preset.tradeAmount();
                              player.sendMessage(ColorUtil.parse(var10001.replace("%trade_item%", var10003 + "x " + mgr.pretty(preset.tradeItem()))));
                              BillfordMenus.open(player);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int freeSpaceFor(Player player, Material mat) {
      int space = 0;

      for(ItemStack stack : player.getInventory().getStorageContents()) {
         if (stack != null && stack.getType() != Material.AIR) {
            if (stack.getType() == mat) {
               space += mat.getMaxStackSize() - stack.getAmount();
            }
         } else {
            space += mat.getMaxStackSize();
         }
      }

      return space;
   }

   private void giveReward(Player player, Material mat, int amount) {
      int give;
      for(int left = amount; left > 0; left -= give) {
         give = Math.min(mat.getMaxStackSize(), left);
         ItemStack stack = new ItemStack(mat, give);
         HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack[]{stack});
         leftover.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
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

   private void play(Player player, String key) {
      String path = "sounds." + key + ".sound";
      String name = this.plugin.getConfig().getString(path, "");
      if (name == null || name.isBlank()) {
         String var10000;
         switch (key) {
            case "click" -> var10000 = "UI_BUTTON_CLICK";
            case "trade-success" -> var10000 = "ENTITY_PLAYER_LEVELUP";
            case "error" -> var10000 = "ENTITY_VILLAGER_NO";
            case "reload" -> var10000 = "BLOCK_NOTE_BLOCK_PLING";
            default -> var10000 = "";
         }

         name = var10000;
      }

      if (!name.isBlank()) {
         try {
            String normalized = name.toUpperCase().replace('.', '_');
            SoundUtil.play(player, player.getLocation(), Sound.valueOf(normalized), 1.0F, 1.0F);
         } catch (Exception var7) {
         }

      }
   }
}
