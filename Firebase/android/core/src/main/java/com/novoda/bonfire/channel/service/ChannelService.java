package com.novoda.bonfire.channel.service;

import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.login.data.model.User;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    void createChannel(String channelName, boolean isPrivate);
}
