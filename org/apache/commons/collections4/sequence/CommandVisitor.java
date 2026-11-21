package org.apache.commons.collections4.sequence;

public interface CommandVisitor<T> {
   void visitDeleteCommand(T var1);

   void visitInsertCommand(T var1);

   void visitKeepCommand(T var1);
}
