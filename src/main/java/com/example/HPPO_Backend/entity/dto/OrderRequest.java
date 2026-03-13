package com.example.HPPO_Backend.entity.dto;

import com.example.HPPO_Backend.entity.OrderStatus;
import lombok.Data;
import java.util.Date;

@Data
public class OrderRequest {

    private Date date;
    private String address;
    private String shipping;
    private OrderStatus status;

}
