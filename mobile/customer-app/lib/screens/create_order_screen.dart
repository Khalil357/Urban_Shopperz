import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/order_service.dart';

class CreateOrderScreen extends StatefulWidget {
  const CreateOrderScreen({super.key});

  @override
  State<CreateOrderScreen> createState() => _CreateOrderScreenState();
}

class _CreateOrderScreenState extends State<CreateOrderScreen> {
  final _items = <_OrderItemEntry>[];
  final _addressController = TextEditingController();
  final _landmarkController = TextEditingController();
  String _shoppingPreference = 'balanced';
  String _deliveryTime = 'asap';
  String _paymentMethod = 'mpesa';
  String _substitutionDefault = 'contact_me';
  bool _loading = false;

  @override
  void dispose() {
    _addressController.dispose();
    _landmarkController.dispose();
    super.dispose();
  }

  void _addItem() {
    setState(() => _items.add(_OrderItemEntry()));
  }

  void _removeItem(int index) {
    setState(() => _items.removeAt(index));
  }

  int get _totalEstimate {
    return _items.fold(0, (sum, item) {
      final price = item.maxPriceCtrl.text.isNotEmpty
          ? int.tryParse(item.maxPriceCtrl.text) ?? 0
          : 0;
      final qty = int.tryParse(item.qtyCtrl.text) ?? 1;
      return sum + (price * qty);
    });
  }

  Future<void> _submitOrder() async {
    if (_items.isEmpty || _addressController.text.isEmpty) {
      _showSnack('Tafadhali jaza bidhaa na anwani ya uwasilishaji');
      return;
    }

    setState(() => _loading = true);

    try {
      final auth = context.read<AuthService>();
      final orderService = OrderService(auth.api);

      final items = _items.map((item) => {
            'name': item.nameCtrl.text,
            'quantity': int.tryParse(item.qtyCtrl.text) ?? 1,
            if (item.unitCtrl.text.isNotEmpty) 'unit': item.unitCtrl.text,
            if (item.brandCtrl.text.isNotEmpty) 'preferredBrand': item.brandCtrl.text,
            if (item.maxPriceCtrl.text.isNotEmpty)
              'maxPrice': int.tryParse(item.maxPriceCtrl.text),
            if (item.notesCtrl.text.isNotEmpty) 'notes': item.notesCtrl.text,
          }).toList();

      final order = await orderService.createOrder(
        zoneId: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', // Mikocheni
        marketId: 'a1eebc99-9c0b-4ef8-bb6d-6bb9bd380a01', // Mikocheni B Market
        deliveryLat: -6.776,
        deliveryLng: 39.263,
        deliveryAddressText: _addressController.text,
        deliveryLandmark: _landmarkController.text.isNotEmpty
            ? _landmarkController.text
            : null,
        shoppingPreference: _shoppingPreference,
        deliveryTime: _deliveryTime,
        paymentMethod: _paymentMethod,
        substitutionDefault: _substitutionDefault,
        items: items,
      );

      if (mounted) {
        setState(() => _loading = false);
        _showSnack('Agizo limeundwa! Namba: ${order.orderNumber}');
        Navigator.of(context).pop(true); // Return true = order created
      }
    } catch (e) {
      if (mounted) {
        setState(() => _loading = false);
        _showSnack('Hitilafu: $e');
      }
    }
  }

