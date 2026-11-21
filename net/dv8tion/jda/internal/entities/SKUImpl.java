package net.dv8tion.jda.internal.entities;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.SKU;
import net.dv8tion.jda.api.entities.SKUFlag;
import net.dv8tion.jda.api.entities.SKUType;
import net.dv8tion.jda.internal.utils.Helpers;

public class SKUImpl extends SkuSnowflakeImpl implements SKU {
   private final SKUType type;
   private final String name;
   private final String slug;
   private final Set<SKUFlag> flags;

   public SKUImpl(long id, SKUType type, String name, String slug, Set<SKUFlag> flags) {
      super(id);
      this.type = type;
      this.name = name;
      this.slug = slug;
      this.flags = flags;
   }

   @Nonnull
   public SKUType getType() {
      return this.type;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public String getSlug() {
      return this.slug;
   }

   @Nonnull
   public Set<SKUFlag> getFlags() {
      return Helpers.<SKUFlag>copyEnumSet(SKUFlag.class, this.flags);
   }
}
