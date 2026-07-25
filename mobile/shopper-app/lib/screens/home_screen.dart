import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/api_client.dart';
import '../services/offer_service.dart';
import '../services/order_service.dart';
import '../models/order_offer.dart';
import 'active_order_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  bool _isOnline = false;
  List<OrderOffer> _offers = [];
  Timer? _pollTimer;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _refresh();
  }

  @override
  void dispose() {
    _pollTimer?.cancel();
    super.dispose();
  }

  Future<void> _refresh() async {
    try {
      final auth = context.read<AuthService>();
      final api = ApiClient(auth);
      final offerSvc = OfferService(api);
      final orderSvc = ShopperOrderService(api);

      final shopperId = auth.user?['id'] ?? '';
      if (shopperId.isNotEmpty) {
        _offers = await offerSvc.getPendingOffers(shopperId);
      }
      if (mounted) setState(() => _loading = false);
    } catch (_) {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _toggleOnline() async {
    setState(() => _isOnline = !_isOnline);
    if (_isOnline) {
      _refresh();
      _pollTimer = Timer.periodic(const Duration(seconds: 10), (_) => _refresh());
    } else {
      _pollTimer?.cancel();
    }
  }

  Future<void> _handleOffer(String offerId, String action) async {
    try {
      final auth = context.read<AuthService>();
      final api = ApiClient(auth);
      final offerSvc = OfferService(api);
      final shopperId = auth.user?['id'] ?? '';

      if (action == 'accept') {
        await offerSvc.acceptOffer(shopperId, offerId);
        if (mounted) {
          // Fetch active order
          final offer = _offers.firstWhere((o) => o.id == offerId);
          final orderSvc = ShopperOrderService(api);
          final active = await orderSvc.getActiveOrder(offer.orderId);
          if (active != null && mounted) {
            Navigator.push(context, MaterialPageRoute(
              builder: (_) => ActiveOrderScreen(orderId: offer.orderId),
            ));
          }
        }
      } else {
        await offerSvc.declineOffer(shopperId, offerId);
      }
      _refresh();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthService>();
    final user = auth.user;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Urban Shopper'),
        actions: [
          Switch(
            value: _isOnline,
            onChanged: (_) => _toggleOnline(),
            activeColor: Colors.green,
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () => auth.logout(),
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: _refresh,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            // Online/offline status
            Card(
              color: _isOnline ? Colors.green[50] : Colors.grey[100],
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Icon(
                      _isOnline ? Icons.check_circle : Icons.cancel,
                      color: _isOnline ? Colors.green : Colors.grey,
                      size: 32,
                    ),
                    const SizedBox(width: 12),
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          _isOnline ? 'Uko Mtandaoni' : 'Uko Nje ya Mtandao',
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: _isOnline ? Colors.green : Colors.grey,
                          ),
                        ),
                        Text(user?['name'] ?? 'Mnuuzaji'),
                      ],
                    ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 16),

            // Offers section
            if (_isOnline) ...[
              const Text('Ofa Zinazosubiri',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 8),
              if (_offers.isEmpty && !_loading)
                const Card(
                  child: Padding(
                    padding: EdgeInsets.all(24),
                    child: Center(child: Text('Hakuna ofa kwa sasa')),
                  ),
                ),
              ..._offers.map((offer) => _OfferCard(
                    offer: offer,
                    onAccept: () => _handleOffer(offer.id, 'accept'),
                    onDecline: () => _handleOffer(offer.id, 'decline'),
                  )),
            ],

            if (!_isOnline)
              const Card(
                child: Padding(
                  padding: EdgeInsets.all(24),
                  child: Center(
                    child: Text(
                      'Washa mtandao kupokea ofa',
                      style: TextStyle(fontSize: 16, color: Colors.grey),
                    ),
                  ),
                ),
              ),

            const SizedBox(height: 24),

            // Stats
            const Text('Takwimu za Leo',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Row(
              children: [
                _StatCard(icon: Icons.shopping_bag, label: 'Maagizo', value: '0'),
                const SizedBox(width: 8),
                _StatCard(icon: Icons.star, label: 'Alama', value: '0.0'),
                const SizedBox(width: 8),
                _StatCard(icon: Icons.monetization_on, label: 'Mapato', value: '0 TZS'),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _OfferCard extends StatelessWidget {
  final OrderOffer offer;
  final VoidCallback onAccept;
  final VoidCallback onDecline;

  const _OfferCard({
    required this.offer,
    required this.onAccept,
    required this.onDecline,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.blue[50],
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.shopping_cart, color: Colors.blue),
                const SizedBox(width: 8),
                const Text('Ofa Mpya!',
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const Spacer(),
                Text('Alama: ${offer.score.toStringAsFixed(1)}',
                    style: const TextStyle(fontWeight: FontWeight.w500)),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _info(Icons.inventory, '${offer.itemCount} bidhaa'),
                const SizedBox(width: 16),
                _info(Icons.directions_bike, '${offer.distanceKm.toStringAsFixed(1)} km'),
              ],
            ),
            Row(
              children: [
                _info(Icons.monetization_on, 'TZS ${offer.estimatedTotal}'),
                const SizedBox(width: 16),
                _info(Icons.local_shipping, 'TZS ${offer.estimatedDeliveryFee} usafiri'),
              ],
            ),
            if (offer.isExpired)
              const Padding(
                padding: EdgeInsets.only(top: 8),
                child: Text('Ofa imekwisha muda',
                    style: TextStyle(color: Colors.red)),
              ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton(
                    onPressed: offer.isExpired ? null : onAccept,
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.green),
                    child: const Text('Kubali', style: TextStyle(color: Colors.white)),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton(
                    onPressed: onDecline,
                    child: const Text('Kataa'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _info(IconData icon, String text) => Padding(
        padding: const EdgeInsets.symmetric(vertical: 2),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 16, color: Colors.grey[600]),
            const SizedBox(width: 4),
            Text(text, style: TextStyle(color: Colors.grey[700])),
          ],
        ),
      );
}

class _StatCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _StatCard(
      {required this.icon, required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Card(
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            children: [
              Icon(icon, color: Colors.blue),
              const SizedBox(height: 4),
              Text(value, style: const TextStyle(fontWeight: FontWeight.bold)),
              Text(label, style: const TextStyle(fontSize: 12)),
            ],
          ),
        ),
      ),
    );
  }
}
