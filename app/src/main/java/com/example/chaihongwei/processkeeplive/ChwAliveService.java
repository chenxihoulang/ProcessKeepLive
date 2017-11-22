package com.example.chaihongwei.processkeeplive;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

/**
 * 保活后台服务，绑定启动保活助手A的服务
 */
public class ChwAliveService extends Service {
    private final String A_PackageName = "com.example.demons";
    private final String A_ServicePath = "com.example.demons.AssistantAService";
    private IPerson mBinderFromA;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindAliveA();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderFromA = IPerson.Stub.asInterface(service);
            if (mBinderFromA != null) {
                try {
                    Log.d("chw",
                            "收到保活助手A的数据：name="
                                    + mBinderFromA.getName() + "；age="
                                    + mBinderFromA.getAge() + "----");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private IPerson.Stub mBinderToA = new IPerson.Stub() {
        @Override
        public String getName() throws RemoteException {
            return "我是保活服务";
        }

        @Override
        public int getAge() throws RemoteException {
            return 3;
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
        if (!isApkInstalled(A_PackageName)) {
            Log.d("chw", "----保活助手A未安装----");
            stopSelf();
            return;
        }
        bindAliveA();
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

    private void bindAliveA() {
        Intent serverIntent = new Intent();
        serverIntent.setClassName(A_PackageName, A_ServicePath);
        bindService(serverIntent, conn, Context.BIND_AUTO_CREATE);
    }

    private boolean isApkInstalled(String packageName) {
        PackageManager mPackageManager = getPackageManager();
        //获得所有已经安装的包信息
        List<PackageInfo> infos = mPackageManager.getInstalledPackages(0);
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }
}
