package okio;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import okio.internal.-Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000°\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u001a\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0005\n\u0002\b\u0004\n\u0002\u0010\n\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004:\u0002\u0094\u0001B\u0007¢\u0006\u0004\b\u0005\u0010\u0006J\b\u0010\u000f\u001a\u00020\u0000H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0000H\u0016J\b\u0010\u0015\u001a\u00020\u0000H\u0016J\b\u0010\u0016\u001a\u00020\u0017H\u0016J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u001a\u001a\u00020\nH\u0016J\b\u0010\u001c\u001a\u00020\u0001H\u0016J\b\u0010\u001d\u001a\u00020\u001eH\u0016J$\u0010\u001f\u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u00132\b\b\u0002\u0010!\u001a\u00020\n2\b\b\u0002\u0010\u001a\u001a\u00020\nH\u0007J \u0010\u001f\u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u00002\b\b\u0002\u0010!\u001a\u00020\n2\u0006\u0010\u001a\u001a\u00020\nJ\u0018\u0010\u001f\u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u00002\b\b\u0002\u0010!\u001a\u00020\nJ\u001a\u0010\"\u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u00132\b\b\u0002\u0010\u001a\u001a\u00020\nH\u0007J\u000e\u0010#\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\u001eJ\u0016\u0010#\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\nJ \u0010#\u001a\u00020\u00192\u0006\u0010$\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010%\u001a\u00020\u0017H\u0002J\u0006\u0010&\u001a\u00020\nJ\b\u0010'\u001a\u00020(H\u0016J\u0016\u0010)\u001a\u00020(2\u0006\u0010*\u001a\u00020\nH\u0087\u0002¢\u0006\u0002\b+J\b\u0010,\u001a\u00020-H\u0016J\b\u0010.\u001a\u00020/H\u0016J\b\u00100\u001a\u00020\nH\u0016J\b\u00101\u001a\u00020-H\u0016J\b\u00102\u001a\u00020/H\u0016J\b\u00103\u001a\u00020\nH\u0016J\b\u00104\u001a\u00020\nH\u0016J\b\u00105\u001a\u00020\nH\u0016J\b\u00106\u001a\u000207H\u0016J\u0010\u00106\u001a\u0002072\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u00108\u001a\u00020/2\u0006\u00109\u001a\u00020:H\u0016J'\u00108\u001a\u0004\u0018\u0001H;\"\b\b\u0000\u0010;*\u00020<2\f\u00109\u001a\b\u0012\u0004\u0012\u0002H;0=H\u0016¢\u0006\u0002\u0010>J\u0018\u0010?\u001a\u00020\u00192\u0006\u0010@\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010A\u001a\u00020\n2\u0006\u0010@\u001a\u00020BH\u0016J\b\u0010C\u001a\u00020DH\u0016J\u0010\u0010C\u001a\u00020D2\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010E\u001a\u00020D2\u0006\u0010F\u001a\u00020GH\u0016J\u0018\u0010E\u001a\u00020D2\u0006\u0010\u001a\u001a\u00020\n2\u0006\u0010F\u001a\u00020GH\u0016J\n\u0010H\u001a\u0004\u0018\u00010DH\u0016J\b\u0010I\u001a\u00020DH\u0016J\u0010\u0010I\u001a\u00020D2\u0006\u0010J\u001a\u00020\nH\u0016J\b\u0010K\u001a\u00020/H\u0016J\b\u0010L\u001a\u00020MH\u0016J\u0010\u0010L\u001a\u00020M2\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010N\u001a\u00020/2\u0006\u0010@\u001a\u00020MH\u0016J\u0010\u0010?\u001a\u00020\u00192\u0006\u0010@\u001a\u00020MH\u0016J \u0010N\u001a\u00020/2\u0006\u0010@\u001a\u00020M2\u0006\u0010!\u001a\u00020/2\u0006\u0010\u001a\u001a\u00020/H\u0016J\u0010\u0010N\u001a\u00020/2\u0006\u0010@\u001a\u00020OH\u0016J\u0006\u0010P\u001a\u00020\u0019J\u0010\u0010Q\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010R\u001a\u00020\u00002\u0006\u0010S\u001a\u000207H\u0016J \u0010R\u001a\u00020\u00002\u0006\u0010S\u001a\u0002072\u0006\u0010!\u001a\u00020/2\u0006\u0010\u001a\u001a\u00020/H\u0016J\u0010\u0010T\u001a\u00020\u00002\u0006\u0010U\u001a\u00020DH\u0016J \u0010T\u001a\u00020\u00002\u0006\u0010U\u001a\u00020D2\u0006\u0010V\u001a\u00020/2\u0006\u0010W\u001a\u00020/H\u0016J\u0010\u0010X\u001a\u00020\u00002\u0006\u0010Y\u001a\u00020/H\u0016J\u0018\u0010Z\u001a\u00020\u00002\u0006\u0010U\u001a\u00020D2\u0006\u0010F\u001a\u00020GH\u0016J(\u0010Z\u001a\u00020\u00002\u0006\u0010U\u001a\u00020D2\u0006\u0010V\u001a\u00020/2\u0006\u0010W\u001a\u00020/2\u0006\u0010F\u001a\u00020GH\u0016J\u0010\u0010R\u001a\u00020\u00002\u0006\u0010[\u001a\u00020MH\u0016J \u0010R\u001a\u00020\u00002\u0006\u0010[\u001a\u00020M2\u0006\u0010!\u001a\u00020/2\u0006\u0010\u001a\u001a\u00020/H\u0016J\u0010\u0010R\u001a\u00020/2\u0006\u0010[\u001a\u00020OH\u0016J\u0010\u0010\\\u001a\u00020\n2\u0006\u0010[\u001a\u00020]H\u0016J\u0018\u0010R\u001a\u00020\u00002\u0006\u0010[\u001a\u00020]2\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010^\u001a\u00020\u00002\u0006\u0010_\u001a\u00020/H\u0016J\u0010\u0010`\u001a\u00020\u00002\u0006\u0010a\u001a\u00020/H\u0016J\u0010\u0010b\u001a\u00020\u00002\u0006\u0010a\u001a\u00020/H\u0016J\u0010\u0010c\u001a\u00020\u00002\u0006\u0010d\u001a\u00020/H\u0016J\u0010\u0010e\u001a\u00020\u00002\u0006\u0010d\u001a\u00020/H\u0016J\u0010\u0010f\u001a\u00020\u00002\u0006\u0010g\u001a\u00020\nH\u0016J\u0010\u0010h\u001a\u00020\u00002\u0006\u0010g\u001a\u00020\nH\u0016J\u0010\u0010i\u001a\u00020\u00002\u0006\u0010g\u001a\u00020\nH\u0016J\u0010\u0010j\u001a\u00020\u00002\u0006\u0010g\u001a\u00020\nH\u0016J\u0015\u0010k\u001a\u00020\b2\u0006\u0010l\u001a\u00020/H\u0000¢\u0006\u0002\bmJ\u0018\u0010R\u001a\u00020\u00192\u0006\u0010[\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0018\u0010N\u001a\u00020\n2\u0006\u0010@\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\nH\u0016J\u0010\u0010n\u001a\u00020\n2\u0006\u0010_\u001a\u00020(H\u0016J\u0018\u0010n\u001a\u00020\n2\u0006\u0010_\u001a\u00020(2\u0006\u0010o\u001a\u00020\nH\u0016J \u0010n\u001a\u00020\n2\u0006\u0010_\u001a\u00020(2\u0006\u0010o\u001a\u00020\n2\u0006\u0010p\u001a\u00020\nH\u0016J\u0010\u0010n\u001a\u00020\n2\u0006\u0010q\u001a\u000207H\u0016J\u0018\u0010n\u001a\u00020\n2\u0006\u0010q\u001a\u0002072\u0006\u0010o\u001a\u00020\nH\u0016J \u0010n\u001a\u00020\n2\u0006\u0010q\u001a\u0002072\u0006\u0010o\u001a\u00020\n2\u0006\u0010p\u001a\u00020\nH\u0016J\u0010\u0010r\u001a\u00020\n2\u0006\u0010s\u001a\u000207H\u0016J\u0018\u0010r\u001a\u00020\n2\u0006\u0010s\u001a\u0002072\u0006\u0010o\u001a\u00020\nH\u0016J\u0018\u0010t\u001a\u00020\u00172\u0006\u0010!\u001a\u00020\n2\u0006\u0010q\u001a\u000207H\u0016J(\u0010t\u001a\u00020\u00172\u0006\u0010!\u001a\u00020\n2\u0006\u0010q\u001a\u0002072\u0006\u0010u\u001a\u00020/2\u0006\u0010\u001a\u001a\u00020/H\u0016J\b\u0010v\u001a\u00020\u0019H\u0016J\b\u0010w\u001a\u00020\u0017H\u0016J\b\u0010x\u001a\u00020\u0019H\u0016J\b\u0010y\u001a\u00020zH\u0016J\u0006\u0010{\u001a\u000207J\u0006\u0010|\u001a\u000207J\u0006\u0010}\u001a\u000207J\u0006\u0010~\u001a\u000207J\u0011\u0010\u007f\u001a\u0002072\u0007\u0010\u0080\u0001\u001a\u00020DH\u0002J\u0010\u0010\u0081\u0001\u001a\u0002072\u0007\u0010\u0082\u0001\u001a\u000207J\u0010\u0010\u0083\u0001\u001a\u0002072\u0007\u0010\u0082\u0001\u001a\u000207J\u0010\u0010\u0084\u0001\u001a\u0002072\u0007\u0010\u0082\u0001\u001a\u000207J\u001b\u0010\u0085\u0001\u001a\u0002072\u0007\u0010\u0080\u0001\u001a\u00020D2\u0007\u0010\u0082\u0001\u001a\u000207H\u0002J\u0015\u0010\u0086\u0001\u001a\u00020\u00172\t\u0010\u0087\u0001\u001a\u0004\u0018\u00010<H\u0096\u0002J\t\u0010\u0088\u0001\u001a\u00020/H\u0016J\t\u0010\u0089\u0001\u001a\u00020DH\u0016J\u0007\u0010\u008a\u0001\u001a\u00020\u0000J\t\u0010\u008b\u0001\u001a\u00020\u0000H\u0016J\u0007\u0010\u008c\u0001\u001a\u000207J\u000f\u0010\u008c\u0001\u001a\u0002072\u0006\u0010\u001a\u001a\u00020/J\u0016\u0010\u008d\u0001\u001a\u00030\u008e\u00012\n\b\u0002\u0010\u008f\u0001\u001a\u00030\u008e\u0001H\u0007J\u0016\u0010\u0090\u0001\u001a\u00030\u008e\u00012\n\b\u0002\u0010\u008f\u0001\u001a\u00030\u008e\u0001H\u0007J\u0017\u0010+\u001a\u00020(2\u0007\u0010\u0091\u0001\u001a\u00020\nH\u0007¢\u0006\u0003\b\u0092\u0001J\u000e\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0003\b\u0093\u0001R\u0014\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0000@\u0000X\u0081\u000e¢\u0006\u0002\n\u0000R&\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n8\u0007@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\u00008VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0095\u0001"},
   d2 = {"Lokio/Buffer;", "Lokio/BufferedSource;", "Lokio/BufferedSink;", "", "Ljava/nio/channels/ByteChannel;", "<init>", "()V", "head", "Lokio/Segment;", "value", "", "size", "()J", "setSize$okio", "(J)V", "buffer", "getBuffer", "()Lokio/Buffer;", "outputStream", "Ljava/io/OutputStream;", "emitCompleteSegments", "emit", "exhausted", "", "require", "", "byteCount", "request", "peek", "inputStream", "Ljava/io/InputStream;", "copyTo", "out", "offset", "writeTo", "readFrom", "input", "forever", "completeSegmentByteCount", "readByte", "", "get", "pos", "getByte", "readShort", "", "readInt", "", "readLong", "readShortLe", "readIntLe", "readLongLe", "readDecimalLong", "readHexadecimalUnsignedLong", "readByteString", "Lokio/ByteString;", "select", "options", "Lokio/Options;", "T", "", "Lokio/TypedOptions;", "(Lokio/TypedOptions;)Ljava/lang/Object;", "readFully", "sink", "readAll", "Lokio/Sink;", "readUtf8", "", "readString", "charset", "Ljava/nio/charset/Charset;", "readUtf8Line", "readUtf8LineStrict", "limit", "readUtf8CodePoint", "readByteArray", "", "read", "Ljava/nio/ByteBuffer;", "clear", "skip", "write", "byteString", "writeUtf8", "string", "beginIndex", "endIndex", "writeUtf8CodePoint", "codePoint", "writeString", "source", "writeAll", "Lokio/Source;", "writeByte", "b", "writeShort", "s", "writeShortLe", "writeInt", "i", "writeIntLe", "writeLong", "v", "writeLongLe", "writeDecimalLong", "writeHexadecimalUnsignedLong", "writableSegment", "minimumCapacity", "writableSegment$okio", "indexOf", "fromIndex", "toIndex", "bytes", "indexOfElement", "targetBytes", "rangeEquals", "bytesOffset", "flush", "isOpen", "close", "timeout", "Lokio/Timeout;", "md5", "sha1", "sha256", "sha512", "digest", "algorithm", "hmacSha1", "key", "hmacSha256", "hmacSha512", "hmac", "equals", "other", "hashCode", "toString", "copy", "clone", "snapshot", "readUnsafe", "Lokio/Buffer$UnsafeCursor;", "unsafeCursor", "readAndWriteUnsafe", "index", "-deprecated_getByte", "-deprecated_size", "UnsafeCursor", "okio"}
)
@SourceDebugExtension({"SMAP\nBuffer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Buffer.kt\nokio/Buffer\n+ 2 Util.kt\nokio/-SegmentedByteString\n+ 3 Buffer.kt\nokio/internal/-Buffer\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 BufferedSource.kt\nokio/internal/-BufferedSource\n*L\n1#1,649:1\n88#2:650\n85#2:683\n85#2:685\n73#2:745\n73#2:771\n82#2:810\n76#2:821\n88#2:1014\n73#2:1029\n85#2:1133\n242#3,32:651\n277#3,10:686\n290#3,18:696\n412#3,2:714\n110#3:716\n414#3:717\n112#3,18:718\n311#3,9:736\n320#3,15:746\n338#3,10:761\n348#3,3:772\n346#3,25:775\n374#3,10:800\n384#3:811\n382#3,9:812\n391#3,7:822\n389#3,20:829\n652#3,60:849\n715#3,56:909\n773#3:965\n776#3:966\n777#3,6:968\n787#3,7:974\n797#3,6:984\n805#3,5:990\n837#3,6:995\n847#3:1001\n848#3,11:1003\n859#3,5:1015\n868#3,9:1020\n878#3,61:1030\n603#3:1091\n606#3:1092\n607#3,5:1094\n614#3:1099\n617#3,7:1100\n626#3,20:1107\n418#3:1127\n421#3,5:1128\n426#3,10:1134\n437#3,7:1144\n442#3,2:1151\n943#3:1153\n944#3,87:1155\n1034#3,48:1242\n573#3:1290\n580#3,21:1291\n1085#3,7:1312\n1095#3,7:1319\n1105#3,4:1326\n1112#3,8:1330\n1123#3,10:1338\n1136#3,14:1348\n447#3,35:1362\n513#3,40:1397\n556#3:1437\n558#3,13:1439\n1153#3:1452\n1204#3:1453\n1205#3,39:1455\n1246#3,2:1494\n1248#3,4:1497\n1255#3,3:1501\n1259#3,4:1505\n110#3:1509\n1263#3,22:1510\n112#3,18:1532\n1338#3,2:1550\n1341#3:1553\n110#3:1554\n1342#3,50:1555\n112#3,18:1605\n1401#3,12:1623\n1416#3,32:1635\n1451#3,12:1667\n1466#3,18:1679\n1488#3:1697\n1489#3:1699\n1494#3,34:1700\n1#4:684\n1#4:967\n1#4:1002\n1#4:1093\n1#4:1154\n1#4:1438\n1#4:1454\n1#4:1496\n1#4:1504\n1#4:1552\n1#4:1698\n26#5,3:981\n*S KotlinDebug\n*F\n+ 1 Buffer.kt\nokio/Buffer\n*L\n167#1:650\n197#1:683\n235#1:685\n261#1:745\n264#1:771\n267#1:810\n267#1:821\n337#1:1014\n340#1:1029\n376#1:1133\n181#1:651,32\n252#1:686,10\n255#1:696,18\n258#1:714,2\n258#1:716\n258#1:717\n258#1:718,18\n261#1:736,9\n261#1:746,15\n264#1:761,10\n264#1:772,3\n264#1:775,25\n267#1:800,10\n267#1:811\n267#1:812,9\n267#1:822,7\n267#1:829,20\n279#1:849,60\n282#1:909,56\n284#1:965\n287#1:966\n287#1:968,6\n289#1:974,7\n294#1:984,6\n297#1:990,5\n331#1:995,6\n337#1:1001\n337#1:1003,11\n337#1:1015,5\n340#1:1020,9\n340#1:1030,61\n342#1:1091\n345#1:1092\n345#1:1094,5\n347#1:1099\n350#1:1100,7\n353#1:1107,20\n373#1:1127\n376#1:1128,5\n376#1:1134,10\n378#1:1144,7\n381#1:1151,2\n386#1:1153\n386#1:1155,87\n389#1:1242,48\n412#1:1290\n418#1:1291,21\n439#1:1312,7\n443#1:1319,7\n445#1:1326,4\n447#1:1330,8\n451#1:1338,10\n455#1:1348,14\n459#1:1362,35\n462#1:1397,40\n465#1:1437\n465#1:1439,13\n467#1:1452\n467#1:1453\n467#1:1455,39\n469#1:1494,2\n469#1:1497,4\n480#1:1501,3\n480#1:1505,4\n480#1:1509\n480#1:1510,22\n480#1:1532,18\n496#1:1550,2\n496#1:1553\n496#1:1554\n496#1:1555,50\n496#1:1605,18\n506#1:1623,12\n576#1:1635,32\n578#1:1667,12\n586#1:1679,18\n594#1:1697\n594#1:1699\n596#1:1700,34\n287#1:967\n337#1:1002\n345#1:1093\n386#1:1154\n465#1:1438\n467#1:1454\n469#1:1496\n480#1:1504\n496#1:1552\n594#1:1698\n291#1:981,3\n*E\n"})
public final class Buffer implements BufferedSource, BufferedSink, Cloneable, ByteChannel {
   @JvmField
   @Nullable
   public Segment head;
   private long size;

