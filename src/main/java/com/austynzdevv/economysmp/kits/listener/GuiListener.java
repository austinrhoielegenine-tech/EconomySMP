package com.austynzdevv.economysmp.kits.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.economysmp.kits.EconomyKits;
import com.austynzdevv.economysmp.kits.Kit;
import com.austynzdevv.economysmp.kits.gui.KitsHolder;
import com.austynzdevv.economysmp.kits.util.MessageUtil;
import com.austynzdevv.economysmp.util.SignInput;
import com.austynzdevv.economysmp.util.TextUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GuiListener implements Listener {
   private final EconomyKits host;
   private final Map<UUID, String> cooldownInput = new ConcurrentHashMap();

   public GuiListener(EconomyKits host) {
      this.host = host;
   }

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      InventoryHolder var3 = event.getInventory().getHolder();
      if (var3 instanceof KitsHolder holder) {
         HumanEntity var4 = event.getWhoClicked();
         if (var4 instanceof Player player) {
            if (!"editor".equals(holder.getType()) || event.getClickedInventory() != event.getView().getTopInventory() || event.getRawSlot() >= 45) {
               event.setCancelled(true);
               if (event.getClickedInventory() == event.getView().getTopInventory()) {
                  ItemStack clicked = event.getCurrentItem();
                  if (clicked != null) {
                     if ("main".equals(holder.getType())) {
                        this.handleMain(player, event.getSlot(), event.getClick());
                     } else if ("preview".equals(holder.getType())) {
                        this.handlePreview(player, holder.getKitId(), event.getSlot());
                     } else if ("editor".equals(holder.getType())) {
                        this.handleEditor(player, event.getInventory(), holder.getKitId(), event.getSlot());
                     }

                  }
               }
            }
         }
      }
   }

   private void handleEditor(Player player, Inventory inv, String kitId, int slot) {
      Kit kit = this.host.getManager().get(kitId);
      if (kit == null) {
         player.closeInventory();
      } else if (slot >= 45) {
         if (slot == 45) {
            List<ItemStack> imported = new ArrayList();

            for(ItemStack item : player.getInventory().getStorageContents()) {
               if (item != null && !item.getType().isAir()) {
                  imported.add(item.clone());
               }
            }

            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (offhand != null && !offhand.getType().isAir()) {
               imported.add(offhand.clone());
            }

            for(ItemStack item : player.getInventory().getArmorContents()) {
               if (item != null && !item.getType().isAir()) {
                  imported.add(item.clone());
               }
            }

            for(int i = 0; i < 45; ++i) {
               inv.setItem(i, (ItemStack)null);
            }

            int i = 0;

            for(ItemStack item : imported) {
               if (i >= 45) {
                  break;
               }

               inv.setItem(i++, item);
            }

         } else if (slot == 47) {
            player.closeInventory();
            player.sendMessage(TextUtil.color("&cChanges discarded."));
         } else if (slot == 49) {
            List<ItemStack> items = new ArrayList();

            for(int i = 0; i < 45; ++i) {
               ItemStack item = inv.getItem(i);
               if (item != null && !item.getType().isAir()) {
                  items.add(item.clone());
               }
            }

            kit.setItems(items);
            this.host.getManager().saveKit(kit);
            player.sendMessage(TextUtil.color("&aKit saved."));
            this.host.getGuiManager().openEditor(player, kit);
         } else if (slot == 51) {
            this.host.getManager().delete(kitId);
            player.closeInventory();
            player.sendMessage(TextUtil.color("&cKit deleted."));
         } else {
            if (slot == 53) {
               List<ItemStack> items = new ArrayList();

               for(int i = 0; i < 45; ++i) {
                  ItemStack item = inv.getItem(i);
                  if (item != null && !item.getType().isAir()) {
                     items.add(item.clone());
                  }
               }

               kit.setItems(items);
               this.host.getManager().saveKit(kit);
               this.cooldownInput.put(player.getUniqueId(), kitId);
               SignInput.open(this.host.getCore(), player, List.of("", "^^^^^^^^^^^^^^", "Seconds", ""), false, () -> {
                  this.cooldownInput.remove(player.getUniqueId());
                  this.host.getGuiManager().openEditor(player, kit);
               }, 600L, () -> this.cooldownInput.remove(player.getUniqueId()));
            }

         }
      }
   }

   @EventHandler
   public void onCooldownSign(SignChangeEvent event) {
      Player player = event.getPlayer();
      String kitId = (String)this.cooldownInput.remove(player.getUniqueId());
      if (kitId != null && SignInput.isActive(player.getUniqueId(), this.host.getCore())) {
         SignInput.cleanup(player);
         String raw = event.getLine(0).trim();

         try {
            long seconds = Long.parseLong(raw);
            if (seconds < 0L) {
               throw new NumberFormatException();
            }

            Kit kit = this.host.getManager().get(kitId);
            if (kit != null) {
               kit.setCooldownSeconds(seconds);
               this.host.getManager().saveKit(kit);
               player.sendMessage(TextUtil.color("&aKit cooldown set to " + seconds + " seconds."));
               this.host.getGuiManager().openEditor(player, kit);
            }
         } catch (NumberFormatException var8) {
            player.sendMessage(TextUtil.color("&cEnter the cooldown in seconds."));
         }

      }
   }

   private void handleMain(Player player, int slot, ClickType click) {
      Kit target = null;

      for(Kit kit : this.host.getManager().all()) {
         int kitSlot = kit.getSlot();
         if (kitSlot == slot) {
            target = kit;
            break;
         }
      }

      if (target == null) {
         for(Kit kit : this.host.getManager().all()) {
            if (kit.getSlot() == slot) {
               target = kit;
               break;
            }
         }
      }

      if (target == null) {
         YamlConfiguration gui = YamlConfiguration.loadConfiguration(new File(this.host.getDataFolder(), "gui/main.yml"));
         ConfigurationSection sec = gui.getConfigurationSection("kits");
         if (sec != null) {
            for(String id : sec.getKeys(false)) {
               if (sec.getInt(id + ".slot", -1) == slot) {
                  target = this.host.getManager().get(id);
                  break;
               }
            }
         }
      }

      if (target != null) {
         if (click != ClickType.RIGHT && click != ClickType.SHIFT_RIGHT) {
            long left = this.host.getManager().getRemaining(player, target);
            if (!target.canClaim(player)) {
               MessageUtil.send(player, "no-permission-kit", "%kit%", target.getDisplayName());
            } else if (left > 0L) {
               MessageUtil.send(player, "kit-cooldown", "%kit%", target.getDisplayName(), "%time%", format(left));
            } else {
               if (this.host.getManager().claim(player, target.getId())) {
                  MessageUtil.send(player, "kit-received", "%kit%", target.getDisplayName());
                  player.closeInventory();
               } else {
                  MessageUtil.send(player, "kit-no-space");
               }

            }
         } else {
            this.host.getGuiManager().openPreview(player, target);
         }
      }
   }

   private void handlePreview(Player player, String kitId, int slot) {
      YamlConfiguration gui = YamlConfiguration.loadConfiguration(new File(this.host.getDataFolder(), "gui/preview.yml"));
      int claimSlot = gui.getInt("claim-button.slot", 49);
      int backSlot = gui.getInt("back-button.slot", 45);
      if (slot == backSlot) {
         this.host.getGuiManager().openMain(player);
      } else {
         if (slot == claimSlot) {
            Kit kit = this.host.getManager().get(kitId);
            if (kit == null) {
               return;
            }

            long left = this.host.getManager().getRemaining(player, kit);
            if (!kit.canClaim(player)) {
               MessageUtil.send(player, "no-permission-kit", "%kit%", kit.getDisplayName());
               return;
            }

            if (left > 0L) {
               MessageUtil.send(player, "kit-cooldown", "%kit%", kit.getDisplayName(), "%time%", format(left));
               return;
            }

            if (this.host.getManager().claim(player, kit.getId())) {
               MessageUtil.send(player, "kit-received", "%kit%", kit.getDisplayName());
               player.closeInventory();
            } else {
               MessageUtil.send(player, "kit-no-space");
            }
         }

      }
   }

   private static String format(long ms) {
      long total = (ms + 999L) / 1000L;
      long h = total / 3600L;
      long m = total % 3600L / 60L;
      long s = total % 60L;
      if (h > 0L) {
         return h + "h " + m + "m";
      } else {
         return m > 0L ? m + "m " + s + "s" : s + "s";
      }
   }
}
