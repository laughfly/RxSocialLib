package com.laughfly.rxsociallib.internal;

import com.laughfly.rxsociallib.SocialCallback;

import io.reactivex.Flowable;
import rx.Observable;

/**
 * Execute login and share.
 * Created by caowy on 2019/4/29.
 * email:cwy.fly2@gmail.com
 */

public abstract class SocialExecutor<Action extends SocialAction, Params extends SocialParams, Result extends SocialResult> {
    private Action mSocialAction;

    protected abstract Action createAction();

    public void start(SocialCallback<Params, Result> callback){
        Action action = createAction();
        action.setCallback(callback);
        action.start();
        mSocialAction = action;
    }

    public void cancel() {
        if (mSocialAction != null) {
            mSocialAction.cancel();
        }
    }

    public Observable<Result> toObservable() {
        Action action = createAction();
        return RxConverter.toObservable(action);
    }

    public io.reactivex.Observable<Result> toObservable2() {
        Action action = createAction();
        return RxConverter.toObservable2(action);
    }

    public Flowable<Result> toFlowable() {
        Action action = createAction();
        return RxConverter.toFlowable(action);
    }
}
