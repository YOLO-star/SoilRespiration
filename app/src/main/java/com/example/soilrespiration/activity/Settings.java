package com.example.soilrespiration.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soilrespiration.R;

public class Settings extends AppCompatActivity {

    private EditText flux_cycle;
    private Button yesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        flux_cycle = (EditText) findViewById(R.id.flux_cycle);
        yesBtn = (Button) findViewById(R.id.yes);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cycle = flux_cycle.getText().toString();
                Toast.makeText(Settings.this, cycle, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
