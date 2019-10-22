package com.example.soilrespiration.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.soilrespiration.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_mail);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        CardView cardView_gather = (CardView) findViewById(R.id.card_yellow);
        cardView_gather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_gather = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(intent_gather);
            }
        });
        CardView cardView_analyze = (CardView) findViewById(R.id.card_green);
        cardView_analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_query = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(intent_query);
            }
        });
        //Button collection = (Button) findViewById(R.id.data_collect);
       // Button analysis = (Button) findViewById(R.id.data_analyze);
        //Button sync = (Button) findViewById(R.id.data_synchronization);
        //Button reserve = (Button) findViewById(R.id.reserve_Btn);
        //collection.setOnClickListener(this);
        //analysis.setOnClickListener(this);
        //sync.setOnClickListener(this);
        //reserve.setOnClickListener(this);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
                default:
        }
        return true;
    }
    /*
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.data_collect:
                Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(intent);
                break;
            case R.id.data_analyze:
                Intent intent1 = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(intent1);
                break;
            case R.id.data_synchronization:
                Toast.makeText(MainActivity.this, "here is synchronize", Toast.LENGTH_SHORT).show();
                break;
            case R.id.reserve_Btn:
                Toast.makeText(MainActivity.this, "here is reserve", Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;
        }
    }   */
}
