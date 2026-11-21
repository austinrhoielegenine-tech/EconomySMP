package okio.internal;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.ExceptionsKt;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import kotlin.text.StringsKt;
import okio.BufferedSource;
import okio.FileHandle;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.ZipFileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000d\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a.\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0014\b\u0002\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00180\u0016H\u0000\u001a\"\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00170\u001a2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00170\u001cH\u0002\u001a\f\u0010\u001d\u001a\u00020\u0017*\u00020\u001eH\u0000\u001a\f\u0010\u001f\u001a\u00020 *\u00020\u001eH\u0002\u001a\u0014\u0010!\u001a\u00020 *\u00020\u001e2\u0006\u0010\"\u001a\u00020 H\u0002\u001a.\u0010#\u001a\u00020$*\u00020\u001e2\u0006\u0010%\u001a\u00020\u00012\u0018\u0010&\u001a\u0014\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020$0'H\u0002\u001a\f\u0010(\u001a\u00020$*\u00020\u001eH\u0000\u001a\u0014\u0010)\u001a\u00020\u0017*\u00020\u001e2\u0006\u0010*\u001a\u00020\u0017H\u0000\u001a\u0018\u0010+\u001a\u0004\u0018\u00010\u0017*\u00020\u001e2\b\u0010*\u001a\u0004\u0018\u00010\u0017H\u0002\u001a\u0010\u0010,\u001a\u00020\u000b2\u0006\u0010-\u001a\u00020\u000bH\u0000\u001a\u001f\u0010.\u001a\u0004\u0018\u00010\u000b2\u0006\u0010/\u001a\u00020\u00012\u0006\u00100\u001a\u00020\u0001H\u0000¢\u0006\u0002\u00101\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\f\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\r\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000e\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u0018\u00102\u001a\u000203*\u00020\u00018BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b4\u00105¨\u00066"},
   d2 = {"LOCAL_FILE_HEADER_SIGNATURE", "", "CENTRAL_FILE_HEADER_SIGNATURE", "END_OF_CENTRAL_DIRECTORY_SIGNATURE", "ZIP64_LOCATOR_SIGNATURE", "ZIP64_EOCD_RECORD_SIGNATURE", "COMPRESSION_METHOD_DEFLATED", "COMPRESSION_METHOD_STORED", "BIT_FLAG_ENCRYPTED", "BIT_FLAG_UNSUPPORTED_MASK", "MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE", "", "HEADER_ID_ZIP64_EXTENDED_INFO", "HEADER_ID_NTFS_EXTRA", "HEADER_ID_EXTENDED_TIMESTAMP", "openZip", "Lokio/ZipFileSystem;", "zipPath", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "predicate", "Lkotlin/Function1;", "Lokio/internal/ZipEntry;", "", "buildIndex", "", "entries", "", "readCentralDirectoryZipEntry", "Lokio/BufferedSource;", "readEocdRecord", "Lokio/internal/EocdRecord;", "readZip64EocdRecord", "regularRecord", "readExtra", "", "extraSize", "block", "Lkotlin/Function2;", "skipLocalHeader", "readLocalHeader", "centralDirectoryZipEntry", "readOrSkipLocalHeader", "filetimeToEpochMillis", "filetime", "dosDateTimeToEpochMillis", "date", "time", "(II)Ljava/lang/Long;", "hex", "", "getHex", "(I)Ljava/lang/String;", "okio"}
)
@SourceDebugExtension({"SMAP\nZipFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n+ 2 Okio.kt\nokio/Okio__OkioKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,503:1\n58#2,4:504\n58#2,4:508\n58#2,22:512\n66#2,10:534\n62#2,3:544\n77#2,3:547\n58#2,22:550\n66#2,10:572\n62#2,3:582\n77#2,3:585\n1056#3:588\n*S KotlinDebug\n*F\n+ 1 ZipFiles.kt\nokio/internal/ZipFilesKt\n*L\n66#1:504,4\n101#1:508,4\n109#1:512,22\n101#1:534,10\n101#1:544,3\n101#1:547,3\n125#1:550,22\n66#1:572,10\n66#1:582,3\n66#1:585,3\n155#1:588\n*E\n"})
public final class ZipFilesKt {
   private static final int LOCAL_FILE_HEADER_SIGNATURE = 67324752;
   private static final int CENTRAL_FILE_HEADER_SIGNATURE = 33639248;
   private static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 101010256;
   private static final int ZIP64_LOCATOR_SIGNATURE = 117853008;
   private static final int ZIP64_EOCD_RECORD_SIGNATURE = 101075792;
   public static final int COMPRESSION_METHOD_DEFLATED = 8;
   public static final int COMPRESSION_METHOD_STORED = 0;
   private static final int BIT_FLAG_ENCRYPTED = 1;
   private static final int BIT_FLAG_UNSUPPORTED_MASK = 1;
   private static final long MAX_ZIP_ENTRY_AND_ARCHIVE_SIZE = 4294967295L;
   private static final int HEADER_ID_ZIP64_EXTENDED_INFO = 1;
   private static final int HEADER_ID_NTFS_EXTRA = 10;
   private static final int HEADER_ID_EXTENDED_TIMESTAMP = 21589;

