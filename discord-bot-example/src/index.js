import 'dotenv/config';
import { readFileSync } from 'node:fs';
import { EmbedBuilder, Client, Events, GatewayIntentBits, MessageFlags } from 'discord.js';
import { queryMinecraftServer } from './server-status.js';
import { createPlayerRepository, leaderboardTypes } from './database.js';
import { createPvpRepository, tierLeaderboardTypes } from './pvp.js';
import { createLinkStore } from './links.js';
import { createLeaderboardEmbed, createStatsEmbed } from './embeds.js';
import { createCompetitiveStatsEmbed, createTierLeaderboardEmbed } from './pvp-embeds.js';
import { createMatchWatcher } from './match-watcher.js';
import { registerCommands } from './register-commands.js';

const token = process.env.DISCORD_TOKEN;

if (!token) {
  throw new Error('DISCORD_TOKEN is required.');
}

const repository = await createPlayerRepository();
const pvp = await createPvpRepository();
const links = createLinkStore();
const ipCommand = loadIpCommandConfig();
const client = new Client({ intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages, GatewayIntentBits.MessageContent] });
const matchWatcher = createMatchWatcher({ client, repository: pvp, links });

await registerCommands();

client.once(Events.ClientReady, async readyClient => {
  console.log(`Logged in as ${readyClient.user.tag}.`);
  await matchWatcher.start();
});

client.on(Events.MessageCreate, async message => {
  if (message.author.bot || !ipCommand.enabled || message.content.trim().toLowerCase() !== ipCommand.command.toLowerCase()) {
    return;
  }

  const address = ipCommand.address;
  const port = ipCommand.port;
  const embed = new EmbedBuilder()
    .setColor(ipCommand.color)
    .setTitle(ipCommand.serverName)
    .setDescription('Minecraft server information')
    .addFields(
      { name: 'Server IP', value: `\u0060${address}${port === 25565 ? '' : `:${port}`}\u0060`, inline: false }
    )
    .setFooter({ text: ipCommand.footer })
    .setTimestamp();

  if (!address || address.toLowerCase() === 'change_me') {
    embed.addFields(
      { name: 'Players', value: 'Unavailable', inline: true },
      { name: 'Minecraft Version', value: 'Unavailable', inline: true },
      { name: 'Ping', value: 'Unavailable', inline: true }
    );
    await message.reply({ embeds: [embed] });
    return;
  }

  try {
    const result = await queryMinecraftServer(address, port);
    const players = result.status.players ?? {};
    const rawVersion = String(result.status.version?.name ?? '');
    const version = rawVersion.match(/\b1\.\d+(?:\.\d+)?\b/)?.[0] ?? 'Unknown';
    embed.addFields(
      { name: 'Players', value: `${players.online ?? 0}/${players.max ?? 0}`, inline: true },
      { name: 'Minecraft Version', value: version, inline: true },
      { name: 'Ping', value: `${result.ping} ms`, inline: true }
    );
  } catch (error) {
    console.warn(`Minecraft status query failed: ${error.message}`);
    embed.addFields(
      { name: 'Players', value: '0/0', inline: true },
      { name: 'Minecraft Version', value: 'Unavailable', inline: true },
      { name: 'Ping', value: 'Offline', inline: true }
    );
  }

  await message.reply({ embeds: [embed] });
});

client.on(Events.InteractionCreate, async interaction => {
  if (!interaction.isChatInputCommand()) {
    return;
  }

  try {
    switch (interaction.commandName) {
      case 'stats':
        await handleStats(interaction);
        return;
      case 'leaderboard':
        await handleLeaderboard(interaction);
        return;
      case 'mystats':
        await handleMyStats(interaction);
        return;
      case 'sync':
      case 'link':
        await handleSync(interaction);
        return;
      case 'unsync':
      case 'unlink':
        await handleUnsync(interaction);
        return;
      case 'tier':
        await handleTier(interaction);
        return;
      default:
    }
  } catch (error) {
    console.error(error);
    const message = 'Failed to read the EconomySMP database.';
    if (interaction.deferred || interaction.replied) {
      await interaction.editReply({ content: message, embeds: [] });
    } else {
      await interaction.reply({ content: message, flags: MessageFlags.Ephemeral });
    }
  }
});

