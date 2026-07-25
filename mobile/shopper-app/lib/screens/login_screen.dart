import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _phoneCtrl = TextEditingController();
  final _otpCtrl = TextEditingController();
  bool _otpSent = false, _loading = false;

  @override
  void dispose() { _phoneCtrl.dispose(); _otpCtrl.dispose(); super.dispose(); }

  Future<void> _requestOtp() async {
    setState(() => _loading = true);
    final phone = _phoneCtrl.text.trim();
    if (phone.length != 12 || !phone.startsWith('255')) {
      _snack('Tafadhali ingiza namba sahihi (+255XXXXXXXXX)');
      setState(() => _loading = false);
      return;
    }
    final ok = await context.read<AuthService>().requestOtp(phone);
    if (mounted) setState(() { _otpSent = ok; _loading = false; });
    if (ok) _snack('Namba ya siri imetumwa');
  }

  Future<void> _verify() async {
    setState(() => _loading = true);
    final ok = await context.read<AuthService>().verifyOtp(
        _phoneCtrl.text.trim(), _otpCtrl.text.trim());
    if (mounted) setState(() => _loading = false);
    if (!ok) _snack('Namba ya siri si sahihi');
  }

  void _snack(String m) => ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(m)));

  @override
  Widget build(BuildContext context) => Scaffold(
        body: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(Icons.delivery_dining, size: 64, color: Colors.blue),
                const Text('Urban Shopper', style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold)),
                const Text('Mnuuzaji', style: TextStyle(fontSize: 18, color: Colors.grey)),
                const SizedBox(height: 32),
                TextField(controller: _phoneCtrl, keyboardType: TextInputType.phone,
                    decoration: const InputDecoration(labelText: 'Namba ya Simu', hintText: '255XXXXXXXXX',
                        border: OutlineInputBorder(), prefixIcon: Icon(Icons.phone))),
                const SizedBox(height: 16),
                if (_otpSent) ...[
                  TextField(controller: _otpCtrl, keyboardType: TextInputType.number, maxLength: 6,
                      decoration: const InputDecoration(labelText: 'Namba ya Siri', hintText: '123456',
                          border: OutlineInputBorder(), prefixIcon: Icon(Icons.lock))),
                  const SizedBox(height: 16),
                  SizedBox(width: double.infinity, height: 48,
                      child: ElevatedButton(onPressed: _loading ? null : _verify,
                          child: _loading ? const CircularProgressIndicator(strokeWidth: 2) : const Text('Ingia'))),
                ] else ...[
                  SizedBox(width: double.infinity, height: 48,
                      child: ElevatedButton(onPressed: _loading ? null : _requestOtp,
                          child: _loading ? const CircularProgressIndicator(strokeWidth: 2) : const Text('Tuma Namba ya Siri'))),
                ],
              ],
            ),
          ),
        ),
      );
}
