package me.xyr0.economysmpcore.sell;

import java.io.File;
import me.xyr0.economysmpcore.SystemHost;
import me.xyr0.economysmpcore.sell.command.AdminCommand;
import me.xyr0.economysmpcore.sell.command.WorthCommand;
import me.xyr0.economysmpcore.sell.listener.ChatListener;
import me.xyr0.economysmpcore.sell.listener.GuiListener;
import me.xyr0.economysmpcore.sell.listener.SellAxeListener;
import me.xyr0.economysmpcore.sell.listener.SellAxeTask;
import me.xyr0.economysmpcore.sell.listener.WorthDisplayListener;
import me.xyr0.economysmpcore.sell.placeholders.SellPlaceholders;
import me.xyr0.economysmpcore.util.Tasks;

public final class AstralSell extends SystemHost {
   private static AstralSell instance;
   private EconomyHook economy;
   private MessageManager messages;
   private PriceManager prices;
   private BlacklistManager blacklist;
   private HistoryManager history;
   private FilterManager filters;
   private ProgressManager progress;
   private WorthDisplayListener worthDisplay;
   private SellPlaceholders placeholders;
   private SellAxeManager sellAxe;

   public AstralSell() {
      super("sell");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      if (!(new File(this.getDataFolder(), "sellaxe.yml")).exists()) {
         this.saveResource("sellaxe.yml", false);
      }

      this.saveDefaults();
      this.reloadConfig();
      this.economy = new EconomyHook();
      if (!this.economy.setup()) {
         this.getLogger().severe("Vault economy missing.");
         this.getLogger().warning("[system] init warning (continuing)");
      }

      this.messages = new MessageManager(this);
      this.prices = new PriceManager(this);
      this.blacklist = new BlacklistManager(this);
      this.history = new HistoryManager(this);
      this.filters = new FilterManager(this);
      this.progress = new ProgressManager(this);
      new WorthCommand(this);
      new AdminCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
         this.placeholders = new SellPlaceholders(this);
         this.placeholders.register();
         this.getLogger().info("PlaceholderAPI hooked.");
      }

      this.getServer().getPluginManager().registerEvents(new ChatListener(this), this.getCore());
      this.worthDisplay = new WorthDisplayListener(this);
      this.getServer().getPluginManager().registerEvents(this.worthDisplay, this.getCore());
      this.sellAxe = new SellAxeManager(this);
      this.getServer().getPluginManager().registerEvents(new SellAxeListener(this, this.sellAxe), this.getCore());
      Tasks.runTimer(this.getCore(), new SellAxeTask(this, this.sellAxe), 20L, 40L);
      Tasks.runAsyncTimer(this.getCore(), () -> {
         this.history.save();
         this.progress.saveAll();
      }, 2400L, 2400L);
      this.getLogger().info("AstralSell enabled.");
   }

   public void stop() {
      if (this.history != null) {
         this.history.save();
      }

      if (this.progress != null) {
         this.progress.saveAll();
      }

      instance = null;
   }

   private void saveDefaults() {
      String[] files = new String[]{"messages.yml", "prices.yml", "gui/sell.yml", "gui/worth.yml", "gui/history.yml", "gui/sellmulti.yml", "gui/progress.yml", "gui/category-items.yml", "level.yml", "prices/blocks.yml", "prices/ores.yml", "prices/crops.yml", "prices/mobs.yml", "prices/natural.yml", "prices/tools.yml", "prices/fish.yml", "prices/books.yml", "prices/potions.yml", "Worth/Worth-Filter.yml"};

      for(String name : files) {
         File out = new File(this.getDataFolder(), name);
         if (!out.exists()) {
            out.getParentFile().mkdirs();
            this.saveResource(name, false);
         }
      }

   }

   public void reloadAll() {
      this.reloadConfig();
      this.messages.reload();
      this.prices.reload();
      this.blacklist.reload();
      this.filters.reload();
      this.progress.reload();
      if (this.worthDisplay != null) {
         this.worthDisplay.reloadConfigValues();
      }

   }

   public static AstralSell get() {
      return instance;
   }

   public EconomyHook getEconomy() {
      return this.economy;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public PriceManager getPrices() {
      return this.prices;
   }

   public BlacklistManager getBlacklist() {
      return this.blacklist;
   }

   public HistoryManager getHistory() {
      return this.history;
   }

   public FilterManager getFilters() {
      return this.filters;
   }

   public WorthDisplayListener getWorthLore() {
      return this.worthDisplay;
   }

   public SellPlaceholders getPlaceholders() {
      return this.placeholders;
   }

   public ProgressManager getProgress() {
      return this.progress;
   }

   public SellAxeManager getSellAxe() {
      return this.sellAxe;
   }
}
