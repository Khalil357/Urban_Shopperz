'use client';

import { useStore } from '@/lib/store';

export default function ZonesPage() {
  const { zones } = useStore();

  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Service Zones</h2>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h3 className="text-lg font-semibold mb-4">
          Configured Zones ({zones.length})
        </h3>

        {zones.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            No zones configured.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left text-gray-500 border-b">
                  <th className="pb-3 font-medium">Name</th>
                  <th className="pb-3 font-medium">Status</th>
                  <th className="pb-3 font-medium">Max Radius (km)</th>
                  <th className="pb-3 font-medium">Actions</th>
                </tr>
              </thead>
              <tbody>
                {zones.map((zone: any) => (
                  <tr key={zone.id} className="border-b border-gray-50">
                    <td className="py-3 font-medium">{zone.name}</td>
                    <td className="py-3">
                      <span
                        className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                          zone.status === 'active'
                            ? 'bg-green-100 text-green-700'
                            : 'bg-gray-100 text-gray-500'
                        }`}
                      >
                        {zone.status}
                      </span>
                    </td>
                    <td className="py-3">{zone.maxAssignmentRadiusKm}</td>
                    <td className="py-3">
                      <button className="text-blue-600 hover:text-blue-700 text-sm">
                        Configure
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
