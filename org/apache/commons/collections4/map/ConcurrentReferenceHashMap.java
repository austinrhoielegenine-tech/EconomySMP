package org.apache.commons.collections4.map;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
   static final ReferenceType DEFAULT_KEY_TYPE;
   static final ReferenceType DEFAULT_VALUE_TYPE;
   static final EnumSet<Option> DEFAULT_OPTIONS;
   static final int DEFAULT_INITIAL_CAPACITY = 16;
   static final float DEFAULT_LOAD_FACTOR = 0.75F;
   static final int DEFAULT_CONCURRENCY_LEVEL = 16;
   private static final int MAXIMUM_CAPACITY = 1073741824;
   private static final int MAX_SEGMENTS = 65536;
   private static final int RETRIES_BEFORE_LOCK = 2;
   private final int segmentMask;
   private final int segmentShift;
   private final Segment<K, V>[] segments;
   private final boolean identityComparisons;
   private transient Set<K> keySet;
   private transient Set<Map.Entry<K, V>> entrySet;
   private transient Collection<V> values;

   public static <K, V> Builder<K, V> builder() {
      return new Builder<K, V>();
   }

   private static int hash(int h) {
      h += h << 15 ^ -12931;
      h ^= h >>> 10;
      h += h << 3;
      h ^= h >>> 6;
      h += (h << 2) + (h << 14);
      return h ^ h >>> 16;
   }

   private ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType keyType, ReferenceType valueType, EnumSet<Option> options) {
      if (loadFactor > 0.0F && initialCapacity >= 0 && concurrencyLevel > 0) {
         if (concurrencyLevel > 65536) {
            concurrencyLevel = 65536;
         }

         int sshift = 0;

         int ssize;
         for(ssize = 1; ssize < concurrencyLevel; ssize <<= 1) {
            ++sshift;
         }

         this.segmentShift = 32 - sshift;
         this.segmentMask = ssize - 1;
         this.segments = ConcurrentReferenceHashMap.Segment.<K, V>newArray(ssize);
         if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
         }

         int c = initialCapacity / ssize;
         if (c * ssize < initialCapacity) {
            ++c;
         }

         int cap;
         for(cap = 1; cap < c; cap <<= 1) {
         }

         this.identityComparisons = options != null && options.contains(ConcurrentReferenceHashMap.Option.IDENTITY_COMPARISONS);

         for(int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(cap, loadFactor, keyType, valueType, this.identityComparisons);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   public void clear() {
      for(Segment<K, V> segment : this.segments) {
         segment.clear();
      }

   }

   public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(remappingFunction);
      int hash = this.hashOf(key);
      Segment<K, V> segment = this.segmentFor(hash);
      return segment.apply(key, hash, remappingFunction);
   }

   public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(mappingFunction);
      int hash = this.hashOf(key);
      Segment<K, V> segment = this.segmentFor(hash);
      V v = segment.get(key, hash);
      return (V)(v == null ? segment.put(key, hash, (Object)null, mappingFunction, true) : v);
   }

   public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(remappingFunction);
      int hash = this.hashOf(key);
      Segment<K, V> segment = this.segmentFor(hash);
      V v = segment.get(key, hash);
      return (V)(v == null ? null : this.segmentFor(hash).applyIfPresent(key, hash, remappingFunction));
   }

   public boolean containsKey(Object key) {
      int hash = this.hashOf(key);
      return this.segmentFor(hash).containsKey(key, hash);
   }

   public boolean containsValue(Object value) {
      Objects.requireNonNull(value, "value");
      Segment<K, V>[] segments = this.segments;
      int[] mc = new int[segments.length];

      for(int k = 0; k < 2; ++k) {
         int mcsum = 0;

         for(int i = 0; i < segments.length; ++i) {
            mcsum += mc[i] = segments[i].modCount;
            if (segments[i].containsValue(value)) {
               return true;
            }
         }

         boolean cleanSweep = true;
         if (mcsum != 0) {
            for(int i = 0; i < segments.length; ++i) {
               if (mc[i] != segments[i].modCount) {
                  cleanSweep = false;
                  break;
               }
            }
         }

         if (cleanSweep) {
            return false;
         }
      }

      for(Segment<K, V> segment : segments) {
         segment.lock();
      }

      boolean found = false;
      boolean var15 = false;

      try {
         var15 = true;
         Segment[] var20 = segments;
         int var24 = segments.length;
         int var27 = 0;

         while(true) {
            if (var27 >= var24) {
               var15 = false;
               break;
            }

            Segment<K, V> segment = var20[var27];
            if (segment.containsValue(value)) {
               found = true;
               var15 = false;
               break;
            }

            ++var27;
         }
      } finally {
         if (var15) {
            for(Segment<K, V> segment : segments) {
               segment.unlock();
            }

         }
      }

      for(Segment<K, V> segment : segments) {
         segment.unlock();
      }

      return found;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      Set<Map.Entry<K, V>> es = this.entrySet;
      return es != null ? es : (this.entrySet = new EntrySet(false));
   }

   public V get(Object key) {
      int hash = this.hashOf(key);
      return (V)this.segmentFor(hash).get(key, hash);
   }

   private int hashOf(Object key) {
      return hash(this.identityComparisons ? System.identityHashCode(key) : key.hashCode());
   }

   public boolean isEmpty() {
      Segment<K, V>[] segments = this.segments;
      int[] mc = new int[segments.length];
      int mcsum = 0;

      for(int i = 0; i < segments.length; ++i) {
         if (segments[i].count != 0) {
            return false;
         }

         mcsum += mc[i] = segments[i].modCount;
      }

      if (mcsum != 0) {
         for(int i = 0; i < segments.length; ++i) {
            if (segments[i].count != 0 || mc[i] != segments[i].modCount) {
               return false;
            }
         }
      }

      return true;
   }

   public Set<K> keySet() {
      Set<K> ks = this.keySet;
      return ks != null ? ks : (this.keySet = new KeySet());
   }

   public void purgeStaleEntries() {
      for(Segment<K, V> segment : this.segments) {
         segment.removeStale();
      }

   }

   public V put(K key, V value) {
      Objects.requireNonNull(key, "key");
      Objects.requireNonNull(value, "value");
      int hash = this.hashOf(key);
      return (V)this.segmentFor(hash).put(key, hash, value, (Function)null, false);
   }

   public void putAll(Map<? extends K, ? extends V> m) {
      for(Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
         this.put(e.getKey(), e.getValue());
      }

   }

   public V putIfAbsent(K key, V value) {
      Objects.requireNonNull(value, "value");
      int hash = this.hashOf(key);
      return (V)this.segmentFor(hash).put(key, hash, value, (Function)null, true);
   }

   public V remove(Object key) {
      int hash = this.hashOf(key);
      return (V)this.segmentFor(hash).remove(key, hash, (Object)null, false);
   }

   public boolean remove(Object key, Object value) {
      int hash = this.hashOf(key);
      if (value == null) {
         return false;
      } else {
         return this.segmentFor(hash).remove(key, hash, value, false) != null;
      }
   }

   public V replace(K key, V value) {
      Objects.requireNonNull(value, "value");
      int hash = this.hashOf(key);
      return (V)this.segmentFor(hash).replace(key, hash, value);
   }

   public boolean replace(K key, V oldValue, V newValue) {
      Objects.requireNonNull(oldValue, "oldValue");
      Objects.requireNonNull(newValue, "newValue");
      int hash = this.hashOf(key);
      return this.segmentFor(hash).replace(key, hash, oldValue, newValue);
   }

   private Segment<K, V> segmentFor(int hash) {
      return this.segments[hash >>> this.segmentShift & this.segmentMask];
   }

   public int size() {
      Segment<K, V>[] segments = this.segments;
      long sum = 0L;
      long check = 0L;
      int[] mc = new int[segments.length];

      for(int k = 0; k < 2; ++k) {
         check = 0L;
         sum = 0L;
         int mcsum = 0;

         for(int i = 0; i < segments.length; ++i) {
            sum += (long)segments[i].count;
            mcsum += mc[i] = segments[i].modCount;
         }

         if (mcsum != 0) {
            for(int i = 0; i < segments.length; ++i) {
               check += (long)segments[i].count;
               if (mc[i] != segments[i].modCount) {
                  check = -1L;
                  break;
               }
            }
         }

         if (check == sum) {
            break;
         }
      }

      if (check != sum) {
         sum = 0L;

         for(Segment<K, V> segment : segments) {
            segment.lock();
         }

         for(Segment<K, V> segment : segments) {
            sum += (long)segment.count;
         }

         for(Segment<K, V> segment : segments) {
            segment.unlock();
         }
      }

      return sum > 2147483647L ? Integer.MAX_VALUE : (int)sum;
   }

   public Collection<V> values() {
      Collection<V> vs = this.values;
      return vs != null ? vs : (this.values = new Values());
   }

   static {
      DEFAULT_KEY_TYPE = ConcurrentReferenceHashMap.ReferenceType.WEAK;
      DEFAULT_VALUE_TYPE = ConcurrentReferenceHashMap.ReferenceType.STRONG;
      DEFAULT_OPTIONS = null;
   }

   public static class Builder<K, V> implements Supplier<ConcurrentReferenceHashMap<K, V>> {
      private static final Map<?, ?> DEFAULT_SOURCE_MAP = null;
      private int initialCapacity = 16;
      private float loadFactor = 0.75F;
      private int concurrencyLevel = 16;
      private ReferenceType keyReferenceType;
      private ReferenceType valueReferenceType;
      private EnumSet<Option> options;
      private Map<? extends K, ? extends V> sourceMap;

      public Builder() {
         this.keyReferenceType = ConcurrentReferenceHashMap.DEFAULT_KEY_TYPE;
         this.valueReferenceType = ConcurrentReferenceHashMap.DEFAULT_VALUE_TYPE;
         this.options = ConcurrentReferenceHashMap.DEFAULT_OPTIONS;
         this.sourceMap = DEFAULT_SOURCE_MAP;
      }

      public ConcurrentReferenceHashMap<K, V> get() {
         ConcurrentReferenceHashMap<K, V> map = new ConcurrentReferenceHashMap<K, V>(this.initialCapacity, this.loadFactor, this.concurrencyLevel, this.keyReferenceType, this.valueReferenceType, this.options);
         if (this.sourceMap != null) {
            map.putAll(this.sourceMap);
         }

         return map;
      }

      public Builder<K, V> setConcurrencyLevel(int concurrencyLevel) {
         this.concurrencyLevel = concurrencyLevel;
         return this;
      }

      public Builder<K, V> setInitialCapacity(int initialCapacity) {
         this.initialCapacity = initialCapacity;
         return this;
      }

      public Builder<K, V> setKeyReferenceType(ReferenceType keyReferenceType) {
         this.keyReferenceType = keyReferenceType;
         return this;
      }

      public Builder<K, V> setLoadFactor(float loadFactor) {
         this.loadFactor = loadFactor;
         return this;
      }

      public Builder<K, V> setOptions(EnumSet<Option> options) {
         this.options = options;
         return this;
      }

      public Builder<K, V> setSourceMap(Map<? extends K, ? extends V> sourceMap) {
         this.sourceMap = sourceMap;
         return this;
      }

      public Builder<K, V> setValueReferenceType(ReferenceType valueReferenceType) {
         this.valueReferenceType = valueReferenceType;
         return this;
      }

      public Builder<K, V> softKeys() {
         this.setKeyReferenceType(ConcurrentReferenceHashMap.ReferenceType.SOFT);
         return this;
      }

      public Builder<K, V> softValues() {
         this.setValueReferenceType(ConcurrentReferenceHashMap.ReferenceType.SOFT);
         return this;
      }

      public Builder<K, V> strongKeys() {
         this.setKeyReferenceType(ConcurrentReferenceHashMap.ReferenceType.STRONG);
         return this;
      }

      public Builder<K, V> strongValues() {
         this.setValueReferenceType(ConcurrentReferenceHashMap.ReferenceType.STRONG);
         return this;
      }

      public Builder<K, V> weakKeys() {
         this.setKeyReferenceType(ConcurrentReferenceHashMap.ReferenceType.WEAK);
         return this;
      }

      public Builder<K, V> weakValues() {
         this.setValueReferenceType(ConcurrentReferenceHashMap.ReferenceType.WEAK);
         return this;
      }
   }

   private final class CachedEntryIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
      private final InitializableEntry<K, V> entry;

      private CachedEntryIterator() {
         this.entry = new InitializableEntry<K, V>();
      }

      public Map.Entry<K, V> next() {
         HashEntry<K, V> e = super.nextEntry();
         return this.entry.init(e.key(), e.value());
      }
   }

   private final class EntryIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<Map.Entry<K, V>> {
      private EntryIterator() {
      }

      public Map.Entry<K, V> next() {
         HashEntry<K, V> e = super.nextEntry();
         return ConcurrentReferenceHashMap.this.new WriteThroughEntry(e.key(), e.value());
      }
   }

   private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private final boolean cached;

      private EntrySet(final boolean cached) {
         this.cached = cached;
      }

      public void clear() {
         ConcurrentReferenceHashMap.this.clear();
      }

      public boolean contains(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            V v = (V)ConcurrentReferenceHashMap.this.get(((Map.Entry)o).getKey());
            return Objects.equals(v, ((Map.Entry)o).getValue());
         }
      }

      public boolean isEmpty() {
         return ConcurrentReferenceHashMap.this.isEmpty();
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return (Iterator<Map.Entry<K, V>>)(this.cached ? ConcurrentReferenceHashMap.this.new CachedEntryIterator() : ConcurrentReferenceHashMap.this.new EntryIterator());
      }

      public boolean remove(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> e = (Map.Entry)o;
            return ConcurrentReferenceHashMap.this.remove(e.getKey(), e.getValue());
         }
      }

      public int size() {
         return ConcurrentReferenceHashMap.this.size();
      }
   }

   private static final class HashEntry<K, V> {
      private final Object keyRef;
      private final int hash;
      private volatile Object valueRef;
      private final HashEntry<K, V> next;

      static <K, V> HashEntry<K, V>[] newArray(int i) {
         return new HashEntry[i];
      }

      HashEntry(K key, int hash, HashEntry<K, V> next, V value, ReferenceType keyType, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
         this.hash = hash;
         this.next = next;
         this.keyRef = this.newKeyReference(key, keyType, refQueue);
         this.valueRef = this.newValueReference(value, valueType, refQueue);
      }

      V dereferenceValue(Object value) {
         return (V)(value instanceof KeyReference ? ((Reference)value).get() : value);
      }

      K key() {
         return (K)(this.keyRef instanceof KeyReference ? ((Reference)this.keyRef).get() : this.keyRef);
      }

      Object newKeyReference(K key, ReferenceType keyType, ReferenceQueue<Object> refQueue) {
         if (keyType == ConcurrentReferenceHashMap.ReferenceType.WEAK) {
            return new WeakKeyReference(key, this.hash, refQueue);
         } else {
            return keyType == ConcurrentReferenceHashMap.ReferenceType.SOFT ? new SoftKeyReference(key, this.hash, refQueue) : key;
         }
      }

      Object newValueReference(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
         if (valueType == ConcurrentReferenceHashMap.ReferenceType.WEAK) {
            return new WeakValueReference(value, this.keyRef, this.hash, refQueue);
         } else {
            return valueType == ConcurrentReferenceHashMap.ReferenceType.SOFT ? new SoftValueReference(value, this.keyRef, this.hash, refQueue) : value;
         }
      }

      void setValue(V value, ReferenceType valueType, ReferenceQueue<Object> refQueue) {
         this.valueRef = this.newValueReference(value, valueType, refQueue);
      }

      V value() {
         return (V)this.dereferenceValue(this.valueRef);
      }
   }

   private abstract class HashIterator {
      private int nextSegmentIndex;
      private int nextTableIndex;
      private HashEntry<K, V>[] currentTable;
      private HashEntry<K, V> nextEntry;
      private HashEntry<K, V> lastReturned;
      private K currentKey;

      private HashIterator() {
         this.nextSegmentIndex = ConcurrentReferenceHashMap.this.segments.length - 1;
         this.nextTableIndex = -1;
         this.advance();
      }

      final void advance() {
         if (this.nextEntry == null || (this.nextEntry = this.nextEntry.next) == null) {
            while(this.nextTableIndex >= 0) {
               if ((this.nextEntry = this.currentTable[this.nextTableIndex--]) != null) {
                  return;
               }
            }

            while(this.nextSegmentIndex >= 0) {
               Segment<K, V> seg = ConcurrentReferenceHashMap.this.segments[this.nextSegmentIndex--];
               if (seg.count != 0) {
                  this.currentTable = seg.table;

                  for(int j = this.currentTable.length - 1; j >= 0; --j) {
                     if ((this.nextEntry = this.currentTable[j]) != null) {
                        this.nextTableIndex = j - 1;
                        return;
                     }
                  }
               }
            }

         }
      }

      public boolean hasMoreElements() {
         return this.hasNext();
      }

      public boolean hasNext() {
         while(this.nextEntry != null) {
            if (this.nextEntry.key() != null) {
               return true;
            }

            this.advance();
         }

         return false;
      }

      HashEntry<K, V> nextEntry() {
         while(this.nextEntry != null) {
            this.lastReturned = this.nextEntry;
            this.currentKey = this.lastReturned.key();
            this.advance();
            if (this.currentKey != null) {
               return this.lastReturned;
            }
         }

         throw new NoSuchElementException();
      }

      public void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else {
            ConcurrentReferenceHashMap.this.remove(this.currentKey);
            this.lastReturned = null;
         }
      }
   }

   private static final class InitializableEntry<K, V> implements Map.Entry<K, V> {
      private K key;
      private V value;

      private InitializableEntry() {
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public Map.Entry<K, V> init(K key, V value) {
         this.key = key;
         this.value = value;
         return this;
      }

      public V setValue(V value) {
         throw new UnsupportedOperationException();
      }
   }

   private final class KeyIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<K>, Enumeration<K> {
      private KeyIterator() {
      }

      public K next() {
         return (K)super.nextEntry().key();
      }

      public K nextElement() {
         return (K)super.nextEntry().key();
      }
   }

   private final class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public void clear() {
         ConcurrentReferenceHashMap.this.clear();
      }

      public boolean contains(Object o) {
         return ConcurrentReferenceHashMap.this.containsKey(o);
      }

      public boolean isEmpty() {
         return ConcurrentReferenceHashMap.this.isEmpty();
      }

      public Iterator<K> iterator() {
         return ConcurrentReferenceHashMap.this.new KeyIterator();
      }

      public boolean remove(Object o) {
         return ConcurrentReferenceHashMap.this.remove(o) != null;
      }

      public int size() {
         return ConcurrentReferenceHashMap.this.size();
      }
   }

   public static enum Option {
      IDENTITY_COMPARISONS;

      // $FF: synthetic method
      private static Option[] $values() {
         return new Option[]{IDENTITY_COMPARISONS};
      }
   }

   public static enum ReferenceType {
      STRONG,
      WEAK,
      SOFT;

      // $FF: synthetic method
      private static ReferenceType[] $values() {
         return new ReferenceType[]{STRONG, WEAK, SOFT};
      }
   }

   private static final class Segment<K, V> extends ReentrantLock {
      private static final long serialVersionUID = 1L;
      private transient volatile int count;
      private transient int modCount;
      private transient int threshold;
      private transient volatile HashEntry<K, V>[] table;
      private final float loadFactor;
      private transient volatile ReferenceQueue<Object> refQueue;
      private final ReferenceType keyType;
      private final ReferenceType valueType;
      private final boolean identityComparisons;

      static <K, V> Segment<K, V>[] newArray(int i) {
         return new Segment[i];
      }

      Segment(int initialCapacity, float loadFactor, ReferenceType keyType, ReferenceType valueType, boolean identityComparisons) {
         this.loadFactor = loadFactor;
         this.keyType = keyType;
         this.valueType = valueType;
         this.identityComparisons = identityComparisons;
         this.setTable(ConcurrentReferenceHashMap.HashEntry.newArray(initialCapacity));
      }

      V apply(K key, int hash, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         this.lock();

         Object var6;
         try {
            V oldValue = (V)this.get(key, hash);
            V newValue = (V)remappingFunction.apply(key, oldValue);
            if (newValue != null) {
               this.putInternal(key, hash, newValue, (Function)null, false);
               var6 = newValue;
               return (V)var6;
            }

            if (oldValue != null) {
               this.removeInternal(key, hash, oldValue, false);
            }

            var6 = null;
         } finally {
            this.unlock();
         }

         return (V)var6;
      }

      V applyIfPresent(K key, int hash, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
         this.lock();

         V newValue;
         try {
            V oldValue = (V)this.get(key, hash);
            if (oldValue != null) {
               newValue = (V)remappingFunction.apply(key, oldValue);
               if (newValue == null) {
                  this.removeInternal(key, hash, oldValue, false);
                  Object var11 = null;
                  return (V)var11;
               }

               this.putInternal(key, hash, newValue, (Function)null, false);
               Object var6 = newValue;
               return (V)var6;
            }

            newValue = null;
         } finally {
            this.unlock();
         }

         return newValue;
      }

      void clear() {
         if (this.count != 0) {
            this.lock();

            try {
               HashEntry<K, V>[] tab = this.table;
               Arrays.fill(tab, (Object)null);
               ++this.modCount;
               this.refQueue = new ReferenceQueue();
               this.count = 0;
            } finally {
               this.unlock();
            }
         }

      }

      boolean containsKey(Object key, int hash) {
         if (this.count != 0) {
            for(HashEntry<K, V> e = this.getFirst(hash); e != null; e = e.next) {
               if (e.hash == hash && this.keyEq(key, e.key())) {
                  return true;
               }
            }
         }

         return false;
      }

      boolean containsValue(Object value) {
         if (this.count != 0) {
            HashEntry<K, V>[] tab = this.table;
            int len = tab.length;

            for(int i = 0; i < len; ++i) {
               for(HashEntry<K, V> e = tab[i]; e != null; e = e.next) {
                  Object opaque = e.valueRef;
                  V v;
                  if (opaque == null) {
                     v = (V)this.readValueUnderLock(e);
                  } else {
                     v = e.dereferenceValue(opaque);
                  }

                  if (Objects.equals(value, v)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }

      V get(Object key, int hash) {
         if (this.count != 0) {
            for(HashEntry<K, V> e = this.getFirst(hash); e != null; e = e.next) {
               if (e.hash == hash && this.keyEq(key, e.key())) {
                  Object opaque = e.valueRef;
                  if (opaque != null) {
                     return e.dereferenceValue(opaque);
                  }

                  return (V)this.readValueUnderLock(e);
               }
            }
         }

         return null;
      }

      HashEntry<K, V> getFirst(int hash) {
         HashEntry<K, V>[] tab = this.table;
         return tab[hash & tab.length - 1];
      }

      V getValue(K key, V value, Function<? super K, ? extends V> function) {
         return (V)(value != null ? value : function.apply(key));
      }

      private boolean keyEq(Object src, Object dest) {
         return this.identityComparisons ? src == dest : Objects.equals(src, dest);
      }

      HashEntry<K, V> newHashEntry(K key, int hash, HashEntry<K, V> next, V value) {
         return new HashEntry<K, V>(key, hash, next, value, this.keyType, this.valueType, this.refQueue);
      }

      V put(K key, int hash, V value, Function<? super K, ? extends V> function, boolean onlyIfAbsent) {
         this.lock();

         Object var6;
         try {
            var6 = this.putInternal(key, hash, value, function, onlyIfAbsent);
         } finally {
            this.unlock();
         }

         return (V)var6;
      }

      private V putInternal(K key, int hash, V value, Function<? super K, ? extends V> function, boolean onlyIfAbsent) {
         this.removeStale();
         int c = this.count;
         if (c++ > this.threshold) {
            int reduced = this.rehash();
            if (reduced > 0) {
               this.count = (c -= reduced) - 1;
            }
         }

         HashEntry<K, V>[] tab = this.table;
         int index = hash & tab.length - 1;
         HashEntry<K, V> first = tab[index];

         HashEntry<K, V> e;
         for(e = first; e != null && (e.hash != hash || !this.keyEq(key, e.key())); e = e.next) {
         }

         V resultValue;
         if (e != null) {
            resultValue = e.value();
            if (!onlyIfAbsent) {
               e.setValue(this.getValue(key, value, function), this.valueType, this.refQueue);
            }
         } else {
            V v = (V)this.getValue(key, value, function);
            resultValue = (V)(function != null ? v : null);
            if (v != null) {
               ++this.modCount;
               tab[index] = this.newHashEntry(key, hash, first, v);
               this.count = c;
            }
         }

         return resultValue;
      }

      V readValueUnderLock(HashEntry<K, V> e) {
         this.lock();

         Object var2;
         try {
            this.removeStale();
            var2 = e.value();
         } finally {
            this.unlock();
         }

         return (V)var2;
      }

      int rehash() {
         HashEntry<K, V>[] oldTable = this.table;
         int oldCapacity = oldTable.length;
         if (oldCapacity >= 1073741824) {
            return 0;
         } else {
            HashEntry<K, V>[] newTable = ConcurrentReferenceHashMap.HashEntry.<K, V>newArray(oldCapacity << 1);
            this.threshold = (int)((float)newTable.length * this.loadFactor);
            int sizeMask = newTable.length - 1;
            int reduce = 0;

            for(int i = 0; i < oldCapacity; ++i) {
               HashEntry<K, V> e = oldTable[i];
               if (e != null) {
                  HashEntry<K, V> next = e.next;
                  int idx = e.hash & sizeMask;
                  if (next == null) {
                     newTable[idx] = e;
                  } else {
                     HashEntry<K, V> lastRun = e;
                     int lastIdx = idx;

                     for(HashEntry<K, V> last = next; last != null; last = last.next) {
                        int k = last.hash & sizeMask;
                        if (k != lastIdx) {
                           lastIdx = k;
                           lastRun = last;
                        }
                     }

                     newTable[lastIdx] = lastRun;

                     for(HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                        K key = p.key();
                        if (key == null) {
                           ++reduce;
                        } else {
                           int k = p.hash & sizeMask;
                           HashEntry<K, V> n = newTable[k];
                           newTable[k] = this.newHashEntry(key, p.hash, n, p.value());
                        }
                     }
                  }
               }
            }

            this.table = newTable;
            return reduce;
         }
      }

      V remove(Object key, int hash, Object value, boolean refRemove) {
         this.lock();

         Object var5;
         try {
            var5 = this.removeInternal(key, hash, value, refRemove);
         } finally {
            this.unlock();
         }

         return (V)var5;
      }

      private V removeInternal(Object key, int hash, Object value, boolean refRemove) {
         if (!refRemove) {
            this.removeStale();
         }

         int c = this.count - 1;
         HashEntry<K, V>[] tab = this.table;
         int index = hash & tab.length - 1;
         HashEntry<K, V> first = tab[index];

         HashEntry<K, V> e;
         for(e = first; e != null && key != e.keyRef && (refRemove || hash != e.hash || !this.keyEq(key, e.key())); e = e.next) {
         }

         V oldValue = null;
         if (e != null) {
            V v = e.value();
            if (value == null || value.equals(v)) {
               oldValue = v;
               ++this.modCount;
               HashEntry<K, V> newFirst = e.next;

               for(HashEntry<K, V> p = first; p != e; p = p.next) {
                  K pKey = p.key();
                  if (pKey == null) {
                     --c;
                  } else {
                     newFirst = this.newHashEntry(pKey, p.hash, newFirst, p.value());
                  }
               }

               tab[index] = newFirst;
               this.count = c;
            }
         }

         return oldValue;
      }

      void removeStale() {
         KeyReference ref;
         while((ref = (KeyReference)this.refQueue.poll()) != null) {
            this.remove(ref.keyRef(), ref.keyHash(), (Object)null, true);
         }

      }

      V replace(K key, int hash, V newValue) {
         this.lock();

         Object var4;
         try {
            var4 = this.replaceInternal(key, hash, newValue);
         } finally {
            this.unlock();
         }

         return (V)var4;
      }

      boolean replace(K key, int hash, V oldValue, V newValue) {
         this.lock();

         boolean var5;
         try {
            var5 = this.replaceInternal2(key, hash, oldValue, newValue);
         } finally {
            this.unlock();
         }

         return var5;
      }

      private V replaceInternal(K key, int hash, V newValue) {
         this.removeStale();

         HashEntry<K, V> e;
         for(e = this.getFirst(hash); e != null && (e.hash != hash || !this.keyEq(key, e.key())); e = e.next) {
         }

         V oldValue = null;
         if (e != null) {
            oldValue = e.value();
            e.setValue(newValue, this.valueType, this.refQueue);
         }

         return oldValue;
      }

      private boolean replaceInternal2(K key, int hash, V oldValue, V newValue) {
         this.removeStale();

         HashEntry<K, V> e;
         for(e = this.getFirst(hash); e != null && (e.hash != hash || !this.keyEq(key, e.key())); e = e.next) {
         }

         boolean replaced = false;
         if (e != null && Objects.equals(oldValue, e.value())) {
            replaced = true;
            e.setValue(newValue, this.valueType, this.refQueue);
         }

         return replaced;
      }

      void setTable(HashEntry<K, V>[] newTable) {
         this.threshold = (int)((float)newTable.length * this.loadFactor);
         this.table = newTable;
         this.refQueue = new ReferenceQueue();
      }
   }

   private static class SimpleEntry<K, V> implements Map.Entry<K, V> {
      private final K key;
      private V value;

      private static boolean eq(Object o1, Object o2) {
         return Objects.equals(o1, o2);
      }

      SimpleEntry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public boolean equals(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<?, ?> e = (Map.Entry)o;
            return eq(this.key, e.getKey()) && eq(this.value, e.getValue());
         }
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public V setValue(V value) {
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      }

      public String toString() {
         return this.key + "=" + this.value;
      }
   }

   private static final class SoftKeyReference<K> extends SoftReference<K> implements KeyReference {
      private final int hash;

      SoftKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
         super(key, refQueue);
         this.hash = hash;
      }

      public int keyHash() {
         return this.hash;
      }

      public Object keyRef() {
         return this;
      }
   }

   private static final class SoftValueReference<V> extends SoftReference<V> implements KeyReference {
      private final Object keyRef;
      private final int hash;

      SoftValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
         super(value, refQueue);
         this.keyRef = keyRef;
         this.hash = hash;
      }

      public int keyHash() {
         return this.hash;
      }

      public Object keyRef() {
         return this.keyRef;
      }
   }

   private final class ValueIterator extends ConcurrentReferenceHashMap<K, V>.HashIterator implements Iterator<V>, Enumeration<V> {
      private ValueIterator() {
      }

      public V next() {
         return (V)super.nextEntry().value();
      }

      public V nextElement() {
         return (V)super.nextEntry().value();
      }
   }

   private final class Values extends AbstractCollection<V> {
      private Values() {
      }

      public void clear() {
         ConcurrentReferenceHashMap.this.clear();
      }

      public boolean contains(Object o) {
         return ConcurrentReferenceHashMap.this.containsValue(o);
      }

      public boolean isEmpty() {
         return ConcurrentReferenceHashMap.this.isEmpty();
      }

      public Iterator<V> iterator() {
         return ConcurrentReferenceHashMap.this.new ValueIterator();
      }

      public int size() {
         return ConcurrentReferenceHashMap.this.size();
      }
   }

   private static final class WeakKeyReference<K> extends WeakReference<K> implements KeyReference {
      private final int hash;

      WeakKeyReference(K key, int hash, ReferenceQueue<Object> refQueue) {
         super(key, refQueue);
         this.hash = hash;
      }

      public int keyHash() {
         return this.hash;
      }

      public Object keyRef() {
         return this;
      }
   }

   private static final class WeakValueReference<V> extends WeakReference<V> implements KeyReference {
      private final Object keyRef;
      private final int hash;

      WeakValueReference(V value, Object keyRef, int hash, ReferenceQueue<Object> refQueue) {
         super(value, refQueue);
         this.keyRef = keyRef;
         this.hash = hash;
      }

      public int keyHash() {
         return this.hash;
      }

      public Object keyRef() {
         return this.keyRef;
      }
   }

   private final class WriteThroughEntry extends SimpleEntry<K, V> {
      private WriteThroughEntry(final K k, final V v) {
         super(k, v);
      }

      public V setValue(V value) {
         Objects.requireNonNull(value, "value");
         V v = (V)super.setValue(value);
         ConcurrentReferenceHashMap.this.put(this.getKey(), value);
         return v;
      }
   }

   private interface KeyReference {
      int keyHash();

      Object keyRef();
   }
}
