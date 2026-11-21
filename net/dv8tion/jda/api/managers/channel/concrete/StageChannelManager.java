package net.dv8tion.jda.api.managers.channel.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.managers.channel.attribute.IAgeRestrictedChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.ISlowmodeChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.AudioChannelManager;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildChannelManager;

public interface StageChannelManager extends AudioChannelManager<StageChannel, StageChannelManager>, StandardGuildChannelManager<StageChannel, StageChannelManager>, IAgeRestrictedChannelManager<StageChannel, StageChannelManager>, ISlowmodeChannelManager<StageChannel, StageChannelManager> {
}
