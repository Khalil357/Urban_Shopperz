import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/api_client.dart';
import '../services/order_service.dart';
import '../models/order_offer.dart';

class ActiveOrderScreen extends StatefulWidget {
  final String orderId;
  const ActiveOrderScreen({super.key, required this.orderId});

  @override
  State<ActiveOrderScreen> createState() => _ActiveOrderScreenState();
}

class _ActiveOrderScreenState extends State<ActiveOrderScreen> {
  ActiveOrder? _order;
  List<dynamic> _items = [];
  String _step = 'arrive'; // arrive → shop → receipt → deliver → confirm
  bool _loading = true;
  final _etaCtrl = TextEditingController(text: '15');

  @override
  void initState() {
    super.initState();
    _load();
  }

  @override
  void dispose() { _etaCtrl.dispose(); super.dispose(); }

  Future<void> _load() async {
    try {
      final auth = context.read<AuthService>();
      final api = ApiClient(auth);
      final svc = ShopperOrderService(api);
      final order = await svc.getActiveOrder(widget.orderId);

      // Get items
      List<dynamic> items = [];
      try {
        items = await api.getList('/orders/${widget.orderId}/items');
      } catch (_) {}

      if (mounted) {
        setState(() {
          _order = order;
          _items = items;
          _loading = false;
          if (order != null) {
            if (order.status == 'DELIVERED') _step = 'done';
            else if (order.status == 'IN_DELIVERY') _step = 'done';
            else if (order.status == 'SHOPPING' || order.status == 'SHOPPING_COMPLETE') _step = 'receipt';
            else if (order.status == 'TRAVELLING_TO_MARKET') _step = 'shop';
            else _step = 'arrive';
          }
        });
      }
    } catch (e) {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _doStep(String action, [Map<String, dynamic>? body]) async {
    try {
      final auth = context.read<AuthService>();
      final api = ApiClient(auth);
      final svc = ShopperOrderService(api);

      switch (action) {
        case 'arrive':
          await svc.arriveAtMarket(widget.orderId);
          break;
        case 'found':
          await svc.markItemStatus(widget.orderId, body!['itemId'], 'found',
              actualPrice: body['price'] as int?);
          break;
        case 'receipt':
          await svc.uploadReceipt(widget.orderId, 'photo', body!['total'] as int);
          break;
        case 'deliver':
          await svc.startDelivery(widget.orderId, int.tryParse(_etaCtrl.text) ?? 15);
          break;
        case 'confirm':
          await svc.confirmDelivery(widget.orderId, lat: -6.776, lng: 39.263);
          break;
        case 'complete':
          await svc.completeDelivery(widget.orderId);
          break;
      }
      _load();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Agizo: ${_order?.orderNumber ?? ''}')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.all(16),
              children: [
                // Status
                Card(
                  color: Colors.blue[50],
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Icon(_stepIcon, size: 48, color: Colors.blue),
                        const SizedBox(height: 8),
                        Text(_order?.statusLabel ?? '',
                            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                        Text('Bidhaa: ${_order?.itemCount ?? 0}'),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),

                // Step cards
                if (_step == 'arrive')
                  _ActionCard(
                    icon: Icons.directions_walk,
                    title: 'Step 1: Fika Sokoni',
                    subtitle: 'Bonyeza unapofika sokoni',
                    buttonText: 'Nimefika Sokoni',
                    onPressed: () => _doStep('arrive'),
                  ),

                if (_step == 'shop')
                  ...List.generate(_items.length, (i) {
                    final item = _items[i];
                    return _ActionCard(
                      icon: Icons.shopping_cart,
                      title: item['name'] ?? 'Bidhaa #${i + 1}',
                      subtitle: 'Idadi: ${item['quantity'] ?? 1}',
                      buttonText: 'Nimeipata',
                      priceField: true,
                      onPressed: (price) => _doStep('found', {
                        'itemId': item['id'],
                        'price': price,
                      }),
                    );
                  }),

                if (_step == 'receipt')
                  _ActionCard(
                    icon: Icons.receipt,
                    title: 'Step: Pakia Risiti',
                    subtitle: 'Pakia risiti ya ununuzi',
                    buttonText: 'Risiti Imepakiwa',
                    priceField: true,
                    priceLabel: 'Jumla ya Risiti (TZS)',
                    onPressed: (total) => _doStep('receipt', {'total': total}),
                  ),

                if (_step == 'deliver')
                  Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        children: [
                          const Icon(Icons.delivery_dining, size: 40, color: Colors.orange),
                          const SizedBox(height: 8),
                          const Text('Anza Usafirishaji',
                              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                          TextField(
                            controller: _etaCtrl,
                            keyboardType: TextInputType.number,
                            decoration: const InputDecoration(
                              labelText: 'Muda wa kufika (dakika)',
                              border: OutlineInputBorder(),
                            ),
                          ),
                          const SizedBox(height: 12),
                          ElevatedButton.icon(
                            onPressed: () => _doStep('deliver'),
                            icon: const Icon(Icons.play_arrow),
                            label: const Text('Anza Kusafirisha'),
                          ),
                        ],
                      ),
                    ),
                  ),

                if (_step == 'confirm')
                  _ActionCard(
                    icon: Icons.check_circle,
                    title: 'Thibitisha Uwasilishaji',
                    subtitle: 'Bonyeza umefika kwa mteja',
                    buttonText: 'Nimefika',
                    onPressed: (_) => _doStep('confirm'),
                  ),

                if (_order?.status == 'DELIVERED')
                  _ActionCard(
                    icon: Icons.done_all,
                    title: 'Uwasilishaji Umekamilika',
                    subtitle: 'Malipo yataingia pochi yako baada ya saa 48',
                    buttonText: 'Maliza Agizo',
                    onPressed: (_) => _doStep('complete'),
                  ),

                if (_order?.status == 'COMPLETED')
                  const Card(
                    color: Colors.green,
                    child: Padding(
                      padding: EdgeInsets.all(24),
                      child: Center(
                        child: Text('Agizo Limekamilika!',
                            style: TextStyle(
                                color: Colors.white,
                                fontSize: 18,
                                fontWeight: FontWeight.bold)),
                      ),
                    ),
                  ),
              ],
            ),
    );
  }

  IconData get _stepIcon {
    switch (_step) {
      case 'arrive': return Icons.directions_walk;
      case 'shop': return Icons.shopping_cart;
      case 'receipt': return Icons.receipt;
      case 'deliver': return Icons.delivery_dining;
      case 'confirm': return Icons.check_circle;
      default: return Icons.done;
    }
  }
}

class _ActionCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final String buttonText;
  final bool priceField;
  final String priceLabel;
  final Function(int?)? onPressed;

  const _ActionCard({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.buttonText,
    this.priceField = false,
    this.priceLabel = 'Bei halisi (TZS)',
    this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    final priceCtrl = TextEditingController();
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Icon(icon, size: 36, color: Colors.blue),
            const SizedBox(height: 8),
            Text(title, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            Text(subtitle, style: TextStyle(color: Colors.grey[600])),
            if (priceField) ...[
              const SizedBox(height: 8),
              TextField(
                controller: priceCtrl,
                keyboardType: TextInputType.number,
                decoration: InputDecoration(
                  labelText: priceLabel,
                  border: const OutlineInputBorder(),
                ),
              ),
            ],
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: () {
                if (onPressed != null) {
                  final price = priceField && priceCtrl.text.isNotEmpty
                      ? int.tryParse(priceCtrl.text)
                      : null;
                  onPressed!(price);
                }
              },
              child: Text(buttonText),
            ),
          ],
        ),
      ),
    );
  }
}
