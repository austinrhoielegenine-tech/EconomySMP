package net.dv8tion.jda.api.hooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.ClassWalker;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

public class AnnotatedEventManager implements IEventManager {
   private static final Logger LOGGER = JDALogger.getLog(AnnotatedEventManager.class);
   private final Set<Object> listeners = ConcurrentHashMap.newKeySet();
   private final Map<Class<?>, Map<Object, List<Method>>> methods = new ConcurrentHashMap();

   public void register(@Nonnull Object listener) {
      if (!listener.getClass().isArray()) {
         if (this.listeners.add(listener)) {
            this.registerListenerMethods(listener);
         }

      } else {
         for(Object o : listener) {
            this.register(o);
         }

      }
   }

   public void unregister(@Nonnull Object listener) {
      if (!listener.getClass().isArray()) {
         if (this.listeners.remove(listener)) {
            this.updateMethods();
         }

      } else {
         for(Object o : listener) {
            this.unregister(o);
         }

      }
   }

   @Nonnull
   public @Unmodifiable List<Object> getRegisteredListeners() {
      return Collections.unmodifiableList(new ArrayList(this.listeners));
   }

   public void handle(@Nonnull GenericEvent event) {
      for(Class<?> eventClass : ClassWalker.walk(event.getClass())) {
         Map<Object, List<Method>> listeners = (Map)this.methods.get(eventClass);
         if (listeners != null) {
            listeners.forEach((key, value) -> value.forEach((method) -> {
                  try {
                     method.setAccessible(true);
                     method.invoke(key, event);
                  } catch (InvocationTargetException | IllegalAccessException e1) {
                     JDAImpl.LOG.error((String)"Couldn't access annotated EventListener method", (Throwable)e1);
                  } catch (Throwable throwable) {
                     JDAImpl.LOG.error("One of the EventListeners had an uncaught exception", throwable);
                     if (throwable instanceof Error) {
                        throw (Error)throwable;
                     }
                  }

               }));
         }
      }

   }

   private void updateMethods() {
      this.methods.clear();

      for(Object listener : this.listeners) {
         this.registerListenerMethods(listener);
      }

   }

   private void registerListenerMethods(Object listener) {
      boolean isClass = listener instanceof Class;
      Class<?> c = isClass ? (Class)listener : listener.getClass();
      Method[] allMethods = c.getDeclaredMethods();

      for(Method m : allMethods) {
         if (m.isAnnotationPresent(SubscribeEvent.class) && (!isClass || Modifier.isStatic(m.getModifiers()))) {
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length == 1 && GenericEvent.class.isAssignableFrom(parameterTypes[0])) {
               Class<?> eventClass = parameterTypes[0];
               ((List)((Map)this.methods.computeIfAbsent(eventClass, (k) -> new ConcurrentHashMap())).computeIfAbsent(listener, (k) -> new CopyOnWriteArrayList())).add(m);
            } else {
               LOGGER.warn((String)"Method '{}' annotated with @{} must have at most 1 parameter, which implements GenericEvent", (Object)m, (Object)SubscribeEvent.class.getSimpleName());
            }
         }
      }

   }
}
