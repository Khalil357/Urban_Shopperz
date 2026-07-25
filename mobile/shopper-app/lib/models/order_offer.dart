class OrderOffer {
  final String id;
  final String orderId;
  final String status;
  final double score;
  final double distanceKm;
  final String offeredAt;
  final String expiresAt;
  final int cascadeRound;
  final int itemCount;
  final int estimatedTotal;
  final int estimatedDeliveryFee;

  OrderOffer({
    required this.id,
    required this.orderId,
    required this.status,
    this.score = 0,
    this.distanceKm = 0,
    required this.offeredAt,
    required this.expiresAt,
    this.cascadeRound = 1,
    this.itemCount = 0,
    this.estimatedTotal = 0,
    this.estimatedDeliveryFee = 0,
  });

  factory OrderOffer.fromJson(Map<String, dynamic> json) => OrderOffer(
        id: json['id'] ?? '',
        orderId: json['orderId'] ?? '',
        status: json['status'] ?? 'pending',
        score: (json['score'] as num?)?.toDouble() ?? 0,
        distanceKm: (json['distanceKm'] as num?)?.toDouble() ?? 0,
        offeredAt: json['offeredAt'] ?? '',
        expiresAt: json['expiresAt'] ?? '',
        cascadeRound: json['cascadeRound'] ?? 1,
        itemCount: json['itemCount'] ?? 0,
        estimatedTotal: json['estimatedTotal'] ?? 0,
        estimatedDeliveryFee: json['estimatedDeliveryFee'] ?? 0,
      );

  bool get isExpired {
    try {
      return DateTime.parse(expiresAt).isBefore(DateTime.now());
    } catch (_) {
      return false;
    }
  }
}

class ActiveOrder {
  final String id;
  final String orderNumber;
  final String status;
  final int itemCount;
  final String? deliveryAddressText;
  final String shoppingPreference;
  final int estimatedTotal;

  ActiveOrder({
    required this.id,
    required this.orderNumber,
    required this.status,
    this.itemCount = 0,
    this.deliveryAddressText,
    this.shoppingPreference = 'balanced',
    this.estimatedTotal = 0,
  });

  factory ActiveOrder.fromJson(Map<String, dynamic> json) => ActiveOrder(
        id: json['id'] ?? '',
        orderNumber: json['orderNumber'] ?? '',
        status: json['status'] ?? '',
        itemCount: json['itemCount'] ?? 0,
        deliveryAddressText: json['deliveryAddressText'],
        shoppingPreference: json['shoppingPreference'] ?? 'balanced',
        estimatedTotal: json['estimatedTotal'] ?? 0,
      );

  String get statusLabel {
    switch (status) {
      case 'ACCEPTED': return 'Imehubiriwa';
      case 'TRAVELLING_TO_MARKET': return 'Naelekea Sokoni';
      case 'SHOPPING': return 'Nanunua';
      case 'SHOPPING_COMPLETE': return 'Ununuzi Umekamilika';
      case 'RECEIPT_VERIFIED': return 'Risiti Imethibitishwa';
      case 'IN_DELIVERY': return 'Nafika';
      case 'DELIVERED': return 'Nimefika';
      default: return status;
    }
  }
}
