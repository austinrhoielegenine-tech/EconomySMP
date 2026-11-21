package org.apache.commons.collections4.trie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;

public abstract class AbstractPatriciaTrie<K, V> extends AbstractBitwiseTrie<K, V> {
   private static final long serialVersionUID = 5155253417231339498L;
   private transient TrieEntry<K, V> root = new TrieEntry<K, V>((Object)null, (Object)null, -1);
   private transient volatile Set<K> keySet;
   private transient volatile Collection<V> values;
   private transient volatile Set<Map.Entry<K, V>> entrySet;
   private transient int size;
   protected transient int modCount;

   static boolean isValidUplink(TrieEntry<?, ?> next, TrieEntry<?, ?> from) {
      return next != null && next.bitIndex <= from.bitIndex && !next.isEmpty();
   }

   protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer) {
      super(keyAnalyzer);
   }

   protected AbstractPatriciaTrie(KeyAnalyzer<? super K> keyAnalyzer, Map<? extends K, ? extends V> map) {
      super(keyAnalyzer);
      this.putAll(map);
   }

   TrieEntry<K, V> addEntry(TrieEntry<K, V> entry, int lengthInBits) {
      TrieEntry<K, V> current = this.root.left;
      TrieEntry<K, V> path = this.root;

      while(current.bitIndex < entry.bitIndex && current.bitIndex > path.bitIndex) {
         path = current;
         if (!this.isBitSet(entry.key, current.bitIndex, lengthInBits)) {
            current = current.left;
         } else {
            current = current.right;
         }
      }

      entry.predecessor = entry;
      if (!this.isBitSet(entry.key, entry.bitIndex, lengthInBits)) {
         entry.left = entry;
         entry.right = current;
      } else {
         entry.left = current;
         entry.right = entry;
      }

      entry.parent = path;
      if (current.bitIndex >= entry.bitIndex) {
         current.parent = entry;
      }

      if (current.bitIndex <= path.bitIndex) {
         current.predecessor = entry;
      }

      if (path != this.root && this.isBitSet(entry.key, path.bitIndex, lengthInBits)) {
         path.right = entry;
      } else {
         path.left = entry;
      }

      return entry;
   }

   TrieEntry<K, V> ceilingEntry(K key) {
      int lengthInBits = this.lengthInBits(key);
      if (lengthInBits == 0) {
         return !this.root.isEmpty() ? this.root : this.firstEntry();
      } else {
         TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
         if (this.compareKeys(key, found.key)) {
            return found;
         } else {
            int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
               TrieEntry<K, V> added = new TrieEntry<K, V>(key, (Object)null, bitIndex);
               this.addEntry(added, lengthInBits);
               this.incrementSize();
               TrieEntry<K, V> ceil = this.nextEntry(added);
               this.removeEntry(added);
               this.modCount -= 2;
               return ceil;
            } else if (KeyAnalyzer.isNullBitKey(bitIndex)) {
               return !this.root.isEmpty() ? this.root : this.firstEntry();
            } else if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
               return found;
            } else {
               throw new IllegalStateException("invalid lookup: " + key);
            }
         }
      }
   }

   public void clear() {
      this.root.key = null;
      this.root.bitIndex = -1;
      this.root.value = null;
      this.root.parent = null;
      this.root.left = this.root;
      this.root.right = null;
      this.root.predecessor = this.root;
      this.size = 0;
      this.incrementModCount();
   }

   public Comparator<? super K> comparator() {
      return this.getKeyAnalyzer();
   }

   public boolean containsKey(Object k) {
      if (k == null) {
         return false;
      } else {
         K key = (K)this.castKey(k);
         int lengthInBits = this.lengthInBits(key);
         TrieEntry<K, V> entry = this.getNearestEntryForKey(key, lengthInBits);
         return !entry.isEmpty() && this.compareKeys(key, entry.key);
      }
   }

   void decrementSize() {
      --this.size;
      this.incrementModCount();
   }

   public Set<Map.Entry<K, V>> entrySet() {
      if (this.entrySet == null) {
         this.entrySet = new EntrySet();
      }

      return this.entrySet;
   }

   TrieEntry<K, V> firstEntry() {
      return this.isEmpty() ? null : this.followLeft(this.root);
   }

   public K firstKey() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return (K)this.firstEntry().getKey();
      }
   }

   TrieEntry<K, V> floorEntry(K key) {
      int lengthInBits = this.lengthInBits(key);
      if (lengthInBits == 0) {
         return !this.root.isEmpty() ? this.root : null;
      } else {
         TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
         if (this.compareKeys(key, found.key)) {
            return found;
         } else {
            int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
               TrieEntry<K, V> added = new TrieEntry<K, V>(key, (Object)null, bitIndex);
               this.addEntry(added, lengthInBits);
               this.incrementSize();
               TrieEntry<K, V> floor = this.previousEntry(added);
               this.removeEntry(added);
               this.modCount -= 2;
               return floor;
            } else if (KeyAnalyzer.isNullBitKey(bitIndex)) {
               return !this.root.isEmpty() ? this.root : null;
            } else if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
               return found;
            } else {
               throw new IllegalStateException("invalid lookup: " + key);
            }
         }
      }
   }

   TrieEntry<K, V> followLeft(TrieEntry<K, V> node) {
      while(true) {
         TrieEntry<K, V> child = node.left;
         if (child.isEmpty()) {
            child = node.right;
         }

         if (child.bitIndex <= node.bitIndex) {
            return child;
         }

         node = child;
      }
   }

   TrieEntry<K, V> followRight(TrieEntry<K, V> node) {
      if (node.right == null) {
         return null;
      } else {
         while(node.right.bitIndex > node.bitIndex) {
            node = node.right;
         }

         return node.right;
      }
   }

   public V get(Object k) {
      TrieEntry<K, V> entry = this.getEntry(k);
      return (V)(entry != null ? entry.getValue() : null);
   }

   TrieEntry<K, V> getEntry(Object k) {
      K key = (K)this.castKey(k);
      if (key == null) {
         return null;
      } else {
         int lengthInBits = this.lengthInBits(key);
         TrieEntry<K, V> entry = this.getNearestEntryForKey(key, lengthInBits);
         return !entry.isEmpty() && this.compareKeys(key, entry.key) ? entry : null;
      }
   }

   TrieEntry<K, V> getNearestEntryForKey(K key, int lengthInBits) {
      TrieEntry<K, V> current = this.root.left;
      TrieEntry<K, V> path = this.root;

      while(current.bitIndex > path.bitIndex) {
         path = current;
         if (!this.isBitSet(key, current.bitIndex, lengthInBits)) {
            current = current.left;
         } else {
            current = current.right;
         }
      }

      return current;
   }

   private SortedMap<K, V> getPrefixMapByBits(K key, int offsetInBits, int lengthInBits) {
      int offsetLength = offsetInBits + lengthInBits;
      if (offsetLength > this.lengthInBits(key)) {
         throw new IllegalArgumentException(offsetInBits + " + " + lengthInBits + " > " + this.lengthInBits(key));
      } else {
         return (SortedMap<K, V>)(offsetLength == 0 ? this : new PrefixRangeMap(key, offsetInBits, lengthInBits));
      }
   }

   public SortedMap<K, V> headMap(K toKey) {
      return new RangeEntryMap((Object)null, toKey);
   }

   TrieEntry<K, V> higherEntry(K key) {
      int lengthInBits = this.lengthInBits(key);
      if (lengthInBits == 0) {
         if (!this.root.isEmpty()) {
            return this.size() > 1 ? this.nextEntry(this.root) : null;
         } else {
            return this.firstEntry();
         }
      } else {
         TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
         if (this.compareKeys(key, found.key)) {
            return this.nextEntry(found);
         } else {
            int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
               TrieEntry<K, V> added = new TrieEntry<K, V>(key, (Object)null, bitIndex);
               this.addEntry(added, lengthInBits);
               this.incrementSize();
               TrieEntry<K, V> ceil = this.nextEntry(added);
               this.removeEntry(added);
               this.modCount -= 2;
               return ceil;
            } else if (KeyAnalyzer.isNullBitKey(bitIndex)) {
               if (!this.root.isEmpty()) {
                  return this.firstEntry();
               } else {
                  return this.size() > 1 ? this.nextEntry(this.firstEntry()) : null;
               }
            } else if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
               return this.nextEntry(found);
            } else {
               throw new IllegalStateException("invalid lookup: " + key);
            }
         }
      }
   }

   private void incrementModCount() {
      ++this.modCount;
   }

   void incrementSize() {
      ++this.size;
      this.incrementModCount();
   }

   public Set<K> keySet() {
      if (this.keySet == null) {
         this.keySet = new KeySet();
      }

      return this.keySet;
   }

   TrieEntry<K, V> lastEntry() {
      return this.followRight(this.root.left);
   }

   public K lastKey() {
      TrieEntry<K, V> entry = this.lastEntry();
      if (entry != null) {
         return (K)entry.getKey();
      } else {
         throw new NoSuchElementException();
      }
   }

   TrieEntry<K, V> lowerEntry(K key) {
      int lengthInBits = this.lengthInBits(key);
      if (lengthInBits == 0) {
         return null;
      } else {
         TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
         if (this.compareKeys(key, found.key)) {
            return this.previousEntry(found);
         } else {
            int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
               TrieEntry<K, V> added = new TrieEntry<K, V>(key, (Object)null, bitIndex);
               this.addEntry(added, lengthInBits);
               this.incrementSize();
               TrieEntry<K, V> prior = this.previousEntry(added);
               this.removeEntry(added);
               this.modCount -= 2;
               return prior;
            } else if (KeyAnalyzer.isNullBitKey(bitIndex)) {
               return null;
            } else if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
               return this.previousEntry(found);
            } else {
               throw new IllegalStateException("invalid lookup: " + key);
            }
         }
      }
   }

   public OrderedMapIterator<K, V> mapIterator() {
      return new TrieMapIterator();
   }

   TrieEntry<K, V> nextEntry(TrieEntry<K, V> node) {
      return node == null ? this.firstEntry() : this.nextEntryImpl(node.predecessor, node, (TrieEntry)null);
   }

   TrieEntry<K, V> nextEntryImpl(TrieEntry<K, V> start, TrieEntry<K, V> previous, TrieEntry<K, V> tree) {
      TrieEntry<K, V> current = start;
      if (previous == null || start != previous.predecessor) {
         while(!current.left.isEmpty() && previous != current.left) {
            if (isValidUplink(current.left, current)) {
               return current.left;
            }

            current = current.left;
         }
      }

      if (current.isEmpty()) {
         return null;
      } else if (current.right == null) {
         return null;
      } else if (previous != current.right) {
         return isValidUplink(current.right, current) ? current.right : this.nextEntryImpl(current.right, previous, tree);
      } else {
         while(current == current.parent.right) {
            if (current == tree) {
               return null;
            }

            current = current.parent;
         }

         if (current == tree) {
            return null;
         } else if (current.parent.right == null) {
            return null;
         } else if (previous != current.parent.right && isValidUplink(current.parent.right, current.parent)) {
            return current.parent.right;
         } else if (current.parent.right == current.parent) {
            return null;
         } else {
            return this.nextEntryImpl(current.parent.right, previous, tree);
         }
      }
   }

   TrieEntry<K, V> nextEntryInSubtree(TrieEntry<K, V> node, TrieEntry<K, V> parentOfSubtree) {
      return node == null ? this.firstEntry() : this.nextEntryImpl(node.predecessor, node, parentOfSubtree);
   }

   public K nextKey(K key) {
      Objects.requireNonNull(key, "key");
      TrieEntry<K, V> entry = this.getEntry(key);
      if (entry != null) {
         TrieEntry<K, V> nextEntry = this.nextEntry(entry);
         return (K)(nextEntry != null ? nextEntry.getKey() : null);
      } else {
         return null;
      }
   }

   public SortedMap<K, V> prefixMap(K key) {
      return this.getPrefixMapByBits(key, 0, this.lengthInBits(key));
   }

   TrieEntry<K, V> previousEntry(TrieEntry<K, V> start) {
      if (start.predecessor == null) {
         throw new IllegalArgumentException("must have come from somewhere!");
      } else if (start.predecessor.right == start) {
         return isValidUplink(start.predecessor.left, start.predecessor) ? start.predecessor.left : this.followRight(start.predecessor.left);
      } else {
         TrieEntry<K, V> node;
         for(node = start.predecessor; node.parent != null && node == node.parent.left; node = node.parent) {
         }

         if (node.parent == null) {
            return null;
         } else if (isValidUplink(node.parent.left, node.parent)) {
            if (node.parent.left == this.root) {
               return this.root.isEmpty() ? null : this.root;
            } else {
               return node.parent.left;
            }
         } else {
            return this.followRight(node.parent.left);
         }
      }
   }

   public K previousKey(K key) {
      Objects.requireNonNull(key, "key");
      TrieEntry<K, V> entry = this.getEntry(key);
      if (entry != null) {
         TrieEntry<K, V> prevEntry = this.previousEntry(entry);
         return (K)(prevEntry != null ? prevEntry.getKey() : null);
      } else {
         return null;
      }
   }

   public V put(K key, V value) {
      Objects.requireNonNull(key, "key");
      int lengthInBits = this.lengthInBits(key);
      if (lengthInBits == 0) {
         if (this.root.isEmpty()) {
            this.incrementSize();
         } else {
            this.incrementModCount();
         }

         return (V)this.root.setKeyValue(key, value);
      } else {
         TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
         if (this.compareKeys(key, found.key)) {
            if (found.isEmpty()) {
               this.incrementSize();
            } else {
               this.incrementModCount();
            }

            return (V)found.setKeyValue(key, value);
         } else {
            int bitIndex = this.bitIndex(key, found.key);
            if (!KeyAnalyzer.isOutOfBoundsIndex(bitIndex)) {
               if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                  TrieEntry<K, V> t = new TrieEntry<K, V>(key, value, bitIndex);
                  this.addEntry(t, lengthInBits);
                  this.incrementSize();
                  return null;
               }

               if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                  if (this.root.isEmpty()) {
                     this.incrementSize();
                  } else {
                     this.incrementModCount();
                  }

                  return (V)this.root.setKeyValue(key, value);
               }

               if (KeyAnalyzer.isEqualBitKey(bitIndex) && found != this.root) {
                  this.incrementModCount();
                  return (V)found.setKeyValue(key, value);
               }
            }

            throw new IllegalArgumentException("Failed to put: " + key + " -> " + value + ", " + bitIndex);
         }
      }
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.root = new TrieEntry<K, V>((Object)null, (Object)null, -1);
      int size = in.readInt();

      for(int i = 0; i < size; ++i) {
         K k = (K)in.readObject();
         V v = (V)in.readObject();
         this.put(k, v);
      }

   }

   public V remove(Object k) {
      if (k == null) {
         return null;
      } else {
         K key = (K)this.castKey(k);
         int lengthInBits = this.lengthInBits(key);
         TrieEntry<K, V> current = this.root.left;
         TrieEntry<K, V> path = this.root;

         while(current.bitIndex > path.bitIndex) {
            path = current;
            if (!this.isBitSet(key, current.bitIndex, lengthInBits)) {
               current = current.left;
            } else {
               current = current.right;
            }
         }

         if (!current.isEmpty() && this.compareKeys(key, current.key)) {
            return (V)this.removeEntry(current);
         } else {
            return null;
         }
      }
   }

   V removeEntry(TrieEntry<K, V> h) {
      if (h != this.root) {
         if (h.isInternalNode()) {
            this.removeInternalEntry(h);
         } else {
            this.removeExternalEntry(h);
         }
      }

      this.decrementSize();
      return (V)h.setKeyValue((Object)null, (Object)null);
   }

   private void removeExternalEntry(TrieEntry<K, V> h) {
      if (h == this.root) {
         throw new IllegalArgumentException("Cannot delete root Entry!");
      } else if (!h.isExternalNode()) {
         throw new IllegalArgumentException(h + " is not an external Entry!");
      } else {
         TrieEntry<K, V> parent = h.parent;
         TrieEntry<K, V> child = h.left == h ? h.right : h.left;
         if (parent.left == h) {
            parent.left = child;
         } else {
            parent.right = child;
         }

         if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
         } else {
            child.predecessor = parent;
         }

      }
   }

   private void removeInternalEntry(TrieEntry<K, V> h) {
      if (h == this.root) {
         throw new IllegalArgumentException("Cannot delete root Entry!");
      } else if (!h.isInternalNode()) {
         throw new IllegalArgumentException(h + " is not an internal Entry!");
      } else {
         TrieEntry<K, V> p = h.predecessor;
         p.bitIndex = h.bitIndex;
         TrieEntry<K, V> parent = p.parent;
         TrieEntry<K, V> child = p.left == h ? p.right : p.left;
         if (p.predecessor == p && p.parent != h) {
            p.predecessor = p.parent;
         }

         if (parent.left == p) {
            parent.left = child;
         } else {
            parent.right = child;
         }

         if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
         }

         if (h.left.parent == h) {
            h.left.parent = p;
         }

         if (h.right.parent == h) {
            h.right.parent = p;
         }

         if (h.parent.left == h) {
            h.parent.left = p;
         } else {
            h.parent.right = p;
         }

         p.parent = h.parent;
         p.left = h.left;
         p.right = h.right;
         if (isValidUplink(p.left, p)) {
            p.left.predecessor = p;
         }

         if (isValidUplink(p.right, p)) {
            p.right.predecessor = p;
         }

      }
   }

   public Map.Entry<K, V> select(K key) {
      int lengthInBits = this.lengthInBits(key);
      Reference<Map.Entry<K, V>> reference = new Reference<Map.Entry<K, V>>();
      return !this.selectR(this.root.left, -1, key, lengthInBits, reference) ? (Map.Entry)reference.get() : null;
   }

   public K selectKey(K key) {
      Map.Entry<K, V> entry = this.select(key);
      return (K)(entry == null ? null : entry.getKey());
   }

   private boolean selectR(TrieEntry<K, V> h, int bitIndex, K key, int lengthInBits, Reference<Map.Entry<K, V>> reference) {
      if (h.bitIndex <= bitIndex) {
         if (!h.isEmpty()) {
            reference.set(h);
            return false;
         } else {
            return true;
         }
      } else {
         if (!this.isBitSet(key, h.bitIndex, lengthInBits)) {
            if (this.selectR(h.left, h.bitIndex, key, lengthInBits, reference)) {
               return this.selectR(h.right, h.bitIndex, key, lengthInBits, reference);
            }
         } else if (this.selectR(h.right, h.bitIndex, key, lengthInBits, reference)) {
            return this.selectR(h.left, h.bitIndex, key, lengthInBits, reference);
         }

         return false;
      }
   }

   public V selectValue(K key) {
      Map.Entry<K, V> entry = this.select(key);
      return (V)(entry == null ? null : entry.getValue());
   }

   public int size() {
      return this.size;
   }

   public SortedMap<K, V> subMap(K fromKey, K toKey) {
      return new RangeEntryMap(fromKey, toKey);
   }

   TrieEntry<K, V> subtree(K prefix, int offsetInBits, int lengthInBits) {
      TrieEntry<K, V> current = this.root.left;
      TrieEntry<K, V> path = this.root;

      while(current.bitIndex > path.bitIndex && lengthInBits > current.bitIndex) {
         path = current;
         if (!this.isBitSet(prefix, offsetInBits + current.bitIndex, offsetInBits + lengthInBits)) {
            current = current.left;
         } else {
            current = current.right;
         }
      }

      TrieEntry<K, V> entry = current.isEmpty() ? path : current;
      if (entry.isEmpty()) {
         return null;
      } else {
         int endIndexInBits = offsetInBits + lengthInBits;
         if (entry == this.root && this.lengthInBits(entry.getKey()) < endIndexInBits) {
            return null;
         } else if (this.isBitSet(prefix, endIndexInBits - 1, endIndexInBits) != this.isBitSet(entry.key, lengthInBits - 1, this.lengthInBits(entry.key))) {
            return null;
         } else {
            int bitIndex = this.getKeyAnalyzer().bitIndex(prefix, offsetInBits, lengthInBits, entry.key, 0, this.lengthInBits(entry.getKey()));
            return bitIndex >= 0 && bitIndex < lengthInBits ? null : entry;
         }
      }
   }

   public SortedMap<K, V> tailMap(K fromKey) {
      return new RangeEntryMap(fromKey, (Object)null);
   }

   public Collection<V> values() {
      if (this.values == null) {
         this.values = new Values();
      }

      return this.values;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeInt(this.size());

      for(Map.Entry<K, V> entry : this.entrySet()) {
         out.writeObject(entry.getKey());
         out.writeObject(entry.getValue());
      }

   }

   private abstract class AbstractRangeMap extends AbstractMap<K, V> implements SortedMap<K, V> {
      private transient volatile Set<Map.Entry<K, V>> entrySet;

      private AbstractRangeMap() {
      }

      public Comparator<? super K> comparator() {
         return AbstractPatriciaTrie.this.comparator();
      }

      public boolean containsKey(Object key) {
         return !this.inRange(AbstractPatriciaTrie.this.castKey(key)) ? false : AbstractPatriciaTrie.this.containsKey(key);
      }

      protected abstract Set<Map.Entry<K, V>> createEntrySet();

      protected abstract SortedMap<K, V> createRangeMap(K var1, boolean var2, K var3, boolean var4);

      public Set<Map.Entry<K, V>> entrySet() {
         if (this.entrySet == null) {
            this.entrySet = this.createEntrySet();
         }

         return this.entrySet;
      }

      public V get(Object key) {
         return (V)(!this.inRange(AbstractPatriciaTrie.this.castKey(key)) ? null : AbstractPatriciaTrie.this.get(key));
      }

      protected abstract K getFromKey();

      protected abstract K getToKey();

      public SortedMap<K, V> headMap(K toKey) {
         if (!this.inRange2(toKey)) {
            throw new IllegalArgumentException("ToKey is out of range: " + toKey);
         } else {
            return this.createRangeMap(this.getFromKey(), this.isFromInclusive(), toKey, this.isToInclusive());
         }
      }

      protected boolean inFromRange(K key, boolean forceInclusive) {
         K fromKey = (K)this.getFromKey();
         boolean fromInclusive = this.isFromInclusive();
         int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, fromKey);
         if (!fromInclusive && !forceInclusive) {
            return ret > 0;
         } else {
            return ret >= 0;
         }
      }

      protected boolean inRange(K key) {
         K fromKey = (K)this.getFromKey();
         K toKey = (K)this.getToKey();
         return (fromKey == null || this.inFromRange(key, false)) && (toKey == null || this.inToRange(key, false));
      }

      protected boolean inRange2(K key) {
         K fromKey = (K)this.getFromKey();
         K toKey = (K)this.getToKey();
         return (fromKey == null || this.inFromRange(key, false)) && (toKey == null || this.inToRange(key, true));
      }

      protected boolean inToRange(K key, boolean forceInclusive) {
         K toKey = (K)this.getToKey();
         boolean toInclusive = this.isToInclusive();
         int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, toKey);
         if (!toInclusive && !forceInclusive) {
            return ret < 0;
         } else {
            return ret <= 0;
         }
      }

      protected abstract boolean isFromInclusive();

      protected abstract boolean isToInclusive();

      public V put(K key, V value) {
         if (!this.inRange(key)) {
            throw new IllegalArgumentException("Key is out of range: " + key);
         } else {
            return (V)AbstractPatriciaTrie.this.put(key, value);
         }
      }

      public V remove(Object key) {
         return (V)(!this.inRange(AbstractPatriciaTrie.this.castKey(key)) ? null : AbstractPatriciaTrie.this.remove(key));
      }

      public SortedMap<K, V> subMap(K fromKey, K toKey) {
         if (!this.inRange2(fromKey)) {
            throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
         } else if (!this.inRange2(toKey)) {
            throw new IllegalArgumentException("ToKey is out of range: " + toKey);
         } else {
            return this.createRangeMap(fromKey, this.isFromInclusive(), toKey, this.isToInclusive());
         }
      }

      public SortedMap<K, V> tailMap(K fromKey) {
         if (!this.inRange2(fromKey)) {
            throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
         } else {
            return this.createRangeMap(fromKey, this.isFromInclusive(), this.getToKey(), this.isToInclusive());
         }
      }
   }

   abstract class AbstractTrieIterator<E> implements Iterator<E> {
      protected int expectedModCount;
      protected TrieEntry<K, V> next;
      protected TrieEntry<K, V> current;

      protected AbstractTrieIterator() {
         this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         this.next = AbstractPatriciaTrie.this.nextEntry((TrieEntry)null);
      }

      protected AbstractTrieIterator(final TrieEntry<K, V> firstEntry) {
         this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         this.next = firstEntry;
      }

      protected TrieEntry<K, V> findNext(TrieEntry<K, V> prior) {
         return AbstractPatriciaTrie.this.nextEntry(prior);
      }

      public boolean hasNext() {
         return this.next != null;
      }

      protected TrieEntry<K, V> nextEntry() {
         if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            TrieEntry<K, V> e = this.next;
            if (e == null) {
               throw new NoSuchElementException();
            } else {
               this.next = this.findNext(e);
               this.current = e;
               return e;
            }
         }
      }

      public void remove() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            TrieEntry<K, V> node = this.current;
            this.current = null;
            AbstractPatriciaTrie.this.removeEntry(node);
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         }
      }
   }

   private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      private EntrySet() {
      }

      public void clear() {
         AbstractPatriciaTrie.this.clear();
      }

      public boolean contains(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            TrieEntry<K, V> candidate = AbstractPatriciaTrie.this.getEntry(((Map.Entry)o).getKey());
            return candidate != null && candidate.equals(o);
         }
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return new EntryIterator();
      }

      public boolean remove(Object obj) {
         if (!(obj instanceof Map.Entry)) {
            return false;
         } else if (!this.contains(obj)) {
            return false;
         } else {
            Map.Entry<?, ?> entry = (Map.Entry)obj;
            AbstractPatriciaTrie.this.remove(entry.getKey());
            return true;
         }
      }

      public int size() {
         return AbstractPatriciaTrie.this.size();
      }

      private final class EntryIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<Map.Entry<K, V>> {
         private EntryIterator() {
         }

         public Map.Entry<K, V> next() {
            return this.nextEntry();
         }
      }
   }

   private final class KeySet extends AbstractSet<K> {
      private KeySet() {
      }

      public void clear() {
         AbstractPatriciaTrie.this.clear();
      }

      public boolean contains(Object o) {
         return AbstractPatriciaTrie.this.containsKey(o);
      }

      public Iterator<K> iterator() {
         return new KeyIterator();
      }

      public boolean remove(Object o) {
         int size = this.size();
         AbstractPatriciaTrie.this.remove(o);
         return size != this.size();
      }

      public int size() {
         return AbstractPatriciaTrie.this.size();
      }

      private final class KeyIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<K> {
         private KeyIterator() {
         }

         public K next() {
            return (K)this.nextEntry().getKey();
         }
      }
   }

   private final class PrefixRangeEntrySet extends AbstractPatriciaTrie<K, V>.RangeEntrySet {
      private final AbstractPatriciaTrie<K, V>.PrefixRangeMap delegate;
      private TrieEntry<K, V> prefixStart;
      private int expectedModCount;

      PrefixRangeEntrySet(final AbstractPatriciaTrie<K, V>.PrefixRangeMap delegate) {
         super(delegate);
         this.delegate = delegate;
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         if (AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
            this.prefixStart = AbstractPatriciaTrie.this.subtree(this.delegate.prefix, this.delegate.offsetInBits, this.delegate.lengthInBits);
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         }

         if (this.prefixStart == null) {
            Set<Map.Entry<K, V>> empty = Collections.emptySet();
            return empty.iterator();
         } else {
            return (Iterator<Map.Entry<K, V>>)(this.delegate.lengthInBits > this.prefixStart.bitIndex ? new SingletonIterator(this.prefixStart) : new EntryIterator(this.prefixStart, this.delegate.prefix, this.delegate.offsetInBits, this.delegate.lengthInBits));
         }
      }

      public int size() {
         return this.delegate.fixup();
      }

      private final class EntryIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<Map.Entry<K, V>> {
         private final K prefix;
         private final int offset;
         private final int lengthInBits;
         private boolean lastOne;
         private TrieEntry<K, V> subtree;

         EntryIterator(final TrieEntry<K, V> startScan, final K prefix, final int offset, final int lengthInBits) {
            this.subtree = startScan;
            this.next = AbstractPatriciaTrie.this.followLeft(startScan);
            this.prefix = prefix;
            this.offset = offset;
            this.lengthInBits = lengthInBits;
         }

         protected TrieEntry<K, V> findNext(TrieEntry<K, V> prior) {
            return AbstractPatriciaTrie.this.nextEntryInSubtree(prior, this.subtree);
         }

         public Map.Entry<K, V> next() {
            Map.Entry<K, V> entry = this.nextEntry();
            if (this.lastOne) {
               this.next = null;
            }

            return entry;
         }

         public void remove() {
            boolean needsFixing = false;
            int bitIdx = this.subtree.bitIndex;
            if (this.current == this.subtree) {
               needsFixing = true;
            }

            super.remove();
            if (bitIdx != this.subtree.bitIndex || needsFixing) {
               this.subtree = AbstractPatriciaTrie.this.subtree(this.prefix, this.offset, this.lengthInBits);
            }

            if (this.lengthInBits >= this.subtree.bitIndex) {
               this.lastOne = true;
            }

         }
      }

      private final class SingletonIterator implements Iterator<Map.Entry<K, V>> {
         private final TrieEntry<K, V> entry;
         private int hit;

         SingletonIterator(final TrieEntry<K, V> entry) {
            this.entry = entry;
         }

         public boolean hasNext() {
            return this.hit == 0;
         }

         public Map.Entry<K, V> next() {
            if (this.hit != 0) {
               throw new NoSuchElementException();
            } else {
               ++this.hit;
               return this.entry;
            }
         }

         public void remove() {
            if (this.hit != 1) {
               throw new IllegalStateException();
            } else {
               ++this.hit;
               AbstractPatriciaTrie.this.removeEntry(this.entry);
            }
         }
      }
   }

   private final class PrefixRangeMap extends AbstractPatriciaTrie<K, V>.AbstractRangeMap {
      private final K prefix;
      private final int offsetInBits;
      private final int lengthInBits;
      private K fromKey;
      private K toKey;
      private transient int expectedModCount;
      private int size;

      private PrefixRangeMap(final K prefix, final int offsetInBits, final int lengthInBits) {
         this.size = -1;
         this.prefix = prefix;
         this.offsetInBits = offsetInBits;
         this.lengthInBits = lengthInBits;
      }

      public void clear() {
         Iterator<Map.Entry<K, V>> it = AbstractPatriciaTrie.this.entrySet().iterator();
         Set<K> currentKeys = this.keySet();

         while(it.hasNext()) {
            if (currentKeys.contains(((Map.Entry)it.next()).getKey())) {
               it.remove();
            }
         }

      }

      protected Set<Map.Entry<K, V>> createEntrySet() {
         return AbstractPatriciaTrie.this.new PrefixRangeEntrySet(this);
      }

      protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
         return AbstractPatriciaTrie.this.new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
      }

      public K firstKey() {
         this.fixup();
         Map.Entry<K, V> e = null;
         if (this.fromKey == null) {
            e = AbstractPatriciaTrie.this.firstEntry();
         } else {
            e = AbstractPatriciaTrie.this.higherEntry(this.fromKey);
         }

         K first = (K)(e != null ? e.getKey() : null);
         if (e != null && AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, first)) {
            return first;
         } else {
            throw new NoSuchElementException();
         }
      }

      private int fixup() {
         if (this.size == -1 || AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
            Iterator<Map.Entry<K, V>> it = super.entrySet().iterator();
            this.size = 0;
            Map.Entry<K, V> entry = null;
            if (it.hasNext()) {
               entry = (Map.Entry)it.next();
               this.size = 1;
            }

            this.fromKey = (K)(entry == null ? null : entry.getKey());
            if (this.fromKey != null) {
               TrieEntry<K, V> prior = AbstractPatriciaTrie.this.previousEntry((TrieEntry)entry);
               this.fromKey = (K)(prior == null ? null : prior.getKey());
            }

            for(this.toKey = this.fromKey; it.hasNext(); entry = (Map.Entry)it.next()) {
               ++this.size;
            }

            this.toKey = (K)(entry == null ? null : entry.getKey());
            if (this.toKey != null) {
               entry = AbstractPatriciaTrie.this.nextEntry((TrieEntry)entry);
               this.toKey = (K)(entry == null ? null : entry.getKey());
            }

            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         }

         return this.size;
      }

      public K getFromKey() {
         return this.fromKey;
      }

      public K getToKey() {
         return this.toKey;
      }

      protected boolean inFromRange(K key, boolean forceInclusive) {
         return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
      }

      protected boolean inRange(K key) {
         return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
      }

      protected boolean inRange2(K key) {
         return this.inRange(key);
      }

      protected boolean inToRange(K key, boolean forceInclusive) {
         return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
      }

      public boolean isFromInclusive() {
         return false;
      }

      public boolean isToInclusive() {
         return false;
      }

      public K lastKey() {
         this.fixup();
         Map.Entry<K, V> e = null;
         if (this.toKey == null) {
            e = AbstractPatriciaTrie.this.lastEntry();
         } else {
            e = AbstractPatriciaTrie.this.lowerEntry(this.toKey);
         }

         K last = (K)(e != null ? e.getKey() : null);
         if (e != null && AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, last)) {
            return last;
         } else {
            throw new NoSuchElementException();
         }
      }
   }

   private final class RangeEntryMap extends AbstractPatriciaTrie<K, V>.AbstractRangeMap {
      private final K fromKey;
      private final K toKey;
      private final boolean fromInclusive;
      private final boolean toInclusive;

      protected RangeEntryMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
         if (fromKey == null && toKey == null) {
            throw new IllegalArgumentException("must have a from or to!");
         } else if (fromKey != null && toKey != null && AbstractPatriciaTrie.this.getKeyAnalyzer().compare(fromKey, toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
         } else {
            this.fromKey = fromKey;
            this.fromInclusive = fromInclusive;
            this.toKey = toKey;
            this.toInclusive = toInclusive;
         }
      }

      protected RangeEntryMap(final K fromKey, final K toKey) {
         this(fromKey, true, toKey, false);
      }

      protected Set<Map.Entry<K, V>> createEntrySet() {
         return AbstractPatriciaTrie.this.new RangeEntrySet(this);
      }

      protected SortedMap<K, V> createRangeMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
         return AbstractPatriciaTrie.this.new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
      }

      public K firstKey() {
         Map.Entry<K, V> e = null;
         if (this.fromKey == null) {
            e = AbstractPatriciaTrie.this.firstEntry();
         } else if (this.fromInclusive) {
            e = AbstractPatriciaTrie.this.ceilingEntry(this.fromKey);
         } else {
            e = AbstractPatriciaTrie.this.higherEntry(this.fromKey);
         }

         K first = (K)(e != null ? e.getKey() : null);
         if (e != null && (this.toKey == null || this.inToRange(first, false))) {
            return first;
         } else {
            throw new NoSuchElementException();
         }
      }

      public K getFromKey() {
         return this.fromKey;
      }

      public K getToKey() {
         return this.toKey;
      }

      public boolean isFromInclusive() {
         return this.fromInclusive;
      }

      public boolean isToInclusive() {
         return this.toInclusive;
      }

      public K lastKey() {
         Map.Entry<K, V> e;
         if (this.toKey == null) {
            e = AbstractPatriciaTrie.this.lastEntry();
         } else if (this.toInclusive) {
            e = AbstractPatriciaTrie.this.floorEntry(this.toKey);
         } else {
            e = AbstractPatriciaTrie.this.lowerEntry(this.toKey);
         }

         K last = (K)(e != null ? e.getKey() : null);
         if (e != null && (this.fromKey == null || this.inFromRange(last, false))) {
            return last;
         } else {
            throw new NoSuchElementException();
         }
      }
   }

   private class RangeEntrySet extends AbstractSet<Map.Entry<K, V>> {
      private final AbstractPatriciaTrie<K, V>.AbstractRangeMap delegate;
      private transient int size = -1;
      private transient int expectedModCount;

      RangeEntrySet(final AbstractPatriciaTrie<K, V>.AbstractRangeMap delegate) {
         this.delegate = (AbstractRangeMap)Objects.requireNonNull(delegate, "delegate");
      }

      public boolean contains(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<K, V> entry = (Map.Entry)o;
            K key = (K)entry.getKey();
            if (!this.delegate.inRange(key)) {
               return false;
            } else {
               TrieEntry<K, V> node = AbstractPatriciaTrie.this.getEntry(key);
               return node != null && AbstractBitwiseTrie.compare(node.getValue(), entry.getValue());
            }
         }
      }

      public boolean isEmpty() {
         return !this.iterator().hasNext();
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         K fromKey = (K)this.delegate.getFromKey();
         K toKey = (K)this.delegate.getToKey();
         TrieEntry<K, V> first = null;
         if (fromKey == null) {
            first = AbstractPatriciaTrie.this.firstEntry();
         } else {
            first = AbstractPatriciaTrie.this.ceilingEntry(fromKey);
         }

         TrieEntry<K, V> last = null;
         if (toKey != null) {
            last = AbstractPatriciaTrie.this.ceilingEntry(toKey);
         }

         return new EntryIterator(first, last);
      }

      public boolean remove(Object o) {
         if (!(o instanceof Map.Entry)) {
            return false;
         } else {
            Map.Entry<K, V> entry = (Map.Entry)o;
            K key = (K)entry.getKey();
            if (!this.delegate.inRange(key)) {
               return false;
            } else {
               TrieEntry<K, V> node = AbstractPatriciaTrie.this.getEntry(key);
               if (node != null && AbstractBitwiseTrie.compare(node.getValue(), entry.getValue())) {
                  AbstractPatriciaTrie.this.removeEntry(node);
                  return true;
               } else {
                  return false;
               }
            }
         }
      }

      public int size() {
         if (this.size == -1 || this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
            this.size = 0;
            Iterator<?> it = this.iterator();

            while(it.hasNext()) {
               ++this.size;
               it.next();
            }

            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
         }

         return this.size;
      }

      private final class EntryIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<Map.Entry<K, V>> {
         private final K excludedKey;

         private EntryIterator(final TrieEntry<K, V> first, final TrieEntry<K, V> last) {
            super(first);
            this.excludedKey = (K)(last != null ? last.getKey() : null);
         }

         public boolean hasNext() {
            return this.next != null && !AbstractBitwiseTrie.compare(this.next.key, this.excludedKey);
         }

         public Map.Entry<K, V> next() {
            if (this.next != null && !AbstractBitwiseTrie.compare(this.next.key, this.excludedKey)) {
               return this.nextEntry();
            } else {
               throw new NoSuchElementException();
            }
         }
      }
   }

   private static final class Reference<E> {
      private E item;

      private Reference() {
      }

      public E get() {
         return this.item;
      }

      public void set(E item) {
         this.item = item;
      }
   }

   protected static class TrieEntry<K, V> extends AbstractBitwiseTrie.BasicEntry<K, V> {
      private static final long serialVersionUID = 4596023148184140013L;
      protected int bitIndex;
      protected TrieEntry<K, V> parent;
      protected TrieEntry<K, V> left;
      protected TrieEntry<K, V> right;
      protected TrieEntry<K, V> predecessor;

      public TrieEntry(K key, V value, int bitIndex) {
         super(key, value);
         this.bitIndex = bitIndex;
         this.parent = null;
         this.left = this;
         this.right = null;
         this.predecessor = this;
      }

      public boolean isEmpty() {
         return this.key == null;
      }

      public boolean isExternalNode() {
         return !this.isInternalNode();
      }

      public boolean isInternalNode() {
         return this.left != this && this.right != this;
      }

      public String toString() {
         StringBuilder buffer = new StringBuilder();
         if (this.bitIndex == -1) {
            buffer.append("RootEntry(");
         } else {
            buffer.append("Entry(");
         }

         buffer.append("key=").append(this.getKey()).append(" [").append(this.bitIndex).append("], ");
         buffer.append("value=").append(this.getValue()).append(", ");
         if (this.parent != null) {
            if (this.parent.bitIndex == -1) {
               buffer.append("parent=").append("ROOT");
            } else {
               buffer.append("parent=").append(this.parent.getKey()).append(" [").append(this.parent.bitIndex).append("]");
            }
         } else {
            buffer.append("parent=").append("null");
         }

         buffer.append(", ");
         if (this.left != null) {
            if (this.left.bitIndex == -1) {
               buffer.append("left=").append("ROOT");
            } else {
               buffer.append("left=").append(this.left.getKey()).append(" [").append(this.left.bitIndex).append("]");
            }
         } else {
            buffer.append("left=").append("null");
         }

         buffer.append(", ");
         if (this.right != null) {
            if (this.right.bitIndex == -1) {
               buffer.append("right=").append("ROOT");
            } else {
               buffer.append("right=").append(this.right.getKey()).append(" [").append(this.right.bitIndex).append("]");
            }
         } else {
            buffer.append("right=").append("null");
         }

         buffer.append(", ");
         if (this.predecessor != null) {
            if (this.predecessor.bitIndex == -1) {
               buffer.append("predecessor=").append("ROOT");
            } else {
               buffer.append("predecessor=").append(this.predecessor.getKey()).append(" [").append(this.predecessor.bitIndex).append("]");
            }
         }

         buffer.append(")");
         return buffer.toString();
      }
   }

   private final class TrieMapIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<K> implements OrderedMapIterator<K, V> {
      protected TrieEntry<K, V> previous;

      private TrieMapIterator() {
      }

      public K getKey() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (K)this.current.getKey();
         }
      }

      public V getValue() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (V)this.current.getValue();
         }
      }

      public boolean hasPrevious() {
         return this.previous != null;
      }

      public K next() {
         return (K)this.nextEntry().getKey();
      }

      protected TrieEntry<K, V> nextEntry() {
         TrieEntry<K, V> nextEntry = super.nextEntry();
         this.previous = nextEntry;
         return nextEntry;
      }

      public K previous() {
         return (K)this.previousEntry().getKey();
      }

      protected TrieEntry<K, V> previousEntry() {
         if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
            throw new ConcurrentModificationException();
         } else {
            TrieEntry<K, V> e = this.previous;
            if (e == null) {
               throw new NoSuchElementException();
            } else {
               this.previous = AbstractPatriciaTrie.this.previousEntry(e);
               this.next = this.current;
               this.current = e;
               return this.current;
            }
         }
      }

      public V setValue(V value) {
         if (this.current == null) {
            throw new IllegalStateException();
         } else {
            return (V)this.current.setValue(value);
         }
      }
   }

   private final class Values extends AbstractCollection<V> {
      private Values() {
      }

      public void clear() {
         AbstractPatriciaTrie.this.clear();
      }

      public boolean contains(Object o) {
         return AbstractPatriciaTrie.this.containsValue(o);
      }

      public Iterator<V> iterator() {
         return new ValueIterator();
      }

      public boolean remove(Object o) {
         Iterator<V> it = this.iterator();

         while(it.hasNext()) {
            V value = (V)it.next();
            if (AbstractBitwiseTrie.compare(value, o)) {
               it.remove();
               return true;
            }
         }

         return false;
      }

      public int size() {
         return AbstractPatriciaTrie.this.size();
      }

      private final class ValueIterator extends AbstractPatriciaTrie<K, V>.AbstractTrieIterator<V> {
         private ValueIterator() {
         }

         public V next() {
            return (V)this.nextEntry().getValue();
         }
      }
   }
}
