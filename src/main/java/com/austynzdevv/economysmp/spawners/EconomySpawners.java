package com.austynzdevv.economysmp.spawners;

import java.util.logging.Logger;
import com.austynzdevv.economysmp.SystemHost;
import com.austynzdevv.economysmp.spawners.command.EconomyCommand;
import com.austynzdevv.economysmp.spawners.listener.GenerationTask;
import com.austynzdevv.economysmp.spawners.listener.GuiListener;
import com.austynzdevv.economysmp.spawners.listener.SaveTask;
import com.austynzdevv.economysmp.spawners.listener.SpawnerListener;
import com.austynzdevv.economysmp.util.Tasks;
import org.bukkit.Bukkit;

public final class EconomySpawners extends SystemHost {
   private static EconomySpawners instance;
   private SettingsConfig settings;
   private EntityConfig entities;
   private MessageConfig messages;
   private SpawnerManager spawnerManager;
   private SpawnerStorage storage;
   private HologramManager hologramManager;
   private GenerationTask generationTask;
   private int generationTaskId = -1;
   private boolean vaultPresent;
   private EconomyHook economy;

   public EconomySpawners() {
      super("spawners");
   }

   public void start() {
      instance = this;
      if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
         this.getLogger().severe("Could not create plugin folder.");
         this.getLogger().warning("spawners init issue");
      } else {
         this.saveDefaultConfigs();
         this.settings = new SettingsConfig(this);
         this.entities = new EntityConfig(this);
         this.messages = new MessageConfig(this);
         this.storage = new SpawnerStorage(this);
         this.spawnerManager = new SpawnerManager(this);
         this.hologramManager = new HologramManager(this);
         this.vaultPresent = Bukkit.getPluginManager().getPlugin("Vault") != null;
         this.economy = new EconomyHook();
         if (this.vaultPresent) {
            if (!this.economy.setup()) {
               this.getLogger().warning("Vault found but no economy provider (install EssentialsX). Sell will not pay.");
            }
         } else {
            this.getLogger().warning("Vault not found. Sell all will not deposit money.");
         }

         this.storage.load();
         this.spawnerManager.loadAll();
         new EconomyCommand(this);
         Bukkit.getPluginManager().registerEvents(new SpawnerListener(this), this.getCore());
         Bukkit.getPluginManager().registerEvents(new GuiListener(this), this.getCore());
         long interval = Math.max(20L, this.settings.getGenerationInterval());
         this.generationTask = new GenerationTask(this);
         this.generationTaskId = Tasks.runTimer(this, this.generationTask, interval, interval);
         long saveInterval = Math.max(60L, (long)this.settings.getSaveInterval()) * 20L;
         Tasks.runAsyncTimer(this, new SaveTask(this), saveInterval, saveInterval);
         if (this.settings.isHolograms()) {
            this.hologramManager.start();
         }

         String soft = "";
         if (this.vaultPresent) {
            soft = soft + " Vault";
         }

         if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            soft = soft + " Floodgate";
         }

         Logger var10000 = this.getLogger();
         String var10001 = this.getDescription().getVersion();
         var10000.info("EconomySpawners v" + var10001 + " enabled — " + this.spawnerManager.size() + " spawners loaded." + (soft.isEmpty() ? "" : " Hooks:" + soft));
      }
   }

   public void stop() {
      if (this.generationTaskId != -1) {
         Tasks.cancel(this.generationTaskId);
         this.generationTaskId = -1;
      }

      this.generationTask = null;
      if (this.hologramManager != null) {
         this.hologramManager.shutdown();
      }

      if (this.spawnerManager != null) {
         this.spawnerManager.saveAll();
      }

      if (this.storage != null) {
         this.storage.save();
      }

      instance = null;
   }

   private void saveDefaultConfigs() {
      this.saveResource("config.yml", false);
      this.saveResource("messages.yml", false);
      this.saveResource("guis/spawnergui.yml", false);
      this.saveResource("guis/confirmsell.yml", false);
      this.saveResource("guis/spawnerpanel.yml", false);
   }

   public void reload() {
      this.settings.reload();
      this.entities.reload();
      this.messages.reload();
      if (this.settings.isHolograms()) {
         this.hologramManager.refreshAll();
      } else {
         this.hologramManager.shutdown();
      }

   }

   public static EconomySpawners get() {
      return instance;
   }

   public SettingsConfig getSettings() {
      return this.settings;
   }

   public EntityConfig getEntities() {
      return this.entities;
   }

   public MessageConfig getMessages() {
      return this.messages;
   }

   public SpawnerManager getSpawnerManager() {
      return this.spawnerManager;
   }

   public SpawnerStorage getStorage() {
      return this.storage;
   }

   public HologramManager getHologramManager() {
      return this.hologramManager;
   }

   public EconomyHook getEconomy() {
      return this.economy;
   }

   public boolean isVaultPresent() {
      return this.vaultPresent;
   }
}
