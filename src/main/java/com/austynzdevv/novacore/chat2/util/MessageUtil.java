package com.austynzdevv.novacore.chat2.util;

import com.austynzdevv.novacore.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class MessageUtil {
   private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

   private MessageUtil() {
   }

   public static String color(String message) {
      return ColorUtil.color(message);
   }

   public static Component component(String message) {
      return (Component)(message == null ? Component.empty() : MINI_MESSAGE.deserialize(message));
   }

   public static Component colorComponent(String message) {
      return ColorUtil.legacyComponent(message);
   }
}
