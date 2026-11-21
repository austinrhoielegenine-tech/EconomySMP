package org.apache.commons.collections4.multimap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.commons.collections4.MultiValuedMap;

public class LinkedHashSetValuedLinkedHashMap<K, V> extends AbstractSetValuedMap<K, V> implements Serializable {
   private static final long serialVersionUID = 20241020L;
   private static final int DEFAULT_INITIAL_MAP_CAPACITY = 16;
   private static final int DEFAULT_INITIAL_SET_CAPACITY = 3;
   private final int initialSetCapacity;

   public LinkedHashSetValuedLinkedHashMap() {
      this(16, 3);
   }

   public LinkedHashSetValuedLinkedHashMap(int initialSetCapacity) {
      this(16, initialSetCapacity);
   }

   public LinkedHashSetValuedLinkedHashMap(int initialMapCapacity, int initialSetCapacity) {
      super(new LinkedHashMap(initialMapCapacity));
      this.initialSetCapacity = initialSetCapacity;
   }

   public LinkedHashSetValuedLinkedHashMap(Map<? extends K, ? extends V> map) {
      this(map.size(), 3);
      super.putAll(map);
   }

   public LinkedHashSetValuedLinkedHashMap(MultiValuedMap<? extends K, ? extends V> map) {
      this(map.size(), 3);
      super.putAll(map);
   }

   protected LinkedHashSet<V> createCollection() {
      return new LinkedHashSet(this.initialSetCapacity);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.setMap(new LinkedHashMap());
      this.doReadObject(in);
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      this.doWriteObject(out);
   }
}
