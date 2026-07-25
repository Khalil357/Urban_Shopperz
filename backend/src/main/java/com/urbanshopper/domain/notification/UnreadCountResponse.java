package com.urbanshopper.domain.notification;

import lombok.Builder;

@Builder
public record UnreadCountResponse(long count) {}
