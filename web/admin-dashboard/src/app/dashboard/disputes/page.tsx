'use client';

import { useStore } from '@/lib/store';

const statusColors: Record<string, string> = {
  REPORTED: 'bg-yellow-100 text-yellow-800',
  UNDER_REVIEW: 'bg-blue-100 text-blue-800',
  AUTOMATED_VALIDATION: 'bg-purple-100 text-purple-800',
  ESCALATED_TO_OPS: 'bg-red-100 text-red-800',
  RESOLVED: 'bg-green-100 text-green-800',
  CLOSED: 'bg-gray-100 text-gray-800',
};

export default function DisputesPage() {
  const { disputes } = useStore();

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Disputes</h2>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="text-lg font-semibold mb-4">
          Open Disputes Queue ({disputes.length})
        </h3>

        {disputes.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            No open disputes. Everything is running smoothly.
          </div>
        ) : (
          <div className="space-y-3">
            {disputes.map((d: any) => (
              <div key={d.id} className="border border-gray-200 rounded-lg p-4">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <span className="font-medium">{d.disputeType}</span>
                    <span className="text-gray-400 text-sm ml-2">#{d.orderId.slice(0, 8)}</span>
                  </div>
                  <span
                    className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      statusColors[d.status] || 'bg-gray-100'
                    }`}
                  >
                    {d.status}
                  </span>
                </div>
                <p className="text-sm text-gray-600 mb-2">{d.reason}</p>
                <div className="flex justify-between text-xs text-gray-400">
                  <span>Created: {d.createdAt?.slice(0, 10)}</span>
                  {d.requestedRefund && (
                    <span>Refund requested: TZS {d.requestedRefund}</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
