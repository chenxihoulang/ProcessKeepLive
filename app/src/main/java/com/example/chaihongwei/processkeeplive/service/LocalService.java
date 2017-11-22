package com.example.chaihongwei.processkeeplive.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.chaihongwei.processkeeplive.IMyAidlInterface;

public class LocalService extends Service {
    String TAG = "LocalService111";
    private localBinder mBinder;
    private localServiceConnection mLocalServiceConnection;


    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new localBinder();
        if (mLocalServiceConnection == null) {
            mLocalServiceConnection = new localServiceConnection();

        }
        Log.e(TAG, TAG + "On Create");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, TAG + " onStartCommand");
        //  绑定远程服务
        bindService(new Intent(this, RemoteService.class), mLocalServiceConnection, Context.BIND_IMPORTANT);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //通过Aidl进行通讯
    class localBinder extends IMyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return TAG;
        }
    }


    //连接远程服务
    class localServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                IMyAidlInterface process = IMyAidlInterface.Stub.asInterface(service);
                Log.e(TAG, "连接" + process.getServiceName() + "服务成功");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调
            // 监测，远程服务已经死掉，则重启远程服务
            Log.e(TAG, "远程服务挂掉了,远程服务被杀死");
            // 启动远程服务
            startService(new Intent(LocalService.this, RemoteService.class));
            // 绑定远程服务
            bindService(new Intent(LocalService.this, RemoteService.class), mLocalServiceConnection, Context.BIND_IMPORTANT);
        }
    }
}