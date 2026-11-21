package me.xyr0.economysmpcore;

public interface Module {
   String getName();

   void onEnable();

   void onDisable();

   void reload();
}
