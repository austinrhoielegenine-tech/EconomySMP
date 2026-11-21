package me.xyr0.astralsmpcore.crates;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import me.xyr0.astralsmpcore.crates.gui.MenuSession;
import me.xyr0.astralsmpcore.util.ColorUtil;
import me.xyr0.astralsmpcore.util.SoundUtil;
import me.xyr0.astralsmpcore.util.Tasks;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public final class OpenManager {
   private final AstralCrates plugin;

   public OpenManager(AstralCrates plugin) {
      this.plugin = plugin;
   }

   public void tryOpen(Player player, Crate crate) {
      this.tryOpen(player, crate, false);
   }

   public void tryOpen(Player player, Crate crate, boolean forcePreview) {
      String effect = crate.getEffect();
      if (effect == null || effect.isBlank()) {
         effect = "CHOOSE";
      }

      boolean hasKey = this.plugin.getKeys().totalKeys(player, crate.getId()) > 0;
      if (!forcePreview && hasKey) {
         if ("CHOOSE".equals(effect)) {
            this.openPreview(player, crate, true);
         } else if ("NOWAIT".equals(effect)) {
            if (!this.plugin.getKeys().consumeKey(player, crate.getId())) {
               this.plugin.getMessages().send(player, "no-key", "&cYou don't have a key for this crate!");
            } else {
               Reward reward = this.plugin.getCrates().pickWeighted(crate);
               if (reward != null) {
                  this.giveReward(player, crate, reward);
               }

            }
         } else if ("ROLLING".equals(effect)) {
            if (!this.plugin.getKeys().consumeKey(player, crate.getId())) {
               this.plugin.getMessages().send(player, "no-key", "&cYou don't have a key for this crate!");
            } else {
               this.startRolling(player, crate);
            }
         } else {
            this.openPreview(player, crate, true);
         }
      } else {
         this.openPreview(player, crate, false);
      }
   }

   public void openPreview(Player player, Crate crate, boolean canClaim) {
      int size = Math.max(9, Math.min(54, crate.getRows() * 9));
      Inventory inv = Bukkit.createInventory(player, size, ColorUtil.parse(crate.getDisplayName()));
      if (crate.isFiller() && crate.getFillerItem() != null) {
         ItemStack filler = crate.getFillerItem();

         for(int i = 0; i < size; ++i) {
            inv.setItem(i, filler.clone());
         }
      }

      for(Reward reward : crate.getRewards()) {
         if (reward.getSlot() >= 0 && reward.getSlot() < size) {
            inv.setItem(reward.getSlot(), reward.getIcon());
         }
      }

      MenuSession.OPEN.put(player.getUniqueId(), true);
      MenuSession.MODE.put(player.getUniqueId(), canClaim ? MenuSession.Mode.PREVIEW : MenuSession.Mode.VIEW);
      MenuSession.CRATE.put(player.getUniqueId(), crate.getId());
      player.openInventory(inv);
      this.play(player, "click");
      if (!canClaim) {
         this.plugin.getMessages().send(player, "view-only", "&7Viewing crate contents. You need a key to claim a reward.");
      }

   }

   public void openConfirm(Player player, Crate crate, Reward reward) {
      Inventory inv = Bukkit.createInventory(player, 27, ColorUtil.parse("&8ᴄᴏɴғɪʀᴍ"));
      ItemStack confirm = this.pane(Material.LIME_STAINED_GLASS_PANE, "&#04fc04ᴄᴏɴғɪʀᴍ");
      ItemStack deny = this.pane(Material.RED_STAINED_GLASS_PANE, "&#fc0404ᴅᴇɴʏ");
      inv.setItem(15, confirm);
      inv.setItem(11, deny);
      inv.setItem(13, reward.getIcon());
      MenuSession.OPEN.put(player.getUniqueId(), true);
      MenuSession.MODE.put(player.getUniqueId(), MenuSession.Mode.CONFIRM);
      MenuSession.CRATE.put(player.getUniqueId(), crate.getId());
      MenuSession.PENDING.put(player.getUniqueId(), reward);
      player.openInventory(inv);
      this.play(player, "click");
   }

   private void startRolling(final Player player, final Crate crate) {
      int size = 27;
      final Inventory inv = Bukkit.createInventory(player, size, ColorUtil.parse("&8ʀᴏʟʟɪɴɢ"));
      MenuSession.OPEN.put(player.getUniqueId(), true);
      MenuSession.MODE.put(player.getUniqueId(), MenuSession.Mode.ROLLING);
      MenuSession.CRATE.put(player.getUniqueId(), crate.getId());
      player.openInventory(inv);
      final Reward finalReward = this.plugin.getCrates().pickWeighted(crate);
      final int[] slots = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
      final Material[] mats = new Material[]{Material.YELLOW_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE};
      final int[] __tid = new int[]{-1};
      BukkitRunnable __t = new BukkitRunnable() {
         int ticks = 0;
         final int duration = 40;

         public void run() {
            if (player.isOnline() && Boolean.TRUE.equals(MenuSession.OPEN.get(player.getUniqueId()))) {
               if (this.ticks < 40) {
                  for(int s : slots) {
                     List<Reward> rewards = crate.getRewards();
                     if (rewards.isEmpty()) {
                        inv.setItem(s, new ItemStack(mats[ThreadLocalRandom.current().nextInt(mats.length)]));
                     } else {
                        Reward r = (Reward)rewards.get(ThreadLocalRandom.current().nextInt(rewards.size()));
                        inv.setItem(s, r.getIcon());
                     }
                  }

                  OpenManager.this.play(player, "click");
                  this.ticks += 2;
               } else {
                  for(int s : slots) {
                     inv.setItem(s, (ItemStack)null);
                  }

                  if (finalReward != null) {
                     inv.setItem(13, finalReward.getIcon());
                     OpenManager.this.giveReward(player, crate, finalReward);
                  }

                  OpenManager.this.play(player, "confirm");
                  Tasks.runLater(OpenManager.this.plugin.getCore(), () -> {
                     if (player.isOnline()) {
                        player.closeInventory();
                     }

                     MenuSession.clear(player.getUniqueId());
                  }, 30L);
                  Tasks.cancel(__tid[0]);
               }
            } else {
               Tasks.cancel(__tid[0]);
            }
         }
      };
      __tid[0] = Tasks.runTimer(this.plugin.getCore(), __t, 0L, 2L);
   }

   public void giveReward(Player player, Crate crate, Reward reward) {
      if (reward.hasCommands()) {
         for(String cmd : reward.getCommands()) {
            String parsed = cmd.replace("%player%", player.getName()).replace("%crate%", crate.getId());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
         }
      } else {
         ItemStack item = reward.getIcon();
         if (item != null) {
            player.getInventory().addItem(new ItemStack[]{item}).values().forEach((left) -> player.getWorld().dropItemNaturally(player.getLocation(), left));
         }
      }

      String rewardName = "";
      if (reward.getIcon() != null && reward.getIcon().hasItemMeta() && reward.getIcon().getItemMeta().hasDisplayName()) {
         rewardName = ColorUtil.strip(LegacyComponentSerializer.legacySection().serialize(reward.getIcon().getItemMeta().displayName()));
      } else {
         rewardName = reward.getId();
      }

      this.plugin.getMessages().send(player, "reward-received", "&aYou received %reward%!", "%reward%", rewardName, "%crate%", crate.getId());
      if (this.plugin.getConfig().getBoolean("broadcast.module.chat.enabled", true)) {
         for(String line : this.plugin.getConfig().getStringList("broadcast.module.chat.message")) {
            String msg = line.replace("%player%", player.getName()).replace("%crate%", crate.getId()).replace("%reward%", rewardName);
            Bukkit.broadcast(ColorUtil.parse(msg));
         }
      }

      this.play(player, "confirm");
   }

   private ItemStack pane(Material mat, String name) {
      ItemStack item = new ItemStack(mat);
      ItemMeta meta = item.getItemMeta();
      if (meta != null) {
         meta.displayName(ColorUtil.parse(name));
         item.setItemMeta(meta);
      }

      return item;
   }

   private void play(Player player, String key) {
      try {
         String var10000;
         switch (key) {
            case "confirm" -> var10000 = "sounds.confirm-sound";
            case "denied" -> var10000 = "sounds.denied-sound";
            default -> var10000 = "sounds.click-sound";
         }

         String path = var10000;
         String sound = this.plugin.getConfig().getString(path, "UI_BUTTON_CLICK");
         SoundUtil.play(player, player.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var6) {
      }

   }
}
