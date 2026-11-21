package com.austynzdevv.novacore;

public interface Module {
   String getName();

   void onEnable();

   void onDisable();

   void reload();
}
