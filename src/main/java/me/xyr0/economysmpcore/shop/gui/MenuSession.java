package me.xyr0.economysmpcore.shop.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.xyr0.economysmpcore.util.SessionUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class MenuSession {
   public static final Map<UUID, View> VIEW = new ConcurrentHashMap();
   public static final Map<UUID, String> SHOP_ID = new ConcurrentHashMap();
   public static final Map<UUID, BuyData> BUY = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, VIEW, SHOP_ID, BUY);
   }

   public static enum View {
      SHOP,
      BUY,
      PAGE_SELECT;

      // $FF: synthetic method
      private static View[] $values() {
         return new View[]{SHOP, BUY, PAGE_SELECT};
      }
   }

   public static final class BuyData {
      public final String shopId;
      public final String slotKey;
      public final Material material;
      public final String displayName;
      public final double unitPrice;
      public final String takeCommand;
      public final String giveCommand;
      public final String economy;
      public final String symbol;
      public final String balancePlaceholder;
      public final int maxAmount;
      public final ItemStack template;
      public int amount = 1;

      public BuyData(String shopId, String slotKey, Material material, String displayName, double unitPrice, String takeCommand, String giveCommand, String economy, String symbol, String balancePlaceholder, int maxAmount, ItemStack template) {
         this.shopId = shopId;
         this.slotKey = slotKey;
         this.material = material;
         this.displayName = displayName;
         this.unitPrice = unitPrice;
         this.takeCommand = takeCommand;
         this.giveCommand = giveCommand;
         this.economy = economy;
         this.symbol = symbol;
         this.balancePlaceholder = balancePlaceholder;
         this.maxAmount = Math.max(1, maxAmount);
         this.template = template == null ? new ItemStack(material) : template.clone();
      }

      public ItemStack buildStack(int amount) {
         ItemStack stack = this.template.clone();
         stack.setAmount(Math.max(1, Math.min(amount, stack.getMaxStackSize())));
         return stack;
      }
   }
}
