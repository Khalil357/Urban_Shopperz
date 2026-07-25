import '../models/order.dart';
import 'api_client.dart';

class OrderService {
  final ApiClient _api;

  OrderService(this._api);

  Future<Order> createOrder({
    required String zoneId,
    String? marketId,
    required double deliveryLat,
    required double deliveryLng,
    required String deliveryAddressText,
    String? deliveryLandmark,
    required String shoppingPreference,
    required String deliveryTime,
    required String paymentMethod,
    required String substitutionDefault,
    required List<Map<String, dynamic>> items,
  }) async {
    final body = {
      'zoneId': zoneId,
      'marketId': marketId,
      'deliveryLocation': {
        'latitude': deliveryLat,
        'longitude': deliveryLng,
        'addressText': deliveryAddressText,
        'landmark': deliveryLandmark,
      },
      'shoppingPreference': shoppingPreference,
      'deliveryTime': deliveryTime,
      'paymentMethod': paymentMethod,
      'substitutionDefault': substitutionDefault,
      'items': items,
    };

    final data = await _api.post('/orders', body: body);
    return Order.fromJson(data);
  }

  Future<Order> getOrder(String id) async {
    final data = await _api.get('/orders/$id');
    return Order.fromJson(data);
  }

  Future<OrderStatusResponse> getOrderStatus(String id) async {
    final data = await _api.get('/orders/$id/status');
    return OrderStatusResponse.fromJson(data);
  }

  Future<List<Order>> getCustomerOrders() async {
    // Customer orders are fetched through the customer endpoint
    // For simplicity, this returns from the generic endpoint
    final list = await _api.getList('/orders');
    return list.map((j) => Order.fromJson(j as Map<String, dynamic>)).toList();
  }

  Future<List<OrderItem>> getOrderItems(String orderId) async {
    final list = await _api.getList('/orders/$orderId/items');
    return list.map((j) => OrderItem.fromJson(j as Map<String, dynamic>)).toList();
  }

  Future<Order> cancelOrder(String id, {String reason = 'Customer requested'}) async {
    final data = await _api.post('/orders/$id/cancel', body: {'reason': reason});
    return Order.fromJson(data);
  }
}
