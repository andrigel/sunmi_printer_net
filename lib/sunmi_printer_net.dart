import 'dart:async';

import 'package:flutter/services.dart';

class SunmiPrinterNet {
  static const _methodChannel = MethodChannel('sunmi_printer_net');
  static const _eventChannel = EventChannel("sunmi_printer_net/status");

  static final StreamController<PrinterStatus> _statusController =
      StreamController<PrinterStatus>();

  static Stream<PrinterStatus> get statusStream => _statusController.stream;

  static Future<PrinterStatus> get status async =>
      await _statusController.stream.last;

  static Future<void> init(String ip, int port) async {
    _eventChannel.receiveBroadcastStream().listen((event) {
      switch (event) {
        case 'connected':
          _statusController.add(PrinterStatus.ready);
          break;
        case 'disconnected':
          _statusController.add(PrinterStatus.disconnected);
          break;
        case 'failed':
          _statusController.add(PrinterStatus.failed);
          break;
      }
    });
    await _methodChannel.invokeMethod<void>('init', {
      'ip': ip,
      'port': port,
    });
    _statusController.add(PrinterStatus.unknown);
  }

  static Future<void> connect() async {
    _statusController.add(PrinterStatus.connecting);
    try {
      await _methodChannel.invokeMethod<void>('connect');
    } catch (e) {
      _statusController.add(PrinterStatus.failed);
    }
  }

  static Future<void> disconnect() async {
    await _methodChannel.invokeMethod<void>('disconnect');
  }

  static Future<void> addText(String text) async {
    await _methodChannel.invokeMethod<void>(
      'addText',
      {
        'text': text,
      },
    );
  }

  static Future<void> setAlignment(PrinterAlignment alignment) async {
    await _methodChannel.invokeMethod<void>(
      'setAlignment',
      {
        'alignment': _alignmentToString[alignment],
      },
    );
  }

  static Future<void> setBoldMode(bool isBold) async {
    await _methodChannel.invokeMethod<void>(
      'setBoldMode',
      {
        'isBold': isBold,
      },
    );
  }

  static Future<void> addSpacing(int count) async {
    await _methodChannel.invokeMethod<void>(
      'addSpacing',
      {
        'count': count,
      },
    );
  }

  static Future<void> commit() async {
    await _methodChannel.invokeMethod<void>(
      'commit',
    );
  }
}

enum PrinterStatus {
  failed,
  unknown,
  ready,
  disconnected,
  connecting,
}

enum PrinterAlignment {
  center,
  left,
  right,
}

const Map<PrinterAlignment, String> _alignmentToString = {
  PrinterAlignment.center: 'cener',
  PrinterAlignment.left: 'left',
  PrinterAlignment.right: 'right',
};
