// IBookManager.aidl
package com.example.loading.helloworld.ipc;
import com.example.loading.helloworld.ipc.BookEntity;
import com.example.loading.helloworld.ipc.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
            List<BookEntity> getBookList();
            void addBook(in BookEntity book);
            void registerListener(IOnNewBookArrivedListener listener);
            void unRegisterListener(IOnNewBookArrivedListener listener);
}
