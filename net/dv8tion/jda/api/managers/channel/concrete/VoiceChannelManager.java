package net.dv8tion.jda.api.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IAgeRestrictedChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildChannelManager;

public interface VoiceChannelManager extends AudioChannelManager<VoiceChannel, VoiceChannelManager>, StandardGuildChannelManager<VoiceChannel, VoiceChannelManager>, IAgeRestrictedChannelManager<VoiceChannel, VoiceChannelManager>, ISlowmodeChannelManager<VoiceChannel, VoiceChannelManager> {
}
