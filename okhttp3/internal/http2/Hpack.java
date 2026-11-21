package okhttp3.internal.http2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002\u0019\u001aB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00050\u0012H\u0002J\u000e\u0010\u0017\u001a\u00020\u00132\u0006\u0010\u0018\u001a\u00020\u0013R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0019\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u001d\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00050\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006\u001b"},
   d2 = {"Lokhttp3/internal/http2/Hpack;", "", "<init>", "()V", "PREFIX_4_BITS", "", "PREFIX_5_BITS", "PREFIX_6_BITS", "PREFIX_7_BITS", "SETTINGS_HEADER_TABLE_SIZE", "SETTINGS_HEADER_TABLE_SIZE_LIMIT", "STATIC_HEADER_TABLE", "", "Lokhttp3/internal/http2/Header;", "getSTATIC_HEADER_TABLE", "()[Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "NAME_TO_FIRST_INDEX", "", "Lokio/ByteString;", "getNAME_TO_FIRST_INDEX", "()Ljava/util/Map;", "nameToFirstIndex", "checkLowercase", "name", "Reader", "Writer", "okhttp"}
)
@SourceDebugExtension({"SMAP\nHpack.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Hpack.kt\nokhttp3/internal/http2/Hpack\n+ 2 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,647:1\n250#2:648\n*S KotlinDebug\n*F\n+ 1 Hpack.kt\nokhttp3/internal/http2/Hpack\n*L\n398#1:648\n*E\n"})
public final class Hpack {
   @NotNull
   public static final Hpack INSTANCE = new Hpack();
   private static final int PREFIX_4_BITS = 15;
   private static final int PREFIX_5_BITS = 31;
   private static final int PREFIX_6_BITS = 63;
   private static final int PREFIX_7_BITS = 127;
   private static final int SETTINGS_HEADER_TABLE_SIZE = 4096;
   private static final int SETTINGS_HEADER_TABLE_SIZE_LIMIT = 16384;
   @NotNull
   private static final Header[] STATIC_HEADER_TABLE;
   @NotNull
   private static final Map<ByteString, Integer> NAME_TO_FIRST_INDEX;

   private Hpack() {
   }

   @NotNull
   public final Header[] getSTATIC_HEADER_TABLE() {
      return STATIC_HEADER_TABLE;
   }

   @NotNull
   public final Map<ByteString, Integer> getNAME_TO_FIRST_INDEX() {
      return NAME_TO_FIRST_INDEX;
   }

   private final Map<ByteString, Integer> nameToFirstIndex() {
      LinkedHashMap result = new LinkedHashMap(STATIC_HEADER_TABLE.length, 1.0F);
      int i = 0;

      for(int var3 = STATIC_HEADER_TABLE.length; i < var3; ++i) {
         if (!result.containsKey(STATIC_HEADER_TABLE[i].name)) {
            ((Map)result).put(STATIC_HEADER_TABLE[i].name, i);
         }
      }

      Map $this$unmodifiable$iv = (Map)result;
      int $i$f$unmodifiable = 0;
      Map var10000 = Collections.unmodifiableMap($this$unmodifiable$iv);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableMap(...)");
      return var10000;
   }

   @NotNull
   public final ByteString checkLowercase(@NotNull ByteString name) throws IOException {
      Intrinsics.checkNotNullParameter(name, "name");
      int i = 0;

      for(int var3 = name.size(); i < var3; ++i) {
         byte var4 = name.getByte(i);
         if (65 <= var4 ? var4 < 91 : false) {
            throw new IOException("PROTOCOL_ERROR response malformed: mixed case name: " + name.utf8());
         }
      }

      return name;
   }

