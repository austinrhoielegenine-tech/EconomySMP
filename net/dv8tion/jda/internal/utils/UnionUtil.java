package net.dv8tion.jda.internal.utils;

public class UnionUtil {
   public static <T> T safeUnionCast(String classCategory, Object instance, Class<T> toObjectClass) {
      if (toObjectClass.isInstance(instance)) {
         return (T)toObjectClass.cast(instance);
      } else {
         String cleanedClassName = instance.getClass().getSimpleName().replace("Impl", "");
         throw new IllegalStateException(Helpers.format("Cannot convert %s of type %s to %s!", classCategory, cleanedClassName, toObjectClass.getSimpleName()));
      }
   }
}
