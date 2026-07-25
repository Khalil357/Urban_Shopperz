'use client';

import { useStore } from '@/lib/store';

export default function OrdersPage() {
  const { metrics } = useStore();

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Orders</h2>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
          <p className="text-sm text-gray-500">Total</p>
          <p className="text-2xl font-bold">{metrics?.totalOrders || 0}</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
          <p className="text-sm text-gray-500">Active</p>
          <p className="text-2xl font-bold text-blue-600">{metrics?.activeOrders || 0}</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
          <p className="text-sm text-gray-500">Today</p>
          <p className="text-2xl font-bold text-green-600">{metrics?.ordersToday || 0}</p>
        </div>
      </div>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="text-lg font-semibold mb-4">Order History</h3>
        <div className="text-center py-12 text-gray-400">
          Orders will appear here once customers start ordering.
        </div>
      </div>
    </div>
  );
}