   static {
      Header[] var0 = new Header[]{new Header(Header.TARGET_AUTHORITY, ""), new Header(Header.TARGET_METHOD, "GET"), new Header(Header.TARGET_METHOD, "POST"), new Header(Header.TARGET_PATH, "/"), new Header(Header.TARGET_PATH, "/index.html"), new Header(Header.TARGET_SCHEME, "http"), new Header(Header.TARGET_SCHEME, "https"), new Header(Header.RESPONSE_STATUS, "200"), new Header(Header.RESPONSE_STATUS, "204"), new Header(Header.RESPONSE_STATUS, "206"), new Header(Header.RESPONSE_STATUS, "304"), new Header(Header.RESPONSE_STATUS, "400"), new Header(Header.RESPONSE_STATUS, "404"), new Header(Header.RESPONSE_STATUS, "500"), new Header("accept-charset", ""), new Header("accept-encoding", "gzip, deflate"), new Header("accept-language", ""), new Header("accept-ranges", ""), new Header("accept", ""), new Header("access-control-allow-origin", ""), new Header("age", ""), new Header("allow", ""), new Header("authorization", ""), new Header("cache-control", ""), new Header("content-disposition", ""), new Header("content-encoding", ""), new Header("content-language", ""), new Header("content-length", ""), new Header("content-location", ""), new Header("content-range", ""), new Header("content-type", ""), new Header("cookie", ""), new Header("date", ""), new Header("etag", ""), new Header("expect", ""), new Header("expires", ""), new Header("from", ""), new Header("host", ""), new Header("if-match", ""), new Header("if-modified-since", ""), new Header("if-none-match", ""), new Header("if-range", ""), new Header("if-unmodified-since", ""), new Header("last-modified", ""), new Header("link", ""), new Header("location", ""), new Header("max-forwards", ""), new Header("proxy-authenticate", ""), new Header("proxy-authorization", ""), new Header("range", ""), new Header("referer", ""), new Header("refresh", ""), new Header("retry-after", ""), new Header("server", ""), new Header("set-cookie", ""), new Header("strict-transport-security", ""), new Header("transfer-encoding", ""), new Header("user-agent", ""), new Header("vary", ""), new Header("via", ""), new Header("www-authenticate", "")};
      STATIC_HEADER_TABLE = var0;
      NAME_TO_FIRST_INDEX = INSTANCE.nameToFirstIndex();
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0005\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\u0018\u00002\u00020\u0001B#\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0014J\u0006\u0010\u0006\u001a\u00020\u0005J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0016H\u0002J\u0010\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0005H\u0002J\u0006\u0010\u001a\u001a\u00020\u0016J\u0010\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u0005H\u0002J\u0010\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u0005H\u0002J\u0010\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u0005H\u0002J\b\u0010\u001f\u001a\u00020\u0016H\u0002J\u0010\u0010 \u001a\u00020\u00162\u0006\u0010!\u001a\u00020\u0005H\u0002J\b\u0010\"\u001a\u00020\u0016H\u0002J\u0010\u0010#\u001a\u00020$2\u0006\u0010\u001c\u001a\u00020\u0005H\u0002J\u0010\u0010%\u001a\u00020&2\u0006\u0010\u001c\u001a\u00020\u0005H\u0002J\u0018\u0010'\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010(\u001a\u00020\u000bH\u0002J\b\u0010)\u001a\u00020\u0005H\u0002J\u0016\u0010*\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020\u0005J\u0006\u0010-\u001a\u00020$R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u000e8\u0006@\u0006X\u0087\u000e¢\u0006\u0004\n\u0002\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0011\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0012\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006."},
      d2 = {"Lokhttp3/internal/http2/Hpack$Reader;", "", "source", "Lokio/Source;", "headerTableSizeSetting", "", "maxDynamicTableByteCount", "<init>", "(Lokio/Source;II)V", "headerList", "", "Lokhttp3/internal/http2/Header;", "Lokio/BufferedSource;", "dynamicTable", "", "[Lokhttp3/internal/http2/Header;", "nextHeaderIndex", "headerCount", "dynamicTableByteCount", "getAndResetHeaderList", "", "adjustDynamicTableByteCount", "", "clearDynamicTable", "evictToRecoverBytes", "bytesToRecover", "readHeaders", "readIndexedHeader", "index", "dynamicTableIndex", "readLiteralHeaderWithoutIndexingIndexedName", "readLiteralHeaderWithoutIndexingNewName", "readLiteralHeaderWithIncrementalIndexingIndexedName", "nameIndex", "readLiteralHeaderWithIncrementalIndexingNewName", "getName", "Lokio/ByteString;", "isStaticHeader", "", "insertIntoDynamicTable", "entry", "readByte", "readInt", "firstByte", "prefixMask", "readByteString", "okhttp"}
   )
   public static final class Reader {
      private final int headerTableSizeSetting;
      private int maxDynamicTableByteCount;
      @NotNull
      private final List<Header> headerList;
      @NotNull
      private final BufferedSource source;
      @JvmField
      @NotNull
      public Header[] dynamicTable;
      private int nextHeaderIndex;
      @JvmField
      public int headerCount;
      @JvmField
      public int dynamicTableByteCount;

