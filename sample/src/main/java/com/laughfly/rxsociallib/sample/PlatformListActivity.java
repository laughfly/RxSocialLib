package com.laughfly.rxsociallib.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laughfly.rxsociallib.RxSocial;
import com.laughfly.rxsociallib.SocialUriUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by caowy on 2019/4/17.
 * email:cwy.fly2@gmail.com
 */

public class PlatformListActivity extends Activity {

    RecyclerView mRecyclerView;

    LayoutInflater mLayoutInflater;

    String[] ALL_PLATFORMS = new String[]{"Weibo", "WeiboStory", "Wechat", "WechatMoments", "QQ", "QQZone"};

    HashMap<String, String> mPlatformNames = new HashMap<>();
    {
        mPlatformNames.put("QQ", "QQ");
        mPlatformNames.put("QQZone", "QQ空间");
        mPlatformNames.put("Weibo", "微博");
        mPlatformNames.put("WeiboStory", "微博故事");
        mPlatformNames.put("Wechat", "微信");
        mPlatformNames.put("WechatMoments", "微信朋友圈");
    }

    HashMap<String, Integer> mPlatformIcons = new HashMap<>();
    {
        mPlatformIcons.put("QQ", R.mipmap.ic_qq);
        mPlatformIcons.put("QQZone", R.mipmap.ic_qqzone);
        mPlatformIcons.put("Weibo", R.mipmap.ic_weibo);
        mPlatformIcons.put("WeiboStory", R.mipmap.ic_weibostory);
        mPlatformIcons.put("Wechat", R.mipmap.ic_wechat);
        mPlatformIcons.put("WechatMoments", R.mipmap.ic_wechatmoments);
    }

    List<String> mPlatforms = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_platform_list);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        mRecyclerView = findViewById(R.id.recycler_view);

        mLayoutInflater = getLayoutInflater();

        Set<String> sharePlatforms = RxSocial.getSharePlatforms();
        Set<String> loginPlatforms = RxSocial.getLoginPlatforms();
        for (String platform : ALL_PLATFORMS) {
            if(sharePlatforms.contains(platform) || loginPlatforms.contains(platform)) {
                mPlatforms.add(platform);
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        mRecyclerView.setAdapter(new ARecyclerViewAdapter());
    }

    private void showActionList(String platform) {
        Intent intent = new Intent(this, ActionListActivity.class);
        intent.putExtra("platform", platform);
        startActivity(intent);
    }

    class ARecyclerViewAdapter extends RecyclerView.Adapter<ARecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.view_platform, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String platform = mPlatforms.get(position);
            String name = mPlatformNames.get(platform);
            int iconRes = mPlatformIcons.get(platform);
            holder.itemView.setTag(platform);
            holder.text.setText(name);
            holder.icon.setImageResource(iconRes);
        }

        @Override
        public int getItemCount() {
            return mPlatforms.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView text;
            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.platform_name);
                icon = itemView.findViewById(R.id.platform_icon);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showActionList((String) v.getTag());
                    }
                });
            }
        }
    }
}
