package screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.R;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.databinding.ActivityCometchatBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constant.StringContract;
import listeners.ConversationItemClickListener;
import listeners.CustomAlertDialogHelper;
import listeners.GroupItemClickListener;
import listeners.OnAlertDialogButtonClickListener;
import listeners.UserItemClickListener;

public class CometChatUnified extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,OnAlertDialogButtonClickListener {

    private ActivityCometchatBinding binding;

    private static final String TAG = CometChatUnified.class.getSimpleName();

    private List<String> unreadCount = new ArrayList<>();

    private int count = 0;

    private BadgeDrawable badgeDrawable;

    private Fragment fragment;

    private User loggedInUser = CometChat.getLoggedInUser();

    private ProgressDialog progressDialog;

    private String groupPassword;

    private Group group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cometchat);

        initViewComponent();

        Log.d(TAG, "onCreate: ");

        CometChatUserListScreen.setItemClickListener(new UserItemClickListener() {
            @Override
            public void setItemClickListener(User user, int position) {
                Toast.makeText(CometChatUnified.this, "Clicked on User: " + user.getName(), Toast.LENGTH_SHORT).show();
                startUserIntent(user);
            }
        });
        CometChatGroupListScreen.setItemClickListener(new GroupItemClickListener() {
            @Override
            public void setItemClickListener(Group g, int position) {
                Toast.makeText(CometChatUnified.this, "Clicked on Group: " + g.getName(), Toast.LENGTH_SHORT).show();
                 group = g;
                if (group.isJoined()) {
                    startGroupIntent(group);
                    Log.d(TAG, "onGroupItemClicked: " + group.toString());
                } else {
                    if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                        View dialogview = getLayoutInflater().inflate(R.layout.cc_dialog, null);
                        TextView tvTitle = dialogview.findViewById(R.id.textViewDialogueTitle);
                        tvTitle.setText("");
                        new CustomAlertDialogHelper(CometChatUnified.this, "Password", dialogview, "Join",
                                "", "Cancel", CometChatUnified.this, 1, false);
                    } else if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {
                        joinGroup(group);
                    }
                }
            }
        });

        CometChatConversationListScreen.setItemClickListener(new ConversationItemClickListener() {
            @Override
            public void setItemClickListener(Conversation conversation, int position) {
                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP))
                    startGroupIntent(((Group) conversation.getConversationWith()));
                else
                    startUserIntent(((User) conversation.getConversationWith()));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUnreadConversationCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unreadCount.clear();
    }

    private void initViewComponent() {

        badgeDrawable = binding.bottomNavigation.getOrCreateBadge(R.id.menu_conversation);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);

        badgeDrawable.setVisible(false);

        loadFragment(new CometChatConversationListScreen());

//        findViewById(R.id.menu_button).setOnClickListener(
//                view -> CometChat.logout(new CometChat.CallbackListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                finish();
//            }
//
//            @Override
//            public void onError(CometChatException e) {
//                Toast.makeText(CometChatActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        }));

    }

    public void getUnreadConversationCount() {
        CometChat.getUnreadMessageCount(new CometChat.CallbackListener<HashMap<String, HashMap<String, Integer>>>() {
            @Override
            public void onSuccess(HashMap<String, HashMap<String, Integer>> stringHashMapHashMap) {

                unreadCount.addAll(stringHashMapHashMap.get("user").keySet());
                unreadCount.addAll(stringHashMapHashMap.get("group").keySet());

                if (unreadCount.size() == 0) {
                    badgeDrawable.setVisible(false);
                } else {
                    badgeDrawable.setVisible(true);
                }
                Log.e(TAG, "onSuccess: " + count);
                badgeDrawable.setNumber(unreadCount.size());
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("onError: ", e.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addConversationListener();

    }

    private void setUnreadCount(BaseMessage message) {

            if (message.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
                if (!unreadCount.contains(message.getReceiverUid())) {
                    unreadCount.add(message.getReceiverUid());
                    setBadge();
                }
            } else {

                if (!unreadCount.contains(message.getSender().getUid())) {
                    unreadCount.add(message.getSender().getUid());
                    setBadge();
                }
            }
    }

    private void setBadge(){
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(badgeDrawable.getNumber() + 1);
    }

    public void addConversationListener() {
        CometChat.addMessageListener(TAG, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage message) {
                setUnreadCount(message);

            }

            @Override
            public void onMediaMessageReceived(MediaMessage message) {
                setUnreadCount(message);
            }

            @Override
            public void onCustomMessageReceived(CustomMessage message) {
                setUnreadCount(message);
            }
        });
    }



    private void startUserIntent(User user) {
        Intent intent = new Intent(CometChatUnified.this, CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.UID, user.getUid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(StringContract.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(StringContract.IntentStrings.NAME, user.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }

    private void startGroupIntent(Group group) {

        Intent intent = new Intent(CometChatUnified.this, CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
        intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_users) {
            fragment = new CometChatUserListScreen();
        } else if (itemId == R.id.menu_group) {
            fragment = new CometChatGroupListScreen();
        } else if (itemId == R.id.menu_conversation) {
          fragment = new CometChatConversationListScreen();
        } else if (itemId == R.id.menu_more)
        { fragment = new CometChatUserInfoScreen();
        }

        return loadFragment(fragment);
    }

    private void joinGroup(Group group) {
        progressDialog = ProgressDialog.show(this, "", "Joining");
        progressDialog.setCancelable(false);
        CometChat.joinGroup(group.getGuid(), group.getGroupType(), groupPassword, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
                Log.d(TAG, "joinGroup onSuccess: " + group.toString());
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                startGroupIntent(group);
            }

            @Override
            public void onError(CometChatException e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toast.makeText(CometChatUnified.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            return true;
        }
        return false;
    }


    @Override
    public void onButtonClick(AlertDialog alertDialog, View v, int which, int popupId) {
        EditText groupPasswordInput = (EditText) v.findViewById(R.id.edittextDialogueInput);
        if (which == DialogInterface.BUTTON_NEGATIVE) { // Cancel
            alertDialog.dismiss();
        } else if (which == DialogInterface.BUTTON_POSITIVE) { // Join
            try {
                progressDialog = ProgressDialog.show(this, "", "Joining");
                progressDialog.setCancelable(false);
                groupPassword = groupPasswordInput.getText().toString();
                if (groupPassword.length() == 0) {
                    groupPasswordInput.setText("");
                    groupPasswordInput.setError("Incorrect");

                } else {
                    try {
                        alertDialog.dismiss();
                        joinGroup(group);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}