import 'dart:async';
import 'dart:convert';
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:socket_io_client/socket_io_client.dart' as IO;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Remote Control App',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(  // Fixed property name
        title: const Text('Remote Control App'),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const ControllerScreen()),
                );
              },
              child: const Text('Control Other Device'),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const ControlledScreen()),
                );
              },
              child: const Text('Allow Remote Control'),
            ),
          ],
        ),
      ),
    );
  }
}

// --- Controller Screen ---
class ControllerScreen extends StatefulWidget {
  const ControllerScreen({super.key});

  @override
  State<ControllerScreen> createState() => _ControllerScreenState();
}

class _ControllerScreenState extends State<ControllerScreen> {
  late IO.Socket socket;
  String _status = 'Disconnected';
  Image? _remoteScreen;
  String? _remoteScreenText;

  @override
  void initState() {
    super.initState();
    _connectToServer();
  }

  void _connectToServer() {
    socket = IO.io('http://192.168.1.100:3000',  // Replace with your server IP
        IO.OptionBuilder()
            .setTransports(['websocket'])
            .disableAutoConnect()
            .build());

    socket.connect();

    socket.onConnect((_) {
      setState(() => _status = 'Connected');
      print('Controller Connected');
    });

    socket.onDisconnect((_) {
      setState(() => _status = 'Disconnected');
      print('Controller Disconnected');
    });

    socket.on('screen_data', (data) {
      if (data is String) {
        final parts = data.split(',');
        if (parts.length < 2) return;
        final header = parts[0];
        final base64Data = parts[1];
        final bytes = base64Decode(base64Data);

        if (header.startsWith('data:image')) {
          setState(() {
            _remoteScreen = Image.memory(bytes);
            _remoteScreenText = null;
          });
        } else if (header.startsWith('data:text')) {
          setState(() {
            _remoteScreenText = String.fromCharCodes(bytes);
            _remoteScreen = null;
          });
        }
      }
    });

    socket.onError((error) {
      print('Socket Error: $error');
      setState(() => _status = 'Error: ${error.toString()}');
    });
  }

  void _sendCommand(String commandType, Map<String, dynamic> data) {
    if (socket.connected) {
      socket.emit('control_command', {
        'type': commandType,
        'payload': data,
      });
      print('Sent command: $commandType with $data');
    } else {
      print('Not connected to server.');
    }
  }

  @override
  void dispose() {
    socket.disconnect();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Control Device'),
        actions: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Center(child: Text('Status: $_status')),
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: _remoteScreen != null
                ? GestureDetector(
                    onTapUp: (details) {
                      final renderBox = context.findRenderObject() as RenderBox;
                      final size = renderBox.size;
                      _sendCommand('tap', {
                        'x': details.localPosition.dx,
                        'y': details.localPosition.dy,
                        'width': size.width,
                        'height': size.height,
                      });
                    },
                    child: FittedBox(
                      fit: BoxFit.contain,
                      child: _remoteScreen,
                    ),
                  )
                : _remoteScreenText != null
                    ? Center(child: Text(_remoteScreenText!))
                    : const Center(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            CircularProgressIndicator(),
                            SizedBox(height: 20),
                            Text('Waiting for screen data...'),
                          ],
                        ),
                      ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                ElevatedButton(
                  onPressed: () => _sendCommand('back', {}),
                  child: const Text('Back'),
                ),
                ElevatedButton(
                  onPressed: () => _sendCommand('home', {}),
                  child: const Text('Home'),
                ),
                ElevatedButton(
                  onPressed: () => _sendCommand('open_app', {'appName': 'com.android.calculator2'}),
                  child: const Text('Open App'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// --- Controlled Screen ---
class ControlledScreen extends StatefulWidget {
  const ControlledScreen({super.key});

  @override
  State<ControlledScreen> createState() => _ControlledScreenState();
}

class _ControlledScreenState extends State<ControlledScreen> {
  late IO.Socket socket;
  String _status = 'Disconnected';
  final GlobalKey _screenshotKey = GlobalKey();
  Timer? _screenTimer;

  @override
  void initState() {
    super.initState();
    _requestPermissions();
    _connectToServer();
  }

  Future<void> _requestPermissions() async {
    await [
      Permission.camera,
      Permission.microphone,
      Permission.storage,
    ].request();
  }

  void _connectToServer() {
    socket = IO.io('http://192.168.1.100:3000',  // Replace with your server IP
        IO.OptionBuilder()
            .setTransports(['websocket'])
            .disableAutoConnect()
            .build());

    socket.connect();

    socket.onConnect((_) {
      setState(() => _status = 'Connected');
      print('Controlled Device Connected');
      _startSendingScreenData();
    });

    socket.onDisconnect((_) {
      setState(() => _status = 'Disconnected');
      print('Controlled Device Disconnected');
      _stopSendingScreenData();
    });

    socket.on('control_command', (data) {
      print('Received command: $data');
      if (data is Map<String, dynamic>) {
        final commandType = data['type'];
        final payload = data['payload'];

        switch (commandType) {
          case 'tap':
            _handleTapCommand(payload);
            break;
          case 'back':
            _handleBackCommand();
            break;
          case 'home':
            _handleHomeCommand();
            break;
          case 'open_app':
            _handleOpenAppCommand(payload);
            break;
          default:
            print('Unknown command type: $commandType');
        }
      }
    });

    socket.onError((error) {
      print('Socket Error: $error');
      setState(() => _status = 'Error: ${error.toString()}');
    });
  }

  void _startSendingScreenData() {
    _screenTimer = Timer.periodic(const Duration(milliseconds: 500), (timer) {
      if (socket.connected) {
        _captureAndSendScreen();
      }
    });
  }

  void _stopSendingScreenData() {
    _screenTimer?.cancel();
  }

  Future<void> _captureAndSendScreen() async {
    try {
      final boundary = _screenshotKey.currentContext?.findRenderObject();
      if (boundary == null || !boundary.attached) return;

      final image = await boundary.toImage();
      final byteData = await image.toByteData(format: ui.ImageByteFormat.png);
      final bytes = byteData?.buffer.asUint8List();

      if (bytes != null) {
        final base64Image = base64Encode(bytes);
        socket.emit('screen_data', 'data:image/png;base64,$base64Image');
      }
    } catch (e) {
      print('Screen capture error: $e');
    }
  }

  void _handleTapCommand(Map<String, dynamic> payload) {
    print('Simulating tap at (${payload['x']}, ${payload['y']})');
  }

  void _handleBackCommand() {
    print('Simulating back button press.');
  }

  void _handleHomeCommand() {
    print('Simulating home button press.');
  }

  void _handleOpenAppCommand(Map<String, dynamic> payload) {
    print('Opening app: ${payload['appName']}');
  }

  @override
  void dispose() {
    _stopSendingScreenData();
    socket.disconnect();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Allow Remote Control'),
        actions: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Center(child: Text('Status: $_status')),
          ),
        ],
      ),
      body: RepaintBoundary(
        key: _screenshotKey,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Text(
                'Your device is being controlled',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 20),
              Icon(Icons.smartphone, size: 100, color: Colors.blue[300]),
              const SizedBox(height: 20),
              Text(
                'Connection: $_status',
                style: const TextStyle(fontSize: 16),
              ),
              const SizedBox(height: 10),
              const Text(
                'This screen is being shared with the controller',
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ),
      ),
    );
  }
}