package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadMember;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;

public class ThreadMemberUpdateHandler extends SocketHandler {
   public ThreadMemberUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.api.getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         long threadId = content.getLong("id");
         ThreadChannelImpl thread = (ThreadChannelImpl)this.getJDA().getThreadChannelById(threadId);
         if (thread == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, threadId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"THREAD_MEMBER_UPDATE attempted to update a thread that does not exist. JSON: {}", (Object)content);
            return null;
         } else {
            long userId = content.getLong("user_id");
            if (userId != this.getJDA().getSelfUser().getIdLong()) {
               JDAImpl.LOG.warn((String)"Received a THREAD_MEMBER_UPDATE for a user that isn't the current bot user. This validates assumptions that THREAD_MEMBER_UPDATE would ONLY be for the current bot user. Skipping this dispatch for now. This should be reported as a bug.\nDetails: {}", (Object)content);
               return null;
            } else {
               CacheView.SimpleCacheView<ThreadMember> view = thread.getThreadMemberView();
               UnlockHook lock = view.writeLock();

               try {
                  ThreadMember threadMember = (ThreadMember)view.getMap().get(userId);
                  if (threadMember == null) {
                     threadMember = this.api.getEntityBuilder().createThreadMember((ThreadChannelImpl)thread, (Member)thread.getGuild().getSelfMember(), content);
                     view.getMap().put(threadMember.getIdLong(), threadMember);
                  }
               } catch (Throwable var14) {
                  if (lock != null) {
                     try {
                        lock.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (lock != null) {
                  lock.close();
               }

               return null;
            }
         }
      }
   }
}
