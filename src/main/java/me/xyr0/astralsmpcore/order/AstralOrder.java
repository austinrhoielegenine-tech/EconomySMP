package me.xyr0.astralsmpcore.order;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import me.xyr0.astralsmpcore.SystemHost;
import me.xyr0.astralsmpcore.order.command.OrdersCommand;
import me.xyr0.astralsmpcore.order.gui.MenuSession;
import me.xyr0.astralsmpcore.order.gui.OrderMenus;
import me.xyr0.astralsmpcore.order.listener.ChatInputListener;
import me.xyr0.astralsmpcore.order.listener.GuiListener;
import me.xyr0.astralsmpcore.order.util.InputUtil;
import me.xyr0.astralsmpcore.util.EconomyUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AstralOrder extends SystemHost {
   private static AstralOrder instance;
   private OrderManager orderManager;
   private MessageManager messages;
   private EconomyHook economy;
   private PriceManager prices;
   private WebhookManager webhooks;
   private BlockManager blocks;
   private InputUtil input;

   public AstralOrder() {
      super("order");
   }

   public void start() {
      instance = this;
      this.saveDefaultConfig();
      if (!(new File(this.getDataFolder(), "messages.yml")).exists()) {
         this.saveResource("messages.yml", false);
      }

      this.saveGuiDefaults();
      this.reloadConfig();
      this.economy = new EconomyHook();
      if (!this.economy.setup()) {
         this.getLogger().severe("Economy not ready — using built-in when available.");
         this.getLogger().warning("[system] init warning (continuing)");
      }

      this.messages = new MessageManager(this);
      this.orderManager = new OrderManager(this);
      this.orderManager.startAutoSave();
      this.prices = new PriceManager(this);
      this.blocks = new BlockManager(this);
      this.webhooks = new WebhookManager(this);
      this.input = new InputUtil(this);
      new OrdersCommand(this);
      new AstralOrderCommand(this);
      this.getServer().getPluginManager().registerEvents(new GuiListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(new ChatInputListener(this), this.getCore());
      this.getServer().getPluginManager().registerEvents(this.input, this.getCore());
      this.getLogger().info("AstralOrder finished loading.");
   }

   public void stop() {
      for(UUID id : new ArrayList<>(MenuSession.CHEST_SNAPSHOT.keySet())) {
         Player p = this.getServer().getPlayer(id);
         if (p != null) {
            ItemStack[] snapshot = (ItemStack[])MenuSession.CHEST_SNAPSHOT.remove(id);
            if (snapshot != null) {
               for(ItemStack stack : snapshot) {
                  if (stack != null && stack.getType() != Material.AIR && stack.getType() != Material.PAPER) {
                     Map<Integer, ItemStack> left = p.getInventory().addItem(new ItemStack[]{stack});
                     left.values().forEach((i) -> p.getWorld().dropItemNaturally(p.getLocation(), i));
                  }
               }
            }
         }
      }

      MenuSession.VIEW.clear();
      if (this.orderManager != null) {
         this.orderManager.save();
      }

      instance = null;
   }

   public void reloadAll() {
      if (this.webhooks != null) {
         this.webhooks.reload();
      }

      this.reloadConfig();
      if (this.messages != null) {
         this.messages.reload();
      }

      if (this.prices != null) {
         this.prices.reload();
      }

   }

   public static AstralOrder get() {
      return instance;
   }

   public OrderManager getOrderManager() {
      return this.orderManager;
   }

   public MessageManager getMessages() {
      return this.messages;
   }

   public EconomyHook getEconomy() {
      return this.economy;
   }

   private void saveGuiDefaults() {
      String[] files = new String[]{"gui/order-view.yml", "gui/your-orders.yml", "gui/new-order.yml", "gui/confirm-delivery.yml", "gui/confirm-cancel.yml", "gui/collect-items.yml", "gui/edit-order.yml", "gui/list-materials.yml", "gui/deliver-items.yml", "gui/confirm-sell.yml", "gui/admin-gui.yml", "gui/enchantment-gui.yml", "items.yml", "prices.yml", "webhook.yml"};

      for(String name : files) {
         File out = new File(this.getDataFolder(), name);
         if (!out.exists()) {
            out.getParentFile().mkdirs();
            this.saveResource(name, false);
         }
      }

   }

   public void handleInput(Player player, MenuSession.ChatMode mode, String msg) {
      UUID id = player.getUniqueId();
      MenuSession.CHAT.put(id, MenuSession.ChatMode.NONE);
      if (msg == null) {
         msg = "";
      }

      msg = msg.trim();
      if (msg.equalsIgnoreCase("cancel")) {
         this.messages.send(player, "chat-input-cancel");
         MenuSession.View ret = (MenuSession.View)MenuSession.INPUT_RETURN.remove(id);
         if (mode == MenuSession.ChatMode.SEARCH) {
            if (ret == MenuSession.View.MATERIALS) {
               OrderMenus.openMaterials(player, 0);
            } else {
               OrderMenus.openBrowse(player, 0);
            }
         } else {
            OrderMenus.openNewOrder(player);
         }

      } else if (mode == MenuSession.ChatMode.SEARCH) {
         MenuSession.SEARCH.put(id, msg);
         MenuSession.View ret = (MenuSession.View)MenuSession.INPUT_RETURN.remove(id);
         if (ret == MenuSession.View.MATERIALS) {
            OrderMenus.openMaterials(player, 0);
         } else {
            OrderMenus.openBrowse(player, 0);
         }

      } else if (mode == MenuSession.ChatMode.AMOUNT) {
         try {
            int amount = Integer.parseInt(msg.replace(",", "").replace(" ", ""));
            int min = Math.max(1, this.getConfig().getInt("minimum-item-per-order", 1));
            int max = this.getConfig().getInt("maximum-item-per-order", -1);
            if (max < 0) {
               max = Integer.MAX_VALUE;
            }

            if (amount < min) {
               this.messages.send(player, "amount-too-low", Map.of("min", String.valueOf(min)));
            } else if (amount > max) {
               this.messages.send(player, "amount-too-high", Map.of("max", String.valueOf(max)));
            } else {
               MenuSession.DRAFT_AMOUNT.put(id, amount);
               this.messages.send(player, "amount-set", Map.of("amount", String.valueOf(amount)));
            }
         } catch (Exception var8) {
            this.messages.send(player, "error");
         }

         MenuSession.INPUT_RETURN.remove(id);
         OrderMenus.openNewOrder(player);
      } else {
         if (mode == MenuSession.ChatMode.PRICE) {
            try {
               double price = EconomyUtil.parseCompact(msg);
               MenuSession.DRAFT_PRICE.put(id, (double)Math.round(price * (double)100.0F) / (double)100.0F);
               this.messages.send(player, "price-set", Map.of("price", String.format("%.2f", price)));
            } catch (Exception var9) {
               this.messages.send(player, "error");
            }

            MenuSession.INPUT_RETURN.remove(id);
            OrderMenus.openNewOrder(player);
         }

      }
   }

   public PriceManager getPrices() {
      return this.prices;
   }

   public BlockManager getBlocks() {
      return this.blocks;
   }

   public InputUtil getInput() {
      return this.input;
   }

   public WebhookManager getWebhooks() {
      return this.webhooks;
   }
}
