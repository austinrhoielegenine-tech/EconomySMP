package com.austynzdevv.novacore.auction.gui;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.austynzdevv.novacore.auction.EconomyAuction;
import com.austynzdevv.novacore.auction.AuctionListing;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class AdminMenus {
   private AdminMenus() {
   }

   private static FileConfiguration yaml() {
      return YamlConfiguration.loadConfiguration(new File(EconomyAuction.get().getCore().getDataFolder(), "gui/admin.gui.yml"));
   }

   private static void open(Player player, Inventory inv) {
      UUID id = player.getUniqueId();
      MenuSession.SWITCHING.add(id);
      player.openInventory(inv);
      SoundUtil.playConfigured(player, EconomyAuction.get().getCore(), "sounds.gui-open");
      Tasks.run(EconomyAuction.get().getCore(), () -> MenuSession.SWITCHING.remove(id));
   }

   public static void openAdmin(Player player, UUID targetId) {
      FileConfiguration cfg = yaml();
      ConfigurationSection root = cfg.getConfigurationSection("admin-panel");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN);
      MenuSession.ADMIN_TARGET.put(player.getUniqueId(), targetId);
      OfflinePlayer off = Bukkit.getOfflinePlayer(targetId);
      String name = off.getName() == null ? targetId.toString() : off.getName();
      String title = root.getString("title", "&8ᴀᴅᴍɪɴ -> &f%player%").replace("%player%", name);
      int rows = root.getInt("rows", 3);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      ConfigurationSection items = root.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "listings", Map.of());
         place(inv, items, "transactions", Map.of());
         if (EconomyAuction.get().getBlocks().isBlocked(targetId)) {
            place(inv, items, "unblock", Map.of());
         } else {
            place(inv, items, "block", Map.of());
         }

         place(inv, items, "back", Map.of());
      }

      open(player, inv);
   }

   private static void place(Inventory inv, ConfigurationSection items, String key, Map<String, String> ph) {
      ConfigurationSection sec = items.getConfigurationSection(key);
      if (sec != null) {
         inv.setItem(sec.getInt("slot", 0), GuiItems.fromSection(sec, ph));
      }
   }

   public static void openListings(Player player, UUID targetId, int page) {
      FileConfiguration cfg = yaml();
      ConfigurationSection root = cfg.getConfigurationSection("admin-listings");
      if (root == null) {
         root = cfg;
      }

      MenuSession.VIEW.put(player.getUniqueId(), MenuSession.View.ADMIN_LISTINGS);
      MenuSession.ADMIN_TARGET.put(player.getUniqueId(), targetId);
      MenuSession.PAGE.put(player.getUniqueId(), page);
      OfflinePlayer off = Bukkit.getOfflinePlayer(targetId);
      String name = off.getName() == null ? targetId.toString() : off.getName();
      String title = root.getString("title", "&8ʟɪꜱᴛɪɴɢꜱ -> &f%player%").replace("%player%", name);
      int rows = root.getInt("rows", 6);
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, ColorUtil.parse(title));
      List<AuctionListing> list = EconomyAuction.get().getAuctions().getBySeller(targetId);
      int pageSize = 45;
      int maxPage = list.isEmpty() ? 0 : (list.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      MenuSession.PAGE.put(player.getUniqueId(), page);
      int from = page * pageSize;
      int to = Math.min(list.size(), from + pageSize);

      for(int i = from; i < to; ++i) {
         AuctionListing listing = (AuctionListing)list.get(i);
         List<String> lore = List.of("&fPrice: &#00fc88" + EconomyUtil.formatAuction(listing.getPrice()), "&fSeller: &#00fc88" + listing.getSellerName(), "", "&cShift-Click to remove");
         ItemStack item = listing.getItem();
         if (item == null || item.getType() == Material.AIR) {
            item = new ItemStack(Material.BARRIER);
         }

         inv.setItem(i - from, GuiItems.withLore(item, (String)null, lore));
      }

      ConfigurationSection items = root.getConfigurationSection("items");
      if (items != null) {
         place(inv, items, "previous-page", Map.of());
         place(inv, items, "next-page", Map.of());
         place(inv, items, "back", Map.of());
      }

      open(player, inv);
   }
}
