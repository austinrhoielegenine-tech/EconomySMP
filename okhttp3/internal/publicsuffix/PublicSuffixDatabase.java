package okhttp3.internal.publicsuffix;

import java.net.IDN;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okhttp3.internal._UtilCommonKt;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\u0007J\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00070\n2\u0006\u0010\b\u001a\u00020\u0007H\u0002J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\n2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\nH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"},
   d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixDatabase;", "", "publicSuffixList", "Lokhttp3/internal/publicsuffix/PublicSuffixList;", "<init>", "(Lokhttp3/internal/publicsuffix/PublicSuffixList;)V", "getEffectiveTldPlusOne", "", "domain", "splitDomain", "", "findMatchingRule", "domainLabels", "Companion", "okhttp"}
)
public final class PublicSuffixDatabase {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final PublicSuffixList publicSuffixList;
   @NotNull
   private static final ByteString WILDCARD_LABEL;
   @NotNull
   private static final List<String> PREVAILING_RULE;
   private static final char EXCEPTION_MARKER = '!';
   @NotNull
   private static PublicSuffixDatabase instance;

   public PublicSuffixDatabase(@NotNull PublicSuffixList publicSuffixList) {
      Intrinsics.checkNotNullParameter(publicSuffixList, "publicSuffixList");
      super();
      this.publicSuffixList = publicSuffixList;
   }

   @Nullable
   public final String getEffectiveTldPlusOne(@NotNull String domain) {
      Intrinsics.checkNotNullParameter(domain, "domain");
      String unicodeDomain = IDN.toUnicode(domain);
      Intrinsics.checkNotNull(unicodeDomain);
      List domainLabels = this.splitDomain(unicodeDomain);
      List rule = this.findMatchingRule(domainLabels);
      if (domainLabels.size() == rule.size() && ((String)rule.get(0)).charAt(0) != '!') {
         return null;
      } else {
         int firstLabelOffset = ((String)rule.get(0)).charAt(0) == '!' ? domainLabels.size() - rule.size() : domainLabels.size() - (rule.size() + 1);
         return SequencesKt.joinToString$default(SequencesKt.drop(CollectionsKt.asSequence((Iterable)this.splitDomain(domain)), firstLabelOffset), (CharSequence)".", (CharSequence)null, (CharSequence)null, 0, (CharSequence)null, (Function1)null, 62, (Object)null);
      }
   }

   private final List<String> splitDomain(String domain) {
      CharSequence var10000 = (CharSequence)domain;
      char[] var3 = new char[]{'.'};
      List domainLabels = StringsKt.split$default(var10000, var3, false, 0, 6, (Object)null);
      return Intrinsics.areEqual((Object)CollectionsKt.last(domainLabels), (Object)"") ? CollectionsKt.dropLast(domainLabels, 1) : domainLabels;
   }

   private final List<String> findMatchingRule(List<String> domainLabels) {
      this.publicSuffixList.ensureLoaded();
      int var3 = 0;
      int i = domainLabels.size();

      ByteString[] labelsWithWildcard;
      for(labelsWithWildcard = new ByteString[i]; var3 < i; ++var3) {
         labelsWithWildcard[var3] = ByteString.Companion.encodeUtf8((String)domainLabels.get(var3));
      }

      ByteString[] domainLabelsUtf8Bytes = labelsWithWildcard;
      String exactMatch = null;
      i = 0;

      for(int var14 = labelsWithWildcard.length; i < var14; ++i) {
         String rule = Companion.binarySearch(this.publicSuffixList.getBytes(), domainLabelsUtf8Bytes, i);
         if (rule != null) {
            exactMatch = rule;
            break;
         }
      }

      String wildcardMatch = null;
      if (domainLabelsUtf8Bytes.length > 1) {
         labelsWithWildcard = (ByteString[])domainLabelsUtf8Bytes.clone();
         int labelIndex = 0;

         for(int var7 = labelsWithWildcard.length - 1; labelIndex < var7; ++labelIndex) {
            labelsWithWildcard[labelIndex] = WILDCARD_LABEL;
            String rule = Companion.binarySearch(this.publicSuffixList.getBytes(), labelsWithWildcard, labelIndex);
            if (rule != null) {
               wildcardMatch = rule;
               break;
            }
         }
      }

      String exception = null;
      if (wildcardMatch != null) {
         int labelIndex = 0;

         for(int var22 = domainLabelsUtf8Bytes.length - 1; labelIndex < var22; ++labelIndex) {
            String rule = Companion.binarySearch(this.publicSuffixList.getExceptionBytes(), domainLabelsUtf8Bytes, labelIndex);
            if (rule != null) {
               exception = rule;
               break;
            }
         }
      }

      if (exception != null) {
         exception = '!' + exception;
         CharSequence var28 = (CharSequence)exception;
         char[] var21 = new char[]{'.'};
         return StringsKt.split$default(var28, var21, false, 0, 6, (Object)null);
      } else if (exactMatch == null && wildcardMatch == null) {
         return PREVAILING_RULE;
      } else {
         List var25;
         label51: {
            if (exactMatch != null) {
               CharSequence var10000 = (CharSequence)exactMatch;
               char[] var9 = new char[]{'.'};
               var25 = StringsKt.split$default(var10000, var9, false, 0, 6, (Object)null);
               if (var25 != null) {
                  break label51;
               }
            }

            var25 = CollectionsKt.emptyList();
         }

         List exactRuleLabels;
         label46: {
            exactRuleLabels = var25;
            if (wildcardMatch != null) {
               CharSequence var26 = (CharSequence)wildcardMatch;
               char[] var10 = new char[]{'.'};
               var25 = StringsKt.split$default(var26, var10, false, 0, 6, (Object)null);
               if (var25 != null) {
                  break label46;
               }
            }

            var25 = CollectionsKt.emptyList();
         }

         List wildcardRuleLabels = var25;
         return exactRuleLabels.size() > wildcardRuleLabels.size() ? exactRuleLabels : wildcardRuleLabels;
      }
   }