      @JvmOverloads
      public Reader(@NotNull Source source, int headerTableSizeSetting, int maxDynamicTableByteCount) {
         Intrinsics.checkNotNullParameter(source, "source");
         super();
         this.headerTableSizeSetting = headerTableSizeSetting;
         this.maxDynamicTableByteCount = maxDynamicTableByteCount;
         this.headerList = (List)(new ArrayList());
         this.source = Okio.buffer(source);
         this.dynamicTable = new Header[8];
         this.nextHeaderIndex = this.dynamicTable.length - 1;
      }

      // $FF: synthetic method
      public Reader(Source var1, int var2, int var3, int var4, DefaultConstructorMarker var5) {
         if ((var4 & 4) != 0) {
            var3 = var2;
         }

         this(var1, var2, var3);
      }

      @NotNull
      public final List<Header> getAndResetHeaderList() {
         List result = CollectionsKt.toList((Iterable)this.headerList);
         this.headerList.clear();
         return result;
      }

      public final int maxDynamicTableByteCount() {
         return this.maxDynamicTableByteCount;
      }

      private final void adjustDynamicTableByteCount() {
         if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
            if (this.maxDynamicTableByteCount == 0) {
               this.clearDynamicTable();
            } else {
               this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
            }
         }

      }

      private final void clearDynamicTable() {
         ArraysKt.fill$default(this.dynamicTable, (Object)null, 0, 0, 6, (Object)null);
         this.nextHeaderIndex = this.dynamicTable.length - 1;
         this.headerCount = 0;
         this.dynamicTableByteCount = 0;
      }

      private final int evictToRecoverBytes(int bytesToRecover) {
         int bytesToRecover = bytesToRecover;
         int entriesToEvict = 0;
         if (bytesToRecover > 0) {
            for(int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecover > 0; --j) {
               Header var10000 = this.dynamicTable[j];
               Intrinsics.checkNotNull(var10000);
               Header toEvict = var10000;
               bytesToRecover -= toEvict.hpackSize;
               this.dynamicTableByteCount -= toEvict.hpackSize;
               int var6 = this.headerCount;
               this.headerCount = var6 + -1;
               ++entriesToEvict;
            }

            System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + entriesToEvict, this.headerCount);
            this.nextHeaderIndex += entriesToEvict;
         }

