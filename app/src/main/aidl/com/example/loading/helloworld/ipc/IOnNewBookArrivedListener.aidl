// IOnNewBookArrivedListener.aidl
package com.example.loading.helloworld.ipc;
import com.example.loading.helloworld.ipc.BookEntity;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in BookEntity newBook);
}
