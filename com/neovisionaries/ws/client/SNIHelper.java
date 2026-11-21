package com.neovisionaries.ws.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;

class SNIHelper {
   private static Constructor<?> sSNIHostNameConstructor;
   private static Method sSetServerNamesMethod;

   private static void initialize() throws Exception {
      sSNIHostNameConstructor = Misc.getConstructor("javax.net.ssl.SNIHostName", new Class[]{String.class});
      sSetServerNamesMethod = Misc.getMethod("javax.net.ssl.SSLParameters", "setServerNames", new Class[]{List.class});
   }

   private static Object createSNIHostName(String hostname) {
      return Misc.newInstance(sSNIHostNameConstructor, hostname);
   }

   private static List<Object> createSNIHostNames(String[] hostnames) {
      List<Object> list = new ArrayList(hostnames.length);

      for(String hostname : hostnames) {
         list.add(createSNIHostName(hostname));
      }

      return list;
   }

   private static void setServerNames(SSLParameters parameters, String[] hostnames) {
      Misc.invoke(sSetServerNamesMethod, parameters, createSNIHostNames(hostnames));
   }

   static void setServerNames(Socket socket, String[] hostnames) {
      if (socket instanceof SSLSocket) {
         if (hostnames != null) {
            int androidSDKVersion = getAndroidSDKVersion();
            if (androidSDKVersion > 0 && androidSDKVersion < 24) {
               try {
                  Method method = socket.getClass().getMethod("setHostname", String.class);
                  method.invoke(socket, hostnames[0]);
               } catch (Exception e) {
                  System.err.println("SNI configuration failed: " + e.getMessage());
               }

            } else {
               SSLParameters parameters = ((SSLSocket)socket).getSSLParameters();
               if (parameters != null) {
                  setServerNames(parameters, hostnames);
               }
            }
         }
      }
   }

   public static int getAndroidSDKVersion() {
      try {
         return Class.forName("android.os.Build$VERSION").getField("SDK_INT").getInt((Object)null);
      } catch (Exception var3) {
         try {
            return Integer.parseInt((String)Class.forName("android.os.Build$VERSION").getField("SDK").get((Object)null));
         } catch (Exception var2) {
            return 0;
         }
      }
   }

   static {
      try {
         initialize();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}
