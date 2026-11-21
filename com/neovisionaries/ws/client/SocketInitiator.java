package com.neovisionaries.ws.client;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.SocketFactory;

public class SocketInitiator {
   private final SocketFactory mSocketFactory;
   private final Address mAddress;
   private final int mConnectTimeout;
   private final String[] mServerNames;
   private final DualStackMode mMode;
   private final int mFallbackDelay;

   public SocketInitiator(SocketFactory socketFactory, Address address, int connectTimeout, String[] serverNames, DualStackMode mode, int fallbackDelay) {
      this.mSocketFactory = socketFactory;
      this.mAddress = address;
      this.mConnectTimeout = connectTimeout;
      this.mServerNames = serverNames;
      this.mMode = mode;
      this.mFallbackDelay = fallbackDelay;
   }

   public Socket establish(InetAddress[] addresses) throws Exception {
      SocketFuture future = new SocketFuture();
      List<SocketRacer> racers = new ArrayList(addresses.length);
      int delay = 0;
      Signal startSignal = null;

      for(InetAddress address : addresses) {
         if ((this.mMode != DualStackMode.IPV4_ONLY || address instanceof Inet4Address) && (this.mMode != DualStackMode.IPV6_ONLY || address instanceof Inet6Address)) {
            delay += this.mFallbackDelay;
            Signal doneSignal = new Signal(delay);
            SocketAddress socketAddress = new InetSocketAddress(address, this.mAddress.getPort());
            SocketRacer racer = new SocketRacer(future, this.mSocketFactory, socketAddress, this.mServerNames, this.mConnectTimeout, startSignal, doneSignal);
            racers.add(racer);
            startSignal = doneSignal;
         }
      }

      return future.await(racers);
   }

   private class Signal {
      private final CountDownLatch mLatch = new CountDownLatch(1);
      private final int mMaxDelay;

      Signal(int maxDelay) {
         this.mMaxDelay = maxDelay;
      }

      boolean isDone() {
         return this.mLatch.getCount() == 0L;
      }

      void await() throws InterruptedException {
         this.mLatch.await((long)this.mMaxDelay, TimeUnit.MILLISECONDS);
      }

      void done() {
         this.mLatch.countDown();
      }
   }

   private class SocketRacer extends Thread {
      private final SocketFuture mFuture;
      private final SocketFactory mSocketFactory;
      private final SocketAddress mSocketAddress;
      private String[] mServerNames;
      private final int mConnectTimeout;
      private final Signal mStartSignal;
      private final Signal mDoneSignal;

      SocketRacer(SocketFuture future, SocketFactory socketFactory, SocketAddress socketAddress, String[] serverNames, int connectTimeout, Signal startSignal, Signal doneSignal) {
         this.mFuture = future;
         this.mSocketFactory = socketFactory;
         this.mSocketAddress = socketAddress;
         this.mServerNames = serverNames;
         this.mConnectTimeout = connectTimeout;
         this.mStartSignal = startSignal;
         this.mDoneSignal = doneSignal;
      }

      public void run() {
         Socket socket = null;

         try {
            if (this.mStartSignal != null) {
               this.mStartSignal.await();
            }

            if (this.mFuture.hasSocket()) {
               return;
            }

            socket = this.mSocketFactory.createSocket();
            SNIHelper.setServerNames(socket, this.mServerNames);
            socket.connect(this.mSocketAddress, this.mConnectTimeout);
            this.complete(socket);
         } catch (Exception e) {
            this.abort(e);
            if (socket != null) {
               try {
                  socket.close();
               } catch (IOException var4) {
               }
            }
         }

      }

      private void complete(Socket socket) {
         synchronized(this.mFuture) {
            if (!this.mDoneSignal.isDone()) {
               this.mFuture.setSocket(this, socket);
               this.mDoneSignal.done();
            }
         }
      }

      void abort(Exception exception) {
         synchronized(this.mFuture) {
            if (!this.mDoneSignal.isDone()) {
               this.mFuture.setException(exception);
               this.mDoneSignal.done();
            }
         }
      }
   }

   private class SocketFuture {
      private CountDownLatch mLatch;
      private List<SocketRacer> mRacers;
      private Socket mSocket;
      private Exception mException;

      private SocketFuture() {
      }

      synchronized boolean hasSocket() {
         return this.mSocket != null;
      }

      synchronized void setSocket(SocketRacer current, Socket socket) {
         if (this.mLatch != null && this.mRacers != null) {
            if (this.mSocket == null) {
               this.mSocket = socket;

               for(SocketRacer racer : this.mRacers) {
                  if (racer != current) {
                     racer.abort(new InterruptedException());
                     racer.interrupt();
                  }
               }
            } else {
               try {
                  socket.close();
               } catch (IOException var5) {
               }
            }

            this.mLatch.countDown();
         } else {
            throw new IllegalStateException("Cannot set socket before awaiting!");
         }
      }

      synchronized void setException(Exception exception) {
         if (this.mLatch != null && this.mRacers != null) {
            if (this.mException == null) {
               this.mException = exception;
            }

            this.mLatch.countDown();
         } else {
            throw new IllegalStateException("Cannot set exception before awaiting!");
         }
      }

      Socket await(List<SocketRacer> racers) throws Exception {
         this.mRacers = racers;
         this.mLatch = new CountDownLatch(this.mRacers.size());

         for(SocketRacer racer : this.mRacers) {
            racer.start();
         }

         this.mLatch.await();
         if (this.mSocket != null) {
            return this.mSocket;
         } else if (this.mException != null) {
            throw this.mException;
         } else {
            throw new WebSocketException(WebSocketError.SOCKET_CONNECT_ERROR, "No viable interface to connect");
         }
      }
   }
}
