class OrderItem {
  final String? id;
  final String name;
  final int quantity;
  final String? unit;
  final String? preferredBrand;
  final int? maxPrice;
  final String? notes;
  final String status; // requested, found, substituted, not_available
  final int? actualPrice;
  final int sortOrder;

  OrderItem({
    this.id,
    required this.name,
    required this.quantity,
    this.unit,
    this.preferredBrand,
    this.maxPrice,
    this.notes,
    this.status = 'requested',
    this.actualPrice,
    this.sortOrder = 0,
  });

  factory OrderItem.fromJson(Map<String, dynamic> json) => OrderItem(
        id: json['id'],
        name: json['name'],
        quantity: json['quantity'] ?? 1,
        unit: json['unit'],
        preferredBrand: json['preferredBrand'],
        maxPrice: json['maxPrice'],
        notes: json['notes'],
        status: json['status'] ?? 'requested',
        actualPrice: json['actualPrice'],
        sortOrder: json['sortOrder'] ?? 0,
      );

  Map<String, dynamic> toJson() => {
        'name': name,
        'quantity': quantity,
        'unit': unit,
        'preferredBrand': preferredBrand,
        'maxPrice': maxPrice,
        'notes': notes,
      };
}

class Order {
  final String id;
  final String orderNumber;
  final String customerId;
  final String? shopperId;
  final String? marketId;
  final String zoneId;
  final String status;
  final String shoppingPreference;
  final String deliveryPreference;
  final String paymentMethod;
  final double? deliveryLat;
  final double? deliveryLng;
  final String? deliveryAddressText;
  final String? deliveryLandmark;
  final int estimatedItemCost;
  final int estimatedServiceFee;
  final int estimatedDeliveryFee;
  final int estimatedTotal;
  final int? actualItemCost;
  final int? actualTotal;
  final int itemCount;
  final String? cancellationReason;
  final String? createdAt;
  final String? updatedAt;

  Order({
    required this.id,
    required this.orderNumber,
    required this.customerId,
    this.shopperId,
    this.marketId,
    required this.zoneId,
    required this.status,
    this.shoppingPreference = 'balanced',
    this.deliveryPreference = 'asap',
    this.paymentMethod = 'mpesa',
    this.deliveryLat,
    this.deliveryLng,
    this.deliveryAddressText,
    this.deliveryLandmark,
    this.estimatedItemCost = 0,
    this.estimatedServiceFee = 0,
    this.estimatedDeliveryFee = 0,
    this.estimatedTotal = 0,
    this.actualItemCost,
    this.actualTotal,
    this.itemCount = 0,
    this.cancellationReason,
    this.createdAt,
    this.updatedAt,
  });

  factory Order.fromJson(Map<String, dynamic> json) => Order(
        id: json['id'] ?? '',
        orderNumber: json['orderNumber'] ?? '',
        customerId: json['customerId'] ?? '',
        shopperId: json['shopperId'],
        marketId: json['marketId'],
        zoneId: json['zoneId'] ?? '',
        status: json['status'] ?? 'CREATED',
        shoppingPreference: json['shoppingPreference'] ?? 'balanced',
        deliveryPreference: json['deliveryPreference'] ?? 'asap',
        paymentMethod: json['paymentMethod'] ?? 'mpesa',
        deliveryLat: (json['deliveryLat'] as num?)?.toDouble(),
        deliveryLng: (json['deliveryLng'] as num?)?.toDouble(),
        deliveryAddressText: json['deliveryAddressText'],
        deliveryLandmark: json['deliveryLandmark'],
        estimatedItemCost: json['estimatedItemCost'] ?? 0,
        estimatedServiceFee: json['estimatedServiceFee'] ?? 0,
        estimatedDeliveryFee: json['estimatedDeliveryFee'] ?? 0,
        estimatedTotal: json['estimatedTotal'] ?? 0,
        actualItemCost: json['actualItemCost'],
        actualTotal: json['actualTotal'],
        itemCount: json['itemCount'] ?? 0,
        cancellationReason: json['cancellationReason'],
        createdAt: json['createdAt'],
        updatedAt: json['updatedAt'],
      );

  String get statusDisplay {
    switch (status) {
      case 'CREATED': return 'Imeundwa';
      case 'AWAITING_PAYMENT_VERIFICATION': return 'Inasubiri malipo';
      case 'QUEUED_FOR_ASSIGNMENT': return 'Inatafuta mnuuzaji';
      case 'OFFERED': return 'Ofa imetumwa';
      case 'ACCEPTED': return 'Imekubaliwa';
      case 'TRAVELLING_TO_MARKET': return 'Anaelekea sokoni';
      case 'SHOPPING': return 'Ananunua';
      case 'SHOPPING_COMPLETE': return 'Ununuzi umekamilika';
      case 'RECEIPT_VERIFIED': return 'Risiti imethibitishwa';
      case 'IN_DELIVERY': return 'Inafika';
      case 'DELIVERED': return 'Imefika';
      case 'COMPLETED': return 'Imekamilika';
      case 'CANCELLED': return 'Imeghairiwa';
      default: return status;
    }
  }

  bool get isActive => !['COMPLETED', 'CANCELLED', 'ARCHIVED'].contains(status);
  bool get isDelivered => status == 'DELIVERED' || status == 'COMPLETED';
}

class OrderStatusResponse {
  final String orderId;
  final String orderNumber;
  final String status;
  final int totalItems;
  final int itemsFound;
  final int itemsSubstituted;
  final int itemsUnavailable;
  final int itemsPending;
  final int estimatedTotal;
  final List<TimelineEvent> timeline;

  OrderStatusResponse({
    required this.orderId,
    required this.orderNumber,
    required this.status,
    this.totalItems = 0,
    this.itemsFound = 0,
    this.itemsSubstituted = 0,
    this.itemsUnavailable = 0,
    this.itemsPending = 0,
    this.estimatedTotal = 0,
    this.timeline = const [],
  });

  factory OrderStatusResponse.fromJson(Map<String, dynamic> json) =>
      OrderStatusResponse(
        orderId: json['orderId'] ?? '',
        orderNumber: json['orderNumber'] ?? '',
        status: json['status'] ?? '',
        totalItems: json['totalItems'] ?? 0,
        itemsFound: json['itemsFound'] ?? 0,
        itemsSubstituted: json['itemsSubstituted'] ?? 0,
        itemsUnavailable: json['itemsUnavailable'] ?? 0,
        itemsPending: json['itemsPending'] ?? 0,
        estimatedTotal: json['estimatedTotal'] ?? 0,
        timeline: (json['timeline'] as List? ?? [])
            .map((t) => TimelineEvent.fromJson(t))
            .toList(),
      );
}

class TimelineEvent {
  final String fromStatus;
  final String toStatus;
  final String triggerEvent;
  final String timestamp;

  TimelineEvent({
    required this.fromStatus,
    required this.toStatus,
    required this.triggerEvent,
    required this.timestamp,
  });

  factory TimelineEvent.fromJson(Map<String, dynamic> json) => TimelineEvent(
        fromStatus: json['fromStatus'] ?? '',
        toStatus: json['toStatus'] ?? '',
        triggerEvent: json['triggerEvent'] ?? '',
        timestamp: json['timestamp'] ?? '',
      );
}
