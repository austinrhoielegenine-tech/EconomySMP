package net.dv8tion.jda.internal.requests.restaction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.SoundboardSoundCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.RequestBody;

public class SoundboardSoundCreateActionImpl extends AuditableRestActionImpl<SoundboardSound> implements SoundboardSoundCreateAction {
   private final String name;
   private final FileUpload file;
   private double volume = (double)1.0F;
   private Emoji emoji;

   public SoundboardSoundCreateActionImpl(JDA api, Route.CompiledRoute route, String name, FileUpload file) {
      super(api, route);
      this.name = name;
      this.file = file;
   }

   @Nonnull
   public SoundboardSoundCreateAction timeout(long timeout, @Nonnull TimeUnit unit) {
      return (SoundboardSoundCreateAction)super.timeout(timeout, unit);
   }

   @Nonnull
   public SoundboardSoundCreateAction addCheck(@Nonnull BooleanSupplier checks) {
      return (SoundboardSoundCreateAction)super.addCheck(checks);
   }

   @Nonnull
   public SoundboardSoundCreateAction setCheck(BooleanSupplier checks) {
      return (SoundboardSoundCreateAction)super.setCheck(checks);
   }

   @Nonnull
   public SoundboardSoundCreateAction deadline(long timestamp) {
      return (SoundboardSoundCreateAction)super.deadline(timestamp);
   }

   @Nonnull
   public SoundboardSoundCreateAction setVolume(double volume) {
      Checks.check(volume >= (double)0.0F && volume <= (double)1.0F, "Volume must be between 0 and 1");
      this.volume = volume;
      return this;
   }

   @Nonnull
   public SoundboardSoundCreateAction setEmoji(@Nullable Emoji emoji) {
      this.emoji = emoji;
      return this;
   }

   protected RequestBody finalizeData() {
      try {
         DataObject json = DataObject.empty().put("name", this.name).put("sound", "data:" + this.getMime() + ";base64," + this.getBase64Sound()).put("volume", this.volume);
         if (this.emoji instanceof UnicodeEmoji) {
            json.put("emoji_name", this.emoji.getName());
         } else if (this.emoji instanceof CustomEmoji) {
            json.put("emoji_id", ((CustomEmoji)this.emoji).getId());
         }

         return this.getRequestBody(json);
      } catch (IOException e) {
         throw new UncheckedIOException("Unable to get request body when creating a guild soundboard sound", e);
      }
   }

   @Nonnull
   private String getBase64Sound() throws IOException {
      byte[] data = IOUtil.readFully(this.file.getData());
      byte[] b64 = Base64.getEncoder().encode(data);
      return new String(b64, StandardCharsets.UTF_8);
   }

   @Nonnull
   private String getMime() {
      int index = this.file.getName().lastIndexOf(46);
      Checks.check(index > -1, "Filename for soundboard sound is missing file extension. Provided: '" + this.file.getName() + "'. Must be MP3 or OGG.");
      String mime;
      switch (this.file.getName().substring(index + 1).toLowerCase(Locale.ROOT)) {
         case "mp3":
            mime = "audio/mpeg";
            break;
         case "ogg":
            mime = "audio/ogg";
            break;
         default:
            throw new IllegalArgumentException("Unsupported file extension: '." + extension + "', must be MP3 or OGG.");
      }

      return mime;
   }

   protected void handleSuccess(Response response, Request<SoundboardSound> request) {
      request.onSuccess(this.api.getEntityBuilder().createSoundboardSound(response.getObject()));
   }
}
