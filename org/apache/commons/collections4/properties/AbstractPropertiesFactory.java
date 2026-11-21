package org.apache.commons.collections4.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public abstract class AbstractPropertiesFactory<T extends Properties> {
   protected AbstractPropertiesFactory() {
   }

   protected abstract T createProperties();

   public T load(ClassLoader classLoader, String name) throws IOException {
      InputStream inputStream = classLoader.getResourceAsStream(name);

      Properties var4;
      try {
         var4 = this.load(inputStream, AbstractPropertiesFactory.PropertyFormat.toPropertyFormat(name));
      } catch (Throwable var7) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return (T)var4;
   }

   public T load(File file) throws FileNotFoundException, IOException {
      return (T)this.load(file.toPath());
   }

   public T load(InputStream inputStream) throws IOException {
      if (inputStream == null) {
         return null;
      } else {
         T properties = this.createProperties();
         properties.load(inputStream);
         return properties;
      }
   }

   public T load(InputStream inputStream, PropertyFormat propertyFormat) throws IOException {
      if (inputStream == null) {
         return null;
      } else {
         T properties = this.createProperties();
         if (propertyFormat == AbstractPropertiesFactory.PropertyFormat.XML) {
            properties.loadFromXML(inputStream);
         } else {
            properties.load(inputStream);
         }

         return properties;
      }
   }

   public T load(Path path) throws IOException {
      InputStream inputStream = Files.newInputStream(path);

      Properties var3;
      try {
         var3 = this.load(inputStream, AbstractPropertiesFactory.PropertyFormat.toPropertyFormat(Objects.toString(path.getFileName(), (String)null)));
      } catch (Throwable var6) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return (T)var3;
   }

   public T load(Reader reader) throws IOException {
      T properties = this.createProperties();
      properties.load(reader);
      return properties;
   }

   public T load(String name) throws IOException {
      return (T)this.load(Paths.get(name));
   }

   public T load(URI uri) throws IOException {
      return (T)this.load(Paths.get(uri));
   }

   public T load(URL url) throws IOException {
      InputStream inputStream = url.openStream();

      Properties var3;
      try {
         var3 = this.load(inputStream, AbstractPropertiesFactory.PropertyFormat.toPropertyFormat(url.getFile()));
      } catch (Throwable var6) {
         if (inputStream != null) {
            try {
               inputStream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (inputStream != null) {
         inputStream.close();
      }

      return (T)var3;
   }

   public static enum PropertyFormat {
      PROPERTIES,
      XML;

      static PropertyFormat toPropertyFormat(String fileName) {
         return ((String)Objects.requireNonNull(fileName, "fileName")).endsWith(".xml") ? XML : PROPERTIES;
      }

      // $FF: synthetic method
      private static PropertyFormat[] $values() {
         return new PropertyFormat[]{PROPERTIES, XML};
      }
   }
}
