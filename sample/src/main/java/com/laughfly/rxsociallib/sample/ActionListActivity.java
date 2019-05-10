package com.laughfly.rxsociallib.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laughfly.rxsociallib.RxSocial;
import com.laughfly.rxsociallib.exception.SocialException;
import com.laughfly.rxsociallib.login.SocialLoginResult;
import com.laughfly.rxsociallib.share.ShareBuilder;
import com.laughfly.rxsociallib.share.SocialShareResult;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.laughfly.rxsociallib.share.ShareType.SHARE_APP;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_AUDIO;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_FILE;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_IMAGE;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_LOCAL_VIDEO;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_MINI_PROGRAM;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_MULTI_FILE;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_MULTI_IMAGE;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_NETWORK_VIDEO;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_TEXT;
import static com.laughfly.rxsociallib.share.ShareType.SHARE_WEB;

/**
 * Created by caowy on 2019/4/18.
 * email:cwy.fly2@gmail.com
 */

public class ActionListActivity extends Activity {

    static final int LOGIN_TYPE = 0;

    static int[] ALL_SHARE_TYPES = {SHARE_TEXT, SHARE_WEB, SHARE_IMAGE,
        SHARE_MULTI_IMAGE, SHARE_AUDIO, SHARE_LOCAL_VIDEO,
        SHARE_NETWORK_VIDEO, SHARE_FILE, SHARE_MULTI_FILE, SHARE_MINI_PROGRAM, SHARE_APP};

    SparseIntArray mTypeIcons = new SparseIntArray();
    {
        mTypeIcons.put(LOGIN_TYPE, R.mipmap.ic_login);
        mTypeIcons.put(SHARE_TEXT, R.mipmap.ic_share_text);
        mTypeIcons.put(SHARE_WEB, R.mipmap.ic_share_web);
        mTypeIcons.put(SHARE_IMAGE, R.mipmap.ic_share_image);
        mTypeIcons.put(SHARE_MULTI_IMAGE, R.mipmap.ic_share_image);
        mTypeIcons.put(SHARE_AUDIO, R.mipmap.ic_share_audio);
        mTypeIcons.put(SHARE_LOCAL_VIDEO, R.mipmap.ic_share_video);
        mTypeIcons.put(SHARE_NETWORK_VIDEO, R.mipmap.ic_share_video);
        mTypeIcons.put(SHARE_FILE, R.mipmap.ic_share_file);
        mTypeIcons.put(SHARE_MULTI_FILE, R.mipmap.ic_share_file);
        mTypeIcons.put(SHARE_MINI_PROGRAM, R.mipmap.ic_share_miniprogram);
        mTypeIcons.put(SHARE_APP, R.mipmap.ic_share_app);

    }

    SparseArray<String> mTypeNames = new SparseArray<>();
    {
        mTypeNames.put(LOGIN_TYPE, "登录");
        mTypeNames.put(SHARE_TEXT, "分享文本");
        mTypeNames.put(SHARE_WEB, "分享网页");
        mTypeNames.put(SHARE_IMAGE, "分享图片");
        mTypeNames.put(SHARE_MULTI_IMAGE, "分享多图");
        mTypeNames.put(SHARE_AUDIO, "分享音频");
        mTypeNames.put(SHARE_LOCAL_VIDEO, "分享本地视频");
        mTypeNames.put(SHARE_NETWORK_VIDEO, "分享网络视频");
        mTypeNames.put(SHARE_FILE, "分享文件");
        mTypeNames.put(SHARE_MULTI_FILE, "分享多个文件");
        mTypeNames.put(SHARE_MINI_PROGRAM, "分享小程序");
        mTypeNames.put(SHARE_APP, "分享APP");
    }

    RecyclerView mRecyclerView;

    LayoutInflater mLayoutInflater;

    String mPlatform;

    List<Integer> mSupportTypes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        mPlatform = getIntent().getStringExtra("platform");
        mLayoutInflater = getLayoutInflater();

        boolean supportLogin = RxSocial.getSupportLogin(mPlatform);
        if(supportLogin) {
            mSupportTypes.add(LOGIN_TYPE);
        }

        int shareTypes = RxSocial.getShareTypes(mPlatform);
        for (int type : ALL_SHARE_TYPES) {
            if((shareTypes & type) == type) {
                mSupportTypes.add(type);
            }
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        mRecyclerView.setAdapter(new ARecyclerViewAdapter());
    }

