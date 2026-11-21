package okio;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;

/** @deprecated */
@Deprecated(
   message = "This annotation is obsolete and should be removed.",
   level = DeprecationLevel.HIDDEN
)
@Retention(AnnotationRetention.SOURCE)
@Target(
   allowedTargets = {AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY}
)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target({ElementType.TYPE, ElementType.METHOD})
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0087\u0002\u0018\u00002\u00020\u0001B\u0000¨\u0006\u0002"},
   d2 = {"Lokio/ExperimentalFileSystem;", "", "okio"}
)
public @interface ExperimentalFileSystem {
}
