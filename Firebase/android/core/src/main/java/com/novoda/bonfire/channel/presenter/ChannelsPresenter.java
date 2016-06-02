package com.novoda.bonfire.channel.presenter;

import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.channel.data.model.Channels;
import com.novoda.bonfire.channel.displayer.ChannelsDisplayer;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.navigation.Navigator;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class ChannelsPresenter {

    private final ChannelsDisplayer channelsDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final Navigator navigator;

    private Subscription subscription;

    public ChannelsPresenter(ChannelsDisplayer channelsDisplayer, ChannelService channelService, LoginService loginService, Navigator navigator) {
        this.channelsDisplayer = channelsDisplayer;
        this.channelService = channelService;
        this.loginService = loginService;
        this.navigator = navigator;
    }

    public void startPresenting() {
        channelsDisplayer.attach(channelsInteractionListener);
        subscription = loginService.getAuthentication()
                .filter(successfullyAuthenticated())
                .flatMap(channelsForUser())
                .subscribe(new Action1<Channels>() {
                    @Override
                    public void call(Channels channels) {
                        channelsDisplayer.display(channels);
                    }
                });
    }

    private Func1<Authentication, Observable<Channels>> channelsForUser() {
        return new Func1<Authentication, Observable<Channels>>() {
            @Override
            public Observable<Channels> call(Authentication authentication) {
                return channelService.getChannelsFor(authentication.getUser());
            }
        };
    }

    private Func1<Authentication, Boolean> successfullyAuthenticated() {
        return new Func1<Authentication, Boolean>() {
            @Override
            public Boolean call(Authentication authentication) {
                return authentication.isSuccess();
            }
        };
    }

    public void stopPresenting() {
        subscription.unsubscribe();
        channelsDisplayer.detach(channelsInteractionListener);
    }

    private final ChannelsDisplayer.ChannelsInteractionListener channelsInteractionListener = new ChannelsDisplayer.ChannelsInteractionListener() {
        @Override
        public void onChannelSelected(Channel channel) {
            navigator.toChannel(channel);
        }

        @Override
        public void onAddNewChannel() {
            navigator.toCreateChannel();
        }

    };
}
