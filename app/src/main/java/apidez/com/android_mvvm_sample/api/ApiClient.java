package apidez.com.android_mvvm_sample.api;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Inject;

import apidez.com.android_mvvm_sample.model.Purchase;
import rx.Observable;

/**
 * Created by nongdenchet on 10/3/15.
 */
public class ApiClient {

    private Gson mGson;

    @Inject
    public ApiClient(@NonNull Gson gson) {
        mGson = gson;
    }

    /**
     * Fake networking
     */
    public Observable<Boolean> submitPurchase(Purchase purchase) {
        return Observable.create(subscriber -> {
            try {
                String json = mGson.toJson(purchase);
                Thread.sleep((json.length() % 3) * 1000);
                subscriber.onNext(true);
                subscriber.onCompleted();
            } catch (Exception exception) {
                subscriber.onError(exception);
            }
        });
    }
}