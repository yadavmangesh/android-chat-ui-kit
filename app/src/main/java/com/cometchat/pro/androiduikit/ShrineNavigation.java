package com.cometchat.pro.androiduikit;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import screen.CometChatUserListScreen;
import screen.CometChatConversationListScreen;
import screen.CometChatGroupListScreen;
import screen.CometChatUserInfoScreen;

public class ShrineNavigation extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_fragment);
        toolbar = findViewById(R.id.app_bar);
        loadFragment(new CometChatConversationListScreen());
        setSupportActionBar(toolbar);
        setNavigationClickListener();

    }

    private void setNavigationClickListener() {
        toolbar.setNavigationOnClickListener(new NavigationIconClickListener(
                this,
                findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                getResources().getDrawable(R.drawable.ic_menu_white_24dp), // Menu open icon
                getResources().getDrawable(R.drawable.shr_close_menu)

        )); // Menu close icon
    }

    public void loadConversations(View view) {
        loadFragment(new CometChatConversationListScreen());
        setNavigationClickListener();
    }

    public void loadUsers(View view) {
        loadFragment(new CometChatUserListScreen());
    }

    public void loadGroups(View view) {
        loadFragment(new CometChatGroupListScreen());
    }

    public void loadMoreInfo(View view) {
        loadFragment(new CometChatUserInfoScreen());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).commit();
            return true;
        }
        return false;
    }

}
