package com.RM.manageSystem.model.dto.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * ClassName: CustomerUpdateRequest
 * PackageName: com.RM.manageSystem.model.dto.customer
 * Description:
 *
 * @Create: 2024/9/12-上午 11:34
 */
@Data
public class CustomerUpdateRequest {
    private static final long serialVersionUID = 1L;
    @NotNull
    private Integer id;
    @Schema(description = "客戶名", example = "小瓜")
    private String customerName;

    @NotNull
    @Schema(description = "性別", example = "F", allowableValues = {"M", "F"})
    private Character gender;

    @Schema(description = "出生年份", example = "2021")
    private Integer birthYear;

    @Schema(description = "電話號碼", example = "0912345678")
    private String phoneNumber;

    @Email
    @Schema(description = "電子郵件", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "頻率", example = "每週一次")
    private String frequency;

    @Schema(description = "常規運動", example = "[\"瑜伽\", \"跑步\"]")
    private List<String> regularExercises;

    @Schema(description = "其他運動", example = "攀岩")
    private String otherExercises;

    @Schema(description = "了解途徑", example = "[\"網路社群\", \"親友介紹\", \"其他\"]")
    private List<String> approaches;

    @Schema(description = "其他了解途徑", example = "電視廣告")
    private String otherApproaches;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "第一次上課日期", example = "2023-01-01")
    private LocalDate firstLesson;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "最近一次上課日期", example = "2025-09-21")
    private LocalDate lastLesson;

    @Schema(description = "總上課數", example = "10")
    private Integer totalLessons;

    @Schema(description = "剩餘課堂數", example = "5")
    private Integer remainingLessons;

    @Schema(description = "病史類別", example = "[\"扭傷/骨折\", \"重大手術\", \"其他\"]")
    private List<String> medicalHistoryCategory;

    @Schema(description = "扭傷/骨折", example = "右腳踝骨折，已康復")
    private String medicalHistoryBroken;

    @Schema(description = "重大手術", example = "闌尾切除手術")
    private String medicalHistorySurgery;

    @Schema(description = "其他", example = "輕度過敏")
    private String medicalHistoryOther;

    @Schema(description = "用藥情況", example = "每天服用降血壓藥物")
    private String medication;

    @Schema(description = "身體狀況", example = "輕度腰痛")
    private String symptoms;

    @Schema(description = "症狀原因", example = "長時間久坐")
    private String symptomCauses;

    @Schema(description = "交通方式", example = "[\"機車\", \"汽車\", \"大眾運輸\", \"步行\"]")
    private List<String> transportationCategory;
}
