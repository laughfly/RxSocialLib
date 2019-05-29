package com.laughfly.rxsociallib.platform.google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.laughfly.rxsociallib.AccessToken;
import com.laughfly.rxsociallib.SocialConstants;
import com.laughfly.rxsociallib.delegate.DefaultDelegateActivity;
import com.laughfly.rxsociallib.exception.SocialLoginException;
import com.laughfly.rxsociallib.login.LoginAction;
import com.laughfly.rxsociallib.login.LoginFeature;
import com.laughfly.rxsociallib.login.LoginFeatures;
import com.laughfly.rxsociallib.login.LoginResult;
import com.laughfly.rxsociallib.login.UserInfo;

/**
 * Created by caowy on 2019/4/26.
 * email:cwy.fly2@gmail.com
 */

@LoginFeatures({
    @LoginFeature(platform = "Google")
})
public class GoogleLogin extends LoginAction {

    private GoogleSignInClient mGoogleSignInClient;

    public GoogleLogin() {
        super();
    }

    @Override
    protected void init() throws Exception {
        String scope = mParams.getScope();
        if (TextUtils.isEmpty(scope)) {
            scope = GoogleConstants.SCOPE_DEFAULT;
        }
        String[] scopes = scope.trim().split(GoogleConstants.SCOPE_SPLIT);

        Scope _scope = new Scope(scopes[0]);
        Scope[] _scopes = new Scope[scopes.length > 1 ? scopes.length - 1 : 0];
        for (int i = 1; i < scopes.length; i++) {
            _scopes[i - 1] = new Scope(scopes[i]);
        }
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder();
        if (mParams.isServerSideMode()) {
            builder.requestServerAuthCode(mParams.getAppId());
        } else {
            builder.requestIdToken(mParams.getAppId());
        }
        GoogleSignInOptions signInOptions = builder.requestScopes(_scope, _scopes).build();

        mGoogleSignInClient = GoogleSignIn.getClient(mParams.getContext(), signInOptions);
    }

    @Override
    protected void execute() throws Exception {
        DefaultDelegateActivity delegate = getDelegate();
        if (mParams.isLogoutOnly()) {
            logoutOnly();
        } else {
            startLogin(delegate);
        }
    }

    @Override
    protected void release() throws Exception {

    }

    private void startLogin(final DefaultDelegateActivity activity) {
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(mParams.getContext());
        if (mParams.isClearLastAccount() && lastSignedInAccount != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    signIn(activity);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finishWithError(e);
                }
            });
        } else {
            signIn(activity);
        }
    }

    private void logoutOnly() {
        Task<Void> task = mGoogleSignInClient.signOut();
        task.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                LoginResult loginResult = new LoginResult();
                loginResult.platform = getPlatform();
                loginResult.logoutOnly = true;
                finishWithSuccess(loginResult);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finishWithError(e);
            }
        });
    }

    private void signIn(DefaultDelegateActivity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, GoogleConstants.REQUEST_SIGNIN);
    }

    @Override
    public void handleResult(int requestCode, int resultCode, Intent data) {
        if (GoogleConstants.REQUEST_SIGNIN == requestCode) {
            try {
                handleSignInResult(data);
            } catch (Exception e2) {
                e2.printStackTrace();
                finishWithError(e2);
            }
        } else {
            finishWithNoResult();
        }
    }

    private void handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);
            if (account != null) {
                LoginResult loginResult = new LoginResult();
                loginResult.resultObject = account;
                loginResult.openId = account.getId();

                if (!TextUtils.isEmpty(account.getDisplayName())) {
                    UserInfo userInfo = new UserInfo();
                    userInfo.nickname = account.getDisplayName();
                    userInfo.avatarUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;
                    loginResult.userInfo = userInfo;
                }

                if (mParams.isServerSideMode()) {
                    loginResult.serverAuthCode = account.getServerAuthCode();
                } else {
                    AccessToken accessToken = new AccessToken();
                    accessToken.openId = account.getId();
                    accessToken.accessToken = account.getIdToken();
                    loginResult.accessToken = accessToken;

                    if (mParams.isSaveAccessToken()) {
                        saveAccessToken(accessToken);
                    }
                }
                finishWithSuccess(loginResult);
            } else {
                finishWithNoResult();
            }
        } catch (ApiException e) {
            e.printStackTrace();
            finishWithError(new SocialLoginException(getPlatform(), SocialConstants.ERR_REQUEST_FAIL, e.getStatusCode(), e.getMessage(), e));
        }
    }


}
