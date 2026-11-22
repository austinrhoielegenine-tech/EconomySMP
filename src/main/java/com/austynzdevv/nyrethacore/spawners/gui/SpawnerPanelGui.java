package com.austynzdevv.nyrethacore.spawners.gui;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.spawners.EconomySpawners;
import com.austynzdevv.nyrethacore.spawners.EntityDefinition;
import com.austynzdevv.nyrethacore.spawners.SpawnerData;
import com.austynzdevv.nyrethacore.spawners.util.ItemBuilder;
import com.austynzdevv.nyrethacore.spawners.util.MobHeadUtil;
import com.austynzdevv.nyrethacore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class SpawnerPanelGui {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, Dimension> DIM = new ConcurrentHashMap();
   public static final Map<UUID, Integer> PAGE = new ConcurrentHashMap();
   public static final Map<UUID, List<SpawnerData>> LIST = new ConcurrentHashMap();

   private SpawnerPanelGui() {
   }

   public static boolean isOpen(UUID id) {
      return VIEW.containsKey(id);
   }

   public static void openMain(Player player) {
      Map<Dimension, Integer> counts = countByDimension();
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, LEGACY.deserialize("&8ꜱᴘᴀᴡɴᴇʀ ᴘᴀɴᴇʟ"));

      for(int i = 0; i < 27; ++i) {
         inv.setItem(i, ItemBuilder.named(Material.GRAY_STAINED_GLASS_PANE, " ", List.of()));
      }

      inv.setItem(11, ItemBuilder.named(Material.GRASS_BLOCK, "&#3AFF00ᴏᴠᴇʀᴡᴏʀʟᴅ", List.of("&7Total Spawners: &f" + String.valueOf(counts.getOrDefault(SpawnerPanelGui.Dimension.OVERWORLD, 0)), "", "&7Click to see all of the spawners")));
      inv.setItem(13, ItemBuilder.named(Material.NETHERRACK, "&#FE3333ɴᴇᴛʜᴇʀ", List.of("&7Total Spawners: &f" + String.valueOf(counts.getOrDefault(SpawnerPanelGui.Dimension.NETHER, 0)), "", "&7Click to see all of the spawners")));
      inv.setItem(15, ItemBuilder.named(Material.END_STONE, "&#C11FFEᴇɴᴅ", List.of("&7Total Spawners: &f" + String.valueOf(counts.getOrDefault(SpawnerPanelGui.Dimension.END, 0)), "", "&7Click to see all of the spawners")));
      UUID id = player.getUniqueId();
      SpawnerGui.SWITCHING.add(id);
      clearSession(id);
      VIEW.put(id, SpawnerPanelGui.View.MAIN);
      player.openInventory(inv);
      Tasks.run(EconomySpawners.get().getCore(), () -> SpawnerGui.SWITCHING.remove(id));
   }

   public static void openDimension(Player player, Dimension dim, int page) {
      List<SpawnerData> all = spawnersIn(dim);
      int pageSize = 45;
      int maxPage = all.isEmpty() ? 0 : (all.size() - 1) / pageSize;
      if (page < 0) {
         page = 0;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      String var10000;
      switch (dim.ordinal()) {
         case 0 -> var10000 = "&8ᴏᴠᴇʀᴡᴏʀʟᴅ ꜱᴘᴀᴡɴᴇʀꜱ";
         case 1 -> var10000 = "&8ɴᴇᴛʜᴇʀ ꜱᴘᴀᴡɴᴇʀꜱ";
         case 2 -> var10000 = "&8ᴇɴᴅ ꜱᴘᴀᴡɴᴇʀꜱ";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      String title = var10000;
      if (maxPage > 0) {
         title = title + " &8(" + (page + 1) + "/" + (maxPage + 1) + ")";
      }

      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, LEGACY.deserialize(title));

      for(int i = 0; i < 54; ++i) {
         inv.setItem(i, ItemBuilder.named(Material.GRAY_STAINED_GLASS_PANE, " ", List.of()));
      }

      int from = page * pageSize;
      int to = Math.min(all.size(), from + pageSize);
      List<SpawnerData> slice = all.subList(from, to);

      for(int i = 0; i < slice.size(); ++i) {
         SpawnerData data = (SpawnerData)slice.get(i);
         EntityDefinition def = EconomySpawners.get().getEntities().get(data.getEntityType());
         String mob = def != null ? strip(def.getDisplay()).replace(" Spawner", "") : data.getEntityType().name();
         List<String> lore = new ArrayList();
         lore.add("&7Amount: &f" + data.getStackSize());
         lore.add("&7World: &f" + data.getWorldName());
         int var10001 = data.getX();
         lore.add("&7Location: &f" + var10001 + ", " + data.getY() + ", " + data.getZ());
         lore.add("");
         lore.add("&aRight-Click to teleport");
         lore.add("&cLeft-Click to remove");
         inv.setItem(i, MobHeadUtil.headFor(data.getEntityType(), "&e" + mob + " Spawner", lore));
      }

      inv.setItem(45, ItemBuilder.named(Material.ARROW, "&fʙᴀᴄᴋ", List.of()));
      inv.setItem(49, ItemBuilder.named(Material.BARRIER, "&cBack", List.of("&7Back to panel")));
      inv.setItem(53, ItemBuilder.named(Material.ARROW, "&fɴᴇхᴛ", List.of()));
      UUID id = player.getUniqueId();
      SpawnerGui.SWITCHING.add(id);
      VIEW.put(id, SpawnerPanelGui.View.DIMENSION);
      DIM.put(id, dim);
      PAGE.put(id, page);
      LIST.put(id, all);
      player.openInventory(inv);
      Tasks.run(EconomySpawners.get().getCore(), () -> SpawnerGui.SWITCHING.remove(id));
   }

   public static void clearSession(UUID id) {
      VIEW.remove(id);
      DIM.remove(id);
      PAGE.remove(id);
      LIST.remove(id);
   }

   private static Map<Dimension, Integer> countByDimension() {
      Map<Dimension, Integer> map = new EnumMap(Dimension.class);

      for(Dimension d : SpawnerPanelGui.Dimension.values()) {
         map.put(d, 0);
      }

      for(SpawnerData data : EconomySpawners.get().getSpawnerManager().getAll()) {
         Dimension d = dimensionOf(data);
         if (d != null) {
            map.merge(d, 1, Integer::sum);
         }
      }

      return map;
   }

   private static List<SpawnerData> spawnersIn(Dimension dim) {
      return (List)EconomySpawners.get().getSpawnerManager().getAll().stream().filter((s) -> dimensionOf(s) == dim).collect(Collectors.toList());
   }

   private static Dimension dimensionOf(SpawnerData data) {
      World world = Bukkit.getWorld(data.getWorldName());
      if (world == null) {
         return null;
      } else {
         Dimension var10000;
         switch (world.getEnvironment()) {
            case NORMAL -> var10000 = SpawnerPanelGui.Dimension.OVERWORLD;
            case NETHER -> var10000 = SpawnerPanelGui.Dimension.NETHER;
            case THE_END -> var10000 = SpawnerPanelGui.Dimension.END;
            default -> var10000 = SpawnerPanelGui.Dimension.OVERWORLD;
         }

         return var10000;
      }
   }

   private static String strip(String input) {
      return input.replaceAll("&#[0-9A-Fa-f]{6}", "").replaceAll("&[0-9a-fk-or]", "").replaceAll("§[0-9a-fk-or]", "");
   }

   public static enum View {
      MAIN,
      DIMENSION;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{MAIN, DIMENSION};
      }
   }

   public static enum Dimension {
      OVERWORLD,
      NETHER,
      END;

      // $FF: synthetic method
      private static Dimension[] $values() {
         return new Dimension[]{OVERWORLD, NETHER, END};
      }
   }
}
