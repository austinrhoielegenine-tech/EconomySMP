package gnu.trove.list;

import java.io.Serializable;

public interface TLinkable<T extends TLinkable> extends Serializable {
   long serialVersionUID = 997545054865482562L;

   T getNext();

   T getPrevious();

   void setNext(T var1);

   void setPrevious(T var1);
}
