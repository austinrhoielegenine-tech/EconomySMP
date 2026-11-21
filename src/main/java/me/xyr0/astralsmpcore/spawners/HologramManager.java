package me.xyr0.astralsmpcore.spawners;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.TextDisplay.TextAlignment;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class HologramManager {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
   private final AstralSpawners plugin;
   private final Map<UUID, UUID> hologramIds = new ConcurrentHashMap();
   private int task = -1;

   public HologramManager(AstralSpawners plugin) {
      this.plugin = plugin;
   }

   public void start() {
      int interval = this.plugin.getSettings().getHologramUpdateInterval();
      this.task = Tasks.runTimer(this.plugin, this::refreshAll, (long)interval, (long)interval);
   }

   public void shutdown() {
      if (this.task != -1) {
         Tasks.cancel(this.task);
         this.task = -1;
      }

      for(UUID entityId : this.hologramIds.values()) {
         Entity e = Bukkit.getEntity(entityId);
         if (e != null) {
            e.remove();
         }
      }

      this.hologramIds.clear();
   }

   public void create(SpawnerData data) {
      this.remove(data);
      Location loc = data.getLocation();
      if (loc != null && loc.getWorld() != null) {
         Location holoLoc = loc.clone().add((double)0.5F, 1.4, (double)0.5F);
         TextDisplay display = (TextDisplay)loc.getWorld().spawn(holoLoc, TextDisplay.class, (td) -> {
            td.setBillboard(Billboard.CENTER);
            td.setShadowed(true);
            td.setSeeThrough(false);
            td.setDefaultBackground(false);
            td.setAlignment(TextAlignment.CENTER);
            td.setTransformation(new Transformation(new Vector3f(0.0F, 0.0F, 0.0F), new AxisAngle4f(), new Vector3f(0.8F, 0.8F, 0.8F), new AxisAngle4f()));
            td.text(LEGACY.deserialize(this.buildText(data)));
         });
         this.hologramIds.put(data.getId(), display.getUniqueId());
      }
   }

   public void update(SpawnerData data) {
      UUID entityId = (UUID)this.hologramIds.get(data.getId());
      if (entityId == null) {
         this.create(data);
      } else {
         Entity e = Bukkit.getEntity(entityId);
         if (e instanceof TextDisplay) {
            TextDisplay td = (TextDisplay)e;
            td.text(LEGACY.deserialize(this.buildText(data)));
         } else {
            this.create(data);
         }
      }
   }

   public void remove(SpawnerData data) {
      UUID entityId = (UUID)this.hologramIds.remove(data.getId());
      if (entityId != null) {
         Entity e = Bukkit.getEntity(entityId);
         if (e != null) {
            e.remove();
         }
      }

   }

   public void refreshAll() {
      for(SpawnerData data : this.plugin.getSpawnerManager().getAll()) {
         if (this.hologramIds.containsKey(data.getId())) {
            this.update(data);
         } else {
            this.create(data);
         }
      }

   }

   private String buildText(SpawnerData data) {
      EntityDefinition def = this.plugin.getEntities().get(data.getEntityType());
      String name = def != null ? def.getDisplay() : data.getEntityType().name();
      String line1 = this.plugin.getMessages().raw("hologram-line1").replace("%entity%", name);
      String line2 = this.plugin.getMessages().raw("hologram-line2").replace("%stack%", String.valueOf(data.getStackSize())).replace("%items%", String.valueOf(data.getTotalItems())).replace("%exp%", String.valueOf(data.getStoredExp()));
      return line1 + "\n" + line2;
   }
}