  void _showSnack(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Agizo Jipya')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          // ── SECTION: Items ──
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                'Bidhaa',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              TextButton.icon(
                onPressed: _addItem,
                icon: const Icon(Icons.add),
                label: const Text('Ongeza'),
              ),
            ],
          ),
          if (_items.isEmpty)
            const Card(
              child: Padding(
                padding: EdgeInsets.all(24),
                child: Center(child: Text('Bonyeza "Ongeza" kuongeza bidhaa')),
              ),
            ),
          ..._items.asMap().entries.map((entry) => _ItemCard(
                index: entry.key,
                item: entry.value,
                onRemove: () => _removeItem(entry.key),
              )),

          const SizedBox(height: 24),

          // ── SECTION: Delivery ──
          const Text(
            'Anwani ya Uwasilishaji',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _addressController,
            maxLines: 2,
            decoration: const InputDecoration(
              hintText: 'Mtaa, jina la nyumba, jengo...',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.location_on),
            ),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _landmarkController,
            decoration: const InputDecoration(
              hintText: 'Alama ya kutambua (hiari)',
              border: OutlineInputBorder(),
              prefixIcon: Icon(Icons.flag),
            ),
          ),

          const SizedBox(height: 24),

          // ── SECTION: Preferences ──
          const Text(
            'Mapendekezo',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          DropdownButtonFormField<String>(
            value: _shoppingPreference,
            decoration: const InputDecoration(
              labelText: 'Upendeleo wa Ununuzi',
              border: OutlineInputBorder(),
            ),
            items: const [
              DropdownMenuItem(value: 'cheapest', child: Text('Bei Nafuu')),
              DropdownMenuItem(value: 'balanced', child: Text('Sawa (Bei + Ubora)')),
              DropdownMenuItem(value: 'best_quality', child: Text('Ubora Bora')),
            ],
            onChanged: (v) => setState(() => _shoppingPreference = v!),
          ),
          const SizedBox(height: 8),
          DropdownButtonFormField<String>(
            value: _deliveryTime,
            decoration: const InputDecoration(
              labelText: 'Muda wa Uwasilishaji',
              border: OutlineInputBorder(),
            ),
            items: const [
              DropdownMenuItem(value: 'asap', child: Text('Haraka iwezekanavyo')),
              DropdownMenuItem(value: 'scheduled', child: Text('Kwa Ratiba')),
            ],
            onChanged: (v) => setState(() => _deliveryTime = v!),
          ),
          const SizedBox(height: 8),
          DropdownButtonFormField<String>(
            value: _substitutionDefault,
            decoration: const InputDecoration(
              labelText: 'Mbadala wa Bidhaa',
              border: OutlineInputBorder(),
            ),
            items: const [
              DropdownMenuItem(
                  value: 'best_match', child: Text('Mbadala bora zaidi')),
              DropdownMenuItem(
                  value: 'contact_me', child: Text('Wasiliana nami')),
              DropdownMenuItem(
                  value: 'no_substitutions', child: Text('Usibadilishe')),
            ],
            onChanged: (v) => setState(() => _substitutionDefault = v!),
          ),
          const SizedBox(height: 8),
          DropdownButtonFormField<String>(
            value: _paymentMethod,
            decoration: const InputDecoration(
              labelText: 'Njia ya Malipo',
              border: OutlineInputBorder(),
            ),
            items: const [
              DropdownMenuItem(value: 'mpesa', child: Text('M-Pesa')),
              DropdownMenuItem(value: 'mixx', child: Text('Mixx')),
            ],
            onChanged: (v) => setState(() => _paymentMethod = v!),
          ),

          const SizedBox(height: 24),

          // ── SECTION: Submit ──
          if (_items.isNotEmpty)
            Card(
              color: Colors.green[50],
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      'Kadirio la Jumla:',
                      style: TextStyle(fontSize: 16),
                    ),
                    Text(
                      'TZS ${_totalEstimate.toStringAsFixed(0)}',
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: _loading ? null : _submitOrder,
              child: _loading
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text('Tuma Agizo', style: TextStyle(fontSize: 16)),
            ),
          ),
          const SizedBox(height: 32),
        ],
      ),
    );
  }
}

// ── Item Entry Card ──

class _OrderItemEntry {
  final nameCtrl = TextEditingController();
  final qtyCtrl = TextEditingController(text: '1');
  final unitCtrl = TextEditingController();
  final brandCtrl = TextEditingController();
  final maxPriceCtrl = TextEditingController();
  final notesCtrl = TextEditingController();
}

class _ItemCard extends StatelessWidget {
  final int index;
  final _OrderItemEntry item;
  final VoidCallback onRemove;

  const _ItemCard({
    required this.index,
    required this.item,
    required this.onRemove,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text('Bidhaa #${index + 1}',
                    style: const TextStyle(fontWeight: FontWeight.bold)),
                const Spacer(),
                IconButton(
                    icon: const Icon(Icons.close, size: 20),
                    onPressed: onRemove),
              ],
            ),
            Row(
              children: [
                Expanded(
                  flex: 3,
                  child: TextField(
                    controller: item.nameCtrl,
                    decoration: const InputDecoration(
                      labelText: 'Jina la bidhaa',
                      hintText: 'Mchele, Nyanya...',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: item.qtyCtrl,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Idadi',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: item.unitCtrl,
                    decoration: const InputDecoration(
                      labelText: 'Kipimo',
                      hintText: 'kg, pcs, lita',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: item.brandCtrl,
                    decoration: const InputDecoration(
                      labelText: 'Chapa (hiari)',
                      hintText: 'Taifa, Sera...',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: item.maxPriceCtrl,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: 'Bei max (TZS)',
                      hintText: '5000',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: item.notesCtrl,
                    decoration: const InputDecoration(
                      labelText: 'Maelezo (hiari)',
                      hintText: 'Si ngumu sana...',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
