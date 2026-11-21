package net.dv8tion.jda.api.requests.restaction.order;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.Unmodifiable;

public interface OrderAction<T, M extends OrderAction<T, M>> extends RestAction<Void> {
   @Nonnull
   @CheckReturnValue
   M setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   M timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   M deadline(long var1);

   boolean isAscendingOrder();

   @Nonnull
   @Unmodifiable List<T> getCurrentOrder();

   @Nonnull
   @CheckReturnValue
   M selectPosition(int var1);

   @Nonnull
   @CheckReturnValue
   M selectPosition(@Nonnull T var1);

   int getSelectedPosition();

   @Nonnull
   T getSelectedEntity();

   @Nonnull
   @CheckReturnValue
   M moveUp(int var1);

   @Nonnull
   @CheckReturnValue
   M moveDown(int var1);

   @Nonnull
   @CheckReturnValue
   M moveTo(int var1);

   @Nonnull
   @CheckReturnValue
   M moveBelow(@Nonnull T var1);

   @Nonnull
   @CheckReturnValue
   M moveAbove(@Nonnull T var1);

   @Nonnull
   @CheckReturnValue
   M swapPosition(int var1);

   @Nonnull
   @CheckReturnValue
   M swapPosition(@Nonnull T var1);

   @Nonnull
   @CheckReturnValue
   M reverseOrder();

   @Nonnull
   @CheckReturnValue
   M shuffleOrder();

   @Nonnull
   @CheckReturnValue
   M sortOrder(@Nonnull Comparator<T> var1);
}
