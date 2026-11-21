package com.austynzdevv.novacore.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

public class ToolDefinition {
   private final String id;
   private final String name;
   private final List<String> lore;
   private final Material material;
   private final int customModelData;
   private final DurabilityMode mode;
   private final int uses;
   private final long durationMs;
   private final Map<Enchantment, Integer> enchants;
   private final boolean soundEnabled;
   private final Sound sound;
   private final float volume;
   private final float pitch;
   private final boolean particleEnabled;
   private final Particle particle;
   private final int particleCount;
   private final int jumpDistance;
   private final int jumpCooldown;
   private final int rocketDuration;
   private final double rocketBoost;
   private final boolean additionalParticleEnabled;
   private final Particle additionalParticle;
   private final int additionalParticleCount;

   public ToolDefinition(String id, FileConfiguration cfg) {
      this.id = id;
      this.name = cfg.getString("name", id);
      this.lore = cfg.getStringList("lore");
      this.material = Material.matchMaterial(cfg.getString("material", "NETHERITE_PICKAXE"));
      this.customModelData = cfg.getInt("custom-model-data", 0);
      DurabilityMode var10001;
      switch (cfg.getString("durability.mode", "infinite").toLowerCase(Locale.ROOT)) {
         case "uses" -> var10001 = ToolDefinition.DurabilityMode.USES;
         case "time" -> var10001 = ToolDefinition.DurabilityMode.TIME;
         default -> var10001 = ToolDefinition.DurabilityMode.INFINITE;
      }

      this.mode = var10001;
      this.uses = cfg.getInt("durability.uses", 100);
      this.durationMs = parseTime(cfg.getString("durability.time", "3d"));
      this.enchants = new HashMap();
      if (cfg.getBoolean("enchantments.enabled", false)) {
         for(String line : cfg.getStringList("enchantments.list")) {
            String[] p = line.split(":");
            if (p.length >= 1) {
               Enchantment ench = null;

               try {
                  ench = (Enchantment)Registry.ENCHANTMENT.get(NamespacedKey.minecraft(p[0].toLowerCase(Locale.ROOT)));
               } catch (Exception var12) {
               }

               if (ench != null) {
                  int lvl = p.length > 1 ? Integer.parseInt(p[1]) : 1;
                  this.enchants.put(ench, lvl);
               }
            }
         }
      }

      this.soundEnabled = cfg.getBoolean("sound.enabled", false);
      Sound s = null;

      try {
         s = Sound.valueOf(cfg.getString("sound.type", "UI_BUTTON_CLICK"));
      } catch (Exception var11) {
      }

      this.sound = s;
      this.volume = (float)cfg.getDouble("sound.volume", (double)1.0F);
      this.pitch = (float)cfg.getDouble("sound.pitch", (double)1.0F);
      this.particleEnabled = cfg.getBoolean("particle.enabled", false);
      Particle part = null;

      try {
         part = Particle.valueOf(cfg.getString("particle.type", "CRIT"));
      } catch (Exception var10) {
      }

      this.particle = part;
      this.particleCount = cfg.getInt("particle.count", 10);
      this.jumpDistance = cfg.getInt("jump.distance", 4);
      this.jumpCooldown = cfg.getInt("jump.cooldown", 15);
      this.rocketDuration = cfg.getInt("rocket.flight-duration", 1);
      this.rocketBoost = cfg.getDouble("rocket.boost-power", (double)1.5F);
      this.additionalParticleEnabled = cfg.getBoolean("particle.additional-particle.enabled", false);
      Particle ap = null;

      try {
         ap = Particle.valueOf(cfg.getString("particle.additional-particle.type", "CRIT"));
      } catch (Exception var9) {
      }

      this.additionalParticle = ap;
      this.additionalParticleCount = cfg.getInt("particle.additional-particle.count", 10);
   }

   private static long parseTime(String raw) {
      if (raw != null && !raw.isEmpty()) {
         raw = raw.trim().toLowerCase(Locale.ROOT);

         try {
            if (raw.endsWith("d")) {
               return Long.parseLong(raw.replace("d", "")) * 24L * 60L * 60L * 1000L;
            } else if (raw.endsWith("h")) {
               return Long.parseLong(raw.replace("h", "")) * 60L * 60L * 1000L;
            } else if (raw.endsWith("m")) {
               return Long.parseLong(raw.replace("m", "")) * 60L * 1000L;
            } else {
               return raw.endsWith("s") ? Long.parseLong(raw.replace("s", "")) * 1000L : Long.parseLong(raw) * 1000L;
            }
         } catch (Exception var2) {
            return 259200000L;
         }
      } else {
         return 259200000L;
      }
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getLore() {
      return (List<String>)(this.lore == null ? new ArrayList() : this.lore);
   }

   public Material getMaterial() {
      return this.material == null ? Material.STICK : this.material;
   }

   public int getCustomModelData() {
      return this.customModelData;
   }

   public DurabilityMode getMode() {
      return this.mode;
   }

   public int getUses() {
      return this.uses;
   }

   public long getDurationMs() {
      return this.durationMs;
   }

   public Map<Enchantment, Integer> getEnchants() {
      return this.enchants;
   }

   public boolean isSoundEnabled() {
      return this.soundEnabled;
   }

   public Sound getSound() {
      return this.sound;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public boolean isParticleEnabled() {
      return this.particleEnabled;
   }

   public Particle getParticle() {
      return this.particle;
   }

   public int getParticleCount() {
      return this.particleCount;
   }

   public int getJumpDistance() {
      return this.jumpDistance;
   }

   public int getJumpCooldown() {
      return this.jumpCooldown;
   }

   public int getRocketDuration() {
      return this.rocketDuration;
   }

   public double getRocketBoost() {
      return this.rocketBoost;
   }

   public boolean isAdditionalParticleEnabled() {
      return this.additionalParticleEnabled;
   }

   public Particle getAdditionalParticle() {
      return this.additionalParticle;
   }

   public int getAdditionalParticleCount() {
      return this.additionalParticleCount;
   }

   public static enum DurabilityMode {
      INFINITE,
      USES,
      TIME;

      // $FF: synthetic method
      private static DurabilityMode[] $values() {
         return new DurabilityMode[]{INFINITE, USES, TIME};
      }
   }
}
