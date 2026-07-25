import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/order.dart';
import '../models/delivery.dart';
import '../services/auth_service.dart';
import '../services/order_service.dart';
import '../services/delivery_service.dart';
import 'rating_screen.dart';

class OrderDetailScreen extends StatefulWidget {
  final String orderId;

  const OrderDetailScreen({super.key, required this.orderId});

  @override
  State<OrderDetailScreen> createState() => _OrderDetailScreenState();
}

class _OrderDetailScreenState extends State<OrderDetailScreen> {
  Order? _order;
  OrderStatusResponse? _status;
  DeliveryEta? _eta;
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _loading = true);
    try {
      final auth = context.read<AuthService>();
      final orderService = OrderService(auth.api);
      final deliveryService = DeliveryService(auth.api);

      final order = await orderService.getOrder(widget.orderId);
      final status = await orderService.getOrderStatus(widget.orderId);

      DeliveryEta? eta;
      try {
        eta = await deliveryService.getDeliveryEta(widget.orderId);
      } catch (_) {}

      if (mounted) {
        setState(() {
          _order = order;
          _status = status;
          _eta = eta;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _cancelOrder() async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Ghairi Agizo'),
        content: const Text('Una uhakika unataka kughairi agizo hili?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Hapana')),
          ElevatedButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Ndiyo')),
        ],
      ),
    );

    if (confirm == true) {
      try {
        final auth = context.read<AuthService>();
        await OrderService(auth.api).cancelOrder(widget.orderId);
        _loadData();
      } catch (e) {
        if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_order?.orderNumber ?? 'Agizo')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('Hitilafu: $_error'))
              : RefreshIndicator(
                  onRefresh: _loadData,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      _buildStatusHeader(),
                      if (_order?.isDelivered == true && _status?.timeline.length != null)
                        _buildRatingPrompt(),
                      const SizedBox(height: 16),
                      _buildPriceCard(),
                      const SizedBox(height: 16),
                      _buildDeliveryInfo(),
                      const SizedBox(height: 16),
                      if (_status != null) _buildTimeline(),
                      if (_order?.isActive == true) ...[
                        const SizedBox(height: 24),
                        SizedBox(
                          width: double.infinity,
                          child: OutlinedButton.icon(
                            onPressed: _cancelOrder,
                            icon: const Icon(Icons.cancel, color: Colors.red),
                            label: const Text('Ghairi Agizo',
                                style: TextStyle(color: Colors.red)),
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
    );
  }

  Widget _buildStatusHeader() {
    final status = _order?.status ?? '';
    final isActive = _order?.isActive ?? false;

    Color statusColor;
    IconData statusIcon;
    if (isActive && status == 'ACCEPTED' || status == 'TRAVELLING_TO_MARKET') {
      statusColor = Colors.blue;
      statusIcon = Icons.shopping_bag;
    } else if (status == 'IN_DELIVERY') {
      statusColor = Colors.orange;
      statusIcon = Icons.delivery_dining;
    } else if (status == 'DELIVERED' || status == 'COMPLETED') {
      statusColor = Colors.green;
      statusIcon = Icons.check_circle;
    } else if (status == 'CANCELLED') {
      statusColor = Colors.red;
      statusIcon = Icons.cancel;
    } else {
      statusColor = Colors.grey;
      statusIcon = Icons.pending;
    }

    return Card(
      color: statusColor.withOpacity(0.1),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(statusIcon, size: 40, color: statusColor),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _order?.statusDisplay ?? status,
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: statusColor,
                    ),
                  ),
                  if (_eta != null && _eta!.etaAt != null)
                    Text('Inatarajiwa: ${_eta!.etaAt}'),
                  if (_eta?.delayMinutes != null && _eta!.delayMinutes > 0)
                    Text(
                      'Imechelewa dakika ${_eta!.delayMinutes}',
                      style: const TextStyle(color: Colors.red),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildPriceCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Bei', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const Divider(),
            _priceRow('Bidhaa', _order?.estimatedItemCost ?? 0),
            _priceRow('Ada ya Huduma', _order?.estimatedServiceFee ?? 0),
            _priceRow('Usafirishaji', _order?.estimatedDeliveryFee ?? 0),
            const Divider(),
            _priceRow('Jumla', _order?.estimatedTotal ?? 0, bold: true),
          ],
        ),
      ),
    );
  }

  Widget _priceRow(String label, int amount, {bool bold = false}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: bold ? const TextStyle(fontWeight: FontWeight.bold) : null),
          Text('TZS ${amount.toStringAsFixed(0)}',
              style: bold ? const TextStyle(fontWeight: FontWeight.bold, fontSize: 16) : null),
        ],
      ),
    );
  }

  Widget _buildDeliveryInfo() {
    if (_order?.deliveryAddressText == null) return const SizedBox();
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Anwani ya Uwasilishaji',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.location_on, color: Colors.red),
                const SizedBox(width: 8),
                Expanded(child: Text(_order!.deliveryAddressText!)),
              ],
            ),
            if (_order?.deliveryLandmark != null) ...[
              const SizedBox(height: 4),
              Row(
                children: [
                  const Icon(Icons.flag, size: 16),
                  const SizedBox(width: 8),
                  Text('Karibu na ${_order!.deliveryLandmark}'),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildRatingPrompt() {
    return Card(
      color: Colors.amber[50],
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            const Icon(Icons.star, color: Colors.amber, size: 32),
            const SizedBox(width: 12),
            const Expanded(child: Text('Mwamini mnuuzaji wako!')),
            TextButton(
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => RatingScreen(
                    orderId: widget.orderId,
                    shopperId: _order?.shopperId ?? '',
                  ),
                ),
              ),
              child: const Text('Pima'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeline() {
    final events = _status?.timeline ?? [];
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Historia',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            ...events.map((event) {
              final isLast = events.last == event;
              return _TimelineItem(
                event: event,
                isLast: isLast,
              );
            }),
          ],
        ),
      ),
    );
  }
}

