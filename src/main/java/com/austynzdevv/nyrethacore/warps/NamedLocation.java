package com.austynzdevv.nyrethacore.warps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class NamedLocation {
   private final String name;
   private final String world;
   private final double x;
   private final double y;
   private final double z;
   private final float yaw;
   private final float pitch;
   private final int maxPlayers;

   public NamedLocation(String name, Location loc, int maxPlayers) {
      this.name = name;
      this.world = loc.getWorld() != null ? loc.getWorld().getName() : "world";
      this.x = loc.getX();
      this.y = loc.getY();
      this.z = loc.getZ();
      this.yaw = loc.getYaw();
      this.pitch = loc.getPitch();
      this.maxPlayers = maxPlayers;
   }

   public NamedLocation(String name, String world, double x, double y, double z, float yaw, float pitch, int maxPlayers) {
      this.name = name;
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.maxPlayers = maxPlayers;
   }

   public String getName() {
      return this.name;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public Location toLocation() {
      World w = Bukkit.getWorld(this.world);
      return w == null ? null : new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
   }

   public int currentPlayers() {
      Location loc = this.toLocation();
      if (loc != null && loc.getWorld() != null) {
         int count = 0;

         for(Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= (double)4096.0F) {
               ++count;
            }
         }

         return count;
      } else {
         return 0;
      }
   }

   public boolean isFull() {
      return this.maxPlayers > 0 && this.currentPlayers() >= this.maxPlayers;
   }
}
