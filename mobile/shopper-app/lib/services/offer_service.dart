import '../models/order_offer.dart';
import 'api_client.dart';

class OfferService {
  final ApiClient _api;

  OfferService(this._api);

  Future<List<OrderOffer>> getPendingOffers(String shopperId) async {
    final list = await _api.getList('/shoppers/$shopperId/offers');
    return list.map((j) => OrderOffer.fromJson(j as Map<String, dynamic>)).toList();
  }

  Future<void> acceptOffer(String shopperId, String offerId) async {
    await _api.post('/shoppers/$shopperId/offers/$offerId/accept');
  }

  Future<void> declineOffer(String shopperId, String offerId) async {
    await _api.post('/shoppers/$shopperId/offers/$offerId/decline');
  }
}
