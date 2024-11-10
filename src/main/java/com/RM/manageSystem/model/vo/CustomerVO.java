package com.RM.manageSystem.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * ClassName: CustomerVO
 * PackageName: com.RM.manageSystem.model.vo
 * Description:
 *
 * @Create: 2024/9/12-上午 11:59
 */
@Data
public class CustomerVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String customerName;
    private Character gender;
    private Integer birthYear;
    private String phoneNumber;
    private String email;
    private String frequency;
    private List<String> regularExercises; // JSON 字符串集合:業主設定好的
    private String otherExercises;    // 出乎業主預料的運動
    private List<String> approaches;  // JSON 字符串集合:知道這間工作室的管道(業主設定標準的3個)
    private String otherApproaches;   // 其他得知管道
    private LocalDate firstLesson;  //第一次上課日期
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
}
