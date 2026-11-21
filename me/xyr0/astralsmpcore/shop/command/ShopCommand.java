package me.xyr0.astralsmpcore.shop.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.xyr0.astralsmpcore.shop.AstralShop;
import me.xyr0.astralsmpcore.shop.gui.ShopMenus;
import me.xyr0.astralsmpcore.shop.gui.ShopPageHolder;
import me.xyr0.astralsmpcore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ShopCommand implements CommandExecutor, TabCompleter {
   private final AstralShop plugin;

   public ShopCommand(AstralShop plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0) {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (sub.equals("reload") || sub.equals("addhanditem") || sub.equals("addhandcategory") || sub.equals("removeitem") || sub.equals("addpage")) {
            return this.handleAdmin(sender, args);
         }
      }

      if (sender instanceof Player player) {
         if (!player.hasPermission("astralsmpcore.system.shop.use")) {
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission")));
            return true;
         } else {
            String shop = "main";
            if (args.length > 0) {
               String asked = args[0].toLowerCase(Locale.ROOT);
               if (this.plugin.getShops().hasShop(asked)) {
                  shop = asked;
               }
            }

            ShopMenus.open(player, shop);
            return true;
         }
      } else {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("only-players")));
         return true;
      }
   }

   private boolean handleAdmin(CommandSender sender, String[] args) {
      if (!sender.hasPermission("astralsmpcore.system.shop.admin")) {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission")));
         return true;
      } else {
         switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
               this.plugin.reloadAll();
               sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("reloaded")));
               break;
            case "addhanditem":
               this.addHandItem(sender, args);
               break;
            case "addhandcategory":
               this.addHandCategory(sender, args);
               break;
            case "removeitem":
               this.removeItem(sender, args);
               break;
            case "addpage":
               this.addPage(sender, args);
         }

         return true;
      }
   }

   private void addHandItem(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 4) {
            sender.sendMessage(ColorUtil.parse("&c/shop addhanditem <category> <price> <slot>"));
         } else {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand != null && !hand.getType().isAir()) {
               String category = args[1].toLowerCase(Locale.ROOT);
               if (!category.equals("main") && this.plugin.getShops().hasShop(category)) {
                  double price;
                  int slot;
                  try {
                     price = Double.parseDouble(args[2]);
                     slot = Integer.parseInt(args[3]);
                  } catch (NumberFormatException var15) {
                     sender.sendMessage(ColorUtil.parse("&cInvalid price or slot."));
                     return;
                  }

                  List<String> pages = new ArrayList();

                  String next;
                  for(String current = category; current != null && this.plugin.getShops().hasShop(current) && !pages.contains(current); current = next != null && !next.isBlank() ? next : null) {
                     pages.add(current);
                     next = this.plugin.getShops().getShop(current).getString("pagination.next-page", "");
                  }

                  if (pages.size() <= 1) {
                     this.addHandItemToPage(sender, hand, category, price, slot);
                  } else {
                     Inventory inv = Bukkit.createInventory(new ShopPageHolder(hand, price, slot, pages), 27, ColorUtil.parse("&8Choose shop page"));

                     for(int i = 0; i < pages.size() && i < 27; ++i) {
                        ItemStack pageItem = new ItemStack(Material.PAPER);
                        ItemMeta meta = pageItem.getItemMeta();
                        if (meta != null) {
                           LegacyComponentSerializer var10001 = LegacyComponentSerializer.legacySection();
                           Object var10002 = pages.get(i);
                           meta.setDisplayName(var10001.serialize(ColorUtil.parse("&b" + (String)var10002)));
                           meta.setLore(List.of(LegacyComponentSerializer.legacySection().serialize(ColorUtil.parse("&7Click to add the item here"))));
                           pageItem.setItemMeta(meta);
                        }

                        inv.setItem(i, pageItem);
                     }

                     player.openInventory(inv);
                  }
               } else {
                  sender.sendMessage(ColorUtil.parse("&cCategory not found."));
               }
            } else {
               sender.sendMessage(ColorUtil.parse("&cHold an item in your hand."));
            }
         }
      } else {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("only-players")));
      }
   }

   private void addPage(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(ColorUtil.parse("&c/shop addpage <category>"));
      } else {
         String category = args[1].toLowerCase(Locale.ROOT);
         if (!category.equals("main") && this.plugin.getShops().hasShop(category)) {
            FileConfiguration current = this.plugin.getShops().getShop(category);
            String next = current.getString("pagination.next-page", "");
            if (next != null && !next.isBlank() && this.plugin.getShops().hasShop(next)) {
               sender.sendMessage(ColorUtil.parse("&cThis category already has a next page: &f" + next));
            } else {
               int pageNumber = 2;

               String pageId;
               do {
                  pageId = category + "-page" + pageNumber++;
               } while(this.plugin.getShops().hasShop(pageId));

               FileConfiguration page = this.plugin.getShops().createCategory(pageId, 27);
               String var10002 = current.getString("title", "&8" + category);
               page.set("title", var10002 + " &7(Page " + (pageNumber - 1) + ")");
               page.set("back-slot", 18);
               page.set("back-open", category);
               page.set("pagination.previous-page", category);
               page.set("pagination.previous-slot", 25);
               page.set("pagination.next-slot", 26);
               current.set("pagination.next-page", pageId);
               current.set("pagination.next-slot", 26);
               current.set("pagination.previous-slot", 25);
               this.plugin.getShops().saveShop(category);
               this.plugin.getShops().saveShop(pageId);
               sender.sendMessage(ColorUtil.parse("&aCreated shop page &f" + pageId + " &afor &f" + category + "&a."));
            }
         } else {
            sender.sendMessage(ColorUtil.parse("&cCategory not found. Use an existing shop category."));
         }
      }
   }

   public void addHandItemToPage(CommandSender sender, ItemStack hand, String category, double price, int slot) {
      FileConfiguration cfg = this.plugin.getShops().getShop(category);
      if (cfg == null) {
         sender.sendMessage(ColorUtil.parse("&cCategory not found."));
      } else {
         int size = Math.max(9, Math.min(54, cfg.getInt("size", 27)));
         if (slot >= 0 && slot < size) {
            String path = "items." + slot;
            cfg.set(path + ".material", hand.getType().name());
            cfg.set(path + ".name", (Object)null);
            cfg.set(path + ".lore", (Object)null);
            if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
               String display;
               try {
                  display = LegacyComponentSerializer.legacySection().serialize(hand.getItemMeta().displayName()).replace('§', '&');
               } catch (Exception var15) {
                  display = hand.getItemMeta().getDisplayName().replace('§', '&');
               }

               cfg.set(path + ".name", display);
            }

            if (hand.hasItemMeta() && hand.getItemMeta().hasLore()) {
               List<String> loreLines = new ArrayList();

               try {
                  LegacyComponentSerializer ser = LegacyComponentSerializer.legacySection();

                  for(Component line : hand.getItemMeta().lore()) {
                     if (line != null) {
                        loreLines.add(ser.serialize(line).replace('§', '&'));
                     }
                  }
               } catch (Exception var16) {
                  List<String> raw = hand.getItemMeta().getLore();
                  if (raw != null) {
                     for(String line : raw) {
                        loreLines.add(line.replace('§', '&'));
                     }
                  }
               }

               if (!loreLines.isEmpty()) {
                  cfg.set(path + ".lore", loreLines);
               }
            }

            cfg.set(path + ".price", price);
            cfg.set(path + ".amount", Math.max(1, hand.getAmount()));
            cfg.set(path + ".enchantments", (Object)null);
            cfg.set(path + ".potion-type", (Object)null);
            cfg.set(path + ".potion-effects", (Object)null);
            ConfigurationSection section = cfg.getConfigurationSection(path);
            if (section == null) {
               section = cfg.createSection(path);
            }

            ShopMenus.writeExtras(hand, section);
            this.plugin.getShops().saveShop(category);
            sender.sendMessage(ColorUtil.parse("&aAdded item to &f" + category + " &aslot &f" + slot));
         } else {
            sender.sendMessage(ColorUtil.parse("&cSlot must be between 0 and " + (size - 1) + "."));
         }
      }
   }

   private void addHandCategory(CommandSender sender, String[] args) {
      if (sender instanceof Player player) {
         if (args.length < 3) {
            sender.sendMessage(ColorUtil.parse("&c/shop addhandcategory <category> <slot>"));
         } else {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand != null && !hand.getType().isAir()) {
               String category = args[1].toLowerCase(Locale.ROOT);

               int slot;
               try {
                  slot = Integer.parseInt(args[2]);
               } catch (NumberFormatException var12) {
                  sender.sendMessage(ColorUtil.parse("&cInvalid slot."));
                  return;
               }

               if (this.plugin.getShops().hasShop(category) && !category.equals("main")) {
                  FileConfiguration main = this.plugin.getShops().getShop("main");
                  if (main == null) {
                     sender.sendMessage(ColorUtil.parse("&cMain shop menu not found."));
                  } else {
                     String path = "items." + slot;
                     main.set(path + ".material", hand.getType().name());
                     String var10000 = this.pretty(hand.getType());
                     String display = "&e" + var10000;
                     if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                        try {
                           display = LegacyComponentSerializer.legacySection().serialize(hand.getItemMeta().displayName()).replace('§', '&');
                        } catch (Exception var11) {
                           display = hand.getItemMeta().getDisplayName().replace('§', '&');
                        }
                     }

                     main.set(path + ".name", display);
                     main.set(path + ".lore", List.of("&fClick to open"));
                     main.set(path + ".open", category);
                     main.set(path + ".price", (Object)null);
                     main.set(path + ".amount", (Object)null);
                     main.set(path + ".enchantments", (Object)null);
                     main.set(path + ".potion-type", (Object)null);
                     main.set(path + ".potion-effects", (Object)null);
                     this.plugin.getShops().saveShop("main");
                     sender.sendMessage(ColorUtil.parse("&aAdded category &f" + category + " &ato main slot &f" + slot));
                  }
               } else {
                  sender.sendMessage(ColorUtil.parse("&cCategory not found. Create it first."));
               }
            } else {
               sender.sendMessage(ColorUtil.parse("&cHold an item in your hand."));
            }
         }
      } else {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("only-players")));
      }
   }

   private void removeItem(CommandSender sender, String[] args) {
      if (args.length < 3) {
         sender.sendMessage(ColorUtil.parse("&c/shop removeitem <category> <slot>"));
      } else {
         String category = args[1].toLowerCase(Locale.ROOT);

         int slot;
         try {
            slot = Integer.parseInt(args[2]);
         } catch (NumberFormatException var7) {
            sender.sendMessage(ColorUtil.parse("&cInvalid slot."));
            return;
         }

         FileConfiguration cfg = this.plugin.getShops().getShop(category);
         if (cfg == null) {
            sender.sendMessage(ColorUtil.parse("&cCategory not found."));
         } else {
            String path = "items." + slot;
            if (!cfg.contains(path)) {
               sender.sendMessage(ColorUtil.parse("&cNo item in that slot."));
            } else {
               cfg.set(path, (Object)null);
               this.plugin.getShops().saveShop(category);
               sender.sendMessage(ColorUtil.parse("&aRemoved item from &f" + category + " &aslot &f" + slot));
            }
         }
      }
   }

   private String pretty(Material mat) {
      String n = mat.name().toLowerCase(Locale.ROOT).replace('_', ' ');
      StringBuilder sb = new StringBuilder();

      for(String part : n.split(" ")) {
         if (!part.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
         }
      }

      return sb.toString();
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      boolean admin = sender.hasPermission("astralsmpcore.system.shop.admin");
      if (args.length == 1) {
         String p = args[0].toLowerCase(Locale.ROOT);
         if (admin) {
            for(String s : List.of("reload", "addhanditem", "addhandcategory", "removeitem", "addpage")) {
               if (s.startsWith(p)) {
                  out.add(s);
               }
            }
         }

         for(String key : this.plugin.getShops().getShopIds()) {
            if (key.toLowerCase(Locale.ROOT).startsWith(p)) {
               out.add(key);
            }
         }
      } else if (args.length == 2 && admin) {
         String sub = args[0].toLowerCase(Locale.ROOT);
         if (sub.equals("addhanditem") || sub.equals("addhandcategory") || sub.equals("removeitem")) {
            String p = args[1].toLowerCase(Locale.ROOT);

            for(String key : this.plugin.getShops().getShopIds()) {
               if ((!key.equals("main") || !sub.equals("addhanditem")) && key.toLowerCase(Locale.ROOT).startsWith(p)) {
                  out.add(key);
               }
            }
         }
      }

      return out;
   }
}
