package net.dv8tion.jda.internal.hooks;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.hooks.InterfacedEventManager;
import net.dv8tion.jda.internal.JDAImpl;

public class EventManagerProxy implements IEventManager {
   private final ExecutorService executor;
   private IEventManager subject;

   public EventManagerProxy(IEventManager subject, ExecutorService executor) {
      this.subject = subject;
      this.executor = executor;
   }

   public void setSubject(IEventManager subject) {
      this.subject = (IEventManager)(subject == null ? new InterfacedEventManager() : subject);
   }

   public IEventManager getSubject() {
      return this.subject;
   }

   public void register(@Nonnull Object listener) {
      this.subject.register(listener);
   }

   public void unregister(@Nonnull Object listener) {
      this.subject.unregister(listener);
   }

   public void handle(@Nonnull GenericEvent event) {
      try {
         if (this.executor != null && !this.executor.isShutdown()) {
            this.executor.execute(() -> this.handleInternally(event));
         } else {
            this.handleInternally(event);
         }
      } catch (RejectedExecutionException var3) {
         JDAImpl.LOG.warn("Event-Pool rejected event execution! Running on handling thread instead...");
         this.handleInternally(event);
      } catch (Exception ex) {
         JDAImpl.LOG.error((String)"Encountered exception trying to schedule event", (Throwable)ex);
      }

   }

   private void handleInternally(@Nonnull GenericEvent event) {
      try {
         this.subject.handle(event);
      } catch (RuntimeException e) {
         JDAImpl.LOG.error((String)"The EventManager.handle() call had an uncaught exception", (Throwable)e);
      }

   }

   @Nonnull
   public List<Object> getRegisteredListeners() {
      return this.subject.getRegisteredListeners();
   }
}
