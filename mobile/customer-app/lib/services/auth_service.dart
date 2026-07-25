import 'package:flutter/foundation.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'api_client.dart';

class AuthService extends ChangeNotifier {
  final _storage = const FlutterSecureStorage();
  String? _token;
  Map<String, dynamic>? _user;
  late ApiClient _apiClient;

  AuthService() {
    _apiClient = ApiClient(this);
  }

  String? get token => _token;
  Map<String, dynamic>? get user => _user;
  ApiClient get api => _apiClient;

  bool get isAuthenticated => _token != null;

  static const String _baseUrl = 'http://localhost:8080/api/v1';

  Future<bool> requestOtp(String phone) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/auth/otp'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'phone': phone}),
      );
      return response.statusCode == 200;
    } catch (e) {
      debugPrint('OTP request failed: $e');
      return false;
    }
  }

  Future<bool> verifyOtp(String phone, String otp) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/auth/verify'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'phone': phone, 'otp': otp}),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body)['data'];
        _token = data['accessToken'];
        _user = data['user'];
        await _storage.write(key: 'token', value: _token);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      debugPrint('OTP verification failed: $e');
      return false;
    }
  }

  Future<void> logout() async {
    _token = null;
    _user = null;
    await _storage.delete(key: 'token');
    notifyListeners();
  }

  Future<void> loadToken() async {
    _token = await _storage.read(key: 'token');
    if (_token != null) {
      // In production, validate token here
    }
    notifyListeners();
  }

  Future<bool> registerCustomer({
    required String phone,
    required String name,
    String language = 'sw',
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/customers/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'phone': phone,
          'name': name,
          'language': language,
        }),
      );
      return response.statusCode == 201;
    } catch (e) {
      debugPrint('Registration failed: $e');
      return false;
    }
  }
}
