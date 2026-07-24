import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _phoneController = TextEditingController();
  final _otpController = TextEditingController();
  bool _otpSent = false;
  bool _loading = false;

  @override
  void dispose() {
    _phoneController.dispose();
    _otpController.dispose();
    super.dispose();
  }

  Future<void> _requestOtp() async {
    setState(() => _loading = true);
    final phone = _phoneController.text.trim();
    if (phone.length != 12 || !phone.startsWith('255')) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Tafadhali ingiza namba sahihi (+255XXXXXXXXX)')),
      );
      setState(() => _loading = false);
      return;
    }
    final auth = context.read<AuthService>();
    final success = await auth.requestOtp(phone);
    if (mounted) {
      setState(() {
        _otpSent = success;
        _loading = false;
      });
      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Namba ya siri imetumwa kwa simu yako')),
        );
      }
    }
  }

  Future<void> _verifyOtp() async {
    setState(() => _loading = true);
    final auth = context.read<AuthService>();
    final success = await auth.verifyOtp(
      _phoneController.text.trim(),
      _otpController.text.trim(),
    );
    if (mounted) {
      setState(() => _loading = false);
      if (!success) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Namba ya siri si sahihi. Tafadhali jaribu tena.')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Urban Shopper',
                style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              Text(
                'Agiza bidhaa sokoni, zikufikie mlangoni',
                style: TextStyle(fontSize: 16, color: Colors.grey[600]),
              ),
              const SizedBox(height: 48),
              TextField(
                controller: _phoneController,
                keyboardType: TextInputType.phone,
                decoration: const InputDecoration(
                  labelText: 'Namba ya Simu',
                  hintText: '255XXXXXXXXX',
                  border: OutlineInputBorder(),
                  prefixIcon: Icon(Icons.phone),
                ),
              ),
              const SizedBox(height: 16),
              if (_otpSent) ...[
                TextField(
                  controller: _otpController,
                  keyboardType: TextInputType.number,
                  maxLength: 6,
                  decoration: const InputDecoration(
                    labelText: 'Namba ya Siri (OTP)',
                    hintText: '123456',
                    border: OutlineInputBorder(),
                    prefixIcon: Icon(Icons.lock),
                  ),
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: _loading ? null : _verifyOtp,
                    child: _loading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Text('Ingia'),
                  ),
                ),
              ] else ...[
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: _loading ? null : _requestOtp,
                    child: _loading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Text('Tuma Namba ya Siri'),
                  ),
                ),
              ],
              const SizedBox(height: 24),
              TextButton(
                onPressed: () {},
                child: const Text('English'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
