package com.austynzdevv.economysmp.billford.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.austynzdevv.economysmp.billford.EconomyBillford;
import com.austynzdevv.economysmp.billford.gui.BillfordMenus;
import com.austynzdevv.economysmp.util.ColorUtil;
import com.austynzdevv.economysmp.util.SoundUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class BillfordCommand implements CommandExecutor, TabCompleter {
   private final EconomyBillford plugin;

   public BillfordCommand(EconomyBillford plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("economysmpcore.system.billford.admin")) {
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission", "&cYou don't have permission to do this!")));
            return true;
         } else {
            this.plugin.reloadAll();
            sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("reload", "&aBillford Plugin Reloaded!")));
            if (sender instanceof Player) {
               Player player = (Player)sender;

               try {
                  String name = this.plugin.getConfig().getString("sounds.reload.sound", "BLOCK_NOTE_BLOCK_PLING");
                  SoundUtil.play(player, player.getLocation(), Sound.valueOf(name.toUpperCase(Locale.ROOT).replace('.', '_')), 1.0F, 1.0F);
               } catch (Exception var7) {
               }
            }

            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         if (!player.hasPermission("economysmpcore.system.billford.use")) {
            player.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("no-permission", "&cYou don't have permission to do this!")));
            return true;
         } else {
            BillfordMenus.open(player);
            return true;
         }
      } else {
         sender.sendMessage(ColorUtil.parse(this.plugin.getMessages().get("not-a-player", "Only players can do this!")));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> out = new ArrayList();
      if (args.length == 1 && sender.hasPermission("economysmpcore.system.billford.admin") && "reload".startsWith(args[0].toLowerCase(Locale.ROOT))) {
         out.add("reload");
      }

      return out;
   }
}
