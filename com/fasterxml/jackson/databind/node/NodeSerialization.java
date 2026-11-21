package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

class NodeSerialization implements Serializable, Externalizable {
   protected static final int LONGEST_EAGER_ALLOC = 100000;
   private static final long serialVersionUID = 1L;
   public byte[] json;

   public NodeSerialization() {
   }

   public NodeSerialization(byte[] b) {
      this.json = b;
   }

   protected Object readResolve() {
      try {
         return InternalNodeMapper.bytesToNode(this.json);
      } catch (IOException e) {
         throw new IllegalArgumentException("Failed to JDK deserialize `JsonNode` value: " + e.getMessage(), e);
      }
   }

   public static NodeSerialization from(Object o) {
      try {
         return new NodeSerialization(InternalNodeMapper.valueToBytes(o));
      } catch (IOException e) {
         throw new IllegalArgumentException("Failed to JDK serialize `" + o.getClass().getSimpleName() + "` value: " + e.getMessage(), e);
      }
   }

   public void writeExternal(ObjectOutput out) throws IOException {
      out.writeInt(this.json.length);
      out.write(this.json);
   }

   public void readExternal(ObjectInput in) throws IOException {
      int len = in.readInt();
      this.json = this._read(in, len);
   }

   private byte[] _read(ObjectInput in, int expLen) throws IOException {
      if (expLen <= 100000) {
         byte[] result = new byte[expLen];
         in.readFully(result, 0, expLen);
         return result;
      } else {
         ByteArrayBuilder bb = new ByteArrayBuilder(100000);
         Throwable var4 = null;

         try {
            byte[] buffer = bb.resetAndGetFirstSegment();
            int outOffset = 0;

            while(true) {
               int toRead = Math.min(buffer.length - outOffset, expLen);
               in.readFully(buffer, 0, toRead);
               expLen -= toRead;
               outOffset += toRead;
               if (expLen == 0) {
                  byte[] var8 = bb.completeAndCoalesce(outOffset);
                  return var8;
               }

               if (outOffset == buffer.length) {
                  buffer = bb.finishCurrentSegment();
                  outOffset = 0;
               }
            }
         } catch (Throwable var17) {
            var4 = var17;
            throw var17;
         } finally {
            if (bb != null) {
               if (var4 != null) {
                  try {
                     bb.close();
                  } catch (Throwable var16) {
                     var4.addSuppressed(var16);
                  }
               } else {
                  bb.close();
               }
            }

         }
      }
   }
}
