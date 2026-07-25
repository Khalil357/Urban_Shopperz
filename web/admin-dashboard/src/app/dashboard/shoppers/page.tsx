'use client';

export default function ShoppersPage() {
  return (
    <div>
      <h2 className="text-2xl font-bold mb-6">Shoppers</h2>
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold">All Shoppers</h3>
          <div className="flex gap-2">
            <select className="px-3 py-1.5 border border-gray-300 rounded-lg text-sm">
              <option>All Status</option>
              <option>Active</option>
              <option>Pending</option>
              <option>Suspended</option>
            </select>
            <input
              type="text"
              placeholder="Search..."
              className="px-3 py-1.5 border border-gray-300 rounded-lg text-sm"
            />
          </div>
        </div>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-gray-500 border-b">
              <th className="pb-3 font-medium">Name</th>
              <th className="pb-3 font-medium">Phone</th>
              <th className="pb-3 font-medium">Status</th>
              <th className="pb-3 font-medium">Transport</th>
              <th className="pb-3 font-medium">Rating</th>
              <th className="pb-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr className="text-gray-400">
              <td colSpan={6} className="text-center py-12">
                No shoppers registered yet. Use the dev bootstrap endpoint to create test data.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
}