   @NotNull
   public static final ZipFileSystem openZip(@NotNull Path zipPath, @NotNull FileSystem fileSystem, @NotNull Function1<? super ZipEntry, Boolean> predicate) throws IOException {
      Intrinsics.checkNotNullParameter(zipPath, "zipPath");
      Intrinsics.checkNotNullParameter(fileSystem, "fileSystem");
      Intrinsics.checkNotNullParameter(predicate, "predicate");
      Closeable $this$use$iv = fileSystem.openReadOnly(zipPath);
      int $i$f$use = 0;
      Throwable thrown$iv = null;
      boolean var66 = false;

      ZipFileSystem var44;
      label1879: {
         try {
            var66 = true;
            FileHandle fileHandle = (FileHandle)$this$use$iv;
            int var7 = false;
            long scanOffset = fileHandle.size() - (long)22;
            if (scanOffset < 0L) {
               throw new IOException("not a zip: size=" + fileHandle.size());
            }

            long stopOffset = Math.max(scanOffset - 65536L, 0L);
            long eocdOffset = 0L;
            Object record = null;
            String comment = null;

            while(true) {
               BufferedSource source = Okio.buffer(fileHandle.source(scanOffset));

               try {
                  if (source.readIntLe() == 101010256) {
                     eocdOffset = scanOffset;
                     var176 = readEocdRecord(source);
                     comment = source.readUtf8((long)var176.getCommentByteCount());
                     break;
                  }
               } finally {
                  source.close();
               }

               scanOffset += -1L;
               if (scanOffset < stopOffset) {
                  throw new IOException("not a zip: end of central directory signature not found");
               }
            }

            long zip64LocatorOffset = eocdOffset - (long)20;
            if (zip64LocatorOffset > 0L) {
               Closeable $this$use$iv = Okio.buffer(fileHandle.source(zip64LocatorOffset));
               int $i$f$use = 0;
               Throwable thrown$iv = null;
               boolean var110 = false;

               Unit zip64LocatorSource;
               label1844: {
                  label1843: {
                     try {
                        var110 = true;
                        BufferedSource thrown$iv = (BufferedSource)$this$use$iv;
                        int var24 = false;
                        if (thrown$iv.readIntLe() == 117853008) {
                           int diskWithCentralDir = thrown$iv.readIntLe();
                           long zip64EocdRecordOffset = thrown$iv.readLongLe();
                           int numDisks = thrown$iv.readIntLe();
                           if (numDisks != 1 || diskWithCentralDir != 0) {
                              throw new IOException("unsupported zip: spanned");
                           }

                           Closeable $this$use$iv = Okio.buffer(fileHandle.source(zip64EocdRecordOffset));
                           int $i$f$use = 0;
                           Throwable thrown$iv = null;
                           boolean var132 = false;

                           Unit result$iv;
                           label1830: {
                              label1829: {
                                 try {
                                    var132 = true;
                                    BufferedSource zip64EocdSource = (BufferedSource)$this$use$iv;
                                    int result$iv = false;
                                    int zip64EocdSignature = zip64EocdSource.readIntLe();
                                    if (zip64EocdSignature != 101075792) {
                                       throw new IOException("bad zip: expected " + getHex(101075792) + " but was " + getHex(zip64EocdSignature));
                                    }

                                    var176 = readZip64EocdRecord(zip64EocdSource, var176);
                                    result$iv = Unit.INSTANCE;
                                    var132 = false;
                                    break label1829;
                                 } catch (Throwable t$iv) {
                                    thrown$iv = t$iv;
                                    result$iv = null;
                                    var132 = false;
                                 } finally {
                                    if (var132) {
                                       try {
                                          if ($this$use$iv != null) {
                                             $this$use$iv.close();
                                             Unit var10000 = Unit.INSTANCE;
                                          } else {
                                             Object var202 = null;
                                          }
                                       } catch (Throwable t$iv) {
                                          if (thrown$iv != null) {
                                             ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                                          }
                                       }

                                    }
                                 }

                                 try {
                                    if ($this$use$iv != null) {
                                       $this$use$iv.close();
                                       Unit var203 = Unit.INSTANCE;
                                    } else {
                                       Object var204 = null;
                                    }
                                 } catch (Throwable t$iv) {
                                    if (thrown$iv == null) {
                                       thrown$iv = t$iv;
                                    } else {
                                       ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                                    }
                                 }
                                 break label1830;
                              }

                              try {
                                 if ($this$use$iv != null) {
                                    $this$use$iv.close();
                                    Unit var205 = Unit.INSTANCE;
                                 } else {
                                    Object var206 = null;
                                 }
                              } catch (Throwable t$iv) {
                                 thrown$iv = t$iv;
                              }
                           }

                           if (thrown$iv != null) {
                              throw thrown$iv;
                           }

                           Object var207 = result$iv;
                        }

                        zip64LocatorSource = Unit.INSTANCE;
                        var110 = false;
                        break label1843;
                     } catch (Throwable t$iv) {
                        thrown$iv = t$iv;
                        zip64LocatorSource = null;
                        var110 = false;
                     } finally {
                        if (var110) {
                           try {
                              if ($this$use$iv != null) {
                                 $this$use$iv.close();
                                 Unit var208 = Unit.INSTANCE;
                              } else {
                                 Object var209 = null;
                              }
                           } catch (Throwable t$iv) {
                              if (thrown$iv != null) {
                                 ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                              }
                           }

                        }
                     }

                     try {
                        if ($this$use$iv != null) {
                           $this$use$iv.close();
                           Unit var210 = Unit.INSTANCE;
                        } else {
                           Object var211 = null;
                        }
                     } catch (Throwable t$iv) {
                        if (thrown$iv == null) {
                           thrown$iv = t$iv;
                        } else {
                           ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                        }
                     }
                     break label1844;
                  }

                  try {
                     if ($this$use$iv != null) {
                        $this$use$iv.close();
                        Unit var212 = Unit.INSTANCE;
                     } else {
                        Object var213 = null;
                     }
                  } catch (Throwable t$iv) {
                     thrown$iv = t$iv;
                  }
               }

               if (thrown$iv != null) {
                  throw thrown$iv;
               }

               Object var214 = zip64LocatorSource;
            }

            List entries = (List)(new ArrayList());
            Closeable $this$use$iv = Okio.buffer(fileHandle.source(var176.getCentralDirectoryOffset()));
            int $i$f$use = 0;
            Throwable thrown$iv = null;
            boolean var88 = false;

            Unit result$iv;
            label1867: {
               label1866: {
                  try {
                     var88 = true;
                     BufferedSource t$iv = (BufferedSource)$this$use$iv;
                     int t$iv = false;
                     long i = 0L;

                     for(long var39 = var176.getEntryCount(); i < var39; ++i) {
                        ZipEntry entry = readCentralDirectoryZipEntry(t$iv);
                        if (entry.getOffset() >= var176.getCentralDirectoryOffset()) {
                           throw new IOException("bad zip: local file header offset >= central directory offset");
                        }

                        if ((Boolean)predicate.invoke(entry)) {
                           ((Collection)entries).add(entry);
                        }
                     }

                     result$iv = Unit.INSTANCE;
                     var88 = false;
                     break label1866;
                  } catch (Throwable t$iv) {
                     thrown$iv = t$iv;
                     result$iv = null;
                     var88 = false;
                  } finally {
                     if (var88) {
                        try {
                           if ($this$use$iv != null) {
                              $this$use$iv.close();
                              Unit var215 = Unit.INSTANCE;
                           } else {
                              Object var216 = null;
                           }
                        } catch (Throwable t$iv) {
                           if (thrown$iv != null) {
                              ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                           }
                        }

                     }
                  }

                  try {
                     if ($this$use$iv != null) {
                        $this$use$iv.close();
                        Unit var217 = Unit.INSTANCE;
                     } else {
                        Object var218 = null;
                     }
                  } catch (Throwable t$iv) {
                     if (thrown$iv == null) {
                        thrown$iv = t$iv;
                     } else {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                     }
                  }
                  break label1867;
               }

               try {
                  if ($this$use$iv != null) {
                     $this$use$iv.close();
                     Unit var219 = Unit.INSTANCE;
                  } else {
                     Object var220 = null;
                  }
               } catch (Throwable t$iv) {
                  thrown$iv = t$iv;
               }
            }

            if (thrown$iv != null) {
               throw thrown$iv;
            }

            Object var221 = result$iv;
            Map index = buildIndex(entries);
            var44 = new ZipFileSystem(zipPath, fileSystem, index, comment);
            var66 = false;
            break label1879;
         } catch (Throwable t$iv) {
            thrown$iv = t$iv;
            Object var41 = null;
            var66 = false;
         } finally {
            if (var66) {
               try {
                  if ($this$use$iv != null) {
                     $this$use$iv.close();
                     Unit var222 = Unit.INSTANCE;
                  } else {
                     Object var223 = null;
                  }
               } catch (Throwable t$iv) {
                  if (thrown$iv != null) {
                     ExceptionsKt.addSuppressed(thrown$iv, t$iv);
                  }
               }

            }
         }

         try {
            if ($this$use$iv != null) {
               $this$use$iv.close();
               Unit var224 = Unit.INSTANCE;
            } else {
               Object var225 = null;
            }
         } catch (Throwable t$iv) {
            if (thrown$iv == null) {
               thrown$iv = t$iv;
            } else {
               ExceptionsKt.addSuppressed(thrown$iv, t$iv);
            }
         }

         if (thrown$iv != null) {
            throw thrown$iv;
         }

         throw new KotlinNothingValueException();
      }

      try {
         if ($this$use$iv != null) {
            $this$use$iv.close();
            Unit var226 = Unit.INSTANCE;
         } else {
            Object var227 = null;
         }
      } catch (Throwable var154) {
      }

      return var44;
   }

