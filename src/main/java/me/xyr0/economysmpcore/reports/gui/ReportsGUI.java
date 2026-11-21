package me.xyr0.economysmpcore.reports.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.xyr0.economysmpcore.reports.AstralReports;
import me.xyr0.economysmpcore.reports.Report;
import me.xyr0.economysmpcore.reports.ReportRepository;
import me.xyr0.economysmpcore.reports.util.HeadUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

public class ReportsGUI {
   private static final HashMap<UUID, HashMap<Integer, UUID>> openReports = new HashMap();
   private static final HashMap<UUID, UUID> selectedPlayers = new HashMap();

   public static void open(Player player, AstralReports plugin) {
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 54, "§bReports");
      ReportRepository repository = new ReportRepository(plugin);
      List<Report> reports = repository.getAllReports();
      HashMap<Integer, UUID> slots = new HashMap();
      List<UUID> added = new ArrayList();
      int slot = 0;

      for(Report report : reports) {
         UUID uuid = report.getReportedUUID();
         if (!added.contains(uuid)) {
            added.add(uuid);
            if (slot >= 54) {
               break;
            }

            inventory.setItem(slot, HeadUtil.getHead(Bukkit.getOfflinePlayer(uuid)));
            if (inventory.getItem(slot).getItemMeta() != null) {
               ItemMeta meta = inventory.getItem(slot).getItemMeta();
               meta.setDisplayName("§b" + report.getReportedName());
               meta.setLore(List.of("§7Player: §f" + report.getReportedName(), "", "§7Reports: §c" + repository.getReportCount(uuid), "", "§7Latest Report:", "§f" + repository.getLatestReport(uuid), "", "§eClick to manage"));
               inventory.getItem(slot).setItemMeta(meta);
            }

            slots.put(slot, uuid);
            ++slot;
         }
      }

      openReports.put(player.getUniqueId(), slots);
      player.openInventory(inventory);
   }

   public static UUID getTarget(Player player, int slot) {
      HashMap<Integer, UUID> slots = (HashMap)openReports.get(player.getUniqueId());
      return slots == null ? null : (UUID)slots.get(slot);
   }

   public static void setSelected(Player player, UUID uuid) {
      selectedPlayers.put(player.getUniqueId(), uuid);
   }

   public static UUID getSelected(Player player) {
      return (UUID)selectedPlayers.get(player.getUniqueId());
   }
}
