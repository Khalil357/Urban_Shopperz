class Rating {
  final String id;
  final String orderId;
  final int score;
  final int? itemAccuracy;
  final int? itemQuality;
  final int? timeliness;
  final int? communication;
  final int? professionalism;
  final String? feedback;
  final bool isRevealed;
  final String? createdAt;

  Rating({
    required this.id,
    required this.orderId,
    required this.score,
    this.itemAccuracy,
    this.itemQuality,
    this.timeliness,
    this.communication,
    this.professionalism,
    this.feedback,
    this.isRevealed = false,
    this.createdAt,
  });

  factory Rating.fromJson(Map<String, dynamic> json) => Rating(
        id: json['id'] ?? '',
        orderId: json['orderId'] ?? '',
        score: json['score'] ?? 5,
        itemAccuracy: json['itemAccuracy'],
        itemQuality: json['itemQuality'],
        timeliness: json['timeliness'],
        communication: json['communication'],
        professionalism: json['professionalism'],
        feedback: json['feedback'],
        isRevealed: json['isRevealed'] ?? false,
        createdAt: json['createdAt'],
      );
}

class RatingSummary {
  final String shopperId;
  final double averageScore;
  final int totalRatings;

  RatingSummary({
    required this.shopperId,
    this.averageScore = 0.0,
    this.totalRatings = 0,
  });

  factory RatingSummary.fromJson(Map<String, dynamic> json) => RatingSummary(
        shopperId: json['shopperId'] ?? '',
        averageScore: (json['averageScore'] as num?)?.toDouble() ?? 0.0,
        totalRatings: json['totalRatings'] ?? 0,
      );
}
