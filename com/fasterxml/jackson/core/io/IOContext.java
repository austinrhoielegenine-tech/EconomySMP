package com.fasterxml.jackson.core.io;

import com.fasterxml.jackson.core.ErrorReportConfiguration;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.core.util.ReadConstrainedTextBuffer;
import com.fasterxml.jackson.core.util.TextBuffer;

public class IOContext implements AutoCloseable {
   protected final ContentReference _contentReference;
   /** @deprecated */
   @Deprecated
   protected final Object _sourceRef;
   protected JsonEncoding _encoding;
   protected final boolean _managedResource;
   protected final BufferRecycler _bufferRecycler;
   protected boolean _releaseRecycler;
   protected final StreamReadConstraints _streamReadConstraints;
   protected final StreamWriteConstraints _streamWriteConstraints;
   protected final ErrorReportConfiguration _errorReportConfiguration;
   protected byte[] _readIOBuffer;
   protected byte[] _writeEncodingBuffer;
   protected byte[] _base64Buffer;
   protected char[] _tokenCBuffer;
   protected char[] _concatCBuffer;
   protected char[] _nameCopyBuffer;
   private boolean _closed;

   public IOContext(StreamReadConstraints src, StreamWriteConstraints swc, ErrorReportConfiguration erc, BufferRecycler br, ContentReference contentRef, boolean managedResource) {
      this._releaseRecycler = true;
      this._closed = false;
      this._streamReadConstraints = src;
      this._streamWriteConstraints = swc;
      this._errorReportConfiguration = erc;
      this._bufferRecycler = br;
      this._contentReference = contentRef;
      this._sourceRef = contentRef.getRawContent();
      this._managedResource = managedResource;
   }

   /** @deprecated */
   @Deprecated
   public IOContext(StreamReadConstraints src, BufferRecycler br, ContentReference contentRef, boolean managedResource) {
      this(src, StreamWriteConstraints.defaults(), ErrorReportConfiguration.defaults(), br, contentRef, managedResource);
   }

   /** @deprecated */
   @Deprecated
   public IOContext(BufferRecycler br, ContentReference contentRef, boolean managedResource) {
      this(StreamReadConstraints.defaults(), StreamWriteConstraints.defaults(), ErrorReportConfiguration.defaults(), br, contentRef, managedResource);
   }

   /** @deprecated */
   @Deprecated
   public IOContext(BufferRecycler br, Object rawContent, boolean managedResource) {
      this(br, ContentReference.rawReference(rawContent), managedResource);
   }

   public IOContext markBufferRecyclerReleased() {
      this._releaseRecycler = false;
      return this;
   }

   public StreamReadConstraints streamReadConstraints() {
      return this._streamReadConstraints;
   }

   public StreamWriteConstraints streamWriteConstraints() {
      return this._streamWriteConstraints;
   }

   public ErrorReportConfiguration errorReportConfiguration() {
      return this._errorReportConfiguration;
   }

   public void setEncoding(JsonEncoding enc) {
      this._encoding = enc;
   }

   public IOContext withEncoding(JsonEncoding enc) {
      this._encoding = enc;
      return this;
   }

   public JsonEncoding getEncoding() {
      return this._encoding;
   }

   public boolean isResourceManaged() {
      return this._managedResource;
   }

   public ContentReference contentReference() {
      return this._contentReference;
   }

   /** @deprecated */
   @Deprecated
   public Object getSourceReference() {
      return this._sourceRef;
   }

   public BufferRecycler bufferRecycler() {
      return this._bufferRecycler;
   }

   public TextBuffer constructTextBuffer() {
      return new TextBuffer(this._bufferRecycler);
   }

   public TextBuffer constructReadConstrainedTextBuffer() {
      return new ReadConstrainedTextBuffer(this._streamReadConstraints, this._bufferRecycler);
   }

   public byte[] allocReadIOBuffer() {
      this._verifyAlloc(this._readIOBuffer);
      return this._readIOBuffer = this._bufferRecycler.allocByteBuffer(0);
   }

