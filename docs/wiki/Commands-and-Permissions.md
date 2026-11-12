# Commands & Permissions Reference

This page contains the complete reference guide for all commands, aliases, syntax, descriptions, and permission nodes provided by **EconomySMP**.

---

## Player Commands & Permissions

| Command | Usage Syntax | Aliases | Description | Permission Node |
| :--- | :--- | :--- | :--- | :--- |
| `/team` | `/team <create\|disband\|invite\|kick\|join\|leave\|home\|sethome\|delhome\|chat\|info\|pvp>` | None | Team management. `/team info <team>` opens a read-only roster of any team on the server, everything else acts on your own team | `economysmp.command.team` |
| `/msg` | `/msg <player> <message>` | `/message`, `/tell`, `/whisper`, `/w` | Send private message to player | `economysmp.command.msg` |
| `/reply` | `/reply <message>` | `/r` | Reply to last private message | `economysmp.command.reply` |
| `/pm` | `/pm` | `/togglepm`, `/privatemessages` | Toggle private messaging on/off | `economysmp.command.pm` |
| `/ignore` | `/ignore <player\|list>` | None | Ignore messages from a player | `economysmp.command.ignore` |
| `/unignore` | `/unignore <player>` | None | Unignore a player | `economysmp.command.unignore` |
| `/home` | `/home [name]` | None | Teleport to a saved home | `economysmp.command.home` |
| `/homes` | `/homes` | None | Open menu or list saved homes | `economysmp.command.homes` |
| `/sethome` | `/sethome [name]` | None | Save current position as home | `economysmp.command.sethome` |
| `/delhome` | `/delhome <name>` | None | Delete a saved home | `economysmp.command.delhome` |
| `/renamehome`| `/renamehome <old> <new>` | None | Rename a saved home | `economysmp.command.renamehome` |
| `/spawn` | `/spawn` | None | Teleport to server spawn | `economysmp.command.spawn` |
| `/afk` | `/afk` | None | Teleport to or enter AFK reward zone | `economysmp.command.afk` |
| `/rtp` | `/rtp [world]` | None | Random teleport into the wilderness | `economysmp.command.rtp` |
| `/rtpq` | `/rtpq [join\|leave]` | `/rtpqueue` | Queue up to be random teleported into the wilderness together with other waiting players | `economysmp.command.rtpq` |
| `/balance` | `/balance [player]` | `/bal`, `/money` | Check money balance | `economysmp.command.balance` |
| `/pay` | `/pay <player> <amount>` | None | Pay money to another player | `economysmp.command.pay` |
| `/shards` | `/shards [player]` | None | Check shard balance | `economysmp.command.shards` |
| `/shardpay` | `/shardpay <player> <amount>` | None | Pay shards to another player | `economysmp.command.shardpay` |
| `/shop` | `/shop` | None | Open GUI shop | `economysmp.command.shop` |
| `/shopedit` | `/shopedit <menu>` | None | Edit a shop menu in game | `economysmp.admin.shop` |
| `/sell` | `/sell` | None | Open GUI sell container | `economysmp.command.sell` |
| `/sellhand` | `/sellhand [amount]` | None | Sell item currently held in hand | `economysmp.command.sellhand` |
| `/sellall` | `/sellall` | None | Sell all sellable items in inventory | `economysmp.command.sellall` |
| `/sellhistory`| `/sellhistory` | None | View personal sell transaction history | `economysmp.command.sellhistory` |
| `/sellmulti` | `/sellmulti [category]` | None | Open sell multiplier menu | `economysmp.command.sellmulti` |
| `/sellmultiplier` | `/sellmultiplier [category]` | None | Open sell multiplier menu | `economysmp.command.sellmulti` |
| `/sellprogress` | `/sellprogress [category]` | None | Open sell multiplier progress menu | `economysmp.command.sellprogress` |
| `/worth` | `/worth [hand]` | `/prices` | Check worth of held item or open price catalog | `economysmp.command.worth` |
| `/meta` | `/meta` | `/farmingmeta` | Show the item that is currently the farming meta | `economysmp.command.meta` |
| `/auctionhouse`| `/auctionhouse [sell\|my\|claims\|history]`| `/ah` | Open Auction House marketplace | `economysmp.command.auctionhouse` |
| `/orders` | `/orders [my\|collect]` | None | Open buy/sell Orders board | `economysmp.command.orders` |
| `/enderchest` | `/enderchest` | `/ec` | Open custom Ender Chest | `economysmp.command.enderchest` |
| `/crates` | `/crates` | None | Open crates overview menu | `economysmp.command.crates` |
| `/duel` | `/duel [player\|accept\|deny\|claims]` | None | Challenge a player or respond to duel | `economysmp.command.duel` |
| `/queue` | `/queue [join\|leave]` | None | Join or leave duel match queues | `economysmp.command.queue` |
| `/draw` | `/draw` | None | Offer or accept draw in active duel | `economysmp.command.draw` |
| `/leave` | `/leave` | None | Leave active duel or FFA instance | `economysmp.command.leave` |
| `/ffa` | `/ffa [join]` | None | Join instanced FFA arena | `economysmp.command.ffa` |
| `/ffastats` | `/ffastats [player]` | None | View FFA kill/death/streak stats | `economysmp.command.ffastats` |
| `/pvp` | `/pvp [join\|leave\|kit\|stats\|top\|queue\|leaderboard\|history\|sync]` | None | Join the ranked PvP arena, queue for a 1v1, browse leaderboards and match history, or get a Discord sync code | `economysmp.command.pvp` |
| `/bounty` | `/bounty [place\|list]` | None | Place or view player bounties | `economysmp.command.bounty` |
| `/leaderboard` | `/leaderboard [type]` | `/lb`, `/top`, `/leaderboards`, `/baltop` | Open leaderboard menus; `/baltop` opens the money leaderboard directly | `economysmp.command.leaderboard` |
| `/voicechatconsent` | `/voicechatconsent [revoke]` | `/vcconsent`, `/voiceconsent` | Open the voice chat consent menu, or withdraw an answer already given with `revoke` | `economysmp.command.voicechatconsent` |

