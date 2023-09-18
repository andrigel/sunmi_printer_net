package com.omega.sunmi_printer_net;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.omega.sunmi_printer_net.Printer;

import com.sunmi.externalprinterlibrary2.SunmiPrinterManager;

public class SunmiPrinterNetPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, EventChannel.StreamHandler {
  private MethodChannel channel;
  private EventChannel eventChannel;
  private EventChannel.EventSink eventSink;
  private Context context;
  private Activity activity;
  private Printer printer;
  private String tag = "sunmi_printer_net";

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "sunmi_printer_net");
    channel.setMethodCallHandler(this);
    eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "sunmi_printer_net/status");
    eventChannel.setStreamHandler(this);
    context = flutterPluginBinding.getApplicationContext();
    printer = new Printer();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("init")) {
      String ip = call.argument("ip");
      Integer port = call.argument("port");
      printer.init(context,eventSink,ip,port);
      result.success("");
    } else if (call.method.equals("connect"))
    {
      printer.connect();

      result.success("");
    } else if (call.method.equals("disconnect"))
    {
      printer.disconnect();
      result.success("");
    } else if (call.method.equals("addText"))
    {
      String text = call.argument("text");
      printer.addText(text);
      result.success("");
    } else if (call.method.equals("setAlignment"))
    {
      String alignment = call.argument("alignment");
      printer.setAlignment(alignment);
      result.success("");
    } else if (call.method.equals("setBoldMode"))
    {
      Boolean val = call.argument("isBold");
      printer.setBoleMode(val);
      result.success("");
    } else if (call.method.equals("addSpacing"))
    {
      Integer count = call.argument("count");
      printer.addSpacing(count);
      result.success("");
    } else if (call.method.equals("commit"))
    {
      printer.commit();
      result.success("");
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromActivity() {
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
      this.eventSink = events;
    }
  
  @Override
  public void onCancel(Object arguments) {
    eventSink = null;
    }
}
