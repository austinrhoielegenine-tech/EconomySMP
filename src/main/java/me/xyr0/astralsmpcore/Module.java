package me.xyr0.astralsmpcore;

public interface Module {
   String getName();

   void onEnable();

   void onDisable();

   void reload();
}