---

## Staff & Moderation Commands

| Command | Usage Syntax | Description | Permission Node |
| :--- | :--- | :--- | :--- |
| `/teleport` | `/teleport <player\|here <player>\|all\|top\|offline <player>\|x y z [world]>` (Aliases `/tp`, `/tphere`, `/tpall`) | Staff teleport suite, including teleporting to an offline player's last known location | `economysmp.staff.teleport` |
| `/tpo` | `/tpo <player>` (Alias `/tpoffline`) | Teleport directly to an offline player's last known location | `economysmp.staff.teleport.offline` |
| `/staffmode` | `/staffmode` (Alias `/staff`) | Toggle Staff Mode GUI & toolset | `economysmp.admin.staffmode` |
| `/vanish` | `/vanish` | Toggle complete invisibility to players | `economysmp.admin.vanish` |
| `/freeze` | `/freeze <player>` | Freeze or unfreeze a target player | `economysmp.admin.freeze` |
| `/invsee` | `/invsee <player>` | Inspect and edit player inventory in real-time | `economysmp.admin.invsee` |
| `/ecsee` | `/ecsee <player>` | Inspect and edit player Ender Chest | `economysmp.admin.ecsee` |
| `/seehomes` | `/seehomes <player>` (Alias `/homesee`) | Browse another player's homes and warp to any of them | `economysmp.staff.seehomes` |
| `/chat` | `/chat <mute\|unmute\|delay\|clear>` | Global chat moderation controls | `economysmp.admin.chat` |
| `/logs` | `/logs <player>` | Browse one player's activity log, chat included | `economysmp.admin.logs` |
| `/chatlog` | `/chatlog [player]` | Browse public chat for the whole server, or for one player | `economysmp.admin.chatlog` |
| `/spawnstash` | `/spawnstash <give\|setup\|list>` (Alias `/stash`) | Manage spawn stash bait chests | `economysmp.admin.spawnstash` |
| `/fakeplayer` | `/fakeplayer` (Alias `/fplayer`) | Spawn fake player bait entities | `economysmp.command.fakeplayer` |
| `/amod` | `/amod <add\|reload>` | Manage the anvil rename word filter | `economysmp.command.amod` |
| `/offend` | `/offend <player> <reason> [time]` | Issue preset offense-based punishment with escalating duration | `economysmp.staff.punishments.offend` |
| `/punishments` | `/punishments [player]` | Browse every punishment on the server, or one player's history | `economysmp.staff.punishments.view` |
| `/ban` | `/ban <player> [reason]` | Issue permanent ban | `economysmp.staff.punishments.ban` |
| `/tempban` | `/tempban <player> <time> [reason]` | Issue temporary ban | `economysmp.staff.punishments.ban` |
| `/mute` | `/mute <player> [reason]` | Issue permanent mute | `economysmp.staff.punishments.mute` |
| `/tempmute` | `/tempmute <player> <time> [reason]` | Issue temporary mute | `economysmp.staff.punishments.mute` |
| `/vcmute` | `/vcmute <player> [reason]` | Mute a player in voice chat, leaving text chat alone | `economysmp.staff.vcmute` |
| `/warn` | `/warn <player> [reason]` | Issue formal warning | `economysmp.staff.punishments.create` |
| `/kick` | `/kick <player> [reason]` | Kick online player from server | `economysmp.staff.punishments.create` |
| `/blacklist` | `/blacklist <player> [reason]` | Issue IP/account blacklist | `economysmp.staff.punishments.blacklist` |
| `/unban` | `/unban <player> [reason]` | Remove active ban | `economysmp.staff.punishments.unban` |
| `/unmute` | `/unmute <player> [reason]` | Remove active mute | `economysmp.staff.punishments.unmute` |
| `/vcunmute` | `/vcunmute <player> [reason]` | Remove active voice chat mute | `economysmp.staff.vcunmute` |
| `/unblacklist` | `/unblacklist <player> [reason]` | Remove active blacklist | `economysmp.staff.punishments.unblacklist` |

