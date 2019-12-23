package com.cometchat.pro.androiduikit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ComponentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_list);
        findViewById(R.id.cc_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_avatar);
                startActivity(intent);
            }
        });
        findViewById(R.id.cc_status_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_status_indicator);
                startActivity(intent);
            }
        });
        findViewById(R.id.cc_badge_count).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_badge_count);
                startActivity(intent);
            }
        });
        findViewById(R.id.cc_user_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_user_view);
                startActivity(intent);
            }
        });
        findViewById(R.id.cc_group_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_group_view);
                startActivity(intent);
            }
        });
        findViewById(R.id.cc_conversation_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComponentListActivity.this,LoadFragmentActivity.class);
                intent.putExtra("screen",R.id.cc_conversation_view);
                startActivity(intent);
            }
        });
    }

    public void backClick(View view)
    {
        super.onBackPressed();
    }
}
