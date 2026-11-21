package okhttp3.internal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;

@Retention(AnnotationRetention.BINARY)
@Documented
@Target(
   allowedTargets = {AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION}
)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0081\u0002\u0018\u00002\u00020\u0001B\u0000¨\u0006\u0002"},
   d2 = {"Lokhttp3/internal/SuppressSignatureCheck;", "", "okhttp"}
)
public @interface SuppressSignatureCheck {
}
