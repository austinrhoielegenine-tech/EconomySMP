package net.dv8tion.jda.api.interactions.commands;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.InteractionMentions;
import net.dv8tion.jda.internal.utils.EntityString;

public class OptionMapping {
   private final DataObject data;
   private final OptionType type;
   private final String name;
   private final TLongObjectMap<Object> resolved;
   private final Mentions mentions;

   public OptionMapping(DataObject data, TLongObjectMap<Object> resolved, JDA jda, Guild guild) {
      this.data = data;
      this.type = OptionType.fromKey(data.getInt("type", -1));
      this.name = data.getString("name");
      this.resolved = resolved;
      if (this.type == OptionType.STRING) {
         this.mentions = new InteractionMentions(this.getAsString(), resolved, (JDAImpl)jda, guild);
      } else {
         this.mentions = new InteractionMentions("", new TLongObjectHashMap(0), (JDAImpl)jda, guild);
      }

   }

   @Nonnull
   public Mentions getMentions() {
      return this.mentions;
   }

   @Nonnull
   public OptionType getType() {
      return this.type;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nonnull
   public Message.Attachment getAsAttachment() {
      Object obj = this.resolved.get(this.getAsLong());
      if (obj instanceof Message.Attachment) {
         return (Message.Attachment)obj;
      } else {
         throw new IllegalStateException("Cannot resolve option of type " + this.type + " to Attachment!");
      }
   }

   @Nonnull
   public String getAsString() {
      return this.data.getString("value");
   }

   public boolean getAsBoolean() {
      if (this.type != OptionType.BOOLEAN) {
         throw new IllegalStateException("Cannot convert option of type " + this.type + " to boolean");
      } else {
         return this.data.getBoolean("value");
      }
   }

   public long getAsLong() {
      switch (this.type) {
         case STRING:
         case MENTIONABLE:
         case CHANNEL:
         case ROLE:
         case USER:
         case INTEGER:
         case ATTACHMENT:
            return this.data.getLong("value");
         default:
            throw new IllegalStateException("Cannot convert option of type " + this.type + " to long");
      }
   }

   public int getAsInt() {
      return Math.toIntExact(this.getAsLong());
   }

   public double getAsDouble() {
      switch (this.type) {
         case STRING:
         case INTEGER:
         case NUMBER:
            return this.data.getDouble("value");
         default:
            throw new IllegalStateException("Cannot convert option of type " + this.type + " to double");
      }
   }

   @Nonnull
   public IMentionable getAsMentionable() {
      Object entity = this.resolved.get(this.getAsLong());
      if (entity instanceof IMentionable) {
         return (IMentionable)entity;
      } else {
         throw new IllegalStateException("Cannot resolve option of type " + this.type + " to IMentionable");
      }
   }

   @Nullable
   public Member getAsMember() {
      if (this.type != OptionType.USER && this.type != OptionType.MENTIONABLE) {
         throw new IllegalStateException("Cannot resolve Member for option " + this.getName() + " of type " + this.type);
      } else {
         Object object = this.resolved.get(this.getAsLong());
         return object instanceof Member ? (Member)object : null;
      }
   }

   @Nonnull
   public User getAsUser() {
      if (this.type != OptionType.USER && this.type != OptionType.MENTIONABLE) {
         throw new IllegalStateException("Cannot resolve User for option " + this.getName() + " of type " + this.type);
      } else {
         Object object = this.resolved.get(this.getAsLong());
         if (object instanceof Member) {
            return ((Member)object).getUser();
         } else if (object instanceof User) {
            return (User)object;
         } else {
            throw new IllegalStateException("Could not resolve User from option type " + this.type);
         }
      }
   }

   @Nonnull
   public Role getAsRole() {
      if (this.type != OptionType.ROLE && this.type != OptionType.MENTIONABLE) {
         throw new IllegalStateException("Cannot resolve Role for option " + this.getName() + " of type " + this.type);
      } else {
         Object role = this.resolved.get(this.getAsLong());
         if (role instanceof Role) {
            return (Role)role;
         } else {
            throw new IllegalStateException("Could not resolve Role from option type " + this.type);
         }
      }
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.getAsChannel().getType();
   }

   @Nonnull
   public GuildChannelUnion getAsChannel() {
      if (this.type != OptionType.CHANNEL) {
         throw new IllegalStateException("Cannot resolve Channel for option " + this.getName() + " of type " + this.type);
      } else {
         Object entity = this.resolved.get(this.getAsLong());
         if (entity instanceof GuildChannel) {
            return (GuildChannelUnion)entity;
         } else {
            throw new IllegalStateException("Could not resolve GuildChannel!");
         }
      }
   }

   public String toString() {
      return (new EntityString(this)).setType(this.getType()).addMetadata("name", this.name).addMetadata("value", this.getAsString()).toString();
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getType(), this.getName()});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof OptionMapping)) {
         return false;
      } else {
         OptionMapping data = (OptionMapping)obj;
         return this.getType() == data.getType() && this.getName().equals(data.getName());
      }
   }
}