   @JvmName(
      name = "size"
   )
   public final long size() {
      return this.size;
   }

   public final void setSize$okio(long var1) {
      this.size = var1;
   }

   @NotNull
   public Buffer buffer() {
      return this;
   }

   @NotNull
   public Buffer getBuffer() {
      return this;
   }

   @NotNull
   public OutputStream outputStream() {
      return new OutputStream() {
         public void write(int b) {
            Buffer.this.writeByte(b);
         }

         public void write(byte[] data, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter(data, "data");
            Buffer.this.write(data, offset, byteCount);
         }

         public void flush() {
         }

         public void close() {
         }

         public String toString() {
            return Buffer.this + ".outputStream()";
         }
      };
   }

   @NotNull
   public Buffer emitCompleteSegments() {
      return this;
   }

   @NotNull
   public Buffer emit() {
      return this;
   }

   public boolean exhausted() {
      return this.size == 0L;
   }

   public void require(long byteCount) throws EOFException {
      if (this.size < byteCount) {
         throw new EOFException();
      }
   }

   public boolean request(long byteCount) {
      return this.size >= byteCount;
   }

   @NotNull
   public BufferedSource peek() {
      return Okio.buffer(new PeekSource(this));
   }

   @NotNull
   public InputStream inputStream() {
      return new InputStream() {
         public int read() {
            int var10000;
            if (Buffer.this.size() > 0L) {
               byte $this$and$iv = Buffer.this.readByte();
               int other$iv = 255;
               int $i$f$and = 0;
               var10000 = $this$and$iv & other$iv;
            } else {
               var10000 = -1;
            }

            return var10000;
         }

         public int read(byte[] sink, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter(sink, "sink");
            return Buffer.this.read(sink, offset, byteCount);
         }

         public int available() {
            long a$iv = Buffer.this.size();
            int b$iv = Integer.MAX_VALUE;
            int $i$f$minOf = 0;
            return (int)Math.min(a$iv, (long)b$iv);
         }

         public void close() {
         }

         public String toString() {
            return Buffer.this + ".inputStream()";
         }
      };
   }

