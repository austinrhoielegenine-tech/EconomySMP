package net.dv8tion.jda.api.interactions.commands;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.UnknownNullability;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Contract;

public interface CommandInteractionPayload extends Interaction {
   @Nonnull
   Command.Type getCommandType();

   @Nonnull
   String getName();

   @Nullable
   String getSubcommandName();

   @Nullable
   String getSubcommandGroup();

   @Nonnull
   default String getFullCommandName() {
      StringBuilder builder = new StringBuilder(this.getName());
      if (this.getSubcommandGroup() != null) {
         builder.append(' ').append(this.getSubcommandGroup());
      }

      if (this.getSubcommandName() != null) {
         builder.append(' ').append(this.getSubcommandName());
      }

      return builder.toString();
   }

   @Nonnull
   default String getCommandString() {
      if (this.getCommandType() != Command.Type.SLASH) {
         return this.getName();
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append("/").append(this.getName());
         if (this.getSubcommandGroup() != null) {
            builder.append(" ").append(this.getSubcommandGroup());
         }

         if (this.getSubcommandName() != null) {
            builder.append(" ").append(this.getSubcommandName());
         }

         for(OptionMapping o : this.getOptions()) {
            builder.append(" ").append(o.getName()).append(": ");
            if (this instanceof CommandAutoCompleteInteraction) {
               switch (o.getType()) {
                  case CHANNEL:
                  case USER:
                  case ROLE:
                  case MENTIONABLE:
                     builder.append(o.getAsLong());
                     continue;
               }
            }

            switch (o.getType()) {
               case CHANNEL:
                  builder.append("#").append(o.getAsChannel().getName());
                  break;
               case USER:
                  builder.append("@").append(o.getAsUser().getName());
                  break;
               case ROLE:
                  builder.append("@").append(o.getAsRole().getName());
                  break;
               case MENTIONABLE:
                  if (o.getAsMentionable() instanceof Role) {
                     builder.append("@").append(o.getAsRole().getName());
                  } else if (o.getAsMentionable() instanceof Member) {
                     builder.append("@").append(o.getAsUser().getName());
                  } else if (o.getAsMentionable() instanceof User) {
                     builder.append("@").append(o.getAsUser().getName());
                  } else {
                     builder.append("@").append(o.getAsMentionable().getIdLong());
                  }
                  break;
               default:
                  builder.append(o.getAsString());
            }
         }

         return builder.toString();
      }
   }

   long getCommandIdLong();

   @Nonnull
   default String getCommandId() {
      return Long.toUnsignedString(this.getCommandIdLong());
   }

   boolean isGuildCommand();

   default boolean isGlobalCommand() {
      return !this.isGuildCommand();
   }

   @Nonnull
   List<OptionMapping> getOptions();

   @Nonnull
   default List<OptionMapping> getOptionsByName(@Nonnull String name) {
      Checks.notNull(name, "Name");
      return (List)this.getOptions().stream().filter((opt) -> opt.getName().equals(name)).collect(Collectors.toList());
   }

   @Nonnull
   default List<OptionMapping> getOptionsByType(@Nonnull OptionType type) {
      Checks.notNull(type, "Type");
      return (List)this.getOptions().stream().filter((it) -> it.getType() == type).collect(Collectors.toList());
   }

   @Nullable
   default OptionMapping getOption(@Nonnull String name) {
      List<OptionMapping> options = this.getOptionsByName(name);
      return options.isEmpty() ? null : (OptionMapping)options.get(0);
   }

   @Nullable
   default <T> T getOption(@Nonnull String name, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
      return (T)this.getOption(name, (Supplier)null, resolver);
   }

   @Contract("_,null,_->null")
   default <T> T getOption(@Nonnull String name, @Nullable T fallback, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
      Checks.notNull(resolver, "Resolver");
      OptionMapping mapping = this.getOption(name);
      return (T)(mapping != null ? resolver.apply(mapping) : fallback);
   }

   @UnknownNullability
   default <T> T getOption(@Nonnull String name, @Nullable Supplier<? extends T> fallback, @Nonnull Function<? super OptionMapping, ? extends T> resolver) {
      Checks.notNull(resolver, "Resolver");
      OptionMapping mapping = this.getOption(name);
      if (mapping != null) {
         return (T)resolver.apply(mapping);
      } else {
         return (T)(fallback == null ? null : fallback.get());
      }
   }
}
