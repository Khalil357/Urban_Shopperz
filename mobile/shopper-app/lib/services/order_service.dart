import '../models/order_offer.dart';
import 'api_client.dart';

class ShopperOrderService {
  final ApiClient _api;

  ShopperOrderService(this._api);

  Future<ActiveOrder?> getActiveOrder(String orderId) async {
    try {
      final data = await _api.get('/orders/$orderId');
      return ActiveOrder.fromJson(data);
    } catch (_) {
      return null;
    }
  }

  Future<void> arriveAtMarket(String orderId) async {
    await _api.post('/orders/$orderId/arrive');
  }

  Future<void> markItemStatus(String orderId, String itemId, String status, {int? actualPrice}) async {
    final body = <String, dynamic>{'status': status};
    if (actualPrice != null) body['actualPrice'] = actualPrice;
    await _api.post('/orders/$orderId/items/$itemId/status', body: body);
  }

  Future<void> uploadReceipt(String orderId, String receiptType, int totalAmount) async {
    await _api.post('/orders/$orderId/receipt', body: {
      'receiptType': receiptType,
      'totalAmount': totalAmount,
    });
  }

  Future<void> startDelivery(String orderId, int etaMinutes) async {
    await _api.post('/orders/$orderId/delivery/start', body: {
      'estimatedTravelMinutes': etaMinutes,
    });
  }

  Future<void> confirmDelivery(String orderId, {required double lat, required double lng}) async {
    await _api.post('/orders/$orderId/delivery/confirm', body: {
      'latitude': lat,
      'longitude': lng,
      'customerConfirmed': true,
    });
  }

  Future<void> completeDelivery(String orderId) async {
    await _api.post('/orders/$orderId/delivery/complete');
  }
}
