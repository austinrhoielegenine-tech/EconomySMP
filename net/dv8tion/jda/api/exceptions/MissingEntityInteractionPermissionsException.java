package net.dv8tion.jda.api.exceptions;

public class MissingEntityInteractionPermissionsException extends IllegalStateException {
   public MissingEntityInteractionPermissionsException(String reason) {
      super(reason);
   }
}
