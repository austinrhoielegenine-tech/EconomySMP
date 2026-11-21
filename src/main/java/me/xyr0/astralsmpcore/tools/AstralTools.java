package me.xyr0.astralsmpcore.tools;

import java.io.File;
import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.tools.command.ToolsCommand;
import me.xyr0.astralsmpcore.tools.listener.FireworkBoostListener;
import me.xyr0.astralsmpcore.tools.listener.GuiListener;
import me.xyr0.astralsmpcore.tools.listener.ToolListener;
import me.xyr0.astralsmpcore.tools.util.MessageUtil;
import me.xyr0.astralsmpcore.tools.util.ToolItemFactory;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AstralTools extends SystemHost {
   private static AstralTools instance;
   private ToolManager toolManager;
   private ToolItemFactory toolFactory;
   private FileConfiguration messages;
   private FileConfiguration guiConfig;
   private FileConfiguration settingsConfig;

   public AstralTools() {
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
      if (this.getCommand("astraltools") != null) {
      }

      this.getServer().getPluginManager().registerEvents(new ToolListener(this, this.toolFactory), this.getCore());
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new FireworkBoostListener(), this.getCore());
      this.startExpiryTask();
      this.getLogger().info("AstralTools enabled.");
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

   public static AstralTools get() {
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
