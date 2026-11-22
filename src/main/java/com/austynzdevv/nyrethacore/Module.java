package com.austynzdevv.nyrethacore;

public interface Module {
   String getName();

   void onEnable();

   void onDisable();

   void reload();
}
