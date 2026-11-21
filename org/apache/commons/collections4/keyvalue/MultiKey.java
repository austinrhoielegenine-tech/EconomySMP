package org.apache.commons.collections4.keyvalue;

import [Ljava.lang.Object;;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class MultiKey<K> implements Serializable {
   private static final long serialVersionUID = 4465448607415788805L;
   private final K[] keys;
   private transient int hashCode;

   private static <T> Class<? extends T> getClass(T value) {
      return value == null ? Object.class : value.getClass();
   }

   @SafeVarargs
   private static <T> Class<? extends T> getComponentType(T... values) {
      Class<? extends T> rootClass = Object.class;
      if (values == null) {
         return rootClass;
      } else {
         Class<? extends T> prevClass = values.length > 0 ? getClass(values[0]) : rootClass;

         for(int i = 1; i < values.length; ++i) {
            Class<? extends T> classI = getClass(values[i]);
            if (prevClass != classI) {
               return rootClass;
            }

            prevClass = classI;
         }

         return prevClass;
      }
   }

   private static <T> T[] newArray(T key1, T key2) {
      T[] array = (T[])((Object[])Array.newInstance(getComponentType(key1, key2), 2));
      array[0] = key1;
      array[1] = key2;
      return array;
   }

   private static <T> T[] newArray(T key1, T key2, T key3) {
      T[] array = (T[])((Object[])Array.newInstance(getComponentType(key1, key2, key3), 3));
      array[0] = key1;
      array[1] = key2;
      array[2] = key3;
      return array;
   }

   private static <T> T[] newArray(T key1, T key2, T key3, T key4) {
      T[] array = (T[])((Object[])Array.newInstance(getComponentType(key1, key2, key3, key4), 4));
      array[0] = key1;
      array[1] = key2;
      array[2] = key3;
      array[3] = key4;
      return array;
   }

   private static <T> T[] newArray(T key1, T key2, T key3, T key4, T key5) {
      T[] array = (T[])((Object[])Array.newInstance(getComponentType(key1, key2, key3, key4, key5), 5));
      array[0] = key1;
      array[1] = key2;
      array[2] = key3;
      array[3] = key4;
      array[4] = key5;
      return array;
   }

   public MultiKey(K key1, K key2) {
      this(newArray(key1, key2), false);
   }

   public MultiKey(K key1, K key2, K key3) {
      this(newArray(key1, key2, key3), false);
   }

   public MultiKey(K key1, K key2, K key3, K key4) {
      this(newArray(key1, key2, key3, key4), false);
   }

   public MultiKey(K key1, K key2, K key3, K key4, K key5) {
      this(newArray(key1, key2, key3, key4, key5), false);
   }

   public MultiKey(K[] keys) {
      this(keys, true);
   }

   public MultiKey(K[] keys, boolean makeClone) {
      Objects.requireNonNull(keys, "keys");
      this.keys = (K[])(makeClone ? (Object[])((Object;)keys).clone() : keys);
      this.calculateHashCode(keys);
   }

   private void calculateHashCode(Object[] keys) {
      int total = 0;

      for(Object key : keys) {
         if (key != null) {
            total ^= key.hashCode();
         }
      }

      this.hashCode = total;
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (other instanceof MultiKey) {
         MultiKey<?> otherMulti = (MultiKey)other;
         return Arrays.equals(this.keys, otherMulti.keys);
      } else {
         return false;
      }
   }

   public K getKey(int index) {
      return (K)this.keys[index];
   }

   public K[] getKeys() {
      return (K[])((Object[])this.keys.clone());
   }

   public int hashCode() {
      return this.hashCode;
   }

   protected Object readResolve() {
      this.calculateHashCode(this.keys);
      return this;
   }

   public int size() {
      return this.keys.length;
   }

   public String toString() {
      return "MultiKey" + Arrays.toString(this.keys);
   }
}
