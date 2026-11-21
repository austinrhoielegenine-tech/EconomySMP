package com.fasterxml.jackson.core.sym;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.core.exc.StreamConstraintsException;
import com.fasterxml.jackson.core.util.InternCache;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicReference;

public final class CharsToNameCanonicalizer {
   public static final int HASH_MULT = 33;
   private static final int DEFAULT_T_SIZE = 64;
   private static final int MAX_T_SIZE = 65536;
   static final int MAX_ENTRIES_FOR_REUSE = 12000;
   static final int MAX_COLL_CHAIN_LENGTH = 150;
   protected final CharsToNameCanonicalizer _parent;
   protected final AtomicReference<TableInfo> _tableInfo;
   protected final StreamReadConstraints _streamReadConstraints;
   protected final int _seed;
   protected final int _factoryFeatures;
   protected boolean _canonicalize;
   protected String[] _symbols;
   protected Bucket[] _buckets;
   protected int _size;
   protected int _sizeThreshold;
   protected int _indexMask;
   protected int _longestCollisionList;
   protected boolean _hashShared;
   protected BitSet _overflows;

   private CharsToNameCanonicalizer(StreamReadConstraints src, int factoryFeatures, int seed) {
      this._parent = null;
      this._seed = seed;
      this._streamReadConstraints = src;
      this._canonicalize = true;
      this._factoryFeatures = factoryFeatures;
      this._hashShared = false;
      this._longestCollisionList = 0;
      this._tableInfo = new AtomicReference(CharsToNameCanonicalizer.TableInfo.createInitial(64));
   }

