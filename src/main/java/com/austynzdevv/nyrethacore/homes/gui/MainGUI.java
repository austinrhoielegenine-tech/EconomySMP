package com.austynzdevv.nyrethacore.homes.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.austynzdevv.nyrethacore.homes.EconomyHomes;
import com.austynzdevv.nyrethacore.homes.StorageManager;
import com.austynzdevv.nyrethacore.teams.EconomyTeams;
import com.austynzdevv.nyrethacore.teams.Team;
import com.austynzdevv.nyrethacore.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MainGUI {
   private final EconomyHomes plugin;

   public MainGUI(EconomyHomes plugin) {
      this.plugin = plugin;
   }

   public void open(Player player) {
      this.open(player, player);
   }

   public void open(Player viewer, Player target) {
      FileConfiguration gui = this.plugin.configs().getMainGui();
      int rows = gui.getInt("size", 6);
      Inventory inv = Bukkit.createInventory(new Holder(target.getUniqueId(), false), rows * 9, ColorUtil.color(gui.getString("menu-title", "&8ʜᴏᴍᴇꜱ")));
      List<Integer> beds = this.slots(gui.getString("layout.beds", "11,12,13,14,15,29,30,31,32,33"));
      List<Integer> dyes = this.slots(gui.getString("layout.dyes", "20,21,22,23,24,38,39,40,41,42"));
      StorageManager.PlayerData data = this.plugin.storage().get(target);
      int max = this.plugin.homes().maxHomes(target);

      for(int i = 0; i < beds.size(); ++i) {
         int num = i + 1;
         String id = String.valueOf(num);
         boolean allowed = num <= max;
         boolean set = data.has(id);
         String path;
         if (!allowed) {
            path = "locked";
         } else if (set) {
            path = "filled";
         } else {
            path = "empty";
         }

         if (set) {
         }

         int bedSlot = (Integer)beds.get(i);
         if (bedSlot >= 0 && bedSlot < inv.getSize()) {
            inv.setItem(bedSlot, this.item(gui, path + ".bed", id, id));
         }

         if (i < dyes.size()) {
            int dyeSlot = (Integer)dyes.get(i);
            if (dyeSlot >= 0 && dyeSlot < inv.getSize()) {
               inv.setItem(dyeSlot, this.item(gui, path + ".dye", id, id));
            }
         }
      }

      boolean hasTeamHome = false;

      try {
         EconomyTeams teams = EconomyTeams.get();
         if (teams != null && teams.getTeams() != null) {
            Team team = teams.getTeams().getByPlayer(target.getUniqueId());
            hasTeamHome = team != null && team.getHome() != null;
         }
      } catch (Exception var19) {
      }

      Material bannerMat = hasTeamHome ? Material.LIGHT_BLUE_BANNER : Material.GRAY_BANNER;
      Material dyeMat = hasTeamHome ? Material.LIGHT_BLUE_DYE : Material.GRAY_DYE;
      String bName = hasTeamHome ? "&#00A4FCTeam Home" : "&7Team Home";
      String bannerLore = hasTeamHome ? "&7Click to teleport to team home" : "&7Click to set team home";
      String dyeLore = hasTeamHome ? "&7Click to delete team home" : "&7Click to set team home";
      inv.setItem(10, this.simple(bannerMat, bName, bannerLore));
      inv.setItem(19, this.simple(dyeMat, bName, dyeLore));
      viewer.openInventory(inv);
      this.plugin.homes().sound(viewer, "interact");
   }

   private ItemStack simple(Material mat, String name, String lore) {
      ItemStack stack = new ItemStack(mat);
      ItemMeta meta = stack.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(ColorUtil.color(name));
         meta.setLore(List.of(ColorUtil.color(lore)));
         stack.setItemMeta(meta);
      }

      return stack;
   }

   private ItemStack item(FileConfiguration gui, String path, String id, String display) {
      Material mat;
      try {
         mat = Material.valueOf(gui.getString(path + ".item", "STONE").toUpperCase());
      } catch (Exception var9) {
         mat = Material.STONE;
      }

      ItemStack stack = new ItemStack(mat);
      ItemMeta meta = stack.getItemMeta();
      if (meta != null) {
         meta.setDisplayName(ColorUtil.color(gui.getString(path + ".label", "StorageManager.Home %home%").replace("%home%", display).replace("%id%", id)));
         List<String> lore = gui.getStringList(path + ".extra");
         if (lore != null && !lore.isEmpty()) {
            meta.setLore((List)lore.stream().map((l) -> ColorUtil.color(l.replace("%home%", display).replace("%id%", id))).collect(Collectors.toList()));
         }

         stack.setItemMeta(meta);
      }

      return stack;
   }

   private List<Integer> slots(String raw) {
      List<Integer> out = new ArrayList();
      if (raw != null && !raw.isEmpty()) {
         for(String part : raw.split(",")) {
            try {
               out.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException var8) {
            }
         }

         return out;
      } else {
         return out;
      }
   }

   public static final class Holder implements InventoryHolder {
      public final UUID target;
      public final boolean confirm;
      public String homeId;
      public boolean teamHome;

      public Holder(UUID target, boolean confirm) {
         this.target = target;
         this.confirm = confirm;
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
