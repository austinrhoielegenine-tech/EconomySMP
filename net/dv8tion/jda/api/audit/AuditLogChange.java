package net.dv8tion.jda.api.audit;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;

public class AuditLogChange {
   protected final Object oldValue;
   protected final Object newValue;
   protected final String key;

   public AuditLogChange(Object oldValue, Object newValue, String key) {
      this.oldValue = oldValue;
      this.newValue = newValue;
      this.key = key;
   }

   @Nullable
   public <T> T getOldValue() {
      return (T)this.oldValue;
   }

   @Nullable
   public <T> T getNewValue() {
      return (T)this.newValue;
   }

   @Nonnull
   public String getKey() {
      return this.key;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key, this.oldValue, this.newValue});
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof AuditLogChange)) {
         return false;
      } else {
         AuditLogChange other = (AuditLogChange)obj;
         return other.key.equals(this.key) && Objects.equals(other.oldValue, this.oldValue) && Objects.equals(other.newValue, this.newValue);
      }
   }

   public String toString() {
      return (new EntityString(this)).setName(this.key).addMetadata((String)null, this.oldValue + " -> " + this.newValue).toString();
   }
}