   // $FF: synthetic method
   public static ZipFileSystem openZip$default(Path var0, FileSystem var1, Function1 var2, int var3, Object var4) throws IOException {
      if ((var3 & 4) != 0) {
         var2 = ZipFilesKt::openZip$lambda$0;
      }

      return openZip(var0, var1, var2);
   }

   private static final Map<Path, ZipEntry> buildIndex(List<ZipEntry> entries) {
      Path root = Path.Companion.get$default(Path.Companion, (String)"/", false, 1, (Object)null);
      Pair[] var3 = new Pair[]{TuplesKt.to(root, new ZipEntry(root, true, (String)null, 0L, 0L, 0L, 0, 0L, 0, 0, (Long)null, (Long)null, (Long)null, (Integer)null, (Integer)null, (Integer)null, 65532, (DefaultConstructorMarker)null))};
      Map result = MapsKt.mutableMapOf(var3);
      Iterable $this$sortedBy$iv = (Iterable)entries;
      int $i$f$sortedBy = 0;

      for(ZipEntry entry : CollectionsKt.sortedWith($this$sortedBy$iv, new ZipFilesKt$buildIndex$$inlined$sortedBy$1())) {
         ZipEntry replaced = (ZipEntry)result.put(entry.getCanonicalPath(), entry);
         if (replaced == null) {
            ZipEntry child = entry;

            while(true) {
               Path var10000 = child.getCanonicalPath().parent();
               if (var10000 == null) {
                  break;
               }

               Path parentPath = var10000;
               ZipEntry parentEntry = (ZipEntry)result.get(parentPath);
               if (parentEntry != null) {
                  ((Collection)parentEntry.getChildren()).add(child.getCanonicalPath());
                  break;
               }

               parentEntry = new ZipEntry(parentPath, true, (String)null, 0L, 0L, 0L, 0, 0L, 0, 0, (Long)null, (Long)null, (Long)null, (Integer)null, (Integer)null, (Integer)null, 65532, (DefaultConstructorMarker)null);
               result.put(parentPath, parentEntry);
               ((Collection)parentEntry.getChildren()).add(child.getCanonicalPath());
               child = parentEntry;
            }
         }
      }

      return result;
   }

