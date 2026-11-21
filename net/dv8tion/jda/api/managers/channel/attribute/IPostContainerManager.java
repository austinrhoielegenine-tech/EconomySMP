package net.dv8tion.jda.api.managers.channel.attribute;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public interface IPostContainerManager<T extends IPostContainer, M extends IPostContainerManager<T, M>> extends IThreadContainerManager<T, M>, IPermissionContainerManager<T, M> {
   @Nonnull
   @CheckReturnValue
   M setTagRequired(boolean var1);

   @Nonnull
   @CheckReturnValue
   M setAvailableTags(@Nonnull List<? extends BaseForumTag> var1);

   @Nonnull
   @CheckReturnValue
   M setDefaultReaction(@Nullable Emoji var1);

   @Nonnull
   @CheckReturnValue
   M setDefaultSortOrder(@Nonnull IPostContainer.SortOrder var1);

   @Nonnull
   @CheckReturnValue
   M setTopic(@Nullable String var1);
}
