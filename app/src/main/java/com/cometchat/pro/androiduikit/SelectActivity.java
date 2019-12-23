package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.cometchat.pro.core.CometChat;
import com.google.android.material.snackbar.Snackbar;

import screen.CometChatUnified;

public class SelectActivity extends AppCompatActivity {

    private RadioGroup screengroup;
    private CardView directIntentFront,directIntentBack,usingScreenFront,usingScreenBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        findViewById(R.id.directLaunch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this, CometChatUnified.class));
            }
        });
        findViewById(R.id.componentLaunch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this,ComponentListActivity.class));
            }
        });
        screengroup = (RadioGroup)findViewById(R.id.screenselector);
        findViewById(R.id.fragmentlaunch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = screengroup.getCheckedRadioButtonId();
                if (id==0)
                {
                    Snackbar.make(view,"Select any one screen.",Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(SelectActivity.this,LoadFragmentActivity.class);
                    intent.putExtra("screen",id);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CometChat.getLoggedInUser()==null)
        {
            startActivity(new Intent(SelectActivity.this,MainActivity.class));
        }
    }
}
