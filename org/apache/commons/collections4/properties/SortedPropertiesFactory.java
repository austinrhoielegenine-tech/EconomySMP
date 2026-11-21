package org.apache.commons.collections4.properties;

public class SortedPropertiesFactory extends AbstractPropertiesFactory<SortedProperties> {
   public static final SortedPropertiesFactory INSTANCE = new SortedPropertiesFactory();

   private SortedPropertiesFactory() {
   }

   protected SortedProperties createProperties() {
      return new SortedProperties();
   }
}