   @NotNull
   public static final ZipEntry readCentralDirectoryZipEntry(@NotNull BufferedSource $this$readCentralDirectoryZipEntry) throws IOException {
      Intrinsics.checkNotNullParameter($this$readCentralDirectoryZipEntry, "<this>");
      int signature = $this$readCentralDirectoryZipEntry.readIntLe();
      if (signature != 33639248) {
         throw new IOException("bad zip: expected " + getHex(33639248) + " but was " + getHex(signature));
      } else {
         $this$readCentralDirectoryZipEntry.skip(4L);
         int bitFlag = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
         if ((bitFlag & 1) != 0) {
            throw new IOException("unsupported zip: general purpose bit flag=" + getHex(bitFlag));
         } else {
            int compressionMethod = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            int dosLastModifiedTime = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            int dosLastModifiedDate = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            long crc = (long)$this$readCentralDirectoryZipEntry.readIntLe() & 4294967295L;
            Ref.LongRef compressedSize = new Ref.LongRef();
            compressedSize.element = (long)$this$readCentralDirectoryZipEntry.readIntLe() & 4294967295L;
            Ref.LongRef size = new Ref.LongRef();
            size.element = (long)$this$readCentralDirectoryZipEntry.readIntLe() & 4294967295L;
            int nameSize = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            int extraSize = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            int commentByteCount = $this$readCentralDirectoryZipEntry.readShortLe() & '\uffff';
            $this$readCentralDirectoryZipEntry.skip(8L);
            Ref.LongRef offset = new Ref.LongRef();
            offset.element = (long)$this$readCentralDirectoryZipEntry.readIntLe() & 4294967295L;
            String name = $this$readCentralDirectoryZipEntry.readUtf8((long)nameSize);
            if (StringsKt.contains$default((CharSequence)name, '\u0000', false, 2, (Object)null)) {
               throw new IOException("bad zip: filename contains 0x00");
            } else {
               int var19 = 0;
               long result = 0L;
               if (size.element == 4294967295L) {
                  result += (long)8;
               }

               if (compressedSize.element == 4294967295L) {
                  result += (long)8;
               }

               if (offset.element == 4294967295L) {
                  result += (long)8;
               }

               long requiredZip64ExtraSize = result;
               Ref.ObjectRef ntfsLastModifiedAtFiletime = new Ref.ObjectRef();
               Ref.ObjectRef ntfsLastAccessedAtFiletime = new Ref.ObjectRef();
               Ref.ObjectRef ntfsCreatedAtFiletime = new Ref.ObjectRef();
               Ref.BooleanRef hasZip64Extra = new Ref.BooleanRef();
               readExtra($this$readCentralDirectoryZipEntry, extraSize, ZipFilesKt::readCentralDirectoryZipEntry$lambda$1);
               if (requiredZip64ExtraSize > 0L && !hasZip64Extra.element) {
                  throw new IOException("bad zip: zip64 extra required but absent");
               } else {
                  String comment = $this$readCentralDirectoryZipEntry.readUtf8((long)commentByteCount);
                  Path canonicalPath = Path.Companion.get$default(Path.Companion, (String)"/", false, 1, (Object)null).resolve(name);
                  boolean isDirectory = StringsKt.endsWith$default(name, "/", false, 2, (Object)null);
                  return new ZipEntry(canonicalPath, isDirectory, comment, crc, compressedSize.element, size.element, compressionMethod, offset.element, dosLastModifiedDate, dosLastModifiedTime, (Long)ntfsLastModifiedAtFiletime.element, (Long)ntfsLastAccessedAtFiletime.element, (Long)ntfsCreatedAtFiletime.element, (Integer)null, (Integer)null, (Integer)null, 57344, (DefaultConstructorMarker)null);
               }
            }
         }
      }
   }

