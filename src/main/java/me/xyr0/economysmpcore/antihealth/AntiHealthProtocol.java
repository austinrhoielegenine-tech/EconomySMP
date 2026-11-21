package me.xyr0.economysmpcore.antihealth;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import java.util.ArrayList;
import java.util.List;
import me.xyr0.economysmpcore.EconomySMPcore;

public class AntiHealthProtocol {
   private final EconomySMPcore plugin;
   private ProtocolManager protocol;
   private PacketListener listener;

   public AntiHealthProtocol(EconomySMPcore plugin) {
      this.plugin = plugin;
   }

   public void start() {
      this.protocol = ProtocolLibrary.getProtocolManager();
      this.listener = new PacketAdapter(this.plugin, new PacketType[]{Server.ENTITY_METADATA}) {
         public void onPacketSending(PacketEvent event) {
            if (!event.isPlayerTemporary()) {
               try {
                  List<WrappedDataValue> values = (List)event.getPacket().getDataValueCollectionModifier().read(0);
                  if (values == null || values.isEmpty()) {
                     return;
                  }

                  List<WrappedDataValue> filtered = new ArrayList(values.size());

                  for(WrappedDataValue value : values) {
                     if (value.getIndex() != 9) {
                        filtered.add(value);
                     }
                  }

                  if (filtered.size() != values.size()) {
                     event.getPacket().getDataValueCollectionModifier().write(0, filtered);
                  }
               } catch (Exception var6) {
               }

            }
         }
      };
      this.protocol.addPacketListener(this.listener);
   }

   public void stop() {
      if (this.protocol != null && this.listener != null) {
         try {
            this.protocol.removePacketListener(this.listener);
         } catch (Exception var2) {
         }
      }

      this.listener = null;
      this.protocol = null;
   }
}
