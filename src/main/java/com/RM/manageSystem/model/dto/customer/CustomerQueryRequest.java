package com.RM.manageSystem.model.dto.customer;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class CustomerQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "頁碼", example = "1")
    private Integer pageNum;
    @Schema(description = "每頁資料數", example = "10")
    private Integer pageSize;
    private String customerName;
    private String phoneNumber;
}
