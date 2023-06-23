package com.fitriarien.instudio.model.response;

import com.fitriarien.instudio.entity.OrderDetail;
import com.fitriarien.instudio.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponse {

    private String orderId;

    private String orderCode;

    private String orderDate;

    private String visitSchedule;

    private String visitAddress;

    private Double orderAmount;

    private Long orderStatus;

    private List<OrderDetail> orderDetailList;
}
