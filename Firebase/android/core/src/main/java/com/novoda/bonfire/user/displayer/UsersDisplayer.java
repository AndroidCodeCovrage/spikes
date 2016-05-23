package com.novoda.bonfire.user.displayer;

import com.novoda.bonfire.user.data.model.User;
import com.novoda.bonfire.user.data.model.Users;

public interface UsersDisplayer {

    void attach(SelectionListener selectionListener);

    void detach(SelectionListener selectionListener);

    void display(Users users);

    void showFailure();

    interface SelectionListener {

        void onUserSelected(User user);

        void onCompleteClicked();

        SelectionListener NO_OP = new SelectionListener() {
            @Override
            public void onUserSelected(User user) {
                // empty implementation
            }

            @Override
            public void onCompleteClicked() {
                // empty implementation
            }
        };
    }
}
