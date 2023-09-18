import 'package:flutter/material.dart';
import 'dart:async';

import 'package:sunmi_printer_net/sunmi_printer_net.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _printerStatus = 'Unknown';

  @override
  void initState() {
    super.initState();
    init();
  }

  Future<void> init() async {
    try {
      await SunmiPrinterNet.init('191.1.1.1', 1111);
      await SunmiPrinterNet.connect();
      SunmiPrinterNet.statusStream.listen(
        (event) {
          setState(() {
            _printerStatus = event.toString().split('.').last;
          });
        },
      );
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Printer status: $_printerStatus\n'),
        ),
      ),
    );
  }
}
