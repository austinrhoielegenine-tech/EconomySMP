package net.dv8tion.jda.internal.requests.restaction.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.order.OrderAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class OrderActionImpl<T, M extends OrderAction<T, M>> extends RestActionImpl<Void> implements OrderAction<T, M> {
   protected final List<T> orderList;
   protected final boolean ascendingOrder;
   protected int selectedPosition;

   public OrderActionImpl(JDA api, Route.CompiledRoute route) {
      this(api, true, route);
   }

   public OrderActionImpl(JDA api, boolean ascendingOrder, Route.CompiledRoute route) {
      super(api, route);
      this.selectedPosition = -1;
      this.orderList = new ArrayList();
      this.ascendingOrder = ascendingOrder;
   }

   @Nonnull
   public M setCheck(BooleanSupplier checks) {
      return (M)(super.setCheck(checks));
   }

   @Nonnull
   public M timeout(long timeout, @Nonnull TimeUnit unit) {
      return (M)(super.timeout(timeout, unit));
   }

   @Nonnull
   public M deadline(long timestamp) {
      return (M)(super.deadline(timestamp));
   }

   public boolean isAscendingOrder() {
      return this.ascendingOrder;
   }

   @Nonnull
   public List<T> getCurrentOrder() {
      return Collections.unmodifiableList(this.orderList);
   }

   @Nonnull
   public M selectPosition(int selectedPosition) {
      Checks.notNegative(selectedPosition, "Provided selectedPosition");
      Checks.check(selectedPosition < this.orderList.size(), "Provided selectedPosition is too big and is out of bounds. selectedPosition: " + selectedPosition);
      this.selectedPosition = selectedPosition;
      return (M)this;
   }

   @Nonnull
   public M selectPosition(@Nonnull T selectedEntity) {
      Checks.notNull(selectedEntity, "Channel");
      this.validateInput(selectedEntity);
      return (M)this.selectPosition(this.orderList.indexOf(selectedEntity));
   }

   public int getSelectedPosition() {
      return this.selectedPosition;
   }

   @Nonnull
   public T getSelectedEntity() {
      if (this.selectedPosition == -1) {
         throw new IllegalStateException("No position has been selected yet");
      } else {
         return (T)this.orderList.get(this.selectedPosition);
      }
   }

   @Nonnull
   public M moveUp(int amount) {
      Checks.notNegative(amount, "Provided amount");
      if (this.selectedPosition == -1) {
         throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
      } else {
         if (this.ascendingOrder) {
            Checks.check(this.selectedPosition - amount >= 0, "Amount provided to move up is too large and would be out of bounds.Selected position: " + this.selectedPosition + " Amount: " + amount + " Largest Position: " + this.orderList.size());
         } else {
            Checks.check(this.selectedPosition + amount < this.orderList.size(), "Amount provided to move up is too large and would be out of bounds.Selected position: " + this.selectedPosition + " Amount: " + amount + " Largest Position: " + this.orderList.size());
         }

         return (M)(this.ascendingOrder ? this.moveTo(this.selectedPosition - amount) : this.moveTo(this.selectedPosition + amount));
      }
   }

   @Nonnull
   public M moveDown(int amount) {
      Checks.notNegative(amount, "Provided amount");
      if (this.selectedPosition == -1) {
         throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
      } else {
         if (this.ascendingOrder) {
            Checks.check(this.selectedPosition + amount < this.orderList.size(), "Amount provided to move down is too large and would be out of bounds. Selected position: " + this.selectedPosition + " Amount: " + amount + " Largest Position: " + this.orderList.size());
         } else {
            Checks.check(this.selectedPosition - amount >= 0, "Amount provided to move down is too large and would be out of bounds. Selected position: " + this.selectedPosition + " Amount: " + amount + " Largest Position: " + this.orderList.size());
         }

         return (M)(this.ascendingOrder ? this.moveTo(this.selectedPosition + amount) : this.moveTo(this.selectedPosition - amount));
      }
   }

   @Nonnull
   public M moveTo(int position) {
      Checks.notNegative(position, "Provided position");
      Checks.check(position < this.orderList.size(), "Provided position is too big and is out of bounds.");
      if (this.selectedPosition == -1) {
         throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
      } else {
         T selectedItem = (T)this.orderList.remove(this.selectedPosition);
         this.orderList.add(position, selectedItem);
         this.selectedPosition = position;
         return (M)this;
      }
   }

   @Nonnull
   public M moveBelow(@Nonnull T other) {
      this.validateInput(other);
      int index = this.getCurrentOrder().indexOf(other);
      this.moveTo(index);
      return (M)(this.isAscendingOrder() ? this.moveDown(1) : this);
   }

   @Nonnull
   public M moveAbove(@Nonnull T other) {
      this.validateInput(other);
      int index = this.getCurrentOrder().indexOf(other);
      this.moveTo(index);
      return (M)(!this.isAscendingOrder() ? this.moveUp(1) : this);
   }

   @Nonnull
   public M swapPosition(int swapPosition) {
      Checks.notNegative(swapPosition, "Provided swapPosition");
      Checks.check(swapPosition < this.orderList.size(), "Provided swapPosition is too big and is out of bounds. swapPosition: " + swapPosition);
      if (this.selectedPosition == -1) {
         throw new IllegalStateException("Cannot move until an item has been selected. Use #selectPosition first.");
      } else {
         T selectedItem = (T)this.orderList.get(this.selectedPosition);
         T swapItem = (T)this.orderList.get(swapPosition);
         this.orderList.set(swapPosition, selectedItem);
         this.orderList.set(this.selectedPosition, swapItem);
         this.selectedPosition = swapPosition;
         return (M)this;
      }
   }

   @Nonnull
   public M swapPosition(@Nonnull T swapEntity) {
      Checks.notNull(swapEntity, "Provided swapEntity");
      this.validateInput(swapEntity);
      return (M)this.swapPosition(this.orderList.indexOf(swapEntity));
   }

   @Nonnull
   public M reverseOrder() {
      Collections.reverse(this.orderList);
      return (M)this;
   }

   @Nonnull
   public M shuffleOrder() {
      Collections.shuffle(this.orderList);
      return (M)this;
   }

   @Nonnull
   public M sortOrder(@Nonnull Comparator<T> comparator) {
      Checks.notNull(comparator, "Provided comparator");
      this.orderList.sort(comparator);
      return (M)this;
   }

   protected abstract void validateInput(T var1);
}
