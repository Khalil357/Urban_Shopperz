import 'api_client.dart';

class AppNotification {
  final String id;
  final String title;
  final String body;
  final String channel;
  final bool isRead;
  final String createdAt;

  AppNotification({
    required this.id,
    required this.title,
    required this.body,
    this.channel = 'in_app',
    this.isRead = false,
    required this.createdAt,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) => AppNotification(
        id: json['id'] ?? '',
        title: json['title'] ?? '',
        body: json['body'] ?? '',
        channel: json['channel'] ?? 'in_app',
        isRead: json['isRead'] ?? false,
        createdAt: json['createdAt'] ?? '',
      );
}

class NotificationService {
  final ApiClient _api;

  NotificationService(this._api);

  Future<List<AppNotification>> getNotifications() async {
    final list = await _api.getList('/notifications');
    return list.map((j) => AppNotification.fromJson(j as Map<String, dynamic>)).toList();
  }

  Future<List<AppNotification>> getUnread() async {
    final list = await _api.getList('/notifications/unread');
    return list.map((j) => AppNotification.fromJson(j as Map<String, dynamic>)).toList();
  }

  Future<int> getUnreadCount() async {
    final data = await _api.get('/notifications/unread/count');
    return data['count'] as int;
  }

  Future<void> markAsRead(String id) async {
    await _api.post('/notifications/$id/read');
  }
}
