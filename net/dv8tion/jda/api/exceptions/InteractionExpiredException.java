package net.dv8tion.jda.api.exceptions;

public class InteractionExpiredException extends RuntimeException {
   public InteractionExpiredException() {
      super("The interaction has expired and can no longer be responded to!");
   }
}
