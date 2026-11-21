package net.dv8tion.jda.api.audio.factory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.audio.AudioConnection;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.MDC;

public class DefaultSendSystem implements IAudioSendSystem {
   private final IPacketProvider packetProvider;
   private Thread sendThread;
   private ConcurrentMap<String, String> contextMap;

   public DefaultSendSystem(@Nonnull IPacketProvider packetProvider) {
      this.packetProvider = packetProvider;
   }

   public void setContextMap(@CheckForNull ConcurrentMap<String, String> contextMap) {
      this.contextMap = contextMap;
   }

   public void start() {
      DatagramSocket udpSocket = this.packetProvider.getUdpSocket();
      this.sendThread = new Thread(() -> {
         if (this.contextMap != null) {
            MDC.setContextMap(this.contextMap);
         }

         long lastFrameSent = System.currentTimeMillis();
         boolean sentPacket = true;

         while(!udpSocket.isClosed() && !this.sendThread.isInterrupted()) {
            boolean var21 = false;

            label245: {
               label246: {
                  label247: {
                     try {
                        var21 = true;
                        boolean changeTalking = !sentPacket || System.currentTimeMillis() - lastFrameSent > 20L;
                        DatagramPacket packet = this.packetProvider.getNextPacket(changeTalking);
                        sentPacket = packet != null;
                        if (sentPacket) {
                           udpSocket.send(packet);
                           var21 = false;
                        } else {
                           var21 = false;
                        }
                        break label245;
                     } catch (NoRouteToHostException var27) {
                        this.packetProvider.onConnectionLost();
                        var21 = false;
                     } catch (SocketException var28) {
                        var21 = false;
                        break label247;
                     } catch (Exception e) {
                        AudioConnection.LOG.error((String)"Error while sending udp audio data", (Throwable)e);
                        var21 = false;
                        break label246;
                     } finally {
                        if (var21) {
                           long sleepTime = 20L - (System.currentTimeMillis() - lastFrameSent);
                           if (sleepTime > 0L) {
                              try {
                                 Thread.sleep(sleepTime);
                              } catch (InterruptedException var22) {
                                 Thread.currentThread().interrupt();
                              }
                           }

                           if (System.currentTimeMillis() < lastFrameSent + 60L) {
                              lastFrameSent += 20L;
                           } else {
                              lastFrameSent = System.currentTimeMillis();
                           }

                        }
                     }

                     long sleepTime = 20L - (System.currentTimeMillis() - lastFrameSent);
                     if (sleepTime > 0L) {
                        try {
                           Thread.sleep(sleepTime);
                        } catch (InterruptedException var25) {
                           Thread.currentThread().interrupt();
                        }
                     }

                     if (System.currentTimeMillis() < lastFrameSent + 60L) {
                        lastFrameSent += 20L;
                     } else {
                        lastFrameSent = System.currentTimeMillis();
                     }
                     continue;
                  }

                  long sleepTime = 20L - (System.currentTimeMillis() - lastFrameSent);
                  if (sleepTime > 0L) {
                     try {
                        Thread.sleep(sleepTime);
                     } catch (InterruptedException var24) {
                        Thread.currentThread().interrupt();
                     }
                  }

                  if (System.currentTimeMillis() < lastFrameSent + 60L) {
                     lastFrameSent += 20L;
                  } else {
                     lastFrameSent = System.currentTimeMillis();
                  }
                  continue;
               }

               long sleepTime = 20L - (System.currentTimeMillis() - lastFrameSent);
               if (sleepTime > 0L) {
                  try {
                     Thread.sleep(sleepTime);
                  } catch (InterruptedException var23) {
                     Thread.currentThread().interrupt();
                  }
               }

               if (System.currentTimeMillis() < lastFrameSent + 60L) {
                  lastFrameSent += 20L;
               } else {
                  lastFrameSent = System.currentTimeMillis();
               }
               continue;
            }

            long sleepTime = 20L - (System.currentTimeMillis() - lastFrameSent);
            if (sleepTime > 0L) {
               try {
                  Thread.sleep(sleepTime);
               } catch (InterruptedException var26) {
                  Thread.currentThread().interrupt();
               }
            }

            if (System.currentTimeMillis() < lastFrameSent + 60L) {
               lastFrameSent += 20L;
            } else {
               lastFrameSent = System.currentTimeMillis();
            }
         }

      });
      this.sendThread.setUncaughtExceptionHandler((thread, throwable) -> {
         JDALogger.getLog(DefaultSendSystem.class).error("Uncaught exception in audio send thread", throwable);
         this.start();
      });
      this.sendThread.setDaemon(true);
      this.sendThread.setName(this.packetProvider.getIdentifier() + " Sending Thread");
      this.sendThread.setPriority(7);
      this.sendThread.start();
   }

   public void shutdown() {
      if (this.sendThread != null) {
         this.sendThread.interrupt();
      }

   }
}
