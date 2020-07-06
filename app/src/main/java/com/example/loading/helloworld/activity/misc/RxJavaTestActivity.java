package com.example.loading.helloworld.activity.misc;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.loading.helloworld.R;
import com.example.loading.helloworld.activity.MiscTestActivity;
import com.loading.common.component.BaseActivity;
import com.loading.common.utils.Loger;
import com.loading.common.widget.TipsToast;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class RxJavaTestActivity extends BaseActivity {
    private static final String TAG = "RxJavaTestActivity";
    private TextView titleTextView = null;
    private TextView bbsBoardView;

    private Observable<String> mObservable01;
    private Observable<String> mObservable02;
    private Observable<String> mObservable03;

    private Observer<String> mObserver01;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava_test);

        titleTextView = findViewById(R.id.title);
        titleTextView.setOnClickListener(mTitleClickListener);
        bbsBoardView = findViewById(R.id.bbs_board);

        initObservableObj();
    }

    private View.OnClickListener mTitleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Title view is clicked");
            TipsToast.getInstance().showTipsText("Title is clicked");
            startMiscActivity();
        }
    };

    public void onBtnClicked(View view) {
        Loger.d(TAG, "-->onBtnClicked()");
        if (view.getId() == R.id.btn_test_01) {
            doTest01();
        } else if (view.getId() == R.id.btn_filter) {
            doFilterTest();
        }
    }

    private void startMiscActivity() {
        Intent intent = new Intent(this, MiscTestActivity.class);
        startActivity(intent);
    }

    private void initObservableObj() {
        mObservable01 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Loger.d(TAG, "-->mObservable01.subscribe(), begin sleep 2s, thread=" + Thread.currentThread().getName());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Loger.w(TAG, "observable 01 sleep is inturrepted ");
                }

                if (emitter.isDisposed()) {
                    Loger.w(TAG, "Observable01 emitter is disposed");
                } else {
                    emitter.onNext("Observable01_result01");
                    Thread.sleep(1000);
                    emitter.onNext("Observable01_result02");
                    emitter.onComplete();
                }

            }
        });
        mObservable02 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Loger.d(TAG, "-->mObservable02.subscribe(), begin sleep 2s, thread=" + Thread.currentThread().getName());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Loger.w(TAG, "observable 02 sleep is inturrepted ");
                }

                if (emitter.isDisposed()) {
                    Loger.w(TAG, "Observable02 emitter is disposed");
                } else {
                    emitter.onNext("Observable02_result01");
                    Thread.sleep(2000);
                    emitter.onNext("Observable02_result02");
                    emitter.onComplete();
                }

            }
        });
        mObservable03 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Loger.d(TAG, "-->mObservable03.subscribe(), begin sleep 2s, thread=" + Thread.currentThread().getName());

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Loger.w(TAG, "observable 03 sleep is inturrepted ");
                }

                if (emitter.isDisposed()) {
                    Loger.w(TAG, "Observable03 emitter is disposed");
                } else {
                    emitter.onNext("Observable03_result");
                    emitter.onComplete();
                }

            }
        });


        mObserver01 = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Loger.d(TAG, "Observer01.onSubscribe(), disposable=" + d + ", thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(String o) {
                Loger.d(TAG, "Observer01.onNext(), object=" + o + ", thread=" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Loger.d(TAG, "Observer01.onError(), Throwable=" + e);
            }

            @Override
            public void onComplete() {
                Loger.d(TAG, "Observer01.onComplete(), thread=" + Thread.currentThread().getName());
            }
        };
    }

    private Observable<String> getFinalObservable(String params) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Loger.d(TAG, "-->final observable.subscribe(), begin sleep 5s, thread=" + Thread.currentThread().getName());

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Loger.w(TAG, "final observable sleep is inturrepted ");
                }

                if (emitter.isDisposed()) {
                    Loger.w(TAG, "final Observable emitter is disposed");
                } else {
                    emitter.onNext("final observable03_result: " + params);
                    emitter.onComplete();
                }

            }
        });
    }

    private void doTest01() {
        bbsBoardView.setText("doTest01 button is clicked.");
        Observable.zip(mObservable01.subscribeOn(Schedulers.io()), mObservable02.subscribeOn(Schedulers.computation()), new BiFunction<String, String, String>() {
            //        Observable.zip(mObservable01, mObservable02, new BiFunction<String, String, String>() {
            @Override
            public String apply(String s, String s2) {
                return "zip result=" + s + "__" + s2;
            }
        })
                .concatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        Loger.d(TAG, "concatMap.apply(), received s=" + s + ", thread=" + Thread.currentThread().getName());
                        return getFinalObservable(s).subscribeOn(Schedulers.io());
//                        return getFinalObservable(s);
                    }
                })
//                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .observeOn(Schedulers.computation())
                .subscribe(mObserver01);
    }

    private void doFilterTest() {
        Loger.d(TAG, "-->doFilterTest(): ");
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).filter(i -> i != 2).flatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> apply(Integer integer) throws Exception {
                return Observable.just(integer * 10);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer != 10;
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Loger.d(TAG, "doOnNext()-->accept(): value=" + integer);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer != 30;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Loger.d(TAG, "Consumer.onNext()-->accept(): value=" + integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Loger.d(TAG, "Consumer.onError()-->accept(): throwable=" + throwable);
            }
        });
    }
}
