package gnu.trove.decorator;

import gnu.trove.iterator.TByteIterator;
import gnu.trove.set.TByteSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TByteSetDecorator extends AbstractSet<Byte> implements Set<Byte>, Externalizable {
   static final long serialVersionUID = 1L;
   protected TByteSet _set;

   public TByteSetDecorator() {
   }

   public TByteSetDecorator(TByteSet set) {
      Objects.requireNonNull(set);
      this._set = set;
   }

   public TByteSet getSet() {
      return this._set;
   }

   public boolean add(Byte value) {
      return value != null && this._set.add(value);
   }

   public boolean equals(Object other) {
      if (this._set.equals(other)) {
         return true;
      } else if (other instanceof Set) {
         Set that = (Set)other;
         if (that.size() != this._set.size()) {
            return false;
         } else {
            Iterator it = that.iterator();
            int i = that.size();

            while(i-- > 0) {
               Object val = it.next();
               if (!(val instanceof Byte)) {
                  return false;
               }

               byte v = (Byte)val;
               if (!this._set.contains(v)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void clear() {
      this._set.clear();
   }

   public boolean remove(Object value) {
      return value instanceof Byte && this._set.remove((Byte)value);
   }

   public Iterator<Byte> iterator() {
      return new Iterator<Byte>() {
         private final TByteIterator it;

         {
            this.it = TByteSetDecorator.this._set.iterator();
         }

         public Byte next() {
            return this.it.next();
         }

         public boolean hasNext() {
            return this.it.hasNext();
         }

         public void remove() {
            this.it.remove();
         }
      };
   }

   public int size() {
      return this._set.size();
   }

   public boolean isEmpty() {
      return this._set.size() == 0;
   }

   public boolean contains(Object o) {
      return !(o instanceof Byte) ? false : this._set.contains((Byte)o);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._set = (TByteSet)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._set);
   }
}
