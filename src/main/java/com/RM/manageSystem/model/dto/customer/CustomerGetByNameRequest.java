package com.RM.manageSystem.model.dto.customer;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: CustomerGetByNameRequest
 * PackageName: com.RM.manageSystem.model.dto.customer
 * Description:
 *
 * @Create: 2024/9/25-下午 12:51
 */
@Data
public class CustomerGetByNameRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String customerName;
}
