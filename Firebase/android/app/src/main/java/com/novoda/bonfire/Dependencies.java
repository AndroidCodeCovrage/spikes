package com.novoda.bonfire;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.novoda.bonfire.analytics.Analytics;
import com.novoda.bonfire.analytics.FirebaseAnalyticsAnalytics;
import com.novoda.bonfire.channel.database.FirebaseChannelsDatabase;
import com.novoda.bonfire.channel.service.ChannelService;
import com.novoda.bonfire.channel.service.PersistedChannelService;
import com.novoda.bonfire.chat.database.FirebaseChatDatabase;
import com.novoda.bonfire.chat.service.ChatService;
import com.novoda.bonfire.chat.service.PersistedChatService;
import com.novoda.bonfire.login.database.FirebaseAuthDatabase;
import com.novoda.bonfire.login.service.FirebaseLoginService;
import com.novoda.bonfire.login.service.LoginService;
import com.novoda.bonfire.user.database.FirebaseUserDatabase;
import com.novoda.bonfire.user.service.PersistedUserService;
import com.novoda.bonfire.user.service.UserService;

public enum Dependencies {
    INSTANCE;

    private Analytics analytics;
    private LoginService loginService;
    private ChatService chatService;
    private ChannelService channelService;
    private UserService userService;

    public void init(Context context) {
        if (needsInitialisation()) {
            Context appContext = context.getApplicationContext();
            FirebaseApp firebaseApp = FirebaseApp.initializeApp(appContext, FirebaseOptions.fromResource(appContext), "Bonfire");
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
            firebaseDatabase.setPersistenceEnabled(true);
            FirebaseUserDatabase userDatabase = new FirebaseUserDatabase(firebaseDatabase);

            analytics = new FirebaseAnalyticsAnalytics(context);
            loginService = new FirebaseLoginService(new FirebaseAuthDatabase(firebaseAuth), userDatabase);
            chatService = new PersistedChatService(new FirebaseChatDatabase(firebaseDatabase));
            channelService = new PersistedChannelService(new FirebaseChannelsDatabase(firebaseDatabase), userDatabase);
            userService = new PersistedUserService(userDatabase);
        }
    }

    private boolean needsInitialisation() {
        return loginService == null || chatService == null || channelService == null || analytics == null;
    }

    public Analytics getAnalytics() {
        return analytics;
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public ChatService getChatService() {
        return chatService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public UserService getUserService() {
        return userService;
    }
}
