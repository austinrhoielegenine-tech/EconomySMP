package gnu.trove.list.array;

import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.list.TShortList;
import gnu.trove.procedure.TShortProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

public class TShortArrayList implements TShortList, Externalizable {
   static final long serialVersionUID = 1L;
   protected static final int DEFAULT_CAPACITY = 10;
   protected short[] _data;
   protected int _pos;
   protected short no_entry_value;

   public TShortArrayList() {
      this(10, (short)0);
   }

   public TShortArrayList(int capacity) {
      this(capacity, (short)0);
   }

   public TShortArrayList(int capacity, short no_entry_value) {
      this._data = new short[capacity];
      this._pos = 0;
      this.no_entry_value = no_entry_value;
   }

   public TShortArrayList(TShortCollection collection) {
      this(collection.size());
      this.addAll(collection);
   }

   public TShortArrayList(short[] values) {
      this(values.length);
      this.add(values);
   }

   protected TShortArrayList(short[] values, short no_entry_value, boolean wrap) {
      if (!wrap) {
         throw new IllegalStateException("Wrong call");
      } else if (values == null) {
         throw new IllegalArgumentException("values can not be null");
      } else {
         this._data = values;
         this._pos = values.length;
         this.no_entry_value = no_entry_value;
      }
   }

   public static TShortArrayList wrap(short[] values) {
      return wrap(values, (short)0);
   }

   public static TShortArrayList wrap(short[] values, short no_entry_value) {
      return new TShortArrayList(values, no_entry_value, true) {
         public void ensureCapacity(int capacity) {
            if (capacity > this._data.length) {
               throw new IllegalStateException("Can not grow ArrayList wrapped external array");
            }
         }
      };
   }

   public short getNoEntryValue() {
      return this.no_entry_value;
   }

   public void ensureCapacity(int capacity) {
      if (capacity > this._data.length) {
         int newCap = Math.max(this._data.length << 1, capacity);
         short[] tmp = new short[newCap];
         System.arraycopy(this._data, 0, tmp, 0, this._data.length);
         this._data = tmp;
      }

   }

   public int size() {
      return this._pos;
   }

   public boolean isEmpty() {
      return this._pos == 0;
   }

   public void trimToSize() {
      if (this._data.length > this.size()) {
         short[] tmp = new short[this.size()];
         this.toArray(tmp, 0, tmp.length);
         this._data = tmp;
      }

   }

   public boolean add(short val) {
      this.ensureCapacity(this._pos + 1);
      this._data[this._pos++] = val;
      return true;
   }

   public void add(short[] vals) {
      this.add(vals, 0, vals.length);
   }

   public void add(short[] vals, int offset, int length) {
      this.ensureCapacity(this._pos + length);
      System.arraycopy(vals, offset, this._data, this._pos, length);
      this._pos += length;
   }

   public void insert(int offset, short value) {
      if (offset == this._pos) {
         this.add(value);
      } else {
         this.ensureCapacity(this._pos + 1);
         System.arraycopy(this._data, offset, this._data, offset + 1, this._pos - offset);
         this._data[offset] = value;
         ++this._pos;
      }
   }

   public void insert(int offset, short[] values) {
      this.insert(offset, values, 0, values.length);
   }

   public void insert(int offset, short[] values, int valOffset, int len) {
      if (offset == this._pos) {
         this.add(values, valOffset, len);
      } else {
         this.ensureCapacity(this._pos + len);
         System.arraycopy(this._data, offset, this._data, offset + len, this._pos - offset);
         System.arraycopy(values, valOffset, this._data, offset, len);
         this._pos += len;
      }
   }

   public short get(int offset) {
      if (offset >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(offset);
      } else {
         return this._data[offset];
      }
   }

   public short getQuick(int offset) {
      return this._data[offset];
   }

   public short set(int offset, short val) {
      if (offset >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(offset);
      } else {
         short prev_val = this._data[offset];
         this._data[offset] = val;
         return prev_val;
      }
   }

   public short replace(int offset, short val) {
      if (offset >= this._pos) {
         throw new ArrayIndexOutOfBoundsException(offset);
      } else {
         short old = this._data[offset];
         this._data[offset] = val;
         return old;
      }
   }

