package com.austynzdevv.novacore.spawners.gui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.spawners.EconomySpawners;
import com.austynzdevv.novacore.spawners.EntityDefinition;
import com.austynzdevv.novacore.spawners.SpawnerData;
import com.austynzdevv.novacore.spawners.util.ItemBuilder;
import com.austynzdevv.novacore.spawners.util.MobHeadUtil;
import com.austynzdevv.novacore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class SpawnerGui {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();
   public static final int PAGE_SIZE = 45;
   public static final Map<UUID, SpawnerData> OPEN = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Set<UUID> SWITCHING = ConcurrentHashMap.newKeySet();

   private SpawnerGui() {
   }

   public static void open(Player player, SpawnerData data) {
      OPEN.put(player.getUniqueId(), data);
      PAGE.put(player.getUniqueId(), 0);
      player.openInventory(build(data, 0));
   }

   public static void refresh(Player player, SpawnerData data) {
      int page = (Integer)PAGE.getOrDefault(player.getUniqueId(), 0);
      int maxPage = maxPage(data);
      if (page > maxPage) {
         page = maxPage;
         PAGE.put(player.getUniqueId(), maxPage);
      }

      Inventory top = player.getOpenInventory().getTopInventory();
      if (top.getSize() == 54 && OPEN.containsKey(player.getUniqueId())) {
         fill(top, data, page);
      } else {
         SWITCHING.add(player.getUniqueId());
         player.openInventory(build(data, page));
         Tasks.run(EconomySpawners.get().getCore(), () -> SWITCHING.remove(player.getUniqueId()));
      }
   }

   public static void setPage(Player player, SpawnerData data, int page) {
      int max = maxPage(data);
      if (page < 0) {
         page = 0;
      }

      if (page > max) {
         page = max;
      }

      PAGE.put(player.getUniqueId(), page);
      Inventory top = player.getOpenInventory().getTopInventory();
      if (top.getSize() == 54 && OPEN.containsKey(player.getUniqueId())) {
         fill(top, data, page);
      } else {
         SWITCHING.add(player.getUniqueId());
         player.openInventory(build(data, page));
         Tasks.run(EconomySpawners.get().getCore(), () -> SWITCHING.remove(player.getUniqueId()));
      }
   }

   public static int getPage(Player player) {
      return (Integer)PAGE.getOrDefault(player.getUniqueId(), 0);
   }

   public static int maxPage(SpawnerData data) {
      int totalStacks = 0;

      for(int amount : data.getStorage().values()) {
         totalStacks += (amount + 63) / 64;
      }

      return totalStacks <= 0 ? 0 : (totalStacks - 1) / 45;
   }

   public static List<ItemStack> pageItems(SpawnerData data, int page) {
      List<ItemStack> flat = new ArrayList();

      int give;
      for(Map.Entry<Material, Integer> e : (new LinkedHashMap<>(data.getStorage())).entrySet()) {
         for(int amount = (Integer)e.getValue(); amount > 0; amount -= give) {
            give = Math.min(amount, 64);
            flat.add(new ItemStack((Material)e.getKey(), give));
         }
      }

      int from = page * 45;
      if (from >= flat.size()) {
         return List.of();
      } else {
         return new ArrayList(flat.subList(from, Math.min(flat.size(), from + 45)));
      }
   }

   public static Map<Material, Integer> pageContents(SpawnerData data, int page) {
      Map<Material, Integer> map = new LinkedHashMap();

      for(ItemStack stack : pageItems(data, page)) {
         map.merge(stack.getType(), stack.getAmount(), Integer::sum);
      }

      return map;
   }

   private static Inventory build(SpawnerData data, int page) {
      String mob = mobName(data);
      int max = maxPage(data);
      int var10000 = data.getStackSize();
      String title = "&8" + var10000 + "x " + mob + " ѕᴘᴀᴡɴᴇʀ";
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, LEGACY.deserialize(title));
      fill(inv, data, page);
      return inv;
   }

   private static void fill(Inventory inv, SpawnerData data, int page) {
      for(int i = 0; i < 54; ++i) {
         inv.setItem(i, (ItemStack)null);
      }

      List<ItemStack> items = pageItems(data, page);

      for(int i = 0; i < items.size() && i < 45; ++i) {
         inv.setItem(i, (ItemStack)items.get(i));
      }

      int max = maxPage(data);
      inv.setItem(45, ItemBuilder.named(Material.ARROW, "&#1FFD98ʙᴀᴄᴋ", List.of("&fClick to go previous page")));
      inv.setItem(53, ItemBuilder.named(Material.ARROW, "&#1FFD98ɴᴇхᴛ", List.of("&fClick to go next page")));
      if (EconomySpawners.get().getSettings().economyEnabled()) {
         inv.setItem(48, ItemBuilder.named(Material.GOLD_INGOT, "&#FF1D1Dѕᴇʟʟ ᴀʟʟ", List.of("&fClick to sell all mobs drops!")));
      }

      inv.setItem(50, ItemBuilder.named(Material.DROPPER, "&#1FFD98ᴅʀᴏᴘ ʟᴏᴏᴛ", List.of("&fClick to drop all loot on the page")));
      String mob = mobName(data);
      List<String> lore = new ArrayList();
      lore.add("&#1FFD98" + data.getTotalItems() + " &fitems stored");
      int limit = EconomySpawners.get().getSettings().getLootLimit();
      if (limit > 0) {
         int pct = Math.min(100, data.getTotalItems() * 100 / Math.max(1, limit));
         lore.add("&#1FFD98(" + pct + "% filled)");
      } else {
         lore.add("&#1FFD98Stack: &f" + data.getStackSize());
      }

      inv.setItem(49, MobHeadUtil.headFor(data.getEntityType(), "&#1FFD98" + mob + " ѕᴘᴀᴡɴᴇʀ", lore));
   }

   private static String mobName(SpawnerData data) {
      EntityDefinition def = EconomySpawners.get().getEntities().get(data.getEntityType());
      if (def != null) {
         return strip(def.getDisplay()).replace(" Spawner", "").trim();
      } else {
         String raw = data.getEntityType().name().toLowerCase().replace('_', ' ');
         String[] parts = raw.split(" ");
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
   }

   private static String strip(String input) {
      return input.replaceAll("&#[0-9A-Fa-f]{6}", "").replaceAll("&[0-9a-fk-or]", "").replaceAll("§[0-9a-fk-or]", "");
   }
}
