package net.dv8tion.jda.api.requests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import okhttp3.HttpUrl;

public class Route {
   public static final List<String> MAJOR_PARAMETER_NAMES = Helpers.<String>listOf("guild_id", "channel_id", "webhook_id", "interaction_token");
   private final Method method;
   private final int paramCount;
   private final String[] template;
   private final boolean isInteraction;

   @Nonnull
   public static Route custom(@Nonnull Method method, @Nonnull String route) {
      Checks.notNull(method, "Method");
      Checks.notEmpty(route, "Route");
      Checks.noWhitespace(route, "Route");
      return new Route(method, route);
   }

   @Nonnull
   public static Route delete(@Nonnull String route) {
      return custom(Method.DELETE, route);
   }

   @Nonnull
   public static Route post(@Nonnull String route) {
      return custom(Method.POST, route);
   }

   @Nonnull
   public static Route put(@Nonnull String route) {
      return custom(Method.PUT, route);
   }

   @Nonnull
   public static Route patch(@Nonnull String route) {
      return custom(Method.PATCH, route);
   }

   @Nonnull
   public static Route get(@Nonnull String route) {
      return custom(Method.GET, route);
   }

   private Route(Method method, String route, boolean isInteraction) {
      this.method = method;
      this.template = Helpers.split(route, "/");
      this.isInteraction = isInteraction;
      int paramCount = 0;

      for(String element : this.template) {
         int opening = Helpers.countMatches(element, '{');
         int closing = Helpers.countMatches(element, '}');
         if (element.startsWith("{") && element.endsWith("}")) {
            Checks.check(closing == 1 && opening == 1, "Route element has invalid syntax: '%s'", (Object)element);
            ++paramCount;
         } else if (opening > 0 || closing > 0) {
            throw new IllegalArgumentException("Route element has invalid syntax: '" + element + "'");
         }
      }

      this.paramCount = paramCount;
   }

   private Route(Method method, String route) {
      this(method, route, false);
   }

   public boolean isInteractionBucket() {
      return this.isInteraction;
   }

   @Nonnull
   public Method getMethod() {
      return this.method;
   }

   @Nonnull
   public String getRoute() {
      return String.join("/", this.template);
   }

   public int getParamCount() {
      return this.paramCount;
   }

