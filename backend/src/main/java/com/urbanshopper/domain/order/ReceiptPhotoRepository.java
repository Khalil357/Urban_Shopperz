package com.urbanshopper.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReceiptPhotoRepository extends JpaRepository<ReceiptPhoto, UUID> {
    List<ReceiptPhoto> findByReceiptIdOrderBySortOrderAsc(UUID receiptId);
}
