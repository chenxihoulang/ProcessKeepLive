package com.example.chaihongwei.processkeeplive;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.chaihongwei.processkeeplive.service.LocalService;
import com.example.chaihongwei.processkeeplive.service.RemoteService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //启动保活后台服务
//        Intent intent = new Intent(this, ChwAliveService.class);
//        startService(intent);

        startService(new Intent(this, LocalService.class));
        startService(new Intent(this, RemoteService.class));
    }
}
