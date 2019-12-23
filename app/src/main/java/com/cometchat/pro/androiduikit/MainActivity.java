package com.cometchat.pro.androiduikit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText uid;
    ArrayList<String> tabArray = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
            });


        findViewById(R.id.superhero1).setOnClickListener(view -> {
                findViewById(R.id.superhero1_progressbar).setVisibility(View.VISIBLE);
                login("superhero1");
        });
        findViewById(R.id.superhero2).setOnClickListener(view -> {
                findViewById(R.id.superhero2_progressbar).setVisibility(View.VISIBLE);
                login("superhero2");
        });
        findViewById(R.id.superhero3).setOnClickListener(view -> {
                findViewById(R.id.superhero3_progressbar).setVisibility(View.VISIBLE);
                login("superhero3");
        });
        findViewById(R.id.superhero4).setOnClickListener(view -> {
                findViewById(R.id.superhero4_progressbar).setVisibility(View.VISIBLE);
                login("superhero4");
        });

    }

    private void login(String uid) {

        CometChat.login(uid, Constant.API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                startActivity(new Intent(MainActivity.this, SelectActivity.class));
                finish();
            }

            @Override
            public void onError(CometChatException e) {
                String str = uid+"_progressbar";
                int id = getResources().getIdentifier(str,"id",getPackageName());
                findViewById(id).setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
