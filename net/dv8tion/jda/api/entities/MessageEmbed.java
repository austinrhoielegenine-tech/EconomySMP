package net.dv8tion.jda.api.entities;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.AttachmentProxy;
import net.dv8tion.jda.api.utils.FileProxy;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public class MessageEmbed implements SerializableData {
   public static final int TITLE_MAX_LENGTH = 256;
   public static final int AUTHOR_MAX_LENGTH = 256;
   public static final int VALUE_MAX_LENGTH = 1024;
   public static final int DESCRIPTION_MAX_LENGTH = 4096;
   public static final int TEXT_MAX_LENGTH = 2048;
   public static final int URL_MAX_LENGTH = 2000;
   public static final int EMBED_MAX_LENGTH_BOT = 6000;
   public static final int MAX_FIELD_AMOUNT = 25;
   protected final Object mutex = new Object();
   protected final String url;
   protected final String title;
   protected final String description;
   protected final EmbedType type;
   protected final OffsetDateTime timestamp;
   protected final int color;
   protected final Thumbnail thumbnail;
   protected final Provider siteProvider;
   protected final AuthorInfo author;
   protected final VideoInfo videoInfo;
   protected final Footer footer;
   protected final ImageInfo image;
   protected final List<Field> fields;
   protected volatile int length = -1;
   protected volatile DataObject json = null;

   public MessageEmbed(String url, String title, String description, EmbedType type, OffsetDateTime timestamp, int color, Thumbnail thumbnail, Provider siteProvider, AuthorInfo author, VideoInfo videoInfo, Footer footer, ImageInfo image, List<Field> fields) {
      this.url = url;
      this.title = title;
      this.description = description;
      this.type = type;
      this.timestamp = timestamp;
      this.color = color;
      this.thumbnail = thumbnail;
      this.siteProvider = siteProvider;
      this.author = author;
      this.videoInfo = videoInfo;
      this.footer = footer;
      this.image = image;
      this.fields = fields != null && !fields.isEmpty() ? Collections.unmodifiableList(fields) : Collections.emptyList();
   }

   @Nullable
   public String getUrl() {
      return this.url;
   }

   @Nullable
   public String getTitle() {
      return this.title;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public EmbedType getType() {
      return this.type;
   }

   @Nullable
   public Thumbnail getThumbnail() {
      return this.thumbnail;
   }

   @Nullable
   public Provider getSiteProvider() {
      return this.siteProvider;
   }

   @Nullable
   public AuthorInfo getAuthor() {
      return this.author;
   }

   @Nullable
   public VideoInfo getVideoInfo() {
      return this.videoInfo;
   }

   @Nullable
   public Footer getFooter() {
      return this.footer;
   }

   @Nullable
   public ImageInfo getImage() {
      return this.image;
   }

   @Nonnull
   public @Unmodifiable List<Field> getFields() {
      return this.fields;
   }

   @Nullable
   public Color getColor() {
      return this.color != 536870911 ? new Color(this.color) : null;
   }

   public int getColorRaw() {
      return this.color;
   }

   @Nullable
   public OffsetDateTime getTimestamp() {
      return this.timestamp;
   }

   public boolean isEmpty() {
      return this.color == 536870911 && this.timestamp == null && this.getImage() == null && this.getThumbnail() == null && this.getLength() == 0;
   }

   public int getLength() {
      if (this.length > -1) {
         return this.length;
      } else {
         synchronized(this.mutex) {
            if (this.length > -1) {
               return this.length;
            } else {
               this.length = 0;
               if (this.title != null) {
                  this.length += Helpers.codePointLength(this.title);
               }

               if (this.description != null) {
                  this.length += Helpers.codePointLength(this.description.trim());
               }

               if (this.author != null) {
                  this.length += Helpers.codePointLength(this.author.getName());
               }

               if (this.footer != null) {
                  this.length += Helpers.codePointLength(this.footer.getText());
               }

               if (this.fields != null) {
                  for(Field f : this.fields) {
                     this.length += Helpers.codePointLength(f.getName()) + Helpers.codePointLength(f.getValue());
                  }
               }

               return this.length;
            }
         }
      }
   }

   public boolean isSendable() {
      if (this.isEmpty()) {
         return false;
      } else {
         int length = this.getLength();
         return length <= 6000;
      }
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof MessageEmbed)) {
         return false;
      } else if (obj == this) {
         return true;
      } else {
         MessageEmbed other = (MessageEmbed)obj;
         return Objects.equals(this.url, other.url) && Objects.equals(this.title, other.title) && Objects.equals(this.description, other.description) && Objects.equals(this.type, other.type) && Objects.equals(this.thumbnail, other.thumbnail) && Objects.equals(this.siteProvider, other.siteProvider) && Objects.equals(this.author, other.author) && Objects.equals(this.videoInfo, other.videoInfo) && Objects.equals(this.footer, other.footer) && Objects.equals(this.image, other.image) && (this.color & 16777215) == (other.color & 16777215) && Objects.equals(this.timestamp, other.timestamp) && Helpers.deepEquals(this.fields, other.fields);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.url, this.title, this.description, this.type, this.thumbnail, this.siteProvider, this.author, this.videoInfo, this.footer, this.image, this.color & 16777215, this.timestamp, this.fields});
   }

   @Nonnull
   public DataObject toData() {
      if (this.json != null) {
         return this.json;
      } else {
         synchronized(this.mutex) {
            if (this.json != null) {
               return this.json;
            } else {
               DataObject obj = DataObject.empty();
               if (this.url != null) {
                  obj.put("url", this.url);
               }

               if (this.title != null) {
                  obj.put("title", this.title);
               }

               if (this.description != null) {
                  obj.put("description", this.description);
               }

               if (this.timestamp != null) {
                  obj.put("timestamp", this.timestamp.format(DateTimeFormatter.ISO_INSTANT));
               }

               if (this.color != 536870911) {
                  obj.put("color", this.color & 16777215);
               }

               if (this.thumbnail != null) {
                  obj.put("thumbnail", DataObject.empty().put("url", this.thumbnail.getUrl()));
               }

               if (this.siteProvider != null) {
                  DataObject siteProviderObj = DataObject.empty();
                  if (this.siteProvider.getName() != null) {
                     siteProviderObj.put("name", this.siteProvider.getName());
                  }

                  if (this.siteProvider.getUrl() != null) {
                     siteProviderObj.put("url", this.siteProvider.getUrl());
                  }

                  obj.put("provider", siteProviderObj);
               }

               if (this.author != null) {
                  DataObject authorObj = DataObject.empty();
                  if (this.author.getName() != null) {
                     authorObj.put("name", this.author.getName());
                  }

                  if (this.author.getUrl() != null) {
                     authorObj.put("url", this.author.getUrl());
                  }

                  if (this.author.getIconUrl() != null) {
                     authorObj.put("icon_url", this.author.getIconUrl());
                  }

                  obj.put("author", authorObj);
               }

               if (this.videoInfo != null) {
                  obj.put("video", DataObject.empty().put("url", this.videoInfo.getUrl()));
               }

               if (this.footer != null) {
                  DataObject footerObj = DataObject.empty();
                  if (this.footer.getText() != null) {
                     footerObj.put("text", this.footer.getText());
                  }

                  if (this.footer.getIconUrl() != null) {
                     footerObj.put("icon_url", this.footer.getIconUrl());
                  }

                  obj.put("footer", footerObj);
               }

               if (this.image != null) {
                  obj.put("image", DataObject.empty().put("url", this.image.getUrl()));
               }

               if (!this.fields.isEmpty()) {
                  DataArray fieldsArray = DataArray.empty();

                  for(Field field : this.fields) {
                     fieldsArray.add(DataObject.empty().put("name", field.getName()).put("value", field.getValue()).put("inline", field.isInline()));
                  }

                  obj.put("fields", fieldsArray);
               }

               return this.json = obj;
            }
         }
      }
   }

   public static class Thumbnail {
      protected final String url;
      protected final String proxyUrl;
      protected final int width;
      protected final int height;

      public Thumbnail(String url, String proxyUrl, int width, int height) {
         this.url = url;
         this.proxyUrl = proxyUrl;
         this.width = width;
         this.height = height;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      @Nullable
      public String getProxyUrl() {
         return this.proxyUrl;
      }

      @Nullable
      public AttachmentProxy getProxy() {
         String proxyUrl = this.getProxyUrl();
         return proxyUrl == null ? null : new AttachmentProxy(proxyUrl);
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Thumbnail)) {
            return false;
         } else {
            Thumbnail thumbnail = (Thumbnail)obj;
            return thumbnail == this || Objects.equals(thumbnail.url, this.url) && Objects.equals(thumbnail.proxyUrl, this.proxyUrl) && thumbnail.width == this.width && thumbnail.height == this.height;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.url, this.proxyUrl, this.width, this.height});
      }
   }

   public static class Provider {
      protected final String name;
      protected final String url;

      public Provider(String name, String url) {
         this.name = name;
         this.url = url;
      }

      @Nullable
      public String getName() {
         return this.name;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Provider)) {
            return false;
         } else {
            Provider provider = (Provider)obj;
            return provider == this || Objects.equals(provider.name, this.name) && Objects.equals(provider.url, this.url);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.url});
      }
   }

   public static class VideoInfo {
      protected final String url;
      protected final String proxyUrl;
      protected final int width;
      protected final int height;

      public VideoInfo(String url, String proxyUrl, int width, int height) {
         this.url = url;
         this.proxyUrl = proxyUrl;
         this.width = width;
         this.height = height;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      @Nullable
      public String getProxyUrl() {
         return this.proxyUrl;
      }

      @Nullable
      public FileProxy getProxy() {
         String proxyUrl = this.getProxyUrl();
         return proxyUrl == null ? null : new FileProxy(proxyUrl);
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof VideoInfo)) {
            return false;
         } else {
            VideoInfo video = (VideoInfo)obj;
            return video == this || Objects.equals(video.url, this.url) && video.width == this.width && video.height == this.height;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.url, this.width, this.height});
      }
   }

   public static class ImageInfo {
      protected final String url;
      protected final String proxyUrl;
      protected final int width;
      protected final int height;

      public ImageInfo(String url, String proxyUrl, int width, int height) {
         this.url = url;
         this.proxyUrl = proxyUrl;
         this.width = width;
         this.height = height;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      @Nullable
      public String getProxyUrl() {
         return this.proxyUrl;
      }

      @Nullable
      public AttachmentProxy getProxy() {
         String proxyUrl = this.getProxyUrl();
         return proxyUrl == null ? null : new AttachmentProxy(proxyUrl);
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof ImageInfo)) {
            return false;
         } else {
            ImageInfo image = (ImageInfo)obj;
            return image == this || Objects.equals(image.url, this.url) && Objects.equals(image.proxyUrl, this.proxyUrl) && image.width == this.width && image.height == this.height;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.url, this.proxyUrl, this.width, this.height});
      }
   }

   public static class AuthorInfo {
      protected final String name;
      protected final String url;
      protected final String iconUrl;
      protected final String proxyIconUrl;

      public AuthorInfo(String name, String url, String iconUrl, String proxyIconUrl) {
         this.name = name;
         this.url = url;
         this.iconUrl = iconUrl;
         this.proxyIconUrl = proxyIconUrl;
      }

      @Nullable
      public String getName() {
         return this.name;
      }

      @Nullable
      public String getUrl() {
         return this.url;
      }

      @Nullable
      public String getIconUrl() {
         return this.iconUrl;
      }

      @Nullable
      public String getProxyIconUrl() {
         return this.proxyIconUrl;
      }

      @Nullable
      public ImageProxy getProxyIcon() {
         return this.proxyIconUrl == null ? null : new ImageProxy(this.proxyIconUrl);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof AuthorInfo)) {
            return false;
         } else {
            AuthorInfo author = (AuthorInfo)obj;
            return author == this || Objects.equals(author.name, this.name) && Objects.equals(author.url, this.url) && Objects.equals(author.iconUrl, this.iconUrl) && Objects.equals(author.proxyIconUrl, this.proxyIconUrl);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.url, this.iconUrl, this.proxyIconUrl});
      }
   }

   public static class Footer {
      protected final String text;
      protected final String iconUrl;
      protected final String proxyIconUrl;

      public Footer(String text, String iconUrl, String proxyIconUrl) {
         this.text = text;
         this.iconUrl = iconUrl;
         this.proxyIconUrl = proxyIconUrl;
      }

      @Nullable
      public String getText() {
         return this.text;
      }

      @Nullable
      public String getIconUrl() {
         return this.iconUrl;
      }

      @Nullable
      public String getProxyIconUrl() {
         return this.proxyIconUrl;
      }

      @Nullable
      public ImageProxy getProxyIcon() {
         return this.proxyIconUrl == null ? null : new ImageProxy(this.proxyIconUrl);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Footer)) {
            return false;
         } else {
            Footer footer = (Footer)obj;
            return footer == this || Objects.equals(footer.text, this.text) && Objects.equals(footer.iconUrl, this.iconUrl) && Objects.equals(footer.proxyIconUrl, this.proxyIconUrl);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.text, this.iconUrl, this.proxyIconUrl});
      }
   }

   public static class Field {
      protected final String name;
      protected final String value;
      protected final boolean inline;

      public Field(String name, String value, boolean inline, boolean checked) {
         if (checked) {
            if (name == null || value == null) {
               throw new IllegalArgumentException("Both Name and Value must be set!");
            }

            if (name.length() > 256) {
               throw new IllegalArgumentException("Name cannot be longer than 256 characters.");
            }

            if (value.length() > 1024) {
               throw new IllegalArgumentException("Value cannot be longer than 1024 characters.");
            }

            name = name.trim();
            value = value.trim();
            if (name.isEmpty()) {
               this.name = "\u200e";
            } else {
               this.name = name;
            }

            if (value.isEmpty()) {
               this.value = "\u200e";
            } else {
               this.value = value;
            }
         } else {
            this.name = name;
            this.value = value;
         }

         this.inline = inline;
      }

      public Field(String name, String value, boolean inline) {
         this(name, value, inline, true);
      }

      @Nullable
      public String getName() {
         return this.name;
      }

      @Nullable
      public String getValue() {
         return this.value;
      }

      public boolean isInline() {
         return this.inline;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Field)) {
            return false;
         } else {
            Field field = (Field)obj;
            return field == this || field.inline == this.inline && Objects.equals(field.name, this.name) && Objects.equals(field.value, this.value);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.value, this.inline});
      }
   }
}
