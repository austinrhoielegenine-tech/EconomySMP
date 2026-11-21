package me.xyr0.astralsmpcore.order;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import me.xyr0.astralsmpcore.data.PlayerDataStore;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OrderManager {
   private final AstralOrder plugin;
   private final Map<UUID, Order> orders = new ConcurrentHashMap();
   private File file;
   private FileConfiguration data;

   public OrderManager(AstralOrder plugin) {
      this.plugin = plugin;
      this.load();
   }

   public void load() {
      this.orders.clear();
      this.file = PlayerDataStore.get().file();
      this.data = PlayerDataStore.get().config();
      List<?> list;
      synchronized(PlayerDataStore.get().lock()) {
         list = this.data.getList("orders");
      }

      if (list != null) {
         for(Object obj : list) {
            if (obj instanceof Map) {
               Map<?, ?> map = (Map)obj;

               try {
                  Order order = Order.deserialize((Map<String, Object>)map);
                  this.orders.put(order.getId(), order);
               } catch (Exception var7) {
                  this.plugin.getLogger().warning("Could not load an order entry");
               }
            }
         }

      }
   }

   public void save() {
      if (this.data != null && this.file != null) {
         List<Map<String, Object>> list = new ArrayList();

         for(Order order : this.orders.values()) {
            list.add(order.serialize());
         }

         PlayerDataStore.get().editAndSave((cfg) -> cfg.set("orders", list));
      }
   }

   public Collection<Order> getAll() {
      return this.orders.values();
   }

   public List<Order> getOpen() {
      return (List)this.orders.values().stream().filter((o) -> o.getRemaining() > 0).sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt())).collect(Collectors.toList());
   }

   public List<Order> getByOwner(UUID owner) {
      return (List)this.orders.values().stream().filter((o) -> o.getOwner().equals(owner)).sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt())).collect(Collectors.toList());
   }

   public Order get(UUID id) {
      return (Order)this.orders.get(id);
   }

   public int countOpen(UUID owner) {
      int n = 0;

      for(Order o : this.orders.values()) {
         if (o.getOwner().equals(owner) && o.getRemaining() > 0) {
            ++n;
         }
      }

      return n;
   }

   public Order create(Player player, ItemStack template, int amount, double priceEach) {
      Order order = new Order(UUID.randomUUID(), player.getUniqueId(), player.getName(), template, amount, priceEach);
      this.orders.put(order.getId(), order);
      return order;
   }

   public void remove(UUID id) {
      this.orders.remove(id);
   }

   public void startAutoSave() {
      int seconds = this.plugin.getConfig().getInt("storage.auto-save-seconds", 120);
      if (seconds <= 0) {
         seconds = 120;
      }

      Tasks.runAsyncTimer(this.plugin.getCore(), this::save, (long)seconds * 20L, (long)seconds * 20L);
   }
}
