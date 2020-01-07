package screen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.User;

import constant.StringContract;

public class CometChatMessageListActivity extends AppCompatActivity {

    private Fragment fragment;

    private static final String TAG = "CometChatMessageListAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometchat_message_list);

         if (getIntent()!=null) {
             Bundle bundle = new Bundle();
             fragment=new CometChatMessageScreen();
             bundle.putString(StringContract.IntentStrings.AVATAR, getIntent().getStringExtra(StringContract.IntentStrings.AVATAR));
             bundle.putString(StringContract.IntentStrings.NAME, getIntent().getStringExtra(StringContract.IntentStrings.NAME));
             bundle.putString(StringContract.IntentStrings.TYPE,getIntent().getStringExtra(StringContract.IntentStrings.TYPE));

              if (getIntent().hasExtra(StringContract.IntentStrings.TYPE)&&getIntent().getStringExtra(StringContract.IntentStrings.TYPE).equals(CometChatConstants.RECEIVER_TYPE_USER)) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: ");

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
