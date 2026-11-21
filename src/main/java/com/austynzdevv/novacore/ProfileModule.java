package com.austynzdevv.novacore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.austynzdevv.novacore.bounties.gui.BountyMenus;
import com.austynzdevv.novacore.economy.CoreEconomy;
import com.austynzdevv.novacore.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ProfileModule implements Module, Listener {
   private final NovaCore plugin;

   public ProfileModule(NovaCore plugin) {
      this.plugin = plugin;
   }

   public String getName() {
      return "profile";
   }

   public void onEnable() {
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.plugin.commands().registerHandler("profile", this::cmd);

      try {
         PluginCommand pc = this.plugin.getCommand("profile");
         if (pc != null) {
            pc.setExecutor((sender, command, label, args) -> this.cmd(sender, args));
         }
      } catch (Exception var2) {
      }

      this.plugin.getLogger().info("Profile /profile enabled");
   }

   public void onDisable() {
   }

   public void reload() {
   }

   private Component toComp(String s) {
      return LegacyComponentSerializer.legacyAmpersand().deserialize(TextUtil.color(s == null ? "" : s));
   }

   private boolean cmd(CommandSender sender, String[] args) {
      if (sender instanceof Player p) {
         if (!p.hasPermission("novacore.system.profile.use") && !p.isOp()) {
            TextUtil.send(p, "&cNo permission.");
            return true;
         } else {
            OfflinePlayer target;
            if (args != null && args.length > 0) {
               target = Bukkit.getOfflinePlayer(args[0]);
               if (target == null || target.getName() == null && !target.hasPlayedBefore()) {
                  TextUtil.send(p, "&cPlayer not found.");
                  return true;
               }
            } else {
               target = p;
            }

            this.open(p, target);
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }

   public void open(Player viewer, OfflinePlayer target) {
      UUID tid = target.getUniqueId();
      String name = target.getName() == null ? tid.toString().substring(0, 8) : target.getName();
      Inventory inv = Bukkit.createInventory(new ProfileHolder(tid), 27, this.toComp("&8Profile &7» &f" + name));
      ItemStack filler = this.named(Material.GRAY_STAINED_GLASS_PANE, " ");

      for(int i = 0; i < 27; ++i) {
         inv.setItem(i, filler);
      }

      ItemStack glass = this.named(Material.GRAY_STAINED_GLASS_PANE, " ");

      for(int i = 0; i < 27; ++i) {
         inv.setItem(i, glass);
      }

      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta sm = (SkullMeta)head.getItemMeta();
      if (sm != null) {
         sm.setOwningPlayer(target);
         sm.displayName(this.toComp("&#00a3fb" + name));
         List<Component> lore = new ArrayList();
         lore.add(this.toComp("&7UUID: &f" + String.valueOf(tid)));
         if (target.isOnline()) {
            lore.add(this.toComp("&aOnline"));
         } else {
            lore.add(this.toComp("&cOffline"));
         }

         try {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp != null && rsp.getProvider() != null) {
               double bal = ((Economy)rsp.getProvider()).getBalance(target);
               lore.add(this.toComp("&7Balance: &#00fc88$" + CoreEconomy.formatMoney(bal)));
            }
         } catch (Exception var14) {
         }

         sm.lore(lore);
         head.setItemMeta(sm);
      }

      inv.setItem(4, head);
      inv.setItem(10, this.named(Material.RED_BED, "&#0044FCʜᴏᴍᴇꜱ", "&7Open " + name + "'s homes", "&7Click to manage beds"));
      inv.setItem(12, this.named(Material.ENDER_CHEST, "&#9b59b6Ender Chest", "&7Open " + name + "'s ender chest"));
      inv.setItem(14, this.named(Material.CHEST, "&#f1c40fInventory", "&7Open " + name + "'s inventory", "&cOnline players only"));
      inv.setItem(16, this.named(Material.GOLD_INGOT, "&#f39c12Auction", "&7Open auction admin for " + name));
      inv.setItem(20, this.named(Material.WRITABLE_BOOK, "&#3498dbOrders", "&7Open orders admin for " + name));
      inv.setItem(22, this.named(Material.SKELETON_SKULL, "&#e74c3cBounties", "&7Open bounties for " + name));
      inv.setItem(24, this.named(Material.BOOK, "&#2ecc71Stats", "&7View profile stats for " + name));
      viewer.openInventory(inv);
   }

   private ItemStack named(Material mat, String name, String... loreLines) {
      ItemStack it = new ItemStack(mat);
      ItemMeta meta = it.getItemMeta();
      if (meta != null) {
         meta.displayName(this.toComp(name));
         if (loreLines != null && loreLines.length > 0) {
            List<Component> lore = new ArrayList();

            for(String l : loreLines) {
               lore.add(this.toComp(l));
            }

            meta.lore(lore);
         }

         it.setItemMeta(meta);
      }

      return it;
   }

   private boolean isProfile(Inventory inv) {
      return inv != null && inv.getHolder() instanceof ProfileHolder;
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = false
   )
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player p) {
         if (this.isProfile(e.getView().getTopInventory()) || e.getInventory().getHolder() instanceof ProfileHolder) {
            e.setCancelled(true);
            e.setResult(Result.DENY);
            if (e.getClickedInventory() != null && e.getClickedInventory() == e.getView().getTopInventory()) {
               InventoryHolder var4 = e.getView().getTopInventory().getHolder();
               if (var4 instanceof ProfileHolder) {
                  ProfileHolder holder = (ProfileHolder)var4;
                  UUID targetId = holder.targetId;
                  OfflinePlayer target = Bukkit.getOfflinePlayer(targetId);
                  String name = target.getName() == null ? targetId.toString() : target.getName();
                  int slot = e.getRawSlot();
                  switch (slot) {
                     case 10:
                        p.closeInventory();
                        Player online = target.getPlayer();
                        if (online == null || !online.isOnline()) {
                           TextUtil.send(p, "&cPlayer must be online to view homes.");
                           return;
                        }

                        try {
                           p.performCommand("home admin " + online.getName());
                        } catch (Exception var18) {
                           TextUtil.send(p, "&cCould not open homes.");
                        }
                     case 11:
                     case 13:
                     case 15:
                     case 17:
                     case 18:
                     case 19:
                     case 21:
                     case 23:
                     default:
                        break;
                     case 12:
                        p.closeInventory();

                        try {
                           Player online2 = target.getPlayer();
                           if (online2 != null && online2.isOnline()) {
                              p.openInventory(online2.getEnderChest());
                           } else {
                              TextUtil.send(p, "&cPlayer must be online for ender chest.");
                           }
                        } catch (Exception var23) {
                           TextUtil.send(p, "&cCould not open ender chest.");
                        }
                        break;
                     case 14:
                        p.closeInventory();
                        Player online3 = target.getPlayer();
                        if (online3 == null || !online3.isOnline()) {
                           TextUtil.send(p, "&cPlayer must be online to view inventory.");
                           return;
                        }

                        p.openInventory(online3.getInventory());
                        break;
                     case 16:
                        p.closeInventory();
                        String tn = target.getName() == null ? "" : target.getName();
                        boolean ok = false;

                        for(String c : new String[]{"ah admin " + tn, "auction admin " + tn, "ahadmin " + tn}) {
                           try {
                              if (p.performCommand(c)) {
                                 ok = true;
                                 break;
                              }
                           } catch (Exception var20) {
                           }

                           try {
                              Bukkit.dispatchCommand(p, c);
                              ok = true;
                              break;
                           } catch (Exception var19) {
                           }
                        }

                        if (!ok) {
                           TextUtil.send(p, "&cCould not open auction admin. Try &f/ah admin " + tn);
                        }
                        break;
                     case 20:
                        p.closeInventory();
                        String tn2 = target.getName() == null ? "" : target.getName();
                        boolean ok2 = false;

                        for(String c : new String[]{"order admin " + tn2, "orders admin " + tn2, "orderadmin " + tn2}) {
                           try {
                              if (p.performCommand(c)) {
                                 ok2 = true;
                                 break;
                              }
                           } catch (Exception var22) {
                           }

                           try {
                              Bukkit.dispatchCommand(p, c);
                              ok2 = true;
                              break;
                           } catch (Exception var21) {
                           }
                        }

                        if (!ok2) {
                           TextUtil.send(p, "&cCould not open orders admin. Try &f/order admin " + tn2);
                        }
                        break;
                     case 22:
                        p.closeInventory();

                        try {
                           BountyMenus.openAdmin(p, target);
                        } catch (Exception var17) {
                           try {
                              p.performCommand("bounty admin " + name);
                           } catch (Exception var16) {
                              TextUtil.send(p, "&cCould not open bounties.");
                           }
                        }
                        break;
                     case 24:
                        p.closeInventory();

                        try {
                           p.performCommand("stats " + name);
                        } catch (Exception var15) {
                           TextUtil.send(p, "&cCould not open stats.");
                        }
                  }

               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = false
   )
   public void onDrag(InventoryDragEvent e) {
      if (this.isProfile(e.getView().getTopInventory()) || e.getInventory().getHolder() instanceof ProfileHolder) {
         e.setCancelled(true);
         e.setResult(Result.DENY);
      }

   }

   public static class ProfileHolder implements InventoryHolder {
      public final UUID targetId;

      public ProfileHolder(UUID targetId) {
         this.targetId = targetId;
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
