package com.neovisionaries.ws.client;

import javax.net.ssl.SSLSocket;

public class HostnameUnverifiedException extends WebSocketException {
   private static final long serialVersionUID = 1L;
   private final SSLSocket mSSLSocket;
   private final String mHostname;

   public HostnameUnverifiedException(SSLSocket socket, String hostname) {
      super(WebSocketError.HOSTNAME_UNVERIFIED, String.format("The certificate of the peer%s does not match the expected hostname (%s)", stringifyPrincipal(socket), hostname));
      this.mSSLSocket = socket;
      this.mHostname = hostname;
   }

   private static String stringifyPrincipal(SSLSocket socket) {
      try {
         return String.format(" (%s)", socket.getSession().getPeerPrincipal().toString());
      } catch (Exception var2) {
         return "";
      }
   }

   public SSLSocket getSSLSocket() {
      return this.mSSLSocket;
   }

   public String getHostname() {
      return this.mHostname;
   }
}