   public void set(int offset, short[] values) {
      this.set(offset, values, 0, values.length);
   }

   public void set(int offset, short[] values, int valOffset, int length) {
      if (offset >= 0 && offset + length <= this._pos) {
         System.arraycopy(values, valOffset, this._data, offset, length);
      } else {
         throw new ArrayIndexOutOfBoundsException(offset);
      }
   }

   public void setQuick(int offset, short val) {
      this._data[offset] = val;
   }

   public void clear() {
      this.clearQuick();
      Arrays.fill(this._data, this.no_entry_value);
   }

   public void clearQuick() {
      this._pos = 0;
   }

   public boolean remove(short value) {
      for(int index = 0; index < this._pos; ++index) {
         if (value == this._data[index]) {
            this.remove(index, 1);
            return true;
         }
      }

      return false;
   }

   public short removeAt(int offset) {
      short old = this.get(offset);
      this.remove(offset, 1);
      return old;
   }

   public void remove(int offset, int length) {
      if (length != 0) {
         if (offset >= 0 && offset < this._pos) {
            if (offset == 0) {
               System.arraycopy(this._data, length, this._data, 0, this._pos - length);
            } else if (this._pos - length != offset) {
               System.arraycopy(this._data, offset + length, this._data, offset, this._pos - (offset + length));
            }

            this._pos -= length;
         } else {
            throw new ArrayIndexOutOfBoundsException(offset);
         }
      }
   }

   public TShortIterator iterator() {
      return new TShortArrayIterator(0);
   }

   public boolean containsAll(Collection<?> collection) {
      for(Object element : collection) {
         if (!(element instanceof Short)) {
            return false;
         }

         short c = (Short)element;
         if (!this.contains(c)) {
            return false;
         }
      }

      return true;
   }

