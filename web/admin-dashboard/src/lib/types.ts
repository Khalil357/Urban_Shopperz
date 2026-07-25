export interface DashboardMetrics {
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
}

export interface Zone {
  id: string;
  name: string;
  status: string;
  maxAssignmentRadiusKm: number;
  baseDeliveryFee?: number;
  perKmRate?: number;
}

export interface Dispute {
  id: string;
  orderId: string;
  disputeType: string;
  status: string;
  reason: string;
  requestedRefund?: number;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  role: string;
  name: string;
}
