package net.dv8tion.jda.internal.components;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ResolvedMedia;
import net.dv8tion.jda.api.utils.AttachmentProxy;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.EntityString;

public class ResolvedMediaImpl implements ResolvedMedia {
   private final String attachmentId;
   private final String url;
   private final String proxyUrl;
   private final int width;
   private final int height;
   private final String contentType;

   public ResolvedMediaImpl(DataObject data) {
      this(data.getString("attachment_id", (String)null), data.getString("url"), data.getString("proxy_url"), data.getInt("width", 0), data.getInt("height", 0), data.getString("content_type", (String)null));
   }

   public ResolvedMediaImpl(String attachmentId, String url, String proxyUrl, int width, int height, String contentType) {
      this.attachmentId = attachmentId;
      this.url = url;
      this.proxyUrl = proxyUrl;
      this.width = width;
      this.height = height;
      this.contentType = contentType;
   }

   @Nullable
   public String getAttachmentId() {
      return this.attachmentId;
   }

   @Nonnull
   public String getUrl() {
      return this.url;
   }

   @Nonnull
   public String getProxyUrl() {
      return this.proxyUrl;
   }

   @Nonnull
   public AttachmentProxy getProxy() {
      return new AttachmentProxy(this.width > 0 && this.height > 0 ? this.proxyUrl : this.url);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   @Nullable
   public String getContentType() {
      return this.contentType;
   }

   public boolean equals(Object o) {
      if (!(o instanceof ResolvedMediaImpl)) {
         return false;
      } else {
         ResolvedMediaImpl that = (ResolvedMediaImpl)o;
         return Objects.equals(this.url, that.url);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.url});
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("proxy_url", this.url).toString();
   }
}
