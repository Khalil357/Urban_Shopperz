import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/order_service.dart';
import '../models/order.dart' as models;
import 'create_order_screen.dart';
import 'order_detail_screen.dart';
import 'orders_screen.dart';
import 'profile_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _navIndex = 0;
  models.Order? _activeOrder;

  @override
  void initState() {
    super.initState();
    _loadActiveOrder();
  }

  Future<void> _loadActiveOrder() async {
    try {
      final auth = context.read<AuthService>();
      final orderService = OrderService(auth.api);
      final orders = await orderService.getCustomerOrders();
      final active = orders.where((o) => o.isActive).toList();
      if (mounted) setState(() => _activeOrder = active.isNotEmpty ? active.first : null);
    } catch (_) {}
  }

  void _onOrderCreated() {
    _loadActiveOrder();
    setState(() => _navIndex = 0);
  }

  @override
  Widget build(BuildContext context) {
    final screens = [
      _HomeTab(
        activeOrder: _activeOrder,
        onNewOrder: () async {
          final created = await Navigator.push<bool>(
            context,
            MaterialPageRoute(builder: (_) => const CreateOrderScreen()),
          );
          if (created == true) _onOrderCreated();
        },
        onOrderTap: () => Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => OrderDetailScreen(orderId: _activeOrder!.id),
          ),
        ),
        onRefresh: _loadActiveOrder,
      ),
      const OrdersScreen(),
      const ProfileScreen(),
    ];

    return Scaffold(
      body: screens[_navIndex],
      bottomNavigationBar: NavigationBar(
        selectedIndex: _navIndex,
        onDestinationSelected: (i) => setState(() => _navIndex = i),
        destinations: const [
          NavigationDestination(icon: Icon(Icons.home), label: 'Nyumbani'),
          NavigationDestination(icon: Icon(Icons.list), label: 'Maagizo'),
          NavigationDestination(icon: Icon(Icons.person), label: 'Akaunti'),
        ],
      ),
    );
  }
}

class _HomeTab extends StatelessWidget {
  final models.Order? activeOrder;
  final VoidCallback onNewOrder;
  final VoidCallback onOrderTap;
  final VoidCallback onRefresh;

  const _HomeTab({
    required this.activeOrder,
    required this.onNewOrder,
    required this.onOrderTap,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Urban Shopper'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () => context.read<AuthService>().logout(),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async => onRefresh(),
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Active order card
            if (activeOrder != null)
              _ActiveOrderCard(order: activeOrder!, onTap: onOrderTap),
            if (activeOrder == null)
              Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 12, vertical: 6),
                        decoration: BoxDecoration(
                          color: Colors.green[100],
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: const Text('Hakuna agizo linaloendelea'),
                      ),
                    ],
                  ),
                ),
              ),
            const SizedBox(height: 24),

            // Quick action cards
            Row(
              children: [
                Expanded(
                  child: _QuickActionCard(
                    icon: Icons.shopping_cart,
                    label: 'Agizo Jipya',
                    color: Colors.green,
                    onTap: onNewOrder,
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _QuickActionCard(
                    icon: Icons.replay,
                    label: 'Agizo la Awali',
                    color: Colors.blue,
                    onTap: () {},
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _QuickActionCard(
                    icon: Icons.notifications,
                    label: 'Arifa',
                    color: Colors.orange,
                    onTap: () {},
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: _QuickActionCard(
                    icon: Icons.receipt_long,
                    label: 'Historia',
                    color: Colors.purple,
                    onTap: () {},
                  ),
                ),
              ],
            ),

            const SizedBox(height: 32),

            // Info section
            Card(
              color: Colors.blue[50],
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Karibu Urban Shopper!',
                      style:
                          TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Agiza bidhaa sokoni na zikufikie mlangoni. '
                      'Mnuuzaji atakununulia bidhaa unazohitaji na kukuletea.',
                      style: TextStyle(color: Colors.grey[700]),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ActiveOrderCard extends StatelessWidget {
  final models.Order order;
  final VoidCallback onTap;

  const _ActiveOrderCard({required this.order, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.green[50],
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  const Icon(Icons.pending, color: Colors.green),
                  const SizedBox(width: 8),
                  Text(
                    'Agizo Linaloendelea',
                    style: TextStyle(
                      color: Colors.green[700],
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(order.orderNumber,
                  style: const TextStyle(fontWeight: FontWeight.w500)),
              const SizedBox(height: 4),
              Text(order.statusDisplay),
              const SizedBox(height: 8),
              LinearProgressIndicator(
                value: 0.6,
                backgroundColor: Colors.green[100],
                color: Colors.green,
              ),
              const SizedBox(height: 8),
              Align(
                alignment: Alignment.centerRight,
                child: Text(
                  'TZS ${order.estimatedTotal}',
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _QuickActionCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color color;
  final VoidCallback onTap;

  const _QuickActionCard({
    required this.icon,
    required this.label,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: [
              Icon(icon, size: 32, color: color),
              const SizedBox(height: 8),
              Text(label,
                  style: const TextStyle(fontWeight: FontWeight.w500)),
            ],
          ),
        ),
      ),
    );
  }
}
