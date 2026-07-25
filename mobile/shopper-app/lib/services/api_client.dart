import 'dart:convert';
import 'package:http/http.dart' as http;
import 'auth_service.dart';

class ApiClient {
  static const String baseUrl = 'http://10.0.2.2:8080/api/v1';
  final AuthService _auth;

  ApiClient(this._auth);

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (_auth.token != null) 'Authorization': 'Bearer ${_auth.token}',
      };

  Future<Map<String, dynamic>> get(String path) async {
    final r = await http.get(Uri.parse('$baseUrl$path'), headers: _headers);
    return _handle(r);
  }

  Future<Map<String, dynamic>> post(String path, {Map<String, dynamic>? body}) async {
    final r = await http.post(Uri.parse('$baseUrl$path'),
        headers: _headers, body: body != null ? jsonEncode(body) : null);
    return _handle(r);
  }

  Future<List<dynamic>> getList(String path) async {
    final r = await http.get(Uri.parse('$baseUrl$path'), headers: _headers);
    final data = jsonDecode(r.body);
    if (!(data['success'] ?? false)) throw Exception(data['error']?['message'] ?? 'Error');
    return data['data'] as List<dynamic>;
  }

  Map<String, dynamic> _handle(http.Response r) {
    final data = jsonDecode(r.body) as Map<String, dynamic>;
    if (!(data['success'] ?? false)) {
      throw Exception(
          (data['error'] as Map?)?['message'] ?? 'Request failed');
    }
    return data['data'] as Map<String, dynamic>;
  }
}

class ApiException implements Exception {
  final String message;
  ApiException(this.message);
  @override
  String toString() => message;
}
