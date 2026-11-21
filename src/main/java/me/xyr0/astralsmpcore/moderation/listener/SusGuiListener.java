package me.xyr0.astralsmpcore.moderation.listener;

import java.util.List;
import java.util.UUID;
import me.xyr0.astralsmpcore.moderation.ModerationModule;
import me.xyr0.astralsmpcore.moderation.SusManager;
import me.xyr0.astralsmpcore.moderation.gui.SusMenus;
import me.xyr0.astralsmpcore.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class SusGuiListener implements Listener {
   private final ModerationModule module;

   public SusGuiListener(ModerationModule module) {
      this.module = module;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onClick(InventoryClickEvent e) {
      HumanEntity var3 = e.getWhoClicked();
      if (var3 instanceof Player player) {
         Inventory top = e.getView().getTopInventory();
         InventoryHolder holder = top.getHolder();
         if (holder instanceof SusMenus.MainHolder || holder instanceof SusMenus.FilterHolder || holder instanceof SusMenus.DetailHolder) {
            e.setCancelled(true);
            if (e.getClickedInventory() != null && e.getClickedInventory() == top) {
               if (player.hasPermission("astralsmpcore.system.moderation.sus")) {
                  SusManager manager = this.module.getSusManager();
                  if (manager != null) {
                     FileConfiguration cfg = this.module.getCfg();
                     int slot = e.getRawSlot();
                     ItemStack current = e.getCurrentItem();
                     if (current != null && !current.getType().isAir()) {
                        if (!(holder instanceof SusMenus.MainHolder)) {
                           if (holder instanceof SusMenus.FilterHolder) {
                              if (current.getType() == Material.ARROW) {
                                 SusMenus.openMain(player, manager, cfg, 0);
                              } else {
                                 SusManager.Anticheat selected = null;
                                 switch (current.getType()) {
                                    case BARRIER -> selected = null;
                                    case IRON_SWORD -> selected = SusManager.Anticheat.GRIM;
                                    case DIAMOND_SWORD -> selected = SusManager.Anticheat.VULCAN;
                                    case GOLDEN_SWORD -> selected = SusManager.Anticheat.MATRIX;
                                    case TOTEM_OF_UNDYING -> selected = SusManager.Anticheat.TOTEMGUARD;
                                    case NETHERITE_SWORD -> selected = SusManager.Anticheat.ANGELGUARD;
                                    default -> {
                                       return;
                                    }
                                 }

                                 manager.setFilter(player.getUniqueId(), selected);
                                 String label = selected == null ? "ALL" : selected.name();
                                 TextUtil.send(player, cfg.getString("sus.messages.filter-set", "&7Filter set to &f%filter%").replace("%filter%", label));
                                 SusMenus.openMain(player, manager, cfg, 0);
                              }
                           } else {
                              if (holder instanceof SusMenus.DetailHolder) {
                                 SusMenus.DetailHolder detail = (SusMenus.DetailHolder)holder;
                                 if (current.getType() == Material.ARROW) {
                                    SusMenus.openMain(player, manager, cfg, 0);
                                    return;
                                 }

                                 if (current.getType() == Material.RED_STAINED_GLASS_PANE) {
                                    manager.clearPlayer(detail.getTarget());
                                    TextUtil.send(player, cfg.getString("sus.messages.cleared", "&aCleared flags for %player%").replace("%player%", manager.displayName(detail.getTarget(), List.of())));
                                    SusMenus.openMain(player, manager, cfg, 0);
                                 }
                              }

                           }
                        } else {
                           SusMenus.MainHolder main = (SusMenus.MainHolder)holder;
                           int size = top.getSize();
                           int bottom = size - 9;
                           ConfigurationSectionSafe clock = slotOf(cfg, "sus.gui.clock.slot", bottom + 3);
                           ConfigurationSectionSafe star = slotOf(cfg, "sus.gui.nether-star.slot", bottom + 4);
                           ConfigurationSectionSafe back = slotOf(cfg, "sus.gui.back.slot", bottom + 0);
                           ConfigurationSectionSafe next = slotOf(cfg, "sus.gui.next.slot", bottom + 8);
                           if (slot == clock.slot) {
                              SusMenus.openFilter(player, manager, cfg);
                           } else if (slot == star.slot) {
                              SusMenus.openMain(player, manager, cfg, main.getPage());
                           } else if (slot == back.slot) {
                              SusMenus.openMain(player, manager, cfg, Math.max(0, main.getPage() - 1));
                           } else if (slot == next.slot) {
                              SusMenus.openMain(player, manager, cfg, main.getPage() + 1);
                           } else {
                              if (slot < bottom && current.getType() == Material.PLAYER_HEAD) {
                                 SkullMeta meta = (SkullMeta)current.getItemMeta();
                                 if (meta != null && meta.getOwningPlayer() != null) {
                                    UUID id = meta.getOwningPlayer().getUniqueId();
                                    String name = meta.getOwningPlayer().getName();
                                    if (name == null || name.isEmpty()) {
                                       OfflinePlayer offline = Bukkit.getOfflinePlayer(id);
                                       name = offline.getName() != null ? offline.getName() : null;
                                    }

                                    if (e.isRightClick()) {
                                       if (name != null && !name.isEmpty()) {
                                          player.closeInventory();
                                          player.performCommand("gtp " + name);
                                       }

                                       return;
                                    }

                                    SusMenus.openDetail(player, manager, cfg, id);
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

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onDrag(InventoryDragEvent e) {
      InventoryHolder holder = e.getView().getTopInventory().getHolder();
      if (holder instanceof SusMenus.MainHolder || holder instanceof SusMenus.FilterHolder || holder instanceof SusMenus.DetailHolder) {
         e.setCancelled(true);
      }

   }

   private static ConfigurationSectionSafe slotOf(FileConfiguration cfg, String path, int def) {
      return new ConfigurationSectionSafe(cfg.getInt(path, def));
   }

   private static record ConfigurationSectionSafe(int slot) {
   }
}
