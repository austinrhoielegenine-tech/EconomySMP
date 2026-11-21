package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.InternCache;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.util.Objects;

public class PropertyName implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final String _USE_DEFAULT = "";
   private static final String _NO_NAME = "";
   public static final PropertyName USE_DEFAULT = new PropertyName("", (String)null);
   public static final PropertyName NO_NAME = new PropertyName(new String(""), (String)null);
   protected final String _simpleName;
   protected final String _namespace;
   protected SerializableString _encodedSimple;

   public PropertyName(String simpleName) {
      this(simpleName, (String)null);
   }

   public PropertyName(String simpleName, String namespace) {
      this._simpleName = ClassUtil.nonNullString(simpleName);
      this._namespace = namespace;
   }

   protected Object readResolve() {
      return this._namespace != null || this._simpleName != null && !"".equals(this._simpleName) ? this : USE_DEFAULT;
   }

   public static PropertyName construct(String simpleName) {
      return simpleName != null && !simpleName.isEmpty() ? new PropertyName(InternCache.instance.intern(simpleName), (String)null) : USE_DEFAULT;
   }

   public static PropertyName construct(String simpleName, String ns) {
      if (simpleName == null) {
         simpleName = "";
      }

      return ns == null && simpleName.isEmpty() ? USE_DEFAULT : new PropertyName(InternCache.instance.intern(simpleName), ns);
   }

   public static PropertyName merge(PropertyName name1, PropertyName name2) {
      if (name1 == null) {
         return name2;
      } else if (name2 == null) {
         return name1;
      } else if (name1 == NO_NAME) {
         return name1;
      } else {
         String ns = _nonEmpty(name1._namespace, name2._namespace);
         String simple = _nonEmpty(name1._simpleName, name2._simpleName);
         if (ns == name1._namespace && simple == name1._simpleName) {
            return name1;
         } else {
            return ns == name2._namespace && simple == name2._simpleName ? name2 : construct(simple, ns);
         }
      }
   }

   private static String _nonEmpty(String str1, String str2) {
      if (str1 == null) {
         return str2;
      } else if (str2 == null) {
         return str1;
      } else {
         return str1.isEmpty() ? str2 : str1;
      }
   }

   public PropertyName internSimpleName() {
      if (this._simpleName.isEmpty()) {
         return this;
      } else {
         String interned = InternCache.instance.intern(this._simpleName);
         return interned == this._simpleName ? this : new PropertyName(interned, this._namespace);
      }
   }

   public PropertyName withSimpleName(String simpleName) {
      if (simpleName == null) {
         simpleName = "";
      }

      return simpleName.equals(this._simpleName) ? this : new PropertyName(simpleName, this._namespace);
   }

   public PropertyName withNamespace(String ns) {
      if (ns == null) {
         if (this._namespace == null) {
            return this;
         }
      } else if (ns.equals(this._namespace)) {
         return this;
      }

      return new PropertyName(this._simpleName, ns);
   }

   public String getSimpleName() {
      return this._simpleName;
   }

   public SerializableString simpleAsEncoded(MapperConfig<?> config) {
      SerializableString sstr = this._encodedSimple;
      if (sstr == null) {
         if (config == null) {
            sstr = new SerializedString(this._simpleName);
         } else {
            sstr = config.compileString(this._simpleName);
         }

         this._encodedSimple = sstr;
      }

      return sstr;
   }

   public String getNamespace() {
      return this._namespace;
   }

   public boolean hasSimpleName() {
      return !this._simpleName.isEmpty();
   }

   public boolean hasSimpleName(String str) {
      return this._simpleName.equals(str);
   }

   public boolean hasNamespace() {
      return this._namespace != null;
   }

   public boolean isEmpty() {
      return this._namespace == null && this._simpleName.isEmpty();
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         PropertyName other = (PropertyName)o;
         if (this._simpleName == null) {
            if (other._simpleName != null) {
               return false;
            }
         } else if (!this._simpleName.equals(other._simpleName)) {
            return false;
         }

         if (this._namespace == null) {
            return null == other._namespace;
         } else {
            return this._namespace.equals(other._namespace);
         }
      }
   }

   public int hashCode() {
      return Objects.hashCode(this._simpleName) * 31 + Objects.hashCode(this._namespace);
   }

   public String toString() {
      return this._namespace == null ? this._simpleName : "{" + this._namespace + "}" + this._simpleName;
   }
}
