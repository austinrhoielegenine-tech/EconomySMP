package com.austynzdevv.novacore.auction;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.austynzdevv.novacore.data.PlayerDataStore;
import com.austynzdevv.novacore.shards.EconomyShards;
import com.austynzdevv.novacore.util.EconomyUtil;
import com.austynzdevv.novacore.util.ItemUtil;
import com.austynzdevv.novacore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionManager {
   private final EconomyAuction plugin;
   private final Map<UUID, AuctionListing> listings = new ConcurrentHashMap();
   private final List<Transaction> transactions = new ArrayList();
   private File listingFile;
   private File txFile;

   public AuctionManager(EconomyAuction plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.listings.clear();
      this.transactions.clear();
      this.listingFile = PlayerDataStore.get().file();
      this.txFile = this.listingFile;
      FileConfiguration cfg = PlayerDataStore.get().config();
      List<?> list = cfg.getList("listings");
      if (list != null) {
         for(Object o : list) {
            if (o instanceof Map) {
               Map<?, ?> map = (Map)o;

               try {
                  AuctionListing listing = AuctionListing.deserialize((Map<String, Object>)map);
                  this.listings.put(listing.getId(), listing);
               } catch (Exception var10) {
                  this.plugin.getLogger().warning("Could not load a listing");
               }
            }
         }
      }

      FileConfiguration tx = PlayerDataStore.get().config();
      List<?> txList = tx.getList("transactions");
      if (txList != null) {
         for(Object o : txList) {
            if (o instanceof Map) {
               Map<?, ?> map = (Map)o;

               try {
                  this.transactions.add(Transaction.deserialize((Map<String, Object>)map));
               } catch (Exception var9) {
               }
            }
         }
      }

   }

   public void save() {
      FileConfiguration cfg = PlayerDataStore.get().config();
      List<Map<String, Object>> list = new ArrayList();

      for(AuctionListing l : this.listings.values()) {
         list.add(l.serialize());
      }

      cfg.set("listings", list);
      FileConfiguration tx = PlayerDataStore.get().config();
      List<Map<String, Object>> tlist = new ArrayList();

      for(Transaction t : this.transactions) {
         tlist.add(t.serialize());
      }

      tx.set("transactions", tlist);
      PlayerDataStore.get().save();
   }

   public List<AuctionListing> getActive() {
      return (List)this.listings.values().stream().filter(AuctionListing::isActive).collect(Collectors.toList());
   }

   public AuctionListing get(UUID id) {
      return (AuctionListing)this.listings.get(id);
   }

   public int countActive(UUID seller) {
      int n = 0;

      for(AuctionListing l : this.listings.values()) {
         if (l.getSellerId().equals(seller) && l.isActive()) {
            ++n;
         }
      }

      return n;
   }

   public List<AuctionListing> getBySeller(UUID seller) {
      return (List)this.listings.values().stream().filter((l) -> l.getSellerId().equals(seller) && l.isActive()).sorted(Comparator.comparingLong(AuctionListing::getCreatedAt).reversed()).collect(Collectors.toList());
   }

   public int getLimit(Player player) {
      int max = this.plugin.getConfig().getInt("auction.max-listings-default", 5);
      int[] checks = new int[]{44, 27, 10, 5};

      for(int lim : checks) {
         if (player.hasPermission("novacorecore.auction.limit." + lim)) {
            return lim;
         }
      }

      return max;
   }

   public boolean isBlacklisted(ItemStack item) {
      if (item == null) {
         return true;
      } else {
         List<String> list = this.plugin.getConfig().getStringList("blacklist-items");
         return list != null && list.stream().anyMatch((s) -> s.equalsIgnoreCase(item.getType().name()));
      }
   }

   public AuctionListing create(Player seller, ItemStack item, double price) {
      return this.create(seller, item, price, false);
   }

   public AuctionListing create(Player seller, ItemStack item, double price, boolean shards) {
      long hours = this.plugin.getConfig().getLong("auction.duration-hours", 24L);
      long now = System.currentTimeMillis();
      AuctionListing listing = new AuctionListing(UUID.randomUUID(), seller.getUniqueId(), seller.getName(), item, price, now, now + hours * 3600000L, shards);
      this.listings.put(listing.getId(), listing);
      if (this.plugin.getWebhooks() != null) {
         this.plugin.getWebhooks().sendListed(listing);
      }

      return listing;
   }

   public boolean buy(Player buyer, AuctionListing listing) {
      if (listing != null && listing.isActive()) {
         if (listing.getSellerId().equals(buyer.getUniqueId())) {
            return false;
         } else {
            if (listing.isShards()) {
               EconomyShards shards = EconomyShards.get();
               if (shards == null || !shards.getShards().has(buyer, (long)Math.ceil(listing.getPrice()))) {
                  return false;
               }

               if (!shards.getShards().take(buyer, (long)Math.ceil(listing.getPrice()))) {
                  return false;
               }
            } else {
               if (!this.plugin.getEconomy().has(buyer, listing.getPrice())) {
                  return false;
               }

               if (!this.plugin.getEconomy().withdraw(buyer, listing.getPrice())) {
                  return false;
               }
            }

            double feePct = this.plugin.getConfig().getDouble("auction.fee-percentage", (double)0.0F);
            double fee = listing.getPrice() * (feePct / (double)100.0F);
            double sellerGets = listing.getPrice() - fee;
            if (listing.isShards()) {
               EconomyShards shards = EconomyShards.get();
               if (shards != null) {
                  shards.getShards().add(listing.getSellerId(), (long)Math.floor(sellerGets));
               }
            } else {
               this.plugin.getEconomy().deposit(listing.getSellerId(), sellerGets);
            }

            ItemStack give = listing.getItem();
            Map<Integer, ItemStack> left = buyer.getInventory().addItem(new ItemStack[]{give});
            left.values().forEach((i) -> buyer.getWorld().dropItemNaturally(buyer.getLocation(), i));
            listing.markSold();
            long now = System.currentTimeMillis();
            this.transactions.add(new Transaction(UUID.randomUUID(), buyer.getUniqueId(), buyer.getName(), listing.getSellerId(), listing.getSellerName(), listing.getItem(), listing.getPrice(), Transaction.Type.BUY, now));
            this.transactions.add(new Transaction(UUID.randomUUID(), listing.getSellerId(), listing.getSellerName(), buyer.getUniqueId(), buyer.getName(), listing.getItem(), listing.getPrice(), Transaction.Type.SELL, now));
            this.plugin.getMessages().send(buyer, "item-bought", Map.of("amount", String.valueOf(listing.getItem().getAmount()), "item", ItemUtil.pretty(listing.getItem()), "price", EconomyUtil.formatAuction(listing.getPrice())));
            Player seller = Bukkit.getPlayer(listing.getSellerId());
            if (seller != null) {
               this.plugin.getMessages().send(seller, "item-sold", Map.of("player", buyer.getName(), "amount", String.valueOf(listing.getItem().getAmount()), "item", ItemUtil.pretty(listing.getItem()), "price", EconomyUtil.formatAuction(listing.getPrice())));
            }

            if (this.plugin.getWebhooks() != null) {
               this.plugin.getWebhooks().sendBought(listing, buyer.getName());
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void adminRemove(AuctionListing listing) {
      if (listing != null && listing.isActive()) {
         listing.markCancelled();
         if (this.plugin.getWebhooks() != null) {
            this.plugin.getWebhooks().sendRemoved(listing);
         }

         Player seller = Bukkit.getPlayer(listing.getSellerId());
         if (seller != null) {
            ItemStack item = listing.getItem();
            Map<Integer, ItemStack> left = seller.getInventory().addItem(new ItemStack[]{item});
            left.values().forEach((i) -> seller.getWorld().dropItemNaturally(seller.getLocation(), i));
            this.plugin.getMessages().send(seller, "item-returned", Map.of("item", ItemUtil.pretty(item)));
         }

      }
   }

   public boolean cancel(Player player, AuctionListing listing) {
      if (listing != null && listing.isActive()) {
         if (!listing.getSellerId().equals(player.getUniqueId()) && !player.hasPermission("novacorecore.system.auction.use")) {
            return false;
         } else {
            listing.markCancelled();
            ItemStack item = listing.getItem();
            Map<Integer, ItemStack> left = player.getInventory().addItem(new ItemStack[]{item});
            left.values().forEach((i) -> player.getWorld().dropItemNaturally(player.getLocation(), i));
            this.plugin.getMessages().send(player, "auction-removed");
            if (this.plugin.getWebhooks() != null) {
               this.plugin.getWebhooks().sendRemoved(listing);
            }

            this.plugin.getMessages().send(player, "item-returned", Map.of("item", ItemUtil.pretty(item)));
            return true;
         }
      } else {
         return false;
      }
   }

   public void processExpired() {
      for(AuctionListing listing : this.listings.values()) {
         if (listing.isExpired()) {
            listing.markCancelled();
            Player seller = Bukkit.getPlayer(listing.getSellerId());
            if (seller != null) {
               ItemStack item = listing.getItem();
               Map<Integer, ItemStack> left = seller.getInventory().addItem(new ItemStack[]{item});
               left.values().forEach((i) -> seller.getWorld().dropItemNaturally(seller.getLocation(), i));
               String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(new Date(listing.getExpiresAt()));
               this.plugin.getMessages().send(seller, "expired", Map.of("amount", String.valueOf(item.getAmount()), "item", ItemUtil.pretty(item), "date", date));
            }
         }
      }

   }

   public List<AuctionListing> filtered(String search, int sort, int filter) {
      return this.filtered(search, sort, filter, false);
   }

   public List<AuctionListing> filtered(String search, int sort, int filter, boolean shardView) {
      List<AuctionListing> list = new ArrayList(this.getActive());
      List<AuctionListing> var7 = (List)list.stream().filter((l) -> l.isShards() == shardView).collect(Collectors.toList());
      if (search != null && !search.isEmpty()) {
         String q = search.toLowerCase(Locale.ROOT);
         var7 = (List)var7.stream().filter((l) -> l.getItem().getType().name().toLowerCase(Locale.ROOT).contains(q) || ItemUtil.pretty(l.getItem()).toLowerCase(Locale.ROOT).contains(q) || l.getSellerName().toLowerCase(Locale.ROOT).contains(q)).collect(Collectors.toList());
      }

      if (filter > 0) {
         var7 = (List)var7.stream().filter((l) -> this.matchesFilter(l.getItem().getType(), filter)).collect(Collectors.toList());
      }

      Comparator<AuctionListing> cmp;
      if (sort == 1) {
         cmp = Comparator.comparingDouble(AuctionListing::getPrice);
      } else if (sort == 2) {
         cmp = Comparator.comparingLong(AuctionListing::getCreatedAt);
      } else if (sort == 3) {
         cmp = Comparator.comparingLong(AuctionListing::getCreatedAt).reversed();
      } else {
         cmp = Comparator.comparingDouble(AuctionListing::getPrice).reversed();
      }

      var7.sort(cmp);
      return var7;
   }

   private boolean matchesFilter(Material mat, int filter) {
      String n = mat.name();
      boolean var10000;
      switch (filter) {
         case 1 -> var10000 = n.contains("BLOCK") || n.contains("STONE") || n.contains("DIRT") || n.contains("WOOD") || n.contains("LOG") || n.contains("PLANKS") || n.endsWith("_ORE");
         case 2 -> var10000 = n.contains("PICKAXE") || n.contains("AXE") || n.contains("SHOVEL") || n.contains("HOE") || n.contains("SHEARS");
         case 3 -> var10000 = n.contains("BEEF") || n.contains("PORK") || n.contains("CHICKEN") || n.contains("BREAD") || n.contains("APPLE") || n.contains("CARROT") || n.contains("POTATO");
         case 4 -> var10000 = n.contains("SWORD") || n.contains("BOW") || n.contains("ARROW") || n.contains("HELMET") || n.contains("CHESTPLATE") || n.contains("LEGGINGS") || n.contains("BOOTS") || n.contains("SHIELD");
         case 5 -> var10000 = n.contains("POTION");
         case 6 -> var10000 = n.contains("BOOK");
         case 7 -> var10000 = n.contains("INGOT") || n.contains("DUST") || n.contains("NUGGET") || n.contains("DIAMOND") || n.contains("EMERALD") || n.contains("REDSTONE");
         default -> var10000 = true;
      }

      return var10000;
   }

   public List<Transaction> getTransactions(UUID player) {
      return (List)this.transactions.stream().filter((t) -> t.getPlayerId().equals(player)).sorted(Comparator.comparingLong(Transaction::getTime).reversed()).collect(Collectors.toList());
   }

   public double totalSpent(UUID player) {
      return this.getTransactions(player).stream().filter((t) -> t.getType() == Transaction.Type.BUY).mapToDouble(Transaction::getPrice).sum();
   }

   public double totalMade(UUID player) {
      return this.getTransactions(player).stream().filter((t) -> t.getType() == Transaction.Type.SELL).mapToDouble(Transaction::getPrice).sum();
   }

   public void startTasks() {
      int sec = this.plugin.getConfig().getInt("auction.expire-check-interval", 60);
      Tasks.runTimer(this.plugin.getCore(), this::processExpired, 20L * (long)sec, 20L * (long)sec);
      Tasks.runAsyncTimer(this.plugin.getCore(), this::save, 2400L, 2400L);
   }
}