   private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, StreamReadConstraints src, int factoryFeatures, int seed, TableInfo parentState) {
      this._parent = parent;
      this._streamReadConstraints = src;
      this._seed = seed;
      this._tableInfo = null;
      this._factoryFeatures = factoryFeatures;
      this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(factoryFeatures);
      this._symbols = parentState.symbols;
      this._buckets = parentState.buckets;
      this._size = parentState.size;
      this._longestCollisionList = parentState.longestCollisionList;
      int arrayLen = this._symbols.length;
      this._sizeThreshold = _thresholdSize(arrayLen);
      this._indexMask = arrayLen - 1;
      this._hashShared = true;
   }

   private static int _thresholdSize(int hashAreaSize) {
      return hashAreaSize - (hashAreaSize >> 2);
   }

   /** @deprecated */
   @Deprecated
   public static CharsToNameCanonicalizer createRoot() {
      return createRoot((TokenStreamFactory)null);
   }

   /** @deprecated */
   @Deprecated
   public static CharsToNameCanonicalizer createRoot(int seed) {
      return createRoot((TokenStreamFactory)null, seed);
   }

   public static CharsToNameCanonicalizer createRoot(TokenStreamFactory owner) {
      return createRoot(owner, 0);
   }

   public static CharsToNameCanonicalizer createRoot(TokenStreamFactory owner, int seed) {
      if (seed == 0) {
         seed = System.identityHashCode(owner);
      }

      StreamReadConstraints src;
      int factoryFeatures;
      if (owner == null) {
         src = StreamReadConstraints.defaults();
         factoryFeatures = 0;
      } else {
         src = owner.streamReadConstraints();
         factoryFeatures = owner.getFactoryFeatures();
      }

      return new CharsToNameCanonicalizer(src, factoryFeatures, seed);
   }

   public CharsToNameCanonicalizer makeChild() {
      return new CharsToNameCanonicalizer(this, this._streamReadConstraints, this._factoryFeatures, this._seed, (TableInfo)this._tableInfo.get());
   }

   /** @deprecated */
   @Deprecated
   public CharsToNameCanonicalizer makeChild(int flags) {
      return this.makeChild();
   }

   public void release() {
      if (this.maybeDirty()) {
         if (this._parent != null && this._canonicalize) {
            this._parent.mergeChild(new TableInfo(this));
            this._hashShared = true;
         }

      }
   }

   private void mergeChild(TableInfo childState) {
      int childCount = childState.size;
      TableInfo currState = (TableInfo)this._tableInfo.get();
      if (childCount != currState.size) {
         if (childCount > 12000) {
            childState = CharsToNameCanonicalizer.TableInfo.createInitial(64);
         }

         this._tableInfo.compareAndSet(currState, childState);
      }
   }

   public int size() {
      return this._tableInfo != null ? ((TableInfo)this._tableInfo.get()).size : this._size;
   }

   public int bucketCount() {
      return this._symbols.length;
   }

   public boolean maybeDirty() {
      return !this._hashShared;
   }

   public int hashSeed() {
      return this._seed;
   }

   public int collisionCount() {
      int count = 0;

      for(Bucket bucket : this._buckets) {
         if (bucket != null) {
            count += bucket.length;
         }
      }

      return count;
   }

   public int maxCollisionLength() {
      return this._longestCollisionList;
   }

   public String findSymbol(char[] buffer, int start, int len, int h) throws IOException {
      if (len < 1) {
         return "";
      } else if (!this._canonicalize) {
         this._streamReadConstraints.validateNameLength(len);
         return new String(buffer, start, len);
      } else {
         int index = this._hashToIndex(h);
         String sym = this._symbols[index];
         if (sym != null) {
            if (sym.length() == len) {
               int i = 0;

               while(sym.charAt(i) == buffer[start + i]) {
                  ++i;
                  if (i == len) {
                     return sym;
                  }
               }
            }

            Bucket b = this._buckets[index >> 1];
            if (b != null) {
               sym = b.has(buffer, start, len);
               if (sym != null) {
                  return sym;
               }

               sym = this._findSymbol2(buffer, start, len, b.next);
               if (sym != null) {
                  return sym;
               }
            }
         }

         this._streamReadConstraints.validateNameLength(len);
         return this._addSymbol(buffer, start, len, h, index);
      }
   }

   private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
      while(b != null) {
         String sym = b.has(buffer, start, len);
         if (sym != null) {
            return sym;
         }

         b = b.next;
      }

      return null;
   }

   private String _addSymbol(char[] buffer, int start, int len, int h, int index) throws IOException {
      if (this._hashShared) {
         this.copyArrays();
         this._hashShared = false;
      } else if (this._size >= this._sizeThreshold) {
         this.rehash();
         index = this._hashToIndex(this.calcHash(buffer, start, len));
      }

      String newSymbol = new String(buffer, start, len);
      if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._factoryFeatures)) {
         newSymbol = InternCache.instance.intern(newSymbol);
      }

      ++this._size;
      if (this._symbols[index] == null) {
         this._symbols[index] = newSymbol;
      } else {
         int bix = index >> 1;
         Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
         int collLen = newB.length;
         if (collLen > 150) {
            this._handleSpillOverflow(bix, newB, index);
         } else {
            this._buckets[bix] = newB;
            this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
         }
      }

      return newSymbol;
   }

   private void _handleSpillOverflow(int bucketIndex, Bucket newBucket, int mainIndex) throws IOException {
      if (this._overflows == null) {
         this._overflows = new BitSet();
         this._overflows.set(bucketIndex);
      } else if (this._overflows.get(bucketIndex)) {
         if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._factoryFeatures)) {
            this._reportTooManyCollisions(150);
         }

         this._canonicalize = false;
      } else {
         this._overflows.set(bucketIndex);
      }

      this._symbols[mainIndex] = newBucket.symbol;
      this._buckets[bucketIndex] = null;
      this._size -= newBucket.length;
      this._longestCollisionList = -1;
   }

   public int _hashToIndex(int rawHash) {
      rawHash += rawHash >>> 15;
      rawHash ^= rawHash << 7;
      rawHash += rawHash >>> 3;
      return rawHash & this._indexMask;
   }

   public int calcHash(char[] buffer, int start, int len) {
      int hash = this._seed;
      int i = start;

      for(int end = start + len; i < end; ++i) {
         hash = hash * 33 + buffer[i];
      }

      return hash == 0 ? 1 : hash;
   }

   public int calcHash(String key) {
      int len = key.length();
      int hash = this._seed;

      for(int i = 0; i < len; ++i) {
         hash = hash * 33 + key.charAt(i);
      }

      return hash == 0 ? 1 : hash;
   }

   private void copyArrays() {
      String[] oldSyms = this._symbols;
      this._symbols = (String[])Arrays.copyOf(oldSyms, oldSyms.length);
      Bucket[] oldBuckets = this._buckets;
      this._buckets = (Bucket[])Arrays.copyOf(oldBuckets, oldBuckets.length);
   }

   private void rehash() throws IOException {
      int size = this._symbols.length;
      int newSize = size + size;
      if (newSize > 65536) {
         this._size = 0;
         this._canonicalize = false;
         this._symbols = new String[64];
         this._buckets = new Bucket[32];
         this._indexMask = 63;
         this._hashShared = false;
      } else {
         String[] oldSyms = this._symbols;
         Bucket[] oldBuckets = this._buckets;
         this._symbols = new String[newSize];
         this._buckets = new Bucket[newSize >> 1];
         this._indexMask = newSize - 1;
         this._sizeThreshold = _thresholdSize(newSize);
         int count = 0;
         int maxColl = 0;

         for(int i = 0; i < size; ++i) {
            String symbol = oldSyms[i];
            if (symbol != null) {
               ++count;
               int index = this._hashToIndex(this.calcHash(symbol));
               if (this._symbols[index] == null) {
                  this._symbols[index] = symbol;
               } else {
                  int bix = index >> 1;
                  Bucket newB = new Bucket(symbol, this._buckets[bix]);
                  this._buckets[bix] = newB;
                  maxColl = Math.max(maxColl, newB.length);
               }
            }
         }

         int bucketSize = size >> 1;

         for(int i = 0; i < bucketSize; ++i) {
            for(Bucket b = oldBuckets[i]; b != null; b = b.next) {
               ++count;
               String symbol = b.symbol;
               int index = this._hashToIndex(this.calcHash(symbol));
               if (this._symbols[index] == null) {
                  this._symbols[index] = symbol;
               } else {
                  int bix = index >> 1;
                  Bucket newB = new Bucket(symbol, this._buckets[bix]);
                  this._buckets[bix] = newB;
                  maxColl = Math.max(maxColl, newB.length);
               }
            }
         }

         this._longestCollisionList = maxColl;
         this._overflows = null;
         if (count != this._size) {
            throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", this._size, count));
         }
      }
   }

   protected void _reportTooManyCollisions(int maxLen) throws StreamConstraintsException {
      throw new StreamConstraintsException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
   }

   protected void verifyInternalConsistency() {
      int count = 0;
      int size = this._symbols.length;

      for(int i = 0; i < size; ++i) {
         String symbol = this._symbols[i];
         if (symbol != null) {
            ++count;
         }
      }

      int bucketSize = size >> 1;

      for(int i = 0; i < bucketSize; ++i) {
         for(Bucket b = this._buckets[i]; b != null; b = b.next) {
            ++count;
         }
      }

      if (count != this._size) {
         throw new IllegalStateException(String.format("Internal error: expected internal size %d vs calculated count %d", this._size, count));
      }
   }

   static final class Bucket {
      public final String symbol;
      public final Bucket next;
      public final int length;

      public Bucket(String s, Bucket n) {
         this.symbol = s;
         this.next = n;
         this.length = n == null ? 1 : n.length + 1;
      }

      public String has(char[] buf, int start, int len) {
         if (this.symbol.length() != len) {
            return null;
         } else {
            int i = 0;

            while(this.symbol.charAt(i) == buf[start + i]) {
               ++i;
               if (i >= len) {
                  return this.symbol;
               }
            }

            return null;
         }
      }
   }

   private static final class TableInfo {
      final int size;
      final int longestCollisionList;
      final String[] symbols;
      final Bucket[] buckets;

      public TableInfo(int size, int longestCollisionList, String[] symbols, Bucket[] buckets) {
         this.size = size;
         this.longestCollisionList = longestCollisionList;
         this.symbols = symbols;
         this.buckets = buckets;
      }

      public TableInfo(CharsToNameCanonicalizer src) {
         this.size = src._size;
         this.longestCollisionList = src._longestCollisionList;
         this.symbols = src._symbols;
         this.buckets = src._buckets;
      }

      public static TableInfo createInitial(int sz) {
         return new TableInfo(0, 0, new String[sz], new Bucket[sz >> 1]);
      }
   }
}
