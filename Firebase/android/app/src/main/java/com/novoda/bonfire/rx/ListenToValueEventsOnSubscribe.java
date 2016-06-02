package com.novoda.bonfire.rx;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.BooleanSubscription;

class ListenToValueEventsOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private final DatabaseReference databaseReference;
    private final Func1<DataSnapshot, T> marshaller;

    ListenToValueEventsOnSubscribe(DatabaseReference databaseReference, Func1<DataSnapshot, T> marshaller) {
        this.databaseReference = databaseReference;
        this.marshaller = marshaller;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        final ValueEventListener eventListener = databaseReference.addValueEventListener(new RxValueListener<>(subscriber, marshaller));
        subscriber.add(BooleanSubscription.create(new Action0() {
            @Override
            public void call() {
                databaseReference.removeEventListener(eventListener);
            }
        }));
    }

    private static class RxValueListener<T> implements ValueEventListener {

        private final Subscriber<? super T> subscriber;
        private final Func1<DataSnapshot, T> marshaller;

        RxValueListener(Subscriber<? super T> subscriber, Func1<DataSnapshot, T> marshaller) {
            this.subscriber = subscriber;
            this.marshaller = marshaller;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(marshaller.call(dataSnapshot));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            subscriber.onError(databaseError.toException()); //TODO handle errors in pipeline
        }

    }

}