---

## Administrator & Setup Commands

| Command | Usage Syntax | Description | Permission Node |
| :--- | :--- | :--- | :--- |
| `/cuboid` | `/cuboid <wand\|create <name>\|delete <name>\|list\|setspawn <name>\|delspawn <name>\|bind ...>` | Region selection & feature binding | `economysmp.admin.cuboid` |
| `/portal` | `/portal <create\|delete\|list\|setcuboid\|setdestination>` | Custom portal trigger setup | `economysmp.admin.portal` |
| `/arena` | `/arena <create\|delete\|setpos1\|setpos2\|setreturn\|enable>` | Duel arena setup and configuration | `economysmp.admin.arena` |
| `/ffaarena` | `/ffaarena <create\|delete\|setpos\|enable>` | Instanced FFA arena management | `economysmp.admin.ffaarena` |
| `/pvp` | `/pvp <wand\|create\|setspawn\|setspawn2\|setlobby\|setboundary\|kit\|schematic\|assign\|reset\|reload>` | Ranked PvP arena setup, kit editing, assigned matches, and schematic resets | `economysmp.admin.pvp` |
| `/crate` | `/crate <create\|delete\|key\|keyall\|bind\|edit>` | Crate & virtual key administration | `economysmp.admin.crate` |
| `/spawner` | `/spawner [give\|info\|panel\|reload\|remove\|split]` | Custom spawner stack administration. `info` and `split` are open to every player; the rest needs the admin node | `economysmp.admin.spawner` |
| `/addmoney` | `/addmoney <player> <amount>` | Add money to player balance | `economysmp.admin.addmoney` |
| `/removemoney` | `/removemoney <player> <amount>` | Deduct money from player balance | `economysmp.admin.removemoney` |
| `/setmoney` | `/setmoney <player> <amount>` | Set player money balance | `economysmp.admin.setmoney` |
| `/topsell` | `/topsell [gui\|items\|volume\|sellers\|export]` | Admin economy metrics and sell analytics | `economysmp.admin.topsell` |
| `/booster` | `/booster <give\|list>` | Give global server shard/money boosters | `economysmp.admin.booster` |
| `/billford` | `/billford <gui\|reload>` | Manage Billford rotating trades NPC | `economysmp.admin.billford` |
| `/serverwipe` | `/serverwipe <confirm\|cancel>` | Guarded admin server wipe execution | `economysmp.admin.serverwipe` |
| `/playerwipe` | `/playerwipe <player> [confirm]` | Wipe everything stored about one player | `economysmp.admin.playerwipe` |
| `/playerunwipe` | `/playerunwipe <player> [confirm]` | Restore a wiped player from the backup their wipe left behind | `economysmp.admin.playerunwipe` |
| `/uds` | `/uds <reload\|version\|status>` | Main plugin administration & hot-reload | `economysmp.admin.uds` |

---

## Punishment Exemption Permissions

