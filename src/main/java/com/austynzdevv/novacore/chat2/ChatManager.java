package com.austynzdevv.novacore.chat2;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatManager {
   private final EconomyChat plugin;
   private boolean chatDisabled = false;
   private final Set<UUID> personalOff = new HashSet();

   public ChatManager(EconomyChat plugin) {
      this.plugin = plugin;
   }

   public void disableChat() {
      this.chatDisabled = true;
   }

   public void enableChat() {
      this.chatDisabled = false;
   }

   public boolean isChatDisabled() {
      return this.chatDisabled;
   }

   public boolean togglePersonal(UUID id) {
      if (this.personalOff.contains(id)) {
         this.personalOff.remove(id);
         return true;
      } else {
         this.personalOff.add(id);
         return false;
      }
   }

   public boolean isPersonalOff(UUID id) {
      return this.personalOff.contains(id);
   }

   public void setPersonalOff(UUID id, boolean off) {
      if (off) {
         this.personalOff.add(id);
      } else {
         this.personalOff.remove(id);
      }

   }

   public void loadState() {
      this.chatDisabled = this.plugin.getConfig().getBoolean("chat-toggle.disabled", false);
   }

   public void saveState() {
      this.plugin.getConfig().set("chat-toggle.disabled", this.chatDisabled);
      this.plugin.saveConfig();
   }
}
