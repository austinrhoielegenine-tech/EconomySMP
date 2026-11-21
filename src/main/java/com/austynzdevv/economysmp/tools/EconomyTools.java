package com.austynzdevv.economysmp.tools;

import java.io.File;
import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.tools.command.ToolsCommand;
import com.austynzdevv.economysmp.tools.listener.FireworkBoostListener;
import com.austynzdevv.economysmp.tools.listener.GuiListener;
import com.austynzdevv.economysmp.tools.listener.ToolListener;
import com.austynzdevv.economysmp.tools.util.MessageUtil;
import com.austynzdevv.economysmp.tools.util.ToolItemFactory;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EconomyTools extends SystemHost {
   private static EconomyTools instance;
   private ToolManager toolManager;
   private ToolItemFactory toolFactory;
   private FileConfiguration messages;
   private FileConfiguration guiConfig;
   private FileConfiguration settingsConfig;

   public EconomyTools() {
      super("tools");
   }

   public void stop() {
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      this.saveResourceIfMissing("messages.yml");
      this.saveResourceIfMissing("gui.yml");

      for(String id : new String[]{"drill", "treechopper", "shovel", "hoe", "enderpearl", "rocket", "waterbucket", "lavabucket", "bucket", "jump"}) {
         this.saveResourceIfMissing("tools/" + id + ".yml");
      }

      this.reloadConfigs();
      this.toolManager = new ToolManager(this);
      this.toolFactory = new ToolItemFactory(this);
      new ToolsCommand(this);
      if (this.getCommand("economytools") != null) {
      }

      this.getServer().getPluginManager().registerEvents(new ToolListener(this, this.toolFactory), this.getCore());
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new FireworkBoostListener(), this.getCore());
      this.startExpiryTask();
      this.getLogger().info("EconomyTools enabled.");
   }

   private void saveResourceIfMissing(String path) {
      File f = new File(this.getDataFolder(), path);
      if (!f.exists()) {
         this.saveResource(path, false);
      }

   }

   public void reloadConfigs() {
      this.reloadConfig();
      this.messages = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "messages.yml"));
      this.guiConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "gui.yml"));
      this.settingsConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
   }

   public void reloadAll() {
      this.reloadConfigs();
      if (this.toolManager != null) {
         this.toolManager.reload();
      }

   }

   public static EconomyTools get() {
      return instance;
   }

   public ToolManager getToolManager() {
      return this.toolManager;
   }

   public ToolItemFactory getToolFactory() {
      return this.toolFactory;
   }

   public FileConfiguration getMessages() {
      return this.messages;
   }

   private void startExpiryTask() {
      Tasks.runTimer(this.getCore(), () -> {
         for(Player player : this.getServer().getOnlinePlayers()) {
            for(int i = 0; i < player.getInventory().getSize(); ++i) {
               ItemStack item = player.getInventory().getItem(i);
               if (item != null && this.toolFactory.isTool(item)) {
                  String type = this.toolFactory.getType(item);
                  ToolDefinition def = this.toolManager.get(type);
                  if (def != null) {
                     if (this.toolFactory.isExpired(item, def)) {
                        player.getInventory().setItem(i, (ItemStack)null);
                        MessageUtil.send(player, "tool-expired");
                     } else if (def.getMode() == ToolDefinition.DurabilityMode.TIME) {
                        this.toolFactory.refreshLore(item, def);
                        player.getInventory().setItem(i, item);
                     }
                  }
               }
            }
         }

         for(World world : this.getServer().getWorlds()) {
            for(Item drop : world.getEntitiesByClass(Item.class)) {
               ItemStack stack = drop.getItemStack();
               if (this.toolFactory.isTool(stack)) {
                  String type = this.toolFactory.getType(stack);
                  ToolDefinition def = this.toolManager.get(type);
                  if (def != null) {
                     if (this.toolFactory.isExpired(stack, def)) {
                        drop.remove();
                     } else if (def.getMode() == ToolDefinition.DurabilityMode.TIME) {
                        this.toolFactory.refreshLore(stack, def);
                        drop.setItemStack(stack);
                     }
                  }
               }
            }
         }

      }, 40L, 40L);
   }

   public FileConfiguration getGuiConfig() {
      return this.guiConfig;
   }

   public FileConfiguration getSettingsConfig() {
      return this.settingsConfig;
   }
}
