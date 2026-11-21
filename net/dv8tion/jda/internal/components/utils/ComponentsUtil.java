package net.dv8tion.jda.internal.components.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.IComponentUnion;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.replacer.ComponentReplacer;
import net.dv8tion.jda.api.components.replacer.IReplaceable;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.utils.ComponentIterator;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class ComponentsUtil {
   public static <T extends IComponentUnion> T safeUnionCast(String componentCategory, Component component, Class<T> toUnionClass) {
      if (toUnionClass.isInstance(component)) {
         T union = (T)(toUnionClass.cast(component));
         Checks.check(!union.isUnknownComponent(), "Cannot provide UnknownComponent");
         return union;
      } else {
         String cleanedClassName = component.getClass().getSimpleName().replace("Impl", "");
         throw new IllegalArgumentException(Helpers.format("Cannot convert %s of type %s to %s!", componentCategory, cleanedClassName, toUnionClass.getSimpleName()));
      }
   }

   public static <T extends Component> T safeUnionCastWithUnknownType(String componentCategory, Component component, Class<T> toUnionClass) {
      if (toUnionClass.isInstance(component)) {
         return (T)(toUnionClass.cast(component));
      } else {
         String cleanedClassName = component.getClass().getSimpleName().replace("Impl", "");
         throw new IllegalArgumentException(Helpers.format("Cannot convert %s of type %s to %s!", componentCategory, cleanedClassName, toUnionClass.getSimpleName()));
      }
   }

   public static <TUnion extends IComponentUnion> List<TUnion> membersToUnion(Collection<? extends Component> members, Class<TUnion> clazz) {
      return (List)members.stream().map((c) -> safeUnionCast("component", c, clazz)).collect(Collectors.toList());
   }

   public static <T extends Component> List<T> membersToUnionWithUnknownType(Collection<? extends Component> members, Class<T> clazz) {
      return (List)members.stream().map((c) -> safeUnionCastWithUnknownType("component", c, clazz)).collect(Collectors.toList());
   }

   public static <R, E extends Component> R doReplace(Class<? extends Component> expectedChildrenType, Iterable<E> children, ComponentReplacer replacer, Function<List<E>, R> finisher) {
      List<E> newComponents = new ArrayList();

      for(E component : children) {
         Component newComponent = replacer.apply(component);
         if (newComponent != null) {
            if (newComponent != component) {
               Checks.checkComponentType(expectedChildrenType, component, newComponent);
            } else if (component instanceof IReplaceable) {
               newComponent = ((IReplaceable)component).replace(replacer);
               Checks.checkComponentType(expectedChildrenType, component, newComponent);
            }

            newComponents.add(newComponent);
         }
      }

      return (R)finisher.apply(newComponents);
   }

   public static long getComponentTreeSize(@Nonnull Collection<? extends Component> tree) {
      return ComponentIterator.createStream(tree).count();
   }

   @Nonnull
   public static List<? extends Component> getIllegalV1Components(@Nonnull Collection<? extends Component> components) {
      return (List)components.stream().filter((c) -> !(c instanceof ActionRow)).collect(Collectors.toList());
   }

   public static boolean hasIllegalV1Components(@Nonnull Collection<? extends Component> components) {
      return !getIllegalV1Components(components).isEmpty();
   }

   public static long getComponentTreeTextContentLength(@Nonnull Collection<? extends Component> components) {
      return (long)ComponentIterator.createStream(components).mapToInt((c) -> c instanceof TextDisplay ? ((TextDisplay)c).getContent().length() : 0).sum();
   }

   public static Stream<FileUpload> getFilesFromMedia(@Nullable ResolvedMedia media) {
      if (media != null && media.getAttachmentId() != null) {
         String fileName = Helpers.getLastPathSegment(media.getUrl());
         return Stream.of(media.getProxy().downloadAsFileUpload(fileName));
      } else {
         return Stream.empty();
      }
   }

   public static String getMediaUrl(@Nullable ResolvedMedia media, @Nullable String url) {
      return media != null && media.getAttachmentId() != null ? "attachment://" + Helpers.getLastPathSegment(media.getUrl()) : url;
   }
}
