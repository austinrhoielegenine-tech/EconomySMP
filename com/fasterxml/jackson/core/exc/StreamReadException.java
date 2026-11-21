package com.fasterxml.jackson.core.exc;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.RequestPayload;

public abstract class StreamReadException extends JsonProcessingException {
   static final long serialVersionUID = 2L;
   protected transient JsonParser _processor;
   protected RequestPayload _requestPayload;

   protected StreamReadException(String msg) {
      this((JsonParser)null, msg, (JsonLocation)null, (Throwable)null);
   }

   protected StreamReadException(JsonParser p, String msg) {
      this(p, msg, _currentLocation(p), (Throwable)null);
   }

   protected StreamReadException(JsonParser p, String msg, Throwable rootCause) {
      this(p, msg, _currentLocation(p), rootCause);
   }

   protected StreamReadException(JsonParser p, String msg, JsonLocation loc) {
      this(p, msg, loc, (Throwable)null);
   }

   protected StreamReadException(String msg, JsonLocation loc, Throwable rootCause) {
      this((JsonParser)null, msg, loc, rootCause);
   }

   protected StreamReadException(JsonParser p, String msg, JsonLocation loc, Throwable rootCause) {
      super(msg, loc, rootCause);
      this._processor = p;
   }

   public abstract StreamReadException withParser(JsonParser var1);

   public abstract StreamReadException withRequestPayload(RequestPayload var1);

   public JsonParser getProcessor() {
      return this._processor;
   }

   public RequestPayload getRequestPayload() {
      return this._requestPayload;
   }

   public String getRequestPayloadAsString() {
      return this._requestPayload != null ? this._requestPayload.toString() : null;
   }

   public String getMessage() {
      String msg = super.getMessage();
      if (this._requestPayload != null) {
         msg = msg + "\nRequest payload : " + this._requestPayload.toString();
      }

      return msg;
   }

   protected static JsonLocation _currentLocation(JsonParser p) {
      return p == null ? null : p.currentLocation();
   }
}
