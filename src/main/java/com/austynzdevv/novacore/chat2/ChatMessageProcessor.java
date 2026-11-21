package com.austynzdevv.novacore.chat2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.austynzdevv.novacore.chat2.util.MessageUtil;
import com.austynzdevv.novacore.util.ColorUtil;
import com.austynzdevv.novacore.util.HotbarUtil;
import com.austynzdevv.novacore.util.SoundUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatMessageProcessor {
   private final EconomyChat plugin;
   private static final Pattern HAND_PATTERN = Pattern.compile("(?i)\\[hand]");
   private static final Pattern INV_PATTERN = Pattern.compile("(?i)\\[(inv|inventory)]");
   private static final Pattern TAG_PATTERN = Pattern.compile("(?i)\\[(hand|inv|inventory)]");

   public ChatMessageProcessor(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public Component processMessage(Player sender, String rawMessage) {
      boolean mentionsEnabled = this.plugin.getConfigManager().isModuleEnabled("mentions");
      boolean handEnabled = this.plugin.getConfigManager().isModuleEnabled("item-hand");
      boolean invEnabled = this.plugin.getConfigManager().isModuleEnabled("item-inventory");
      Map<String, Player> nameMap = new LinkedHashMap();
      if (mentionsEnabled) {
         for(Player online : Bukkit.getOnlinePlayers()) {
            nameMap.put(online.getName(), online);
         }
      }

      List<Token> tokens = this.tokenize(rawMessage, handEnabled, invEnabled);
      TextComponent.Builder builder = Component.text();
      Set<UUID> tagged = new HashSet();

      for(Token token : tokens) {
         if (token.type == ChatMessageProcessor.TokenType.HAND) {
            builder.append(this.buildHandComponent(sender));
         } else if (token.type == ChatMessageProcessor.TokenType.INV) {
            builder.append(this.buildInventoryComponent(sender));
         } else {
            builder.append(this.processMentions(token.text, nameMap, tagged, sender));
         }
      }

      if (!tagged.isEmpty()) {
         String actionBarMsg = this.plugin.getConfigManager().getMessage("chat.tagged").replace("%player%", sender.getName());
         Component actionBar = MessageUtil.colorComponent(actionBarMsg);

         for(UUID id : tagged) {
            Player target = Bukkit.getPlayer(id);
            if (target != null && target.isOnline()) {
               HotbarUtil.send(target, actionBar);

               try {
                  SoundUtil.play(target, target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.2F);
               } catch (Exception var16) {
               }
            }
         }
      }

      return builder.build();
   }

   private List<Token> tokenize(String message, boolean handEnabled, boolean invEnabled) {
      List<Token> tokens = new ArrayList();
      if (message != null && !message.isEmpty()) {
         Matcher matcher = TAG_PATTERN.matcher(message);

         int last;
         for(last = 0; matcher.find(); last = matcher.end()) {
            if (matcher.start() > last) {
               tokens.add(new Token(ChatMessageProcessor.TokenType.TEXT, message.substring(last, matcher.start())));
            }

            String tag = matcher.group(1).toLowerCase(Locale.ROOT);
            if (tag.equals("hand") && handEnabled) {
               tokens.add(new Token(ChatMessageProcessor.TokenType.HAND, matcher.group()));
            } else if ((tag.equals("inv") || tag.equals("inventory")) && invEnabled) {
               tokens.add(new Token(ChatMessageProcessor.TokenType.INV, matcher.group()));
            } else {
               tokens.add(new Token(ChatMessageProcessor.TokenType.TEXT, matcher.group()));
            }
         }

         if (last < message.length()) {
            tokens.add(new Token(ChatMessageProcessor.TokenType.TEXT, message.substring(last)));
         }

         if (tokens.isEmpty()) {
            tokens.add(new Token(ChatMessageProcessor.TokenType.TEXT, message));
         }

         return tokens;
      } else {
         return tokens;
      }
   }

   private Component processMentions(String text, Map<String, Player> nameMap, Set<UUID> tagged, Player sender) {
      if (!nameMap.isEmpty() && !text.isEmpty()) {
         List<String> names = new ArrayList(nameMap.keySet());
         names.sort((a, b) -> Integer.compare(b.length(), a.length()));
         TextComponent.Builder builder = Component.text();
         int i = 0;
         int len = text.length();

         while(i < len) {
            boolean matched = false;

            for(String name : names) {
               int nLen = name.length();
               if (i + nLen <= len && text.regionMatches(i, name, 0, nLen) && this.isWordBoundary(text, i, i + nLen)) {
                  Player mentioned = (Player)nameMap.get(name);
                  if (mentioned != null && !mentioned.getUniqueId().equals(sender.getUniqueId())) {
                     tagged.add(mentioned.getUniqueId());
                  }

                  builder.append(((TextComponent)Component.text("@" + name).color(NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false));
                  i += nLen;
                  matched = true;
                  break;
               }
            }

            if (!matched) {
               int start;
               for(start = i++; i < len; ++i) {
                  boolean wouldMatch = false;

                  for(String name : names) {
                     int nLen = name.length();
                     if (i + nLen <= len && text.regionMatches(i, name, 0, nLen) && this.isWordBoundary(text, i, i + nLen)) {
                        wouldMatch = true;
                        break;
                     }
                  }

                  if (wouldMatch) {
                     break;
                  }
               }

               builder.append(ColorUtil.colorComponent(text.substring(start, i)));
            }
         }

         return builder.build();
      } else {
         return ColorUtil.colorComponent(text);
      }
   }

   private boolean isWordBoundary(String text, int start, int end) {
      boolean startOk = start == 0 || !Character.isLetterOrDigit(text.charAt(start - 1));
      boolean endOk = end >= text.length() || !Character.isLetterOrDigit(text.charAt(end));
      return startOk && endOk;
   }

   private Component buildInventoryComponent(Player player) {
      List<Component> lines = new ArrayList();
      lines.add(Component.text(player.getName() + "'s Inventory", NamedTextColor.AQUA, new TextDecoration[]{TextDecoration.BOLD}));
      lines.add(Component.empty());
      int shown = 0;

      for(ItemStack item : player.getInventory().getContents()) {
         if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
            String name = this.getItemDisplayName(item);
            lines.add(((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("x" + item.getAmount() + " ", NamedTextColor.GRAY))).append(Component.text(name, NamedTextColor.WHITE))).build());
            ++shown;
            if (shown >= 40) {
               lines.add(Component.text("...", NamedTextColor.DARK_GRAY));
               break;
            }
         }
      }

      if (shown == 0) {
         lines.add(Component.text("Empty", NamedTextColor.DARK_GRAY));
      }

      TextComponent.Builder hoverBuilder = Component.text();

      for(int i = 0; i < lines.size(); ++i) {
         hoverBuilder.append((Component)lines.get(i));
         if (i < lines.size() - 1) {
            hoverBuilder.append(Component.newline());
         }
      }

      return ((TextComponent)((TextComponent)((TextComponent)Component.text("[Inventory]").color(NamedTextColor.GREEN)).decoration(TextDecoration.BOLD, true)).decoration(TextDecoration.ITALIC, false)).hoverEvent(HoverEvent.showText(hoverBuilder.build()));
   }

   private Component buildHandComponent(Player player) {
      ItemStack item = player.getInventory().getItemInMainHand().clone();
      if (item != null && item.getType() != Material.AIR && item.getAmount() > 0) {
         String displayName = this.getItemDisplayName(item);
         return ((TextComponent)((TextComponent)Component.text("[" + displayName + "]").color(NamedTextColor.AQUA)).decoration(TextDecoration.ITALIC, false)).hoverEvent(item.asHoverEvent());
      } else {
         return ((TextComponent)Component.text("[Empty Hand]").color(NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false);
      }
   }

   private String getItemDisplayName(ItemStack item) {
      ItemMeta meta = item.getItemMeta();
      if (meta != null && meta.hasDisplayName()) {
         return ChatColor.stripColor(meta.getDisplayName());
      } else {
         String mat = item.getType().name().toLowerCase(Locale.ROOT).replace('_', ' ');
         StringBuilder pretty = new StringBuilder();

         for(String part : mat.split(" ")) {
            if (!part.isEmpty()) {
               pretty.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
            }
         }

         return pretty.toString().trim();
      }
   }

   private static enum TokenType {
      TEXT,
      HAND,
      INV;

      // $FF: synthetic method
      private static TokenType[] $values() {
         return new TokenType[]{TEXT, HAND, INV};
      }
   }

   private static class Token {
      final TokenType type;
      final String text;

      Token(TokenType type, String text) {
         this.type = type;
         this.text = text;
      }
   }
}
