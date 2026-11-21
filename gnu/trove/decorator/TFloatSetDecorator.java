package gnu.trove.decorator;

import gnu.trove.iterator.TFloatIterator;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TFloatSetDecorator extends AbstractSet<Float> implements Set<Float>, Externalizable {
   static final long serialVersionUID = 1L;
   protected TFloatSet _set;

   public TFloatSetDecorator() {
   }

   public TFloatSetDecorator(TFloatSet set) {
      Objects.requireNonNull(set);
      this._set = set;
   }

   public TFloatSet getSet() {
      return this._set;
   }

   public boolean add(Float value) {
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
               if (!(val instanceof Float)) {
                  return false;
               }

               float v = (Float)val;
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
      return value instanceof Float && this._set.remove((Float)value);
   }

   public Iterator<Float> iterator() {
      return new Iterator<Float>() {
         private final TFloatIterator it;

         {
            this.it = TFloatSetDecorator.this._set.iterator();
         }

         public Float next() {
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
      return !(o instanceof Float) ? false : this._set.contains((Float)o);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._set = (TFloatSet)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._set);
   }
}