class _TimelineItem extends StatelessWidget {
  final TimelineEvent event;
  final bool isLast;

  const _TimelineItem({required this.event, required this.isLast});

  @override
  Widget build(BuildContext context) {
    return IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 24,
            child: Column(
              children: [
                Container(
                  width: 12,
                  height: 12,
                  decoration: BoxDecoration(
                    color: Colors.green,
                    shape: BoxShape.circle,
                  ),
                ),
                if (!isLast)
                  Expanded(
                    child: Container(width: 2, color: Colors.green[200]),
                  ),
              ],
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.only(bottom: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _eventLabel(event),
                    style: const TextStyle(fontWeight: FontWeight.w500),
                  ),
                  Text(
                    _formatTime(event.timestamp),
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  String _eventLabel(TimelineEvent e) {
    switch (e.triggerEvent) {
      case 'OrderSubmitted': return 'Agizo limewasilishwa';
      case 'PaymentVerified': return 'Malipo yamethibitishwa';
      case 'OrderReadyForAssignment': return 'Inatafuta mnuuzaji';
      case 'ShopperAcceptedOffer': return 'Mnuuzaji amekubali';
      case 'ShopperEnRoute': return 'Anaelekea sokoni';
      case 'ShopperArrivedAtMarket': return 'Amefika sokoni';
      case 'ReceiptUploadedAndVerified': return 'Risiti imepokelewa';
      case 'DeliveryStarted': return 'Usafirishaji umeanza';
      case 'ShopperArrivedAndDelivered': return 'Bidhaa zimefika';
      case 'InspectionWindowElapsed': return 'Agizo limekamilika';
      default: return e.triggerEvent;
    }
  }

  String _formatTime(String iso) {
    try {
      return iso.substring(11, 19); // Extract HH:MM:SS from ISO
    } catch (_) {
      return iso;
    }
  }
}
