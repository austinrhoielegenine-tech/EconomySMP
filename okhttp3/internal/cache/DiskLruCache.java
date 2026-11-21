package okhttp3.internal.cache;

import java.io.Closeable;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.ExceptionsKt;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.platform.Platform;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.FileSystem;
import okio.ForwardingFileSystem;
import okio.ForwardingSource;
import okio.Okio;
import okio.Path;
import okio.Sink;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0083\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010)\n\u0002\b\u0005*\u00019\u0018\u0000 a2\u00020\u00012\u00020\u00022\u00020\u0003:\u0004^_`aB7\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\u0006\u0010;\u001a\u00020<J\b\u0010=\u001a\u00020<H\u0002J\b\u0010>\u001a\u00020!H\u0002J\u0010\u0010?\u001a\u00020<2\u0006\u0010@\u001a\u00020$H\u0002J\b\u0010A\u001a\u00020<H\u0002J\r\u0010B\u001a\u00020<H\u0000¢\u0006\u0002\bCJ\u0017\u0010D\u001a\b\u0018\u00010ER\u00020\u00002\u0006\u0010F\u001a\u00020$H\u0086\u0002J \u0010G\u001a\b\u0018\u00010HR\u00020\u00002\u0006\u0010F\u001a\u00020$2\b\b\u0002\u0010I\u001a\u00020\fH\u0007J\u0006\u0010\u001f\u001a\u00020\fJ!\u0010J\u001a\u00020<2\n\u0010K\u001a\u00060HR\u00020\u00002\u0006\u0010L\u001a\u00020+H\u0000¢\u0006\u0002\bMJ\b\u0010N\u001a\u00020+H\u0002J\u000e\u0010O\u001a\u00020+2\u0006\u0010F\u001a\u00020$J\u0019\u0010P\u001a\u00020+2\n\u0010Q\u001a\u00060%R\u00020\u0000H\u0000¢\u0006\u0002\bRJ\b\u0010S\u001a\u00020<H\u0002J\b\u0010T\u001a\u00020<H\u0016J\u0006\u0010U\u001a\u00020+J\b\u0010V\u001a\u00020<H\u0016J\u0006\u0010W\u001a\u00020<J\b\u0010X\u001a\u00020+H\u0002J\u0006\u0010Y\u001a\u00020<J\u0006\u0010Z\u001a\u00020<J\u0010\u0010[\u001a\u00020<2\u0006\u0010F\u001a\u00020$H\u0002J\u0010\u0010\\\u001a\f\u0012\b\u0012\u00060ER\u00020\u00000]R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R$\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0017\u001a\u00020\f@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u000e\u0010\u001c\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R8\u0010\"\u001a&\u0012\u0004\u0012\u00020$\u0012\b\u0012\u00060%R\u00020\u00000#j\u0012\u0012\u0004\u0012\u00020$\u0012\b\u0012\u00060%R\u00020\u0000`&X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b'\u0010(R\u000e\u0010)\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010.\u001a\u00020+X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u00100\"\u0004\b1\u00102R\u000e\u00103\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u00020+X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00105\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000207X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u00108\u001a\u000209X\u0082\u0004¢\u0006\u0004\n\u0002\u0010:¨\u0006b"},
   d2 = {"Lokhttp3/internal/cache/DiskLruCache;", "Ljava/io/Closeable;", "Ljava/io/Flushable;", "Lokhttp3/internal/concurrent/Lockable;", "fileSystem", "Lokio/FileSystem;", "directory", "Lokio/Path;", "appVersion", "", "valueCount", "maxSize", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "<init>", "(Lokio/FileSystem;Lokio/Path;IIJLokhttp3/internal/concurrent/TaskRunner;)V", "getDirectory", "()Lokio/Path;", "getValueCount$okhttp", "()I", "getFileSystem$okhttp", "()Lokio/FileSystem;", "value", "getMaxSize", "()J", "setMaxSize", "(J)V", "journalFile", "journalFileTmp", "journalFileBackup", "size", "journalWriter", "Lokio/BufferedSink;", "lruEntries", "Ljava/util/LinkedHashMap;", "", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "Lkotlin/collections/LinkedHashMap;", "getLruEntries$okhttp", "()Ljava/util/LinkedHashMap;", "redundantOpCount", "hasJournalErrors", "", "civilizedFileSystem", "initialized", "closed", "getClosed$okhttp", "()Z", "setClosed$okhttp", "(Z)V", "mostRecentTrimFailed", "mostRecentRebuildFailed", "nextSequenceNumber", "cleanupQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "cleanupTask", "okhttp3/internal/cache/DiskLruCache$cleanupTask$1", "Lokhttp3/internal/cache/DiskLruCache$cleanupTask$1;", "initialize", "", "readJournal", "newJournalWriter", "readJournalLine", "line", "processJournal", "rebuildJournal", "rebuildJournal$okhttp", "get", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "key", "edit", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "expectedSequenceNumber", "completeEdit", "editor", "success", "completeEdit$okhttp", "journalRebuildRequired", "remove", "removeEntry", "entry", "removeEntry$okhttp", "checkNotClosed", "flush", "isClosed", "close", "trimToSize", "removeOldestEntry", "delete", "evictAll", "validateKey", "snapshots", "", "Snapshot", "Editor", "Entry", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 4 Okio.kt\nokio/Okio__OkioKt\n+ 5 FileSystem.kt\nokio/FileSystem\n+ 6 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,1120:1\n1#2:1121\n55#3,4:1122\n55#3,4:1189\n72#4:1126\n58#4,4:1128\n66#4,10:1133\n62#4,3:1143\n77#4,3:1146\n58#4,4:1152\n66#4,10:1157\n62#4,18:1167\n73#5:1127\n74#5:1132\n86#5:1149\n191#5:1150\n95#5:1151\n96#5:1156\n37#6,2:1185\n37#6,2:1187\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache\n*L\n230#1:1122,4\n324#1:1189,4\n279#1:1126\n279#1:1128,4\n279#1:1133,10\n279#1:1143,3\n279#1:1146,3\n411#1:1152,4\n411#1:1157,10\n411#1:1167,18\n279#1:1127\n279#1:1132\n411#1:1149\n411#1:1150\n411#1:1151\n411#1:1156\n715#1:1185,2\n765#1:1187,2\n*E\n"})