   private static final EocdRecord readEocdRecord(BufferedSource $this$readEocdRecord) throws IOException {
      int diskNumber = $this$readEocdRecord.readShortLe() & '\uffff';
      int diskWithCentralDir = $this$readEocdRecord.readShortLe() & '\uffff';
      long entryCount = (long)($this$readEocdRecord.readShortLe() & '\uffff');
      long totalEntryCount = (long)($this$readEocdRecord.readShortLe() & '\uffff');
      if (entryCount == totalEntryCount && diskNumber == 0 && diskWithCentralDir == 0) {
         $this$readEocdRecord.skip(4L);
         long centralDirectoryOffset = (long)$this$readEocdRecord.readIntLe() & 4294967295L;
         int commentByteCount = $this$readEocdRecord.readShortLe() & '\uffff';
         return new EocdRecord(entryCount, centralDirectoryOffset, commentByteCount);
      } else {
         throw new IOException("unsupported zip: spanned");
      }
   }

   private static final EocdRecord readZip64EocdRecord(BufferedSource $this$readZip64EocdRecord, EocdRecord regularRecord) throws IOException {
      $this$readZip64EocdRecord.skip(12L);
      int diskNumber = $this$readZip64EocdRecord.readIntLe();
      int diskWithCentralDirStart = $this$readZip64EocdRecord.readIntLe();
      long entryCount = $this$readZip64EocdRecord.readLongLe();
      long totalEntryCount = $this$readZip64EocdRecord.readLongLe();
      if (entryCount == totalEntryCount && diskNumber == 0 && diskWithCentralDirStart == 0) {
         $this$readZip64EocdRecord.skip(8L);
         long centralDirectoryOffset = $this$readZip64EocdRecord.readLongLe();
         return new EocdRecord(entryCount, centralDirectoryOffset, regularRecord.getCommentByteCount());
      } else {
         throw new IOException("unsupported zip: spanned");
      }
   }

