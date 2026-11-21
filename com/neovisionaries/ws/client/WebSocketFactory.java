package com.neovisionaries.ws.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class WebSocketFactory {
   private final SocketFactorySettings mSocketFactorySettings;
   private final ProxySettings mProxySettings;
   private int mConnectionTimeout;
   private int mSocketTimeout;
   private DualStackMode mDualStackMode;
   private int mDualStackFallbackDelay;
   private boolean mVerifyHostname;
   private String[] mServerNames;

   public WebSocketFactory() {
      this.mDualStackMode = DualStackMode.BOTH;
      this.mDualStackFallbackDelay = 250;
      this.mVerifyHostname = true;
      this.mSocketFactorySettings = new SocketFactorySettings();
      this.mProxySettings = new ProxySettings(this);
   }

   public WebSocketFactory(WebSocketFactory other) {
      this.mDualStackMode = DualStackMode.BOTH;
      this.mDualStackFallbackDelay = 250;
      this.mVerifyHostname = true;
      if (other == null) {
         throw new IllegalArgumentException("The given WebSocketFactory is null");
      } else {
         this.mSocketFactorySettings = new SocketFactorySettings(other.mSocketFactorySettings);
         this.mProxySettings = new ProxySettings(this, other.mProxySettings);
         this.mConnectionTimeout = other.mConnectionTimeout;
         this.mSocketTimeout = other.mSocketTimeout;
         this.mDualStackMode = other.mDualStackMode;
         this.mDualStackFallbackDelay = other.mDualStackFallbackDelay;
         this.mVerifyHostname = other.mVerifyHostname;
         if (other.mServerNames != null) {
            this.mServerNames = new String[other.mServerNames.length];
            System.arraycopy(other.mServerNames, 0, this.mServerNames, 0, this.mServerNames.length);
         }

      }
   }

   public SocketFactory getSocketFactory() {
      return this.mSocketFactorySettings.getSocketFactory();
   }

   public WebSocketFactory setSocketFactory(SocketFactory factory) {
      this.mSocketFactorySettings.setSocketFactory(factory);
      return this;
   }

   public SSLSocketFactory getSSLSocketFactory() {
      return this.mSocketFactorySettings.getSSLSocketFactory();
   }

   public WebSocketFactory setSSLSocketFactory(SSLSocketFactory factory) {
      this.mSocketFactorySettings.setSSLSocketFactory(factory);
      return this;
   }

   public SSLContext getSSLContext() {
      return this.mSocketFactorySettings.getSSLContext();
   }

   public WebSocketFactory setSSLContext(SSLContext context) {
      this.mSocketFactorySettings.setSSLContext(context);
      return this;
   }

   public ProxySettings getProxySettings() {
      return this.mProxySettings;
   }

   public int getConnectionTimeout() {
      return this.mConnectionTimeout;
   }

   public WebSocketFactory setConnectionTimeout(int timeout) {
      if (timeout < 0) {
         throw new IllegalArgumentException("timeout value cannot be negative.");
      } else {
         this.mConnectionTimeout = timeout;
         return this;
      }
   }

   public int getSocketTimeout() {
      return this.mSocketTimeout;
   }

   public WebSocketFactory setSocketTimeout(int timeout) {
      if (timeout < 0) {
         throw new IllegalArgumentException("timeout value cannot be negative.");
      } else {
         this.mSocketTimeout = timeout;
         return this;
      }
   }

   public DualStackMode getDualStackMode() {
      return this.mDualStackMode;
   }

   public WebSocketFactory setDualStackMode(DualStackMode mode) {
      this.mDualStackMode = mode;
      return this;
   }

   public int getDualStackFallbackDelay() {
      return this.mDualStackFallbackDelay;
   }

   public WebSocketFactory setDualStackFallbackDelay(int delay) {
      if (delay < 0) {
         throw new IllegalArgumentException("delay value cannot be negative.");
      } else {
         this.mDualStackFallbackDelay = delay;
         return this;
      }
   }

   public boolean getVerifyHostname() {
      return this.mVerifyHostname;
   }

   public WebSocketFactory setVerifyHostname(boolean verifyHostname) {
      this.mVerifyHostname = verifyHostname;
      return this;
   }

   public String[] getServerNames() {
      return this.mServerNames;
   }

   public WebSocketFactory setServerNames(String[] serverNames) {
      this.mServerNames = serverNames;
      return this;
   }

   public WebSocketFactory setServerName(String serverName) {
      return this.setServerNames(new String[]{serverName});
   }

   public WebSocket createSocket(String uri) throws IOException {
      return this.createSocket(uri, this.getConnectionTimeout());
   }

   public WebSocket createSocket(String uri, int timeout) throws IOException {
      if (uri == null) {
         throw new IllegalArgumentException("The given URI is null.");
      } else if (timeout < 0) {
         throw new IllegalArgumentException("The given timeout value is negative.");
      } else {
         return this.createSocket(URI.create(uri), timeout);
      }
   }

   public WebSocket createSocket(URL url) throws IOException {
      return this.createSocket(url, this.getConnectionTimeout());
   }

   public WebSocket createSocket(URL url, int timeout) throws IOException {
      if (url == null) {
         throw new IllegalArgumentException("The given URL is null.");
      } else if (timeout < 0) {
         throw new IllegalArgumentException("The given timeout value is negative.");
      } else {
         try {
            return this.createSocket(url.toURI(), timeout);
         } catch (URISyntaxException var4) {
            throw new IllegalArgumentException("Failed to convert the given URL into a URI.");
         }
      }
   }

   public WebSocket createSocket(URI uri) throws IOException {
      return this.createSocket(uri, this.getConnectionTimeout());
   }

   public WebSocket createSocket(URI uri, int timeout) throws IOException {
      if (uri == null) {
         throw new IllegalArgumentException("The given URI is null.");
      } else if (timeout < 0) {
         throw new IllegalArgumentException("The given timeout value is negative.");
      } else {
         String scheme = uri.getScheme();
         String userInfo = uri.getUserInfo();
         String host = Misc.extractHost(uri);
         int port = uri.getPort();
         String path = uri.getRawPath();
         String query = uri.getRawQuery();
         return this.createSocket(scheme, userInfo, host, port, path, query, timeout);
      }
   }

   private WebSocket createSocket(String scheme, String userInfo, String host, int port, String path, String query, int timeout) throws IOException {
      boolean secure = isSecureConnectionRequired(scheme);
      if (host != null && host.length() != 0) {
         path = determinePath(path);
         SocketConnector connector = this.createRawSocket(host, port, secure, timeout);
         return this.createWebSocket(secure, userInfo, host, port, path, query, connector);
      } else {
         throw new IllegalArgumentException("The host part is empty.");
      }
   }

   private static boolean isSecureConnectionRequired(String scheme) {
      if (scheme != null && scheme.length() != 0) {
         if (!"wss".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            if (!"ws".equalsIgnoreCase(scheme) && !"http".equalsIgnoreCase(scheme)) {
               throw new IllegalArgumentException("Bad scheme: " + scheme);
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         throw new IllegalArgumentException("The scheme part is empty.");
      }
   }

   private static String determinePath(String path) {
      if (path != null && path.length() != 0) {
         return path.startsWith("/") ? path : "/" + path;
      } else {
         return "/";
      }
   }

   private SocketConnector createRawSocket(String host, int port, boolean secure, int timeout) throws IOException {
      port = determinePort(port, secure);
      boolean proxied = this.mProxySettings.getHost() != null;
      return proxied ? this.createProxiedRawSocket(host, port, secure, timeout) : this.createDirectRawSocket(host, port, secure, timeout);
   }

   private SocketConnector createProxiedRawSocket(String host, int port, boolean secure, int timeout) {
      int proxyPort = determinePort(this.mProxySettings.getPort(), this.mProxySettings.isSecure());
      SocketFactory factory = this.mProxySettings.selectSocketFactory();
      Address address = new Address(this.mProxySettings.getHost(), proxyPort);
      ProxyHandshaker handshaker = new ProxyHandshaker(host, port, this.mProxySettings);
      SSLSocketFactory sslSocketFactory = secure ? (SSLSocketFactory)this.mSocketFactorySettings.selectSocketFactory(secure) : null;
      return (new SocketConnector(factory, address, timeout, this.mSocketTimeout, this.mProxySettings.getServerNames(), handshaker, sslSocketFactory, host, port)).setDualStackSettings(this.mDualStackMode, this.mDualStackFallbackDelay).setVerifyHostname(this.mVerifyHostname);
   }

   private SocketConnector createDirectRawSocket(String host, int port, boolean secure, int timeout) {
      SocketFactory factory = this.mSocketFactorySettings.selectSocketFactory(secure);
      Address address = new Address(host, port);
      return (new SocketConnector(factory, address, timeout, this.mServerNames, this.mSocketTimeout)).setDualStackSettings(this.mDualStackMode, this.mDualStackFallbackDelay).setVerifyHostname(this.mVerifyHostname);
   }

   private static int determinePort(int port, boolean secure) {
      if (0 <= port) {
         return port;
      } else {
         return secure ? 443 : 80;
      }
   }

   private WebSocket createWebSocket(boolean secure, String userInfo, String host, int port, String path, String query, SocketConnector connector) {
      if (0 <= port) {
         host = host + ":" + port;
      }

      if (query != null) {
         path = path + "?" + query;
      }

      return new WebSocket(this, secure, userInfo, host, path, connector);
   }
}
