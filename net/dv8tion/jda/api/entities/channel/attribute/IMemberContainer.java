package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.jetbrains.annotations.Unmodifiable;

public interface IMemberContainer extends GuildChannel {
   @Nonnull
   @Unmodifiable List<Member> getMembers();
}
