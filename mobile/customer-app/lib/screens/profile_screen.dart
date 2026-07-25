import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/notification_service.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  int _unreadCount = 0;

  @override
  void initState() {
    super.initState();
    _loadUnread();
  }

  Future<void> _loadUnread() async {
    try {
      final auth = context.read<AuthService>();
      final notifService = NotificationService(auth.api);
      final count = await notifService.getUnreadCount();
      if (mounted) setState(() => _unreadCount = count);
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthService>();
    final user = auth.user;

    return Scaffold(
      appBar: AppBar(title: const Text('Akaunti')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // Profile header
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 30,
                    backgroundColor: Colors.green[100],
                    child: Text(
                      (user?['name'] as String? ?? 'U')[0].toUpperCase(),
                      style: const TextStyle(fontSize: 24),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          user?['name'] ?? 'Mtumiaji',
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Text(
                          user?['phone'] ?? '',
                          style: TextStyle(color: Colors.grey[600]),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Notification count badge
          Card(
            child: ListTile(
              leading: const Icon(Icons.notifications),
              title: const Text('Arifa'),
              trailing: _unreadCount > 0
                  ? Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.red,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        '$_unreadCount',
                        style: const TextStyle(color: Colors.white, fontSize: 12),
                      ),
                    )
                  : null,
            ),
          ),

          // Language toggle
          Card(
            child: ListTile(
              leading: const Icon(Icons.language),
              title: const Text('Lugha'),
              subtitle: const Text('Kiswahili'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: () {},
            ),
          ),

          // About
          Card(
            child: ListTile(
              leading: const Icon(Icons.info),
              title: const Text('Kuhusu Urban Shopper'),
              subtitle: const Text('Toleo 0.1.0'),
            ),
          ),

          const SizedBox(height: 24),

          // Logout
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () async {
                await auth.logout();
                if (context.mounted) {
                  Navigator.of(context).pushAndRemoveUntil(
                    MaterialPageRoute(
                        builder: (_) => const Scaffold()),
                    (route) => false,
                  );
                }
              },
              icon: const Icon(Icons.logout, color: Colors.red),
              label: const Text('Toka',
                  style: TextStyle(color: Colors.red)),
            ),
          ),
        ],
      ),
    );
  }
}
