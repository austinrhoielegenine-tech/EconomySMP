package com.neovisionaries.ws.client;

import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;

final class OkHostnameVerifier implements HostnameVerifier {
   public static final OkHostnameVerifier INSTANCE = new OkHostnameVerifier();
   private static final Pattern VERIFY_AS_IP_ADDRESS = Pattern.compile("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");
   private static final int ALT_DNS_NAME = 2;
   private static final int ALT_IPA_NAME = 7;

   private OkHostnameVerifier() {
   }

   public boolean verify(String host, SSLSession session) {
      try {
         Certificate[] certificates = session.getPeerCertificates();
         return this.verify(host, (X509Certificate)certificates[0]);
      } catch (SSLException var4) {
         return false;
      }
   }

   public boolean verify(String host, X509Certificate certificate) {
      return verifyAsIpAddress(host) ? this.verifyIpAddress(host, certificate) : this.verifyHostName(host, certificate);
   }

   static boolean verifyAsIpAddress(String host) {
      return VERIFY_AS_IP_ADDRESS.matcher(host).matches();
   }

   private boolean verifyIpAddress(String ipAddress, X509Certificate certificate) {
      List<String> altNames = getSubjectAltNames(certificate, 7);
      int i = 0;

      for(int size = altNames.size(); i < size; ++i) {
         if (ipAddress.equalsIgnoreCase((String)altNames.get(i))) {
            return true;
         }
      }

      return false;
   }

   private boolean verifyHostName(String hostName, X509Certificate certificate) {
      hostName = hostName.toLowerCase(Locale.US);
      boolean hasDns = false;
      List<String> altNames = getSubjectAltNames(certificate, 2);
      int i = 0;

      for(int size = altNames.size(); i < size; ++i) {
         hasDns = true;
         if (this.verifyHostName(hostName, (String)altNames.get(i))) {
            return true;
         }
      }

      if (!hasDns) {
         X500Principal principal = certificate.getSubjectX500Principal();
         String cn = (new DistinguishedNameParser(principal)).findMostSpecific("cn");
         if (cn != null) {
            return this.verifyHostName(hostName, cn);
         }
      }

      return false;
   }

   public static List<String> allSubjectAltNames(X509Certificate certificate) {
      List<String> altIpaNames = getSubjectAltNames(certificate, 7);
      List<String> altDnsNames = getSubjectAltNames(certificate, 2);
      List<String> result = new ArrayList(altIpaNames.size() + altDnsNames.size());
      result.addAll(altIpaNames);
      result.addAll(altDnsNames);
      return result;
   }

   private static List<String> getSubjectAltNames(X509Certificate certificate, int type) {
      List<String> result = new ArrayList();

      try {
         Collection<?> subjectAltNames = certificate.getSubjectAlternativeNames();
         if (subjectAltNames == null) {
            return Collections.emptyList();
         } else {
            for(Object subjectAltName : subjectAltNames) {
               List<?> entry = (List)subjectAltName;
               if (entry != null && entry.size() >= 2) {
                  Integer altNameType = (Integer)entry.get(0);
                  if (altNameType != null && altNameType == type) {
                     String altName = (String)entry.get(1);
                     if (altName != null) {
                        result.add(altName);
                     }
                  }
               }
            }

            return result;
         }
      } catch (CertificateParsingException var9) {
         return Collections.emptyList();
      }
   }

   private boolean verifyHostName(String hostName, String pattern) {
      if (hostName != null && hostName.length() != 0 && !hostName.startsWith(".") && !hostName.endsWith("..")) {
         if (pattern != null && pattern.length() != 0 && !pattern.startsWith(".") && !pattern.endsWith("..")) {
            if (!hostName.endsWith(".")) {
               hostName = hostName + '.';
            }

            if (!pattern.endsWith(".")) {
               pattern = pattern + '.';
            }

            pattern = pattern.toLowerCase(Locale.US);
            if (!pattern.contains("*")) {
               return hostName.equals(pattern);
            } else if (pattern.startsWith("*.") && pattern.indexOf(42, 1) == -1) {
               if (hostName.length() < pattern.length()) {
                  return false;
               } else if ("*.".equals(pattern)) {
                  return false;
               } else {
                  String suffix = pattern.substring(1);
                  if (!hostName.endsWith(suffix)) {
                     return false;
                  } else {
                     int suffixStartIndexInHostName = hostName.length() - suffix.length();
                     return suffixStartIndexInHostName <= 0 || hostName.lastIndexOf(46, suffixStartIndexInHostName - 1) == -1;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
