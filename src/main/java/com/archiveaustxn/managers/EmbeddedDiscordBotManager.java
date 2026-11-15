package com.archiveaustxn.managers;

import com.archiveaustxn.EconomySMP;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.Color;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Runs the EconomySMP Discord application in the plugin JVM.
 *
 * The bot uses the same one-time sync codes created by /link and /pvp sync.
 * Its Discord-to-Minecraft link table is kept in the configured links SQLite file.
 */
public final class EmbeddedDiscordBotManager extends ListenerAdapter {

    private final EconomySMP plugin;
    private volatile JDA jda;
    private Connection linksConnection;

    public EmbeddedDiscordBotManager(EconomySMP plugin) {
        this.plugin = plugin;
    }

    public synchronized void start() {
        shutdown();
        var config = plugin.getConfigManager().getDiscord();
        if (!config.getBoolean("enabled", true)
                || !config.getBoolean("BOT.ENABLED", true)) {
            plugin.getLogger().info("Embedded Discord bot is disabled in core/discord/discord.yml.");
            return;
        }

        String token = config.getString("BOT.TOKEN", "").trim();
        if (token.isEmpty() || token.equalsIgnoreCase("your_bot_token")) {
            plugin.getLogger().warning("Discord bot is not started: BOT.TOKEN is empty in core/discord/discord.yml.");
            return;
        }

        try {
            openLinksDatabase(config.getString("BOT.LINKS_FILE", "data/links.db"));
            // Slash commands do not need privileged Message Content intent.
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(this)
                    .build();
            plugin.getLogger().info("Connecting the embedded EconomySMP Discord bot...");
        } catch (Exception exception) {
            closeLinksDatabase();
            jda = null;
            plugin.getLogger().log(Level.SEVERE, "Could not start the embedded Discord bot. Check BOT.TOKEN and bot gateway intents.", exception);
        }
    }

    public synchronized void shutdown() {
        JDA current = jda;
        jda = null;
        if (current != null) {
            current.shutdown();
        }
        closeLinksDatabase();
    }

    public synchronized void reload() {
        start();
    }

    public boolean isConnected() {
        JDA current = jda;
        return current != null && current.getStatus() == JDA.Status.CONNECTED;
    }

    @Override
    public void onReady(ReadyEvent event) {
        plugin.getLogger().info("Discord bot connected as " + event.getJDA().getSelfUser().getAsTag() + ".");
        registerCommands(event.getJDA());
    }

    private void registerCommands(JDA client) {
        var commands = java.util.List.of(
                Commands.slash("link", "Link your Minecraft account.")
                        .addOption(OptionType.STRING, "code", "The one-time code from /link in Minecraft.", true),
                Commands.slash("sync", "Link your Minecraft account.")
                        .addOption(OptionType.STRING, "code", "The one-time code from /pvp sync in Minecraft.", true),
                Commands.slash("unlink", "Unlink your Minecraft account from Discord."),
                Commands.slash("unsync", "Unlink your Minecraft account from Discord."),
                Commands.slash("ip", "Show the Minecraft server address.")
        );

        String guildId = plugin.getConfigManager().getDiscord().getString("BOT.GUILD_ID", "").trim();
        if (!guildId.isEmpty() && !guildId.equalsIgnoreCase("your_guild_id")) {
            Guild guild = client.getGuildById(guildId);
            if (guild != null) {
                guild.updateCommands().addCommands(commands).queue(
                        ignored -> plugin.getLogger().info("Discord slash commands registered in guild " + guildId + "."),
                        error -> plugin.getLogger().log(Level.WARNING, "Could not register Discord slash commands.", error));
                return;
            }
            plugin.getLogger().warning("BOT.GUILD_ID is configured but the bot is not in that guild; registering slash commands globally.");
        }

        client.updateCommands().addCommands(commands).queue(
                ignored -> plugin.getLogger().info("Discord slash commands registered globally."),
                error -> plugin.getLogger().log(Level.WARNING, "Could not register Discord slash commands.", error));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName().toLowerCase(Locale.ROOT);
        if (command.equals("link") || command.equals("sync")) {
            String code = event.getOption("code") == null ? "" : event.getOption("code").getAsString();
            String response = linkAccount(event.getUser().getId(), code);
            event.reply(response).setEphemeral(true).queue(
                    null,
                    error -> plugin.getLogger().log(Level.WARNING, "Could not respond to Discord link command.", error));
        } else if (command.equals("unlink") || command.equals("unsync")) {
            String response = unlinkAccount(event.getUser().getId());
            event.reply(response).setEphemeral(true).queue();
        } else if (command.equals("ip")) {
            var config = plugin.getConfigManager().getDiscord();
            if (!config.getBoolean("IP-COMMAND.ENABLED", true)) {
                event.reply("The server address command is disabled.").setEphemeral(true).queue();
                return;
            }
            String address = config.getString("IP-COMMAND.SERVER-ADDRESS", "CHANGE_ME").trim();
            int port = config.getInt("IP-COMMAND.SERVER-PORT", 25565);
            String displayAddress = address + (port == 25565 ? "" : ":" + port);
            Color color;
            try {
                color = Color.decode(config.getString("IP-COMMAND.COLOR", "#009BFF"));
            } catch (NumberFormatException ignored) {
                color = new Color(0x009BFF);
            }
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(color)
                    .setTitle(config.getString("IP-COMMAND.SERVER-NAME", "EconomySMP"))
                    .setDescription("Minecraft server information")
                    .addField("Server IP", address.equalsIgnoreCase("CHANGE_ME")
                            ? "Not configured" : "`" + displayAddress + "`", false)
                    .setFooter(config.getString("IP-COMMAND.FOOTER", "EconomySMP"))
                    .setTimestamp(java.time.Instant.now());
            event.replyEmbeds(embed.build()).queue();
        }
    }

