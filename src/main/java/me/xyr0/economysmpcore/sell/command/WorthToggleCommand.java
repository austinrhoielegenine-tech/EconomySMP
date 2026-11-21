package me.xyr0.economysmpcore.sell.command;

import me.xyr0.economysmpcore.sell.AstralSell;
import me.xyr0.economysmpcore.util.ColorUtil;
import me.xyr0.economysmpcore.util.HotbarUtil;
import me.xyr0.economysmpcore.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorthToggleCommand implements CommandExecutor {
   private final AstralSell plugin;

   public WorthToggleCommand(AstralSell plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (sender instanceof Player player) {
         if (!player.hasPermission("economysmpcore.system.sell.use") && !player.hasPermission("economysmpcore.system.sell.use")) {
            this.plugin.getMessages().send(player, "no-permission");
            return true;
         } else if (this.plugin.getWorthLore() == null) {
            return true;
         } else {
            boolean enabled = this.plugin.getWorthLore().toggleWorth(player);
            String msg = enabled ? this.plugin.getMessages().raw("worth-toggle-on", "&7ᴡᴏʀᴛʜ ʟᴏʀᴇ ᴇɴᴀʙʟᴇᴅ") : this.plugin.getMessages().raw("worth-toggle-off", "&7ᴡᴏʀᴛʜ ʟᴏʀᴇ ᴅɪsᴀʙʟᴇᴅ");
            Component component = ColorUtil.parse(msg);
            player.sendMessage(component);
            HotbarUtil.send(player, component);
            SoundUtil.playConfigured(player, AstralSell.get().getCore(), "sounds.gui-click");
            return true;
         }
      } else {
         sender.sendMessage("Players only.");
         return true;
      }
   }
}
