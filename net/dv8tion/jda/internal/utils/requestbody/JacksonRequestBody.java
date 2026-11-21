package net.dv8tion.jda.internal.utils.requestbody;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.utils.SerializationUtil;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class JacksonRequestBody extends RequestBody {
   private final Object data;

   public JacksonRequestBody(Object data) {
      this.data = data;
   }

   @Nullable
   public MediaType contentType() {
      return Requester.MEDIA_TYPE_JSON;
   }

   public void writeTo(@Nonnull BufferedSink bufferedSink) throws IOException {
      ObjectWriter writer = SerializationUtil.getObjectWriter(false);
      writer.writeValue(bufferedSink.outputStream(), this.data);
   }
}
