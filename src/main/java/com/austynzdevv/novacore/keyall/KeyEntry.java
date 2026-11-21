package com.austynzdevv.novacore.keyall;

public final class KeyEntry {
   private final String id;
   private final int chance;
   private final String display;
   private final String command;

   public KeyEntry(String id, int chance, String display, String command) {
      this.id = id;
      this.chance = Math.max(0, chance);
      this.display = display == null ? id : display;
      this.command = command == null ? "" : command;
   }

   public String getId() {
      return this.id;
   }

   public int getChance() {
      return this.chance;
   }

   public String getDisplay() {
      return this.display;
   }

   public String getCommand() {
      return this.command;
   }
}
