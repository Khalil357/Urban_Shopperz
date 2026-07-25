'use client';

import { useStore } from '@/lib/store';
import MetricCard from '@/components/MetricCard';

export default function OverviewPage() {
  const { metrics } = useStore();

  if (!metrics) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-green-600" />
      </div>
    );
  }

  const formatTZS = (n: number) =>
    'TZS ' + n.toLocaleString('sw-TZ');

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <h2 className="text-2xl font-bold">Dashboard Overview</h2>
        <button
          onClick={() => useStore.getState().fetchMetrics()}
          className="text-sm text-green-600 hover:text-green-700"
        >
          ↻ Refresh
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <MetricCard
          title="Monthly Revenue"
          value={formatTZS(metrics.monthlyRevenue)}
          trend="up"
          color="#2ECC71"
        />
        <MetricCard
          title="Orders Today"
          value={metrics.ordersToday.toString()}
          subtitle={`${metrics.activeOrders} active`}
          trend={metrics.ordersToday > 0 ? 'up' : 'neutral'}
          color="#3498DB"
        />
        <MetricCard
          title="Fulfillment Rate"
          value={`${metrics.fulfillmentRate.toFixed(1)}%`}
          subtitle={`${metrics.completedOrders} completed, ${metrics.cancelledOrders} cancelled`}
          trend={metrics.fulfillmentRate > 90 ? 'up' : 'down'}
          color="#2ECC71"
        />
        <MetricCard
          title="Active Shoppers"
          value={metrics.activeShoppers.toString()}
          subtitle={`${metrics.totalShoppers} total registered`}
          trend={metrics.activeShoppers > 0 ? 'up' : 'neutral'}
          color="#F39C12"
        />
        <MetricCard
          title="Open Disputes"
          value={metrics.openDisputes.toString()}
          subtitle={`${metrics.disputeRate.toFixed(1)}% dispute rate`}
          trend={metrics.openDisputes > 0 ? 'down' : 'neutral'}
          color="#E74C3C"
        />
        <MetricCard
          title="Total Orders"
          value={metrics.totalOrders.toString()}
          subtitle="All time"
          trend="neutral"
          color="#9B59B6"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-semibold mb-4">Order Status Breakdown</h3>
          <div className="space-y-3">
            {[
              { label: 'Active Orders', value: metrics.activeOrders, color: 'bg-blue-500' },
              { label: 'Completed', value: metrics.completedOrders, color: 'bg-green-500' },
              { label: 'Cancelled', value: metrics.cancelledOrders, color: 'bg-red-500' },
            ].map((item) => (
              <div key={item.label}>
                <div className="flex justify-between text-sm mb-1">
                  <span>{item.label}</span>
                  <span className="font-medium">{item.value}</span>
                </div>
                <div className="w-full bg-gray-100 rounded-full h-2">
                  <div
                    className={`${item.color} h-2 rounded-full`}
                    style={{
                      width: `${
                        metrics.totalOrders > 0
                          ? (item.value / metrics.totalOrders) * 100
                          : 0
                      }%`,
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-lg font-semibold mb-4">Quick Actions</h3>
          <div className="space-y-3">
            {[
              { label: 'View Shopper Applications', href: '/dashboard/shoppers', color: 'blue' },
              { label: 'Manage Disputes', href: '/dashboard/disputes', color: 'red' },
              { label: 'Configure Zones', href: '/dashboard/zones', color: 'green' },
            ].map((action) => (
              <a
                key={action.label}
                href={action.href}
                className={`block px-4 py-3 rounded-lg text-sm font-medium text-center text-white bg-${action.color}-600 hover:bg-${action.color}-700`}
              >
                {action.label}
              </a>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
