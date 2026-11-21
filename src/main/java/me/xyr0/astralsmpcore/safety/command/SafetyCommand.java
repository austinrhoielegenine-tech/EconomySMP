package me.xyr0.astralsmpcore.safety.command;

import me.xyr0.astralsmpcore.safety.AstralSafety;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SafetyCommand implements CommandExecutor {
   private final AstralSafety plugin;

   public SafetyCommand(AstralSafety plugin) {
      this.plugin = plugin;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
         if (!sender.hasPermission("astralsmpcore.system.safety.use")) {
            sender.sendMessage(this.color(this.plugin.getConfig().getString("messages.no-permission")));
            return true;
         } else {
            this.plugin.reloadConfig();
            sender.sendMessage(this.color(this.plugin.getConfig().getString("messages.reload")));
            return true;
         }
      } else if (sender instanceof Player) {
         Player player = (Player)sender;
         this.openBook(player);
         return true;
      } else {
         sender.sendMessage("Only players can use this command.");
         return true;
      }
   }

   private void openBook(Player player) {
      ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
      BookMeta meta = (BookMeta)book.getItemMeta();
      meta.setTitle(this.plugin.getConfig().getString("book.title"));
      meta.setAuthor(this.plugin.getConfig().getString("book.author"));

      for(String page : this.plugin.getConfig().getStringList("pages")) {
         meta.addPage(new String[]{this.color(page)});
      }

      book.setItemMeta(meta);
      player.openBook(book);
   }

   private String color(String text) {
      return text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
   }
}
