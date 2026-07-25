import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/rating_service.dart';

class RatingScreen extends StatefulWidget {
  final String orderId;
  final String shopperId;

  const RatingScreen({
    super.key,
    required this.orderId,
    required this.shopperId,
  });

  @override
  State<RatingScreen> createState() => _RatingScreenState();
}

class _RatingScreenState extends State<RatingScreen> {
  int _score = 5;
  int _itemAccuracy = 5;
  int _itemQuality = 5;
  int _timeliness = 4;
  int _communication = 5;
  int _professionalism = 5;
  final _feedbackCtrl = TextEditingController();
  bool _loading = false;
  bool _submitted = false;

  @override
  void dispose() {
    _feedbackCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    setState(() => _loading = true);
    try {
      final auth = context.read<AuthService>();
      final ratingService = RatingService(auth.api);
      await ratingService.submitRating(
        orderId: widget.orderId,
        score: _score,
        itemAccuracy: _itemAccuracy,
        itemQuality: _itemQuality,
        timeliness: _timeliness,
        communication: _communication,
        professionalism: _professionalism,
        feedback: _feedbackCtrl.text.isNotEmpty ? _feedbackCtrl.text : null,
      );
      if (mounted) {
        setState(() { _submitted = true; _loading = false; });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _loading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Hitilafu: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_submitted) {
      return Scaffold(
        appBar: AppBar(title: const Text('Shukrani!')),
        body: const Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.check_circle, size: 80, color: Colors.green),
              SizedBox(height: 16),
              Text('Asante kwa kunipima!',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
              SizedBox(height: 8),
              Text('Maoni yako yanatusaidia kuboresha huduma zetu.'),
            ],
          ),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Mwamini Mnuuzaji')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Icon(Icons.star, size: 64, color: Colors.amber),
          const Center(
            child: Text(
              'Mwamini mnuuzaji wako',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
          const SizedBox(height: 24),
          _buildStarRow('Alama ya Jumla', _score, (v) => _score = v),
          const Divider(),
          _buildStarRow('Usahihi wa Bidhaa', _itemAccuracy, (v) => _itemAccuracy = v),
          _buildStarRow('Ubora wa Bidhaa', _itemQuality, (v) => _itemQuality = v),
          _buildStarRow('Uwajibu (Muda)', _timeliness, (v) => _timeliness = v),
          _buildStarRow('Mawasiliano', _communication, (v) => _communication = v),
          _buildStarRow('Utaalamu', _professionalism, (v) => _professionalism = v),
          const Divider(),
          TextField(
            controller: _feedbackCtrl,
            maxLines: 3,
            decoration: const InputDecoration(
              labelText: 'Maoni (hiari)',
              hintText: 'Elezea uzoefu wako...',
              border: OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 24),
          SizedBox(
            width: double.infinity,
            height: 48,
            child: ElevatedButton(
              onPressed: _loading ? null : _submit,
              child: _loading
                  ? const CircularProgressIndicator(strokeWidth: 2)
                  : const Text('Tuma', style: TextStyle(fontSize: 16)),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStarRow(String label, int value, ValueChanged<int> onChanged) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          SizedBox(width: 140, child: Text(label)),
          ...List.generate(5, (i) {
            return IconButton(
              icon: Icon(
                i < value ? Icons.star : Icons.star_border,
                color: Colors.amber,
              ),
              onPressed: () => onChanged(i + 1),
              constraints: const BoxConstraints(minWidth: 36),
              padding: EdgeInsets.zero,
            );
          }),
        ],
      ),
    );
  }
}
