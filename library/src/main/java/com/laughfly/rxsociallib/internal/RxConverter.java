package com.laughfly.rxsociallib.internal;

import com.laughfly.rxsociallib.SocialCallback;
import com.laughfly.rxsociallib.exception.SocialException;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Cancellable;

/**
 * Created by caowy on 2019/4/29.
 * email:cwy.fly2@gmail.com
 */

public class RxConverter {
    public static<Params, Result> Observable<Result> toObservable(SocialAction action) {
        return Observable.create(new Action1<Emitter<Result>>() {
            @Override
            public void call(Emitter<Result> emitter) {
                emitter.setCancellation(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Params, Result> callback = new SocialCallback<Params, Result>() {
                    @Override
                    public void onError(String platform, Params params, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Params params, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform, Params params) {
                        emitter.onCompleted();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        }, Emitter.BackpressureMode.LATEST);
    }

    public static<Params, Result> io.reactivex.Observable<Result> toObservable2(SocialAction action) {
        return io.reactivex.Observable.create(new ObservableOnSubscribe<Result>() {
            @Override
            public void subscribe(ObservableEmitter<Result> emitter) throws Exception {
                emitter.setCancellable(new io.reactivex.functions.Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Params, Result> callback = new SocialCallback<Params, Result>() {
                    @Override
                    public void onError(String platform, Params params, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Params params, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform, Params params) {
                        emitter.onComplete();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        });
    }

    public static<Params, Result> Flowable<Result> toFlowable(SocialAction action) {
        return Flowable.create(new FlowableOnSubscribe<Result>() {
            @Override
            public void subscribe(FlowableEmitter<Result> emitter) throws Exception {
                emitter.setCancellable(new io.reactivex.functions.Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        action.cancel();
                    }
                });
                SocialCallback<Params, Result> callback = new SocialCallback<Params, Result>() {
                    @Override
                    public void onError(String platform, Params params, SocialException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onSuccess(String platform, Params params, Result resp) {
                        emitter.onNext(resp);
                    }

                    @Override
                    public void onFinish(String platform, Params params) {
                        emitter.onComplete();
                    }
                };
                action.setCallback(callback);
                action.start();
            }
        }, BackpressureStrategy.ERROR);
    }
}
