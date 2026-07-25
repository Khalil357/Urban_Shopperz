class Delivery {
  final String id;
  final String orderId;
  final String shopperId;
  final String customerId;
  final String status;
  final String? etaAt;
  final String? originalEtaAt;
  final String? arrivalAt;
  final int delayMinutes;
  final String? delayReason;
  final String? recipientName;
  final bool customerConfirmed;
  final String? inspectionDeadline;
  final String? completedAt;

  Delivery({
    required this.id,
    required this.orderId,
    required this.shopperId,
    required this.customerId,
    required this.status,
    this.etaAt,
    this.originalEtaAt,
    this.arrivalAt,
    this.delayMinutes = 0,
    this.delayReason,
    this.recipientName,
    this.customerConfirmed = false,
    this.inspectionDeadline,
    this.completedAt,
  });

  factory Delivery.fromJson(Map<String, dynamic> json) => Delivery(
        id: json['id'] ?? '',
        orderId: json['orderId'] ?? '',
        shopperId: json['shopperId'] ?? '',
        customerId: json['customerId'] ?? '',
        status: json['status'] ?? 'pending',
        etaAt: json['etaAt'],
        originalEtaAt: json['originalEtaAt'],
        arrivalAt: json['arrivalAt'],
        delayMinutes: json['delayMinutes'] ?? 0,
        delayReason: json['delayReason'],
        recipientName: json['recipientName'],
        customerConfirmed: json['customerConfirmed'] ?? false,
        inspectionDeadline: json['inspectionDeadline'],
        completedAt: json['completedAt'],
      );
}

class DeliveryEta {
  final String orderId;
  final String orderStatus;
  final String deliveryStatus;
  final String? etaAt;
  final int delayMinutes;
  final String? delayReason;

  DeliveryEta({
    required this.orderId,
    required this.orderStatus,
    required this.deliveryStatus,
    this.etaAt,
    this.delayMinutes = 0,
    this.delayReason,
  });

  factory DeliveryEta.fromJson(Map<String, dynamic> json) => DeliveryEta(
        orderId: json['orderId'] ?? '',
        orderStatus: json['orderStatus'] ?? '',
        deliveryStatus: json['deliveryStatus'] ?? '',
        etaAt: json['etaAt'],
        delayMinutes: json['delayMinutes'] ?? 0,
        delayReason: json['delayReason'],
      );
}
