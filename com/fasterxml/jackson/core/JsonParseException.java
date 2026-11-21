package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.util.RequestPayload;

public class JsonParseException extends StreamReadException {
   private static final long serialVersionUID = 2L;

   public JsonParseException(String msg) {
      this((JsonParser)null, msg, (JsonLocation)null, (Throwable)null);
   }

   public JsonParseException(JsonParser p, String msg) {
      this(p, msg, _currentLocation(p), (Throwable)null);
   }

   public JsonParseException(JsonParser p, String msg, Throwable rootCause) {
      this(p, msg, _currentLocation(p), rootCause);
   }

   public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
      this(p, msg, loc, (Throwable)null);
   }

   public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable rootCause) {
      super(p, msg, loc, rootCause);
   }

   /** @deprecated */
   @Deprecated
   public JsonParseException(String msg, JsonLocation loc) {
      this((JsonParser)null, msg, loc, (Throwable)null);
   }

   /** @deprecated */
   @Deprecated
   public JsonParseException(String msg, JsonLocation loc, Throwable rootCause) {
      this((JsonParser)null, msg, loc, rootCause);
   }

   public JsonParseException withParser(JsonParser p) {
      this._processor = p;
      return this;
   }

   public JsonParseException withRequestPayload(RequestPayload payload) {
      this._requestPayload = payload;
      return this;
   }

   public JsonParser getProcessor() {
      return super.getProcessor();
   }

   public RequestPayload getRequestPayload() {
      return super.getRequestPayload();
   }

   public String getRequestPayloadAsString() {
      return super.getRequestPayloadAsString();
   }

   public String getMessage() {
      return super.getMessage();
   }
}