   private static final void readExtra(BufferedSource $this$readExtra, int extraSize, Function2<? super Integer, ? super Long, Unit> block) {
      long dataSize;
      long remaining;
      for(remaining = (long)extraSize; remaining != 0L; remaining -= dataSize) {
         if (remaining < 4L) {
            throw new IOException("bad zip: truncated header in extra field");
         }

         int headerId = $this$readExtra.readShortLe() & '\uffff';
         dataSize = (long)$this$readExtra.readShortLe() & 65535L;
         remaining -= (long)4;
         if (remaining < dataSize) {
            throw new IOException("bad zip: truncated value in extra field");
         }

         $this$readExtra.require(dataSize);
         long sizeBefore = $this$readExtra.getBuffer().size();
         block.invoke(headerId, dataSize);
         long fieldRemaining = dataSize + $this$readExtra.getBuffer().size() - sizeBefore;
         if (fieldRemaining < 0L) {
            throw new IOException("unsupported zip: too many bytes processed for " + headerId);
         }

         if (fieldRemaining > 0L) {
            $this$readExtra.getBuffer().skip(fieldRemaining);
         }
      }

   }

   public static final void skipLocalHeader(@NotNull BufferedSource $this$skipLocalHeader) {
      Intrinsics.checkNotNullParameter($this$skipLocalHeader, "<this>");
      readOrSkipLocalHeader($this$skipLocalHeader, (ZipEntry)null);
   }