    private void onActionClick(int type) {
        if(LOGIN_TYPE == type) {
            doLogin();
        } else {
            doShare(type);
        }
    }

    private void doLogin() {
        RxSocial.loginBuilder()
            .setPlatform(mPlatform)
            .setClearLastAccount(false)
            .setSaveAccessToken(false)
            .setLogoutOnly(false)
            .setServerSideMode(true)
            .setFetchUserProfile(false)
            .build()
            .toObservable()
            .subscribe(new rx.Observer<SocialLoginResult>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    handleLoginFail(e);
                }

                @Override
                public void onNext(SocialLoginResult socialLoginResult) {
                    handleLoginSuccess(socialLoginResult);
                }
            });
    }

    private void handleLoginFail(Throwable e) {
        int errorCode = e instanceof SocialException ? ((SocialException) e).getErrCode() : 0;
        Toast.makeText(ActionListActivity.this, "登录失败: " + errorCode, Toast.LENGTH_SHORT).show();
    }

    private void handleLoginSuccess(SocialLoginResult result) {
        if(result.logoutOnly) {
            Toast.makeText(ActionListActivity.this, "注销成功: " + result.platform, Toast.LENGTH_SHORT).show();
        } else {
            String nameOrId = result.userInfo != null ? result.userInfo.nickname : result.openId;
            Toast.makeText(ActionListActivity.this, "登录成功: " + nameOrId, Toast.LENGTH_SHORT).show();
        }
    }

    private void doShare(int type) {
        ShareBuilder builder = null;
        switch (type) {
            case SHARE_TEXT:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type));
                break;
            case SHARE_WEB:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setWebUrl(TestData.getWebUrl(type))
                    .setThumbUri(TestData.getThumbUri(type));
                break;
            case SHARE_IMAGE:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setImageUri(TestData.getImageUri(type));
                break;
            case SHARE_MULTI_IMAGE:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setImageList(TestData.getImageList());
                break;
            case SHARE_AUDIO:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setWebUrl(TestData.getWebUrl(type))
                    .setThumbUri(TestData.getThumbUri(type))
                    .setAudioUri(TestData.getAudioUri());
                break;
            case SHARE_LOCAL_VIDEO:
            case SHARE_NETWORK_VIDEO:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setVideoUri(TestData.getVideoUri(type));
                break;
            case SHARE_FILE:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setFileUri(TestData.getFileUri());
                break;
            case SHARE_MULTI_FILE:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setFileList(TestData.getFileList());
                break;
            case SHARE_APP:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setThumbUri(TestData.getThumbUri(type))
                    .setAppInfo(TestData.getAppInfo());
                break;
            case SHARE_MINI_PROGRAM:
                builder = RxSocial.shareBuilder()
                    .setPlatform(mPlatform)
                    .setTitle(TestData.getTitle(type))
                    .setText(TestData.getText(type))
                    .setThumbUri(TestData.getThumbUri(type))
                    .setWebUrl(TestData.getWebUrl(type))
                    .setMiniProgramPath(TestData.getMiniProgramPath())
                    .setMiniProgramUserName(TestData.getMiniProgramUser())
                    .setMiniProgramType(TestData.getMiniProgramType());
                break;
        }
        if(builder != null) {
            builder.build().toObservable2()
                .subscribe(new Observer<SocialShareResult>() {

                    @Override
                    public void onSubscribe(final Disposable d) {
                    }

                    @Override
                    public void onNext(SocialShareResult result) {
                        handleShareSuccess(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handleShareFail(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        }
    }

    private void handleShareFail(Throwable e) {
        int errorCode = e instanceof SocialException ? ((SocialException) e).getErrCode() : 0;
        Toast.makeText(ActionListActivity.this, "分享失败: " + errorCode, Toast.LENGTH_SHORT).show();
    }

    private void handleShareSuccess(SocialShareResult result) {
        Toast.makeText(ActionListActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
    }

    class ARecyclerViewAdapter extends RecyclerView.Adapter<ARecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.view_action, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Integer type = mSupportTypes.get(position);
            String name = mTypeNames.get(type);
            holder.itemView.setTag(type);

            holder.name.setText(name);
            holder.icon.setImageResource(mTypeIcons.get(type));
        }

        @Override
        public int getItemCount() {
            return mSupportTypes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.action_name);
                icon = itemView.findViewById(R.id.action_icon);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onActionClick((Integer) v.getTag());
                    }
                });
            }
        }
    }
}