         return entriesToEvict;
      }

      public final void readHeaders() throws IOException {
         while(!this.source.exhausted()) {
            int b = _UtilCommonKt.and((byte)this.source.readByte(), 255);
            if (b == 128) {
               throw new IOException("index == 0");
            }

            if ((b & 128) == 128) {
               int index = this.readInt(b, 127);
               this.readIndexedHeader(index - 1);
            } else if (b == 64) {
               this.readLiteralHeaderWithIncrementalIndexingNewName();
            } else if ((b & 64) == 64) {
               int index = this.readInt(b, 63);
               this.readLiteralHeaderWithIncrementalIndexingIndexedName(index - 1);
            } else if ((b & 32) == 32) {
               this.maxDynamicTableByteCount = this.readInt(b, 31);
               if (this.maxDynamicTableByteCount < 0 || this.maxDynamicTableByteCount > this.headerTableSizeSetting) {
                  throw new IOException("Invalid dynamic table size update " + this.maxDynamicTableByteCount);
               }

               this.adjustDynamicTableByteCount();
            } else if (b != 16 && b != 0) {
               int index = this.readInt(b, 15);
               this.readLiteralHeaderWithoutIndexingIndexedName(index - 1);
            } else {
               this.readLiteralHeaderWithoutIndexingNewName();
            }
         }

      }

      private final void readIndexedHeader(int index) throws IOException {
         if (this.isStaticHeader(index)) {
            Header staticEntry = Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index];
            this.headerList.add(staticEntry);
         } else {
            int dynamicTableIndex = this.dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex < 0 || dynamicTableIndex >= this.dynamicTable.length) {
               throw new IOException("Header index too large " + (index + 1));
            }

            Collection var10000 = (Collection)this.headerList;
            Header var10001 = this.dynamicTable[dynamicTableIndex];
            Intrinsics.checkNotNull(var10001);
            var10000.add(var10001);
         }

      }

      private final int dynamicTableIndex(int index) {
         return this.nextHeaderIndex + 1 + index;
      }

      private final void readLiteralHeaderWithoutIndexingIndexedName(int index) throws IOException {
         ByteString name = this.getName(index);
         ByteString value = this.readByteString();
         this.headerList.add(new Header(name, value));
      }

      private final void readLiteralHeaderWithoutIndexingNewName() throws IOException {
         ByteString name = Hpack.INSTANCE.checkLowercase(this.readByteString());
         ByteString value = this.readByteString();
         this.headerList.add(new Header(name, value));
      }

      private final void readLiteralHeaderWithIncrementalIndexingIndexedName(int nameIndex) throws IOException {
         ByteString name = this.getName(nameIndex);
         ByteString value = this.readByteString();
         this.insertIntoDynamicTable(-1, new Header(name, value));
      }

      private final void readLiteralHeaderWithIncrementalIndexingNewName() throws IOException {
         ByteString name = Hpack.INSTANCE.checkLowercase(this.readByteString());
         ByteString value = this.readByteString();
         this.insertIntoDynamicTable(-1, new Header(name, value));
      }

      private final ByteString getName(int index) throws IOException {
         ByteString var10000;
         if (this.isStaticHeader(index)) {
            var10000 = Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[index].name;
         } else {
            int dynamicTableIndex = this.dynamicTableIndex(index - Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length);
            if (dynamicTableIndex < 0 || dynamicTableIndex >= this.dynamicTable.length) {
               throw new IOException("Header index too large " + (index + 1));
            }

            Header var3 = this.dynamicTable[dynamicTableIndex];
            Intrinsics.checkNotNull(var3);
            var10000 = var3.name;
         }

         return var10000;
      }

      private final boolean isStaticHeader(int index) {
         return index >= 0 && index <= Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length - 1;
      }

      private final void insertIntoDynamicTable(int index, Header entry) {
         this.headerList.add(entry);
         int delta = entry.hpackSize;
         if (index != -1) {
            Header var10001 = this.dynamicTable[this.dynamicTableIndex(index)];
            Intrinsics.checkNotNull(var10001);
            delta -= var10001.hpackSize;
         }

         if (delta > this.maxDynamicTableByteCount) {
            this.clearDynamicTable();
         } else {
            int bytesToRecover = this.dynamicTableByteCount + delta - this.maxDynamicTableByteCount;
            int entriesEvicted = this.evictToRecoverBytes(bytesToRecover);
            if (index == -1) {
               if (this.headerCount + 1 > this.dynamicTable.length) {
                  Header[] doubled = new Header[this.dynamicTable.length * 2];
                  System.arraycopy(this.dynamicTable, 0, doubled, this.dynamicTable.length, this.dynamicTable.length);
                  this.nextHeaderIndex = this.dynamicTable.length - 1;
                  this.dynamicTable = doubled;
               }

               int var8 = this.nextHeaderIndex;
               this.nextHeaderIndex = var8 + -1;
               this.dynamicTable[var8] = entry;
               var8 = this.headerCount++;
            } else {
               int index = index + this.dynamicTableIndex(index) + entriesEvicted;
               this.dynamicTable[index] = entry;
            }

            this.dynamicTableByteCount += delta;
         }
      }

      private final int readByte() throws IOException {
         return _UtilCommonKt.and((byte)this.source.readByte(), 255);
      }

      public final int readInt(int firstByte, int prefixMask) throws IOException {
         int prefix = firstByte & prefixMask;
         if (prefix < prefixMask) {
            return prefix;
         } else {
            int result = prefixMask;
            int shift = 0;

            while(true) {
               int b = this.readByte();
               if ((b & 128) == 0) {
                  result += b << shift;
                  return result;
               }

               result += (b & 127) << shift;
               shift += 7;
            }
         }
      }

      @NotNull
      public final ByteString readByteString() throws IOException {
         int firstByte = this.readByte();
         boolean huffmanDecode = (firstByte & 128) == 128;
         long length = (long)this.readInt(firstByte, 127);
         ByteString var10000;
         if (huffmanDecode) {
            Buffer decodeBuffer = new Buffer();
            Huffman.INSTANCE.decode(this.source, length, decodeBuffer);
            var10000 = decodeBuffer.readByteString();
         } else {
            var10000 = this.source.readByteString(length);
         }

         return var10000;
      }

      @JvmOverloads
      public Reader(@NotNull Source source, int headerTableSizeSetting) {
         Intrinsics.checkNotNullParameter(source, "source");
         this(source, headerTableSizeSetting, 0, 4, (DefaultConstructorMarker)null);
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B%\b\u0007\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0014\u001a\u00020\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0017\u001a\u00020\u0003H\u0002J\u0010\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u000fH\u0002J\u0014\u0010\u001a\u001a\u00020\u00152\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000f0\u001cJ\u001e\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u00032\u0006\u0010 \u001a\u00020\u0003J\u000e\u0010!\u001a\u00020\u00152\u0006\u0010\"\u001a\u00020#J\u000e\u0010$\u001a\u00020\u00152\u0006\u0010\u0002\u001a\u00020\u0003J\b\u0010%\u001a\u00020\u0015H\u0002R\u0012\u0010\u0002\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\f\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u000e8\u0006@\u0006X\u0087\u000e¢\u0006\u0004\n\u0002\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0012\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0013\u001a\u00020\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006&"},
      d2 = {"Lokhttp3/internal/http2/Hpack$Writer;", "", "headerTableSizeSetting", "", "useCompression", "", "out", "Lokio/Buffer;", "<init>", "(IZLokio/Buffer;)V", "smallestHeaderTableSizeSetting", "emitDynamicTableSizeUpdate", "maxDynamicTableByteCount", "dynamicTable", "", "Lokhttp3/internal/http2/Header;", "[Lokhttp3/internal/http2/Header;", "nextHeaderIndex", "headerCount", "dynamicTableByteCount", "clearDynamicTable", "", "evictToRecoverBytes", "bytesToRecover", "insertIntoDynamicTable", "entry", "writeHeaders", "headerBlock", "", "writeInt", "value", "prefixMask", "bits", "writeByteString", "data", "Lokio/ByteString;", "resizeHeaderTable", "adjustDynamicTableByteCount", "okhttp"}
   )
   public static final class Writer {
      @JvmField
      public int headerTableSizeSetting;
      private final boolean useCompression;
      @NotNull
      private final Buffer out;
      private int smallestHeaderTableSizeSetting;
      private boolean emitDynamicTableSizeUpdate;
      @JvmField
      public int maxDynamicTableByteCount;
      @JvmField
      @NotNull
      public Header[] dynamicTable;
      private int nextHeaderIndex;
      @JvmField
      public int headerCount;
      @JvmField
      public int dynamicTableByteCount;

      @JvmOverloads
      public Writer(int headerTableSizeSetting, boolean useCompression, @NotNull Buffer out) {
         Intrinsics.checkNotNullParameter(out, "out");
         super();
         this.headerTableSizeSetting = headerTableSizeSetting;
         this.useCompression = useCompression;
         this.out = out;
         this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
         this.maxDynamicTableByteCount = this.headerTableSizeSetting;
         this.dynamicTable = new Header[8];
         this.nextHeaderIndex = this.dynamicTable.length - 1;
      }

      // $FF: synthetic method
      public Writer(int var1, boolean var2, Buffer var3, int var4, DefaultConstructorMarker var5) {
         if ((var4 & 1) != 0) {
            var1 = 4096;
         }

         if ((var4 & 2) != 0) {
            var2 = true;
         }

         this(var1, var2, var3);
      }

      private final void clearDynamicTable() {
         ArraysKt.fill$default(this.dynamicTable, (Object)null, 0, 0, 6, (Object)null);
         this.nextHeaderIndex = this.dynamicTable.length - 1;
         this.headerCount = 0;
         this.dynamicTableByteCount = 0;
      }

      private final int evictToRecoverBytes(int bytesToRecover) {
         int bytesToRecover = bytesToRecover;
         int entriesToEvict = 0;
         if (bytesToRecover > 0) {
            for(int j = this.dynamicTable.length - 1; j >= this.nextHeaderIndex && bytesToRecover > 0; --j) {
               Header var10001 = this.dynamicTable[j];
               Intrinsics.checkNotNull(var10001);
               bytesToRecover -= var10001.hpackSize;
               int var6 = this.dynamicTableByteCount;
               Header var10002 = this.dynamicTable[j];
               Intrinsics.checkNotNull(var10002);
               this.dynamicTableByteCount = var6 - var10002.hpackSize;
               int var5 = this.headerCount;
               this.headerCount = var5 + -1;
               ++entriesToEvict;
            }

            System.arraycopy(this.dynamicTable, this.nextHeaderIndex + 1, this.dynamicTable, this.nextHeaderIndex + 1 + entriesToEvict, this.headerCount);
            Arrays.fill(this.dynamicTable, this.nextHeaderIndex + 1, this.nextHeaderIndex + 1 + entriesToEvict, (Object)null);
            this.nextHeaderIndex += entriesToEvict;
         }

         return entriesToEvict;
      }

      private final void insertIntoDynamicTable(Header entry) {
         int delta = entry.hpackSize;
         if (delta > this.maxDynamicTableByteCount) {
            this.clearDynamicTable();
         } else {
            int bytesToRecover = this.dynamicTableByteCount + delta - this.maxDynamicTableByteCount;
            this.evictToRecoverBytes(bytesToRecover);
            if (this.headerCount + 1 > this.dynamicTable.length) {
               Header[] doubled = new Header[this.dynamicTable.length * 2];
               System.arraycopy(this.dynamicTable, 0, doubled, this.dynamicTable.length, this.dynamicTable.length);
               this.nextHeaderIndex = this.dynamicTable.length - 1;
               this.dynamicTable = doubled;
            }

            int index = this.nextHeaderIndex;
            this.nextHeaderIndex = index + -1;
            this.dynamicTable[index] = entry;
            index = this.headerCount++;
            this.dynamicTableByteCount += delta;
         }
      }

      public final void writeHeaders(@NotNull List<Header> headerBlock) throws IOException {
         Intrinsics.checkNotNullParameter(headerBlock, "headerBlock");
         if (this.emitDynamicTableSizeUpdate) {
            if (this.smallestHeaderTableSizeSetting < this.maxDynamicTableByteCount) {
               this.writeInt(this.smallestHeaderTableSizeSetting, 31, 32);
            }

            this.emitDynamicTableSizeUpdate = false;
            this.smallestHeaderTableSizeSetting = Integer.MAX_VALUE;
            this.writeInt(this.maxDynamicTableByteCount, 31, 32);
         }

         int i = 0;

         for(int var3 = headerBlock.size(); i < var3; ++i) {
            Header header = (Header)headerBlock.get(i);
            ByteString name = header.name.toAsciiLowercase();
            ByteString value = header.value;
            int headerIndex = -1;
            int headerNameIndex = -1;
            Integer staticIndex = (Integer)Hpack.INSTANCE.getNAME_TO_FIRST_INDEX().get(name);
            if (staticIndex != null) {
               headerNameIndex = staticIndex + 1;
               if (2 <= headerNameIndex ? headerNameIndex < 8 : false) {
                  if (Intrinsics.areEqual((Object)Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex - 1].value, (Object)value)) {
                     headerIndex = headerNameIndex;
                  } else if (Intrinsics.areEqual((Object)Hpack.INSTANCE.getSTATIC_HEADER_TABLE()[headerNameIndex].value, (Object)value)) {
                     headerIndex = headerNameIndex + 1;
                  }
               }
            }

            if (headerIndex == -1) {
               int j = this.nextHeaderIndex + 1;

               for(int var11 = this.dynamicTable.length; j < var11; ++j) {
                  Header var10000 = this.dynamicTable[j];
                  Intrinsics.checkNotNull(var10000);
                  if (Intrinsics.areEqual((Object)var10000.name, (Object)name)) {
                     var10000 = this.dynamicTable[j];
                     Intrinsics.checkNotNull(var10000);
                     if (Intrinsics.areEqual((Object)var10000.value, (Object)value)) {
                        headerIndex = j - this.nextHeaderIndex + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                        break;
                     }

                     if (headerNameIndex == -1) {
                        headerNameIndex = j - this.nextHeaderIndex + Hpack.INSTANCE.getSTATIC_HEADER_TABLE().length;
                     }
                  }
               }
            }

            if (headerIndex != -1) {
               this.writeInt(headerIndex, 127, 128);
            } else if (headerNameIndex == -1) {
               this.out.writeByte(64);
               this.writeByteString(name);
               this.writeByteString(value);
               this.insertIntoDynamicTable(header);
            } else if (name.startsWith(Header.PSEUDO_PREFIX) && !Intrinsics.areEqual((Object)Header.TARGET_AUTHORITY, (Object)name)) {
               this.writeInt(headerNameIndex, 15, 0);
               this.writeByteString(value);
            } else {
               this.writeInt(headerNameIndex, 63, 64);
               this.writeByteString(value);
               this.insertIntoDynamicTable(header);
            }
         }

      }

      public final void writeInt(int value, int prefixMask, int bits) {
         if (value < prefixMask) {
            this.out.writeByte(bits | value);
         } else {
            this.out.writeByte(bits | prefixMask);

            int value;
            for(value = value - prefixMask; value >= 128; value >>>= 7) {
               int b = value & 127;
               this.out.writeByte(b | 128);
            }

            this.out.writeByte(value);
         }
      }

      public final void writeByteString(@NotNull ByteString data) throws IOException {
         Intrinsics.checkNotNullParameter(data, "data");
         if (this.useCompression && Huffman.INSTANCE.encodedLength(data) < data.size()) {
            Buffer huffmanBuffer = new Buffer();
            Huffman.INSTANCE.encode(data, huffmanBuffer);
            ByteString huffmanBytes = huffmanBuffer.readByteString();
            this.writeInt(huffmanBytes.size(), 127, 128);
            this.out.write(huffmanBytes);
         } else {
            this.writeInt(data.size(), 127, 0);
            this.out.write(data);
         }

      }

      public final void resizeHeaderTable(int headerTableSizeSetting) {
         this.headerTableSizeSetting = headerTableSizeSetting;
         int effectiveHeaderTableSize = Math.min(headerTableSizeSetting, 16384);
         if (this.maxDynamicTableByteCount != effectiveHeaderTableSize) {
            if (effectiveHeaderTableSize < this.maxDynamicTableByteCount) {
               this.smallestHeaderTableSizeSetting = Math.min(this.smallestHeaderTableSizeSetting, effectiveHeaderTableSize);
            }

            this.emitDynamicTableSizeUpdate = true;
            this.maxDynamicTableByteCount = effectiveHeaderTableSize;
            this.adjustDynamicTableByteCount();
         }
      }

      private final void adjustDynamicTableByteCount() {
         if (this.maxDynamicTableByteCount < this.dynamicTableByteCount) {
            if (this.maxDynamicTableByteCount == 0) {
               this.clearDynamicTable();
            } else {
               this.evictToRecoverBytes(this.dynamicTableByteCount - this.maxDynamicTableByteCount);
            }
         }

      }

      @JvmOverloads
      public Writer(int headerTableSizeSetting, @NotNull Buffer out) {
         Intrinsics.checkNotNullParameter(out, "out");
         this(headerTableSizeSetting, false, out, 2, (DefaultConstructorMarker)null);
      }

      @JvmOverloads
      public Writer(@NotNull Buffer out) {
         Intrinsics.checkNotNullParameter(out, "out");
         this(0, false, out, 3, (DefaultConstructorMarker)null);
      }
   }
}