   @NotNull
   public static final ZipEntry readLocalHeader(@NotNull BufferedSource $this$readLocalHeader, @NotNull ZipEntry centralDirectoryZipEntry) {
      Intrinsics.checkNotNullParameter($this$readLocalHeader, "<this>");
      Intrinsics.checkNotNullParameter(centralDirectoryZipEntry, "centralDirectoryZipEntry");
      ZipEntry var10000 = readOrSkipLocalHeader($this$readLocalHeader, centralDirectoryZipEntry);
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }

   private static final ZipEntry readOrSkipLocalHeader(BufferedSource $this$readOrSkipLocalHeader, ZipEntry centralDirectoryZipEntry) {
      int signature = $this$readOrSkipLocalHeader.readIntLe();
      if (signature != 67324752) {
         throw new IOException("bad zip: expected " + getHex(67324752) + " but was " + getHex(signature));
      } else {
         $this$readOrSkipLocalHeader.skip(2L);
         int bitFlag = $this$readOrSkipLocalHeader.readShortLe() & '\uffff';
         if ((bitFlag & 1) != 0) {
            throw new IOException("unsupported zip: general purpose bit flag=" + getHex(bitFlag));
         } else {
            $this$readOrSkipLocalHeader.skip(18L);
            long fileNameLength = (long)$this$readOrSkipLocalHeader.readShortLe() & 65535L;
            int extraSize = $this$readOrSkipLocalHeader.readShortLe() & '\uffff';
            $this$readOrSkipLocalHeader.skip(fileNameLength);
            if (centralDirectoryZipEntry == null) {
               $this$readOrSkipLocalHeader.skip((long)extraSize);
               return null;
            } else {
               Ref.ObjectRef extendedLastModifiedAtSeconds = new Ref.ObjectRef();
               Ref.ObjectRef extendedLastAccessedAtSeconds = new Ref.ObjectRef();
               Ref.ObjectRef extendedCreatedAtSeconds = new Ref.ObjectRef();
               readExtra($this$readOrSkipLocalHeader, extraSize, ZipFilesKt::readOrSkipLocalHeader$lambda$0);
               return centralDirectoryZipEntry.copy$okio((Integer)extendedLastModifiedAtSeconds.element, (Integer)extendedLastAccessedAtSeconds.element, (Integer)extendedCreatedAtSeconds.element);
            }
         }
      }
   }

   public static final long filetimeToEpochMillis(long filetime) {
      return filetime / (long)10000 - 11644473600000L;
   }

   @Nullable
   public static final Long dosDateTimeToEpochMillis(int date, int time) {
      return time == -1 ? null : _ZlibJvmKt.datePartsToEpochMillis(1980 + (date >> 9 & 127), date >> 5 & 15, date & 31, time >> 11 & 31, time >> 5 & 63, (time & 31) << 1);
   }

   private static final String getHex(int $this$hex) {
      StringBuilder var10000 = (new StringBuilder()).append("0x");
      String var10001 = Integer.toString($this$hex, CharsKt.checkRadix(16));
      Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
      return var10000.append(var10001).toString();
   }

   private static final boolean openZip$lambda$0(ZipEntry it) {
      Intrinsics.checkNotNullParameter(it, "it");
      return true;
   }

