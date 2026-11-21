package net.dv8tion.jda.api.interactions.commands;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.EntityString;

public class SlashCommandReference implements ICommandReference {
   private final long id;
   private final String name;
   private final String subcommand;
   private final String subcommandGroup;

   public SlashCommandReference(@Nonnull String name, @Nullable String subcommandGroup, @Nullable String subcommand, long id) {
      this.name = name;
      this.subcommandGroup = subcommandGroup;
      this.subcommand = subcommand;
      this.id = id;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getSubcommandName() {
      return this.subcommand;
   }

   @Nullable
   public String getSubcommandGroup() {
      return this.subcommandGroup;
   }

   @Nonnull
   public String getFullCommandName() {
      StringJoiner joiner = new StringJoiner(" ");
      joiner.add(this.name);
      if (this.subcommandGroup != null) {
         joiner.add(this.subcommandGroup);
      }

      if (this.subcommand != null) {
         joiner.add(this.subcommand);
      }

      return joiner.toString();
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getFullCommandName()).toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SlashCommandReference)) {
         return false;
      } else {
         SlashCommandReference that = (SlashCommandReference)o;
         return this.id == that.id && this.name.equals(that.name) && Objects.equals(this.subcommand, that.subcommand) && Objects.equals(this.subcommandGroup, that.subcommandGroup);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.name, this.subcommand, this.subcommandGroup});
   }
}
