package com.austynzdevv.novacore.tpa.gui;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import com.austynzdevv.novacore.tpa.TpaRequest;
import com.austynzdevv.novacore.util.SessionUtil;

public final class MenuSession {
   public static final Map<UUID, Boolean> OPEN = new ConcurrentHashMap();
   public static final Map<UUID, Mode> MODE = new ConcurrentHashMap();
   public static final Map<UUID, UUID> SENDER = new ConcurrentHashMap();
   public static final Map<UUID, UUID> TARGET = new ConcurrentHashMap();
   public static final Map<UUID, TpaRequest.Type> TYPE = new ConcurrentHashMap();

   private MenuSession() {
   }

   public static void clear(UUID id) {
      SessionUtil.clear(id, OPEN, MODE, SENDER, TARGET, TYPE);
   }

   public static enum Mode {
      ACCEPT,
      SEND;

      // $FF: synthetic method
      private static Mode[] $values() {
         return new Mode[]{ACCEPT, SEND};
      }
   }
}
