package com.austynzdevv.nyrethacore.punishment2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.austynzdevv.nyrethacore.punishment2.gui.HistoryGUI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class HistoryManager {
   private final EconomyPunishment plugin;

   public HistoryManager(EconomyPunishment plugin) {
      this.plugin = plugin;
   }

   public List<Punishment> getHistory(UUID uuid) {
      return this.plugin.getDatabaseManager().getHistory(uuid);
   }

   public List<Punishment> getVisibleHistory(UUID uuid) {
      List<Punishment> visible = new ArrayList();

      for(Punishment punishment : this.getHistory(uuid)) {
         if (!punishment.getType().equalsIgnoreCase("FREEZE") && !punishment.getType().equalsIgnoreCase("UNFREEZE")) {
            visible.add(punishment);
         }
      }

      return visible;
   }

   public void openHistory(Player viewer, OfflinePlayer target, int page) {
      HistoryGUI gui = new HistoryGUI(this.plugin);
      gui.open(viewer, target, page);
   }
}
