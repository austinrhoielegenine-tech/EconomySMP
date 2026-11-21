package me.xyr0.economysmpcore.scoreboard;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisplayScoreboard;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.ObjectiveMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerScoreboardObjective.RenderType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.CollisionRule;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.OptionData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.TeamMode;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateScore.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class FoliaSidebar {
   private static final ConcurrentHashMap<UUID, FoliaSidebar> BOARDS = new ConcurrentHashMap();
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
   private static final AtomicBoolean LOGGED_FAIL = new AtomicBoolean(false);
   private static Plugin plugin;
   private final UUID uuid;
   private final String objectiveId;
   private String title = "";
   private final List<String> lines = new ArrayList();
   private boolean created;

   private FoliaSidebar(Player player) {
      this.uuid = player.getUniqueId();
      int var10001 = this.uuid.hashCode();
      this.objectiveId = "asmp" + Integer.toHexString(var10001 & 1048575);
   }

   public static void init(Plugin p) {
      plugin = p;
   }

   public static FoliaSidebar of(Player player) {
      return (FoliaSidebar)BOARDS.computeIfAbsent(player.getUniqueId(), (id) -> new FoliaSidebar(player));
   }

   public static void remove(Player player) {
      FoliaSidebar board = (FoliaSidebar)BOARDS.remove(player.getUniqueId());
      if (board != null) {
         board.destroy();
      }

   }

   public void set(String title, List<String> newLines) {
      Player player = Bukkit.getPlayer(this.uuid);
      if (player != null && player.isOnline()) {
         String t = title == null ? "" : title;
         List<String> next = new ArrayList();
         if (newLines != null) {
            int max = Math.min(newLines.size(), 15);

            for(int i = 0; i < max; ++i) {
               String line = (String)newLines.get(i);
               next.add(line == null ? "" : line);
            }
         }

         if (!this.created) {
            if (this.create(player, t, next)) {
               ;
            }
         } else {
            if (!this.title.equals(t)) {
               this.title = t;
               this.sendObjective(player, ObjectiveMode.UPDATE);
            }

            int oldSize = this.lines.size();
            int newSize = next.size();
            if (oldSize > newSize) {
               for(int i = newSize; i < oldSize; ++i) {
                  this.removeLine(player, i);
               }
            }

            this.lines.clear();
            this.lines.addAll(next);

            for(int i = 0; i < this.lines.size(); ++i) {
               this.setLine(player, i, (String)this.lines.get(i), this.lines.size() - i);
            }

         }
      }
   }

   private boolean create(Player player, String title, List<String> lines) {
      this.title = title;
      this.lines.clear();
      this.lines.addAll(lines);
      if (!this.sendObjective(player, ObjectiveMode.CREATE)) {
         return false;
      } else if (!this.send(player, new WrapperPlayServerDisplayScoreboard(1, this.objectiveId))) {
         return false;
      } else {
         for(int i = 0; i < this.lines.size(); ++i) {
            this.setLine(player, i, (String)this.lines.get(i), this.lines.size() - i);
         }

         this.created = true;
         return true;
      }
   }

   public void destroy() {
      Player player = Bukkit.getPlayer(this.uuid);
      if (this.created) {
         if (player != null && player.isOnline()) {
            for(int i = 0; i < 15; ++i) {
               try {
                  this.removeLine(player, i);
               } catch (Exception var5) {
               }
            }

            try {
               this.sendObjective(player, ObjectiveMode.REMOVE);
            } catch (Exception var4) {
            }
         }

         this.created = false;
         this.lines.clear();
      }
   }

   private boolean sendObjective(Player player, WrapperPlayServerScoreboardObjective.ObjectiveMode mode) {
      Component display = LEGACY.deserialize(color(this.title));
      return this.send(player, new WrapperPlayServerScoreboardObjective(this.objectiveId, mode, display, RenderType.INTEGER));
   }

   private void setLine(Player player, int index, String text, int score) {
      String entry = this.entry(index);
      String teamName = this.team(index);
      String safe = text == null ? "" : text;
      if (safe.length() > 64) {
         safe = safe.substring(0, 64);
      }

      Component prefix = LEGACY.deserialize(color(safe));
      WrapperPlayServerTeams.ScoreBoardTeamInfo info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(Component.empty(), prefix, Component.empty(), NameTagVisibility.NEVER, CollisionRule.NEVER, NamedTextColor.WHITE, OptionData.NONE);
      this.send(player, new WrapperPlayServerTeams(teamName, TeamMode.REMOVE, Optional.empty(), Collections.emptyList()));
      this.send(player, new WrapperPlayServerTeams(teamName, TeamMode.CREATE, info, List.of(entry)));
      this.send(player, new WrapperPlayServerUpdateScore(entry, Action.CREATE_OR_UPDATE_ITEM, this.objectiveId, Optional.of(score)));
   }

   private void removeLine(Player player, int index) {
      String entry = this.entry(index);
      String teamName = this.team(index);
      this.send(player, new WrapperPlayServerUpdateScore(entry, Action.REMOVE_ITEM, this.objectiveId, Optional.empty()));
      this.send(player, new WrapperPlayServerTeams(teamName, TeamMode.REMOVE, Optional.empty(), Collections.emptyList()));
   }

   private String entry(int index) {
      String var10000 = Integer.toHexString(index % 16);
      return "§" + var10000 + "§" + (char)(97 + index % 26);
   }

   private String team(int index) {
      return "as" + index;
   }

   private boolean send(Player player, PacketWrapper<?> packet) {
      try {
         User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
         if (user == null) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
            return true;
         } else {
            user.sendPacket(packet);
            return true;
         }
      } catch (Exception t) {
         if (LOGGED_FAIL.compareAndSet(false, true) && plugin != null) {
            plugin.getLogger().warning("Folia scoreboard packet failed: " + String.valueOf(t));
            t.printStackTrace();
         }

         return false;
      }
   }

   private static String color(String s) {
      if (s == null) {
         return "";
      } else {
         s = s.replaceAll("<#([A-Fa-f0-9]{6})>", "&#$1");
         s = s.replaceAll("</#[A-Fa-f0-9]{6}>", "");
         Pattern HEX = Pattern.compile("&#([A-Fa-f0-9]{6})");
         Matcher m = HEX.matcher(s);
         StringBuffer sb = new StringBuffer();

         while(m.find()) {
            String hex = m.group(1);
            StringBuilder rep = new StringBuilder("§x");

            for(char ch : hex.toCharArray()) {
               rep.append('§').append(ch);
            }

            m.appendReplacement(sb, Matcher.quoteReplacement(rep.toString()));
         }

         m.appendTail(sb);
         return sb.toString().replace('&', '§');
      }
   }
}
