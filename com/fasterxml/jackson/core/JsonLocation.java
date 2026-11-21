package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.io.ContentReference;
import java.io.Serializable;

public class JsonLocation implements Serializable {
   private static final long serialVersionUID = 2L;
   /** @deprecated */
   @Deprecated
   public static final int MAX_CONTENT_SNIPPET = 500;
   public static final JsonLocation NA = new JsonLocation(ContentReference.unknown(), -1L, -1L, -1, -1);
   protected final long _totalBytes;
   protected final long _totalChars;
   protected final int _lineNr;
   protected final int _columnNr;
   protected final ContentReference _contentReference;
   protected transient String _sourceDescription;

   public JsonLocation(ContentReference contentRef, long totalChars, int lineNr, int colNr) {
      this(contentRef, -1L, totalChars, lineNr, colNr);
   }

   public JsonLocation(ContentReference contentRef, long totalBytes, long totalChars, int lineNr, int columnNr) {
      if (contentRef == null) {
         contentRef = ContentReference.unknown();
      }

      this._contentReference = contentRef;
      this._totalBytes = totalBytes;
      this._totalChars = totalChars;
      this._lineNr = lineNr;
      this._columnNr = columnNr;
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation(Object srcRef, long totalChars, int lineNr, int columnNr) {
      this(_wrap(srcRef), totalChars, lineNr, columnNr);
   }

   /** @deprecated */
   @Deprecated
   public JsonLocation(Object srcRef, long totalBytes, long totalChars, int lineNr, int columnNr) {
      this(_wrap(srcRef), totalBytes, totalChars, lineNr, columnNr);
   }

   protected static ContentReference _wrap(Object srcRef) {
      return srcRef instanceof ContentReference ? (ContentReference)srcRef : ContentReference.construct(false, srcRef, ErrorReportConfiguration.defaults());
   }

   public ContentReference contentReference() {
      return this._contentReference;
   }

   /** @deprecated */
   @Deprecated
   public Object getSourceRef() {
      return this._contentReference.getRawContent();
   }

   public int getLineNr() {
      return this._lineNr;
   }

   public int getColumnNr() {
      return this._columnNr;
   }

   public long getCharOffset() {
      return this._totalChars;
   }

   public long getByteOffset() {
      return this._totalBytes;
   }

   public String sourceDescription() {
      if (this._sourceDescription == null) {
         this._sourceDescription = this._contentReference.buildSourceDescription();
      }

      return this._sourceDescription;
   }

   public String offsetDescription() {
      return this.appendOffsetDescription(new StringBuilder(40)).toString();
   }

   public StringBuilder appendOffsetDescription(StringBuilder sb) {
      if (this._contentReference.hasTextualContent()) {
         sb.append("line: ");
         if (this._lineNr >= 0) {
            sb.append(this._lineNr);
         } else {
            sb.append("UNKNOWN");
         }

         sb.append(", column: ");
         if (this._columnNr >= 0) {
            sb.append(this._columnNr);
         } else {
            sb.append("UNKNOWN");
         }
      } else if (this._lineNr > 0) {
         sb.append("line: ").append(this._lineNr);
         if (this._columnNr > 0) {
            sb.append(", column: ");
            sb.append(this._columnNr);
         }
      } else {
         sb.append("byte offset: #");
         if (this._totalBytes >= 0L) {
            sb.append(this._totalBytes);
         } else {
            sb.append("UNKNOWN");
         }
      }

      return sb;
   }

   public int hashCode() {
      int hash = this._contentReference == null ? 1 : 2;
      hash ^= this._lineNr;
      hash += this._columnNr;
      hash ^= (int)this._totalChars;
      hash += (int)this._totalBytes;
      return hash;
   }

   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (other == null) {
         return false;
      } else if (!(other instanceof JsonLocation)) {
         return false;
      } else {
         JsonLocation otherLoc = (JsonLocation)other;
         if (this._contentReference == null) {
            if (otherLoc._contentReference != null) {
               return false;
            }
         } else if (!this._contentReference.equals(otherLoc._contentReference)) {
            return false;
         }

         return this._lineNr == otherLoc._lineNr && this._columnNr == otherLoc._columnNr && this._totalChars == otherLoc._totalChars && this._totalBytes == otherLoc._totalBytes;
      }
   }

   public String toString() {
      String srcDesc = this.sourceDescription();
      StringBuilder sb = (new StringBuilder(40 + srcDesc.length())).append("[Source: ").append(srcDesc).append("; ");
      return this.appendOffsetDescription(sb).append(']').toString();
   }
}
