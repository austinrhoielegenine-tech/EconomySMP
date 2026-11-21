package gnu.trove;

public class Version {
   public static void main(String[] args) {
      System.out.println(getVersion());
   }

   public static String getVersion() {
      String version = Version.class.getPackage().getImplementationVersion();
      return version != null ? "trove4j version " + version : "Sorry no Implementation-Version manifest attribute available";
   }
}
