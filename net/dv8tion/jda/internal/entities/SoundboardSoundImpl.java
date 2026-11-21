package net.dv8tion.jda.internal.entities;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.attribute.ISoundboardSoundChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.SoundboardSoundManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.managers.SoundboardSoundManagerImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.PermissionUtil;

public class SoundboardSoundImpl extends SoundboardSoundSnowflakeImpl implements SoundboardSound {
   private final JDA api;
   private final String name;
   private final double volume;
   private final EmojiUnion emoji;
   private final Guild guild;
   private final boolean available;
   private final User user;

   public SoundboardSoundImpl(JDA api, long id, String name, double volume, EmojiUnion emoji, Guild guild, boolean available, User user) {
      super(id);
      this.api = api;
      this.name = name;
      this.volume = volume;
      this.emoji = emoji;
      this.guild = guild;
      this.available = available;
      this.user = user;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public double getVolume() {
      return this.volume;
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return this.emoji;
   }

   @Nullable
   public Guild getGuild() {
      return this.guild;
   }

   public boolean isAvailable() {
      return this.available;
   }

   @Nullable
   public User getUser() {
      return this.user;
   }

   @Nonnull
   public RestAction<Void> sendTo(@Nonnull ISoundboardSoundChannel channel) {
      Checks.notNull(channel, "Channel");
      if (!this.isAvailable()) {
         throw new IllegalStateException("Cannot send an unavailable sound");
      } else if (this.guild != null) {
         Guild targetGuild = channel.getGuild();
         if (!targetGuild.equals(this.guild) && !targetGuild.getSelfMember().hasPermission(channel, new Permission[]{Permission.VOICE_USE_EXTERNAL_SOUNDS})) {
            throw new InsufficientPermissionException(channel, Permission.VOICE_USE_EXTERNAL_SOUNDS);
         } else {
            return channel.sendSoundboardSound(this, this.guild.getIdLong());
         }
      } else {
         return channel.sendSoundboardSound(this, (String)null);
      }
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      Checks.check(this.getGuild() != null, "Cannot delete default soundboard sounds");
      this.checkEditPermissions();
      return this.guild.deleteSoundboardSound(this);
   }

   @Nonnull
   public SoundboardSoundManager getManager() {
      Checks.check(this.getGuild() != null, "Cannot edit default soundboard sounds");
      this.checkEditPermissions();
      return new SoundboardSoundManagerImpl(this.guild, this);
   }

   private void checkEditPermissions() {
      Member selfMember = this.guild.getSelfMember();
      if (Objects.equals(this.getUser(), selfMember.getUser())) {
         PermissionUtil.requireAnyPermission(selfMember, Permission.CREATE_GUILD_EXPRESSIONS, Permission.MANAGE_GUILD_EXPRESSIONS);
      } else if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS})) {
         throw new InsufficientPermissionException(this.guild, Permission.MANAGE_GUILD_EXPRESSIONS);
      }

   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }
}
