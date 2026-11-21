package org.apache.commons.collections4.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator implements Iterator<Node> {
   private final NodeList nodeList;
   private int index;

   public NodeListIterator(Node node) {
      Objects.requireNonNull(node, "node");
      this.nodeList = node.getChildNodes();
   }

   public NodeListIterator(NodeList nodeList) {
      this.nodeList = (NodeList)Objects.requireNonNull(nodeList, "nodeList");
   }

   public boolean hasNext() {
      return this.nodeList != null && this.index < this.nodeList.getLength();
   }

   public Node next() {
      if (this.nodeList != null && this.index < this.nodeList.getLength()) {
         return this.nodeList.item(this.index++);
      } else {
         throw new NoSuchElementException("underlying nodeList has no more elements");
      }
   }

   public void remove() {
      throw new UnsupportedOperationException("remove() method not supported for a NodeListIterator.");
   }
}
