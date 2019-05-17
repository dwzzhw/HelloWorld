package com.example.loading.helloworld.ipc;

import android.os.IBinder;
import android.os.RemoteException;

public class RemoteServiceImp implements IRemoteServiceInterface {
    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public String getServerName(String cliName) throws RemoteException {
        return null;
    }

    private MyServiceBinder myBinder = new MyServiceBinder();

    @Override
    public IBinder asBinder() {
        return myBinder;
    }

    private class MyServiceBinder extends IRemoteServiceInterface.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public String getServerName(String cliName) throws RemoteException {
            return null;
        }
    }
}
