package com.cometchat.pro.androiduikit;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import utils.FontUtils;

public class DemoMain extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(Constant.REGION).build();
        new FontUtils(this);
        CometChat.init(this, Constant.APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(CometChatException e) {
                Toast.makeText(DemoMain.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
