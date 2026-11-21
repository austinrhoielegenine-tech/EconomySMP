package gnu.trove.decorator;

import gnu.trove.iterator.TShortIterator;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class TShortSetDecorator extends AbstractSet<Short> implements Set<Short>, Externalizable {
   static final long serialVersionUID = 1L;
   protected TShortSet _set;

   public TShortSetDecorator() {
   }

   public TShortSetDecorator(TShortSet set) {
      Objects.requireNonNull(set);
      this._set = set;
   }

   public TShortSet getSet() {
      return this._set;
   }

   public boolean add(Short value) {
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
               if (!(val instanceof Short)) {
                  return false;
               }

               short v = (Short)val;
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
      return value instanceof Short && this._set.remove((Short)value);
   }

   public Iterator<Short> iterator() {
      return new Iterator<Short>() {
         private final TShortIterator it;

         {
            this.it = TShortSetDecorator.this._set.iterator();
         }

         public Short next() {
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
      return !(o instanceof Short) ? false : this._set.contains((Short)o);
   }

   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      in.readByte();
      this._set = (TShortSet)in.readObject();
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeByte(0);
      out.writeObject(this._set);
   }
}
