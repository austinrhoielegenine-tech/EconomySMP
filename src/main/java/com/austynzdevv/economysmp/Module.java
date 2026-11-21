package com.austynzdevv.economysmp;

public interface Module {
   String getName();

   void onEnable();

   void onDisable();

   void reload();
}