   static {
      byte[] var0 = new byte[]{42};
      WILDCARD_LABEL = ByteString.Companion.of(var0);
      PREVAILING_RULE = CollectionsKt.listOf("*");
      instance = new PublicSuffixDatabase(PublicSuffixList_jvmKt.getDefault(PublicSuffixList.Companion));
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\f\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\r\u001a\u00020\fJ)\u0010\u000e\u001a\u0004\u0018\u00010\b*\u00020\u00052\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0002¢\u0006\u0002\u0010\u0013J\r\u0010\u0014\u001a\u00020\u0015H\u0000¢\u0006\u0002\b\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0017"},
      d2 = {"Lokhttp3/internal/publicsuffix/PublicSuffixDatabase$Companion;", "", "<init>", "()V", "WILDCARD_LABEL", "Lokio/ByteString;", "PREVAILING_RULE", "", "", "EXCEPTION_MARKER", "", "instance", "Lokhttp3/internal/publicsuffix/PublicSuffixDatabase;", "get", "binarySearch", "labels", "", "labelIndex", "", "(Lokio/ByteString;[Lokio/ByteString;I)Ljava/lang/String;", "resetForTests", "", "resetForTests$okhttp", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final PublicSuffixDatabase get() {
         return PublicSuffixDatabase.instance;
      }

      private final String binarySearch(ByteString $this$binarySearch, ByteString[] labels, int labelIndex) {
         int low = 0;
         int high = $this$binarySearch.size();
         String match = null;

         while(low < high) {
            int mid;
            for(mid = (low + high) / 2; mid > -1 && $this$binarySearch.getByte(mid) != 10; --mid) {
            }

            ++mid;

            int end;
            for(end = 1; $this$binarySearch.getByte(mid + end) != 10; ++end) {
            }

            int publicSuffixLength = mid + end - mid;
            int compareResult = 0;
            int currentLabelIndex = labelIndex;
            int currentLabelByteIndex = 0;
            int publicSuffixByteIndex = 0;
            boolean expectDot = false;

            while(true) {
               int byte0 = 0;
               if (expectDot) {
                  byte0 = 46;
                  expectDot = false;
               } else {
                  byte0 = _UtilCommonKt.and((byte)labels[currentLabelIndex].getByte(currentLabelByteIndex), 255);
               }

               int byte1 = _UtilCommonKt.and((byte)$this$binarySearch.getByte(mid + publicSuffixByteIndex), 255);
               compareResult = byte0 - byte1;
               if (compareResult != 0) {
                  break;
               }

               ++publicSuffixByteIndex;
               ++currentLabelByteIndex;
               if (publicSuffixByteIndex == publicSuffixLength) {
                  break;
               }

               if (labels[currentLabelIndex].size() == currentLabelByteIndex) {
                  if (currentLabelIndex == labels.length - 1) {
                     break;
                  }

                  ++currentLabelIndex;
                  currentLabelByteIndex = -1;
                  expectDot = true;
               }
            }

            if (compareResult < 0) {
               high = mid - 1;
            } else if (compareResult > 0) {
               low = mid + end + 1;
            } else {
               int publicSuffixBytesLeft = publicSuffixLength - publicSuffixByteIndex;
               int labelBytesLeft = labels[currentLabelIndex].size() - currentLabelByteIndex;
               int i = currentLabelIndex + 1;

               for(int var18 = labels.length; i < var18; ++i) {
                  labelBytesLeft += labels[i].size();
               }

               if (labelBytesLeft < publicSuffixBytesLeft) {
                  high = mid - 1;
               } else {
                  if (labelBytesLeft <= publicSuffixBytesLeft) {
                     match = $this$binarySearch.substring(mid, mid + publicSuffixLength).string(Charsets.UTF_8);
                     break;
                  }

                  low = mid + end + 1;
               }
            }
         }

         return match;
      }

      public final void resetForTests$okhttp() {
         PublicSuffixDatabase.instance = new PublicSuffixDatabase(PublicSuffixList_jvmKt.getDefault(PublicSuffixList.Companion));
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