    private synchronized String linkAccount(String discordId, String suppliedCode) {
        var discordConfig = plugin.getConfigManager().getDiscord();
        if (!discordConfig.getBoolean("ACCOUNT_LINKING.ENABLED", true)
                || plugin.getPvpManager() == null
                || !plugin.getPvpManager().isSyncEnabled()) {
            return "Account linking is currently disabled on this server.";
        }

        String code = suppliedCode == null ? "" : suppliedCode.trim().toLowerCase(Locale.ROOT);
        if (code.isEmpty()) {
            return "Enter the one-time code shown by /link in Minecraft.";
        }

        if (linksConnection == null) {
            return "The link database is unavailable. Please contact a server administrator.";
        }

        try {
            try (PreparedStatement existing = linksConnection.prepareStatement(
                    "SELECT player_name FROM links WHERE discord_id = ?")) {
                existing.setString(1, discordId);
                try (ResultSet result = existing.executeQuery()) {
                    if (result.next()) {
                        String name = result.getString("player_name");
                        return "Your Discord account is already linked to **" + safe(name) + "**. Use /unlink first.";
                    }
                }
            }

            String uuid;
            String playerName;
            long expiresAt;
            Connection gameDatabase = plugin.getDatabaseManager() == null
                    ? null : plugin.getDatabaseManager().getConnection();
            if (gameDatabase == null) {
                return "The Minecraft database is unavailable. Please try again later.";
            }

            try (PreparedStatement lookup = gameDatabase.prepareStatement(
                    "SELECT player_uuid, player_name, expires_at FROM pvp_sync_codes WHERE LOWER(code) = ?")) {
                lookup.setString(1, code);
                try (ResultSet result = lookup.executeQuery()) {
                    if (!result.next()) {
                        return message("BOT.LINK_INVALID", "That link code is invalid. Run /link in Minecraft to get a new one.");
                    }
                    uuid = result.getString("player_uuid");
                    playerName = result.getString("player_name");
                    expiresAt = result.getLong("expires_at");
                }
            }

            if (expiresAt <= System.currentTimeMillis()) {
                try (PreparedStatement delete = gameDatabase.prepareStatement(
                        "DELETE FROM pvp_sync_codes WHERE LOWER(code) = ?")) {
                    delete.setString(1, code);
                    delete.executeUpdate();
                }
                return message("BOT.LINK_EXPIRED", "That link code has expired. Run /link in Minecraft to get a new one.");
            }

            try (PreparedStatement existingPlayer = linksConnection.prepareStatement(
                    "SELECT discord_id FROM links WHERE player_uuid = ?")) {
                existingPlayer.setString(1, uuid);
                try (ResultSet result = existingPlayer.executeQuery()) {
                    if (result.next()) {
                        return message("BOT.LINK_TAKEN", "That Minecraft account is already linked to another Discord user.");
                    }
                }
            }

            try (PreparedStatement insert = linksConnection.prepareStatement(
                    "INSERT INTO links (discord_id, player_uuid, player_name, code, linked_at) VALUES (?, ?, ?, ?, ?)")) {
                insert.setString(1, discordId);
                insert.setString(2, uuid);
                insert.setString(3, playerName);
                insert.setString(4, code);
                insert.setLong(5, System.currentTimeMillis());
                insert.executeUpdate();
            }

            try (PreparedStatement delete = gameDatabase.prepareStatement(
                    "DELETE FROM pvp_sync_codes WHERE LOWER(code) = ?")) {
                delete.setString(1, code);
                delete.executeUpdate();
            }
            return message("BOT.LINK_SUCCESS", "Successfully linked your Minecraft account!")
                    .replace("{player}", safe(playerName));
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to link a Discord account.", exception);
            return "The account link failed because the database returned an error. Please try again.";
        }
    }

