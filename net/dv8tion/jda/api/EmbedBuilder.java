package net.dv8tion.jda.api;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class EmbedBuilder {
   public static final String ZERO_WIDTH_SPACE = "\u200e";
   public static final Pattern URL_PATTERN = Pattern.compile("\\s*(https?|attachment)://\\S+\\s*", 2);
   private final List<MessageEmbed.Field> fields = new ArrayList();
   private final StringBuilder description = new StringBuilder();
   private int color = 536870911;
   private String url;
   private String title;
   private OffsetDateTime timestamp;
   private MessageEmbed.Thumbnail thumbnail;
   private MessageEmbed.AuthorInfo author;
   private MessageEmbed.Footer footer;
   private MessageEmbed.ImageInfo image;

   public EmbedBuilder() {
   }

   public EmbedBuilder(@Nullable EmbedBuilder builder) {
      this.copyFrom(builder);
   }

   public EmbedBuilder(@Nullable MessageEmbed embed) {
      this.copyFrom(embed);
   }

   @Nonnull
   public static EmbedBuilder fromData(@Nonnull DataObject data) {
      Checks.notNull(data, "DataObject");
      EmbedBuilder builder = new EmbedBuilder();
      builder.setTitle(data.getString("title", (String)null));
      builder.setUrl(data.getString("url", (String)null));
      builder.setDescription(data.getString("description", ""));
      builder.setTimestamp(data.isNull("timestamp") ? null : OffsetDateTime.parse(data.getString("timestamp")));
      builder.setColor(data.getInt("color", 536870911));
      data.optObject("thumbnail").ifPresent((thumbnail) -> builder.setThumbnail(thumbnail.getString("url")));
      data.optObject("author").ifPresent((author) -> builder.setAuthor(author.getString("name", ""), author.getString("url", (String)null), author.getString("icon_url", (String)null)));
      data.optObject("footer").ifPresent((footer) -> builder.setFooter(footer.getString("text", ""), footer.getString("icon_url", (String)null)));
      data.optObject("image").ifPresent((image) -> builder.setImage(image.getString("url")));
      data.optArray("fields").ifPresent((arr) -> arr.stream(DataArray::getObject).forEach((field) -> builder.addField(field.getString("name", "\u200e"), field.getString("value", "\u200e"), field.getBoolean("inline", false))));
      return builder;
   }

   @Nonnull
   public MessageEmbed build() {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot build an empty embed!");
      } else if (this.description.length() > 4096) {
         throw new IllegalStateException(Helpers.format("Description is longer than %d! Please limit your input!", 4096));
      } else if (this.length() > 6000) {
         throw new IllegalStateException(Helpers.format("Cannot build an embed with more than %d characters!", 6000));
      } else if (this.fields.size() > 25) {
         throw new IllegalStateException(Helpers.format("Cannot build an embed with more than %d embed fields set!", 25));
      } else {
         String description = this.description.length() < 1 ? null : this.description.toString();
         return EntityBuilder.createMessageEmbed(this.url, this.title, description, EmbedType.RICH, this.timestamp, this.color, this.thumbnail, (MessageEmbed.Provider)null, this.author, (MessageEmbed.VideoInfo)null, this.footer, this.image, new ArrayList(this.fields));
      }
   }

   @Nonnull
   public EmbedBuilder clear() {
      this.description.setLength(0);
      this.fields.clear();
      this.url = null;
      this.title = null;
      this.timestamp = null;
      this.color = 536870911;
      this.thumbnail = null;
      this.author = null;
      this.footer = null;
      this.image = null;
      return this;
   }

   public void copyFrom(@Nullable EmbedBuilder builder) {
      if (builder != null) {
         this.setDescription(builder.description.toString());
         this.clearFields();
         this.fields.addAll(builder.fields);
         this.url = builder.url;
         this.title = builder.title;
         this.timestamp = builder.timestamp;
         this.color = builder.color;
         this.thumbnail = builder.thumbnail;
         this.author = builder.author;
         this.footer = builder.footer;
         this.image = builder.image;
      }

   }

   public void copyFrom(@Nullable MessageEmbed embed) {
      if (embed != null) {
         this.setDescription(embed.getDescription());
         this.clearFields();
         this.fields.addAll(embed.getFields());
         this.url = embed.getUrl();
         this.title = embed.getTitle();
         this.timestamp = embed.getTimestamp();
         this.color = embed.getColorRaw();
         this.thumbnail = embed.getThumbnail();
         this.author = embed.getAuthor();
         this.footer = embed.getFooter();
         this.image = embed.getImage();
      }

   }

   public boolean isEmpty() {
      return (this.title == null || this.title.trim().isEmpty()) && this.thumbnail == null && this.author == null && this.footer == null && this.image == null && this.description.toString().trim().isEmpty() && this.fields.isEmpty();
   }

   public int length() {
      int length = this.description.toString().trim().length();
      synchronized(this.fields) {
         length = (Integer)this.fields.stream().map((f) -> f.getName().length() + f.getValue().length()).reduce(length, Integer::sum);
      }

      if (this.title != null) {
         length += this.title.length();
      }

      if (this.author != null) {
         length += this.author.getName().length();
      }

      if (this.footer != null) {
         length += this.footer.getText().length();
      }

      return length;
   }

   public boolean isValidLength() {
      int length = this.length();
      return length <= 6000;
   }

   @Nonnull
   public EmbedBuilder setTitle(@Nullable String title) {
      return this.setTitle(title, (String)null);
   }

   @Nonnull
   public EmbedBuilder setTitle(@Nullable String title, @Nullable String url) {
      if (title == null) {
         this.title = null;
         this.url = null;
      } else {
         Checks.notEmpty(title, "Title");
         Checks.check(title.length() <= 256, "Title cannot be longer than %d characters.", (int)256);
         if (Helpers.isBlank(url)) {
            url = null;
         }

         this.urlCheck(url);
         this.title = title;
         this.url = url;
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder setUrl(@Nullable String url) {
      if (Helpers.isBlank(url)) {
         url = null;
      }

      this.urlCheck(url);
      this.url = url;
      return this;
   }

   @Nonnull
   public StringBuilder getDescriptionBuilder() {
      return this.description;
   }

   @Nonnull
   public final EmbedBuilder setDescription(@Nullable CharSequence description) {
      this.description.setLength(0);
      if (description != null && description.length() >= 1) {
         this.appendDescription(description);
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder appendDescription(@Nonnull CharSequence description) {
      Checks.notNull(description, "description");
      Checks.check(this.description.length() + description.length() <= 4096, "Description cannot be longer than %d characters.", (int)4096);
      this.description.append(description);
      return this;
   }

   @Nonnull
   public EmbedBuilder setTimestamp(@Nullable TemporalAccessor temporal) {
      this.timestamp = Helpers.toOffsetDateTime(temporal);
      return this;
   }

   @Nonnull
   public EmbedBuilder setColor(@Nullable Color color) {
      this.color = color == null ? 536870911 : color.getRGB();
      return this;
   }

   @Nonnull
   public EmbedBuilder setColor(int color) {
      this.color = color;
      return this;
   }

   @Nonnull
   public EmbedBuilder setThumbnail(@Nullable String url) {
      if (url == null) {
         this.thumbnail = null;
      } else {
         this.urlCheck(url);
         this.thumbnail = new MessageEmbed.Thumbnail(url, (String)null, 0, 0);
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder setImage(@Nullable String url) {
      if (url == null) {
         this.image = null;
      } else {
         this.urlCheck(url);
         this.image = new MessageEmbed.ImageInfo(url, (String)null, 0, 0);
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder setAuthor(@Nullable String name) {
      return this.setAuthor(name, (String)null, (String)null);
   }

   @Nonnull
   public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url) {
      return this.setAuthor(name, url, (String)null);
   }

   @Nonnull
   public EmbedBuilder setAuthor(@Nullable String name, @Nullable String url, @Nullable String iconUrl) {
      if (name == null) {
         this.author = null;
      } else {
         Checks.notLonger(name, 256, "Name");
         this.urlCheck(url);
         this.urlCheck(iconUrl);
         this.author = new MessageEmbed.AuthorInfo(name, url, iconUrl, (String)null);
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder setFooter(@Nullable String text) {
      return this.setFooter(text, (String)null);
   }

   @Nonnull
   public EmbedBuilder setFooter(@Nullable String text, @Nullable String iconUrl) {
      if (text == null) {
         this.footer = null;
      } else {
         Checks.notLonger(text, 2048, "Text");
         this.urlCheck(iconUrl);
         this.footer = new MessageEmbed.Footer(text, iconUrl, (String)null);
      }

      return this;
   }

   @Nonnull
   public EmbedBuilder addField(@Nullable MessageEmbed.Field field) {
      return field == null ? this : this.addField(field.getName(), field.getValue(), field.isInline());
   }

   @Nonnull
   public EmbedBuilder addField(@Nonnull String name, @Nonnull String value, boolean inline) {
      Checks.notNull(name, "Name");
      Checks.notNull(value, "Value");
      this.fields.add(new MessageEmbed.Field(name, value, inline));
      return this;
   }

   @Nonnull
   public EmbedBuilder addBlankField(boolean inline) {
      this.fields.add(new MessageEmbed.Field("\u200e", "\u200e", inline));
      return this;
   }

   @Nonnull
   public EmbedBuilder clearFields() {
      this.fields.clear();
      return this;
   }

   @Nonnull
   public List<MessageEmbed.Field> getFields() {
      return this.fields;
   }

   private void urlCheck(@Nullable String url) {
      if (url != null) {
         Checks.notLonger(url, 2000, "URL");
         Checks.check(URL_PATTERN.matcher(url).matches(), "URL must be a valid http(s) or attachment url.");
      }

   }
}
