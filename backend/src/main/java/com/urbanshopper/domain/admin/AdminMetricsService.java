package com.urbanshopper.domain.admin;

import com.urbanshopper.domain.assignment.ShopperAvailabilityRepository;
import com.urbanshopper.domain.dispute.DisputeRepository;
import com.urbanshopper.domain.dispute.DisputeStatus;
import com.urbanshopper.domain.order.OrderRepository;
import com.urbanshopper.domain.order.OrderStatus;
import com.urbanshopper.domain.payment.PaymentRepository;
import com.urbanshopper.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Admin Metrics — dashboard KPIs for operations monitoring (L-010).
 */
@Service
@RequiredArgsConstructor
public class AdminMetricsService {

    private final OrderRepository orderRepository;
    private final ShopperAvailabilityRepository availabilityRepository;
    private final DisputeRepository disputeRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public DashboardMetrics getDashboardMetrics() {
        var now = Instant.now();
        var todayStart = now.truncatedTo(ChronoUnit.DAYS);
        var weekAgo = now.minus(7, ChronoUnit.DAYS);
        var monthAgo = now.minus(30, ChronoUnit.DAYS);

        // Order metrics
        var totalOrders = orderRepository.count();
        var todayOrders = orderRepository.countByCreatedAtAfter(todayStart);
        var activeOrders = orderRepository.countByStatusIn(
            java.util.List.of(OrderStatus.OFFERED, OrderStatus.ACCEPTED,
                OrderStatus.TRAVELLING_TO_MARKET, OrderStatus.SHOPPING,
                OrderStatus.IN_DELIVERY));
        var completedOrders = orderRepository.countByStatus(OrderStatus.COMPLETED);
        var cancelledOrders = orderRepository.countByStatus(OrderStatus.CANCELLED);

        var terminalOrders = completedOrders + cancelledOrders;
        var fulfillmentRate = totalOrders > 0 && terminalOrders > 0
            ? (double) completedOrders / terminalOrders * 100
            : 0.0;

        // Shopper metrics
        var activeShoppers = availabilityRepository.countByStatus("online");
        var totalShoppers = availabilityRepository.count();

        // Revenue
        var capturedPayments = paymentRepository.findByStatus(PaymentStatus.CAPTURED);
        var monthlyRevenue = capturedPayments.stream()
            .filter(p -> p.getCapturedAt() != null && p.getCapturedAt().isAfter(monthAgo))
            .mapToInt(p -> p.getCapturedAmount() != null ? p.getCapturedAmount() : 0)
            .sum();

        // Dispute metrics
        var openDisputes = disputeRepository.findByStatusOrderByCreatedAtAsc(DisputeStatus.REPORTED).size()
                         + disputeRepository.findByStatusOrderByCreatedAtAsc(DisputeStatus.UNDER_REVIEW).size()
                         + disputeRepository.findByStatusOrderByCreatedAtAsc(DisputeStatus.EVIDENCE_COLLECTION).size()
                         + disputeRepository.findByStatusOrderByCreatedAtAsc(DisputeStatus.AUTOMATED_VALIDATION).size();
        var disputeRate = totalOrders > 0
            ? (double) (openDisputes + disputeRepository.findByStatusOrderByCreatedAtAsc(DisputeStatus.RESOLVED).size()) / totalOrders * 100
            : 0.0;

        return DashboardMetrics.builder()
            .totalOrders(totalOrders)
            .ordersToday(todayOrders)
            .activeOrders(activeOrders)
            .completedOrders(completedOrders)
            .cancelledOrders(cancelledOrders)
            .fulfillmentRate(Math.round(fulfillmentRate * 100.0) / 100.0)
            .activeShoppers(activeShoppers)
            .totalShoppers(totalShoppers)
            .monthlyRevenue(monthlyRevenue)
            .openDisputes(openDisputes)
            .disputeRate(Math.round(disputeRate * 100.0) / 100.0)
            .build();
    }

    public record DashboardMetrics(
        long totalOrders,
        long ordersToday,
        long activeOrders,
        long completedOrders,
        long cancelledOrders,
        double fulfillmentRate,
        long activeShoppers,
        long totalShoppers,
        int monthlyRevenue,
        int openDisputes,
        double disputeRate
    ) {
        public static DashboardMetricsBuilder builder() { return new DashboardMetricsBuilder(); }
        public static class DashboardMetricsBuilder {
            private long totalOrders; private long ordersToday; private long activeOrders;
            private long completedOrders; private long cancelledOrders; private double fulfillmentRate;
            private long activeShoppers; private long totalShoppers; private int monthlyRevenue;
            private int openDisputes; private double disputeRate;
            public DashboardMetricsBuilder totalOrders(long v) { this.totalOrders = v; return this; }
            public DashboardMetricsBuilder ordersToday(long v) { this.ordersToday = v; return this; }
            public DashboardMetricsBuilder activeOrders(long v) { this.activeOrders = v; return this; }
            public DashboardMetricsBuilder completedOrders(long v) { this.completedOrders = v; return this; }
            public DashboardMetricsBuilder cancelledOrders(long v) { this.cancelledOrders = v; return this; }
            public DashboardMetricsBuilder fulfillmentRate(double v) { this.fulfillmentRate = v; return this; }
            public DashboardMetricsBuilder activeShoppers(long v) { this.activeShoppers = v; return this; }
            public DashboardMetricsBuilder totalShoppers(long v) { this.totalShoppers = v; return this; }
            public DashboardMetricsBuilder monthlyRevenue(int v) { this.monthlyRevenue = v; return this; }
            public DashboardMetricsBuilder openDisputes(int v) { this.openDisputes = v; return this; }
            public DashboardMetricsBuilder disputeRate(double v) { this.disputeRate = v; return this; }
            public DashboardMetrics build() { return new DashboardMetrics(totalOrders, ordersToday, activeOrders, completedOrders, cancelledOrders, fulfillmentRate, activeShoppers, totalShoppers, monthlyRevenue, openDisputes, disputeRate); }
        }
    }
}