Holding `/offend`, `/ban` or `/mute` says nothing about who the command may be used on, so a rank
handed one of those nodes can otherwise punish anybody on the server, the owner included. Give a rank
the exempt node and staff who lack the bypass are turned away instead.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.admin.punishments.exempt` | `op` | The player cannot be punished. `/offend`, `/ban`, `/tempban`, `/mute`, `/tempmute`, `/vcmute`, `/warn`, `/kick` and `/blacklist` all refuse to touch them. |
| `economysmp.admin.punishments.exempt.bypass` | `op` | Punish players who hold the exempt node anyway. Give it to the ranks that are allowed to act on each other. |

Both nodes sit under `economysmp.admin.` rather than beside the other punishment nodes on
purpose. A server that hands its moderators `economysmp.staff.punishments.*` would otherwise
make every one of them unpunishable. `economysmp.admin.*` grants both, so an admin rank keeps
its protection and can still act on other admins.

The check reads the target's permissions off the player, which only works while they are online.
Punishing someone who is offline goes through as it always has, so an exempt player can still be
banned in absentia.

The refusal message is `PUNISHMENTS.TARGET-EXEMPT` in `messages.yml`.

---

## Spawner Permissions

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.spawner.bypass` | `false` | Break spawners without a Silk Touch pickaxe while `SETTINGS.REQUIRE_SILK_TOUCH` is enabled in `spawners.yml`. Registered with `default: false`, so operators do not receive it automatically. Assign it explicitly via LuckPerms. |
| `economysmp.command.spawner` | `true` | Reach `/spawner` at all. Every player has it, which is what makes `/spawner info` and `/spawner split` usable; the admin subcommands stay behind their own node. |
| `economysmp.admin.spawner` | `op` | Run `/spawner give`, `panel`, `reload`, `remove` and `forcebreak`, and see the full `/spawner info` breakdown on any spawner regardless of who owns it. |

---

## RTP Rank Permissions

These nodes are not registered in `plugin.yml` and are read straight off the player, so they work with
LuckPerms or any other permission plugin. Assign them per rank.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.rtp.cooldown.<seconds>` | `false` | Override the `/rtp` cooldown for this player, in seconds. `economysmp.rtp.cooldown.3` gives a 3 second cooldown, and `economysmp.rtp.cooldown.0` removes the cooldown entirely. |
| `economysmp.rtp.priority.<weight>` | `false` | Position in the RTP waiting queue when all slots are busy. Higher weight is served first. |

When a player holds more than one cooldown node the **lowest** value wins, so stacked ranks always give
the player the fastest cooldown they are entitled to. A player with no cooldown node falls back to the
per-world `WORLD-SETTINGS.<world>.COOLDOWN` value in `rtp.yml`.

Named rank nodes can be mapped to a cooldown instead of using numeric nodes. See
`SETTINGS.RANK-COOLDOWNS.PERMISSIONS` in [Config-rtp.yml](Config-rtp.yml) — the shipped defaults map
`economysmp.rtp.cooldown.vip`, `.vip+` and `.vip++` to 15, 10 and 3 seconds.

Set `SETTINGS.RANK-COOLDOWNS.ENABLED: false` in `rtp.yml` to ignore every cooldown permission and use
the per-world value for everyone.

---

## Home Limit Permissions

These nodes are not registered in `plugin.yml` and are read straight off the player, so they work with
LuckPerms or any other permission plugin. Assign them per rank.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.homes.<1-100>` | `false` | How many homes the player may save. `economysmp.homes.10` allows ten homes. |
| `economysmp.homes.page.<1-100>` | `false` | The same limit expressed in menu pages, five homes each. `economysmp.homes.page.3` allows fifteen homes. |

When a player holds more than one node the **highest** value wins, so a player with both `.homes.5`
and `.homes.20` gets 20 homes, and mixing the two styles is fine — `.homes.page.2` and `.homes.12`
together give 12. A player with no home node falls back to `SETTINGS.HOME-DEFAULT` in `config.yml`,
which ships as 2.

The limit applies everywhere a home is created, so `/sethome`, the `/homes` menu and the Bedrock form
all stop at the same number, and the menu paginates to fit whatever the player is entitled to.

Wildcards do not grant a limit. `economysmp.*` leaves the player on `HOME-DEFAULT`, the same way
it does for `economysmp.enderchest.rows.<1-6>`, so a staff wildcard cannot quietly hand every
player 100 homes.

Named rank nodes can be mapped to a home count instead of using numeric nodes. See
`SETTINGS.HOME-PERMISSIONS.PERMISSIONS` in [Config-config.yml](Config-config.yml) — the shipped
defaults map `economysmp.homes.vip`, `.vip+` and `.vip++` to 5, 10 and 15 homes.

