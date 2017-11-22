package com.example.demons;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.chaihongwei.processkeeplive.IPerson;

/**
 * 保活助手A守护后台服务，绑定保活助手B
 */
public class AssistantAService extends Service {
    private final String B_PackageName = "com.example.demons2";
    private final String B_ServicePath = "com.example.demons2.AssistantBService";
    private IPerson mBinderFromB;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindAliveB();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderFromB = IPerson.Stub.asInterface(service);
            if (mBinderFromB != null) {
                try {
                    Log.d("chw",
                            "收到保活助手B Service返回的数据：name="
                                    + mBinderFromB.getName() + "；age="
                                    + mBinderFromB.getAge());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private IPerson.Stub mBinderToB = new IPerson.Stub() {
        @Override
        public String getName() throws RemoteException {
            return "我是保活助手A";
        }

        @Override
        public int getAge() throws RemoteException {
            return 2;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderToB;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //提升Service的优先级
        Notification notification = new Notification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(1, notification);

        Log.d("chw", "****保活助手AonCreate：绑定启动保活助手B****");
        bindAliveB();
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

    private void bindAliveB() {
        Intent clientIntent = new Intent();
        clientIntent.setClassName(B_PackageName, B_ServicePath);
        bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
    }
}