async function handleStats(interaction) {
  const playerInput = interaction.options.getString('player', true);
  await interaction.deferReply();

  const player = await repository.findPlayer(playerInput);
  if (!player) {
    await interaction.editReply(`No player found for \`${playerInput}\`.`);
    return;
  }

  await interaction.editReply({ embeds: [createStatsEmbed(player, interaction.user.username)] });
}

async function handleLeaderboard(interaction) {
  const typeKey = interaction.options.getString('type', true);
  const limit = interaction.options.getInteger('limit') ?? 10;
  const type = leaderboardTypes[typeKey];

  await interaction.deferReply();

  if (!type) {
    await interaction.editReply(`Unknown leaderboard type: \`${typeKey}\`.`);
    return;
  }

  const entries = await repository.getLeaderboard(typeKey, limit);
  if (entries.length === 0) {
    await interaction.editReply('No leaderboard data found.');
    return;
  }

  await interaction.editReply({ embeds: [createLeaderboardEmbed(typeKey, entries, interaction.user.username)] });
}

// ── Ranked arena ─────────────────────────────────────────────────────────────

async function handleMyStats(interaction) {
  await interaction.deferReply();

  const link = links.getLink(interaction.user.id);
  if (!link) {
    await interaction.editReply(
      'Your Discord account is not linked yet. Run `/pvp sync` in game, then `/sync` here with the code it gives you.'
    );
    return;
  }

  await replyWithCompetitiveStats(interaction, link.player_uuid, link.player_name);
}

async function handleTier(interaction) {
  const subcommand = interaction.options.getSubcommand();
  if (subcommand === 'stats') {
    await interaction.deferReply();
    await replyWithCompetitiveStats(interaction, interaction.options.getString('player', true));
    return;
  }

  const typeKey = interaction.options.getString('type', true);
  const limit = interaction.options.getInteger('limit') ?? 10;
  await interaction.deferReply();

  if (!tierLeaderboardTypes[typeKey]) {
    await interaction.editReply(`Unknown leaderboard type: \`${typeKey}\`.`);
    return;
  }

  const entries = (await pvp.getTierLeaderboard(typeKey, limit)).filter(Boolean);
  if (entries.length === 0) {
    await interaction.editReply('Nobody has fought a ranked match yet.');
    return;
  }

  await interaction.editReply({
    embeds: [createTierLeaderboardEmbed(typeKey, entries, interaction.user.username)]
  });
}

async function replyWithCompetitiveStats(interaction, lookup, fallbackName) {
  const stats = await pvp.findStats(lookup);
  if (!stats) {
    await interaction.editReply(`No ranked record found for \`${fallbackName ?? lookup}\`.`);
    return;
  }

  const record = await pvp.getRecord(stats.uuid);
  await interaction.editReply({
    embeds: [createCompetitiveStatsEmbed(stats, record, interaction.user.username)]
  });
}

/**
 * Claims a Minecraft account with a code issued in game.
 *
 * The reply is ephemeral throughout: a code that is still valid should not stay readable in a
 * public channel, and neither should the failure that tells somebody a code was wrong.
 */
async function handleSync(interaction) {
  const code = interaction.options.getString('code', true).trim().toLowerCase();
  await interaction.deferReply({ flags: MessageFlags.Ephemeral });

  const entry = await pvp.findSyncCode(code);
  if (!entry) {
    await interaction.editReply('That link code is not valid. Run `/link` in game for a new one.');
    return;
  }
  if (entry.expiresAt > 0 && entry.expiresAt < Date.now()) {
    await interaction.editReply('That link code has expired. Run `/link` in game for a new one.');
    return;
  }
  if (links.isCodeUsed(code)) {
    await interaction.editReply('That link code has already been used. Run `/link` in game for a new one.');
    return;
  }

  const taken = links.getLinkByUuid(entry.uuid);
  if (taken && taken.discord_id !== interaction.user.id) {
    await interaction.editReply(
      'That Minecraft account is already linked to another Discord user. They need to `/unlink` first.'
    );
    return;
  }

  const name = entry.name ?? (await pvp.findStats(entry.uuid))?.username ?? 'your account';
  links.link(interaction.user.id, entry.uuid, name, code);
  const roleNote = await setLinkedRole(interaction, true);
  await interaction.editReply(`Linked! Your Discord account is now connected to **${name}**.${roleNote}`);
}