Set `SETTINGS.HOME-PERMISSIONS.ENABLED: false` in `config.yml` to ignore every home permission and
give everyone `HOME-DEFAULT`.

---

## Home & Team Blacklist Bypass Permissions

These nodes allow players (such as staff or administrators) to set personal homes or team homes in worlds excluded by `SETTINGS.HOME-EXCLUDED-WORLDS` or `TEAM.EXCLUDED-WORLDS` in `config.yml`.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.homes.bypass` | `op` | Bypass personal home world exclusions (`/sethome`, `/homes` menu, Bedrock form) and arena restrictions. |
| `economysmp.teams.bypass` | `op` | Bypass team home world exclusions (`/team sethome`, `/team` menu, Bedrock form) and arena restrictions. |

---

## Ender Chest Size Permissions

These nodes are not registered in `plugin.yml` and are read straight off the player, so they work with
LuckPerms or any other permission plugin. Assign them per rank.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.enderchest.rows.<1-6>` | `false` | Size of the player's Ender Chest, in rows. `economysmp.enderchest.rows.3` gives 27 slots, the same as a vanilla Ender Chest, and `economysmp.enderchest.rows.6` gives the full 54. |

One row is 9 slots, so the six tiers are 9, 18, 27, 36, 45 and 54 slots. When a player holds more than
one node the **highest** value wins, so a player with both `.rows.2` and `.rows.5` gets 45 slots. A
value above 6 is treated as 6. A player with no row node falls back to `ENDER-CHEST.DEFAULT-ROWS` in
`ender-chest.yml`, which ships as 6 — lower it if you want these permissions to hand out bigger chests
as a rank perk.

Wildcards do not grant a tier. `economysmp.*` leaves the player on the default size, the same way
it does for `economysmp.homes.<1-100>`, so a staff wildcard cannot quietly resize everyone.

Named rank nodes can be mapped to a row count instead of using numeric nodes. See
`ENDER-CHEST.ROW-PERMISSIONS.PERMISSIONS` in [Config-ender-chest.yml](Config-ender-chest.yml) — the
shipped defaults map `economysmp.enderchest.rows.vip`, `.vip+` and `.vip++` to 4, 5 and 6 rows.

Set `ENDER-CHEST.ROW-PERMISSIONS.ENABLED: false` in `ender-chest.yml` to ignore every row permission
and give everyone `DEFAULT-ROWS`.

---

## Auction Listing Limit Permissions

These nodes are not registered in `plugin.yml` and are read straight off the player, so they work with
LuckPerms or any other permission plugin. Assign them per rank.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `economysmp.auctionhouse.limit.<1-100>` | `false` | How many auctions the player may have active at once. `economysmp.auctionhouse.limit.15` allows fifteen listings. |
| `donutauction.limit.<1-100>` | `false` | The same limit under the short alias prefix, for servers carrying an older node layout. |

The number is a total, not a bonus added to the default. When a player holds more than one node the
**highest** value wins, so a player with both `.limit.10` and `.limit.25` gets 25. A player with no
limit node falls back to `SETTINGS.MAX_ACTIVE_LISTINGS_DEFAULT` in `auction-house.yml`, which ships as
5, and `/ah limit` prints a player's active count against whatever they are entitled to.

Wildcards do not grant a limit. `economysmp.*` leaves the player on `MAX_ACTIVE_LISTINGS_DEFAULT`,
the same way it does for `economysmp.homes.<1-100>`, so a staff wildcard cannot quietly hand every
player 100 listings.

Named rank nodes can be mapped to a listing count instead of using numeric nodes. See
`SETTINGS.MAX_ACTIVE_LISTINGS_BY_PERMISSION` in [Config-auction-house.yml](Config-auction-house.yml) — map a
node there to a negative number to give it unlimited listings.

---

## Media Rank & Badge Permissions

Media permissions are registered with `default: false` and require explicit assignment via LuckPerms (or explicit permission attachment). Being an OP player does not automatically grant media badge status.

| Permission Node | Default | Description |
| :--- | :--- | :--- |
| `rank.media` | `false` | Display configurable Media tablist badge. Must be assigned explicitly via LuckPerms. |
| `rank.media.plus` | `false` | Display configurable Media+ tablist badge. Must be assigned explicitly via LuckPerms. |
| `rank.media.include` | `false` | Include player in media badge handling. Must be assigned explicitly via LuckPerms. |
