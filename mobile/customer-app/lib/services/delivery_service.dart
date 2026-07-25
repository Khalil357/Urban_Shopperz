import '../models/delivery.dart';
import 'api_client.dart';

class DeliveryService {
  final ApiClient _api;

  DeliveryService(this._api);

  Future<DeliveryEta> getDeliveryEta(String orderId) async {
    final data = await _api.get('/orders/$orderId/delivery/eta');
    return DeliveryEta.fromJson(data);
  }
}
