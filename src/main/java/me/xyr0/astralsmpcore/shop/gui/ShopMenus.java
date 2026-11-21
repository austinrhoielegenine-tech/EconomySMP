package me.xyr0.astralsmpcore.shop.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import me.xyr0.astralsmpcore.shop.AstralShop;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public final class ShopMenus {
   private ShopMenus() {
   }

   public static void open(Player player, String shopId) {
      FileConfiguration shop = AstralShop.get().getShops().getShop(shopId);
      if (shop != null) {
         String title = shop.getString("title", "&8Shop");
         int size = Math.max(9, Math.min(54, shop.getInt("size", 27)));
         if (size % 9 != 0) {
            size = 27;
         }

         Inventory inv = Bukkit.createInventory(player, size, ColorUtil.parse(title));
         ConfigurationSection items = shop.getConfigurationSection("items");
         String type = shop.getString("type", "shop");
         String symbol = shop.getString("currency-symbol", AstralShop.get().getConfig().getString("currency-symbol", "$"));
         if (items != null) {
            for(String key : items.getKeys(false)) {
               try {
                  int slot = Integer.parseInt(key);
                  if (slot >= 0 && slot < size) {
                     ConfigurationSection item = items.getConfigurationSection(key);
                     if (item != null) {
                        inv.setItem(slot, buildShopItem(item, type, symbol));
                     }
                  }
               } catch (Exception var13) {
               }
            }
         }

         if (type.equalsIgnoreCase("shop") && shop.contains("back-slot")) {
            int back = shop.getInt("back-slot", 18);
            if (back >= 0 && back < size) {
               inv.setItem(back, named(Material.RED_STAINED_GLASS_PANE, msg("back-name", "&cBack"), List.of(msg("back-lore", "&fClick to return"))));
            }
         }

         String nextPage = shop.getString("pagination.next-page", "");
         String previousPage = shop.getString("pagination.previous-page", "");
         int nextSlot = shop.getInt("pagination.next-slot", size - 1);
         int previousSlot = shop.getInt("pagination.previous-slot", size - 2);
         if (!nextPage.isBlank() && nextSlot >= 0 && nextSlot < size) {
            inv.setItem(nextSlot, named(Material.ARROW, "&aNext page", List.of("&7Click to continue")));
         }

         if (!previousPage.isBlank() && previousSlot >= 0 && previousSlot < size) {
            inv.setItem(previousSlot, named(Material.ARROW, "&ePrevious page", List.of("&7Click to go back")));
         }

         MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.SHOP);
         MenuSession.SHOP_ID.put(player.getUniqueId(), shopId);
         player.openInventory(inv);
      }
   }

   public static void openBuy(Player player, MenuSession.BuyData data) {
      MenuSession.BuyData previous = (MenuSession.BuyData)MenuSession.BUY.get(player.getUniqueId());
      boolean alreadyOpen = MenuSession.VIEW.get(player.getUniqueId()) == MenuSession.View.BUY && previous != null && player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null && player.getOpenInventory().getTopInventory().getSize() == 27 && previous.shopId.equals(data.shopId) && previous.slotKey.equals(data.slotKey);
      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.BUY);
      MenuSession.BUY.put(player.getUniqueId(), data);
      if (alreadyOpen) {
         updateBuyPreview(player, data);
      } else {
         String title = msg("buy-title", "&8ʙᴜʏɪɴɢ");
         Inventory inv = Bukkit.createInventory(player, 27, ColorUtil.parse(title));
         inv.setItem(13, buildBuyPreview(data));
         inv.setItem(9, named(Material.RED_STAINED_GLASS_PANE, msg("minus64-name", "&cRemove 64"), List.of()));
         inv.setItem(10, named(Material.RED_STAINED_GLASS_PANE, msg("minus10-name", "&cRemove 10"), List.of()));
         inv.setItem(11, named(Material.RED_STAINED_GLASS_PANE, msg("minus-name", "&cRemove 1"), List.of()));
         inv.setItem(15, named(Material.LIME_STAINED_GLASS_PANE, msg("plus-name", "&aAdd 1"), List.of()));
         inv.setItem(16, named(Material.LIME_STAINED_GLASS_PANE, msg("plus10-name", "&aAdd 10"), List.of()));
         inv.setItem(17, named(Material.LIME_STAINED_GLASS_PANE, msg("plus64-name", "&aSet to 64"), List.of()));
         inv.setItem(21, named(Material.RED_STAINED_GLASS_PANE, msg("cancel-name", "&#fc0404ᴄᴀɴᴄᴇʟ"), List.of(msg("cancel-lore", "&fClick to cancel"))));
         inv.setItem(23, named(Material.LIME_STAINED_GLASS_PANE, msg("confirm-name", "&#04fc04ᴄᴏɴғɪʀᴍ"), List.of(msg("confirm-lore", "&fClick to buy"))));
         player.openInventory(inv);
      }
   }

   public static void updateBuyPreview(Player player, MenuSession.BuyData data) {
      if (player != null && data != null) {
         Inventory top = player.getOpenInventory() != null ? player.getOpenInventory().getTopInventory() : null;
         if (top != null && top.getSize() == 27) {
            top.setItem(13, buildBuyPreview(data));
         }
      }
   }

   private static ItemStack buildBuyPreview(MenuSession.BuyData data) {
      int amt = Math.min(data.maxAmount, Math.max(1, data.amount));
      ItemStack preview = data.buildStack(amt);
      ItemMeta meta = preview.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(data.displayName));
         List<Component> lore = new ArrayList();
         lore.add(ColorUtil.parse(msg("amount-lore", "&fAmount: &a%amount%").replace("%amount%", String.valueOf(data.amount))));
         lore.add(ColorUtil.parse(msg("price-lore", "&fPrice: &a%symbol%%price%").replace("%symbol%", data.symbol == null ? "$" : data.symbol).replace("%price%", format(data.unitPrice * (double)data.amount))));
         meta.lore(lore);
         preview.setItemMeta(meta);
      }

      return preview;
   }

   public static ItemStack buildFromConfig(ConfigurationSection item) {
      Material mat = Material.STONE;

      try {
         mat = Material.valueOf(item.getString("material", "STONE").toUpperCase(Locale.ROOT));
      } catch (Exception var10) {
      }

      int amount = Math.max(1, item.getInt("amount", 1));
      ItemStack stack = new ItemStack(mat, amount);
      applyExtras(stack, item);
      ItemMeta meta = stack.getItemMeta();
      if (meta != null) {
         String name = item.getString("name");
         if (name != null && !name.isBlank()) {
            meta.displayName(ColorUtil.parse(name));
         }

         List<String> cfgLore = item.getStringList("lore");
         if (cfgLore != null && !cfgLore.isEmpty()) {
            List<Component> lore = new ArrayList();

            for(String line : cfgLore) {
               lore.add(ColorUtil.parse(line));
            }

            meta.lore(lore);
         }

         stack.setItemMeta(meta);
      }

      return stack;
   }

   private static ItemStack buildShopItem(ConfigurationSection item, String type, String symbol) {
      ItemStack stack = buildFromConfig(item);
      ItemMeta meta = stack.getItemMeta();
      if (meta != null) {
         List<Component> lore = meta.lore();
         if (lore == null) {
            lore = new ArrayList();
         } else {
            lore = new ArrayList(lore);
         }

         if (type.equalsIgnoreCase("shop") && item.contains("price")) {
            double price = item.getDouble("price");
            lore.add(ColorUtil.parse("&a" + symbol + format(price)));
         }

         if (!lore.isEmpty()) {
            meta.lore(lore);
         }

         stack.setItemMeta(meta);
      }

      return stack;
   }

   static void applyExtras(ItemStack stack, ConfigurationSection item) {
      if (item != null && stack != null) {
         ItemMeta meta = stack.getItemMeta();
         if (meta != null) {
            List<String> enchants = item.getStringList("enchantments");
            if (enchants != null) {
               for(String raw : enchants) {
                  if (raw != null && !raw.isBlank()) {
                     String[] parts = raw.split(":");
                     if (parts.length >= 1) {
                        try {
                           Enchantment ench = Enchantment.getByName(parts[0].trim().toUpperCase(Locale.ROOT));
                           if (ench == null) {
                              ench = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].trim().toLowerCase(Locale.ROOT)));
                           }

                           if (ench != null) {
                              int level = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 1;
                              meta.addEnchant(ench, Math.max(1, level), true);
                           }
                        } catch (Exception var18) {
                        }
                     }
                  }
               }
            }

            if (meta instanceof PotionMeta) {
               PotionMeta potionMeta = (PotionMeta)meta;
               String potionType = item.getString("potion-type");
               if (potionType != null && !potionType.isBlank()) {
                  try {
                     PotionType pt = PotionType.valueOf(potionType.trim().toUpperCase(Locale.ROOT));
                     potionMeta.setBasePotionType(pt);
                  } catch (Exception var17) {
                  }
               }

               List<String> effects = item.getStringList("potion-effects");
               if (effects != null && !effects.isEmpty()) {
                  for(PotionEffect existing : new ArrayList<>(potionMeta.getCustomEffects())) {
                     potionMeta.removeCustomEffect(existing.getType());
                  }

                  for(String raw : effects) {
                     if (raw != null && !raw.isBlank()) {
                        String[] parts = raw.split(":");
                        if (parts.length >= 1) {
                           try {
                              String effectName = parts[0].trim().toLowerCase(Locale.ROOT).replace(" ", "_");
                              PotionEffectType type = null;

                              try {
                                 type = (PotionEffectType)Registry.EFFECT.get(NamespacedKey.minecraft(effectName));
                              } catch (Exception var15) {
                              }

                              if (type == null) {
                                 type = PotionEffectType.getByName(parts[0].trim().toUpperCase(Locale.ROOT));
                              }

                              if (type != null) {
                                 int amplifier = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
                                 int duration = parts.length > 2 ? Integer.parseInt(parts[2].trim()) : 3600;
                                 potionMeta.addCustomEffect(new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier)), true);
                              }
                           } catch (Exception var16) {
                           }
                        }
                     }
                  }
               }

               Boolean extended = item.contains("potion-extended") ? item.getBoolean("potion-extended") : null;
               Boolean upgraded = item.contains("potion-upgraded") ? item.getBoolean("potion-upgraded") : null;
               if (extended != null || upgraded != null) {
                  try {
                     PotionType base = potionMeta.getBasePotionType();
                     if (base != null) {
                        potionMeta.setBasePotionType(base);
                     }
                  } catch (Exception var14) {
                  }
               }
            }

            stack.setItemMeta(meta);
         }
      }
   }

   public static void writeExtras(ItemStack hand, ConfigurationSection path) {
      if (hand != null && path != null) {
         ItemMeta meta = hand.getItemMeta();
         if (meta != null) {
            Map<Enchantment, Integer> enchants = meta.getEnchants();
            if (enchants != null && !enchants.isEmpty()) {
               List<String> list = new ArrayList();

               for(Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                  String key = ((Enchantment)e.getKey()).getKey().getKey().toUpperCase(Locale.ROOT);
                  list.add(key + ":" + String.valueOf(e.getValue()));
               }

               path.set("enchantments", list);
            }

            if (meta instanceof PotionMeta) {
               PotionMeta potionMeta = (PotionMeta)meta;

               try {
                  PotionType base = potionMeta.getBasePotionType();
                  if (base != null) {
                     path.set("potion-type", base.name());
                  }
               } catch (Exception var9) {
               }

               List<PotionEffect> custom = potionMeta.getCustomEffects();
               if (custom != null && !custom.isEmpty()) {
                  List<String> effects = new ArrayList();

                  for(PotionEffect effect : custom) {
                     String var10001 = effect.getType().getName();
                     effects.add(var10001 + ":" + effect.getAmplifier() + ":" + effect.getDuration());
                  }

                  path.set("potion-effects", effects);
               }
            }

         }
      }
   }

   private static ItemStack named(Material mat, String name, List<String> loreLines) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(name));
         if (loreLines != null && !loreLines.isEmpty()) {
            List<Component> lore = new ArrayList();

            for(String line : loreLines) {
               lore.add(ColorUtil.parse(line));
            }

            meta.lore(lore);
         }

         item.setItemMeta(meta);
      }

      return item;
   }

   private static String msg(String key, String def) {
      return AstralShop.get().getMessages().get(key, def);
   }

   private static String format(double value) {
      return value == (double)((long)value) ? String.valueOf((long)value) : String.format("%.2f", value);
   }
}
