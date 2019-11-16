package com.example.soilrespiration.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.soilrespiration.R;

public class DialogActivity extends AppCompatActivity {

    private Button EnterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        EnterBtn = (Button) findViewById(R.id.enter_dialog);
        EnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialogActivity.this, CollectActivity.class);
                startActivity(intent);
            }
        });
    }
}
