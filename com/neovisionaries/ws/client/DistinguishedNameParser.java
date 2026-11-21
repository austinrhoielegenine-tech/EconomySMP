package com.neovisionaries.ws.client;

import javax.security.auth.x500.X500Principal;

final class DistinguishedNameParser {
   private final String dn;
   private final int length;
   private int pos;
   private int beg;
   private int end;
   private int cur;
   private char[] chars;

   public DistinguishedNameParser(X500Principal principal) {
      this.dn = principal.getName("RFC2253");
      this.length = this.dn.length();
   }

   private String nextAT() {
      while(this.pos < this.length && this.chars[this.pos] == ' ') {
         ++this.pos;
      }

      if (this.pos == this.length) {
         return null;
      } else {
         for(this.beg = this.pos++; this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] != ' '; ++this.pos) {
         }

         if (this.pos >= this.length) {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
         } else {
            this.end = this.pos;
            if (this.chars[this.pos] == ' ') {
               while(this.pos < this.length && this.chars[this.pos] != '=' && this.chars[this.pos] == ' ') {
                  ++this.pos;
               }

               if (this.chars[this.pos] != '=' || this.pos == this.length) {
                  throw new IllegalStateException("Unexpected end of DN: " + this.dn);
               }
            }

            ++this.pos;

            while(this.pos < this.length && this.chars[this.pos] == ' ') {
               ++this.pos;
            }

            if (this.end - this.beg > 4 && this.chars[this.beg + 3] == '.' && (this.chars[this.beg] == 'O' || this.chars[this.beg] == 'o') && (this.chars[this.beg + 1] == 'I' || this.chars[this.beg + 1] == 'i') && (this.chars[this.beg + 2] == 'D' || this.chars[this.beg + 2] == 'd')) {
               this.beg += 4;
            }

            return new String(this.chars, this.beg, this.end - this.beg);
         }
      }
   }

   private String quotedAV() {
      ++this.pos;
      this.beg = this.pos;

      for(this.end = this.beg; this.pos != this.length; ++this.end) {
         if (this.chars[this.pos] == '"') {
            ++this.pos;

            while(this.pos < this.length && this.chars[this.pos] == ' ') {
               ++this.pos;
            }

            return new String(this.chars, this.beg, this.end - this.beg);
         }

         if (this.chars[this.pos] == '\\') {
            this.chars[this.end] = this.getEscaped();
         } else {
            this.chars[this.end] = this.chars[this.pos];
         }

         ++this.pos;
      }

      throw new IllegalStateException("Unexpected end of DN: " + this.dn);
   }

   private String hexAV() {
      if (this.pos + 4 >= this.length) {
         throw new IllegalStateException("Unexpected end of DN: " + this.dn);
      } else {
         this.beg = this.pos++;

         label57:
         while(true) {
            if (this.pos == this.length || this.chars[this.pos] == '+' || this.chars[this.pos] == ',' || this.chars[this.pos] == ';') {
               this.end = this.pos;
               break;
            }

            if (this.chars[this.pos] == ' ') {
               this.end = this.pos++;

               while(true) {
                  if (this.pos >= this.length || this.chars[this.pos] != ' ') {
                     break label57;
                  }

                  ++this.pos;
               }
            }

            if (this.chars[this.pos] >= 'A' && this.chars[this.pos] <= 'F') {
               char[] var10000 = this.chars;
               int var10001 = this.pos;
               var10000[var10001] = (char)(var10000[var10001] + 32);
            }

            ++this.pos;
         }

         int hexLen = this.end - this.beg;
         if (hexLen >= 5 && (hexLen & 1) != 0) {
            byte[] encoded = new byte[hexLen / 2];
            int i = 0;

            for(int p = this.beg + 1; i < encoded.length; ++i) {
               encoded[i] = (byte)this.getByte(p);
               p += 2;
            }

            return new String(this.chars, this.beg, hexLen);
         } else {
            throw new IllegalStateException("Unexpected end of DN: " + this.dn);
         }
      }
   }

   private String escapedAV() {
      this.beg = this.pos;
      this.end = this.pos;

      while(this.pos < this.length) {
         switch (this.chars[this.pos]) {
            case ' ':
               this.cur = this.end;
               ++this.pos;

               for(this.chars[this.end++] = ' '; this.pos < this.length && this.chars[this.pos] == ' '; ++this.pos) {
                  this.chars[this.end++] = ' ';
               }

               if (this.pos == this.length || this.chars[this.pos] == ',' || this.chars[this.pos] == '+' || this.chars[this.pos] == ';') {
                  return new String(this.chars, this.beg, this.cur - this.beg);
               }
               break;
            case '+':
            case ',':
            case ';':
               return new String(this.chars, this.beg, this.end - this.beg);
            case '\\':
               this.chars[this.end++] = this.getEscaped();
               ++this.pos;
               break;
            default:
               this.chars[this.end++] = this.chars[this.pos];
               ++this.pos;
         }
      }

      return new String(this.chars, this.beg, this.end - this.beg);
   }

   private char getEscaped() {
      ++this.pos;
      if (this.pos == this.length) {
         throw new IllegalStateException("Unexpected end of DN: " + this.dn);
      } else {
         switch (this.chars[this.pos]) {
            case ' ':
            case '"':
            case '#':
            case '%':
            case '*':
            case '+':
            case ',':
            case ';':
            case '<':
            case '=':
            case '>':
            case '\\':
            case '_':
               return this.chars[this.pos];
            default:
               return this.getUTF8();
         }
      }
   }

   private char getUTF8() {
      int res = this.getByte(this.pos);
      ++this.pos;
      if (res < 128) {
         return (char)res;
      } else if (res >= 192 && res <= 247) {
         int count;
         if (res <= 223) {
            count = 1;
            res &= 31;
         } else if (res <= 239) {
            count = 2;
            res &= 15;
         } else {
            count = 3;
            res &= 7;
         }

         for(int i = 0; i < count; ++i) {
            ++this.pos;
            if (this.pos == this.length || this.chars[this.pos] != '\\') {
               return '?';
            }

            ++this.pos;
            int b = this.getByte(this.pos);
            ++this.pos;
            if ((b & 192) != 128) {
               return '?';
            }

            res = (res << 6) + (b & 63);
         }

         return (char)res;
      } else {
         return '?';
      }
   }

   private int getByte(int position) {
      if (position + 1 >= this.length) {
         throw new IllegalStateException("Malformed DN: " + this.dn);
      } else {
         int b1 = this.chars[position];
         if (b1 >= 48 && b1 <= 57) {
            b1 -= 48;
         } else if (b1 >= 97 && b1 <= 102) {
            b1 -= 87;
         } else {
            if (b1 < 65 || b1 > 70) {
               throw new IllegalStateException("Malformed DN: " + this.dn);
            }

            b1 -= 55;
         }

         int b2 = this.chars[position + 1];
         if (b2 >= 48 && b2 <= 57) {
            b2 -= 48;
         } else if (b2 >= 97 && b2 <= 102) {
            b2 -= 87;
         } else {
            if (b2 < 65 || b2 > 70) {
               throw new IllegalStateException("Malformed DN: " + this.dn);
            }

            b2 -= 55;
         }

         return (b1 << 4) + b2;
      }
   }

   public String findMostSpecific(String attributeType) {
      this.pos = 0;
      this.beg = 0;
      this.end = 0;
      this.cur = 0;
      this.chars = this.dn.toCharArray();
      String attType = this.nextAT();
      if (attType == null) {
         return null;
      } else {
         do {
            String attValue = "";
            if (this.pos == this.length) {
               return null;
            }

            switch (this.chars[this.pos]) {
               case '"':
                  attValue = this.quotedAV();
                  break;
               case '#':
                  attValue = this.hexAV();
               case '+':
               case ',':
               case ';':
                  break;
               default:
                  attValue = this.escapedAV();
            }

            if (attributeType.equalsIgnoreCase(attType)) {
               return attValue;
            }

            if (this.pos >= this.length) {
               return null;
            }

            if (this.chars[this.pos] != ',' && this.chars[this.pos] != ';' && this.chars[this.pos] != '+') {
               throw new IllegalStateException("Malformed DN: " + this.dn);
            }

            ++this.pos;
            attType = this.nextAT();
         } while(attType != null);

         throw new IllegalStateException("Malformed DN: " + this.dn);
      }
   }
}
