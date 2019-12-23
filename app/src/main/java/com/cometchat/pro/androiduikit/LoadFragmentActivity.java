package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.androiduikit.ComponentFragments.AvatarFragment;
import com.cometchat.pro.androiduikit.ComponentFragments.BadgeCountFragment;
import com.cometchat.pro.androiduikit.ComponentFragments.ConversationListViewFragment;
import com.cometchat.pro.androiduikit.ComponentFragments.GroupListViewFragment;
import com.cometchat.pro.androiduikit.ComponentFragments.StatusIndicatorFragment;
import com.cometchat.pro.androiduikit.ComponentFragments.UserListViewFragment;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;

import constant.StringContract;
import listeners.ConversationItemClickListener;
import listeners.CustomAlertDialogHelper;
import listeners.GroupItemClickListener;
import listeners.OnAlertDialogButtonClickListener;
import listeners.UserItemClickListener;
import screen.CometChatConversationListScreen;
import screen.CometChatMessageListActivity;
import screen.CometChatGroupListScreen;
import screen.CometChatUserInfoScreen;
import screen.CometChatUserListScreen;

public class LoadFragmentActivity extends AppCompatActivity implements  OnAlertDialogButtonClickListener {

    private ProgressDialog progressDialog;
    private String groupPassword;
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_fragment);
        int id = getIntent().getIntExtra("screen", 0);
        if (id == R.id.users) {
            loadFragment(new CometChatUserListScreen());

        } else if (id == R.id.groups) {
            loadFragment(new CometChatGroupListScreen());
        } else if (id == R.id.conversations) {
            loadFragment(new CometChatConversationListScreen());
        } else if (id == R.id.moreinfo) {
            loadFragment(new CometChatUserInfoScreen());
        } else if (id== R.id.cc_avatar)
        {
            loadFragment(new AvatarFragment());
        } else if (id== R.id.cc_status_indicator)
        {
            loadFragment(new StatusIndicatorFragment());
        } else if (id== R.id.cc_badge_count)
        {
            loadFragment(new BadgeCountFragment());
        } else if (id== R.id.cc_user_view)
        {
            loadFragment(new UserListViewFragment());
        } else if (id== R.id.cc_group_view)
        {
            loadFragment(new GroupListViewFragment());
        } else if (id== R.id.cc_conversation_view)
        {
            loadFragment(new ConversationListViewFragment());
        }
        CometChatUserListScreen.setItemClickListener(new UserItemClickListener() {
            @Override
            public void setItemClickListener(User user, int position) {
                userIntent(user);
            }
        });

        CometChatGroupListScreen.setItemClickListener(new GroupItemClickListener() {
            @Override
            public void setItemClickListener(Group group, int position) {
                if (group.isJoined()) {
                    startGroupIntent(group);
                } else {
                    if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PASSWORD)) {
                        View dialogview = getLayoutInflater().inflate(R.layout.cc_dialog, null);
                        TextView tvTitle = dialogview.findViewById(R.id.textViewDialogueTitle);
                        tvTitle.setText("");
                        new CustomAlertDialogHelper(LoadFragmentActivity.this, "Password", dialogview, "Join",
                                "", "Cancel", LoadFragmentActivity.this, 1, false);
                    } else if (group.getGroupType().equals(CometChatConstants.GROUP_TYPE_PUBLIC)) {
                        joinGroup(group);
                    }
                }
            }
        });

        CometChatConversationListScreen.setItemClickListener(new ConversationItemClickListener() {
            @Override
            public void setItemClickListener(Conversation conversation, int position) {

                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP)) {
                    startGroupIntent(((Group) conversation.getConversationWith()));
                } else {
                    User user = ((User) conversation.getConversationWith());
                    Intent intent = new Intent(LoadFragmentActivity.this, CometChatMessageListActivity.class);
                    intent.putExtra(StringContract.IntentStrings.UID, user.getUid());
                    intent.putExtra(StringContract.IntentStrings.AVATAR, user.getAvatar());
                    intent.putExtra(StringContract.IntentStrings.STATUS, user.getStatus());
                    intent.putExtra(StringContract.IntentStrings.NAME, user.getName());
                    intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                    startActivity(intent);
                }
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




    public void userIntent(User user) {
        Intent intent = new Intent(LoadFragmentActivity.this, CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.UID, user.getUid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, user.getAvatar());
        intent.putExtra(StringContract.IntentStrings.STATUS, user.getStatus());
        intent.putExtra(StringContract.IntentStrings.NAME, user.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
        startActivity(intent);
    }

    private void startGroupIntent(Group group) {

        Intent intent = new Intent(LoadFragmentActivity.this, CometChatMessageListActivity.class);
        intent.putExtra(StringContract.IntentStrings.GUID, group.getGuid());
        intent.putExtra(StringContract.IntentStrings.AVATAR, group.getIcon());
        intent.putExtra(StringContract.IntentStrings.NAME, group.getName());
        intent.putExtra(StringContract.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP);
        startActivity(intent);
    }

    private void joinGroup(Group group) {
        progressDialog = ProgressDialog.show(this, "", "Joining");
        progressDialog.setCancelable(false);
        CometChat.joinGroup(group.getGuid(), group.getGroupType(), groupPassword, new CometChat.CallbackListener<Group>() {
            @Override
            public void onSuccess(Group group) {
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
                Toast.makeText(LoadFragmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