   @Nonnull
   public CompiledRoute compile(@Nonnull String... params) {
      Checks.noneNull(params, "Arguments");
      Checks.check(params.length == this.paramCount, "Error Compiling Route: [%s], incorrect amount of parameters provided. Expected: %d, Provided: %d", this, this.paramCount, params.length);
      StringJoiner major = (new StringJoiner(":")).setEmptyValue("n/a");
      List<PathSegment> path = new ArrayList();
      int paramIndex = 0;

      for(String element : this.template) {
         if (element.charAt(0) == '{') {
            String name = element.substring(1, element.length() - 1);
            String value = params[paramIndex++];
            if (MAJOR_PARAMETER_NAMES.contains(name)) {
               if (value.length() > 30) {
                  major.add(name + "=" + Integer.toUnsignedString(value.hashCode()));
               } else {
                  major.add(name + "=" + value);
               }
            }

            path.add(new PathSegment(value, false));
         } else {
            path.add(new PathSegment(element, true));
         }
      }

      return new CompiledRoute(this, path, major.toString());
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.method, Arrays.hashCode(this.template)});
   }

   public boolean equals(Object o) {
      if (!(o instanceof Route)) {
         return false;
      } else {
         Route oRoute = (Route)o;
         return this.method.equals(oRoute.method) && Arrays.equals(this.template, oRoute.template);
      }
   }

   public String toString() {
      return this.method + "/" + this.getRoute();
   }

   private static String compileRoute(List<PathSegment> path, List<QueryParameter> query) {
      String compiledRoute = (String)path.stream().map(PathSegment::toString).collect(Collectors.joining("/"));
      if (query == null) {
         return compiledRoute;
      } else {
         String queryString = (String)query.stream().map(QueryParameter::toString).collect(Collectors.joining("&", "?", ""));
         return compiledRoute + queryString;
      }
   }

   public static class Misc {
      public static final Route GET_VOICE_REGIONS;
      public static final Route GATEWAY;
      public static final Route GATEWAY_BOT;

      static {
         GET_VOICE_REGIONS = new Route(Method.GET, "voice/regions");
         GATEWAY = new Route(Method.GET, "gateway");
         GATEWAY_BOT = new Route(Method.GET, "gateway/bot");
      }
   }

   public static class Applications {
      public static final Route GET_BOT_APPLICATION;
      public static final Route EDIT_BOT_APPLICATION;
      public static final Route GET_ROLE_CONNECTION_METADATA;
      public static final Route UPDATE_ROLE_CONNECTION_METADATA;
      public static final Route GET_ENTITLEMENTS;
      public static final Route GET_ENTITLEMENT;
      public static final Route CONSUME_ENTITLEMENT;
      public static final Route CREATE_TEST_ENTITLEMENT;
      public static final Route DELETE_TEST_ENTITLEMENT;
      public static final Route GET_APPLICATION_EMOJIS;
      public static final Route GET_APPLICATION_EMOJI;
      public static final Route CREATE_APPLICATION_EMOJI;
      public static final Route MODIFY_APPLICATION_EMOJI;
      public static final Route DELETE_APPLICATION_EMOJI;
      public static final Route GET_SKUS;

      static {
         GET_BOT_APPLICATION = new Route(Method.GET, "oauth2/applications/@me");
         EDIT_BOT_APPLICATION = new Route(Method.PATCH, "applications/@me");
         GET_ROLE_CONNECTION_METADATA = new Route(Method.GET, "applications/{application_id}/role-connections/metadata");
         UPDATE_ROLE_CONNECTION_METADATA = new Route(Method.PUT, "applications/{application_id}/role-connections/metadata");
         GET_ENTITLEMENTS = new Route(Method.GET, "applications/{application_id}/entitlements");
         GET_ENTITLEMENT = new Route(Method.GET, "applications/{application_id}/entitlements/{entitlement_id}");
         CONSUME_ENTITLEMENT = new Route(Method.POST, "applications/{application_id}/entitlements/{entitlement_id}/consume");
         CREATE_TEST_ENTITLEMENT = new Route(Method.POST, "applications/{application_id}/entitlements");
         DELETE_TEST_ENTITLEMENT = new Route(Method.DELETE, "applications/{application_id}/entitlements/{entitlement_id}");
         GET_APPLICATION_EMOJIS = new Route(Method.GET, "applications/{application_id}/emojis");
         GET_APPLICATION_EMOJI = new Route(Method.GET, "applications/{application_id}/emojis/{emoji_id}");
         CREATE_APPLICATION_EMOJI = new Route(Method.POST, "applications/{application_id}/emojis");
         MODIFY_APPLICATION_EMOJI = new Route(Method.PATCH, "applications/{application_id}/emojis/{emoji_id}");
         DELETE_APPLICATION_EMOJI = new Route(Method.DELETE, "applications/{application_id}/emojis/{emoji_id}");
         GET_SKUS = new Route(Method.GET, "applications/{application_id}/skus");
      }
   }

   public static class Interactions {
      public static final Route GET_COMMANDS;
      public static final Route GET_COMMAND;
      public static final Route CREATE_COMMAND;
      public static final Route UPDATE_COMMANDS;
      public static final Route EDIT_COMMAND;
      public static final Route DELETE_COMMAND;
      public static final Route GET_GUILD_COMMANDS;
      public static final Route GET_GUILD_COMMAND;
      public static final Route CREATE_GUILD_COMMAND;
      public static final Route UPDATE_GUILD_COMMANDS;
      public static final Route EDIT_GUILD_COMMAND;
      public static final Route DELETE_GUILD_COMMAND;
      public static final Route GET_ALL_COMMAND_PERMISSIONS;
      public static final Route EDIT_ALL_COMMAND_PERMISSIONS;
      public static final Route GET_COMMAND_PERMISSIONS;
      public static final Route EDIT_COMMAND_PERMISSIONS;
      public static final Route CALLBACK;
      public static final Route CREATE_FOLLOWUP;
      public static final Route EDIT_FOLLOWUP;
      public static final Route DELETE_FOLLOWUP;
      public static final Route GET_MESSAGE;

      static {
         GET_COMMANDS = new Route(Method.GET, "applications/{application_id}/commands");
         GET_COMMAND = new Route(Method.GET, "applications/{application_id}/commands/{command_id}");
         CREATE_COMMAND = new Route(Method.POST, "applications/{application_id}/commands");
         UPDATE_COMMANDS = new Route(Method.PUT, "applications/{application_id}/commands");
         EDIT_COMMAND = new Route(Method.PATCH, "applications/{application_id}/commands/{command_id}");
         DELETE_COMMAND = new Route(Method.DELETE, "applications/{application_id}/commands/{command_id}");
         GET_GUILD_COMMANDS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands");
         GET_GUILD_COMMAND = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}");
         CREATE_GUILD_COMMAND = new Route(Method.POST, "applications/{application_id}/guilds/{guild_id}/commands");
         UPDATE_GUILD_COMMANDS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands");
         EDIT_GUILD_COMMAND = new Route(Method.PATCH, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}");
         DELETE_GUILD_COMMAND = new Route(Method.DELETE, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}");
         GET_ALL_COMMAND_PERMISSIONS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/permissions");
         EDIT_ALL_COMMAND_PERMISSIONS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands/permissions");
         GET_COMMAND_PERMISSIONS = new Route(Method.GET, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}/permissions");
         EDIT_COMMAND_PERMISSIONS = new Route(Method.PUT, "applications/{application_id}/guilds/{guild_id}/commands/{command_id}/permissions");
         CALLBACK = new Route(Method.POST, "interactions/{interaction_id}/{interaction_token}/callback", true);
         CREATE_FOLLOWUP = new Route(Method.POST, "webhooks/{application_id}/{interaction_token}", true);
         EDIT_FOLLOWUP = new Route(Method.PATCH, "webhooks/{application_id}/{interaction_token}/messages/{message_id}", true);
         DELETE_FOLLOWUP = new Route(Method.DELETE, "webhooks/{application_id}/{interaction_token}/messages/{message_id}", true);
         GET_MESSAGE = new Route(Method.GET, "webhooks/{application_id}/{interaction_token}/messages/{message_id}", true);
      }
   }

   public static class Self {
      public static final Route GET_SELF;
      public static final Route MODIFY_SELF;
      public static final Route GET_GUILDS;
      public static final Route LEAVE_GUILD;
      public static final Route GET_PRIVATE_CHANNELS;
      public static final Route CREATE_PRIVATE_CHANNEL;

      static {
         GET_SELF = new Route(Method.GET, "users/@me");
         MODIFY_SELF = new Route(Method.PATCH, "users/@me");
         GET_GUILDS = new Route(Method.GET, "users/@me/guilds");
         LEAVE_GUILD = new Route(Method.DELETE, "users/@me/guilds/{guild_id}");
         GET_PRIVATE_CHANNELS = new Route(Method.GET, "users/@me/channels");
         CREATE_PRIVATE_CHANNEL = new Route(Method.POST, "users/@me/channels");
      }
   }

   public static class Users {
      public static final Route GET_USER;

      static {
         GET_USER = new Route(Method.GET, "users/{user_id}");
      }
   }

   public static class Guilds {
      public static final Route GET_GUILD;
      public static final Route MODIFY_GUILD;
      public static final Route GET_VANITY_URL;
      public static final Route CREATE_CHANNEL;
      public static final Route GET_CHANNELS;
      public static final Route MODIFY_CHANNELS;
      public static final Route MODIFY_ROLES;
      public static final Route GET_ROLE_MEMBER_COUNTS;
      public static final Route GET_BANS;
      public static final Route GET_BAN;
      public static final Route UNBAN;
      public static final Route BAN;
      public static final Route BULK_BAN;
      public static final Route KICK_MEMBER;
      public static final Route MODIFY_MEMBER;
      public static final Route ADD_MEMBER;
      public static final Route GET_MEMBER;
      public static final Route MODIFY_SELF;
      public static final Route PRUNABLE_COUNT;
      public static final Route PRUNE_MEMBERS;
      public static final Route GET_WEBHOOKS;
      public static final Route GET_GUILD_EMBED;
      public static final Route MODIFY_GUILD_EMBED;
      public static final Route GET_GUILD_EMOJIS;
      public static final Route GET_AUDIT_LOGS;
      public static final Route GET_VOICE_REGIONS;
      public static final Route GET_VOICE_STATE;
      public static final Route UPDATE_VOICE_STATE;
      public static final Route GET_INTEGRATIONS;
      public static final Route CREATE_INTEGRATION;
      public static final Route DELETE_INTEGRATION;
      public static final Route MODIFY_INTEGRATION;
      public static final Route SYNC_INTEGRATION;
      public static final Route ADD_MEMBER_ROLE;
      public static final Route REMOVE_MEMBER_ROLE;
      public static final Route LIST_ACTIVE_THREADS;
      public static final Route GET_SCHEDULED_EVENT;
      public static final Route GET_SCHEDULED_EVENTS;
      public static final Route CREATE_SCHEDULED_EVENT;
      public static final Route MODIFY_SCHEDULED_EVENT;
      public static final Route DELETE_SCHEDULED_EVENT;
      public static final Route GET_SCHEDULED_EVENT_USERS;
      public static final Route GET_WELCOME_SCREEN;
      public static final Route MODIFY_WELCOME_SCREEN;
      public static final Route MODIFY_GUILD_INCIDENTS;

      static {
         GET_GUILD = new Route(Method.GET, "guilds/{guild_id}");
         MODIFY_GUILD = new Route(Method.PATCH, "guilds/{guild_id}");
         GET_VANITY_URL = new Route(Method.GET, "guilds/{guild_id}/vanity-url");
         CREATE_CHANNEL = new Route(Method.POST, "guilds/{guild_id}/channels");
         GET_CHANNELS = new Route(Method.GET, "guilds/{guild_id}/channels");
         MODIFY_CHANNELS = new Route(Method.PATCH, "guilds/{guild_id}/channels");
         MODIFY_ROLES = new Route(Method.PATCH, "guilds/{guild_id}/roles");
         GET_ROLE_MEMBER_COUNTS = new Route(Method.GET, "guilds/{guild_id}/roles/member-counts");
         GET_BANS = new Route(Method.GET, "guilds/{guild_id}/bans");
         GET_BAN = new Route(Method.GET, "guilds/{guild_id}/bans/{user_id}");
         UNBAN = new Route(Method.DELETE, "guilds/{guild_id}/bans/{user_id}");
         BAN = new Route(Method.PUT, "guilds/{guild_id}/bans/{user_id}");
         BULK_BAN = new Route(Method.POST, "guilds/{guild_id}/bulk-ban");
         KICK_MEMBER = new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}");
         MODIFY_MEMBER = new Route(Method.PATCH, "guilds/{guild_id}/members/{user_id}");
         ADD_MEMBER = new Route(Method.PUT, "guilds/{guild_id}/members/{user_id}");
         GET_MEMBER = new Route(Method.GET, "guilds/{guild_id}/members/{user_id}");
         MODIFY_SELF = new Route(Method.PATCH, "guilds/{guild_id}/members/@me");
         PRUNABLE_COUNT = new Route(Method.GET, "guilds/{guild_id}/prune");
         PRUNE_MEMBERS = new Route(Method.POST, "guilds/{guild_id}/prune");
         GET_WEBHOOKS = new Route(Method.GET, "guilds/{guild_id}/webhooks");
         GET_GUILD_EMBED = new Route(Method.GET, "guilds/{guild_id}/embed");
         MODIFY_GUILD_EMBED = new Route(Method.PATCH, "guilds/{guild_id}/embed");
         GET_GUILD_EMOJIS = new Route(Method.GET, "guilds/{guild_id}/emojis");
         GET_AUDIT_LOGS = new Route(Method.GET, "guilds/{guild_id}/audit-logs");
         GET_VOICE_REGIONS = new Route(Method.GET, "guilds/{guild_id}/regions");
         GET_VOICE_STATE = new Route(Method.GET, "guilds/{guild_id}/voice-states/{user_id}");
         UPDATE_VOICE_STATE = new Route(Method.PATCH, "guilds/{guild_id}/voice-states/{user_id}");
         GET_INTEGRATIONS = new Route(Method.GET, "guilds/{guild_id}/integrations");
         CREATE_INTEGRATION = new Route(Method.POST, "guilds/{guild_id}/integrations");
         DELETE_INTEGRATION = new Route(Method.DELETE, "guilds/{guild_id}/integrations/{integration_id}");
         MODIFY_INTEGRATION = new Route(Method.PATCH, "guilds/{guild_id}/integrations/{integration_id}");
         SYNC_INTEGRATION = new Route(Method.POST, "guilds/{guild_id}/integrations/{integration_id}/sync");
         ADD_MEMBER_ROLE = new Route(Method.PUT, "guilds/{guild_id}/members/{user_id}/roles/{role_id}");
         REMOVE_MEMBER_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/members/{user_id}/roles/{role_id}");
         LIST_ACTIVE_THREADS = new Route(Method.GET, "guilds/{guild_id}/threads/active");
         GET_SCHEDULED_EVENT = new Route(Method.GET, "guilds/{guild_id}/scheduled-events/{scheduled_event_id}");
         GET_SCHEDULED_EVENTS = new Route(Method.GET, "guilds/{guild_id}/scheduled-events");
         CREATE_SCHEDULED_EVENT = new Route(Method.POST, "guilds/{guild_id}/scheduled-events");
         MODIFY_SCHEDULED_EVENT = new Route(Method.PATCH, "guilds/{guild_id}/scheduled-events/{scheduled_event_id}");
         DELETE_SCHEDULED_EVENT = new Route(Method.DELETE, "guilds/{guild_id}/scheduled-events/{scheduled_event_id}");
         GET_SCHEDULED_EVENT_USERS = new Route(Method.GET, "guilds/{guild_id}/scheduled-events/{scheduled_event_id}/users");
         GET_WELCOME_SCREEN = new Route(Method.GET, "guilds/{guild_id}/welcome-screen");
         MODIFY_WELCOME_SCREEN = new Route(Method.PATCH, "guilds/{guild_id}/welcome-screen");
         MODIFY_GUILD_INCIDENTS = new Route(Method.PUT, "guilds/{guild_id}/incident-actions");
      }
   }

   public static class Emojis {
      public static final Route MODIFY_EMOJI;
      public static final Route DELETE_EMOJI;
      public static final Route CREATE_EMOJI;
      public static final Route GET_EMOJIS;
      public static final Route GET_EMOJI;

      static {
         MODIFY_EMOJI = new Route(Method.PATCH, "guilds/{guild_id}/emojis/{emoji_id}");
         DELETE_EMOJI = new Route(Method.DELETE, "guilds/{guild_id}/emojis/{emoji_id}");
         CREATE_EMOJI = new Route(Method.POST, "guilds/{guild_id}/emojis");
         GET_EMOJIS = new Route(Method.GET, "guilds/{guild_id}/emojis");
         GET_EMOJI = new Route(Method.GET, "guilds/{guild_id}/emojis/{emoji_id}");
      }
   }

   public static class Stickers {
      public static final Route GET_GUILD_STICKERS;
      public static final Route GET_GUILD_STICKER;
      public static final Route MODIFY_GUILD_STICKER;
      public static final Route DELETE_GUILD_STICKER;
      public static final Route CREATE_GUILD_STICKER;
      public static final Route GET_STICKER;
      public static final Route LIST_PACKS;

      static {
         GET_GUILD_STICKERS = new Route(Method.GET, "guilds/{guild_id}/stickers");
         GET_GUILD_STICKER = new Route(Method.GET, "guilds/{guild_id}/stickers/{sticker_id}");
         MODIFY_GUILD_STICKER = new Route(Method.PATCH, "guilds/{guild_id}/stickers/{sticker_id}");
         DELETE_GUILD_STICKER = new Route(Method.DELETE, "guilds/{guild_id}/stickers/{sticker_id}");
         CREATE_GUILD_STICKER = new Route(Method.POST, "guilds/{guild_id}/stickers");
         GET_STICKER = new Route(Method.GET, "stickers/{sticker_id}");
         LIST_PACKS = new Route(Method.GET, "sticker-packs");
      }
   }

   public static class SoundboardSounds {
      public static final Route SEND_SOUNDBOARD_SOUND;
      public static final Route LIST_DEFAULT_SOUNDBOARD_SOUNDS;
      public static final Route LIST_GUILD_SOUNDBOARD_SOUNDS;
      public static final Route GET_GUILD_SOUNDBOARD_SOUND;
      public static final Route CREATE_GUILD_SOUNDBOARD_SOUND;
      public static final Route MODIFY_GUILD_SOUNDBOARD_SOUND;
      public static final Route DELETE_GUILD_SOUNDBOARD_SOUND;

      static {
         SEND_SOUNDBOARD_SOUND = new Route(Method.POST, "channels/{channel_id}/send-soundboard-sound");
         LIST_DEFAULT_SOUNDBOARD_SOUNDS = new Route(Method.GET, "soundboard-default-sounds");
         LIST_GUILD_SOUNDBOARD_SOUNDS = new Route(Method.GET, "guilds/{guild_id}/soundboard-sounds");
         GET_GUILD_SOUNDBOARD_SOUND = new Route(Method.GET, "guilds/{guild_id}/soundboard-sounds/{sound_id}");
         CREATE_GUILD_SOUNDBOARD_SOUND = new Route(Method.POST, "guilds/{guild_id}/soundboard-sounds");
         MODIFY_GUILD_SOUNDBOARD_SOUND = new Route(Method.PATCH, "guilds/{guild_id}/soundboard-sounds/{sound_id}");
         DELETE_GUILD_SOUNDBOARD_SOUND = new Route(Method.DELETE, "guilds/{guild_id}/soundboard-sounds/{sound_id}");
      }
   }

   public static class Webhooks {
      public static final Route GET_WEBHOOK;
      public static final Route GET_TOKEN_WEBHOOK;
      public static final Route DELETE_WEBHOOK;
      public static final Route DELETE_TOKEN_WEBHOOK;
      public static final Route MODIFY_WEBHOOK;
      public static final Route MODIFY_TOKEN_WEBHOOK;
      public static final Route EXECUTE_WEBHOOK;
      public static final Route EXECUTE_WEBHOOK_FETCH;
      public static final Route EXECUTE_WEBHOOK_EDIT;
      public static final Route EXECUTE_WEBHOOK_DELETE;
      public static final Route EXECUTE_WEBHOOK_SLACK;
      public static final Route EXECUTE_WEBHOOK_GITHUB;

      static {
         GET_WEBHOOK = new Route(Method.GET, "webhooks/{webhook_id}");
         GET_TOKEN_WEBHOOK = new Route(Method.GET, "webhooks/{webhook_id}/{token}");
         DELETE_WEBHOOK = new Route(Method.DELETE, "webhooks/{webhook_id}");
         DELETE_TOKEN_WEBHOOK = new Route(Method.DELETE, "webhooks/{webhook_id}/{token}");
         MODIFY_WEBHOOK = new Route(Method.PATCH, "webhooks/{webhook_id}");
         MODIFY_TOKEN_WEBHOOK = new Route(Method.PATCH, "webhooks/{webhook_id}/{token}");
         EXECUTE_WEBHOOK = new Route(Method.POST, "webhooks/{webhook_id}/{token}");
         EXECUTE_WEBHOOK_FETCH = new Route(Method.GET, "webhooks/{webhook_id}/{token}/messages/{message_id}");
         EXECUTE_WEBHOOK_EDIT = new Route(Method.PATCH, "webhooks/{webhook_id}/{token}/messages/{message_id}");
         EXECUTE_WEBHOOK_DELETE = new Route(Method.DELETE, "webhooks/{webhook_id}/{token}/messages/{message_id}");
         EXECUTE_WEBHOOK_SLACK = new Route(Method.POST, "webhooks/{webhook_id}/{token}/slack");
         EXECUTE_WEBHOOK_GITHUB = new Route(Method.POST, "webhooks/{webhook_id}/{token}/github");
      }
   }

   public static class Roles {
      public static final Route GET_ROLES;
      public static final Route CREATE_ROLE;
      public static final Route GET_ROLE;
      public static final Route MODIFY_ROLE;
      public static final Route DELETE_ROLE;

      static {
         GET_ROLES = new Route(Method.GET, "guilds/{guild_id}/roles");
         CREATE_ROLE = new Route(Method.POST, "guilds/{guild_id}/roles");
         GET_ROLE = new Route(Method.GET, "guilds/{guild_id}/roles/{role_id}");
         MODIFY_ROLE = new Route(Method.PATCH, "guilds/{guild_id}/roles/{role_id}");
         DELETE_ROLE = new Route(Method.DELETE, "guilds/{guild_id}/roles/{role_id}");
      }
   }

   public static class Channels {
      public static final Route DELETE_CHANNEL;
      public static final Route MODIFY_CHANNEL;
      public static final Route GET_CHANNEL;
      public static final Route GET_WEBHOOKS;
      public static final Route CREATE_WEBHOOK;
      public static final Route CREATE_PERM_OVERRIDE;
      public static final Route MODIFY_PERM_OVERRIDE;
      public static final Route DELETE_PERM_OVERRIDE;
      public static final Route SET_STATUS;
      public static final Route SEND_TYPING;
      public static final Route GET_PERMISSIONS;
      public static final Route GET_PERM_OVERRIDE;
      public static final Route FOLLOW_CHANNEL;
      public static final Route CREATE_THREAD_FROM_MESSAGE;
      public static final Route CREATE_THREAD;
      public static final Route JOIN_THREAD;
      public static final Route ADD_THREAD_MEMBER;
      public static final Route LEAVE_THREAD;
      public static final Route REMOVE_THREAD_MEMBER;
      public static final Route GET_THREAD_MEMBER;
      public static final Route LIST_THREAD_MEMBERS;
      public static final Route LIST_PUBLIC_ARCHIVED_THREADS;
      public static final Route LIST_PRIVATE_ARCHIVED_THREADS;
      public static final Route LIST_JOINED_PRIVATE_ARCHIVED_THREADS;

      static {
         DELETE_CHANNEL = new Route(Method.DELETE, "channels/{channel_id}");
         MODIFY_CHANNEL = new Route(Method.PATCH, "channels/{channel_id}");
         GET_CHANNEL = new Route(Method.GET, "channels/{channel_id}");
         GET_WEBHOOKS = new Route(Method.GET, "channels/{channel_id}/webhooks");
         CREATE_WEBHOOK = new Route(Method.POST, "channels/{channel_id}/webhooks");
         CREATE_PERM_OVERRIDE = new Route(Method.PUT, "channels/{channel_id}/permissions/{permoverride_id}");
         MODIFY_PERM_OVERRIDE = new Route(Method.PUT, "channels/{channel_id}/permissions/{permoverride_id}");
         DELETE_PERM_OVERRIDE = new Route(Method.DELETE, "channels/{channel_id}/permissions/{permoverride_id}");
         SET_STATUS = new Route(Method.PUT, "channels/{channel_id}/voice-status");
         SEND_TYPING = new Route(Method.POST, "channels/{channel_id}/typing");
         GET_PERMISSIONS = new Route(Method.GET, "channels/{channel_id}/permissions");
         GET_PERM_OVERRIDE = new Route(Method.GET, "channels/{channel_id}/permissions/{permoverride_id}");
         FOLLOW_CHANNEL = new Route(Method.POST, "channels/{channel_id}/followers");
         CREATE_THREAD_FROM_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages/{message_id}/threads");
         CREATE_THREAD = new Route(Method.POST, "channels/{channel_id}/threads");
         JOIN_THREAD = new Route(Method.PUT, "channels/{channel_id}/thread-members/@me");
         ADD_THREAD_MEMBER = new Route(Method.PUT, "channels/{channel_id}/thread-members/{user_id}");
         LEAVE_THREAD = new Route(Method.DELETE, "channels/{channel_id}/thread-members/@me");
         REMOVE_THREAD_MEMBER = new Route(Method.DELETE, "channels/{channel_id}/thread-members/{user_id}");
         GET_THREAD_MEMBER = new Route(Method.GET, "channels/{channel_id}/thread-members/{user_id}");
         LIST_THREAD_MEMBERS = new Route(Method.GET, "channels/{channel_id}/thread-members");
         LIST_PUBLIC_ARCHIVED_THREADS = new Route(Method.GET, "channels/{channel_id}/threads/archived/public");
         LIST_PRIVATE_ARCHIVED_THREADS = new Route(Method.GET, "channels/{channel_id}/threads/archived/private");
         LIST_JOINED_PRIVATE_ARCHIVED_THREADS = new Route(Method.GET, "channels/{channel_id}/users/@me/threads/archived/private");
      }
   }

   public static class StageInstances {
      public static final Route GET_INSTANCE;
      public static final Route DELETE_INSTANCE;
      public static final Route UPDATE_INSTANCE;
      public static final Route CREATE_INSTANCE;

      static {
         GET_INSTANCE = new Route(Method.GET, "stage-instances/{channel_id}");
         DELETE_INSTANCE = new Route(Method.DELETE, "stage-instances/{channel_id}");
         UPDATE_INSTANCE = new Route(Method.PATCH, "stage-instances/{channel_id}");
         CREATE_INSTANCE = new Route(Method.POST, "stage-instances");
      }
   }

   public static class AutoModeration {
      public static final Route LIST_RULES;
      public static final Route GET_RULE;
      public static final Route CREATE_RULE;
      public static final Route UPDATE_RULE;
      public static final Route DELETE_RULE;

      static {
         LIST_RULES = new Route(Method.GET, "guilds/{guild_id}/auto-moderation/rules");
         GET_RULE = new Route(Method.GET, "guilds/{guild_id}/auto-moderation/rules/{rule_id}");
         CREATE_RULE = new Route(Method.POST, "guilds/{guild_id}/auto-moderation/rules");
         UPDATE_RULE = new Route(Method.PATCH, "guilds/{guild_id}/auto-moderation/rules/{rule_id}");
         DELETE_RULE = new Route(Method.DELETE, "guilds/{guild_id}/auto-moderation/rules/{rule_id}");
      }
   }

   public static class Messages {
      public static final Route EDIT_MESSAGE;
      public static final Route SEND_MESSAGE;
      public static final Route GET_MESSAGE_PINS;
      public static final Route PIN_MESSAGE;
      public static final Route UNPIN_MESSAGE;
      public static final Route ADD_REACTION;
      public static final Route REMOVE_REACTION;
      public static final Route REMOVE_ALL_REACTIONS;
      public static final Route GET_REACTION_USERS;
      public static final Route CLEAR_EMOJI_REACTIONS;
      public static final Route DELETE_MESSAGE;
      public static final Route GET_MESSAGE_HISTORY;
      public static final Route CROSSPOST_MESSAGE;
      public static final Route GET_MESSAGE;
      public static final Route DELETE_MESSAGES;
      public static final Route END_POLL;
      public static final Route GET_POLL_ANSWER_VOTERS;

      static {
         EDIT_MESSAGE = new Route(Method.PATCH, "channels/{channel_id}/messages/{message_id}");
         SEND_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages");
         GET_MESSAGE_PINS = new Route(Method.GET, "channels/{channel_id}/messages/pins");
         PIN_MESSAGE = new Route(Method.PUT, "channels/{channel_id}/messages/pins/{message_id}");
         UNPIN_MESSAGE = new Route(Method.DELETE, "channels/{channel_id}/messages/pins/{message_id}");
         ADD_REACTION = new Route(Method.PUT, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}");
         REMOVE_REACTION = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}/{user_id}");
         REMOVE_ALL_REACTIONS = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions");
         GET_REACTION_USERS = new Route(Method.GET, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}");
         CLEAR_EMOJI_REACTIONS = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}/reactions/{reaction_code}");
         DELETE_MESSAGE = new Route(Method.DELETE, "channels/{channel_id}/messages/{message_id}");
         GET_MESSAGE_HISTORY = new Route(Method.GET, "channels/{channel_id}/messages");
         CROSSPOST_MESSAGE = new Route(Method.POST, "channels/{channel_id}/messages/{message_id}/crosspost");
         GET_MESSAGE = new Route(Method.GET, "channels/{channel_id}/messages/{message_id}");
         DELETE_MESSAGES = new Route(Method.POST, "channels/{channel_id}/messages/bulk-delete");
         END_POLL = new Route(Method.POST, "channels/{channel_id}/polls/{message_id}/expire");
         GET_POLL_ANSWER_VOTERS = new Route(Method.GET, "channels/{channel_id}/polls/{message_id}/answers/{answer_id}");
      }
   }

   public static class Invites {
      public static final Route GET_INVITE;
      public static final Route GET_GUILD_INVITES;
      public static final Route GET_CHANNEL_INVITES;
      public static final Route CREATE_INVITE;
      public static final Route DELETE_INVITE;

      static {
         GET_INVITE = new Route(Method.GET, "invites/{code}");
         GET_GUILD_INVITES = new Route(Method.GET, "guilds/{guild_id}/invites");
         GET_CHANNEL_INVITES = new Route(Method.GET, "channels/{channel_id}/invites");
         CREATE_INVITE = new Route(Method.POST, "channels/{channel_id}/invites");
         DELETE_INVITE = new Route(Method.DELETE, "invites/{code}");
      }
   }

   public static class Templates {
      public static final Route GET_TEMPLATE;
      public static final Route SYNC_TEMPLATE;
      public static final Route CREATE_TEMPLATE;
      public static final Route MODIFY_TEMPLATE;
      public static final Route DELETE_TEMPLATE;
      public static final Route GET_GUILD_TEMPLATES;

      static {
         GET_TEMPLATE = new Route(Method.GET, "guilds/templates/{code}");
         SYNC_TEMPLATE = new Route(Method.PUT, "guilds/{guild_id}/templates/{code}");
         CREATE_TEMPLATE = new Route(Method.POST, "guilds/{guild_id}/templates");
         MODIFY_TEMPLATE = new Route(Method.PATCH, "guilds/{guild_id}/templates/{code}");
         DELETE_TEMPLATE = new Route(Method.DELETE, "guilds/{guild_id}/templates/{code}");
         GET_GUILD_TEMPLATES = new Route(Method.GET, "guilds/{guild_id}/templates");
      }
   }

   public class CompiledRoute {
      private final Route baseRoute;
      private final String major;
      private final List<PathSegment> path;
      private final List<QueryParameter> query;
      private final String compiledRoute;

      private CompiledRoute(Route baseRoute, List<PathSegment> path, String major) {
         this.baseRoute = baseRoute;
         this.path = path;
         this.major = major;
         this.query = null;
         this.compiledRoute = Route.compileRoute(this.path, this.query);
      }

      private CompiledRoute(CompiledRoute original, List<QueryParameter> query) {
         this.baseRoute = original.baseRoute;
         this.path = original.path;
         this.major = original.major;
         this.query = query;
         this.compiledRoute = Route.compileRoute(this.path, this.query);
      }

      @Nonnull
      @CheckReturnValue
      public CompiledRoute withQueryParams(@Nonnull String... params) {
         Checks.notNull(params, "Params");
         Checks.check(params.length >= 2, "Params length must be at least 2");
         Checks.check((params.length & 1) == 0, "Params length must be a multiple of 2");
         List<QueryParameter> newQuery;
         if (this.query == null) {
            newQuery = new ArrayList(params.length / 2);
         } else {
            newQuery = new ArrayList(this.query.size() + params.length / 2);
            newQuery.addAll(this.query);
         }

         for(int i = 0; i < params.length; i += 2) {
            Checks.notEmpty(params[i], "Query key [" + i / 2 + "]");
            Checks.notNull(params[i + 1], "Query value [" + i / 2 + "]");
            newQuery.add(new QueryParameter(params[i], params[i + 1]));
         }

         return Route.this.new CompiledRoute(this, newQuery);
      }

      @Nonnull
      public String getMajorParameters() {
         return this.major;
      }

      @Nonnull
      public String getCompiledRoute() {
         return this.compiledRoute;
      }

      @Nonnull
      public HttpUrl toHttpUrl(@Nonnull HttpUrl baseUrl) {
         Checks.notNull(baseUrl, "Base URL");
         HttpUrl.Builder url = baseUrl.newBuilder();

         for(PathSegment segment : this.path) {
            if (segment.encoded) {
               url.addEncodedPathSegment(segment.value);
            } else {
               url.addPathSegment(segment.value);
            }
         }

         if (this.query != null) {
            for(QueryParameter queryParam : this.query) {
               url.addQueryParameter(queryParam.name, queryParam.value);
            }
         }

         return url.build();
      }

      @Nonnull
      public Route getBaseRoute() {
         return this.baseRoute;
      }

      @Nonnull
      public Method getMethod() {
         return this.baseRoute.method;
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.baseRoute, this.path});
      }

      public boolean equals(Object o) {
         if (!(o instanceof CompiledRoute)) {
            return false;
         } else {
            CompiledRoute oCompiled = (CompiledRoute)o;
            return this.baseRoute.equals(oCompiled.getBaseRoute()) && this.path.equals(oCompiled.path);
         }
      }

      public String toString() {
         return (new EntityString(this)).setType(Route.this.method).addMetadata("compiledRoute", this.getCompiledRoute()).toString();
      }
   }

   private static final class QueryParameter {
      private final String name;
      private final String value;

      private QueryParameter(String name, String value) {
         this.name = name;
         this.value = value;
      }

      public String toString() {
         return this.name + "=" + EncodingUtil.encodeUTF8(this.value);
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof QueryParameter)) {
            return false;
         } else {
            QueryParameter other = (QueryParameter)o;
            return Objects.equals(this.name, other.name) && Objects.equals(this.value, other.value);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.name, this.value});
      }
   }

   private static final class PathSegment {
      private final String value;
      private final boolean encoded;

      private PathSegment(String value, boolean encoded) {
         this.value = value;
         this.encoded = encoded;
      }

      public String toString() {
         return this.encoded ? this.value : EncodingUtil.encodeUTF8(this.value);
      }

      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof PathSegment)) {
            return false;
         } else {
            PathSegment other = (PathSegment)o;
            return this.encoded == other.encoded && Objects.equals(this.value, other.value);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.value, this.encoded});
      }
   }
}
