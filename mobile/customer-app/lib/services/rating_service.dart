import '../models/rating.dart';
import 'api_client.dart';

class RatingService {
  final ApiClient _api;

  RatingService(this._api);

  Future<Rating> submitRating({
    required String orderId,
    required int score,
    int? itemAccuracy,
    int? itemQuality,
    int? timeliness,
    int? communication,
    int? professionalism,
    String? feedback,
  }) async {
    final body = {
      'orderId': orderId,
      'score': score,
      if (itemAccuracy != null) 'itemAccuracy': itemAccuracy,
      if (itemQuality != null) 'itemQuality': itemQuality,
      if (timeliness != null) 'timeliness': timeliness,
      if (communication != null) 'communication': communication,
      if (professionalism != null) 'professionalism': professionalism,
      if (feedback != null) 'feedback': feedback,
    };

    final data = await _api.post('/ratings', body: body);
    return Rating.fromJson(data);
  }

  Future<RatingSummary> getShopperRatingSummary(String shopperId) async {
    final data = await _api.get('/shoppers/$shopperId/ratings/summary');
    return RatingSummary.fromJson(data);
  }
}
