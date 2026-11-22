package com.austynzdevv.nyrethacore.shop.listener;

import java.util.Locale;
import com.austynzdevv.nyrethacore.shop.EconomyShop;
import com.austynzdevv.nyrethacore.shop.command.ShopCommand;
import com.austynzdevv.nyrethacore.shop.gui.MenuSession;
import com.austynzdevv.nyrethacore.shop.gui.ShopMenus;
import com.austynzdevv.nyrethacore.shop.gui.ShopPageHolder;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import com.austynzdevv.nyrethacore.util.SoundUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class GuiListener implements Listener {
   private final EconomyShop plugin;

   public GuiListener(EconomyShop plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         InventoryHolder var4 = event.getView().getTopInventory().getHolder();
         if (var4 instanceof ShopPageHolder holder) {
            if (event.getClickedInventory() == event.getView().getTopInventory()) {
               event.setCancelled(true);
               int slot = event.getRawSlot();
               if (slot >= 0 && slot < holder.pages().size()) {
                  (new ShopCommand(this.plugin)).addHandItemToPage(player, holder.hand(), (String)holder.pages().get(slot), holder.price(), holder.slot());
                  player.closeInventory();
               }
            } else {
               event.setCancelled(true);
            }

         } else {
            MenuSession.View view = (MenuSession.View)MenuSession.VIEW.get(player.getUniqueId());
            if (view != null) {
               boolean topClick = event.getClickedInventory() != null && event.getClickedInventory().equals(event.getView().getTopInventory());
               if (topClick) {
                  event.setCancelled(true);
                  if (view == MenuSession.View.SHOP) {
                     this.handleShop(player, event.getRawSlot());
                  } else if (view == MenuSession.View.BUY) {
                     this.handleBuy(player, event.getRawSlot());
                  }

               } else {
                  if (event.isShiftClick() || event.getClick() == ClickType.NUMBER_KEY || event.getClick() == ClickType.SWAP_OFFHAND || event.getClick() == ClickType.DOUBLE_CLICK) {
                     event.setCancelled(true);
                  }

               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onDrag(InventoryDragEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (MenuSession.VIEW.get(player.getUniqueId()) != null) {
            int topSize = event.getView().getTopInventory().getSize();

            for(int slot : event.getRawSlots()) {
               if (slot < topSize) {
                  event.setCancelled(true);
                  return;
               }
            }

         }
      }
   }

   private void handleShop(Player player, int slot) {
      String shopId = (String)MenuSession.SHOP_ID.get(player.getUniqueId());
      if (shopId != null) {
         FileConfiguration shop = this.plugin.getShops().getShop(shopId);
         if (shop != null) {
            String nextPage = shop.getString("pagination.next-page", "");
            String previousPage = shop.getString("pagination.previous-page", "");
            if (!nextPage.isBlank() && slot == shop.getInt("pagination.next-slot", this.eventSlotDefault(shop))) {
               this.play(player, "click");
               ShopMenus.open(player, nextPage);
            } else if (!previousPage.isBlank() && slot == shop.getInt("pagination.previous-slot", this.eventSlotDefault(shop) - 1)) {
               this.play(player, "click");
               ShopMenus.open(player, previousPage);
            } else if (shop.contains("back-slot") && slot == shop.getInt("back-slot")) {
               this.play(player, "click");
               String back = shop.getString("back-open", "main");
               ShopMenus.open(player, back);
            } else {
               ConfigurationSection item = shop.getConfigurationSection("items." + slot);
               if (item != null) {
                  if (item.contains("open")) {
                     this.play(player, "click");
                     ShopMenus.open(player, item.getString("open"));
                  } else if (shop.getString("type", "shop").equalsIgnoreCase("shop")) {
                     if (item.contains("price")) {
                        this.play(player, "click");
                        Material mat = Material.STONE;

                        try {
                           mat = Material.valueOf(item.getString("material", "STONE").toUpperCase());
                        } catch (Exception var18) {
                        }

                        String economy = shop.getString("economy", this.plugin.getConfig().getString("default-economy", "vault"));
                        String take = item.getString("take-command", shop.getString("take-command", this.plugin.getConfig().getString("default-take-command", "")));
                        String give = item.getString("give-command", "");
                        String symbol = shop.getString("currency-symbol", this.plugin.getConfig().getString("currency-symbol", "$"));
                        String name = item.getString("name");
                        if (name == null || name.isBlank()) {
                           name = mat.name().toLowerCase(Locale.ROOT).replace('_', ' ');
                        }

                        String balancePh = shop.getString("balance-placeholder", "");
                        ItemStack template = ShopMenus.buildFromConfig(item);
                        int maxAmount = template.getMaxStackSize();
                        if (maxAmount < 1) {
                           maxAmount = 1;
                        }

                        MenuSession.BuyData data = new MenuSession.BuyData(shopId, String.valueOf(slot), mat, name, item.getDouble("price"), take, give, economy, symbol, balancePh, maxAmount, template);
                        ShopMenus.openBuy(player, data);
                     }
                  }
               }
            }
         }
      }
   }

   private int eventSlotDefault(FileConfiguration shop) {
      return Math.max(0, shop.getInt("size", 27) - 1);
   }

   private void handleBuy(Player player, int slot) {
      MenuSession.BuyData data = (MenuSession.BuyData)MenuSession.BUY.get(player.getUniqueId());
      if (data != null) {
         if (slot == 9) {
            data.amount = 1;
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 10) {
            data.amount = Math.max(1, data.amount - 10);
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 11) {
            data.amount = Math.max(1, data.amount - 1);
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 15) {
            data.amount = Math.min(data.maxAmount, data.amount + 1);
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 16) {
            data.amount = Math.min(data.maxAmount, data.amount + 10);
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 17) {
            data.amount = data.maxAmount;
            this.play(player, "click");
            ShopMenus.openBuy(player, data);
         } else if (slot == 21) {
            this.play(player, "click");
            ShopMenus.open(player, data.shopId);
         } else if (slot == 23) {
            double total = data.unitPrice * (double)data.amount;
            boolean charged = this.plugin.getEconomy().charge(player, data.economy, data.takeCommand, total, data.amount, data.balancePlaceholder);
            if (!charged) {
               this.play(player, "fail");
               String key = data.economy != null && data.economy.equalsIgnoreCase("command") ? "not-enough-shards" : "not-enough";
               String def = key.equals("not-enough-shards") ? "&cYou don't have enough shards!" : "&cYou don't have enough money.";
               player.sendMessage(ColorUtil.parse(this.msg(key, def)));
            } else {
               if (data.economy == null || data.economy.equalsIgnoreCase("vault")) {
                  this.plugin.getSpend().addVaultSpend(player.getUniqueId(), total);
               }

               ItemStack stack = data.buildStack(data.amount);
               this.plugin.getEconomy().giveReward(player, data.giveCommand, data.displayName, data.amount, stack);
               this.play(player, "buy");
               String var10000 = this.msg("bought", "&aBought &f%amount%x %item% &afor &f%price%").replace("%amount%", String.valueOf(data.amount)).replace("%item%", data.displayName);
               String var10002 = data.symbol == null ? "$" : data.symbol;
               String bought = var10000.replace("%price%", var10002 + this.format(total));
               player.sendMessage(ColorUtil.parse(bought));
               ShopMenus.openBuy(player, data);
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (MenuSession.VIEW.get(player.getUniqueId()) != null) {
            Tasks.run(this.plugin.getCore(), () -> {
               if (!player.isOnline()) {
                  MenuSession.clear(player.getUniqueId());
               } else {
                  int topSize = player.getOpenInventory().getTopInventory().getSize();
                  if (topSize <= 5 || !MenuSession.VIEW.containsKey(player.getUniqueId())) {
                     MenuSession.clear(player.getUniqueId());
                  }
               }
            });
         }
      }
   }

   private void play(Player player, String key) {
      if (this.plugin.getConfig().getBoolean("sounds.enabled", true)) {
         String name = this.plugin.getConfig().getString("sounds." + key, "");
         if (name != null && !name.isBlank()) {
            try {
               Sound sound = Sound.valueOf(name.toUpperCase());
               float volume = (float)this.plugin.getConfig().getDouble("sounds.volume", (double)1.0F);
               float pitch = (float)this.plugin.getConfig().getDouble("sounds.pitch", (double)1.0F);
               SoundUtil.play(player, player.getLocation(), sound, volume, pitch);
            } catch (Exception var7) {
            }

         }
      }
   }

   private String msg(String key, String def) {
      return this.plugin.getMessages().get(key, def);
   }

   private String format(double value) {
      return value == (double)((long)value) ? String.valueOf((long)value) : String.format("%.2f", value);
   }
}
