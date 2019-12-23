package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputEditText uid = findViewById(R.id.etUID);
        inputLayout = findViewById(R.id.inputUID);

        uid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                 if (i== EditorInfo.IME_ACTION_DONE){
                     if (uid.getText().toString().isEmpty()) {
                         Toast.makeText(LoginActivity.this, "Fill Username field", Toast.LENGTH_LONG).show();
                     }
                     else {
                         findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
                         inputLayout.setEndIconVisible(false);
                         login(uid.getText().toString());
                     }
                 }
                return true;
            }
        });

        inputLayout.setEndIconOnClickListener(view -> {
            if (uid.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Fill Username field", Toast.LENGTH_LONG).show();
            }
            else {
                findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
                inputLayout.setEndIconVisible(false);
                login(uid.getText().toString());
            }

        });

    }

    private void login(String uid) {

        CometChat.login(uid, Constant.API_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                     startActivity(new Intent(LoginActivity.this, SelectActivity.class));
                       finish();
            }

            @Override
            public void onError(CometChatException e) {
                inputLayout.setEndIconVisible(true);
                findViewById(R.id.loginProgress).setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
