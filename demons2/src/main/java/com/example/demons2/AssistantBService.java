package com.example.demons2;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.chaihongwei.processkeeplive.IPerson;

/**
 * 保活助手APK后台服务
 */
public class AssistantBService extends Service {
    private final String Chw_PackageName = "com.example.chaihongwei.processkeeplive";
    private final String Chw_ServicePath = "com.example.chaihongwei.processkeeplive.ChwAliveService";
    private final String A_PackageName = "com.example.demons";
    private final String A_ServicePath = "com.example.demons.AssistantAService";

    private IPerson mBinderFomAOrChw;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindAliveA();
            bindChw();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderFomAOrChw = IPerson.Stub.asInterface(service);
        }
    };


    private IPerson.Stub mBinderToA = new IPerson.Stub() {
        @Override
        public String getName() throws RemoteException {
            return "我是保活助手B";
        }

        @Override
        public int getAge() throws RemoteException {
            return 1;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderToA;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindAliveA();
        bindChw();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindService(conn);
    }

    private void bindChw() {
        Intent clientIntent = new Intent();
        clientIntent.setClassName(Chw_PackageName, Chw_ServicePath);
        bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
    }

    private void bindAliveA() {
        Intent clientIntent = new Intent();
        clientIntent.setClassName(A_PackageName, A_ServicePath);
        bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
    }
}