package net.dv8tion.jda.internal.utils.compress;

import java.util.zip.DataFormatException;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public interface Decompressor {
   Logger LOG = JDALogger.getLog(Decompressor.class);

   Compression getType();

   void reset();

   void shutdown();

   @Nullable
   byte[] decompress(byte[] var1) throws DataFormatException;
}
