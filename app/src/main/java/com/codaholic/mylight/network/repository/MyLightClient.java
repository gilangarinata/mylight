package com.codaholic.mylight.network.repository;

import androidx.lifecycle.MutableLiveData;

import com.codaholic.mylight.network.ResponseAPI;
import com.codaholic.mylight.network.RouteApi;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MyLightClient {
    private CompositeDisposable compositeDisposable;
    private MyLightRepository myLightRepository = new MyLightRepository();
    public MyLightClient() {compositeDisposable = new CompositeDisposable();}

    public MutableLiveData<ResponseAPI> postStringClient(String baseEndpoint, String endoint, HashMap<String, String> map, OkHttpClient timeout) {
        MutableLiveData<ResponseAPI> response = new MutableLiveData<>();
        compositeDisposable.add(
                myLightRepository.getClient(baseEndpoint, timeout)
                        .create(RouteApi.class)
                        .setRoutePost(endoint, map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> response.setValue(ResponseAPI.loading()))
                        .subscribe(
                                responseBody -> response.setValue(ResponseAPI.success(responseBody)),
                                throwable -> response.setValue(ResponseAPI.error(throwable)))

        );
        return response;
    }


    public MutableLiveData<ResponseAPI> postMultipathClient(String baseEndpoint, String endoint, Map map, OkHttpClient timeout) {
        MutableLiveData<ResponseAPI> response = new MutableLiveData<>();
        compositeDisposable.add(
                myLightRepository.getClient(baseEndpoint, timeout)
                        .create(RouteApi.class)
                        .setRoutePostMultipath(endoint, map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> response.setValue(ResponseAPI.loading()))
                        .subscribe(
                                responseBody -> response.setValue(ResponseAPI.success((Response<ResponseBody>) responseBody)),
                                throwable -> response.setValue(ResponseAPI.error((Throwable) throwable)))

        );
        return response;
    }

    public MutableLiveData<ResponseAPI> getClient(String baseEndpoint, String endoint, HashMap<String, String> map, OkHttpClient timeout) {
        MutableLiveData<ResponseAPI> response = new MutableLiveData<>();
        compositeDisposable.add(
                myLightRepository.getClient(baseEndpoint, timeout)
                        .create(RouteApi.class)
                        .setRouteGet(endoint,map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> response.setValue(ResponseAPI.loading()))
                        .subscribe(
                                responseBody -> response.setValue(ResponseAPI.success(responseBody)),
                                throwable -> response.setValue(ResponseAPI.error(throwable)))

        );
        return response;
    }



    public MutableLiveData<ResponseAPI> deleteClient(String baseEndpoint, String endoint, HashMap<String, String> map, OkHttpClient timeout) {
        MutableLiveData<ResponseAPI> response = new MutableLiveData<>();
        compositeDisposable.add(
                myLightRepository.getClient(baseEndpoint, timeout)
                        .create(RouteApi.class)
                        .setRouteDelete(endoint,map)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> response.setValue(ResponseAPI.loading()))
                        .subscribe(
                                responseBody -> response.setValue(ResponseAPI.success(responseBody)),
                                throwable -> response.setValue(ResponseAPI.error(throwable)))

        );
        return response;
    }


    public MutableLiveData<ResponseAPI> streamingClient(String baseEndpoint, String endoint, OkHttpClient timeout) {
        MutableLiveData<ResponseAPI> response = new MutableLiveData<>();
        compositeDisposable.add(
                myLightRepository.getClient(baseEndpoint, timeout)
                        .create(RouteApi.class)
                        .setRouteStreaming(endoint)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> response.setValue(ResponseAPI.loading()))
                        .subscribe(
                                responseBody -> response.setValue(ResponseAPI.success(responseBody)),
                                throwable -> response.setValue(ResponseAPI.error(throwable)))

        );
        return response;
    }

    public boolean isDisposed() {
        return compositeDisposable.isDisposed();
    }

    public void dispose() {
        if (compositeDisposable != null) compositeDisposable.dispose();

    }

    public void clear() {
        if (compositeDisposable != null) compositeDisposable.clear();
    }
}
