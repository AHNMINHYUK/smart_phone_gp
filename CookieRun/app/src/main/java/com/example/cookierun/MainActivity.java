package com.example.cookierun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cookierun.game.MainScene;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(MainScene.PARAM_STAGE_INDEX, 1);
        startActivity(intent);
    }

    public void onBtnFirst(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(MainScene.PARAM_STAGE_INDEX, 0);
        startActivity(intent);
    }

    public void onBtnSecond(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(MainScene.PARAM_STAGE_INDEX, 1);
        startActivity(intent);
    }
}