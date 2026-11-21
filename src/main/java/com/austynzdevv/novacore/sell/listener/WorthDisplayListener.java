package com.austynzdevv.novacore.sell.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.SettingsModule;
import com.austynzdevv.novacore.order.EconomyOrder;
import com.austynzdevv.novacore.order.Order;
import com.austynzdevv.novacore.sell.EconomySell;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class WorthDisplayListener implements Listener {
   private static final long LORE_REMOVAL_MS = 100L;
   private static final long UPDATE_INTERVAL_TICKS = 100L;
   private static final long BLOCK_BREAK_COOLDOWN_TICKS = 50L;
   private final EconomySell plugin;
   private final NamespacedKey markerKey;
   private final NamespacedKey loreCountKey;
   private final Set<UUID> disabledWorth = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Long> loreRemovalUntil = new ConcurrentHashMap();
   private final Map<UUID, Long> blockBreakCooldown = new ConcurrentHashMap();
   private final Set<UUID> breakingBlock = ConcurrentHashMap.newKeySet();
   private final Map<UUID, Boolean> inBlockedGui = new ConcurrentHashMap();
   private final Map<UUID, Boolean> nearlyFullCache = new ConcurrentHashMap();
   private int updateTimerId = -1;
   private List<String> blockedGuiTitles = new ArrayList();

   public WorthDisplayListener(EconomySell plugin) {
      this.plugin = plugin;
      this.markerKey = new NamespacedKey("novacorecore", "worth_lore");
      this.loreCountKey = new NamespacedKey("novacorecore", "worth_lore_lines");
      this.reloadConfigValues();
      this.startUpdateTimer();
   }

   public void reloadConfigValues() {
      List<String> list = this.plugin.getConfig().getStringList("worth-lore.dont-show-gui");
      if (list == null || list.isEmpty()) {
         list = this.plugin.getConfig().getStringList("worth-lore.guis");
      }

      if (list == null || list.isEmpty()) {
         list = this.plugin.getConfig().getStringList("don-t-show-gui");
      }

      this.blockedGuiTitles = list != null ? new ArrayList(list) : new ArrayList();
   }

   private boolean enabled() {
      return this.plugin.getConfig().getBoolean("worth-lore.enabled", true);
   }

   private boolean playerInvEnabled() {
      return this.plugin.getConfig().getBoolean("worth-lore.player-inventory", true);
   }

   public boolean isWorthEnabled(Player player) {
      if (player == null) {
         return false;
      } else if (SettingsModule.isWorthDisabled(player.getUniqueId())) {
         return false;
      } else {
         return !this.disabledWorth.contains(player.getUniqueId());
      }
   }

   public boolean toggleWorth(Player player) {
      UUID id = player.getUniqueId();
      if (this.disabledWorth.contains(id)) {
         this.disabledWorth.remove(id);
         this.updatePlayerInventory(player);
         return true;
      } else {
         this.disabledWorth.add(id);
         this.removeAllWorthLores(player);
         player.updateInventory();
         return false;
      }
   }

   public void refresh(Player player) {
      if (player != null && player.isOnline()) {
         this.removeAllWorthLores(player);
         this.updatePlayerInventory(player);
         player.updateInventory();
      }
   }

   private void startUpdateTimer() {
      if (this.updateTimerId != -1) {
         Tasks.cancel(this.updateTimerId);
         this.updateTimerId = -1;
      }

      this.updateTimerId = Tasks.runTimer(this.plugin.getCore(), new Runnable() {
         public void run() {
            if (WorthDisplayListener.this.enabled()) {
               for(Player player : Bukkit.getOnlinePlayers()) {
                  if (!WorthDisplayListener.this.isInBlockBreakCooldown(player) && !WorthDisplayListener.this.breakingBlock.contains(player.getUniqueId())) {
                     WorthDisplayListener.this.updatePlayerInventory(player);
                  }
               }

            }
         }
      }, 40L, 100L);
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      if (this.enabled()) {
         final Player player = event.getPlayer();
         Tasks.runLater(this.plugin.getCore(), new Runnable() {
            public void run() {
               if (player.isOnline()) {
                  WorthDisplayListener.this.updatePlayerInventory(player);
               }

            }
         }, 5L);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      UUID id = event.getPlayer().getUniqueId();
      this.loreRemovalUntil.remove(id);
      this.blockBreakCooldown.remove(id);
      this.breakingBlock.remove(id);
      this.inBlockedGui.remove(id);
      this.nearlyFullCache.remove(id);
      this.removeAllWorthLores(event.getPlayer());
   }

   @EventHandler
   public void onGameModeChange(PlayerGameModeChangeEvent event) {
      if (this.enabled()) {
         final Player player = event.getPlayer();
         Tasks.runLater(this.plugin.getCore(), new Runnable() {
            public void run() {
               if (player.isOnline()) {
                  WorthDisplayListener.this.removeAllWorthLores(player);
                  WorthDisplayListener.this.updatePlayerInventory(player);
               }

            }
         }, 1L);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPickup(EntityPickupItemEvent event) {
      if (this.enabled() && event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         ItemStack ground = event.getItem().getItemStack();
         if (ground != null) {
            this.stripLore(ground);
            event.getItem().setItemStack(ground);
         }

         this.activateLoreRemoval(player);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent event) {
      if (this.enabled()) {
         final Player player = event.getPlayer();
         this.breakingBlock.add(player.getUniqueId());
         this.removeAllWorthLores(player);
         this.setBlockBreakCooldown(player);
         Tasks.runLater(this.plugin.getCore(), new Runnable() {
            public void run() {
               WorthDisplayListener.this.breakingBlock.remove(player.getUniqueId());
            }
         }, 5L);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onClick(InventoryClickEvent event) {
      if (this.enabled() && event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         ItemStack current = event.getCurrentItem();
         if (current != null && !current.getType().isAir()) {
            this.stripLore(current);
         }

         ItemStack cursor = event.getCursor();
         if (cursor != null && !cursor.getType().isAir()) {
            this.stripLore(cursor);
         }

         if (event.getHotbarButton() >= 0) {
            ItemStack hot = player.getInventory().getItem(event.getHotbarButton());
            if (hot != null) {
               this.stripLore(hot);
            }
         }

         if (event.getClickedInventory() != null && !(event.getClickedInventory() instanceof PlayerInventory)) {
            this.stripInventory(event.getClickedInventory());
         }

         this.activateLoreRemoval(player);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onDrag(InventoryDragEvent event) {
      if (this.enabled() && event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         ItemStack oldCursor = event.getOldCursor();
         if (oldCursor != null) {
            this.stripLore(oldCursor);
         }

         for(ItemStack stack : event.getNewItems().values()) {
            if (stack != null) {
               this.stripLore(stack);
            }
         }

         this.activateLoreRemoval(player);
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onOpen(InventoryOpenEvent event) {
      if (this.enabled() && event.getPlayer() instanceof Player) {
         Player player = (Player)event.getPlayer();
         String title = event.getView().getTitle();
         boolean blocked = false;
         if (title != null) {
            String plainTitle = ChatColorStrip(title);

            for(String blockedTitle : this.blockedGuiTitles) {
               if (blockedTitle != null && !blockedTitle.isEmpty()) {
                  if (title.contains(blockedTitle)) {
                     blocked = true;
                     break;
                  }

                  String plainBlocked = PlainTextComponentSerializer.plainText().serialize(ColorUtil.parse(blockedTitle));
                  if (plainTitle != null && plainBlocked != null && !plainBlocked.isEmpty() && plainTitle.contains(plainBlocked)) {
                     blocked = true;
                     break;
                  }

                  String strippedConfig = ChatColorStrip(blockedTitle);
                  if (plainTitle != null && strippedConfig != null && !strippedConfig.isEmpty() && plainTitle.contains(strippedConfig)) {
                     blocked = true;
                     break;
                  }
               }
            }
         }

         this.inBlockedGui.put(player.getUniqueId(), blocked);
         InventoryType type = event.getInventory().getType();
         if (this.isContainer(type)) {
            this.stripInventory(event.getInventory());
         }

         this.updatePlayerInventory(player);
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      if (this.enabled() && event.getPlayer() instanceof Player) {
         final Player player = (Player)event.getPlayer();
         this.inBlockedGui.remove(player.getUniqueId());
         this.stripInventory(event.getInventory());
         Tasks.runLater(this.plugin.getCore(), new Runnable() {
            public void run() {
               if (player.isOnline()) {
                  WorthDisplayListener.this.updatePlayerInventory(player);
               }

            }
         }, 1L);
      }
   }

   private void updatePlayerInventory(Player player) {
      if (player != null && player.isOnline()) {
         if (this.enabled() && this.playerInvEnabled()) {
            if (!this.isInBlockBreakCooldown(player) && !this.breakingBlock.contains(player.getUniqueId())) {
               try {
                  this.updateNearlyFullCache(player);
                  boolean showWorth = !Boolean.TRUE.equals(this.inBlockedGui.get(player.getUniqueId()));
                  boolean loreRemovalActive = this.isLoreRemovalActive(player);
                  boolean worthToggleEnabled = this.isWorthEnabled(player);
                  boolean inventoryFull = this.shouldDisableDueToFullInventory(player);
                  boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
                  boolean shouldAddLore = showWorth && !loreRemovalActive && worthToggleEnabled && !inventoryFull && !isCreative;
                  ItemStack[] contents = player.getInventory().getContents();

                  for(int i = 0; i < contents.length; ++i) {
                     ItemStack item = contents[i];
                     if (item != null && !item.getType().isAir()) {
                        this.updateItem(player, item, shouldAddLore);
                     }
                  }

                  ItemStack offHand = player.getInventory().getItemInOffHand();
                  if (offHand != null && !offHand.getType().isAir()) {
                     this.updateItem(player, offHand, shouldAddLore);
                  }

                  ItemStack cursor = player.getItemOnCursor();
                  if (cursor != null && !cursor.getType().isAir()) {
                     this.updateItem(player, cursor, shouldAddLore);
                  }
               } catch (Exception var11) {
               }

            }
         } else {
            this.removeAllWorthLores(player);
         }
      }
   }

   private void updateItem(Player player, ItemStack item, boolean addLore) {
      if (item != null && !item.getType().isAir()) {
         if (!item.getType().name().contains("STAINED_GLASS_PANE")) {
            boolean hasLore = this.hasWorthMarker(item);
            double total = (double)0.0F;
            boolean hasPrice = false;
            if (addLore && !this.plugin.getBlacklist().isBlocked(item)) {
               total = this.plugin.getPrices().getSellPrice(player, item);
               if (this.plugin.getConfig().getBoolean("Integrate-order", false)) {
                  try {
                     EconomyOrder orders = EconomyOrder.get();
                     if (orders != null && orders.getOrderManager() != null) {
                        Order best = null;
                        double bestEach = (double)0.0F;

                        for(Order o : orders.getOrderManager().getOpen()) {
                           if (o.getMaterial() == item.getType() && o.getRemaining() > 0 && o.getPriceEach() > bestEach) {
                              bestEach = o.getPriceEach();
                              best = o;
                           }
                        }

                        if (best != null) {
                           int amount = Math.min(item.getAmount(), best.getRemaining());
                           total = best.getPriceEach() * (double)amount;
                           if (amount < item.getAmount()) {
                              total += this.plugin.getPrices().getSellPrice(player, new ItemStack(item.getType(), item.getAmount() - amount));
                           }
                        }
                     }
                  } catch (Exception var17) {
                  }
               }

               hasPrice = total > (double)0.0F;
            }

            boolean shouldHave = addLore && hasPrice;
            if (!shouldHave) {
               if (hasLore) {
                  this.stripLore(item);
               }

            } else {
               if (hasLore) {
                  ItemMeta meta = item.getItemMeta();
                  if (meta != null && meta.hasLore() && meta.lore() != null) {
                     Integer count = (Integer)meta.getPersistentDataContainer().get(this.loreCountKey, PersistentDataType.INTEGER);
                     if (count != null && count > 0) {
                        List<Component> lore = meta.lore();
                        String expected = EconomyUtil.formatSell(total);
                        boolean priceVisible = false;
                        int from = Math.max(0, lore.size() - count);

                        for(int li = from; li < lore.size(); ++li) {
                           String plain = PlainTextComponentSerializer.plainText().serialize((Component)lore.get(li));
                           if (plain != null && plain.contains(expected)) {
                              priceVisible = true;
                              break;
                           }
                        }

                        if (priceVisible) {
                           return;
                        }
                     }
                  }
               }

               this.stripLore(item);
               this.applyLore(player, item, total);
            }
         }
      }
   }

   private boolean applyLore(Player player, ItemStack stack, double total) {
      ItemMeta meta = stack.getItemMeta();
      if (meta == null) {
         return false;
      } else {
         double unit = stack.getAmount() > 0 ? total / (double)stack.getAmount() : total;
         List<String> template = this.plugin.getConfig().getStringList("worth-lore.lines");
         if (template == null || template.isEmpty()) {
            String single = this.plugin.getConfig().getString("worth-lore.lore-format", (String)null);
            template = new ArrayList();
            if (single != null && !single.isEmpty()) {
               template.add(single);
            } else {
               template.add("&7Worth: &#00fc88%price%");
            }
         }

         List<Component> lore;
         if (meta.hasLore() && meta.lore() != null) {
            lore = new ArrayList(meta.lore());
         } else {
            lore = new ArrayList();
         }

         int added = 0;

         for(int t = 0; t < template.size(); ++t) {
            String line = (String)template.get(t);
            String out = line.replace("%price%", EconomyUtil.formatSell(total)).replace("%stack_price%", EconomyUtil.formatSell(total)).replace("%unit%", EconomyUtil.formatSell(unit)).replace("%amount%", String.valueOf(stack.getAmount()));
            lore.add(ColorUtil.parse(out));
            ++added;
         }

         meta.lore(lore);
         meta.getPersistentDataContainer().set(this.markerKey, PersistentDataType.BYTE, (byte)1);
         meta.getPersistentDataContainer().set(this.loreCountKey, PersistentDataType.INTEGER, added);
         stack.setItemMeta(meta);
         return true;
      }
   }

   private boolean stripLore(ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         ItemMeta meta = stack.getItemMeta();
         if (meta == null) {
            return false;
         } else if (!meta.getPersistentDataContainer().has(this.markerKey, PersistentDataType.BYTE)) {
            return false;
         } else {
            Integer count = (Integer)meta.getPersistentDataContainer().get(this.loreCountKey, PersistentDataType.INTEGER);
            List<Component> lore;
            if (meta.hasLore() && meta.lore() != null) {
               lore = new ArrayList(meta.lore());
            } else {
               lore = new ArrayList();
            }

            if (count != null && count > 0 && lore.size() >= count) {
               for(int i = 0; i < count; ++i) {
                  lore.remove(lore.size() - 1);
               }
            }

            if (lore.isEmpty()) {
               meta.lore((List)null);
            } else {
               meta.lore(lore);
            }

            meta.getPersistentDataContainer().remove(this.markerKey);
            meta.getPersistentDataContainer().remove(this.loreCountKey);
            stack.setItemMeta(meta);
            return true;
         }
      }
   }

   private boolean hasWorthMarker(ItemStack item) {
      if (item != null && item.hasItemMeta()) {
         ItemMeta meta = item.getItemMeta();
         return meta != null && meta.getPersistentDataContainer().has(this.markerKey, PersistentDataType.BYTE);
      } else {
         return false;
      }
   }

   private void removeAllWorthLores(Player player) {
      if (player != null) {
         for(ItemStack item : player.getInventory().getContents()) {
            if (item != null && !item.getType().isAir()) {
               this.stripLore(item);
            }
         }

         ItemStack offHand = player.getInventory().getItemInOffHand();
         if (offHand != null && !offHand.getType().isAir()) {
            this.stripLore(offHand);
         }

         ItemStack cursor = player.getItemOnCursor();
         if (cursor != null && !cursor.getType().isAir()) {
            this.stripLore(cursor);
         }

      }
   }

   private void stripInventory(Inventory inv) {
      if (inv != null) {
         for(int i = 0; i < inv.getSize(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack != null && !stack.getType().isAir()) {
               ItemStack copy = stack.clone();
               if (this.stripLore(copy)) {
                  inv.setItem(i, copy);
               }
            }
         }

      }
   }

   private void activateLoreRemoval(Player player) {
      this.loreRemovalUntil.put(player.getUniqueId(), System.currentTimeMillis() + 100L);
      this.updatePlayerInventory(player);
      final UUID id = player.getUniqueId();
      Tasks.runLater(this.plugin.getCore(), new Runnable() {
         public void run() {
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline()) {
               WorthDisplayListener.this.updatePlayerInventory(p);
            }

         }
      }, 3L);
   }

   private boolean isLoreRemovalActive(Player player) {
      Long end = (Long)this.loreRemovalUntil.get(player.getUniqueId());
      if (end == null) {
         return false;
      } else if (System.currentTimeMillis() > end) {
         this.loreRemovalUntil.remove(player.getUniqueId());
         return false;
      } else {
         return true;
      }
   }

   private void setBlockBreakCooldown(Player player) {
      this.blockBreakCooldown.put(player.getUniqueId(), System.currentTimeMillis());
      final UUID id = player.getUniqueId();
      Tasks.runLater(this.plugin.getCore(), new Runnable() {
         public void run() {
            WorthDisplayListener.this.blockBreakCooldown.remove(id);
            Player p = Bukkit.getPlayer(id);
            if (p != null && p.isOnline()) {
               WorthDisplayListener.this.updatePlayerInventory(p);
            }

         }
      }, 50L);
   }

   private boolean isInBlockBreakCooldown(Player player) {
      return this.blockBreakCooldown.containsKey(player.getUniqueId());
   }

   private boolean isContainer(InventoryType type) {
      return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST || type == InventoryType.BARREL || type == InventoryType.SHULKER_BOX || type == InventoryType.HOPPER || type == InventoryType.DROPPER || type == InventoryType.DISPENSER;
   }

   private boolean isInventoryNearlyFull(Player player) {
      Inventory inv = player.getInventory();
      int free = 0;

      for(int i = 0; i <= 35; ++i) {
         ItemStack item = inv.getItem(i);
         if (item == null || item.getType().isAir()) {
            ++free;
         }
      }

      ItemStack off = inv.getItem(40);
      if (off == null || off.getType().isAir()) {
         ++free;
      }

      return free <= 1;
   }

   private void updateNearlyFullCache(Player player) {
      boolean full = this.isInventoryNearlyFull(player);
      this.nearlyFullCache.put(player.getUniqueId(), full);
   }

   private boolean shouldDisableDueToFullInventory(Player player) {
      Boolean cached = (Boolean)this.nearlyFullCache.get(player.getUniqueId());
      if (cached == null) {
         cached = this.isInventoryNearlyFull(player);
         this.nearlyFullCache.put(player.getUniqueId(), cached);
      }

      return cached;
   }

   private static String ChatColorStrip(String input) {
      return input == null ? null : input.replaceAll("(?i)[§&][0-9A-FK-ORX]", "");
   }
}
