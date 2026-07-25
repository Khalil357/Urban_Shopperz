'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useStore } from '@/lib/store';
import Sidebar from '@/components/Sidebar';

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const { token, fetchMetrics, fetchZones, fetchDisputes } = useStore();
  const router = useRouter();

  useEffect(() => {
    if (!token) {
      router.push('/');
      return;
    }
    fetchMetrics();
    fetchZones();
    fetchDisputes();
    const interval = setInterval(fetchMetrics, 30000);
    return () => clearInterval(interval);
  }, [token]);

  if (!token) return null;

  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      <main className="ml-64 p-8">{children}</main>
    </div>
  );
}
