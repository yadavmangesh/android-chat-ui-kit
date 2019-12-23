package com.cometchat.pro.androiduikit;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.models.User;

import constant.StringContract;
import screen.CometChatMessageScreen;

public class ChatActivity extends AppCompatActivity {

    private Fragment fragment;

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         if (getIntent()!=null) {
             Bundle bundle = new Bundle();
             fragment=new CometChatMessageScreen();
             bundle.putString(StringContract.IntentStrings.AVATAR, getIntent().getStringExtra(StringContract.IntentStrings.AVATAR));
             bundle.putString(StringContract.IntentStrings.NAME, getIntent().getStringExtra(StringContract.IntentStrings.NAME));
             bundle.putString(StringContract.IntentStrings.TYPE,getIntent().getStringExtra(StringContract.IntentStrings.TYPE));

              if (getIntent().getStringExtra(StringContract.IntentStrings.TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {
                  bundle.putString(StringContract.IntentStrings.UID, getIntent().getStringExtra(StringContract.IntentStrings.UID));
                  bundle.putString(StringContract.IntentStrings.STATUS, getIntent().getStringExtra(StringContract.IntentStrings.STATUS));
            }else {
                  bundle.putString(StringContract.IntentStrings.GUID, getIntent().getStringExtra(StringContract.IntentStrings.GUID));
              }
              fragment.setArguments(bundle);
             getSupportFragmentManager().beginTransaction().replace(R.id.ChatFragment,fragment).commit();
         }
    }



    @Override
    protected void onResume() {
        super.onResume();
        CometChat.addUserListener(TAG, new CometChat.UserListener() {
            @Override
            public void onUserOnline(User user) {
                Log.d(TAG, "onUserOnline: "+user.toString());

            }

            @Override
            public void onUserOffline(User user) {
                Log.d(TAG, "onUserOffline: "+user.toString());

            }
        });
    }
}
