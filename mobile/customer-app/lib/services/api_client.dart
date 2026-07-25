import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'auth_service.dart';

/// Base API client with JWT auth headers and error handling.
class ApiClient {
  // Android emulator → host machine: 10.0.2.2
  // iOS simulator → host machine: localhost
  // Physical device → use your machine's LAN IP
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1';

  final AuthService _authService;

  ApiClient(this._authService);

  Map<String, String> _headers() => {
        'Content-Type': 'application/json',
        if (_authService.token != null)
          'Authorization': 'Bearer ${_authService.token}',
      };

  Future<Map<String, dynamic>> get(String path) async {
    final uri = Uri.parse('$baseUrl$path');
    debugPrint('GET $uri');
    final response = await http.get(uri, headers: _headers());
    return _handleResponse(response);
  }

  Future<Map<String, dynamic>> post(String path, {Map<String, dynamic>? body}) async {
    final uri = Uri.parse('$baseUrl$path');
    debugPrint('POST $uri body=$body');
    final response = await http.post(
      uri,
      headers: _headers(),
      body: body != null ? jsonEncode(body) : null,
    );
    return _handleResponse(response);
  }

  Future<Map<String, dynamic>> patch(String path, {Map<String, dynamic>? body}) async {
    final uri = Uri.parse('$baseUrl$path');
    final response = await http.patch(
      uri,
      headers: _headers(),
      body: body != null ? jsonEncode(body) : null,
    );
    return _handleResponse(response);
  }

  Map<String, dynamic> _handleResponse(http.Response response) {
    final data = jsonDecode(response.body) as Map<String, dynamic>;

    if (!(data['success'] ?? false)) {
      final error = data['error'] as Map<String, dynamic>?;
      throw ApiException(
        error?['code'] ?? 'UNKNOWN',
        error?['message'] ?? 'Unknown error',
      );
    }

    return data['data'] as Map<String, dynamic>;
  }

  Future<List<dynamic>> getList(String path) async {
    final uri = Uri.parse('$baseUrl$path');
    debugPrint('GET $uri');
    final response = await http.get(uri, headers: _headers());
    if (response.body.isEmpty) return [];
    final data = jsonDecode(response.body) as Map<String, dynamic>;
    if (!(data['success'] ?? false)) {
      throw ApiException('API_ERROR', 'Request failed');
    }
    return data['data'] as List<dynamic>;
  }
}

class ApiException implements Exception {
  final String code;
  final String message;
  ApiException(this.code, this.message);

  @override
  String toString() => '[$code] $message';
}