async function handleUnsync(interaction) {
  await interaction.deferReply({ flags: MessageFlags.Ephemeral });

  const link = links.getLink(interaction.user.id);
  if (!link) {
    await interaction.editReply('Your Discord account is not linked to a Minecraft account.');
    return;
  }

  links.unlink(interaction.user.id);
  await setLinkedRole(interaction, false);
  await interaction.editReply(`Unlinked from **${link.player_name ?? link.player_uuid}**.`);
}

/**
 * Gives or removes the optional "linked" role (LINKED_ROLE_ID).
 *
 * Returns a short note for the reply when the role could not be applied, so a missing permission
 * is visible to the player instead of silently doing nothing. The link itself never depends on it.
 */
async function setLinkedRole(interaction, grant) {
  const roleId = process.env.LINKED_ROLE_ID?.trim();
  if (!roleId || !interaction.inGuild()) {
    return '';
  }

  try {
    const member = await interaction.guild.members.fetch(interaction.user.id);
    if (grant) {
      await member.roles.add(roleId, 'Linked Minecraft account');
    } else {
      await member.roles.remove(roleId, 'Unlinked Minecraft account');
    }
    return '';
  } catch (error) {
    console.warn(`Could not ${grant ? 'give' : 'remove'} the linked role: ${error.message}`);
    return grant
      ? '\nYour link is saved, but I could not give you the linked role. Ask a staff member to check my permissions.'
      : '';
  }
}


