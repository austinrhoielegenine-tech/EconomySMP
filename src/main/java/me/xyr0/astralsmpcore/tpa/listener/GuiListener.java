package me.xyr0.astralsmpcore.tpa.listener;

import java.util.UUID;
import me.xyr0.astralsmpcore.tpa.AstralTPA;
import me.xyr0.astralsmpcore.tpa.TpaManager;
import me.xyr0.astralsmpcore.tpa.TpaRequest;
import me.xyr0.astralsmpcore.tpa.gui.MenuSession;
import me.xyr0.astralsmpcore.tpa.gui.TpaMenus;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public final class GuiListener implements Listener {
   private final AstralTPA plugin;

   public GuiListener(AstralTPA plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent event) {
      HumanEntity var3 = event.getWhoClicked();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            event.setCancelled(true);
            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               SoundUtil.play(player, player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7F, 1.2F);
            }

            if (event.getClickedInventory() != null && event.getClickedInventory() == event.getView().getTopInventory()) {
               MenuSession.Mode mode = (MenuSession.Mode)MenuSession.MODE.getOrDefault(player.getUniqueId(), MenuSession.Mode.ACCEPT);
               int slot = event.getRawSlot();
               if (mode == MenuSession.Mode.SEND) {
                  this.handleSend(player, slot);
               } else {
                  FileConfiguration gui = TpaMenus.getAcceptGui();
                  ConfigurationSection items = gui.getConfigurationSection("items");
                  if (items != null) {
                     ConfigurationSection accept = items.getConfigurationSection("accept");
                     ConfigurationSection deny = items.getConfigurationSection("deny");
                     if (accept != null && accept.getInt("slot", 16) == slot) {
                        UUID senderId = (UUID)MenuSession.SENDER.get(player.getUniqueId());
                        Player sender = senderId != null ? Bukkit.getPlayer(senderId) : null;
                        player.closeInventory();
                        MenuSession.clear(player.getUniqueId());
                        this.plugin.getTpa().accept(player, sender != null ? sender.getName() : null);
                     } else {
                        if (deny != null && deny.getInt("slot", 10) == slot) {
                           UUID senderId = (UUID)MenuSession.SENDER.get(player.getUniqueId());
                           Player sender = senderId != null ? Bukkit.getPlayer(senderId) : null;
                           player.closeInventory();
                           MenuSession.clear(player.getUniqueId());
                           this.plugin.getTpa().deny(player, sender != null ? sender.getName() : null);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private void handleSend(Player player, int slot) {
      FileConfiguration gui = TpaMenus.getSendGui();
      ConfigurationSection items = gui.getConfigurationSection("items");
      if (items != null) {
         ConfigurationSection confirm = items.getConfigurationSection("confirm");
         ConfigurationSection cancel = items.getConfigurationSection("cancel");
         if (cancel != null && cancel.getInt("slot", 10) == slot) {
            player.closeInventory();
            MenuSession.clear(player.getUniqueId());
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-send-cancelled", "&cTeleport request sending cancelled.")));
         } else if (confirm != null && confirm.getInt("slot", 16) == slot) {
            UUID targetId = (UUID)MenuSession.TARGET.get(player.getUniqueId());
            TpaRequest.Type type = (TpaRequest.Type)MenuSession.TYPE.get(player.getUniqueId());
            player.closeInventory();
            MenuSession.clear(player.getUniqueId());
            if (targetId != null && type != null) {
               Player target = Bukkit.getPlayer(targetId);
               if (target != null && target.isOnline()) {
                  TpaManager mgr = this.plugin.getTpa();
                  if (mgr.hasOutgoingTo(player.getUniqueId(), target.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-already-sent", "&cYou have already sent a teleport request to &#37BFF8{target}&c. Please wait.").replace("{target}", target.getName())));
                  } else if (mgr.getIncomingFrom(target.getUniqueId(), player.getUniqueId()) != null) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-already-has-request", "&#37BFF8{target}&c already has an incoming teleport request from you. Please wait.").replace("{target}", target.getName())));
                  } else if (type == TpaRequest.Type.TPA && mgr.isTpaDisabled(target.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-receiver-disabled", "&c{player} has tpa requests disabled.").replace("{player}", target.getName())));
                  } else if (type == TpaRequest.Type.TPAHERE && mgr.isTpahereDisabled(target.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-here-receiver-disabled", "&c{player} has tpahere requests disabled.").replace("{player}", target.getName())));
                  } else if (mgr.isIgnoring(target.getUniqueId(), player.getUniqueId())) {
                     player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("tpa-ignored", "&c{player} is ignoring your teleport requests.").replace("{player}", target.getName())));
                  } else {
                     mgr.sendRequest(player, target, type);
                  }
               } else {
                  player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("player-not-found", "&cPlayer not found!")));
               }
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      HumanEntity var3 = event.getPlayer();
      if (var3 instanceof Player player) {
         if (Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
            Tasks.run(this.plugin.getCore(), () -> {
               if (!player.isOnline()) {
                  MenuSession.clear(player.getUniqueId());
               } else if (player.getOpenInventory().getTopInventory().getSize() <= 5 || !Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
                  MenuSession.clear(player.getUniqueId());
               }
            });
         }
      }
   }
}
