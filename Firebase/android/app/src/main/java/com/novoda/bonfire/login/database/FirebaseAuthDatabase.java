package com.novoda.bonfire.login.database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.novoda.bonfire.login.data.model.Authentication;
import com.novoda.bonfire.user.data.model.User;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

public class FirebaseAuthDatabase implements AuthDatabase {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthDatabase(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Observable<Authentication> readAuthentication() {
        return Observable.create(new Observable.OnSubscribe<Authentication>() {
            @Override
            public void call(Subscriber<? super Authentication> subscriber) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    subscriber.onNext(authenticationFrom(currentUser));
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Authentication> loginWithGoogle(final String idToken) {
        return getAuthenticationForCredential(new Func0<AuthCredential>() {
            @Override
            public AuthCredential call() {
                return GoogleAuthProvider.getCredential(idToken, null);
            }
        });
    }

    @Override
    public Observable<Authentication> loginWithTwitter(final String token, final String secret) {
        return getAuthenticationForCredential(new Func0<AuthCredential>() {
            @Override
            public AuthCredential call() {
                return TwitterAuthProvider.getCredential(token, secret);
            }
        });
    }

    @NonNull
    private Observable<Authentication> getAuthenticationForCredential(final Func0<AuthCredential> credentialGenerator) {
        return Observable.create(new Observable.OnSubscribe<Authentication>() {
            @Override
            public void call(final Subscriber<? super Authentication> subscriber) {
                AuthCredential credential = credentialGenerator.call();
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = task.getResult().getUser();
                                    subscriber.onNext(authenticationFrom(firebaseUser));
                                } else {
                                    subscriber.onNext(new Authentication(task.getException()));
                                }
                                subscriber.onCompleted();
                            }
                        });
            }
        });
    }

    private Authentication authenticationFrom(FirebaseUser currentUser) {
        Uri photoUrl = currentUser.getPhotoUrl();
        return new Authentication(new User(currentUser.getUid(), currentUser.getDisplayName(), photoUrl == null ? "" : photoUrl.toString()));
    }

}
