package com.fasterxml.jackson.annotation;

import java.util.HashMap;
import java.util.Map;

public class SimpleObjectIdResolver implements ObjectIdResolver {
   protected Map<ObjectIdGenerator.IdKey, Object> _items;

   public void bindItem(ObjectIdGenerator.IdKey id, Object ob) {
      if (this._items == null) {
         this._items = new HashMap();
      } else {
         Object old = this._items.get(id);
         if (old != null) {
            if (old == ob) {
               return;
            }

            throw new IllegalStateException(String.format("Object Id conflict: Id %s already bound to an Object %s: attempt to re-bind to a different Object %s", id.toString(), this._desc(old), this._desc(ob)));
         }
      }

      this._items.put(id, ob);
   }

   private String _desc(Object ob) {
      if (ob == null) {
         return "(null)";
      } else {
         String desc;
         if (ob instanceof String) {
            desc = "\"" + ob + "\"";
         } else {
            desc = ob.toString();
            if (desc.length() > 100) {
               desc = desc.substring(0, 100) + "[... truncated]";
            }
         }

         return "(type: `" + ob.getClass().getName() + "`, value: " + desc + ")";
      }
   }

   public Object resolveId(ObjectIdGenerator.IdKey id) {
      return this._items == null ? null : this._items.get(id);
   }

   public boolean canUseFor(ObjectIdResolver resolverType) {
      return resolverType.getClass() == this.getClass();
   }

   public ObjectIdResolver newForDeserialization(Object context) {
      return new SimpleObjectIdResolver();
   }
}