   private static final Unit readCentralDirectoryZipEntry$lambda$1(Ref.BooleanRef $hasZip64Extra, long $requiredZip64ExtraSize, Ref.LongRef $size, BufferedSource $this_readCentralDirectoryZipEntry, Ref.LongRef $compressedSize, Ref.LongRef $offset, Ref.ObjectRef $ntfsLastModifiedAtFiletime, Ref.ObjectRef $ntfsLastAccessedAtFiletime, Ref.ObjectRef $ntfsCreatedAtFiletime, int headerId, long dataSize) {
      switch (headerId) {
         case 1:
            if ($hasZip64Extra.element) {
               throw new IOException("bad zip: zip64 extra repeated");
            }

            $hasZip64Extra.element = true;
            if (dataSize < $requiredZip64ExtraSize) {
               throw new IOException("bad zip: zip64 extra too short");
            }

            $size.element = $size.element == 4294967295L ? $this_readCentralDirectoryZipEntry.readLongLe() : $size.element;
            $compressedSize.element = $compressedSize.element == 4294967295L ? $this_readCentralDirectoryZipEntry.readLongLe() : 0L;
            $offset.element = $offset.element == 4294967295L ? $this_readCentralDirectoryZipEntry.readLongLe() : 0L;
            break;
         case 10:
            if (dataSize < 4L) {
               throw new IOException("bad zip: NTFS extra too short");
            }

            $this_readCentralDirectoryZipEntry.skip(4L);
            readExtra($this_readCentralDirectoryZipEntry, (int)(dataSize - 4L), ZipFilesKt::readCentralDirectoryZipEntry$lambda$1$0);
      }

      return Unit.INSTANCE;
   }

   private static final Unit readCentralDirectoryZipEntry$lambda$1$0(Ref.ObjectRef $ntfsLastModifiedAtFiletime, BufferedSource $this_readCentralDirectoryZipEntry, Ref.ObjectRef $ntfsLastAccessedAtFiletime, Ref.ObjectRef $ntfsCreatedAtFiletime, int attributeId, long attributeSize) {
      if (attributeId == 1) {
         if ($ntfsLastModifiedAtFiletime.element != null) {
            throw new IOException("bad zip: NTFS extra attribute tag 0x0001 repeated");
         }

         if (attributeSize != 24L) {
            throw new IOException("bad zip: NTFS extra attribute tag 0x0001 size != 24");
         }

         $ntfsLastModifiedAtFiletime.element = (T)$this_readCentralDirectoryZipEntry.readLongLe();
         $ntfsLastAccessedAtFiletime.element = (T)$this_readCentralDirectoryZipEntry.readLongLe();
         $ntfsCreatedAtFiletime.element = (T)$this_readCentralDirectoryZipEntry.readLongLe();
      }

      return Unit.INSTANCE;
   }

   private static final Unit readOrSkipLocalHeader$lambda$0(BufferedSource $this_readOrSkipLocalHeader, Ref.ObjectRef $extendedLastModifiedAtSeconds, Ref.ObjectRef $extendedLastAccessedAtSeconds, Ref.ObjectRef $extendedCreatedAtSeconds, int headerId, long dataSize) {
      if (headerId == 21589) {
         if (dataSize < 1L) {
            throw new IOException("bad zip: extended timestamp extra too short");
         }

         int flags = $this_readOrSkipLocalHeader.readByte() & 255;
         boolean hasLastModifiedAtMillis = (flags & 1) == 1;
         boolean hasLastAccessedAtMillis = (flags & 2) == 2;
         boolean hasCreatedAtMillis = (flags & 4) == 4;
         int var14 = 0;
         long result = 1L;
         if (hasLastModifiedAtMillis) {
            result += 4L;
         }

         if (hasLastAccessedAtMillis) {
            result += 4L;
         }

         if (hasCreatedAtMillis) {
            result += 4L;
         }

         if (dataSize < result) {
            throw new IOException("bad zip: extended timestamp extra too short");
         }

         if (hasLastModifiedAtMillis) {
            $extendedLastModifiedAtSeconds.element = (T)$this_readOrSkipLocalHeader.readIntLe();
         }

         if (hasLastAccessedAtMillis) {
            $extendedLastAccessedAtSeconds.element = (T)$this_readOrSkipLocalHeader.readIntLe();
         }

         if (hasCreatedAtMillis) {
            $extendedCreatedAtSeconds.element = (T)$this_readOrSkipLocalHeader.readIntLe();
         }
      }

      return Unit.INSTANCE;
   }
}
