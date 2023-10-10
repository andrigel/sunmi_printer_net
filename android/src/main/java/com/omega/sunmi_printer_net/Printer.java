package com.omega.sunmi_printer_net;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.EventChannel;
import android.os.Handler;
import android.os.Looper;

import com.sunmi.externalprinterlibrary2.ConnectCallback;
import com.sunmi.externalprinterlibrary2.ResultCallback;
import com.sunmi.externalprinterlibrary2.SearchMethod;
import com.sunmi.externalprinterlibrary2.SunmiPrinterManager;
import com.sunmi.externalprinterlibrary2.printer.CloudPrinter;
import com.sunmi.externalprinterlibrary2.style.AlignStyle;
import com.sunmi.externalprinterlibrary2.style.BarcodeType;
import com.sunmi.externalprinterlibrary2.style.CloudPrinterStatus;
import com.sunmi.externalprinterlibrary2.style.EncodeType;
import com.sunmi.externalprinterlibrary2.style.ErrorLevel;
import com.sunmi.externalprinterlibrary2.style.HriStyle;
import com.sunmi.externalprinterlibrary2.style.ImageAlgorithm;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class Printer implements ResultCallback {

    private CloudPrinter cloudPrinter;

    private String tag = "sunmi_printer_net";

    private Context context;
    
    private String ip;

    private Integer port;

    private Boolean isInitialized = false;

    private EventChannel.EventSink eventSink;

    private Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    public void sendEvent(String message) {
        if (eventSink != null) {
            eventSink.success(message);
        }
    }

    public void init(Context context, EventChannel.EventSink eventSink, String ip, Integer port)
    {
        this.context=context;
        this.ip=ip;
        this.port=port;
        this.eventSink=eventSink;
        this.isInitialized = true;
    }

    public void connect() {
        if(!isInitialized)
        {
            Log.d(tag,"Noticed connecting request, but printer ip isn\'t initialized");
        }
        Log.d(tag,"Trying to connect to " + ip + ":"+ port.toString());
        cloudPrinter = SunmiPrinterManager.getInstance().createCloudPrinter(ip, port);
        cloudPrinter.connect(context, new ConnectCallback() {
            @Override
            public void onConnect() {
                cloudPrinter.initStyle();
                uiThreadHandler.post(() -> eventSink.success("connected"));
                Log.d(tag,"onConnect");
            }

            @Override
            public void onFailed(String s) {
                  uiThreadHandler.post(() -> eventSink.success("failed"));
                  Log.d(tag,"onFailed: " + s);
            }

            @Override
            public void onDisConnect() {
                  uiThreadHandler.post(() -> eventSink.success("disconnected"));
                  Log.d(tag,"onDisconnect");
            }
        });
    }

    public void disconnect() {
        if(cloudPrinter != null) {
            cloudPrinter.release(context);
        }
    }

    public void setEncodeMode(String mode) {
        cloudPrinter.setEncodeMode(EncodeType.valueOf(mode));
    }

    public void addText(String text) {
        if(checkConnect()) {
          cloudPrinter.printText(text);
        }
    }

    public void setAlignment(String alignment) {
        if(checkConnect()) {
            if(alignment == "center")
            {
                cloudPrinter.setAlignment(AlignStyle.CENTER);
            } else if(alignment == "left")
            {
                cloudPrinter.setAlignment(AlignStyle.LEFT);
            } else if(alignment == "right")
            {
                cloudPrinter.setAlignment(AlignStyle.RIGHT);
            }
          
        }
    }

    public void setBoleMode(Boolean val) {
        if(checkConnect()) {
            cloudPrinter.setBoldMode(val);
        }
    }

    public void addSpacing(Integer count){
        cloudPrinter.lineFeed(count);
    }
    
    public void commit() {
        if(checkConnect()) {

          cloudPrinter.commitTransBuffer(this);
        }
    }

    public void openCash() {
        if(checkConnect()) {
            cloudPrinter.openCashBox();
        }
    }

    public void getSn() {
        if(checkConnect()) {
            cloudPrinter.getDeviceSN(s -> Log.d(tag,"getDeviceSN: " + s));
        }
    }


    public void getState() {
        if(checkConnect()) {
            cloudPrinter.getDeviceState(s -> Log.d(tag,"getDeviceState: " + s));
        }
    }

    @Override
    public void onComplete() {
        Log.d(tag,"onComplete");
    }

    @Override
    public void onFailed(CloudPrinterStatus cloudPrinterStatus) {
        Log.d(tag,"onFailed");
    }

    private boolean checkConnect() {
        if(cloudPrinter == null) {
            return false;
        }
        if(!cloudPrinter.isConnected()) {
            return false;
        }
        return true;
    }
}