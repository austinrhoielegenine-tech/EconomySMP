package com.austynzdevv.economysmp.tpa;

import java.util.UUID;

public final class TpaRequest {
   private final UUID sender;
   private final UUID target;
   private final Type type;
   private final long createdAt;

   public TpaRequest(UUID sender, UUID target, Type type) {
      this.sender = sender;
      this.target = target;
      this.type = type;
      this.createdAt = System.currentTimeMillis();
   }

   public UUID getSender() {
      return this.sender;
   }

   public UUID getTarget() {
      return this.target;
   }

   public Type getType() {
      return this.type;
   }

   public long getCreatedAt() {
      return this.createdAt;
   }

   public static enum Type {
      TPA,
      TPAHERE;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{TPA, TPAHERE};
      }
   }
}
