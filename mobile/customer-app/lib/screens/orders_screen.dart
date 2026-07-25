import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/order_service.dart';
import '../models/order.dart' as models;
import 'order_detail_screen.dart';

class OrdersScreen extends StatefulWidget {
  const OrdersScreen({super.key});

  @override
  State<OrdersScreen> createState() => _OrdersScreenState();
}

class _OrdersScreenState extends State<OrdersScreen> {
  List<models.Order>? _orders;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadOrders();
  }

  Future<void> _loadOrders() async {
    setState(() => _loading = true);
    try {
      final auth = context.read<AuthService>();
      final orderService = OrderService(auth.api);
      final orders = await orderService.getCustomerOrders();
      if (mounted) setState(() { _orders = orders; _loading = false; });
    } catch (e) {
      if (mounted) {
        setState(() => _loading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Hitilafu: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Maagizo Yangu')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _orders == null || _orders!.isEmpty
              ? const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.receipt_long, size: 64, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('Hujawahi kuagiza',
                          style: TextStyle(fontSize: 16, color: Colors.grey)),
                    ],
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _loadOrders,
                  child: ListView.builder(
                    padding: const EdgeInsets.all(8),
                    itemCount: _orders!.length,
                    itemBuilder: (ctx, i) => _OrderCard(
                      order: _orders![i],
                      onTap: () => Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => OrderDetailScreen(
                              orderId: _orders![i].id),
                        ),
                      ),
                    ),
                  ),
                ),
    );
  }
}

class _OrderCard extends StatelessWidget {
  final models.Order order;
  final VoidCallback onTap;

  const _OrderCard({required this.order, required this.onTap});

  @override
  Widget build(BuildContext context) {
    Color statusColor;
    switch (order.status) {
      case 'DELIVERED': case 'COMPLETED': statusColor = Colors.green; break;
      case 'CANCELLED': statusColor = Colors.red; break;
      case 'IN_DELIVERY': statusColor = Colors.orange; break;
      default: statusColor = Colors.blue;
    }

    return Card(
      margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
      child: ListTile(
        onTap: onTap,
        leading: CircleAvatar(
          backgroundColor: statusColor.withOpacity(0.2),
          child: Icon(Icons.receipt, color: statusColor),
        ),
        title: Text(order.orderNumber,
            style: const TextStyle(fontWeight: FontWeight.w500)),
        subtitle: Text(order.statusDisplay),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Text('TZS ${order.estimatedTotal}',
                style: const TextStyle(fontWeight: FontWeight.bold)),
            if (order.createdAt != null)
              Text(order.createdAt!.substring(0, 10),
                  style: TextStyle(fontSize: 12, color: Colors.grey[600])),
          ],
        ),
      ),
    );
  }
}
