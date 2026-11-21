package org.apache.commons.collections4.properties;

public class OrderedPropertiesFactory extends AbstractPropertiesFactory<OrderedProperties> {
   public static final OrderedPropertiesFactory INSTANCE = new OrderedPropertiesFactory();

   private OrderedPropertiesFactory() {
   }

   protected OrderedProperties createProperties() {
      return new OrderedProperties();
   }
}