function loadIpCommandConfig() {
  let yaml = '';
  const configPath = process.env.DISCORD_CONFIG_PATH || new URL('../../src/main/resources/core/discord.yml', import.meta.url);
  try {
    yaml = readFileSync(configPath, 'utf8');
  } catch (error) {
    console.warn(`Could not read discord.yml for !ip settings: ${error.message}`);
  }

  const section = yaml.match(/^IP-COMMAND:\\s*\\n((?: {2,}.*(?:\\n|$))*)/m)?.[1] ?? '';
  const value = (key, fallback) => {
    const match = section.match(new RegExp(`^  ${key}:\\s*(.*?)\\s*import 'dotenv/config';
import { readFileSync } from 'node:fs';
import { EmbedBuilder, Client, Events, GatewayIntentBits, MessageFlags } from 'discord.js';
import { queryMinecraftServer } from './server-status.js';
import { createPlayerRepository, leaderboardTypes } from './database.js';
import { createPvpRepository, tierLeaderboardTypes } from './pvp.js';
import { createLinkStore } from './links.js';
import { createLeaderboardEmbed, createStatsEmbed } from './embeds.js';
import { createCompetitiveStatsEmbed, createTierLeaderboardEmbed } from './pvp-embeds.js';
import { createMatchWatcher } from './match-watcher.js';
import { registerCommands } from './register-commands.js';

const token = process.env.DISCORD_TOKEN;

if (!token) {
  throw new Error('DISCORD_TOKEN is required.');
}

const repository = await createPlayerRepository();
const pvp = await createPvpRepository();
const links = createLinkStore();
const ipCommand = loadIpCommandConfig();
const client = new Client({ intents: [GatewayIntentBits.Guilds, GatewayIntentBits.GuildMessages, GatewayIntentBits.MessageContent] });
const matchWatcher = createMatchWatcher({ client, repository: pvp, links });

await registerCommands();

client.once(Events.ClientReady, async readyClient => {
  console.log(`Logged in as ${readyClient.user.tag}.`);
  await matchWatcher.start();
});

client.on(Events.MessageCreate, async message => {
  if (message.author.bot || !ipCommand.enabled || message.content.trim().toLowerCase() !== ipCommand.command.toLowerCase()) {
    return;
  }

  const address = ipCommand.address;
  const port = ipCommand.port;
  const embed = new EmbedBuilder()
    .setColor(ipCommand.color)
    .setTitle(ipCommand.serverName)
    .setDescription('Minecraft server information')
    .addFields(
      { name: 'Server IP', value: `\u0060${address}${port === 25565 ? '' : `:${port}`}\u0060`, inline: false }
    )
    .setFooter({ text: ipCommand.footer })
    .setTimestamp();

  if (!address || address.toLowerCase() === 'change_me') {
    embed.addFields(
      { name: 'Players', value: 'Unavailable', inline: true },
      { name: 'Minecraft Version', value: 'Unavailable', inline: true },
      { name: 'Ping', value: 'Unavailable', inline: true }
    );
    await message.reply({ embeds: [embed] });
    return;
  }

  try {
    const result = await queryMinecraftServer(address, port);
    const players = result.status.players ?? {};
    const rawVersion = String(result.status.version?.name ?? '');
    const version = rawVersion.match(/\b1\.\d+(?:\.\d+)?\b/)?.[0] ?? 'Unknown';
    embed.addFields(
      { name: 'Players', value: `${players.online ?? 0}/${players.max ?? 0}`, inline: true },
      { name: 'Minecraft Version', value: version, inline: true },
      { name: 'Ping', value: `${result.ping} ms`, inline: true }
    );
  } catch (error) {
    console.warn(`Minecraft status query failed: ${error.message}`);
    embed.addFields(
      { name: 'Players', value: '0/0', inline: true },
      { name: 'Minecraft Version', value: 'Unavailable', inline: true },
      { name: 'Ping', value: 'Offline', inline: true }
    );
  }

  await message.reply({ embeds: [embed] });
});

client.on(Events.InteractionCreate, async interaction => {
  if (!interaction.isChatInputCommand()) {
    return;
  }

  try {
    switch (interaction.commandName) {
      case 'stats':
        await handleStats(interaction);
        return;
      case 'leaderboard':
        await handleLeaderboard(interaction);
        return;
      case 'mystats':
        await handleMyStats(interaction);
        return;
      case 'sync':
      case 'link':
        await handleSync(interaction);
        return;
      case 'unsync':
      case 'unlink':
        await handleUnsync(interaction);
        return;
      case 'tier':
        await handleTier(interaction);
        return;
      default:
    }
  } catch (error) {
    console.error(error);
    const message = 'Failed to read the EconomySMP database.';
    if (interaction.deferred || interaction.replied) {
      await interaction.editReply({ content: message, embeds: [] });
    } else {
      await interaction.reply({ content: message, flags: MessageFlags.Ephemeral });
    }
  }
});

async function handleStats(interaction) {
  const playerInput = interaction.options.getString('player', true);
  await interaction.deferReply();

  const player = await repository.findPlayer(playerInput);
  if (!player) {
    await interaction.editReply(`No player found for \`${playerInput}\`.`);
    return;
  }

  await interaction.editReply({ embeds: [createStatsEmbed(player, interaction.user.username)] });
}

async function handleLeaderboard(interaction) {
  const typeKey = interaction.options.getString('type', true);
  const limit = interaction.options.getInteger('limit') ?? 10;
  const type = leaderboardTypes[typeKey];

  await interaction.deferReply();

  if (!type) {
    await interaction.editReply(`Unknown leaderboard type: \`${typeKey}\`.`);
    return;
  }

  const entries = await repository.getLeaderboard(typeKey, limit);
  if (entries.length === 0) {
    await interaction.editReply('No leaderboard data found.');
    return;
  }

  await interaction.editReply({ embeds: [createLeaderboardEmbed(typeKey, entries, interaction.user.username)] });
}

// ── Ranked arena ─────────────────────────────────────────────────────────────

async function handleMyStats(interaction) {
  await interaction.deferReply();

  const link = links.getLink(interaction.user.id);
  if (!link) {
    await interaction.editReply(
      'Your Discord account is not linked yet. Run `/pvp sync` in game, then `/sync` here with the code it gives you.'
    );
    return;
  }

  await replyWithCompetitiveStats(interaction, link.player_uuid, link.player_name);
}

async function handleTier(interaction) {
  const subcommand = interaction.options.getSubcommand();
  if (subcommand === 'stats') {
    await interaction.deferReply();
    await replyWithCompetitiveStats(interaction, interaction.options.getString('player', true));
    return;
  }

  const typeKey = interaction.options.getString('type', true);
  const limit = interaction.options.getInteger('limit') ?? 10;
  await interaction.deferReply();

  if (!tierLeaderboardTypes[typeKey]) {
    await interaction.editReply(`Unknown leaderboard type: \`${typeKey}\`.`);
    return;
  }

  const entries = (await pvp.getTierLeaderboard(typeKey, limit)).filter(Boolean);
  if (entries.length === 0) {
    await interaction.editReply('Nobody has fought a ranked match yet.');
    return;
  }

  await interaction.editReply({
    embeds: [createTierLeaderboardEmbed(typeKey, entries, interaction.user.username)]
  });
}

async function replyWithCompetitiveStats(interaction, lookup, fallbackName) {
  const stats = await pvp.findStats(lookup);
  if (!stats) {
    await interaction.editReply(`No ranked record found for \`${fallbackName ?? lookup}\`.`);
    return;
  }

  const record = await pvp.getRecord(stats.uuid);
  await interaction.editReply({
    embeds: [createCompetitiveStatsEmbed(stats, record, interaction.user.username)]
  });
}

/**
 * Claims a Minecraft account with a code issued in game.
 *
 * The reply is ephemeral throughout: a code that is still valid should not stay readable in a
 * public channel, and neither should the failure that tells somebody a code was wrong.
 */
async function handleSync(interaction) {
  const code = interaction.options.getString('code', true).trim().toLowerCase();
  await interaction.deferReply({ flags: MessageFlags.Ephemeral });

  const entry = await pvp.findSyncCode(code);
  if (!entry) {
    await interaction.editReply('That link code is not valid. Run `/link` in game for a new one.');
    return;
  }
  if (entry.expiresAt > 0 && entry.expiresAt < Date.now()) {
    await interaction.editReply('That link code has expired. Run `/link` in game for a new one.');
    return;
  }
  if (links.isCodeUsed(code)) {
    await interaction.editReply('That link code has already been used. Run `/link` in game for a new one.');
    return;
  }

  const taken = links.getLinkByUuid(entry.uuid);
  if (taken && taken.discord_id !== interaction.user.id) {
    await interaction.editReply(
      'That Minecraft account is already linked to another Discord user. They need to `/unlink` first.'
    );
    return;
  }

  const name = entry.name ?? (await pvp.findStats(entry.uuid))?.username ?? 'your account';
  links.link(interaction.user.id, entry.uuid, name, code);
  const roleNote = await setLinkedRole(interaction, true);
  await interaction.editReply(`Linked! Your Discord account is now connected to **${name}**.${roleNote}`);
}

async function handleUnsync(interaction) {
  await interaction.deferReply({ flags: MessageFlags.Ephemeral });

  const link = links.getLink(interaction.user.id);
  if (!link) {
    await interaction.editReply('Your Discord account is not linked to a Minecraft account.');
    return;
  }

  links.unlink(interaction.user.id);
  await setLinkedRole(interaction, false);
  await interaction.editReply(`Unlinked from **${link.player_name ?? link.player_uuid}**.`);
}

/**
 * Gives or removes the optional "linked" role (LINKED_ROLE_ID).
 *
 * Returns a short note for the reply when the role could not be applied, so a missing permission
 * is visible to the player instead of silently doing nothing. The link itself never depends on it.
 */
async function setLinkedRole(interaction, grant) {
  const roleId = process.env.LINKED_ROLE_ID?.trim();
  if (!roleId || !interaction.inGuild()) {
    return '';
  }

  try {
    const member = await interaction.guild.members.fetch(interaction.user.id);
    if (grant) {
      await member.roles.add(roleId, 'Linked Minecraft account');
    } else {
      await member.roles.remove(roleId, 'Unlinked Minecraft account');
    }
    return '';
  } catch (error) {
    console.warn(`Could not ${grant ? 'give' : 'remove'} the linked role: ${error.message}`);
    return grant
      ? '\nYour link is saved, but I could not give you the linked role. Ask a staff member to check my permissions.'
      : '';
  }
}

, 'm'));
    if (!match) return fallback;
    return match[1].replace(/^['"]|['"]$/g, '');
  };
  const address = process.env.MINECRAFT_SERVER_ADDRESS || value('SERVER-ADDRESS', '');
  const port = Number(process.env.MINECRAFT_SERVER_PORT || value('SERVER-PORT', '25565'));
  const colorText = value('COLOR', '#009BFF').replace('#', '');
  const color = /^[0-9a-fA-F]{6}$/.test(colorText) ? Number.parseInt(colorText, 16) : 0x009bff;

  return {
    enabled: value('ENABLED', 'true').toLowerCase() === 'true',
    command: value('COMMAND', '!ip'),
    serverName: value('SERVER-NAME', 'LinearSMP'),
    address,
    port: Number.isInteger(port) && port > 0 && port <= 65535 ? port : 25565,
    color,
    footer: value('FOOTER', 'Made By ArchiveAustxn')
  };
}

function shutdown() {
  matchWatcher.stop();
  repository.close?.();
  pvp.close?.();
  links.close?.();
  client.destroy();
}

process.once('SIGINT', shutdown);
process.once('SIGTERM', shutdown);

await client.login(token);
