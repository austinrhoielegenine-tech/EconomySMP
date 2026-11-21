package org.apache.commons.collections4.bloomfilter;

public final class Shape {
   private static final double LN_2 = Math.log((double)2.0F);
   private static final double DENOMINATOR;
   private final int numberOfHashFunctions;
   private final int numberOfBits;

   private static int calculateNumberOfHashFunctions(int numberOfItems, int numberOfBits) {
      long k = Math.round(LN_2 * (double)numberOfBits / (double)numberOfItems);
      if (k < 1L) {
         throw new IllegalArgumentException(String.format("Filter too small: Calculated number of hash functions (%s) was less than 1", k));
      } else {
         return (int)k;
      }
   }

   private static void checkCalculatedProbability(double probability) {
      if (probability >= (double)1.0F) {
         throw new IllegalArgumentException("Calculated probability is greater than or equal to 1: " + probability);
      }
   }

   private static int checkNumberOfBits(int numberOfBits) {
      if (numberOfBits < 1) {
         throw new IllegalArgumentException("Number of bits must be greater than 0: " + numberOfBits);
      } else {
         return numberOfBits;
      }
   }

   private static int checkNumberOfHashFunctions(int numberOfHashFunctions) {
      if (numberOfHashFunctions < 1) {
         throw new IllegalArgumentException("Number of hash functions must be greater than 0: " + numberOfHashFunctions);
      } else {
         return numberOfHashFunctions;
      }
   }

   private static int checkNumberOfItems(int numberOfItems) {
      if (numberOfItems < 1) {
         throw new IllegalArgumentException("Number of items must be greater than 0: " + numberOfItems);
      } else {
         return numberOfItems;
      }
   }

   private static void checkProbability(double probability) {
      if (!(probability > (double)0.0F) || !(probability < (double)1.0F)) {
         throw new IllegalArgumentException("Probability must be greater than 0 and less than 1: " + probability);
      }
   }

   public static Shape fromKM(int numberOfHashFunctions, int numberOfBits) {
      return new Shape(numberOfHashFunctions, numberOfBits);
   }

   public static Shape fromNM(int numberOfItems, int numberOfBits) {
      checkNumberOfItems(numberOfItems);
      checkNumberOfBits(numberOfBits);
      int numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
      Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
      checkCalculatedProbability(shape.getProbability(numberOfItems));
      return shape;
   }

   public static Shape fromNMK(int numberOfItems, int numberOfBits, int numberOfHashFunctions) {
      checkNumberOfItems(numberOfItems);
      checkNumberOfBits(numberOfBits);
      checkNumberOfHashFunctions(numberOfHashFunctions);
      Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
      checkCalculatedProbability(shape.getProbability(numberOfItems));
      return shape;
   }

   public static Shape fromNP(int numberOfItems, double probability) {
      checkNumberOfItems(numberOfItems);
      checkProbability(probability);
      double m = Math.ceil((double)numberOfItems * Math.log(probability) / DENOMINATOR);
      if (m > (double)Integer.MAX_VALUE) {
         throw new IllegalArgumentException("Resulting filter has more than 2147483647 bits: " + m);
      } else {
         int numberOfBits = (int)m;
         int numberOfHashFunctions = calculateNumberOfHashFunctions(numberOfItems, numberOfBits);
         Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
         checkCalculatedProbability(shape.getProbability(numberOfItems));
         return shape;
      }
   }

   public static Shape fromPMK(double probability, int numberOfBits, int numberOfHashFunctions) {
      checkProbability(probability);
      checkNumberOfBits(numberOfBits);
      checkNumberOfHashFunctions(numberOfHashFunctions);
      double n = Math.ceil((double)numberOfBits / ((double)(-numberOfHashFunctions) / Math.log(-Math.expm1(Math.log(probability) / (double)numberOfHashFunctions))));
      Shape shape = new Shape(numberOfHashFunctions, numberOfBits);
      checkCalculatedProbability(shape.getProbability((int)n));
      return shape;
   }

   private Shape(int numberOfHashFunctions, int numberOfBits) {
      this.numberOfHashFunctions = checkNumberOfHashFunctions(numberOfHashFunctions);
      this.numberOfBits = checkNumberOfBits(numberOfBits);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof Shape)) {
         return false;
      } else {
         Shape other = (Shape)obj;
         return this.numberOfBits == other.numberOfBits && this.numberOfHashFunctions == other.numberOfHashFunctions;
      }
   }

   public double estimateMaxN() {
      return (double)this.numberOfBits * LN_2 / (double)this.numberOfHashFunctions;
   }

   public double estimateN(int cardinality) {
      double c = (double)cardinality;
      double m = (double)this.numberOfBits;
      double k = (double)this.numberOfHashFunctions;
      return -(m / k) * Math.log1p(-c / m);
   }

   public int getNumberOfBits() {
      return this.numberOfBits;
   }

   public int getNumberOfHashFunctions() {
      return this.numberOfHashFunctions;
   }

   public double getProbability(int numberOfItems) {
      if (numberOfItems < 0) {
         throw new IllegalArgumentException("Number of items must be greater than or equal to 0: " + numberOfItems);
      } else {
         return numberOfItems == 0 ? (double)0.0F : Math.pow(-Math.expm1((double)-1.0F * (double)this.numberOfHashFunctions * (double)numberOfItems / (double)this.numberOfBits), (double)this.numberOfHashFunctions);
      }
   }

   public int hashCode() {
      return (31 + this.numberOfBits) * 31 + this.numberOfHashFunctions;
   }

   public boolean isSparse(int cardinality) {
      return cardinality <= BitMaps.numberOfBitMaps(this) * 2;
   }

   public String toString() {
      return String.format("Shape[k=%s m=%s]", this.numberOfHashFunctions, this.numberOfBits);
   }

   static {
      DENOMINATOR = -LN_2 * LN_2;
   }
}