   public boolean containsAll(TShortCollection collection) {
      if (this == collection) {
         return true;
      } else {
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            short element = iter.next();
            if (!this.contains(element)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean containsAll(short[] array) {
      int i = array.length;

      while(i-- > 0) {
         if (!this.contains(array[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean addAll(Collection<? extends Short> collection) {
      boolean changed = false;

      for(Short element : collection) {
         short e = element;
         if (this.add(e)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(TShortCollection collection) {
      boolean changed = false;
      TShortIterator iter = collection.iterator();

      while(iter.hasNext()) {
         short element = iter.next();
         if (this.add(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean addAll(short[] array) {
      boolean changed = false;

      for(short element : array) {
         if (this.add(element)) {
            changed = true;
         }
      }

      return changed;
   }

   public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TShortIterator iter = this.iterator();

      while(iter.hasNext()) {
         if (!collection.contains(iter.next())) {
            iter.remove();
            modified = true;
         }
      }

      return modified;
   }

   public boolean retainAll(TShortCollection collection) {
      if (this == collection) {
         return false;
      } else {
         boolean modified = false;
         TShortIterator iter = this.iterator();

         while(iter.hasNext()) {
            if (!collection.contains(iter.next())) {
               iter.remove();
               modified = true;
            }
         }

         return modified;
      }
   }

   public boolean retainAll(short[] array) {
      boolean changed = false;
      Arrays.sort(array);
      short[] data = this._data;
      int i = this._pos;

      while(i-- > 0) {
         if (Arrays.binarySearch(array, data[i]) < 0) {
            this.remove(i, 1);
            changed = true;
         }
      }

      return changed;
   }

   public boolean removeAll(Collection<?> collection) {
      boolean changed = false;

      for(Object element : collection) {
         if (element instanceof Short) {
            short c = (Short)element;
            if (this.remove(c)) {
               changed = true;
            }
         }
      }

      return changed;
   }

   public boolean removeAll(TShortCollection collection) {
      if (collection == this) {
         this.clear();
         return true;
      } else {
         boolean changed = false;
         TShortIterator iter = collection.iterator();

         while(iter.hasNext()) {
            short element = iter.next();
            if (this.remove(element)) {
               changed = true;
            }
         }

         return changed;
      }
   }

   public boolean removeAll(short[] array) {
      boolean changed = false;
      int i = array.length;

      while(i-- > 0) {
         if (this.remove(array[i])) {
            changed = true;
         }
      }

      return changed;
   }

   public void transformValues(TShortFunction function) {
      for(int i = 0; i < this._pos; ++i) {
         this._data[i] = function.execute(this._data[i]);
      }

   }

   public void reverse() {
      this.reverse(0, this._pos);
   }

   public void reverse(int from, int to) {
      if (from != to) {
         if (from > to) {
            throw new IllegalArgumentException("from cannot be greater than to");
         } else {
            int i = from;

            for(int j = to - 1; i < j; --j) {
               this.swap(i, j);
               ++i;
            }

         }
      }
   }

   public void shuffle(Random rand) {
      int i = this._pos;

      while(i-- > 1) {
         this.swap(i, rand.nextInt(i));
      }

   }

   private void swap(int i, int j) {
      short tmp = this._data[i];
      this._data[i] = this._data[j];
      this._data[j] = tmp;
   }

   public TShortList subList(int begin, int end) {
      if (end < begin) {
         throw new IllegalArgumentException("end index " + end + " greater than begin index " + begin);
      } else if (begin < 0) {
         throw new IndexOutOfBoundsException("begin index can not be < 0");
      } else if (end > this._data.length) {
         throw new IndexOutOfBoundsException("end index < " + this._data.length);
      } else {
         TShortArrayList list = new TShortArrayList(end - begin);

         for(int i = begin; i < end; ++i) {
            list.add(this._data[i]);
         }

         return list;
      }
   }

   public short[] toArray() {
      return this.toArray(0, this._pos);
   }

   public short[] toArray(int offset, int len) {
      short[] rv = new short[len];
      this.toArray(rv, offset, len);
      return rv;
   }

   public short[] toArray(short[] dest) {
      int len = dest.length;
      if (dest.length > this._pos) {
         len = this._pos;
         dest[len] = this.no_entry_value;
      }

      this.toArray(dest, 0, len);
      return dest;
   }

   public short[] toArray(short[] dest, int offset, int len) {
      if (len == 0) {
         return dest;
      } else if (offset >= 0 && offset < this._pos) {
         System.arraycopy(this._data, offset, dest, 0, len);
         return dest;
      } else {
         throw new ArrayIndexOutOfBoundsException(offset);
      }
   }

   public short[] toArray(short[] dest, int source_pos, int dest_pos, int len) {
      if (len == 0) {
         return dest;
      } else if (source_pos >= 0 && source_pos < this._pos) {
         System.arraycopy(this._data, source_pos, dest, dest_pos, len);
         return dest;
      } else {
         throw new ArrayIndexOutOfBoundsException(source_pos);
      }
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof TShortList)) {
         return false;
      } else if (other instanceof TShortArrayList) {
         TShortArrayList that = (TShortArrayList)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            int i = this._pos;

            while(i-- > 0) {
               if (this._data[i] != that._data[i]) {
                  return false;
               }
            }

            return true;
         }
      } else {
         TShortList that = (TShortList)other;
         if (that.size() != this.size()) {
            return false;
         } else {
            for(int i = 0; i < this._pos; ++i) {
               if (this._data[i] != that.get(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int h = 0;

      for(int i = this._pos; i-- > 0; h += HashFunctions.hash(this._data[i])) {
      }

      return h;
   }

   public boolean forEach(TShortProcedure procedure) {
      for(int i = 0; i < this._pos; ++i) {
         if (!procedure.execute(this._data[i])) {
            return false;
         }
      }

      return true;
   }

   public boolean forEachDescending(TShortProcedure procedure) {
      int i = this._pos;

      while(i-- > 0) {
         if (!procedure.execute(this._data[i])) {
            return false;
         }
      }

      return true;
   }

   public void sort() {
      Arrays.sort(this._data, 0, this._pos);
   }

   public void sort(int fromIndex, int toIndex) {
      Arrays.sort(this._data, fromIndex, toIndex);
   }

   public void fill(short val) {
      Arrays.fill(this._data, 0, this._pos, val);
   }

   public void fill(int fromIndex, int toIndex, short val) {
      if (toIndex > this._pos) {
         this.ensureCapacity(toIndex);
         this._pos = toIndex;
      }

      Arrays.fill(this._data, fromIndex, toIndex, val);
   }

   public int binarySearch(short value) {
      return this.binarySearch(value, 0, this._pos);
   }

   public int binarySearch(short value, int fromIndex, int toIndex) {
      if (fromIndex < 0) {
         throw new ArrayIndexOutOfBoundsException(fromIndex);
      } else if (toIndex > this._pos) {
         throw new ArrayIndexOutOfBoundsException(toIndex);
      } else {
         int low = fromIndex;
         int high = toIndex - 1;

         while(low <= high) {
            int mid = low + high >>> 1;
            short midVal = this._data[mid];
            if (midVal < value) {
               low = mid + 1;
            } else {
               if (midVal <= value) {
                  return mid;
               }

               high = mid - 1;
            }
         }

         return -(low + 1);
      }
   }

   public int indexOf(short value) {
      return this.indexOf(0, value);
   }

   public int indexOf(int offset, short value) {
      for(int i = offset; i < this._pos; ++i) {
         if (this._data[i] == value) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexOf(short value) {
      return this.lastIndexOf(this._pos, value);
   }

   public int lastIndexOf(int offset, short value) {
      int i = offset;

      while(i-- > 0) {
         if (this._data[i] == value) {
            return i;
         }
      }

      return -1;
   }

   public boolean contains(short value) {
      return this.lastIndexOf(value) >= 0;
   }

   public TShortList grep(TShortProcedure condition) {
      TShortArrayList list = new TShortArrayList();

      for(int i = 0; i < this._pos; ++i) {
         if (condition.execute(this._data[i])) {
            list.add(this._data[i]);
         }
      }

      return list;
   }

   public TShortList inverseGrep(TShortProcedure condition) {
      TShortArrayList list = new TShortArrayList();

      for(int i = 0; i < this._pos; ++i) {
         if (!condition.execute(this._data[i])) {
            list.add(this._data[i]);
         }
      }

      return list;
   }

   public short max() {
      if (this.size() == 0) {
         throw new IllegalStateException("cannot find maximum of an empty list");
      } else {
         short max = Short.MIN_VALUE;

         for(int i = 0; i < this._pos; ++i) {
            if (this._data[i] > max) {
               max = this._data[i];
            }
         }

         return max;
      }
   }

   public short min() {
      if (this.size() == 0) {
         throw new IllegalStateException("cannot find minimum of an empty list");
      } else {
         short min = 32767;

         for(int i = 0; i < this._pos; ++i) {
            if (this._data[i] < min) {
               min = this._data[i];
            }
         }

         return min;
      }
   }

   public short sum() {
      short sum = 0;

      for(int i = 0; i < this._pos; ++i) {
         sum += this._data[i];
      }

      return sum;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder("{");
      int i = 0;

      for(int end = this._pos - 1; i < end; ++i) {
         buf.append(this._data[i]);
         buf.append(", ");
      }

      if (this.size() > 0) {
         buf.append(this._data[this._pos - 1]);
      }

      buf.append("}");
      return buf.toString();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeInt(this._pos);
      out.writeShort(this.no_entry_value);
      int len = this._data.length;
      out.writeInt(len);

      for(int i = 0; i < len; ++i) {
         out.writeShort(this._data[i]);
      }

   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._pos = in.readInt();
      this.no_entry_value = in.readShort();
      int len = in.readInt();
      this._data = new short[len];

      for(int i = 0; i < len; ++i) {
         this._data[i] = in.readShort();
      }

   }

   class TShortArrayIterator implements TShortIterator {
      private int cursor = 0;
      int lastRet = -1;

      TShortArrayIterator(int index) {
         this.cursor = index;
      }

      public boolean hasNext() {
         return this.cursor < TShortArrayList.this.size();
      }

      public short next() {
         try {
            short next = TShortArrayList.this.get(this.cursor);
            this.lastRet = this.cursor++;
            return next;
         } catch (IndexOutOfBoundsException var2) {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         if (this.lastRet == -1) {
            throw new IllegalStateException();
         } else {
            try {
               TShortArrayList.this.remove(this.lastRet, 1);
               if (this.lastRet < this.cursor) {
                  --this.cursor;
               }

               this.lastRet = -1;
            } catch (IndexOutOfBoundsException var2) {
               throw new ConcurrentModificationException();
            }
         }
      }
   }
}
