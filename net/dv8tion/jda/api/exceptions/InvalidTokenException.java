package net.dv8tion.jda.api.exceptions;

public class InvalidTokenException extends RuntimeException {
   public InvalidTokenException() {
      super("The provided token is invalid!");
   }
}
