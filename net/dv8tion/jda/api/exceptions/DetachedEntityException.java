package net.dv8tion.jda.api.exceptions;

public class DetachedEntityException extends UnsupportedOperationException {
   public DetachedEntityException() {
      this("Unsupported operation on detached entities");
   }

   public DetachedEntityException(String reason) {
      super(reason);
   }
}