public final class DiskLruCache implements Closeable, Flushable, Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Path directory;
   private final int appVersion;
   private final int valueCount;
   @NotNull
   private final FileSystem fileSystem;
   private long maxSize;
   @NotNull
   private final Path journalFile;
   @NotNull
   private final Path journalFileTmp;
   @NotNull
   private final Path journalFileBackup;
   private long size;
   @Nullable
   private BufferedSink journalWriter;
   @NotNull
   private final LinkedHashMap<String, Entry> lruEntries;
   private int redundantOpCount;
   private boolean hasJournalErrors;
   private boolean civilizedFileSystem;
   private boolean initialized;
   private boolean closed;
   private boolean mostRecentTrimFailed;
   private boolean mostRecentRebuildFailed;
   private long nextSequenceNumber;
   @NotNull
   private final TaskQueue cleanupQueue;
   @NotNull
   private final <undefinedtype> cleanupTask;
   @JvmField
   @NotNull
   public static final String JOURNAL_FILE = "journal";
   @JvmField
   @NotNull
   public static final String JOURNAL_FILE_TEMP = "journal.tmp";
   @JvmField
   @NotNull
   public static final String JOURNAL_FILE_BACKUP = "journal.bkp";
   @JvmField
   @NotNull
   public static final String MAGIC = "libcore.io.DiskLruCache";
   @JvmField
   @NotNull
   public static final String VERSION_1 = "1";
   @JvmField
   public static final long ANY_SEQUENCE_NUMBER = -1L;
   @JvmField
   @NotNull
   public static final Regex LEGAL_KEY_PATTERN = new Regex("[a-z0-9_-]{1,120}");
   @JvmField
   @NotNull
   public static final String CLEAN = "CLEAN";
   @JvmField
   @NotNull
   public static final String DIRTY = "DIRTY";
   @JvmField
   @NotNull
   public static final String REMOVE = "REMOVE";
   @JvmField
   @NotNull
   public static final String READ = "READ";

   public DiskLruCache(@NotNull FileSystem fileSystem, @NotNull Path directory, int appVersion, int valueCount, long maxSize, @NotNull TaskRunner taskRunner) {
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      Intrinsics.checkNotNullParameter(directory, "directory");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      super();
      this.directory = directory;
      this.appVersion = appVersion;
      this.valueCount = valueCount;
      this.fileSystem = new ForwardingFileSystem(fileSystem) {
         public Sink sink(Path file, boolean mustCreate) {
            Intrinsics.checkNotNullParameter(file, "file");
            Path var10000 = file.parent();
            if (var10000 != null) {
               Path it = var10000;
               int var4 = 0;
               this.createDirectories(it);
            }

            return super.sink(file, mustCreate);
         }
      };
      this.maxSize = maxSize;
      this.lruEntries = new LinkedHashMap(0, 0.75F, true);
      this.cleanupQueue = taskRunner.newQueue();
      String var8 = _UtilJvmKt.okHttpName + " Cache";
      this.cleanupTask = new Task(var8) {
         public long runOnce() {
            DiskLruCache var1 = DiskLruCache.this;
            DiskLruCache var2 = DiskLruCache.this;
            synchronized(var1){}

            long var8;
            try {
               int var4 = 0;
               if (var2.initialized && !var2.getClosed$okhttp()) {
                  try {
                     var2.trimToSize();
                  } catch (IOException var13) {
                     var2.mostRecentTrimFailed = true;
                  }

                  try {
                     if (var2.journalRebuildRequired()) {
                        var2.rebuildJournal$okhttp();
                        var2.redundantOpCount = 0;
                     }
                  } catch (IOException var14) {
                     var2.mostRecentRebuildFailed = true;
                     BufferedSink var10000 = var2.journalWriter;
                     if (var10000 != null) {
                        _UtilCommonKt.closeQuietly(var10000);
                     }

                     var2.journalWriter = Okio.buffer(Okio.blackhole());
                  }

                  long var6 = -1L;
                  return var6;
               }

               var8 = -1L;
            } finally {
               ;
            }

            return var8;
         }
      };
      if (maxSize <= 0L) {
         int var11 = 0;
         String var12 = "maxSize <= 0";
         throw new IllegalArgumentException(var12.toString());
      } else if (this.valueCount <= 0) {
         int var9 = 0;
         String var10 = "valueCount <= 0";
         throw new IllegalArgumentException(var10.toString());
      } else {
         this.journalFile = this.directory.resolve(JOURNAL_FILE);
         this.journalFileTmp = this.directory.resolve(JOURNAL_FILE_TEMP);
         this.journalFileBackup = this.directory.resolve(JOURNAL_FILE_BACKUP);
      }
   }

   @NotNull
   public final Path getDirectory() {
      return this.directory;
   }

   public final int getValueCount$okhttp() {
      return this.valueCount;
   }

   @NotNull
   public final FileSystem getFileSystem$okhttp() {
      return this.fileSystem;
   }

   public final synchronized long getMaxSize() {
      return this.maxSize;
   }

   public final synchronized void setMaxSize(long value) {
      this.maxSize = value;
      if (this.initialized) {
         TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
      }

   }

   @NotNull
   public final LinkedHashMap<String, Entry> getLruEntries$okhttp() {
      return this.lruEntries;
   }

   public final boolean getClosed$okhttp() {
      return this.closed;
   }

   public final void setClosed$okhttp(boolean var1) {
      this.closed = var1;
   }

   public final synchronized void initialize() throws IOException {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else if (!this.initialized) {
         if (this.fileSystem.exists(this.journalFileBackup)) {
            if (this.fileSystem.exists(this.journalFile)) {
               this.fileSystem.delete(this.journalFileBackup);
            } else {
               this.fileSystem.atomicMove(this.journalFileBackup, this.journalFile);
            }
         }

         this.civilizedFileSystem = _UtilCommonKt.isCivilized(this.fileSystem, this.journalFileBackup);
         if (this.fileSystem.exists(this.journalFile)) {
            try {
               this.readJournal();
               this.processJournal();
               this.initialized = true;
               return;
            } catch (IOException journalIsCorrupt) {
               Platform.Companion.get().log("DiskLruCache " + this.directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing", 5, (Throwable)journalIsCorrupt);

               try {
                  this.delete();
               } finally {
                  this.closed = false;
               }
            }
         }

         this.rebuildJournal$okhttp();
         this.initialized = true;
      }
   }

   private final void readJournal() throws IOException {
      FileSystem this_$iv = this.fileSystem;
      Path file$iv = this.journalFile;
      int var3 = 0;
      Closeable $this$use$iv$iv = Okio.buffer(this_$iv.source(file$iv));
      int $i$f$use = 0;
      Throwable thrown$iv$iv = null;
      boolean var25 = false;

      label201: {
         label200: {
            try {
               var25 = true;
               BufferedSource it$iv = (BufferedSource)$this$use$iv$iv;
               int var32 = false;
               BufferedSource $this$readJournal_u24lambda_u240 = it$iv;
               int var10 = false;
               String magic = it$iv.readUtf8LineStrict();
               String version = it$iv.readUtf8LineStrict();
               String appVersionString = it$iv.readUtf8LineStrict();
               String valueCountString = it$iv.readUtf8LineStrict();
               String blank = it$iv.readUtf8LineStrict();
               if (!Intrinsics.areEqual((Object)MAGIC, (Object)magic) || !Intrinsics.areEqual((Object)VERSION_1, (Object)version) || !Intrinsics.areEqual((Object)String.valueOf(this.appVersion), (Object)appVersionString) || !Intrinsics.areEqual((Object)String.valueOf(this.valueCount), (Object)valueCountString) || ((CharSequence)blank).length() > 0) {
                  throw new IOException("unexpected journal header: [" + magic + ", " + version + ", " + valueCountString + ", " + blank + ']');
               }

               int lineCount = 0;

               while(true) {
                  try {
                     this.readJournalLine($this$readJournal_u24lambda_u240.readUtf8LineStrict());
                     ++lineCount;
                  } catch (EOFException var29) {
                     this.redundantOpCount = lineCount - this.lruEntries.size();
                     if (!$this$readJournal_u24lambda_u240.exhausted()) {
                        this.rebuildJournal$okhttp();
                        break;
                     }

                     BufferedSink var10000 = this.journalWriter;
                     if (var10000 != null) {
                        _UtilCommonKt.closeQuietly(var10000);
                     }

                     this.journalWriter = this.newJournalWriter();
                     break;
                  }
               }

               Unit var33 = Unit.INSTANCE;
               var25 = false;
               break label200;
            } catch (Throwable t$iv$iv) {
               thrown$iv$iv = t$iv$iv;
               Object var8 = null;
               var25 = false;
            } finally {
               if (var25) {
                  try {
                     if ($this$use$iv$iv != null) {
                        $this$use$iv$iv.close();
                     }
                  } catch (Throwable t$iv$iv) {
                     if (thrown$iv$iv != null) {
                        ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                     }
                  }

               }
            }

            try {
               if ($this$use$iv$iv != null) {
                  $this$use$iv$iv.close();
               }
            } catch (Throwable t$iv$iv) {
               if (thrown$iv$iv == null) {
                  thrown$iv$iv = t$iv$iv;
               } else {
                  ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
               }
            }
            break label201;
         }

         try {
            if ($this$use$iv$iv != null) {
               $this$use$iv$iv.close();
            }
         } catch (Throwable t$iv$iv) {
            thrown$iv$iv = t$iv$iv;
         }
      }

      if (thrown$iv$iv != null) {
         throw thrown$iv$iv;
      }
   }

   private final BufferedSink newJournalWriter() throws FileNotFoundException {
      Sink fileSink = this.fileSystem.appendingSink(this.journalFile);
      FaultHidingSink faultHidingSink = new FaultHidingSink(fileSink, DiskLruCache::newJournalWriter$lambda$0);
      return Okio.buffer(faultHidingSink);
   }

   private final void readJournalLine(String line) throws IOException {
      int firstSpace = StringsKt.indexOf$default((CharSequence)line, ' ', 0, false, 6, (Object)null);
      if (firstSpace == -1) {
         throw new IOException("unexpected journal line: " + line);
      } else {
         int keyBegin = firstSpace + 1;
         int secondSpace = StringsKt.indexOf$default((CharSequence)line, ' ', keyBegin, false, 4, (Object)null);
         String key = null;
         if (secondSpace == -1) {
            String var10000 = line.substring(keyBegin);
            Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
            key = var10000;
            if (firstSpace == REMOVE.length() && StringsKt.startsWith$default(line, REMOVE, false, 2, (Object)null)) {
               this.lruEntries.remove(key);
               return;
            }
         } else {
            String var10 = line.substring(keyBegin, secondSpace);
            Intrinsics.checkNotNullExpressionValue(var10, "substring(...)");
            key = var10;
         }

         Entry entry = (Entry)this.lruEntries.get(key);
         if (entry == null) {
            entry = new Entry(key);
            ((Map)this.lruEntries).put(key, entry);
         }

         if (secondSpace != -1 && firstSpace == CLEAN.length() && StringsKt.startsWith$default(line, CLEAN, false, 2, (Object)null)) {
            String var11 = line.substring(secondSpace + 1);
            Intrinsics.checkNotNullExpressionValue(var11, "substring(...)");
            CharSequence var12 = (CharSequence)var11;
            char[] var8 = new char[]{' '};
            List parts = StringsKt.split$default(var12, var8, false, 0, 6, (Object)null);
            entry.setReadable$okhttp(true);
            entry.setCurrentEditor$okhttp((Editor)null);
            entry.setLengths$okhttp(parts);
         } else if (secondSpace == -1 && firstSpace == DIRTY.length() && StringsKt.startsWith$default(line, DIRTY, false, 2, (Object)null)) {
            entry.setCurrentEditor$okhttp(new Editor(entry));
         } else if (secondSpace != -1 || firstSpace != READ.length() || !StringsKt.startsWith$default(line, READ, false, 2, (Object)null)) {
            throw new IOException("unexpected journal line: " + line);
         }

      }
   }

   private final void processJournal() throws IOException {
      _UtilCommonKt.deleteIfExists(this.fileSystem, this.journalFileTmp);
      Iterator i = this.lruEntries.values().iterator();

      while(i.hasNext()) {
         Object var10000 = i.next();
         Intrinsics.checkNotNullExpressionValue(var10000, "next(...)");
         Entry entry = (Entry)var10000;
         if (entry.getCurrentEditor$okhttp() == null) {
            int t = 0;

            for(int var6 = this.valueCount; t < var6; ++t) {
               this.size += entry.getLengths$okhttp()[t];
            }
         } else {
            entry.setCurrentEditor$okhttp((Editor)null);
            int t = 0;

            for(int var4 = this.valueCount; t < var4; ++t) {
               _UtilCommonKt.deleteIfExists(this.fileSystem, (Path)entry.getCleanFiles$okhttp().get(t));
               _UtilCommonKt.deleteIfExists(this.fileSystem, (Path)entry.getDirtyFiles$okhttp().get(t));
            }

            i.remove();
         }
      }

   }

   public final synchronized void rebuildJournal$okhttp() throws IOException {
      BufferedSink var10000 = this.journalWriter;
      if (var10000 != null) {
         var10000.close();
      }

      FileSystem $this$iv = this.fileSystem;
      Path file$iv = this.journalFileTmp;
      boolean mustCreate$iv = false;
      int var4 = 0;
      Closeable $this$use$iv$iv = Okio.buffer($this$iv.sink(file$iv, mustCreate$iv));
      int $i$f$use = 0;
      Throwable thrown$iv$iv = null;
      boolean var20 = false;

      label176: {
         label175: {
            try {
               var20 = true;
               BufferedSink it$iv = (BufferedSink)$this$use$iv$iv;
               int var26 = false;
               BufferedSink $this$rebuildJournal_u24lambda_u240 = it$iv;
               int var11 = false;
               it$iv.writeUtf8(MAGIC).writeByte(10);
               it$iv.writeUtf8(VERSION_1).writeByte(10);
               it$iv.writeDecimalLong((long)this.appVersion).writeByte(10);
               it$iv.writeDecimalLong((long)this.valueCount).writeByte(10);
               it$iv.writeByte(10);

               for(Object var31 : this.lruEntries.values()) {
                  Intrinsics.checkNotNullExpressionValue(var31, "next(...)");
                  Entry entry = (Entry)var31;
                  if (entry.getCurrentEditor$okhttp() != null) {
                     $this$rebuildJournal_u24lambda_u240.writeUtf8(DIRTY).writeByte(32);
                     $this$rebuildJournal_u24lambda_u240.writeUtf8(entry.getKey$okhttp());
                     $this$rebuildJournal_u24lambda_u240.writeByte(10);
                  } else {
                     $this$rebuildJournal_u24lambda_u240.writeUtf8(CLEAN).writeByte(32);
                     $this$rebuildJournal_u24lambda_u240.writeUtf8(entry.getKey$okhttp());
                     entry.writeLengths$okhttp($this$rebuildJournal_u24lambda_u240);
                     $this$rebuildJournal_u24lambda_u240.writeByte(10);
                  }
               }

               Unit var27 = Unit.INSTANCE;
               var20 = false;
               break label175;
            } catch (Throwable t$iv$iv) {
               thrown$iv$iv = t$iv$iv;
               Object var9 = null;
               var20 = false;
            } finally {
               if (var20) {
                  try {
                     if ($this$use$iv$iv != null) {
                        $this$use$iv$iv.close();
                     }
                  } catch (Throwable t$iv$iv) {
                     if (thrown$iv$iv != null) {
                        ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
                     }
                  }

               }
            }

            try {
               if ($this$use$iv$iv != null) {
                  $this$use$iv$iv.close();
               }
            } catch (Throwable t$iv$iv) {
               if (thrown$iv$iv == null) {
                  thrown$iv$iv = t$iv$iv;
               } else {
                  ExceptionsKt.addSuppressed(thrown$iv$iv, t$iv$iv);
               }
            }
            break label176;
         }

         try {
            if ($this$use$iv$iv != null) {
               $this$use$iv$iv.close();
            }
         } catch (Throwable t$iv$iv) {
            thrown$iv$iv = t$iv$iv;
         }
      }

      if (thrown$iv$iv != null) {
         throw thrown$iv$iv;
      } else {
         if (this.fileSystem.exists(this.journalFile)) {
            this.fileSystem.atomicMove(this.journalFile, this.journalFileBackup);
            this.fileSystem.atomicMove(this.journalFileTmp, this.journalFile);
            _UtilCommonKt.deleteIfExists(this.fileSystem, this.journalFileBackup);
         } else {
            this.fileSystem.atomicMove(this.journalFileTmp, this.journalFile);
         }

         var10000 = this.journalWriter;
         if (var10000 != null) {
            _UtilCommonKt.closeQuietly(var10000);
         }

         this.journalWriter = this.newJournalWriter();
         this.hasJournalErrors = false;
         this.mostRecentRebuildFailed = false;
      }
   }

   @Nullable
   public final synchronized Snapshot get(@NotNull String key) throws IOException {
      Intrinsics.checkNotNullParameter(key, "key");
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      Entry var10000 = (Entry)this.lruEntries.get(key);
      if (var10000 == null) {
         return null;
      } else {
         Entry entry = var10000;
         Snapshot var5 = entry.snapshot$okhttp();
         if (var5 == null) {
            return null;
         } else {
            Snapshot snapshot = var5;
            int var4 = this.redundantOpCount++;
            BufferedSink var6 = this.journalWriter;
            Intrinsics.checkNotNull(var6);
            var6.writeUtf8(READ).writeByte(32).writeUtf8(key).writeByte(10);
            if (this.journalRebuildRequired()) {
               TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
            }

            return snapshot;
         }
      }
   }

   @JvmOverloads
   @Nullable
   public final synchronized Editor edit(@NotNull String key, long expectedSequenceNumber) throws IOException {
      Intrinsics.checkNotNullParameter(key, "key");
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      Entry entry = (Entry)this.lruEntries.get(key);
      if (expectedSequenceNumber == ANY_SEQUENCE_NUMBER || entry != null && entry.getSequenceNumber$okhttp() == expectedSequenceNumber) {
         if ((entry != null ? entry.getCurrentEditor$okhttp() : null) != null) {
            return null;
         } else if (entry != null && entry.getLockingSourceCount$okhttp() != 0) {
            return null;
         } else if (!this.mostRecentTrimFailed && !this.mostRecentRebuildFailed) {
            BufferedSink var10000 = this.journalWriter;
            Intrinsics.checkNotNull(var10000);
            BufferedSink journalWriter = var10000;
            journalWriter.writeUtf8(DIRTY).writeByte(32).writeUtf8(key).writeByte(10);
            journalWriter.flush();
            if (this.hasJournalErrors) {
               return null;
            } else {
               if (entry == null) {
                  entry = new Entry(key);
                  ((Map)this.lruEntries).put(key, entry);
               }

               Editor editor = new Editor(entry);
               entry.setCurrentEditor$okhttp(editor);
               return editor;
            }
         } else {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
            return null;
         }
      } else {
         return null;
      }
   }

   // $FF: synthetic method
   public static Editor edit$default(DiskLruCache var0, String var1, long var2, int var4, Object var5) throws IOException {
      if ((var4 & 2) != 0) {
         var2 = ANY_SEQUENCE_NUMBER;
      }

      return var0.edit(var1, var2);
   }

   public final synchronized long size() throws IOException {
      this.initialize();
      return this.size;
   }

   public final synchronized void completeEdit$okhttp(@NotNull Editor editor, boolean success) throws IOException {
      Intrinsics.checkNotNullParameter(editor, "editor");
      Entry entry = editor.getEntry$okhttp();
      if (!Intrinsics.areEqual((Object)entry.getCurrentEditor$okhttp(), (Object)editor)) {
         throw new IllegalStateException("Check failed.");
      } else {
         if (success && !entry.getReadable$okhttp()) {
            int i = 0;

            for(int var5 = this.valueCount; i < var5; ++i) {
               boolean[] var10000 = editor.getWritten$okhttp();
               Intrinsics.checkNotNull(var10000);
               if (!var10000[i]) {
                  editor.abort();
                  throw new IllegalStateException("Newly created entry didn't create value for index " + i);
               }

               if (!this.fileSystem.exists((Path)entry.getDirtyFiles$okhttp().get(i))) {
                  editor.abort();
                  return;
               }
            }
         }

         int i = 0;

         for(int var15 = this.valueCount; i < var15; ++i) {
            Path dirty = (Path)entry.getDirtyFiles$okhttp().get(i);
            if (success && !entry.getZombie$okhttp()) {
               if (this.fileSystem.exists(dirty)) {
                  Path clean = (Path)entry.getCleanFiles$okhttp().get(i);
                  this.fileSystem.atomicMove(dirty, clean);
                  long oldLength = entry.getLengths$okhttp()[i];
                  Long var18 = this.fileSystem.metadata(clean).getSize();
                  long newLength = var18 != null ? var18 : 0L;
                  entry.getLengths$okhttp()[i] = newLength;
                  this.size = this.size - oldLength + newLength;
               }
            } else {
               _UtilCommonKt.deleteIfExists(this.fileSystem, dirty);
            }
         }

         entry.setCurrentEditor$okhttp((Editor)null);
         if (entry.getZombie$okhttp()) {
            this.removeEntry$okhttp(entry);
         } else {
            i = this.redundantOpCount++;
            BufferedSink var19 = this.journalWriter;
            Intrinsics.checkNotNull(var19);
            BufferedSink var14 = var19;
            int var16 = 0;
            if (!entry.getReadable$okhttp() && !success) {
               this.lruEntries.remove(entry.getKey$okhttp());
               var14.writeUtf8(REMOVE).writeByte(32);
               var14.writeUtf8(entry.getKey$okhttp());
               var14.writeByte(10);
            } else {
               entry.setReadable$okhttp(true);
               var14.writeUtf8(CLEAN).writeByte(32);
               var14.writeUtf8(entry.getKey$okhttp());
               entry.writeLengths$okhttp(var14);
               var14.writeByte(10);
               if (success) {
                  long var17 = (long)(this.nextSequenceNumber++);
                  entry.setSequenceNumber$okhttp(var17);
               }
            }

            var14.flush();
            if (this.size > this.maxSize || this.journalRebuildRequired()) {
               TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
            }

         }
      }
   }

   private final boolean journalRebuildRequired() {
      int redundantOpCompactThreshold = 2000;
      return this.redundantOpCount >= redundantOpCompactThreshold && this.redundantOpCount >= this.lruEntries.size();
   }

   public final synchronized boolean remove(@NotNull String key) throws IOException {
      Intrinsics.checkNotNullParameter(key, "key");
      this.initialize();
      this.checkNotClosed();
      this.validateKey(key);
      Entry var10000 = (Entry)this.lruEntries.get(key);
      if (var10000 == null) {
         return false;
      } else {
         Entry entry = var10000;
         boolean removed = this.removeEntry$okhttp(entry);
         if (removed && this.size <= this.maxSize) {
            this.mostRecentTrimFailed = false;
         }

         return removed;
      }
   }

   public final boolean removeEntry$okhttp(@NotNull Entry entry) throws IOException {
      Intrinsics.checkNotNullParameter(entry, "entry");
      if (!this.civilizedFileSystem) {
         if (entry.getLockingSourceCount$okhttp() > 0) {
            BufferedSink var10000 = this.journalWriter;
            if (var10000 != null) {
               BufferedSink it = var10000;
               int var5 = 0;
               it.writeUtf8(DIRTY);
               it.writeByte(32);
               it.writeUtf8(entry.getKey$okhttp());
               it.writeByte(10);
               it.flush();
            }
         }

         if (entry.getLockingSourceCount$okhttp() > 0 || entry.getCurrentEditor$okhttp() != null) {
            entry.setZombie$okhttp(true);
            return true;
         }
      }

      Editor var9 = entry.getCurrentEditor$okhttp();
      if (var9 != null) {
         var9.detach$okhttp();
      }

      int i = 0;

      for(int var3 = this.valueCount; i < var3; ++i) {
         _UtilCommonKt.deleteIfExists(this.fileSystem, (Path)entry.getCleanFiles$okhttp().get(i));
         this.size -= entry.getLengths$okhttp()[i];
         entry.getLengths$okhttp()[i] = 0L;
      }

      i = this.redundantOpCount++;
      BufferedSink var10 = this.journalWriter;
      if (var10 != null) {
         BufferedSink it = var10;
         int var8 = 0;
         it.writeUtf8(REMOVE);
         it.writeByte(32);
         it.writeUtf8(entry.getKey$okhttp());
         it.writeByte(10);
      }

      this.lruEntries.remove(entry.getKey$okhttp());
      if (this.journalRebuildRequired()) {
         TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
      }

      return true;
   }

   private final synchronized void checkNotClosed() {
      if (this.closed) {
         int var1 = 0;
         String var2 = "cache is closed";
         throw new IllegalStateException(var2.toString());
      }
   }

   public synchronized void flush() throws IOException {
      if (this.initialized) {
         this.checkNotClosed();
         this.trimToSize();
         BufferedSink var10000 = this.journalWriter;
         Intrinsics.checkNotNull(var10000);
         var10000.flush();
      }
   }

   public final synchronized boolean isClosed() {
      return this.closed;
   }

   public synchronized void close() throws IOException {
      if (this.initialized && !this.closed) {
         Collection var10000 = this.lruEntries.values();
         Intrinsics.checkNotNullExpressionValue(var10000, "<get-values>(...)");
         Collection $this$toTypedArray$iv = var10000;
         int $i$f$toTypedArray = 0;
         Entry[] var1 = (Entry[])$this$toTypedArray$iv.toArray(new Entry[0]);
         int thisCollection$iv = 0;

         for(int var7 = var1.length; thisCollection$iv < var7; ++thisCollection$iv) {
            Entry entry = var1[thisCollection$iv];
            Intrinsics.checkNotNull(entry);
            if (entry.getCurrentEditor$okhttp() != null) {
               Editor var8 = entry.getCurrentEditor$okhttp();
               if (var8 != null) {
                  var8.detach$okhttp();
               }
            }
         }

         this.trimToSize();
         BufferedSink var9 = this.journalWriter;
         if (var9 != null) {
            _UtilCommonKt.closeQuietly(var9);
         }

         this.journalWriter = null;
         this.closed = true;
      } else {
         this.closed = true;
      }
   }

   public final void trimToSize() throws IOException {
      while(true) {
         if (this.size > this.maxSize) {
            if (this.removeOldestEntry()) {
               continue;
            }

            return;
         }

         this.mostRecentTrimFailed = false;
         return;
      }
   }

   private final boolean removeOldestEntry() {
      for(Object var10000 : this.lruEntries.values()) {
         Intrinsics.checkNotNullExpressionValue(var10000, "next(...)");
         Entry toEvict = (Entry)var10000;
         if (!toEvict.getZombie$okhttp()) {
            this.removeEntry$okhttp(toEvict);
            return true;
         }
      }

      return false;
   }

   public final void delete() throws IOException {
      this.close();
      _UtilCommonKt.deleteContents(this.fileSystem, this.directory);
   }

   public final synchronized void evictAll() throws IOException {
      this.initialize();
      Collection var10000 = this.lruEntries.values();
      Intrinsics.checkNotNullExpressionValue(var10000, "<get-values>(...)");
      Collection $this$toTypedArray$iv = var10000;
      int $i$f$toTypedArray = 0;
      Entry[] var1 = (Entry[])$this$toTypedArray$iv.toArray(new Entry[0]);
      int thisCollection$iv = 0;

      for(int var7 = var1.length; thisCollection$iv < var7; ++thisCollection$iv) {
         Entry entry = var1[thisCollection$iv];
         Intrinsics.checkNotNull(entry);
         this.removeEntry$okhttp(entry);
      }

      this.mostRecentTrimFailed = false;
   }

   private final void validateKey(String key) {
      if (!LEGAL_KEY_PATTERN.matches((CharSequence)key)) {
         int var2 = 0;
         String var3 = "keys must match regex [a-z0-9_-]{1,120}: \"" + key + '"';
         throw new IllegalArgumentException(var3.toString());
      }
   }

   @NotNull
   public final synchronized Iterator<Snapshot> snapshots() throws IOException {
      this.initialize();
      return new Iterator<Snapshot>() {
         private final Iterator<Entry> delegate;
         private Snapshot nextSnapshot;
         private Snapshot removeSnapshot;

         {
            Iterator var10001 = (new ArrayList(DiskLruCache.this.getLruEntries$okhttp().values())).iterator();
            Intrinsics.checkNotNullExpressionValue(var10001, "iterator(...)");
            this.delegate = var10001;
         }

         public boolean hasNext() {
            if (this.nextSnapshot != null) {
               return true;
            } else {
               DiskLruCache var1 = DiskLruCache.this;
               DiskLruCache var2 = DiskLruCache.this;
               synchronized(var1){}

               boolean var7;
               try {
                  int var4 = 0;
                  if (!var2.getClosed$okhttp()) {
                     while(this.delegate.hasNext()) {
                        Entry var10001 = (Entry)this.delegate.next();
                        if (var10001 != null) {
                           Snapshot var11 = var10001.snapshot$okhttp();
                           if (var11 != null) {
                              this.nextSnapshot = var11;
                              boolean var6 = true;
                              return var6;
                           }
                        }
                     }

                     Unit var10 = Unit.INSTANCE;
                     return false;
                  }

                  var7 = false;
               } finally {
                  ;
               }

               return var7;
            }
         }

         public Snapshot next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.removeSnapshot = this.nextSnapshot;
               this.nextSnapshot = null;
               Snapshot var10000 = this.removeSnapshot;
               Intrinsics.checkNotNull(var10000);
               return var10000;
            }
         }

         public void remove() {
            Snapshot removeSnapshot = this.removeSnapshot;
            if (removeSnapshot == null) {
               int var2 = 0;
               String var8 = "remove() before next()";
               throw new IllegalStateException(var8.toString());
            } else {
               try {
                  DiskLruCache.this.remove(removeSnapshot.key());
               } catch (IOException var6) {
               } finally {
                  this.removeSnapshot = null;
               }

            }
         }
      };
   }

   @JvmOverloads
   @Nullable
   public final Editor edit(@NotNull String key) throws IOException {
      Intrinsics.checkNotNullParameter(key, "key");
      return edit$default(this, key, 0L, 2, (Object)null);
   }

   private static final Unit newJournalWriter$lambda$0(DiskLruCache this$0, IOException it) {
      Intrinsics.checkNotNullParameter(it, "it");
      Lockable $this$assertLockHeld$iv = this$0;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         this$0.hasJournalErrors = true;
         return Unit.INSTANCE;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B/\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u0006\u0010\u0002\u001a\u00020\u0003J\f\u0010\r\u001a\b\u0018\u00010\u000eR\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0014\u001a\u00020\u0015H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"},
      d2 = {"Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "Ljava/io/Closeable;", "key", "", "sequenceNumber", "", "sources", "", "Lokio/Source;", "lengths", "", "<init>", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;JLjava/util/List;[J)V", "edit", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getSource", "index", "", "getLength", "close", "", "okhttp"}
   )
   public final class Snapshot implements Closeable {
      @NotNull
      private final String key;
      private final long sequenceNumber;
      @NotNull
      private final List<Source> sources;
      @NotNull
      private final long[] lengths;

      public Snapshot(@NotNull String key, long sequenceNumber, @NotNull List<? extends Source> sources, @NotNull long[] lengths) {
         Intrinsics.checkNotNullParameter(key, "key");
         Intrinsics.checkNotNullParameter(sources, "sources");
         Intrinsics.checkNotNullParameter(lengths, "lengths");
         super();
         this.key = key;
         this.sequenceNumber = sequenceNumber;
         this.sources = sources;
         this.lengths = lengths;
      }

      @NotNull
      public final String key() {
         return this.key;
      }

      @Nullable
      public final Editor edit() throws IOException {
         return DiskLruCache.this.edit(this.key, this.sequenceNumber);
      }

      @NotNull
      public final Source getSource(int index) {
         return (Source)this.sources.get(index);
      }

      public final long getLength(int index) {
         return this.lengths[index];
      }

      public void close() {
         for(Source source : this.sources) {
            _UtilCommonKt.closeQuietly(source);
         }

      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0018\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u0015\b\u0000\u0012\n\u0010\u0002\u001a\u00060\u0003R\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\r\u0010\u000f\u001a\u00020\u0010H\u0000¢\u0006\u0002\b\u0011J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0018\u001a\u00020\u0010J\u0006\u0010\u0019\u001a\u00020\u0010R\u0018\u0010\u0002\u001a\u00060\u0003R\u00020\u0004X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0016\u0010\t\u001a\u0004\u0018\u00010\nX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"},
      d2 = {"Lokhttp3/internal/cache/DiskLruCache$Editor;", "", "entry", "Lokhttp3/internal/cache/DiskLruCache$Entry;", "Lokhttp3/internal/cache/DiskLruCache;", "<init>", "(Lokhttp3/internal/cache/DiskLruCache;Lokhttp3/internal/cache/DiskLruCache$Entry;)V", "getEntry$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Entry;", "written", "", "getWritten$okhttp", "()[Z", "done", "", "detach", "", "detach$okhttp", "newSource", "Lokio/Source;", "index", "", "newSink", "Lokio/Sink;", "commit", "abort", "okhttp"}
   )
   public final class Editor {
      @NotNull
      private final Entry entry;
      @Nullable
      private final boolean[] written;
      private boolean done;

      public Editor(@NotNull Entry entry) {
         Intrinsics.checkNotNullParameter(entry, "entry");
         super();
         this.entry = entry;
         this.written = this.entry.getReadable$okhttp() ? null : new boolean[DiskLruCache.this.getValueCount$okhttp()];
      }

      @NotNull
      public final Entry getEntry$okhttp() {
         return this.entry;
      }

      @Nullable
      public final boolean[] getWritten$okhttp() {
         return this.written;
      }

      public final void detach$okhttp() {
         if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
            if (DiskLruCache.this.civilizedFileSystem) {
               DiskLruCache.this.completeEdit$okhttp(this, false);
            } else {
               this.entry.setZombie$okhttp(true);
            }
         }

      }

      @Nullable
      public final Source newSource(int index) {
         DiskLruCache var2 = DiskLruCache.this;
         DiskLruCache var3 = DiskLruCache.this;
         synchronized(var2){}

         Object var9;
         try {
            int var5 = 0;
            if (this.done) {
               throw new IllegalStateException("Check failed.");
            }

            if (this.entry.getReadable$okhttp() && Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this) && !this.entry.getZombie$okhttp()) {
               Source var6;
               try {
                  var6 = var3.getFileSystem$okhttp().source((Path)this.entry.getCleanFiles$okhttp().get(index));
               } catch (FileNotFoundException var12) {
                  var6 = null;
               }

               Source var8 = var6;
               return var8;
            }

            var9 = null;
         } finally {
            ;
         }

         return (Source)var9;
      }

      @NotNull
      public final Sink newSink(int index) {
         DiskLruCache var2 = DiskLruCache.this;
         DiskLruCache var3 = DiskLruCache.this;
         synchronized(var2){}

         Sink var11;
         try {
            int var5 = 0;
            if (this.done) {
               throw new IllegalStateException("Check failed.");
            }

            if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
               if (!this.entry.getReadable$okhttp()) {
                  boolean[] var10000 = this.written;
                  Intrinsics.checkNotNull(var10000);
                  var10000[index] = true;
               }

               Path dirtyFile = (Path)this.entry.getDirtyFiles$okhttp().get(index);
               Sink sink = null;

               try {
                  sink = var3.getFileSystem$okhttp().sink(dirtyFile);
               } catch (FileNotFoundException var14) {
                  Sink var10 = Okio.blackhole();
                  return var10;
               }

               Sink var9 = new FaultHidingSink(sink, Editor::newSink$lambda$0$0);
               return var9;
            }

            var11 = Okio.blackhole();
         } finally {
            ;
         }

         return var11;
      }

      public final void commit() throws IOException {
         DiskLruCache var1 = DiskLruCache.this;
         DiskLruCache var2 = DiskLruCache.this;
         synchronized(var1){}

         try {
            int var4 = 0;
            if (this.done) {
               throw new IllegalStateException("Check failed.");
            }

            if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
               var2.completeEdit$okhttp(this, true);
            }

            this.done = true;
            Unit var8 = Unit.INSTANCE;
         } finally {
            ;
         }

      }

      public final void abort() throws IOException {
         DiskLruCache var1 = DiskLruCache.this;
         DiskLruCache var2 = DiskLruCache.this;
         synchronized(var1){}

         try {
            int var4 = 0;
            if (this.done) {
               throw new IllegalStateException("Check failed.");
            }

            if (Intrinsics.areEqual((Object)this.entry.getCurrentEditor$okhttp(), (Object)this)) {
               var2.completeEdit$okhttp(this, false);
            }

            this.done = true;
            Unit var8 = Unit.INSTANCE;
         } finally {
            ;
         }

      }

      private static final Unit newSink$lambda$0$0(DiskLruCache this$0, Editor this$1, IOException it) {
         Intrinsics.checkNotNullParameter(it, "it");
         synchronized(this$0) {
            int var4 = 0;
            this$1.detach$okhttp();
            Unit var7 = Unit.INSTANCE;
         }

         return Unit.INSTANCE;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0016\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001b\u0010/\u001a\u0002002\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u000302H\u0000¢\u0006\u0002\b3J\u0015\u00104\u001a\u0002002\u0006\u00105\u001a\u000206H\u0000¢\u0006\u0002\b7J\u0016\u00108\u001a\u0002092\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u000302H\u0002J\u0013\u0010:\u001a\b\u0018\u00010;R\u00020\u001eH\u0000¢\u0006\u0002\b<J\u0010\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020$H\u0002R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u001a\u0010\u0013\u001a\u00020\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001a\u0010\u0019\u001a\u00020\u0014X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0016\"\u0004\b\u001b\u0010\u0018R \u0010\u001c\u001a\b\u0018\u00010\u001dR\u00020\u001eX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001a\u0010#\u001a\u00020$X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010&\"\u0004\b'\u0010(R\u001a\u0010)\u001a\u00020*X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.¨\u0006@"},
      d2 = {"Lokhttp3/internal/cache/DiskLruCache$Entry;", "", "key", "", "<init>", "(Lokhttp3/internal/cache/DiskLruCache;Ljava/lang/String;)V", "getKey$okhttp", "()Ljava/lang/String;", "lengths", "", "getLengths$okhttp", "()[J", "cleanFiles", "", "Lokio/Path;", "getCleanFiles$okhttp", "()Ljava/util/List;", "dirtyFiles", "getDirtyFiles$okhttp", "readable", "", "getReadable$okhttp", "()Z", "setReadable$okhttp", "(Z)V", "zombie", "getZombie$okhttp", "setZombie$okhttp", "currentEditor", "Lokhttp3/internal/cache/DiskLruCache$Editor;", "Lokhttp3/internal/cache/DiskLruCache;", "getCurrentEditor$okhttp", "()Lokhttp3/internal/cache/DiskLruCache$Editor;", "setCurrentEditor$okhttp", "(Lokhttp3/internal/cache/DiskLruCache$Editor;)V", "lockingSourceCount", "", "getLockingSourceCount$okhttp", "()I", "setLockingSourceCount$okhttp", "(I)V", "sequenceNumber", "", "getSequenceNumber$okhttp", "()J", "setSequenceNumber$okhttp", "(J)V", "setLengths", "", "strings", "", "setLengths$okhttp", "writeLengths", "writer", "Lokio/BufferedSink;", "writeLengths$okhttp", "invalidLengths", "", "snapshot", "Lokhttp3/internal/cache/DiskLruCache$Snapshot;", "snapshot$okhttp", "newSource", "Lokio/Source;", "index", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nDiskLruCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,1120:1\n55#2,4:1121\n*S KotlinDebug\n*F\n+ 1 DiskLruCache.kt\nokhttp3/internal/cache/DiskLruCache$Entry\n*L\n1045#1:1121,4\n*E\n"})
   public final class Entry {
      @NotNull
      private final String key;
      @NotNull
      private final long[] lengths;
      @NotNull
      private final List<Path> cleanFiles;
      @NotNull
      private final List<Path> dirtyFiles;
      private boolean readable;
      private boolean zombie;
      @Nullable
      private Editor currentEditor;
      private int lockingSourceCount;
      private long sequenceNumber;

      public Entry(@NotNull String key) {
         Intrinsics.checkNotNullParameter(key, "key");
         super();
         this.key = key;
         this.lengths = new long[DiskLruCache.this.getValueCount$okhttp()];
         this.cleanFiles = (List)(new ArrayList());
         this.dirtyFiles = (List)(new ArrayList());
         StringBuilder fileBuilder = (new StringBuilder(this.key)).append('.');
         int truncateTo = fileBuilder.length();
         int i = 0;

         for(int var6 = DiskLruCache.this.getValueCount$okhttp(); i < var6; ++i) {
            fileBuilder.append(i);
            Collection var7 = (Collection)this.cleanFiles;
            Path var10000 = DiskLruCache.this.getDirectory();
            String var10001 = fileBuilder.toString();
            Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
            var7.add(var10000.resolve(var10001));
            fileBuilder.append(".tmp");
            var7 = (Collection)this.dirtyFiles;
            var10000 = DiskLruCache.this.getDirectory();
            var10001 = fileBuilder.toString();
            Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
            var7.add(var10000.resolve(var10001));
            fileBuilder.setLength(truncateTo);
         }

      }

      @NotNull
      public final String getKey$okhttp() {
         return this.key;
      }

      @NotNull
      public final long[] getLengths$okhttp() {
         return this.lengths;
      }

      @NotNull
      public final List<Path> getCleanFiles$okhttp() {
         return this.cleanFiles;
      }

      @NotNull
      public final List<Path> getDirtyFiles$okhttp() {
         return this.dirtyFiles;
      }

      public final boolean getReadable$okhttp() {
         return this.readable;
      }

      public final void setReadable$okhttp(boolean var1) {
         this.readable = var1;
      }

      public final boolean getZombie$okhttp() {
         return this.zombie;
      }

      public final void setZombie$okhttp(boolean var1) {
         this.zombie = var1;
      }

      @Nullable
      public final Editor getCurrentEditor$okhttp() {
         return this.currentEditor;
      }

      public final void setCurrentEditor$okhttp(@Nullable Editor var1) {
         this.currentEditor = var1;
      }

      public final int getLockingSourceCount$okhttp() {
         return this.lockingSourceCount;
      }

      public final void setLockingSourceCount$okhttp(int var1) {
         this.lockingSourceCount = var1;
      }

      public final long getSequenceNumber$okhttp() {
         return this.sequenceNumber;
      }

      public final void setSequenceNumber$okhttp(long var1) {
         this.sequenceNumber = var1;
      }

      public final void setLengths$okhttp(@NotNull List<String> strings) throws IOException {
         Intrinsics.checkNotNullParameter(strings, "strings");
         if (strings.size() != DiskLruCache.this.getValueCount$okhttp()) {
            this.invalidLengths(strings);
            throw new KotlinNothingValueException();
         } else {
            try {
               int i = 0;

               for(int var3 = ((Collection)strings).size(); i < var3; ++i) {
                  this.lengths[i] = Long.parseLong((String)strings.get(i));
               }

            } catch (NumberFormatException var4) {
               this.invalidLengths(strings);
               throw new KotlinNothingValueException();
            }
         }
      }

      public final void writeLengths$okhttp(@NotNull BufferedSink writer) throws IOException {
         Intrinsics.checkNotNullParameter(writer, "writer");
         long[] var2 = this.lengths;
         int var3 = 0;

         for(int var4 = var2.length; var3 < var4; ++var3) {
            long length = var2[var3];
            writer.writeByte(32).writeDecimalLong(length);
         }

      }

      private final Void invalidLengths(List<String> strings) throws IOException {
         throw new IOException("unexpected journal line: " + strings);
      }

      @Nullable
      public final Snapshot snapshot$okhttp() {
         Lockable $this$assertLockHeld$iv = (Lockable)DiskLruCache.this;
         int $i$f$assertLockHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
         } else if (!this.readable) {
            return null;
         } else if (DiskLruCache.this.civilizedFileSystem || this.currentEditor == null && !this.zombie) {
            List sources = (List)(new ArrayList());
            long[] lengths = (long[])this.lengths.clone();

            try {
               int i = 0;

               for(int var4 = DiskLruCache.this.getValueCount$okhttp(); i < var4; ++i) {
                  ((Collection)sources).add(this.newSource(i));
               }

               return DiskLruCache.this.new Snapshot(this.key, this.sequenceNumber, sources, lengths);
            } catch (FileNotFoundException var8) {
               for(Source source : sources) {
                  _UtilCommonKt.closeQuietly(source);
               }

               try {
                  DiskLruCache.this.removeEntry$okhttp(this);
               } catch (IOException var7) {
               }

               return null;
            }
         } else {
            return null;
         }
      }

      private final Source newSource(int index) {
         Source fileSource = DiskLruCache.this.getFileSystem$okhttp().source((Path)this.cleanFiles.get(index));
         if (DiskLruCache.this.civilizedFileSystem) {
            return fileSource;
         } else {
            int var3 = this.lockingSourceCount++;
            return new ForwardingSource(fileSource) {
               private boolean closed;

               public void close() {
                  super.close();
                  if (!this.closed) {
                     this.closed = true;
                     DiskLruCache var1 = DiskLruCache.this;
                     Entry var2 = Entry.this;
                     DiskLruCache var3 = DiskLruCache.this;
                     synchronized(var1) {
                        int var5 = 0;
                        int var6 = var2.getLockingSourceCount$okhttp();
                        var2.setLockingSourceCount$okhttp(var6 + -1);
                        if (var2.getLockingSourceCount$okhttp() == 0 && var2.getZombie$okhttp()) {
                           var3.removeEntry$okhttp(var2);
                        }

                        Unit var8 = Unit.INSTANCE;
                     }
                  }

               }
            };
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\u000b8\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u00020\r8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u00020\u00058\u0006X\u0087D¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lokhttp3/internal/cache/DiskLruCache$Companion;", "", "<init>", "()V", "JOURNAL_FILE", "", "JOURNAL_FILE_TEMP", "JOURNAL_FILE_BACKUP", "MAGIC", "VERSION_1", "ANY_SEQUENCE_NUMBER", "", "LEGAL_KEY_PATTERN", "Lkotlin/text/Regex;", "CLEAN", "DIRTY", "REMOVE", "READ", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
