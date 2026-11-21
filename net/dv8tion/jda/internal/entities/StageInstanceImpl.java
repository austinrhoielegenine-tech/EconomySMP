package net.dv8tion.jda.internal.entities;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.StageInstanceManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.managers.StageInstanceManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;

public class StageInstanceImpl implements StageInstance {
   private final long id;
   private StageChannel channel;
   private String topic;
   private StageInstance.PrivacyLevel privacyLevel;

   public StageInstanceImpl(long id, StageChannel channel) {
      this.id = id;
      this.channel = channel;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public Guild getGuild() {
      return this.getChannel().getGuild();
   }

   @Nonnull
   public StageChannel getChannel() {
      StageChannel real = this.channel.getJDA().getStageChannelById(this.channel.getIdLong());
      if (real != null) {
         this.channel = real;
      }

      return this.channel;
   }

   @Nonnull
   public String getTopic() {
      return this.topic;
   }

   @Nonnull
   public StageInstance.PrivacyLevel getPrivacyLevel() {
      return this.privacyLevel;
   }

   @Nonnull
   public RestAction<Void> delete() {
      this.checkPermissions();
      Route.CompiledRoute route = Route.StageInstances.DELETE_INSTANCE.compile(this.channel.getId());
      return new RestActionImpl<Void>(this.channel.getJDA(), route);
   }

   @Nonnull
   public StageInstanceManager getManager() {
      this.checkPermissions();
      return new StageInstanceManagerImpl(this);
   }

   public StageInstanceImpl setTopic(String topic) {
      this.topic = topic;
      return this;
   }

   public StageInstanceImpl setPrivacyLevel(StageInstance.PrivacyLevel privacyLevel) {
      this.privacyLevel = privacyLevel;
      return this;
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("channel", this.getChannel()).toString();
   }

   private void checkPermissions() {
      EnumSet<Permission> permissions = this.getGuild().getSelfMember().getPermissions(this.getChannel());

      for(Permission perm : EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS)) {
         if (!permissions.contains(perm)) {
            throw new InsufficientPermissionException(this.getChannel(), perm, "You must be a stage moderator to manage a stage instance! Missing Permission: " + perm);
         }
      }

   }
}
