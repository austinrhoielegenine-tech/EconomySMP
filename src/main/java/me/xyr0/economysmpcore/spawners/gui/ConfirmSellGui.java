package me.xyr0.economysmpcore.spawners.gui;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.spawners.AstralSpawners;
import me.xyr0.economysmpcore.spawners.EntityDefinition;
import me.xyr0.economysmpcore.spawners.SpawnerData;
import me.xyr0.economysmpcore.spawners.util.ItemBuilder;
import me.xyr0.economysmpcore.spawners.util.MobHeadUtil;
import me.xyr0.economysmpcore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ConfirmSellGui {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().hexColors().character('&').build();
   public static final Map<UUID, SpawnerData> OPEN = new ConcurrentHashMap();

   private ConfirmSellGui() {
   }

   public static void open(Player player, SpawnerData data) {
      EntityDefinition def = AstralSpawners.get().getEntities().get(data.getEntityType());
      double total = (double)0.0F;
      Material main = null;
      int mainQty = 0;
      if (def != null) {
         for(Map.Entry<Material, Integer> e : data.getStorage().entrySet()) {
            double price = def.getPrice((Material)e.getKey());
            if (price > (double)0.0F) {
               total += price * (double)(Integer)e.getValue();
            }

            if ((Integer)e.getValue() > mainQty) {
               mainQty = (Integer)e.getValue();
               main = (Material)e.getKey();
            }
         }
      }

      String symbol = AstralSpawners.get().getSettings().getCurrencySymbol();
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, LEGACY.deserialize("&8ᴄᴏɴꜰɪʀᴍ ѕᴇʟʟ"));
      String lootName = main != null ? pretty(main) : "Loot";
      inv.setItem(13, MobHeadUtil.headFor(data.getEntityType(), "&#1FFD98" + lootName, List.of("&#1FFD98" + data.getTotalItems() + " &fitems", "&7Value: &#1FFD98" + symbol + String.format("%.2f", total))));
      inv.setItem(11, ItemBuilder.named(Material.RED_STAINED_GLASS_PANE, "&#FF1D1Dᴄᴀɴᴄᴇʟ", List.of("&fClick to cancel")));
      inv.setItem(15, ItemBuilder.named(Material.LIME_STAINED_GLASS_PANE, "&#3AFF00ᴄᴏɴꜰɪʀᴍ", List.of("&fClick to sell all items", "&7(" + symbol + String.format("%.2f", total) + ")")));
      UUID id = player.getUniqueId();
      SpawnerGui.SWITCHING.add(id);
      SpawnerGui.OPEN.remove(id);
      OPEN.put(id, data);
      player.openInventory(inv);
      Tasks.run(AstralSpawners.get().getCore(), () -> SpawnerGui.SWITCHING.remove(id));
   }

   private static String pretty(Material mat) {
      String raw = mat.name().toLowerCase().replace('_', ' ');
      StringBuilder sb = new StringBuilder();

      for(String p : raw.split(" ")) {
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