   public byte[] allocReadIOBuffer(int minSize) {
      this._verifyAlloc(this._readIOBuffer);
      return this._readIOBuffer = this._bufferRecycler.allocByteBuffer(0, minSize);
   }

   public byte[] allocWriteEncodingBuffer() {
      this._verifyAlloc(this._writeEncodingBuffer);
      return this._writeEncodingBuffer = this._bufferRecycler.allocByteBuffer(1);
   }

   public byte[] allocWriteEncodingBuffer(int minSize) {
      this._verifyAlloc(this._writeEncodingBuffer);
      return this._writeEncodingBuffer = this._bufferRecycler.allocByteBuffer(1, minSize);
   }

   public byte[] allocBase64Buffer() {
      this._verifyAlloc(this._base64Buffer);
      return this._base64Buffer = this._bufferRecycler.allocByteBuffer(3);
   }

   public byte[] allocBase64Buffer(int minSize) {
      this._verifyAlloc(this._base64Buffer);
      return this._base64Buffer = this._bufferRecycler.allocByteBuffer(3, minSize);
   }

   public char[] allocTokenBuffer() {
      this._verifyAlloc(this._tokenCBuffer);
      return this._tokenCBuffer = this._bufferRecycler.allocCharBuffer(0);
   }

   public char[] allocTokenBuffer(int minSize) {
      this._verifyAlloc(this._tokenCBuffer);
      return this._tokenCBuffer = this._bufferRecycler.allocCharBuffer(0, minSize);
   }

   public char[] allocConcatBuffer() {
      this._verifyAlloc(this._concatCBuffer);
      return this._concatCBuffer = this._bufferRecycler.allocCharBuffer(1);
   }

   public char[] allocNameCopyBuffer(int minSize) {
      this._verifyAlloc(this._nameCopyBuffer);
      return this._nameCopyBuffer = this._bufferRecycler.allocCharBuffer(3, minSize);
   }

   public void releaseReadIOBuffer(byte[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._readIOBuffer);
         this._readIOBuffer = null;
         this._bufferRecycler.releaseByteBuffer(0, buf);
      }

   }

   public void releaseWriteEncodingBuffer(byte[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._writeEncodingBuffer);
         this._writeEncodingBuffer = null;
         this._bufferRecycler.releaseByteBuffer(1, buf);
      }

   }

   public void releaseBase64Buffer(byte[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._base64Buffer);
         this._base64Buffer = null;
         this._bufferRecycler.releaseByteBuffer(3, buf);
      }

   }

   public void releaseTokenBuffer(char[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._tokenCBuffer);
         this._tokenCBuffer = null;
         this._bufferRecycler.releaseCharBuffer(0, buf);
      }

   }

   public void releaseConcatBuffer(char[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._concatCBuffer);
         this._concatCBuffer = null;
         this._bufferRecycler.releaseCharBuffer(1, buf);
      }

   }

   public void releaseNameCopyBuffer(char[] buf) {
      if (buf != null) {
         this._verifyRelease(buf, this._nameCopyBuffer);
         this._nameCopyBuffer = null;
         this._bufferRecycler.releaseCharBuffer(3, buf);
      }

   }

   protected final void _verifyAlloc(Object buffer) {
      if (buffer != null) {
         throw new IllegalStateException("Trying to call same allocXxx() method second time");
      }
   }

   protected final void _verifyRelease(byte[] toRelease, byte[] src) {
      if (toRelease != src && toRelease.length < src.length) {
         throw this.wrongBuf();
      }
   }

   protected final void _verifyRelease(char[] toRelease, char[] src) {
      if (toRelease != src && toRelease.length < src.length) {
         throw this.wrongBuf();
      }
   }

   private IllegalArgumentException wrongBuf() {
      return new IllegalArgumentException("Trying to release buffer smaller than original");
   }

   public void close() {
      if (!this._closed) {
         this._closed = true;
         if (this._releaseRecycler) {
            this._releaseRecycler = false;
            this._bufferRecycler.releaseToPool();
         }
      }

   }
}
