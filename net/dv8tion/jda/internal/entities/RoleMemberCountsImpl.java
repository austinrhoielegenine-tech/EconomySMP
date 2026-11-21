package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongIntMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.RoleMemberCount;
import net.dv8tion.jda.api.entities.RoleMemberCounts;
import org.jetbrains.annotations.Unmodifiable;

public class RoleMemberCountsImpl implements RoleMemberCounts {
   private final Guild guild;
   private final TLongIntMap roleMemberCounts;

   public RoleMemberCountsImpl(Guild guild, TLongIntMap roleMemberCounts) {
      this.guild = guild;
      this.roleMemberCounts = roleMemberCounts;
   }

   public int get(long roleId) {
      return this.roleMemberCounts.get(roleId);
   }

   public boolean contains(long roleId) {
      return this.roleMemberCounts.containsKey(roleId);
   }

   @Nonnull
   public @Unmodifiable List<RoleMemberCount> asList() {
      List<RoleMemberCount> map = new ArrayList(this.roleMemberCounts.size());
      this.roleMemberCounts.forEachEntry((roleId, count) -> {
         map.add(new RoleMemberCountImpl(this.guild, roleId, count));
         return true;
      });
      return Collections.unmodifiableList(map);
   }
}
