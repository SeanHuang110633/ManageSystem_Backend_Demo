package com.RM.manageSystem.model.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private static final long serialVersionUID = 1L;
    @NotNull(groups = Customer.Update.class)
    private Integer id;
    @NotEmpty
    private String customerName;
    @NotNull(groups = Customer.Add.class)
    private Character gender;
    private Integer birthYear;
    private String phoneNumber;
    @Email
    private String email;
    private String frequency;  //上課頻率
    private List<String> regularExercises; // JSON 字符串集合:業主設定好的
    private String otherExercises;    // 出乎業主預料的運動
    private List<String> approaches;  // JSON 字符串集合:知道這間工作室的管道(業主設定標準的3個)
    private String otherApproaches;   // 其他得知管道
    @DateTimeFormat
    private LocalDate firstLesson;  //第一次上課日期
    @DateTimeFormat
    private LocalDate lastLesson;  //最近一次上課日期
    private Integer totalLessons;  //總上課數(預設為1)
    private Integer coachId; //預設為1(老闆)
    private Integer remainingLessons;  //剩餘課堂數(預設為0)
    //以下預設為無
    //==================================================
    private List<String> medicalHistoryCategory;
    private String medicalHistoryBroken;  //病史-骨折
    private String medicalHistorySurgery;  //病史-手術
    private String medicalHistoryOther;  //病史-其他
    //===================================================
    private String medication;  //用藥情況
    private String symptoms;  //身體狀況
    private String symptomCauses;
    private List<String> transportationCategory;
    private String transportationHabits; //(取代掉)
    private String exerciseHabits;  //(取代掉)
    //在service層添加
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDelete;

    // 作為更新操作的驗證分組
    public interface Update extends Default {}

    public interface Add extends Default{}


}
