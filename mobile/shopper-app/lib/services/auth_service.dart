import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthService extends ChangeNotifier {
  final _storage = const FlutterSecureStorage();
  String? _token;
  Map<String, dynamic>? _user;

  String? get token => _token;
  Map<String, dynamic>? get user => _user;
  bool get isAuthenticated => _token != null;

  static const String _baseUrl = 'http://10.0.2.2:8080/api/v1';

  Future<bool> requestOtp(String phone) async {
    try {
      final r = await http.post(Uri.parse('$_baseUrl/auth/otp'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({'phone': phone}));
      return r.statusCode == 200;
    } catch (e) {
      debugPrint('OTP error: $e');
      return false;
    }
  }

  Future<bool> verifyOtp(String phone, String otp) async {
    try {
      final r = await http.post(Uri.parse('$_baseUrl/auth/verify'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({'phone': phone, 'otp': otp}));
      if (r.statusCode == 200) {
        final data = jsonDecode(r.body)['data'];
        _token = data['accessToken'];
        _user = data['user'];
        await _storage.write(key: 'token', value: _token);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('Verify error: $e');
      return false;
    }
  }

  Future<void> logout() async {
    _token = null;
    _user = null;
    await _storage.delete(key: 'token');
    notifyListeners();
  }
}
