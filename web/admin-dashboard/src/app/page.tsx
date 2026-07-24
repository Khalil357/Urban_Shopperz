'use client';

import { useState } from 'react';

interface MetricCardProps {
  title: string;
  value: string;
  trend: 'up' | 'down' | 'neutral';
  color: string;
}

function MetricCard({ title, value, trend, color }: MetricCardProps) {
  return (
    <div className="bg-white rounded-lg shadow p-6">
      <h3 className="text-sm font-medium text-gray-500 mb-2">{title}</h3>
      <div className="flex items-end justify-between">
        <p className="text-2xl font-bold" style={{ color }}>{value}</p>
        <span className={`text-sm ${
          trend === 'up' ? 'text-green-500' :
          trend === 'down' ? 'text-red-500' : 'text-gray-400'
        }`}>
          {trend === 'up' ? '↑' : trend === 'down' ? '↓' : '→'}
        </span>
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const [metrics] = useState({
    gmvToday: 'TZS 1,250,000',
    activeShoppers: '234',
    fulfillmentRate: '96.5%',
    avgAssignmentTime: '18s',
    openDisputes: '3',
    pendingApplications: '12',
  });

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar */}
      <aside className="fixed left-0 top-0 h-full w-64 bg-white shadow-lg">
        <div className="p-6">
          <h1 className="text-xl font-bold text-green-600">Urban Shopper</h1>
          <p className="text-sm text-gray-500">Admin Dashboard</p>
        </div>
        <nav className="px-4 space-y-2">
          {[
            { icon: '📊', label: 'Overview', active: true },
            { icon: '👥', label: 'Shoppers', active: false },
            { icon: '📦', label: 'Orders', active: false },
            { icon: '⚖️', label: 'Disputes', badge: metrics.openDisputes, active: false },
            { icon: '🚨', label: 'Fraud', active: false },
            { icon: '🌍', label: 'Zones', active: false },
            { icon: '📈', label: 'Reports', active: false },
            { icon: '⚙️', label: 'Settings', active: false },
          ].map((item) => (
            <div
              key={item.label}
              className={`flex items-center justify-between px-4 py-3 rounded-lg cursor-pointer ${
                item.active ? 'bg-green-50 text-green-700' : 'hover:bg-gray-50'
              }`}
            >
              <span>{item.icon} {item.label}</span>
              {'badge' in item && item.badge && (
                <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-full">
                  {item.badge}
                </span>
              )}
            </div>
          ))}
        </nav>
      </aside>

      {/* Main content */}
      <main className="ml-64 p-8">
        {/* Top bar */}
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl font-bold">Dashboard</h2>
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-500">Last updated: just now</span>
            <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center text-white text-sm">
              A
            </div>
          </div>
        </div>

        {/* Metrics cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          <MetricCard title="GMV Today" value={metrics.gmvToday} trend="up" color="#2ECC71" />
          <MetricCard title="Active Shoppers" value={metrics.activeShoppers} trend="up" color="#3498DB" />
          <MetricCard title="Fulfillment Rate" value={metrics.fulfillmentRate} trend="up" color="#2ECC71" />
          <MetricCard title="Avg Assignment Time" value={metrics.avgAssignmentTime} trend="up" color="#F39C12" />
          <MetricCard title="Open Disputes" value={metrics.openDisputes} trend="down" color="#E74C3C" />
          <MetricCard title="Pending Applications" value={metrics.pendingApplications} trend="neutral" color="#3498DB" />
        </div>

        {/* Two-column layout */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Pending Shoppers */}
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Pending Shopper Applications</h3>
            <div className="text-center py-8 text-gray-400">
              <p className="text-4xl mb-2">{metrics.pendingApplications}</p>
              <p>applications awaiting review</p>
            </div>
          </div>

          {/* Recent Activity */}
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold mb-4">Recent Activity</h3>
            <div className="space-y-4">
              {[
                { event: 'New dispute opened', time: '5 min ago', type: 'warning' },
                { event: 'Order completed #UBR-1245', time: '12 min ago', type: 'success' },
                { event: 'Shopper Juma went online', time: '20 min ago', type: 'info' },
              ].map((activity, i) => (
                <div key={i} className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                  <span className={`w-2 h-2 rounded-full ${
                    activity.type === 'warning' ? 'bg-yellow-400' :
                    activity.type === 'success' ? 'bg-green-400' : 'bg-blue-400'
                  }`} />
                  <div className="flex-1">
                    <p className="text-sm">{activity.event}</p>
                    <p className="text-xs text-gray-400">{activity.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