   @JvmOverloads
   @NotNull
   public final Buffer copyTo(@NotNull OutputStream out, long offset, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      long offset = offset;
      long byteCount = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size, offset, byteCount);
      if (byteCount == 0L) {
         return this;
      } else {
         Segment s = this.head;

         while(true) {
            Intrinsics.checkNotNull(s);
            if (offset < (long)(s.limit - s.pos)) {
               while(byteCount > 0L) {
                  Intrinsics.checkNotNull(s);
                  int pos = (int)((long)s.pos + offset);
                  int a$iv = s.limit - pos;
                  int $i$f$minOf = 0;
                  int toCopy = (int)Math.min((long)a$iv, byteCount);
                  out.write(s.data, pos, toCopy);
                  byteCount -= (long)toCopy;
                  offset = 0L;
                  s = s.next;
               }

               return this;
            }

            offset -= (long)(s.limit - s.pos);
            s = s.next;
         }
      }
   }

   // $FF: synthetic method
   public static Buffer copyTo$default(Buffer var0, OutputStream var1, long var2, long var4, int var6, Object var7) throws IOException {
      if ((var6 & 2) != 0) {
         var2 = 0L;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.size - var2;
      }

      return var0.copyTo(var1, var2, var4);
   }

   @NotNull
   public final Buffer copyTo(@NotNull Buffer out, long offset, long byteCount) {
      Intrinsics.checkNotNullParameter(out, "out");
      Buffer out$iv = out;
      int $i$f$commonCopyTo = 0;
      long offset$iv = offset;
      long byteCount$iv = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size(), offset, byteCount);
      Buffer var10000;
      if (byteCount == 0L) {
         var10000 = this;
      } else {
         out.setSize$okio(out.size() + byteCount);
         Segment s$iv = this.head;

         while(true) {
            Intrinsics.checkNotNull(s$iv);
            if (offset$iv < (long)(s$iv.limit - s$iv.pos)) {
               while(byteCount$iv > 0L) {
                  Intrinsics.checkNotNull(s$iv);
                  Segment copy$iv = s$iv.sharedCopy();
                  copy$iv.pos += (int)offset$iv;
                  copy$iv.limit = Math.min(copy$iv.pos + (int)byteCount$iv, copy$iv.limit);
                  if (out$iv.head == null) {
                     copy$iv.prev = copy$iv;
                     copy$iv.next = copy$iv.prev;
                     out$iv.head = copy$iv.next;
                  } else {
                     Segment var19 = out$iv.head;
                     Intrinsics.checkNotNull(var19);
                     var19 = var19.prev;
                     Intrinsics.checkNotNull(var19);
                     var19.push(copy$iv);
                  }

                  byteCount$iv -= (long)(copy$iv.limit - copy$iv.pos);
                  offset$iv = 0L;
                  s$iv = s$iv.next;
               }

               var10000 = this;
               break;
            }

            offset$iv -= (long)(s$iv.limit - s$iv.pos);
            s$iv = s$iv.next;
         }
      }

      return var10000;
   }

   // $FF: synthetic method
   public static Buffer copyTo$default(Buffer var0, Buffer var1, long var2, long var4, int var6, Object var7) {
      if ((var6 & 2) != 0) {
         var2 = 0L;
      }

      return var0.copyTo(var1, var2, var4);
   }

   @NotNull
   public final Buffer copyTo(@NotNull Buffer out, long offset) {
      Intrinsics.checkNotNullParameter(out, "out");
      return this.copyTo(out, offset, this.size - offset);
   }

   // $FF: synthetic method
   public static Buffer copyTo$default(Buffer var0, Buffer var1, long var2, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = 0L;
      }

      return var0.copyTo(var1, var2);
   }

   @JvmOverloads
   @NotNull
   public final Buffer writeTo(@NotNull OutputStream out, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      long byteCount = byteCount;
      -SegmentedByteString.checkOffsetAndCount(this.size, 0L, byteCount);
      Segment s = this.head;

      while(byteCount > 0L) {
         Intrinsics.checkNotNull(s);
         int b$iv = s.limit - s.pos;
         int $i$f$minOf = 0;
         int toCopy = (int)Math.min(byteCount, (long)b$iv);
         out.write(s.data, s.pos, toCopy);
         s.pos += toCopy;
         this.size -= (long)toCopy;
         byteCount -= (long)toCopy;
         if (s.pos == s.limit) {
            Segment toRecycle = s;
            s = s.pop();
            this.head = s;
            SegmentPool.recycle(toRecycle);
         }
      }

      return this;
   }

   // $FF: synthetic method
   public static Buffer writeTo$default(Buffer var0, OutputStream var1, long var2, int var4, Object var5) throws IOException {
      if ((var4 & 2) != 0) {
         var2 = var0.size;
      }

      return var0.writeTo(var1, var2);
   }

   @NotNull
   public final Buffer readFrom(@NotNull InputStream input) throws IOException {
      Intrinsics.checkNotNullParameter(input, "input");
      this.readFrom(input, Long.MAX_VALUE, true);
      return this;
   }

   @NotNull
   public final Buffer readFrom(@NotNull InputStream input, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(input, "input");
      if (byteCount < 0L) {
         int var4 = 0;
         String var5 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var5.toString());
      } else {
         this.readFrom(input, byteCount, false);
         return this;
      }
   }

   private final void readFrom(InputStream input, long byteCount, boolean forever) throws IOException {
      int bytesRead;
      for(long byteCount = byteCount; byteCount > 0L || forever; byteCount -= (long)bytesRead) {
         Segment tail = this.writableSegment$okio(1);
         int b$iv = 8192 - tail.limit;
         int $i$f$minOf = 0;
         int maxToCopy = (int)Math.min(byteCount, (long)b$iv);
         bytesRead = input.read(tail.data, tail.limit, maxToCopy);
         if (bytesRead == -1) {
            if (tail.pos == tail.limit) {
               this.head = tail.pop();
               SegmentPool.recycle(tail);
            }

            if (forever) {
               return;
            }

            throw new EOFException();
         }

         tail.limit += bytesRead;
         this.size += (long)bytesRead;
      }

   }

   public final long completeSegmentByteCount() {
      int $i$f$commonCompleteSegmentByteCount = 0;
      long result$iv = this.size();
      long var10000;
      if (result$iv == 0L) {
         var10000 = 0L;
      } else {
         Segment var6 = this.head;
         Intrinsics.checkNotNull(var6);
         var6 = var6.prev;
         Intrinsics.checkNotNull(var6);
         Segment tail$iv = var6;
         if (tail$iv.limit < 8192 && tail$iv.owner) {
            result$iv -= (long)(tail$iv.limit - tail$iv.pos);
         }

         var10000 = result$iv;
      }

      return var10000;
   }

   public byte readByte() throws EOFException {
      int $i$f$commonReadByte = 0;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         Segment var10000 = this.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment$iv = var10000;
         int pos$iv = segment$iv.pos;
         int limit$iv = segment$iv.limit;
         byte[] data$iv = segment$iv.data;
         byte b$iv = data$iv[pos$iv++];
         this.setSize$okio(this.size() - 1L);
         if (pos$iv == limit$iv) {
            this.head = segment$iv.pop();
            SegmentPool.recycle(segment$iv);
         } else {
            segment$iv.pos = pos$iv;
         }

         return b$iv;
      }
   }

   @JvmName(
      name = "getByte"
   )
   public final byte getByte(long pos) {
      int $i$f$commonGet = 0;
      -SegmentedByteString.checkOffsetAndCount(this.size(), pos, 1L);
      long fromIndex$iv$iv = pos;
      int $i$f$seek = 0;
      Segment var10000 = this.head;
      byte var27;
      if (var10000 == null) {
         long offset$iv = -1L;
         Segment s$iv = null;
         int var14 = 0;
         Intrinsics.checkNotNull(s$iv);
         var27 = s$iv.data[(int)((long)s$iv.pos + pos - offset$iv)];
      } else {
         Segment s$iv$iv = var10000;
         if (this.size() - pos < pos) {
            long offset$iv$iv;
            for(offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(s$iv$iv.limit - s$iv$iv.pos)) {
               Segment var28 = s$iv$iv.prev;
               Intrinsics.checkNotNull(var28);
               s$iv$iv = var28;
            }

            int var21 = 0;
            Intrinsics.checkNotNull(s$iv$iv);
            var27 = s$iv$iv.data[(int)((long)s$iv$iv.pos + pos - offset$iv$iv)];
         } else {
            long offset$iv$iv = 0L;

            while(true) {
               long nextOffset$iv$iv = offset$iv$iv + (long)(s$iv$iv.limit - s$iv$iv.pos);
               if (nextOffset$iv$iv > fromIndex$iv$iv) {
                  int var24 = 0;
                  Intrinsics.checkNotNull(s$iv$iv);
                  var27 = s$iv$iv.data[(int)((long)s$iv$iv.pos + pos - offset$iv$iv)];
                  break;
               }

               Segment var29 = s$iv$iv.next;
               Intrinsics.checkNotNull(var29);
               s$iv$iv = var29;
               offset$iv$iv = nextOffset$iv$iv;
            }
         }
      }

      return var27;
   }

   public short readShort() throws EOFException {
      int $i$f$commonReadShort = 0;
      if (this.size() < 2L) {
         throw new EOFException();
      } else {
         Segment var10000 = this.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment$iv = var10000;
         int pos$iv = segment$iv.pos;
         int limit$iv = segment$iv.limit;
         short var24;
         if (limit$iv - pos$iv < 2) {
            byte $this$and$iv$iv = this.readByte();
            int other$iv$iv = 255;
            int $i$f$and = 0;
            var24 = ($this$and$iv$iv & other$iv$iv) << 8;
            $this$and$iv$iv = this.readByte();
            other$iv$iv = 255;
            $i$f$and = 0;
            int s$iv = var24 | $this$and$iv$iv & other$iv$iv;
            var24 = (short)s$iv;
         } else {
            byte[] data$iv = segment$iv.data;
            byte $this$and$iv$iv = data$iv[pos$iv++];
            int other$iv$iv = 255;
            int $i$f$and = 0;
            var24 = ($this$and$iv$iv & other$iv$iv) << 8;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255;
            $i$f$and = 0;
            int s$iv = var24 | $this$and$iv$iv & other$iv$iv;
            this.setSize$okio(this.size() - 2L);
            if (pos$iv == limit$iv) {
               this.head = segment$iv.pop();
               SegmentPool.recycle(segment$iv);
            } else {
               segment$iv.pos = pos$iv;
            }

            var24 = (short)s$iv;
         }

         return (short)var24;
      }
   }

   public int readInt() throws EOFException {
      int $i$f$commonReadInt = 0;
      if (this.size() < 4L) {
         throw new EOFException();
      } else {
         Segment var10000 = this.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment$iv = var10000;
         int pos$iv = segment$iv.pos;
         int limit$iv = segment$iv.limit;
         int var39;
         if ((long)(limit$iv - pos$iv) < 4L) {
            byte $this$and$iv$iv = this.readByte();
            int other$iv$iv = 255;
            int $i$f$and = 0;
            var39 = ($this$and$iv$iv & other$iv$iv) << 24;
            $this$and$iv$iv = this.readByte();
            other$iv$iv = 255;
            $i$f$and = 0;
            var39 |= ($this$and$iv$iv & other$iv$iv) << 16;
            $this$and$iv$iv = this.readByte();
            other$iv$iv = 255;
            $i$f$and = 0;
            var39 |= ($this$and$iv$iv & other$iv$iv) << 8;
            $this$and$iv$iv = this.readByte();
            other$iv$iv = 255;
            $i$f$and = 0;
            var39 |= $this$and$iv$iv & other$iv$iv;
         } else {
            byte[] data$iv = segment$iv.data;
            byte $this$and$iv$iv = data$iv[pos$iv++];
            int other$iv$iv = 255;
            int $i$f$and = 0;
            var39 = ($this$and$iv$iv & other$iv$iv) << 24;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255;
            $i$f$and = 0;
            var39 |= ($this$and$iv$iv & other$iv$iv) << 16;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255;
            $i$f$and = 0;
            var39 |= ($this$and$iv$iv & other$iv$iv) << 8;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255;
            $i$f$and = 0;
            int i$iv = var39 | $this$and$iv$iv & other$iv$iv;
            this.setSize$okio(this.size() - 4L);
            if (pos$iv == limit$iv) {
               this.head = segment$iv.pop();
               SegmentPool.recycle(segment$iv);
            } else {
               segment$iv.pos = pos$iv;
            }

            var39 = i$iv;
         }

         return var39;
      }
   }

   public long readLong() throws EOFException {
      int $i$f$commonReadLong = 0;
      if (this.size() < 8L) {
         throw new EOFException();
      } else {
         Segment var10000 = this.head;
         Intrinsics.checkNotNull(var10000);
         Segment segment$iv = var10000;
         int pos$iv = segment$iv.pos;
         int limit$iv = segment$iv.limit;
         long var49;
         if ((long)(limit$iv - pos$iv) < 8L) {
            int $this$and$iv$iv = this.readInt();
            long other$iv$iv = 4294967295L;
            int $i$f$and = 0;
            var49 = ((long)$this$and$iv$iv & other$iv$iv) << 32;
            $this$and$iv$iv = this.readInt();
            other$iv$iv = 4294967295L;
            $i$f$and = 0;
            var49 |= (long)$this$and$iv$iv & other$iv$iv;
         } else {
            byte[] data$iv = segment$iv.data;
            byte $this$and$iv$iv = data$iv[pos$iv++];
            long other$iv$iv = 255L;
            int $i$f$and = 0;
            var49 = ((long)$this$and$iv$iv & other$iv$iv) << 56;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 48;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 40;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 32;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 24;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 16;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            var49 |= ((long)$this$and$iv$iv & other$iv$iv) << 8;
            $this$and$iv$iv = data$iv[pos$iv++];
            other$iv$iv = 255L;
            $i$f$and = 0;
            long v$iv = var49 | (long)$this$and$iv$iv & other$iv$iv;
            this.setSize$okio(this.size() - 8L);
            if (pos$iv == limit$iv) {
               this.head = segment$iv.pop();
               SegmentPool.recycle(segment$iv);
            } else {
               segment$iv.pos = pos$iv;
            }

            var49 = v$iv;
         }

         return var49;
      }
   }

   public short readShortLe() throws EOFException {
      return -SegmentedByteString.reverseBytes(this.readShort());
   }

   public int readIntLe() throws EOFException {
      return -SegmentedByteString.reverseBytes(this.readInt());
   }

   public long readLongLe() throws EOFException {
      return -SegmentedByteString.reverseBytes(this.readLong());
   }

   public long readDecimalLong() throws EOFException {
      Buffer $this$commonReadDecimalLong$iv = this;
      int $i$f$commonReadDecimalLong = 0;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         long value$iv = 0L;
         int seen$iv = 0;
         boolean negative$iv = false;
         boolean done$iv = false;
         long overflowDigit$iv = -7L;

         while(true) {
            Segment var10000 = $this$commonReadDecimalLong$iv.head;
            Intrinsics.checkNotNull(var10000);
            Segment segment$iv = var10000;
            byte[] data$iv = segment$iv.data;
            int pos$iv = segment$iv.pos;
            int limit$iv = segment$iv.limit;

            while(true) {
               label86: {
                  if (pos$iv < limit$iv) {
                     byte b$iv = data$iv[pos$iv];
                     if (b$iv >= 48 && b$iv <= 57) {
                        int digit$iv = 48 - b$iv;
                        if (value$iv < -922337203685477580L || value$iv == -922337203685477580L && (long)digit$iv < overflowDigit$iv) {
                           Buffer buffer$iv = (new Buffer()).writeDecimalLong(value$iv).writeByte(b$iv);
                           if (!negative$iv) {
                              buffer$iv.readByte();
                           }

                           throw new NumberFormatException("Number too large: " + buffer$iv.readUtf8());
                        }

                        value$iv *= 10L;
                        value$iv += (long)digit$iv;
                        break label86;
                     }

                     if (b$iv == 45 && seen$iv == 0) {
                        negative$iv = true;
                        --overflowDigit$iv;
                        break label86;
                     }

                     done$iv = true;
                  }

                  if (pos$iv == limit$iv) {
                     $this$commonReadDecimalLong$iv.head = segment$iv.pop();
                     SegmentPool.recycle(segment$iv);
                  } else {
                     segment$iv.pos = pos$iv;
                  }

                  if (!done$iv && $this$commonReadDecimalLong$iv.head != null) {
                     break;
                  }

                  $this$commonReadDecimalLong$iv.setSize$okio($this$commonReadDecimalLong$iv.size() - (long)seen$iv);
                  int minimumSeen$iv = negative$iv ? 2 : 1;
                  if (seen$iv < minimumSeen$iv) {
                     if ($this$commonReadDecimalLong$iv.size() == 0L) {
                        throw new EOFException();
                     }

                     String expected$iv = negative$iv ? "Expected a digit" : "Expected a digit or '-'";
                     throw new NumberFormatException(expected$iv + " but was 0x" + -SegmentedByteString.toHexString($this$commonReadDecimalLong$iv.getByte(0L)));
                  }

                  return negative$iv ? value$iv : -value$iv;
               }

               ++pos$iv;
               ++seen$iv;
            }
         }
      }
   }

   public long readHexadecimalUnsignedLong() throws EOFException {
      Buffer $this$commonReadHexadecimalUnsignedLong$iv = this;
      int $i$f$commonReadHexadecimalUnsignedLong = 0;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         long value$iv = 0L;
         int seen$iv = 0;
         boolean done$iv = false;

         do {
            Segment var10000 = $this$commonReadHexadecimalUnsignedLong$iv.head;
            Intrinsics.checkNotNull(var10000);
            Segment segment$iv = var10000;
            byte[] data$iv = segment$iv.data;
            int pos$iv = segment$iv.pos;

            int limit$iv;
            for(limit$iv = segment$iv.limit; pos$iv < limit$iv; ++seen$iv) {
               int digit$iv = 0;
               byte b$iv = data$iv[pos$iv];
               if (b$iv >= 48 && b$iv <= 57) {
                  digit$iv = b$iv - 48;
               } else if (b$iv >= 97 && b$iv <= 102) {
                  digit$iv = b$iv - 97 + 10;
               } else {
                  if (b$iv < 65 || b$iv > 70) {
                     if (seen$iv == 0) {
                        throw new NumberFormatException("Expected leading [0-9a-fA-F] character but was 0x" + -SegmentedByteString.toHexString(b$iv));
                     }

                     done$iv = true;
                     break;
                  }

                  digit$iv = b$iv - 65 + 10;
               }

               if ((value$iv & -1152921504606846976L) != 0L) {
                  Buffer buffer$iv = (new Buffer()).writeHexadecimalUnsignedLong(value$iv).writeByte(b$iv);
                  throw new NumberFormatException("Number too large: " + buffer$iv.readUtf8());
               }

               value$iv <<= 4;
               value$iv |= (long)digit$iv;
               ++pos$iv;
            }

            if (pos$iv == limit$iv) {
               $this$commonReadHexadecimalUnsignedLong$iv.head = segment$iv.pop();
               SegmentPool.recycle(segment$iv);
            } else {
               segment$iv.pos = pos$iv;
            }
         } while(!done$iv && $this$commonReadHexadecimalUnsignedLong$iv.head != null);

         $this$commonReadHexadecimalUnsignedLong$iv.setSize$okio($this$commonReadHexadecimalUnsignedLong$iv.size() - (long)seen$iv);
         return value$iv;
      }
   }

   @NotNull
   public ByteString readByteString() {
      int $i$f$commonReadByteString = 0;
      return this.readByteString(this.size());
   }

   @NotNull
   public ByteString readByteString(long byteCount) throws EOFException {
      int $i$f$commonReadByteString = 0;
      if (byteCount < 0L || byteCount > 2147483647L) {
         int var7 = 0;
         String it$iv = "byteCount: " + byteCount;
         throw new IllegalArgumentException(it$iv.toString());
      } else if (this.size() < byteCount) {
         throw new EOFException();
      } else {
         ByteString var10000;
         if (byteCount >= 4096L) {
            ByteString var8 = this.snapshot((int)byteCount);
            int var9 = 0;
            this.skip(byteCount);
            var10000 = var8;
         } else {
            var10000 = new ByteString(this.readByteArray(byteCount));
         }

         return var10000;
      }
   }

   public int select(@NotNull Options options) {
      Intrinsics.checkNotNullParameter(options, "options");
      int $i$f$commonSelect = 0;
      int index$iv = -Buffer.selectPrefix$default(this, options, false, 2, (Object)null);
      int var10000;
      if (index$iv == -1) {
         var10000 = -1;
      } else {
         int selectedSize$iv = options.getByteStrings$okio()[index$iv].size();
         this.skip((long)selectedSize$iv);
         var10000 = index$iv;
      }

      return var10000;
   }

   @Nullable
   public <T> T select(@NotNull TypedOptions<T> options) {
      Intrinsics.checkNotNullParameter(options, "options");
      BufferedSource $this$commonSelect$iv = this;
      int $i$f$commonSelect = 0;
      int index$iv = $this$commonSelect$iv.select(options.getOptions$okio());
      return (T)(index$iv == -1 ? null : options.get(index$iv));
   }

   public void readFully(@NotNull Buffer sink, long byteCount) throws EOFException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadFully = 0;
      if (this.size() < byteCount) {
         sink.write(this, this.size());
         throw new EOFException();
      } else {
         sink.write(this, byteCount);
      }
   }

   public long readAll(@NotNull Sink sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonReadAll = 0;
      long byteCount$iv = this.size();
      if (byteCount$iv > 0L) {
         sink.write(this, byteCount$iv);
      }

      return byteCount$iv;
   }

   @NotNull
   public String readUtf8() {
      return this.readString(this.size, Charsets.UTF_8);
   }

   @NotNull
   public String readUtf8(long byteCount) throws EOFException {
      return this.readString(byteCount, Charsets.UTF_8);
   }

   @NotNull
   public String readString(@NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      return this.readString(this.size, charset);
   }

   @NotNull
   public String readString(long byteCount, @NotNull Charset charset) throws EOFException {
      Intrinsics.checkNotNullParameter(charset, "charset");
      if (byteCount < 0L || byteCount > 2147483647L) {
         int result = 0;
         String var7 = "byteCount: " + byteCount;
         throw new IllegalArgumentException(var7.toString());
      } else if (this.size < byteCount) {
         throw new EOFException();
      } else if (byteCount == 0L) {
         return "";
      } else {
         Segment var10000 = this.head;
         Intrinsics.checkNotNull(var10000);
         Segment s = var10000;
         if ((long)s.pos + byteCount > (long)s.limit) {
            return new String(this.readByteArray(byteCount), charset);
         } else {
            String result = new String(s.data, s.pos, (int)byteCount, charset);
            s.pos += (int)byteCount;
            this.size -= byteCount;
            if (s.pos == s.limit) {
               this.head = s.pop();
               SegmentPool.recycle(s);
            }

            return result;
         }
      }
   }

   @Nullable
   public String readUtf8Line() throws EOFException {
      int $i$f$commonReadUtf8Line = 0;
      long newline$iv = this.indexOf((byte)10);
      return newline$iv != -1L ? -Buffer.readUtf8Line(this, newline$iv) : (this.size() != 0L ? this.readUtf8(this.size()) : null);
   }

   @NotNull
   public String readUtf8LineStrict() throws EOFException {
      return this.readUtf8LineStrict(Long.MAX_VALUE);
   }

   @NotNull
   public String readUtf8LineStrict(long limit) throws EOFException {
      int $i$f$commonReadUtf8LineStrict = 0;
      if (limit < 0L) {
         int var7 = 0;
         String var17 = "limit < 0: " + limit;
         throw new IllegalArgumentException(var17.toString());
      } else {
         long scanLength$iv = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
         long newline$iv = this.indexOf((byte)10, 0L, scanLength$iv);
         String var10000;
         if (newline$iv != -1L) {
            var10000 = -Buffer.readUtf8Line(this, newline$iv);
         } else {
            if (scanLength$iv >= this.size() || this.getByte(scanLength$iv - 1L) != 13 || this.getByte(scanLength$iv) != 10) {
               Buffer data$iv = new Buffer();
               byte a$iv$iv = 32;
               long b$iv$iv = this.size();
               int $i$f$minOf = 0;
               this.copyTo(data$iv, 0L, Math.min((long)a$iv$iv, b$iv$iv));
               throw new EOFException("\\n not found: limit=" + Math.min(this.size(), limit) + " content=" + data$iv.readByteString().hex() + '…');
            }

            var10000 = -Buffer.readUtf8Line(this, scanLength$iv);
         }

         return var10000;
      }
   }

   public int readUtf8CodePoint() throws EOFException {
      Buffer $this$commonReadUtf8CodePoint$iv = this;
      int $i$f$commonReadUtf8CodePoint = 0;
      if (this.size() == 0L) {
         throw new EOFException();
      } else {
         byte b0$iv = this.getByte(0L);
         int codePoint$iv = 0;
         int byteCount$iv = 0;
         int min$iv = 0;
         int other$iv$iv = 128;
         int $i$f$and = 0;
         int var10000;
         if ((b0$iv & other$iv$iv) == 0) {
            other$iv$iv = 127;
            $i$f$and = 0;
            codePoint$iv = b0$iv & other$iv$iv;
            byteCount$iv = 1;
            min$iv = 0;
         } else {
            other$iv$iv = 224;
            $i$f$and = 0;
            if ((b0$iv & other$iv$iv) == 192) {
               other$iv$iv = 31;
               $i$f$and = 0;
               codePoint$iv = b0$iv & other$iv$iv;
               byteCount$iv = 2;
               min$iv = 128;
            } else {
               other$iv$iv = 240;
               $i$f$and = 0;
               if ((b0$iv & other$iv$iv) == 224) {
                  other$iv$iv = 15;
                  $i$f$and = 0;
                  codePoint$iv = b0$iv & other$iv$iv;
                  byteCount$iv = 3;
                  min$iv = 2048;
               } else {
                  other$iv$iv = 248;
                  $i$f$and = 0;
                  if ((b0$iv & other$iv$iv) != 240) {
                     this.skip(1L);
                     var10000 = 65533;
                     return var10000;
                  }

                  other$iv$iv = 7;
                  $i$f$and = 0;
                  codePoint$iv = b0$iv & other$iv$iv;
                  byteCount$iv = 4;
                  min$iv = 65536;
               }
            }
         }

         if (this.size() < (long)byteCount$iv) {
            throw new EOFException("size < " + byteCount$iv + ": " + this.size() + " (to read code point prefixed 0x" + -SegmentedByteString.toHexString(b0$iv) + ')');
         } else {
            int i$iv = 1;

            while(true) {
               if (i$iv >= byteCount$iv) {
                  $this$commonReadUtf8CodePoint$iv.skip((long)byteCount$iv);
                  var10000 = codePoint$iv > 1114111 ? '�' : ((55296 <= codePoint$iv ? codePoint$iv < 57344 : false) ? '�' : (codePoint$iv < min$iv ? '�' : codePoint$iv));
                  break;
               }

               other$iv$iv = $this$commonReadUtf8CodePoint$iv.getByte((long)i$iv);
               int other$iv$iv = 192;
               int $i$f$and = 0;
               if ((other$iv$iv & other$iv$iv) != 128) {
                  $this$commonReadUtf8CodePoint$iv.skip((long)i$iv);
                  var10000 = 65533;
                  break;
               }

               codePoint$iv <<= 6;
               other$iv$iv = 63;
               $i$f$and = 0;
               codePoint$iv |= other$iv$iv & other$iv$iv;
               ++i$iv;
            }

            return var10000;
         }
      }
   }

   @NotNull
   public byte[] readByteArray() {
      int $i$f$commonReadByteArray = 0;
      return this.readByteArray(this.size());
   }

   @NotNull
   public byte[] readByteArray(long byteCount) throws EOFException {
      int $i$f$commonReadByteArray = 0;
      if (byteCount < 0L || byteCount > 2147483647L) {
         int var7 = 0;
         String var9 = "byteCount: " + byteCount;
         throw new IllegalArgumentException(var9.toString());
      } else if (this.size() < byteCount) {
         throw new EOFException();
      } else {
         byte[] result$iv = new byte[(int)byteCount];
         this.readFully(result$iv);
         return result$iv;
      }
   }

   public int read(@NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      return this.read(sink, 0, sink.length);
   }

   public void readFully(@NotNull byte[] sink) throws EOFException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Buffer $this$commonReadFully$iv = this;
      byte[] sink$iv = sink;
      int $i$f$commonReadFully = 0;

      int read$iv;
      for(int offset$iv = 0; offset$iv < sink$iv.length; offset$iv += read$iv) {
         read$iv = $this$commonReadFully$iv.read(sink$iv, offset$iv, sink$iv.length - offset$iv);
         if (read$iv == -1) {
            throw new EOFException();
         }
      }

   }

   public int read(@NotNull byte[] sink, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      -SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      Segment var10000 = this.head;
      int var11;
      if (var10000 == null) {
         var11 = -1;
      } else {
         Segment s$iv = var10000;
         int toCopy$iv = Math.min(byteCount, s$iv.limit - s$iv.pos);
         ArraysKt.copyInto(s$iv.data, sink, offset, s$iv.pos, s$iv.pos + toCopy$iv);
         s$iv.pos += toCopy$iv;
         this.setSize$okio(this.size() - (long)toCopy$iv);
         if (s$iv.pos == s$iv.limit) {
            this.head = s$iv.pop();
            SegmentPool.recycle(s$iv);
         }

         var11 = toCopy$iv;
      }

      return var11;
   }

   public int read(@NotNull ByteBuffer sink) throws IOException {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Segment var10000 = this.head;
      if (var10000 == null) {
         return -1;
      } else {
         Segment s = var10000;
         int toCopy = Math.min(sink.remaining(), s.limit - s.pos);
         sink.put(s.data, s.pos, toCopy);
         s.pos += toCopy;
         this.size -= (long)toCopy;
         if (s.pos == s.limit) {
            this.head = s.pop();
            SegmentPool.recycle(s);
         }

         return toCopy;
      }
   }

   public final void clear() {
      int $i$f$commonClear = 0;
      this.skip(this.size());
   }

   public void skip(long byteCount) throws EOFException {
      Buffer $this$commonSkip$iv = this;
      int $i$f$commonSkip = 0;
      long byteCount$iv = byteCount;

      while(byteCount$iv > 0L) {
         Segment var10000 = $this$commonSkip$iv.head;
         if (var10000 == null) {
            throw new EOFException();
         }

         Segment head$iv = var10000;
         int b$iv$iv = head$iv.limit - head$iv.pos;
         int $i$f$minOf = 0;
         int toSkip$iv = (int)Math.min(byteCount$iv, (long)b$iv$iv);
         $this$commonSkip$iv.setSize$okio($this$commonSkip$iv.size() - (long)toSkip$iv);
         byteCount$iv -= (long)toSkip$iv;
         head$iv.pos += toSkip$iv;
         if (head$iv.pos == head$iv.limit) {
            $this$commonSkip$iv.head = head$iv.pop();
            SegmentPool.recycle(head$iv);
         }
      }

   }

   @NotNull
   public Buffer write(@NotNull ByteString byteString) {
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int offset$iv = 0;
      int byteCount$iv = byteString.size();
      int $i$f$commonWrite = 0;
      byteString.write$okio(this, offset$iv, byteCount$iv);
      return this;
   }

   @NotNull
   public Buffer write(@NotNull ByteString byteString, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(byteString, "byteString");
      int $i$f$commonWrite = 0;
      byteString.write$okio(this, offset, byteCount);
      return this;
   }

   @NotNull
   public Buffer writeUtf8(@NotNull String string) {
      Intrinsics.checkNotNullParameter(string, "string");
      return this.writeUtf8(string, 0, string.length());
   }

   @NotNull
   public Buffer writeUtf8(@NotNull String string, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter(string, "string");
      Buffer $this$commonWriteUtf8$iv = this;
      String string$iv = string;
      int endIndex$iv = endIndex;
      int $i$f$commonWriteUtf8 = 0;
      if (beginIndex < 0) {
         int var21 = 0;
         String var22 = "beginIndex < 0: " + beginIndex;
         throw new IllegalArgumentException(var22.toString());
      } else if (endIndex < beginIndex) {
         int var19 = 0;
         String var20 = "endIndex < beginIndex: " + endIndex + " < " + beginIndex;
         throw new IllegalArgumentException(var20.toString());
      } else if (endIndex > string.length()) {
         int var17 = 0;
         String c$iv = "endIndex > string.length: " + endIndex + " > " + string.length();
         throw new IllegalArgumentException(c$iv.toString());
      } else {
         int i$iv = beginIndex;

         while(i$iv < endIndex$iv) {
            int c$iv = string$iv.charAt(i$iv);
            if (c$iv < 128) {
               Segment tail$iv = $this$commonWriteUtf8$iv.writableSegment$okio(1);
               byte[] data$iv = tail$iv.data;
               int segmentOffset$iv = tail$iv.limit - i$iv;
               int runLimit$iv = Math.min(endIndex$iv, 8192 - segmentOffset$iv);

               for(data$iv[segmentOffset$iv + i$iv++] = (byte)c$iv; i$iv < runLimit$iv; data$iv[segmentOffset$iv + i$iv++] = (byte)c$iv) {
                  c$iv = string$iv.charAt(i$iv);
                  if (c$iv >= 128) {
                     break;
                  }
               }

               int runSize$iv = i$iv + segmentOffset$iv - tail$iv.limit;
               tail$iv.limit += runSize$iv;
               $this$commonWriteUtf8$iv.setSize$okio($this$commonWriteUtf8$iv.size() + (long)runSize$iv);
            } else if (c$iv < 2048) {
               Segment tail$iv = $this$commonWriteUtf8$iv.writableSegment$okio(2);
               tail$iv.data[tail$iv.limit] = (byte)(c$iv >> 6 | 192);
               tail$iv.data[tail$iv.limit + 1] = (byte)(c$iv & 63 | 128);
               tail$iv.limit += 2;
               $this$commonWriteUtf8$iv.setSize$okio($this$commonWriteUtf8$iv.size() + 2L);
               ++i$iv;
            } else if (c$iv >= 55296 && c$iv <= 57343) {
               int low$iv = i$iv + 1 < endIndex$iv ? string$iv.charAt(i$iv + 1) : 0;
               if (c$iv <= 56319 && (56320 <= low$iv ? low$iv < 57344 : false)) {
                  int codePoint$iv = 65536 + ((c$iv & 1023) << 10 | low$iv & 1023);
                  Segment tail$iv = $this$commonWriteUtf8$iv.writableSegment$okio(4);
                  tail$iv.data[tail$iv.limit] = (byte)(codePoint$iv >> 18 | 240);
                  tail$iv.data[tail$iv.limit + 1] = (byte)(codePoint$iv >> 12 & 63 | 128);
                  tail$iv.data[tail$iv.limit + 2] = (byte)(codePoint$iv >> 6 & 63 | 128);
                  tail$iv.data[tail$iv.limit + 3] = (byte)(codePoint$iv & 63 | 128);
                  tail$iv.limit += 4;
                  $this$commonWriteUtf8$iv.setSize$okio($this$commonWriteUtf8$iv.size() + 4L);
                  i$iv += 2;
               } else {
                  $this$commonWriteUtf8$iv.writeByte(63);
                  ++i$iv;
               }
            } else {
               Segment tail$iv = $this$commonWriteUtf8$iv.writableSegment$okio(3);
               tail$iv.data[tail$iv.limit] = (byte)(c$iv >> 12 | 224);
               tail$iv.data[tail$iv.limit + 1] = (byte)(c$iv >> 6 & 63 | 128);
               tail$iv.data[tail$iv.limit + 2] = (byte)(c$iv & 63 | 128);
               tail$iv.limit += 3;
               $this$commonWriteUtf8$iv.setSize$okio($this$commonWriteUtf8$iv.size() + 3L);
               ++i$iv;
            }
         }

         return $this$commonWriteUtf8$iv;
      }
   }

   @NotNull
   public Buffer writeUtf8CodePoint(int codePoint) {
      int $i$f$commonWriteUtf8CodePoint = 0;
      if (codePoint < 128) {
         this.writeByte(codePoint);
      } else if (codePoint < 2048) {
         Segment tail$iv = this.writableSegment$okio(2);
         tail$iv.data[tail$iv.limit] = (byte)(codePoint >> 6 | 192);
         tail$iv.data[tail$iv.limit + 1] = (byte)(codePoint & 63 | 128);
         tail$iv.limit += 2;
         this.setSize$okio(this.size() + 2L);
      } else if (55296 <= codePoint ? codePoint < 57344 : false) {
         this.writeByte(63);
      } else if (codePoint < 65536) {
         Segment tail$iv = this.writableSegment$okio(3);
         tail$iv.data[tail$iv.limit] = (byte)(codePoint >> 12 | 224);
         tail$iv.data[tail$iv.limit + 1] = (byte)(codePoint >> 6 & 63 | 128);
         tail$iv.data[tail$iv.limit + 2] = (byte)(codePoint & 63 | 128);
         tail$iv.limit += 3;
         this.setSize$okio(this.size() + 3L);
      } else {
         if (codePoint > 1114111) {
            throw new IllegalArgumentException("Unexpected code point: 0x" + -SegmentedByteString.toHexString(codePoint));
         }

         Segment tail$iv = this.writableSegment$okio(4);
         tail$iv.data[tail$iv.limit] = (byte)(codePoint >> 18 | 240);
         tail$iv.data[tail$iv.limit + 1] = (byte)(codePoint >> 12 & 63 | 128);
         tail$iv.data[tail$iv.limit + 2] = (byte)(codePoint >> 6 & 63 | 128);
         tail$iv.data[tail$iv.limit + 3] = (byte)(codePoint & 63 | 128);
         tail$iv.limit += 4;
         this.setSize$okio(this.size() + 4L);
      }

      return this;
   }

   @NotNull
   public Buffer writeString(@NotNull String string, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(string, "string");
      Intrinsics.checkNotNullParameter(charset, "charset");
      return this.writeString(string, 0, string.length(), charset);
   }

   @NotNull
   public Buffer writeString(@NotNull String string, int beginIndex, int endIndex, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(string, "string");
      Intrinsics.checkNotNullParameter(charset, "charset");
      if (beginIndex < 0) {
         int var10 = 0;
         String var11 = "beginIndex < 0: " + beginIndex;
         throw new IllegalArgumentException(var11.toString());
      } else if (endIndex < beginIndex) {
         int var8 = 0;
         String var9 = "endIndex < beginIndex: " + endIndex + " < " + beginIndex;
         throw new IllegalArgumentException(var9.toString());
      } else if (endIndex > string.length()) {
         int var6 = 0;
         String var7 = "endIndex > string.length: " + endIndex + " > " + string.length();
         throw new IllegalArgumentException(var7.toString());
      } else if (Intrinsics.areEqual((Object)charset, (Object)Charsets.UTF_8)) {
         return this.writeUtf8(string, beginIndex, endIndex);
      } else {
         String var10000 = string.substring(beginIndex, endIndex);
         Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
         byte[] var12 = var10000.getBytes(charset);
         Intrinsics.checkNotNullExpressionValue(var12, "getBytes(...)");
         byte[] data = var12;
         return this.write((byte[])data, 0, data.length);
      }
   }

   @NotNull
   public Buffer write(@NotNull byte[] source) {
      Intrinsics.checkNotNullParameter(source, "source");
      int $i$f$commonWrite = 0;
      return this.write((byte[])source, 0, source.length);
   }

   @NotNull
   public Buffer write(@NotNull byte[] source, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      Buffer $this$commonWrite$iv = this;
      byte[] source$iv = source;
      int $i$f$commonWrite = 0;
      int offset$iv = offset;
      -SegmentedByteString.checkOffsetAndCount((long)source.length, (long)offset, (long)byteCount);

      Segment tail$iv;
      int toCopy$iv;
      for(int limit$iv = offset + byteCount; offset$iv < limit$iv; tail$iv.limit += toCopy$iv) {
         tail$iv = $this$commonWrite$iv.writableSegment$okio(1);
         toCopy$iv = Math.min(limit$iv - offset$iv, 8192 - tail$iv.limit);
         ArraysKt.copyInto(source$iv, tail$iv.data, tail$iv.limit, offset$iv, offset$iv + toCopy$iv);
         offset$iv += toCopy$iv;
      }

      $this$commonWrite$iv.setSize$okio($this$commonWrite$iv.size() + (long)byteCount);
      return $this$commonWrite$iv;
   }

   public int write(@NotNull ByteBuffer source) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      int byteCount = source.remaining();

      Segment tail;
      int toCopy;
      for(int remaining = byteCount; remaining > 0; tail.limit += toCopy) {
         tail = this.writableSegment$okio(1);
         toCopy = Math.min(remaining, 8192 - tail.limit);
         source.get(tail.data, tail.limit, toCopy);
         remaining -= toCopy;
      }

      this.size += (long)byteCount;
      return byteCount;
   }

   public long writeAll(@NotNull Source source) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Buffer $this$commonWriteAll$iv = this;
      Source source$iv = source;
      int $i$f$commonWriteAll = 0;
      long totalBytesRead$iv = 0L;

      while(true) {
         long readCount$iv = source$iv.read($this$commonWriteAll$iv, 8192L);
         if (readCount$iv == -1L) {
            return totalBytesRead$iv;
         }

         totalBytesRead$iv += readCount$iv;
      }
   }

   @NotNull
   public Buffer write(@NotNull Source source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Buffer $this$commonWrite$iv = this;
      Source source$iv = source;
      int $i$f$commonWrite = 0;

      long read$iv;
      for(long byteCount$iv = byteCount; byteCount$iv > 0L; byteCount$iv -= read$iv) {
         read$iv = source$iv.read($this$commonWrite$iv, byteCount$iv);
         if (read$iv == -1L) {
            throw new EOFException();
         }
      }

      return $this$commonWrite$iv;
   }

   @NotNull
   public Buffer writeByte(int b) {
      int $i$f$commonWriteByte = 0;
      Segment tail$iv = this.writableSegment$okio(1);
      byte[] var10000 = tail$iv.data;
      int var6 = tail$iv.limit++;
      var10000[var6] = (byte)b;
      this.setSize$okio(this.size() + 1L);
      return this;
   }

   @NotNull
   public Buffer writeShort(int s) {
      int $i$f$commonWriteShort = 0;
      Segment tail$iv = this.writableSegment$okio(2);
      byte[] data$iv = tail$iv.data;
      int limit$iv = tail$iv.limit;
      data$iv[limit$iv++] = (byte)(s >>> 8 & 255);
      data$iv[limit$iv++] = (byte)(s & 255);
      tail$iv.limit = limit$iv;
      this.setSize$okio(this.size() + 2L);
      return this;
   }

   @NotNull
   public Buffer writeShortLe(int s) {
      return this.writeShort(-SegmentedByteString.reverseBytes((short)s));
   }

   @NotNull
   public Buffer writeInt(int i) {
      int $i$f$commonWriteInt = 0;
      Segment tail$iv = this.writableSegment$okio(4);
      byte[] data$iv = tail$iv.data;
      int limit$iv = tail$iv.limit;
      data$iv[limit$iv++] = (byte)(i >>> 24 & 255);
      data$iv[limit$iv++] = (byte)(i >>> 16 & 255);
      data$iv[limit$iv++] = (byte)(i >>> 8 & 255);
      data$iv[limit$iv++] = (byte)(i & 255);
      tail$iv.limit = limit$iv;
      this.setSize$okio(this.size() + 4L);
      return this;
   }

   @NotNull
   public Buffer writeIntLe(int i) {
      return this.writeInt(-SegmentedByteString.reverseBytes(i));
   }

   @NotNull
   public Buffer writeLong(long v) {
      int $i$f$commonWriteLong = 0;
      Segment tail$iv = this.writableSegment$okio(8);
      byte[] data$iv = tail$iv.data;
      int limit$iv = tail$iv.limit;
      data$iv[limit$iv++] = (byte)((int)(v >>> 56 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 48 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 40 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 32 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 24 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 16 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v >>> 8 & 255L));
      data$iv[limit$iv++] = (byte)((int)(v & 255L));
      tail$iv.limit = limit$iv;
      this.setSize$okio(this.size() + 8L);
      return this;
   }

   @NotNull
   public Buffer writeLongLe(long v) {
      return this.writeLong(-SegmentedByteString.reverseBytes(v));
   }

   @NotNull
   public Buffer writeDecimalLong(long v) {
      int $i$f$commonWriteDecimalLong = 0;
      long v$iv = v;
      Buffer var10000;
      if (v == 0L) {
         var10000 = this.writeByte(48);
      } else {
         boolean negative$iv = false;
         if (v < 0L) {
            v$iv = -v;
            if (v$iv < 0L) {
               var10000 = this.writeUtf8("-9223372036854775808");
               return var10000;
            }

            negative$iv = true;
         }

         int width$iv = -Buffer.access$countDigitsIn(v$iv);
         if (negative$iv) {
            ++width$iv;
         }

         Segment tail$iv = this.writableSegment$okio(width$iv);
         byte[] data$iv = tail$iv.data;

         int pos$iv;
         for(pos$iv = tail$iv.limit + width$iv; v$iv != 0L; v$iv /= (long)10) {
            int digit$iv = (int)(v$iv % (long)10);
            --pos$iv;
            data$iv[pos$iv] = -Buffer.getHEX_DIGIT_BYTES()[digit$iv];
         }

         if (negative$iv) {
            --pos$iv;
            data$iv[pos$iv] = 45;
         }

         tail$iv.limit += width$iv;
         this.setSize$okio(this.size() + (long)width$iv);
         var10000 = this;
      }

      return var10000;
   }

   @NotNull
   public Buffer writeHexadecimalUnsignedLong(long v) {
      int $i$f$commonWriteHexadecimalUnsignedLong = 0;
      long v$iv = v;
      Buffer var10000;
      if (v == 0L) {
         var10000 = this.writeByte(48);
      } else {
         long x$iv = v | v >>> 1;
         x$iv |= x$iv >>> 2;
         x$iv |= x$iv >>> 4;
         x$iv |= x$iv >>> 8;
         x$iv |= x$iv >>> 16;
         x$iv |= x$iv >>> 32;
         x$iv -= x$iv >>> 1 & 6148914691236517205L;
         x$iv = (x$iv >>> 2 & 3689348814741910323L) + (x$iv & 3689348814741910323L);
         x$iv = (x$iv >>> 4) + x$iv & 1085102592571150095L;
         x$iv += x$iv >>> 8;
         x$iv += x$iv >>> 16;
         x$iv = (x$iv & 63L) + (x$iv >>> 32 & 63L);
         int width$iv = (int)((x$iv + (long)3) / (long)4);
         Segment tail$iv = this.writableSegment$okio(width$iv);
         byte[] data$iv = tail$iv.data;
         int pos$iv = tail$iv.limit + width$iv - 1;

         for(int start$iv = tail$iv.limit; pos$iv >= start$iv; --pos$iv) {
            data$iv[pos$iv] = -Buffer.getHEX_DIGIT_BYTES()[(int)(v$iv & 15L)];
            v$iv >>>= 4;
         }

         tail$iv.limit += width$iv;
         this.setSize$okio(this.size() + (long)width$iv);
         var10000 = this;
      }

      return var10000;
   }

   @NotNull
   public final Segment writableSegment$okio(int minimumCapacity) {
      int $i$f$commonWritableSegment = 0;
      if (minimumCapacity < 1 || minimumCapacity > 8192) {
         int var5 = 0;
         String var7 = "unexpected capacity";
         throw new IllegalArgumentException(var7.toString());
      } else {
         Segment var10000;
         if (this.head == null) {
            Segment result$iv = SegmentPool.take();
            this.head = result$iv;
            result$iv.prev = result$iv;
            result$iv.next = result$iv;
            var10000 = result$iv;
         } else {
            var10000 = this.head;
            Intrinsics.checkNotNull(var10000);
            Segment tail$iv = var10000.prev;
            Intrinsics.checkNotNull(tail$iv);
            if (tail$iv.limit + minimumCapacity > 8192 || !tail$iv.owner) {
               tail$iv = tail$iv.push(SegmentPool.take());
            }

            var10000 = tail$iv;
         }

         return var10000;
      }
   }

   public void write(@NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      Buffer $this$commonWrite$iv = this;
      Buffer source$iv = source;
      int $i$f$commonWrite = 0;
      long byteCount$iv = byteCount;
      if (source == this) {
         int var11 = 0;
         String var16 = "source == this";
         throw new IllegalArgumentException(var16.toString());
      } else {
         -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);

         while(byteCount$iv > 0L) {
            Segment var10001 = source$iv.head;
            Intrinsics.checkNotNull(var10001);
            int var22 = var10001.limit;
            Segment var10002 = source$iv.head;
            Intrinsics.checkNotNull(var10002);
            if (byteCount$iv < (long)(var22 - var10002.pos)) {
               Segment var19;
               if ($this$commonWrite$iv.head != null) {
                  var19 = $this$commonWrite$iv.head;
                  Intrinsics.checkNotNull(var19);
                  var19 = var19.prev;
               } else {
                  var19 = null;
               }

               Segment tail$iv = var19;
               if (tail$iv != null && tail$iv.owner && byteCount$iv + (long)tail$iv.limit - (long)(tail$iv.shared ? 0 : tail$iv.pos) <= 8192L) {
                  var19 = source$iv.head;
                  Intrinsics.checkNotNull(var19);
                  var19.writeTo(tail$iv, (int)byteCount$iv);
                  source$iv.setSize$okio(source$iv.size() - byteCount$iv);
                  $this$commonWrite$iv.setSize$okio($this$commonWrite$iv.size() + byteCount$iv);
                  break;
               }

               Segment var23 = source$iv.head;
               Intrinsics.checkNotNull(var23);
               source$iv.head = var23.split((int)byteCount$iv);
            }

            Segment segmentToMove$iv = source$iv.head;
            Intrinsics.checkNotNull(segmentToMove$iv);
            long movedByteCount$iv = (long)(segmentToMove$iv.limit - segmentToMove$iv.pos);
            source$iv.head = segmentToMove$iv.pop();
            if ($this$commonWrite$iv.head == null) {
               $this$commonWrite$iv.head = segmentToMove$iv;
               segmentToMove$iv.prev = segmentToMove$iv;
               segmentToMove$iv.next = segmentToMove$iv.prev;
            } else {
               Segment var20 = $this$commonWrite$iv.head;
               Intrinsics.checkNotNull(var20);
               Segment tail$iv = var20.prev;
               Intrinsics.checkNotNull(tail$iv);
               tail$iv = tail$iv.push(segmentToMove$iv);
               tail$iv.compact();
            }

            source$iv.setSize$okio(source$iv.size() - movedByteCount$iv);
            $this$commonWrite$iv.setSize$okio($this$commonWrite$iv.size() + movedByteCount$iv);
            byteCount$iv -= movedByteCount$iv;
         }

      }
   }

   public long read(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      long byteCount$iv = 0L;
      byteCount$iv = byteCount;
      if (byteCount < 0L) {
         int var9 = 0;
         String var12 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var12.toString());
      } else {
         long var10000;
         if (this.size() == 0L) {
            var10000 = -1L;
         } else {
            if (byteCount > this.size()) {
               byteCount$iv = this.size();
            }

            sink.write(this, byteCount$iv);
            var10000 = byteCount$iv;
         }

         return var10000;
      }
   }

   public long indexOf(byte b) {
      return this.indexOf(b, 0L, Long.MAX_VALUE);
   }

   public long indexOf(byte b, long fromIndex) {
      return this.indexOf(b, fromIndex, Long.MAX_VALUE);
   }

   public long indexOf(byte b, long fromIndex, long toIndex) {
      byte b$iv = b;
      int $i$f$commonIndexOf = 0;
      long fromIndex$iv = 0L;
      fromIndex$iv = fromIndex;
      long toIndex$iv = 0L;
      toIndex$iv = toIndex;
      if (!(0L <= fromIndex ? fromIndex <= toIndex : false)) {
         int var13 = 0;
         String var46 = "size=" + this.size() + " fromIndex=" + fromIndex + " toIndex=" + toIndex;
         throw new IllegalArgumentException(var46.toString());
      } else {
         if (toIndex > this.size()) {
            toIndex$iv = this.size();
         }

         long var10000;
         if (fromIndex == toIndex$iv) {
            var10000 = -1L;
         } else {
            long fromIndex$iv$iv = fromIndex;
            int $i$f$seek = 0;
            Segment var52 = this.head;
            if (var52 == null) {
               long var18 = -1L;
               Segment s$iv = null;
               int var21 = 0;
               var10000 = -1L;
            } else {
               Segment s$iv$iv = var52;
               if (this.size() - fromIndex < fromIndex) {
                  long offset$iv$iv;
                  for(offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(s$iv$iv.limit - s$iv$iv.pos)) {
                     var52 = s$iv$iv.prev;
                     Intrinsics.checkNotNull(var52);
                     s$iv$iv = var52;
                  }

                  int var47 = 0;
                  if (s$iv$iv == null) {
                     var10000 = -1L;
                  } else {
                     Segment s$iv = s$iv$iv;

                     for(long offset$iv = offset$iv$iv; offset$iv < toIndex$iv; s$iv = var52) {
                        byte[] data$iv = s$iv.data;
                        int limit$iv = (int)Math.min((long)s$iv.limit, (long)s$iv.pos + toIndex$iv - offset$iv);

                        for(int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv); pos$iv < limit$iv; ++pos$iv) {
                           if (data$iv[pos$iv] == b$iv) {
                              var10000 = (long)(pos$iv - s$iv.pos) + offset$iv;
                              return var10000;
                           }
                        }

                        offset$iv += (long)(s$iv.limit - s$iv.pos);
                        fromIndex$iv = offset$iv;
                        var52 = s$iv.next;
                        Intrinsics.checkNotNull(var52);
                     }

                     var10000 = -1L;
                  }
               } else {
                  long offset$iv$iv = 0L;

                  while(true) {
                     long nextOffset$iv$iv = offset$iv$iv + (long)(s$iv$iv.limit - s$iv$iv.pos);
                     if (nextOffset$iv$iv > fromIndex$iv$iv) {
                        int var36 = 0;
                        if (s$iv$iv == null) {
                           var10000 = -1L;
                        } else {
                           Segment s$iv = s$iv$iv;

                           for(long offset$iv = offset$iv$iv; offset$iv < toIndex$iv; s$iv = var52) {
                              byte[] data$iv = s$iv.data;
                              int limit$iv = (int)Math.min((long)s$iv.limit, (long)s$iv.pos + toIndex$iv - offset$iv);

                              for(int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv); pos$iv < limit$iv; ++pos$iv) {
                                 if (data$iv[pos$iv] == b$iv) {
                                    var10000 = (long)(pos$iv - s$iv.pos) + offset$iv;
                                    return var10000;
                                 }
                              }

                              offset$iv += (long)(s$iv.limit - s$iv.pos);
                              fromIndex$iv = offset$iv;
                              var52 = s$iv.next;
                              Intrinsics.checkNotNull(var52);
                           }

                           var10000 = -1L;
                        }
                        break;
                     }

                     var52 = s$iv$iv.next;
                     Intrinsics.checkNotNull(var52);
                     s$iv$iv = var52;
                     offset$iv$iv = nextOffset$iv$iv;
                  }
               }
            }
         }

         return var10000;
      }
   }

   public long indexOf(@NotNull ByteString bytes) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.indexOf(bytes, 0L);
   }

   public long indexOf(@NotNull ByteString bytes, long fromIndex) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.indexOf(bytes, fromIndex, Long.MAX_VALUE);
   }

   public long indexOf(@NotNull ByteString bytes, long fromIndex, long toIndex) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return -Buffer.commonIndexOf$default(this, bytes, fromIndex, toIndex, 0, 0, 24, (Object)null);
   }

   public long indexOfElement(@NotNull ByteString targetBytes) {
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      return this.indexOfElement(targetBytes, 0L);
   }

   public long indexOfElement(@NotNull ByteString targetBytes, long fromIndex) {
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      Buffer $this$commonIndexOfElement$iv = this;
      int $i$f$commonIndexOfElement = 0;
      long fromIndex$iv = 0L;
      fromIndex$iv = fromIndex;
      if (fromIndex < 0L) {
         int var9 = 0;
         String var45 = "fromIndex < 0: " + fromIndex;
         throw new IllegalArgumentException(var45.toString());
      } else {
         long fromIndex$iv$iv = fromIndex;
         int $i$f$seek = 0;
         Segment var10000 = this.head;
         long var67;
         if (var10000 == null) {
            long var14 = -1L;
            Segment s$iv = null;
            int var17 = 0;
            var67 = -1L;
         } else {
            Segment s$iv$iv = var10000;
            if (this.size() - fromIndex < fromIndex) {
               long offset$iv$iv;
               for(offset$iv$iv = this.size(); offset$iv$iv > fromIndex$iv$iv; offset$iv$iv -= (long)(s$iv$iv.limit - s$iv$iv.pos)) {
                  Segment var68 = s$iv$iv.prev;
                  Intrinsics.checkNotNull(var68);
                  s$iv$iv = var68;
               }

               int var46 = 0;
               if (s$iv$iv == null) {
                  var67 = -1L;
               } else {
                  Segment s$iv = s$iv$iv;
                  long offset$iv = offset$iv$iv;
                  Segment var70;
                  if (targetBytes.size() != 2) {
                     for(byte[] targetByteArray$iv = targetBytes.internalArray$okio(); offset$iv < $this$commonIndexOfElement$iv.size(); s$iv = var70) {
                        byte[] data$iv = s$iv.data;
                        int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv);

                        for(int limit$iv = s$iv.limit; pos$iv < limit$iv; ++pos$iv) {
                           int b$iv = data$iv[pos$iv];
                           int pos$iv = 0;

                           for(int var27 = targetByteArray$iv.length; pos$iv < var27; ++pos$iv) {
                              byte t$iv = targetByteArray$iv[pos$iv];
                              if (b$iv == t$iv) {
                                 var67 = (long)(pos$iv - s$iv.pos) + offset$iv;
                                 return var67;
                              }
                           }
                        }

                        offset$iv += (long)(s$iv.limit - s$iv.pos);
                        fromIndex$iv = offset$iv;
                        var70 = s$iv.next;
                        Intrinsics.checkNotNull(var70);
                     }
                  } else {
                     byte b0$iv = targetBytes.getByte(0);

                     for(byte b1$iv = targetBytes.getByte(1); offset$iv < $this$commonIndexOfElement$iv.size(); s$iv = var70) {
                        byte[] data$iv = s$iv.data;
                        int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv);

                        for(int limit$iv = s$iv.limit; pos$iv < limit$iv; ++pos$iv) {
                           int b$iv = data$iv[pos$iv];
                           if (b$iv == b0$iv || b$iv == b1$iv) {
                              var67 = (long)(pos$iv - s$iv.pos) + offset$iv;
                              return var67;
                           }
                        }

                        offset$iv += (long)(s$iv.limit - s$iv.pos);
                        fromIndex$iv = offset$iv;
                        var70 = s$iv.next;
                        Intrinsics.checkNotNull(var70);
                     }
                  }

                  var67 = -1L;
               }
            } else {
               long offset$iv$iv = 0L;

               while(true) {
                  long nextOffset$iv$iv = offset$iv$iv + (long)(s$iv$iv.limit - s$iv$iv.pos);
                  if (nextOffset$iv$iv > fromIndex$iv$iv) {
                     int var37 = 0;
                     if (s$iv$iv == null) {
                        var67 = -1L;
                     } else {
                        Segment s$iv = s$iv$iv;
                        long offset$iv = offset$iv$iv;
                        Segment var73;
                        if (targetBytes.size() != 2) {
                           for(byte[] targetByteArray$iv = targetBytes.internalArray$okio(); offset$iv < $this$commonIndexOfElement$iv.size(); s$iv = var73) {
                              byte[] data$iv = s$iv.data;
                              int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv);

                              for(int limit$iv = s$iv.limit; pos$iv < limit$iv; ++pos$iv) {
                                 int b$iv = data$iv[pos$iv];
                                 int var64 = 0;

                                 for(int var41 = targetByteArray$iv.length; var64 < var41; ++var64) {
                                    byte t$iv = targetByteArray$iv[var64];
                                    if (b$iv == t$iv) {
                                       var67 = (long)(pos$iv - s$iv.pos) + offset$iv;
                                       return var67;
                                    }
                                 }
                              }

                              offset$iv += (long)(s$iv.limit - s$iv.pos);
                              fromIndex$iv = offset$iv;
                              var73 = s$iv.next;
                              Intrinsics.checkNotNull(var73);
                           }
                        } else {
                           byte b0$iv = targetBytes.getByte(0);

                           for(byte b1$iv = targetBytes.getByte(1); offset$iv < $this$commonIndexOfElement$iv.size(); s$iv = var73) {
                              byte[] data$iv = s$iv.data;
                              int pos$iv = (int)((long)s$iv.pos + fromIndex$iv - offset$iv);

                              for(int limit$iv = s$iv.limit; pos$iv < limit$iv; ++pos$iv) {
                                 int b$iv = data$iv[pos$iv];
                                 if (b$iv == b0$iv || b$iv == b1$iv) {
                                    var67 = (long)(pos$iv - s$iv.pos) + offset$iv;
                                    return var67;
                                 }
                              }

                              offset$iv += (long)(s$iv.limit - s$iv.pos);
                              fromIndex$iv = offset$iv;
                              var73 = s$iv.next;
                              Intrinsics.checkNotNull(var73);
                           }
                        }

                        var67 = -1L;
                     }
                     break;
                  }

                  Segment var71 = s$iv$iv.next;
                  Intrinsics.checkNotNull(var71);
                  s$iv$iv = var71;
                  offset$iv$iv = nextOffset$iv$iv;
               }
            }
         }

         return var67;
      }
   }

   public boolean rangeEquals(long offset, @NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.rangeEquals(offset, bytes, 0, bytes.size());
   }

   public boolean rangeEquals(long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      int $i$f$commonRangeEquals = 0;
      boolean var10000;
      if (byteCount < 0) {
         var10000 = false;
      } else if (offset >= 0L && offset + (long)byteCount <= this.size()) {
         if (bytesOffset >= 0 && bytesOffset + byteCount <= bytes.size()) {
            if (byteCount == 0) {
               var10000 = true;
            } else {
               long var13 = offset + 1L;
               var10000 = -Buffer.commonIndexOf(this, bytes, offset, var13, bytesOffset, byteCount) != -1L;
            }
         } else {
            var10000 = false;
         }
      } else {
         var10000 = false;
      }

      return var10000;
   }

   public void flush() {
   }

   public boolean isOpen() {
      return true;
   }

   public void close() {
   }

   @NotNull
   public Timeout timeout() {
      return Timeout.NONE;
   }

   @NotNull
   public final ByteString md5() {
      return this.digest("MD5");
   }

   @NotNull
   public final ByteString sha1() {
      return this.digest("SHA-1");
   }

   @NotNull
   public final ByteString sha256() {
      return this.digest("SHA-256");
   }

   @NotNull
   public final ByteString sha512() {
      return this.digest("SHA-512");
   }

   private final ByteString digest(String algorithm) {
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      Segment var10000 = this.head;
      if (var10000 != null) {
         Segment head = var10000;
         int var4 = 0;
         messageDigest.update(head.data, head.pos, head.limit - head.pos);
         var10000 = head.next;
         Intrinsics.checkNotNull(var10000);

         for(Segment s = var10000; s != head; s = var10000) {
            messageDigest.update(s.data, s.pos, s.limit - s.pos);
            var10000 = s.next;
            Intrinsics.checkNotNull(var10000);
         }
      }

      byte[] var10002 = messageDigest.digest();
      Intrinsics.checkNotNullExpressionValue(var10002, "digest(...)");
      return new ByteString(var10002);
   }

   @NotNull
   public final ByteString hmacSha1(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac("HmacSHA1", key);
   }

   @NotNull
   public final ByteString hmacSha256(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac("HmacSHA256", key);
   }

   @NotNull
   public final ByteString hmacSha512(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac("HmacSHA512", key);
   }

   private final ByteString hmac(String algorithm, ByteString key) {
      try {
         Mac mac = Mac.getInstance(algorithm);
         mac.init((Key)(new SecretKeySpec(key.internalArray$okio(), algorithm)));
         Segment var10000 = this.head;
         if (var10000 != null) {
            Segment head = var10000;
            int var6 = 0;
            mac.update(head.data, head.pos, head.limit - head.pos);
            var10000 = head.next;
            Intrinsics.checkNotNull(var10000);

            for(Segment s = var10000; s != head; s = var10000) {
               mac.update(s.data, s.pos, s.limit - s.pos);
               var10000 = s.next;
               Intrinsics.checkNotNull(var10000);
            }
         }

         byte[] var10002 = mac.doFinal();
         Intrinsics.checkNotNullExpressionValue(var10002, "doFinal(...)");
         return new ByteString(var10002);
      } catch (InvalidKeyException e) {
         throw new IllegalArgumentException((Throwable)e);
      }
   }

   public boolean equals(@Nullable Object other) {
      Buffer $this$commonEquals$iv = this;
      int $i$f$commonEquals = 0;
      boolean var10000;
      if (this == other) {
         var10000 = true;
      } else if (!(other instanceof Buffer)) {
         var10000 = false;
      } else if (this.size() != ((Buffer)other).size()) {
         var10000 = false;
      } else if (this.size() == 0L) {
         var10000 = true;
      } else {
         Segment var18 = this.head;
         Intrinsics.checkNotNull(var18);
         Segment sa$iv = var18;
         var18 = ((Buffer)other).head;
         Intrinsics.checkNotNull(var18);
         Segment sb$iv = var18;
         int posA$iv = sa$iv.pos;
         int posB$iv = sb$iv.pos;
         long pos$iv = 0L;

         long count$iv;
         for(count$iv = 0L; pos$iv < $this$commonEquals$iv.size(); pos$iv += count$iv) {
            count$iv = (long)Math.min(sa$iv.limit - posA$iv, sb$iv.limit - posB$iv);
            long i$iv = 0L;

            for(long var15 = count$iv; i$iv < var15; ++i$iv) {
               if (sa$iv.data[posA$iv++] != sb$iv.data[posB$iv++]) {
                  var10000 = false;
                  return var10000;
               }
            }

            if (posA$iv == sa$iv.limit) {
               var18 = sa$iv.next;
               Intrinsics.checkNotNull(var18);
               sa$iv = var18;
               posA$iv = sa$iv.pos;
            }

            if (posB$iv == sb$iv.limit) {
               var18 = sb$iv.next;
               Intrinsics.checkNotNull(var18);
               sb$iv = var18;
               posB$iv = sb$iv.pos;
            }
         }

         var10000 = true;
      }

      return var10000;
   }

   public int hashCode() {
      Buffer $this$commonHashCode$iv = this;
      int $i$f$commonHashCode = 0;
      Segment var10000 = this.head;
      int var7;
      if (var10000 == null) {
         var7 = 0;
      } else {
         Segment s$iv = var10000;
         int result$iv = 1;

         do {
            int pos$iv = s$iv.pos;

            for(int limit$iv = s$iv.limit; pos$iv < limit$iv; ++pos$iv) {
               result$iv = 31 * result$iv + s$iv.data[pos$iv];
            }

            Segment var8 = s$iv.next;
            Intrinsics.checkNotNull(var8);
            s$iv = var8;
         } while(s$iv != $this$commonHashCode$iv.head);

         var7 = result$iv;
      }

      return var7;
   }

   @NotNull
   public String toString() {
      return this.snapshot().toString();
   }

   @NotNull
   public final Buffer copy() {
      int $i$f$commonCopy = 0;
      Buffer result$iv = new Buffer();
      Buffer var10000;
      if (this.size() == 0L) {
         var10000 = result$iv;
      } else {
         Segment var7 = this.head;
         Intrinsics.checkNotNull(var7);
         Segment head$iv = var7;
         Segment headCopy$iv = head$iv.sharedCopy();
         result$iv.head = headCopy$iv;
         headCopy$iv.prev = result$iv.head;
         headCopy$iv.next = headCopy$iv.prev;

         for(Segment s$iv = head$iv.next; s$iv != head$iv; s$iv = s$iv.next) {
            var7 = headCopy$iv.prev;
            Intrinsics.checkNotNull(var7);
            Intrinsics.checkNotNull(s$iv);
            var7.push(s$iv.sharedCopy());
         }

         result$iv.setSize$okio(this.size());
         var10000 = result$iv;
      }

      return var10000;
   }

   @NotNull
   public Buffer clone() {
      return this.copy();
   }

   @NotNull
   public final ByteString snapshot() {
      int $i$f$commonSnapshot = 0;
      if (this.size() > 2147483647L) {
         int var3 = 0;
         String var4 = "size > Int.MAX_VALUE: " + this.size();
         throw new IllegalStateException(var4.toString());
      } else {
         return this.snapshot((int)this.size());
      }
   }

   @NotNull
   public final ByteString snapshot(int byteCount) {
      int byteCount$iv = byteCount;
      int $i$f$commonSnapshot = 0;
      ByteString var10000;
      if (byteCount == 0) {
         var10000 = ByteString.EMPTY;
      } else {
         -SegmentedByteString.checkOffsetAndCount(this.size(), 0L, (long)byteCount);
         int offset$iv = 0;
         int segmentCount$iv = 0;

         for(Segment s$iv = this.head; offset$iv < byteCount$iv; s$iv = s$iv.next) {
            Intrinsics.checkNotNull(s$iv);
            if (s$iv.limit == s$iv.pos) {
               throw new AssertionError("s.limit == s.pos");
            }

            offset$iv += s$iv.limit - s$iv.pos;
            ++segmentCount$iv;
         }

         byte[][] segments$iv = new byte[segmentCount$iv][];
         int[] directory$iv = new int[segmentCount$iv * 2];
         offset$iv = 0;
         segmentCount$iv = 0;

         for(Segment var12 = this.head; offset$iv < byteCount$iv; var12 = var12.next) {
            Intrinsics.checkNotNull(var12);
            segments$iv[segmentCount$iv] = var12.data;
            offset$iv += var12.limit - var12.pos;
            directory$iv[segmentCount$iv] = Math.min(offset$iv, byteCount$iv);
            directory$iv[segmentCount$iv + ((Object[])segments$iv).length] = var12.pos;
            var12.shared = true;
            ++segmentCount$iv;
         }

         var10000 = new SegmentedByteString(segments$iv, directory$iv);
      }

      return var10000;
   }

   @JvmOverloads
   @NotNull
   public final UnsafeCursor readUnsafe(@NotNull UnsafeCursor unsafeCursor) {
      Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
      return -Buffer.commonReadUnsafe(this, unsafeCursor);
   }

   // $FF: synthetic method
   public static UnsafeCursor readUnsafe$default(Buffer var0, UnsafeCursor var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = -SegmentedByteString.getDEFAULT__new_UnsafeCursor();
      }

      return var0.readUnsafe(var1);
   }

   @JvmOverloads
   @NotNull
   public final UnsafeCursor readAndWriteUnsafe(@NotNull UnsafeCursor unsafeCursor) {
      Intrinsics.checkNotNullParameter(unsafeCursor, "unsafeCursor");
      return -Buffer.commonReadAndWriteUnsafe(this, unsafeCursor);
   }

   // $FF: synthetic method
   public static UnsafeCursor readAndWriteUnsafe$default(Buffer var0, UnsafeCursor var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = -SegmentedByteString.getDEFAULT__new_UnsafeCursor();
      }

      return var0.readAndWriteUnsafe(var1);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to operator function",
      replaceWith = @ReplaceWith(
   expression = "this[index]",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_getByte"
   )
   public final byte _deprecated_getByte/* $FF was: -deprecated_getByte*/(long index) {
      return this.getByte(index);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "size",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_size"
   )
   public final long _deprecated_size/* $FF was: -deprecated_size*/() {
      return this.size;
   }

   @JvmOverloads
   @NotNull
   public final Buffer copyTo(@NotNull OutputStream out, long offset) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      return copyTo$default(this, (OutputStream)out, offset, 0L, 4, (Object)null);
   }

   @JvmOverloads
   @NotNull
   public final Buffer copyTo(@NotNull OutputStream out) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      return copyTo$default(this, (OutputStream)out, 0L, 0L, 6, (Object)null);
   }

   @JvmOverloads
   @NotNull
   public final Buffer writeTo(@NotNull OutputStream out) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      return writeTo$default(this, out, 0L, 2, (Object)null);
   }

   @JvmOverloads
   @NotNull
   public final UnsafeCursor readUnsafe() {
      return readUnsafe$default(this, (UnsafeCursor)null, 1, (Object)null);
   }

   @JvmOverloads
   @NotNull
   public final UnsafeCursor readAndWriteUnsafe() {
      return readAndWriteUnsafe$default(this, (UnsafeCursor)null, 1, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0015\u001a\u00020\u0013J\u000e\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u000fJ\u000e\u0010\u0019\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u0013J\b\u0010\u001b\u001a\u00020\u001cH\u0016R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u0012\u0010\u000e\u001a\u00020\u000f8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00118\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0012\u001a\u00020\u00138\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0014\u001a\u00020\u00138\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001d"},
      d2 = {"Lokio/Buffer$UnsafeCursor;", "Ljava/io/Closeable;", "<init>", "()V", "buffer", "Lokio/Buffer;", "readWrite", "", "segment", "Lokio/Segment;", "getSegment$okio", "()Lokio/Segment;", "setSegment$okio", "(Lokio/Segment;)V", "offset", "", "data", "", "start", "", "end", "next", "seek", "resizeBuffer", "newSize", "expandBuffer", "minByteCount", "close", "", "okio"}
   )
   @SourceDebugExtension({"SMAP\nBuffer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Buffer.kt\nokio/Buffer$UnsafeCursor\n+ 2 Buffer.kt\nokio/internal/-Buffer\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,649:1\n1549#2:650\n1550#2:652\n1554#2:653\n1555#2,68:655\n1626#2:723\n1627#2,32:725\n1659#2,18:758\n1680#2:776\n1681#2,18:778\n1703#2:796\n1705#2,7:798\n1#3:651\n1#3:654\n1#3:724\n1#3:777\n1#3:797\n85#4:757\n*S KotlinDebug\n*F\n+ 1 Buffer.kt\nokio/Buffer$UnsafeCursor\n*L\n636#1:650\n636#1:652\n638#1:653\n638#1:655,68\n640#1:723\n640#1:725,32\n640#1:758,18\n642#1:776\n642#1:778,18\n645#1:796\n645#1:798,7\n636#1:651\n638#1:654\n640#1:724\n642#1:777\n645#1:797\n640#1:757\n*E\n"})
   public static final class UnsafeCursor implements Closeable {
      @JvmField
      @Nullable
      public Buffer buffer;
      @JvmField
      public boolean readWrite;
      @Nullable
      private Segment segment;
      @JvmField
      public long offset = -1L;
      @JvmField
      @Nullable
      public byte[] data;
      @JvmField
      public int start = -1;
      @JvmField
      public int end = -1;

      @Nullable
      public final Segment getSegment$okio() {
         return this.segment;
      }

      public final void setSegment$okio(@Nullable Segment var1) {
         this.segment = var1;
      }

      public final int next() {
         int $i$f$commonNext = 0;
         long var10000 = this.offset;
         Buffer var10001 = this.buffer;
         Intrinsics.checkNotNull(var10001);
         if (var10000 == var10001.size()) {
            int var3 = 0;
            String var4 = "no more bytes";
            throw new IllegalStateException(var4.toString());
         } else {
            return this.offset == -1L ? this.seek(0L) : this.seek(this.offset + (long)(this.end - this.start));
         }
      }

      public final int seek(long offset) {
         long offset$iv = offset;
         int $i$f$commonSeek = 0;
         Buffer var10000 = this.buffer;
         if (var10000 == null) {
            int var7 = 0;
            String var21 = "not attached to a buffer";
            throw new IllegalStateException(var21.toString());
         } else {
            Buffer buffer$iv = var10000;
            if (offset >= -1L && offset <= buffer$iv.size()) {
               int var24;
               if (offset != -1L && offset != buffer$iv.size()) {
                  long min$iv = 0L;
                  long max$iv = buffer$iv.size();
                  Segment head$iv = buffer$iv.head;
                  Segment tail$iv = buffer$iv.head;
                  if (this.getSegment$okio() != null) {
                     long var25 = this.offset;
                     int var10001 = this.start;
                     Segment var10002 = this.getSegment$okio();
                     Intrinsics.checkNotNull(var10002);
                     long segmentOffset$iv = var25 - (long)(var10001 - var10002.pos);
                     if (segmentOffset$iv > offset) {
                        max$iv = segmentOffset$iv;
                        tail$iv = this.getSegment$okio();
                     } else {
                        min$iv = segmentOffset$iv;
                        head$iv = this.getSegment$okio();
                     }
                  }

                  Segment next$iv = null;
                  long nextOffset$iv = 0L;
                  if (max$iv - offset > offset - min$iv) {
                     next$iv = head$iv;
                     nextOffset$iv = min$iv;

                     while(true) {
                        Intrinsics.checkNotNull(next$iv);
                        if (offset$iv < nextOffset$iv + (long)(next$iv.limit - next$iv.pos)) {
                           break;
                        }

                        nextOffset$iv += (long)(next$iv.limit - next$iv.pos);
                        next$iv = next$iv.next;
                     }
                  } else {
                     next$iv = tail$iv;

                     for(nextOffset$iv = max$iv; nextOffset$iv > offset$iv; nextOffset$iv -= (long)(next$iv.limit - next$iv.pos)) {
                        Intrinsics.checkNotNull(next$iv);
                        next$iv = next$iv.prev;
                        Intrinsics.checkNotNull(next$iv);
                     }
                  }

                  if (this.readWrite) {
                     Intrinsics.checkNotNull(next$iv);
                     if (next$iv.shared) {
                        Segment unsharedNext$iv = next$iv.unsharedCopy();
                        if (buffer$iv.head == next$iv) {
                           buffer$iv.head = unsharedNext$iv;
                        }

                        next$iv = next$iv.push(unsharedNext$iv);
                        Segment var26 = next$iv.prev;
                        Intrinsics.checkNotNull(var26);
                        var26.pop();
                     }
                  }

                  this.setSegment$okio(next$iv);
                  this.offset = offset$iv;
                  Intrinsics.checkNotNull(next$iv);
                  this.data = next$iv.data;
                  this.start = next$iv.pos + (int)(offset$iv - nextOffset$iv);
                  this.end = next$iv.limit;
                  var24 = this.end - this.start;
               } else {
                  this.setSegment$okio((Segment)null);
                  this.offset = offset;
                  this.data = null;
                  this.start = -1;
                  this.end = -1;
                  var24 = -1;
               }

               return var24;
            } else {
               throw new ArrayIndexOutOfBoundsException("offset=" + offset + " > size=" + buffer$iv.size());
            }
         }
      }

      public final long resizeBuffer(long newSize) {
         UnsafeCursor $this$commonResizeBuffer$iv = this;
         int $i$f$commonResizeBuffer = 0;
         Buffer var10000 = this.buffer;
         if (var10000 == null) {
            int var25 = 0;
            String var26 = "not attached to a buffer";
            throw new IllegalStateException(var26.toString());
         } else {
            Buffer buffer$iv = var10000;
            if (!this.readWrite) {
               int var7 = 0;
               String var24 = "resizeBuffer() only permitted for read/write buffers";
               throw new IllegalStateException(var24.toString());
            } else {
               long oldSize$iv = buffer$iv.size();
               if (newSize <= oldSize$iv) {
                  if (newSize < 0L) {
                     int var11 = 0;
                     String var27 = "newSize < 0: " + newSize;
                     throw new IllegalArgumentException(var27.toString());
                  }

                  int tailSize$iv;
                  for(long bytesToSubtract$iv = oldSize$iv - newSize; bytesToSubtract$iv > 0L; bytesToSubtract$iv -= (long)tailSize$iv) {
                     Segment var29 = buffer$iv.head;
                     Intrinsics.checkNotNull(var29);
                     Segment tail$iv = var29.prev;
                     Intrinsics.checkNotNull(tail$iv);
                     tailSize$iv = tail$iv.limit - tail$iv.pos;
                     if ((long)tailSize$iv > bytesToSubtract$iv) {
                        tail$iv.limit -= (int)bytesToSubtract$iv;
                        break;
                     }

                     buffer$iv.head = tail$iv.pop();
                     SegmentPool.recycle(tail$iv);
                  }

                  this.setSegment$okio((Segment)null);
                  this.offset = newSize;
                  this.data = null;
                  this.start = -1;
                  this.end = -1;
               } else if (newSize > oldSize$iv) {
                  boolean needsToSeek$iv = true;
                  long bytesToAdd$iv = newSize - oldSize$iv;

                  while(bytesToAdd$iv > 0L) {
                     Segment tail$iv = buffer$iv.writableSegment$okio(1);
                     int b$iv$iv = 8192 - tail$iv.limit;
                     int $i$f$minOf = 0;
                     int segmentBytesToAdd$iv = (int)Math.min(bytesToAdd$iv, (long)b$iv$iv);
                     tail$iv.limit += segmentBytesToAdd$iv;
                     bytesToAdd$iv -= (long)segmentBytesToAdd$iv;
                     if (needsToSeek$iv) {
                        $this$commonResizeBuffer$iv.setSegment$okio(tail$iv);
                        $this$commonResizeBuffer$iv.offset = oldSize$iv;
                        $this$commonResizeBuffer$iv.data = tail$iv.data;
                        $this$commonResizeBuffer$iv.start = tail$iv.limit - segmentBytesToAdd$iv;
                        $this$commonResizeBuffer$iv.end = tail$iv.limit;
                        needsToSeek$iv = false;
                     }
                  }
               }

               buffer$iv.setSize$okio(newSize);
               return oldSize$iv;
            }
         }
      }

      public final long expandBuffer(int minByteCount) {
         int $i$f$commonExpandBuffer = 0;
         if (minByteCount <= 0) {
            int var13 = 0;
            String var14 = "minByteCount <= 0: " + minByteCount;
            throw new IllegalArgumentException(var14.toString());
         } else if (minByteCount > 8192) {
            int var5 = 0;
            String var12 = "minByteCount > Segment.SIZE: " + minByteCount;
            throw new IllegalArgumentException(var12.toString());
         } else {
            Buffer var10000 = this.buffer;
            if (var10000 == null) {
               int var16 = 0;
               String var17 = "not attached to a buffer";
               throw new IllegalStateException(var17.toString());
            } else {
               Buffer buffer$iv = var10000;
               if (!this.readWrite) {
                  int var6 = 0;
                  String var15 = "expandBuffer() only permitted for read/write buffers";
                  throw new IllegalStateException(var15.toString());
               } else {
                  long oldSize$iv = buffer$iv.size();
                  Segment tail$iv = buffer$iv.writableSegment$okio(minByteCount);
                  int result$iv = 8192 - tail$iv.limit;
                  tail$iv.limit = 8192;
                  buffer$iv.setSize$okio(oldSize$iv + (long)result$iv);
                  this.setSegment$okio(tail$iv);
                  this.offset = oldSize$iv;
                  this.data = tail$iv.data;
                  this.start = 8192 - result$iv;
                  this.end = 8192;
                  return (long)result$iv;
               }
            }
         }
      }

      public void close() {
         int $i$f$commonClose = 0;
         if (this.buffer == null) {
            int var3 = 0;
            String var4 = "not attached to a buffer";
            throw new IllegalStateException(var4.toString());
         } else {
            this.buffer = null;
            this.setSegment$okio((Segment)null);
            this.offset = -1L;
            this.data = null;
            this.start = -1;
            this.end = -1;
         }
      }
   }
}