    private synchronized String unlinkAccount(String discordId) {
        if (linksConnection == null) {
            return "The link database is unavailable. Please contact a server administrator.";
        }
        try (PreparedStatement query = linksConnection.prepareStatement(
                "SELECT player_name FROM links WHERE discord_id = ?")) {
            query.setString(1, discordId);
            String playerName;
            try (ResultSet result = query.executeQuery()) {
                if (!result.next()) {
                    return message("BOT.UNLINK_NOT_LINKED", "Your Discord account is not linked to a Minecraft account.");
                }
                playerName = result.getString("player_name");
            }
            try (PreparedStatement delete = linksConnection.prepareStatement(
                    "DELETE FROM links WHERE discord_id = ?")) {
                delete.setString(1, discordId);
                delete.executeUpdate();
            }
            return message("BOT.UNLINK_SUCCESS", "Successfully unlinked your Minecraft account.")
                    .replace("{player}", safe(playerName));
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.WARNING, "Failed to unlink a Discord account.", exception);
            return "The account unlink failed because the database returned an error. Please try again.";
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild()) {
            return;
        }
        var config = plugin.getConfigManager().getDiscord();
        if (!config.getBoolean("IP-COMMAND.ENABLED", true)) {
            return;
        }
        String trigger = config.getString("IP-COMMAND.COMMAND", "!ip").trim();
        if (!event.getMessage().getContentRaw().trim().equalsIgnoreCase(trigger)) {
            return;
        }

        String address = config.getString("IP-COMMAND.SERVER-ADDRESS", "CHANGE_ME").trim();
        int port = config.getInt("IP-COMMAND.SERVER-PORT", 25565);
        String displayAddress = address + (port == 25565 ? "" : ":" + port);
        String colorValue = config.getString("IP-COMMAND.COLOR", "#009BFF");
        Color color;
        try {
            color = Color.decode(colorValue);
        } catch (NumberFormatException ignored) {
            color = new Color(0x009BFF);
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setTitle(config.getString("IP-COMMAND.SERVER-NAME", "EconomySMP"))
                .setDescription("Minecraft server information")
                .addField("Server IP", address.equalsIgnoreCase("CHANGE_ME")
                        ? "Not configured" : "`" + displayAddress + "`", false)
                .setFooter(config.getString("IP-COMMAND.FOOTER", "EconomySMP"))
                .setTimestamp(java.time.Instant.now());
        event.getChannel().sendMessageEmbeds(embed.build()).queue();
    }

    private String message(String path, String fallback) {
        return plugin.getConfigManager().getDiscord().getString(path, fallback);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "Minecraft player" : value.replace("@", "@​");
    }

    private void openLinksDatabase(String configuredPath) throws Exception {
        File file = new File(configuredPath == null || configuredPath.isBlank()
                ? "data/links.db" : configuredPath);
        if (!file.isAbsolute()) {
            file = new File(plugin.getDataFolder(), file.getPath());
        }
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create Discord link database directory: " + parent);
        }
        linksConnection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        try (var statement = linksConnection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS links (
                      discord_id TEXT PRIMARY KEY,
                      player_uuid TEXT NOT NULL UNIQUE,
                      player_name TEXT,
                      code TEXT,
                      linked_at INTEGER NOT NULL
                    )
                    """);
            statement.execute("CREATE UNIQUE INDEX IF NOT EXISTS links_code ON links(code)");
        }
    }

    private void closeLinksDatabase() {
        if (linksConnection != null) {
            try {
                linksConnection.close();
            } catch (SQLException exception) {
                plugin.getLogger().log(Level.FINE, "Could not close Discord link database cleanly.", exception);
            }
            linksConnection = null;
        }
    }
}
