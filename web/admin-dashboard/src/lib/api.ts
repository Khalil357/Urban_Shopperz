const BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';

class ApiError extends Error {
  constructor(public code: string, message: string) {
    super(message);
  }
}

async function request<T>(
  path: string,
  options: RequestInit = {},
  token?: string
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${BASE_URL}${path}`, { ...options, headers });
  const data = await res.json();

  if (!data.success) {
    throw new ApiError(
      data.error?.code || 'UNKNOWN',
      data.error?.message || 'Request failed'
    );
  }
  return data.data as T;
}

export const api = {
  // Auth
  login: (username: string, password: string) =>
    request<{ token: string; username: string; role: string; name: string }>(
      '/admin/login',
      {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      }
    ),

  // Dashboard
  getMetrics: (token: string) =>
    request<{
      totalOrders: number;
      ordersToday: number;
      activeOrders: number;
      completedOrders: number;
      cancelledOrders: number;
      fulfillmentRate: number;
      activeShoppers: number;
      totalShoppers: number;
      monthlyRevenue: number;
      openDisputes: number;
      disputeRate: number;
    }>('/admin/metrics', {}, token),

  // Zones
  getZones: (token: string) =>
    request<
      {
        id: string;
        name: string;
        status: string;
        maxAssignmentRadiusKm: number;
      }[]
    >('/admin/zones', {}, token),

  // Disputes
  getDisputeQueue: (token: string) =>
    request<
      {
        id: string;
        orderId: string;
        disputeType: string;
        status: string;
        reason: string;
        requestedRefund?: number;
        createdAt: string;
      }[]
    >('/admin/disputes/queue', {}, token),
};
