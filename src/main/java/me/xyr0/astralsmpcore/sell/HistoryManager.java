package me.xyr0.astralsmpcore.sell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import me.xyr0.astralsmpcore.data.PlayerDataStore;

public class HistoryManager {
   private final AstralSell plugin;
   private final List<SellRecord> records = new ArrayList();

   public HistoryManager(AstralSell plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.records.clear();
      List<?> list = PlayerDataStore.get().config().getList("sell-history.records");
      if (list != null) {
         for(Object o : list) {
            if (o instanceof Map) {
               Map<?, ?> map = (Map)o;

               try {
                  this.records.add(SellRecord.deserialize(map));
               } catch (Exception var6) {
               }
            }
         }

      }
   }

   public void save() {
      List<Map<String, Object>> list = new ArrayList();

      for(SellRecord r : this.records) {
         list.add(r.serialize());
      }

      PlayerDataStore.get().config().set("sell-history.records", list);
      PlayerDataStore.get().save();
   }

   public void add(SellRecord record) {
      this.records.add(record);
      int max = this.plugin.getConfig().getInt("history.max-entries", 100);
      List<SellRecord> mine = (List)this.records.stream().filter((r) -> r.getPlayerId().equals(record.getPlayerId())).sorted(Comparator.comparingLong(SellRecord::getTime).reversed()).collect(Collectors.toList());
      if (mine.size() > max) {
         for(int i = max; i < mine.size(); ++i) {
            this.records.remove(mine.get(i));
         }
      }

   }

   public List<SellRecord> get(UUID player) {
      return (List)this.records.stream().filter((r) -> r.getPlayerId().equals(player)).sorted(Comparator.comparingLong(SellRecord::getTime).reversed()).collect(Collectors.toList());
   }
}
