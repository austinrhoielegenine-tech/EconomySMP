package com.austynzdevv.nyrethacore.shop.command;

import java.util.ArrayList;
import java.util.List;
import com.austynzdevv.nyrethacore.shop.EconomyShop;
import com.austynzdevv.nyrethacore.shop.gui.ShopMenus;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AdminShopCommand implements CommandExecutor, TabCompleter {
   private final EconomyShop plugin;

   public AdminShopCommand(EconomyShop plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("nyrethacorecore.system.shop.admin")) {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission")));
         return true;
      } else if (args.length == 0) {
         this.help(sender);
         return true;
      } else {
         switch (args[0].toLowerCase()) {
            case "createshop":
               if (args.length < 2) {
                  sender.sendMessage(ColorUtil.parse("&c/adminshop createshop <id> [size]"));
                  return true;
               }

               String id = args[1].toLowerCase();
               int size = args.length > 2 ? this.parseSize(args[2]) : 27;
               if (this.plugin.getShops().hasShop(id)) {
                  sender.sendMessage(ColorUtil.parse("&cShop already exists."));
                  return true;
               }

               this.plugin.getShops().createCategory(id, size);
               sender.sendMessage(ColorUtil.parse("&aCreated category &f" + id + " &ain categories/" + id + ".yml"));
               break;
            case "additem":
               if (!(sender instanceof Player)) {
                  sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("only-players")));
                  return true;
               }

               Player player = (Player)sender;
               if (args.length < 4) {
                  sender.sendMessage(ColorUtil.parse("&c/adminshop additem <shop> <slot> <price>"));
                  return true;
               }

               ItemStack hand = player.getInventory().getItemInMainHand();
               if (hand == null || hand.getType().isAir()) {
                  sender.sendMessage(ColorUtil.parse("&cHold an item in your hand."));
                  return true;
               }

               String shop = args[1].toLowerCase();
               FileConfiguration cfg = this.plugin.getShops().getShop(shop);
               if (cfg == null || shop.equals("main")) {
                  sender.sendMessage(ColorUtil.parse("&cCategory not found."));
                  return true;
               }

               int slot;
               double price;
               try {
                  slot = Integer.parseInt(args[2]);
                  price = Double.parseDouble(args[3]);
               } catch (NumberFormatException var20) {
                  sender.sendMessage(ColorUtil.parse("&cInvalid slot or price."));
                  return true;
               }

               String path = "items." + slot;
               cfg.set(path + ".material", hand.getType().name());
               cfg.set(path + ".name", (Object)null);
               cfg.set(path + ".lore", (Object)null);
               if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                  String display = LegacyComponentSerializer.legacySection().serialize(hand.getItemMeta().displayName()).replace('§', '&');
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
                  } catch (Exception var21) {
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
               this.plugin.getShops().saveShop(shop);
               sender.sendMessage(ColorUtil.parse("&aAdded item to &f" + shop + " &aslot &f" + slot));
               break;
            case "seteconomy":
               if (args.length < 3) {
                  sender.sendMessage(ColorUtil.parse("&c/adminshop seteconomy <shop> <vault|command>"));
                  return true;
               }

               String shop2 = args[1].toLowerCase();
               FileConfiguration cfg2 = this.plugin.getShops().getShop(shop2);
               if (cfg2 == null || shop2.equals("main")) {
                  sender.sendMessage(ColorUtil.parse("&cCategory not found."));
                  return true;
               }

               cfg2.set("economy", args[2].toLowerCase());
               this.plugin.getShops().saveShop(shop2);
               sender.sendMessage(ColorUtil.parse("&aSet economy of &f" + shop2 + " &ato &f" + args[2].toLowerCase()));
               break;
            case "settake":
               if (args.length < 3) {
                  sender.sendMessage(ColorUtil.parse("&c/adminshop settake <shop> <command...>"));
                  return true;
               }

               String shop3 = args[1].toLowerCase();
               FileConfiguration cfg3 = this.plugin.getShops().getShop(shop3);
               if (cfg3 == null || shop3.equals("main")) {
                  sender.sendMessage(ColorUtil.parse("&cCategory not found."));
                  return true;
               }

               StringBuilder sb = new StringBuilder();

               for(int i = 2; i < args.length; ++i) {
                  if (i > 2) {
                     sb.append(' ');
                  }

                  sb.append(args[i]);
               }

               cfg3.set("take-command", sb.toString());
               cfg3.set("economy", "command");
               this.plugin.getShops().saveShop(shop3);
               sender.sendMessage(ColorUtil.parse("&aTake command set for &f" + shop3));
               break;
            case "list":
               sender.sendMessage(ColorUtil.parse("&6Shops:"));

               for(String key : this.plugin.getShops().getShopIds()) {
                  sender.sendMessage(ColorUtil.parse("&7- &f" + key));
               }
               break;
            default:
               this.help(sender);
         }

         return true;
      }
   }

   private void help(CommandSender sender) {
      sender.sendMessage(ColorUtil.parse("&6EconomyShop Admin"));
      sender.sendMessage(ColorUtil.parse("&e/adminshop createshop <id> [size]"));
      sender.sendMessage(ColorUtil.parse("&e/adminshop additem <shop> <slot> <price>"));
      sender.sendMessage(ColorUtil.parse("&e/adminshop seteconomy <shop> <vault|command>"));
      sender.sendMessage(ColorUtil.parse("&e/adminshop settake <shop> <command...>"));
      sender.sendMessage(ColorUtil.parse("&e/adminshop list"));
   }

   private int parseSize(String raw) {
      try {
         int size = Integer.parseInt(raw);
         return size % 9 != 0 ? 27 : Math.max(9, Math.min(54, size));
      } catch (Exception var3) {
         return 27;
      }
   }

   private String pretty(Material mat) {
      String n = mat.name().toLowerCase().replace('_', ' ');
      String[] parts = n.split(" ");
      StringBuilder sb = new StringBuilder();

      for(String p : parts) {
         if (!p.isEmpty()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
         }
      }

      return sb.toString();
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1) {
         for(String s : List.of("createshop", "additem", "seteconomy", "settake", "list")) {
            if (s.startsWith(args[0].toLowerCase())) {
               out.add(s);
            }
         }
      } else if (args.length == 2) {
         for(String key : this.plugin.getShops().getShopIds()) {
            if (key.startsWith(args[1].toLowerCase())) {
               out.add(key);
            }
         }
      }

      return out;
   }
}
