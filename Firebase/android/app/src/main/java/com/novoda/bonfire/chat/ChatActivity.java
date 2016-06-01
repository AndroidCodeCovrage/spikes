package com.novoda.bonfire.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.novoda.bonfire.BaseActivity;
import com.novoda.bonfire.Dependencies;
import com.novoda.bonfire.R;
import com.novoda.bonfire.channel.data.model.Channel;
import com.novoda.bonfire.chat.displayer.ChatDisplayer;
import com.novoda.bonfire.chat.presenter.ChatPresenter;
import com.novoda.bonfire.navigation.AndroidNavigator;

public class ChatActivity extends BaseActivity {

    private static final String NAME_EXTRA = "channel_name";
    private static final String ACCESS_EXTRA = "channel_access";
    private ChatPresenter presenter;

    public static Intent createIntentFor(Context context, Channel channel) {
        Intent intent = new Intent(context, ChatActivity.class);

        intent.putExtra(NAME_EXTRA, channel.getName());
        intent.putExtra(ACCESS_EXTRA, channel.getAccess());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chatView);
        presenter = new ChatPresenter(
                Dependencies.INSTANCE.getLoginService(),
                Dependencies.INSTANCE.getChatService(),
                chatDisplayer,
                getIntent().getStringExtra(NAME_EXTRA),
                (Channel.Access) getIntent().getSerializableExtra(ACCESS_EXTRA),
                Dependencies.INSTANCE.getFirebaseAnalytics(),
                new AndroidNavigator(this)
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
