package com.RM.manageSystem.controller;

import com.RM.manageSystem.common.PageBean;
import com.RM.manageSystem.common.Result;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.model.dto.customer.CustomerAddRequest;
import com.RM.manageSystem.model.dto.customer.CustomerQueryRequest;
import com.RM.manageSystem.model.dto.customer.CustomerUpdateRequest;
import com.RM.manageSystem.model.entity.Customer;
import com.RM.manageSystem.model.vo.CustomerVO;
import com.RM.manageSystem.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.RM.manageSystem.common.ErrorCode.PARAMS_ERROR;
import static com.RM.manageSystem.common.ErrorCode.SUCCESS;


@Slf4j
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer Management", description = "客戶戶API測試")
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // region CRUD

    /**
     * 分頁查詢客戶列表
     *
     * @param request 客戶查詢參數封裝類
     * @return ResponseEntity<Result<PageBean<CustomerVO>>> 包含客戶列表的分頁結果
     */
    @Operation(summary = "分頁查詢客戶列表", description = "根據查詢參數分頁獲取客戶列表")
    @GetMapping
    public ResponseEntity<Result<PageBean<CustomerVO>>> listCustomers(
            @Parameter(description = "客戶查詢參數") @ModelAttribute CustomerQueryRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(request,customer);
        log.info("查詢客戶列表傳入的參數: customerQueryRequest={}", request);
        PageBean<CustomerVO> result = customerService.listCustomers(request.getPageNum(), request.getPageSize(), customer);
        return ResponseEntity.ok(Result.success(SUCCESS, result));
    }

    /**
     * 查詢單個客戶
     *
     * @param id 客戶id
     * @return ResponseEntity<Result<CustomerVO>> 單個客戶結果
     */
    @Operation(summary = "查詢單個客戶", description = "依據ID查詢指定客戶")
    @GetMapping("/userById")
    public ResponseEntity<Result<CustomerVO>> getCustomerById(@Parameter(description = "客戶ID") @RequestParam Integer id ){
        if (id == null || id <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("查詢客戶傳入的參數: id={}", id);
        CustomerVO result = customerService.getCustomerById(id);
        return ResponseEntity.ok(Result.success(SUCCESS, result));
    }


    /**
     * 新增客戶
     *
     * @param request 客戶新增請求對象
     * @return ResponseEntity<Result<?>> 新增結果
     */
    @Operation(summary = "新增客戶", description = "新增一個客戶記錄")
    @PostMapping
    public ResponseEntity<Result<?>> addCustomer(@RequestBody @Validated CustomerAddRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("新增客戶，參數: {}", request);
        Customer customer = new Customer();
        BeanUtils.copyProperties(request,customer);
        validateCustomerInput(customer);
        customerService.addCustomer(customer);
        log.info("新增客戶成功");
        return new ResponseEntity<>(Result.success(SUCCESS, "新增客戶成功"), HttpStatus.CREATED);
    }

    /**
     * 更新客戶資訊
     *
     * @param request 客戶更新請求對象
     * @return ResponseEntity<Result<?>> 更新結果
     */
    @Operation(summary = "更新客戶資訊", description = "更新指定客戶的資訊")
    @PutMapping
    public ResponseEntity<Result<?>> updateCustomer(@RequestBody @Validated CustomerUpdateRequest request) {
        if (request == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        log.info("更新客戶資料: {}", request);
        Customer customer = new Customer();
        BeanUtils.copyProperties(request,customer);
        validateCustomerInput(customer);
        customerService.updateCustomer(customer);
        log.info("更新客戶資料成功: {}", customer);
        return ResponseEntity.ok(Result.success(SUCCESS));
    }

    /**
     * 刪除客戶
     *
     * @param id 客戶ID
     * @return ResponseEntity<Result<?>> 刪除結果
     */
    @Operation(summary = "刪除客戶", description = "根據ID刪除指定客戶")
    @DeleteMapping
    public ResponseEntity<Result<?>> deleteCustomerById(@Parameter(description = "客戶ID") @RequestParam Integer id) {
        log.info("刪除客戶ID: {}", id);
        customerService.deleteCustomerById(id);
        log.info("刪除客戶成功ID: {}", id);
        return ResponseEntity.ok(Result.success(SUCCESS));
    }

    /**
     * 查詢所有客戶來源
     *
     * @return ResponseEntity<Result<Map<String, Integer>>> 客戶來源統計結果
     */
    @Operation(summary = "查詢客戶來源統計", description = "獲取所有客戶來源的統計數據")
    @GetMapping("/statistics/approach")
    public ResponseEntity<Result<ArrayList<Integer>>> listApproaches() {
        log.info("查詢所有客戶來源");
        ArrayList<Integer> approachesNum = customerService.listApproaches();
        log.info("查詢客戶來源列表統計結果");
        return ResponseEntity.ok(Result.success(SUCCESS, "查詢所有客戶來源成功", approachesNum));
    }

    /**
     * 查詢所有客戶初次來店日期(統計每月新增客戶數)
     *
     * @return ResponseEntity<Result<Map<Integer, int[]>>> 每月新增客戶數統計結果
     */
    @Operation(summary = "查詢客戶初次來店日期統計", description = "獲取每月新增客戶數的統計數據")
    @GetMapping("/statistics/firstLesson")
    public ResponseEntity<Result<Map<Integer, int[]>>> listFirstLesson() {
        log.info("查詢所有客戶初次來店日期");
        Map<Integer, int[]> dateMap = customerService.listFirstLesson();
        log.info("查詢客戶列表返回結果共{}筆資料", dateMap.size());
        return ResponseEntity.ok(Result.success(SUCCESS, dateMap));
    }

    // endregion

    /**
     * 查詢客戶性別統計數據
     *
     * @return ResponseEntity<Result<int[]>> 客戶性別統計結果
     */
    @Operation(summary = "查詢客戶性別統計", description = "獲取客戶性別的統計數據")
    @GetMapping("/statistics/gender")
    public ResponseEntity<Result<int[]>> listGender() {
        log.info("查詢客戶性別");
        int[] genderNum = customerService.listGender();
        log.info("查詢客戶性別結果{}", genderNum);
        return ResponseEntity.ok(Result.success(SUCCESS, genderNum));
    }

    /**
     * 驗證客戶輸入信息
     *
     * @param customer 客戶對象
     * @throws BusinessException 如果輸入無效
     */
    private void validateCustomerInput(Customer customer) {
        // 校驗使用者手動輸入選項的參數
        if (isInvalidInput(customer.getOtherExercises(), customer.getRegularExercises(), "其他")) {
            throw new BusinessException(PARAMS_ERROR, "請輸入其他運動(可填無或取消勾選其他)");
        }
        if (isInvalidInput(customer.getOtherApproaches(), customer.getApproaches(), "其他")) {
            throw new BusinessException(PARAMS_ERROR, "請輸入其他得知管道(可填無或取消勾選其他)");
        }
        if (isInvalidInput(customer.getMedicalHistoryBroken(), customer.getMedicalHistoryCategory(), "扭傷/骨折")) {
            throw new BusinessException(PARAMS_ERROR, "請輸入扭傷/骨折情形(可填無或取消勾選其他)");
        }
        if (isInvalidInput(customer.getMedicalHistorySurgery(), customer.getMedicalHistoryCategory(), "重大手術")) {
            throw new BusinessException(PARAMS_ERROR, "請輸入重大手術情形(可填無或取消勾選其他)");
        }
        if (isInvalidInput(customer.getMedicalHistoryOther(), customer.getMedicalHistoryCategory(), "其他")) {
            throw new BusinessException(PARAMS_ERROR, "請輸入其他情形(可填無或取消勾選其他)");
        }
    }

    /**
     * 檢查輸入是否無效
     *
     * @param input 輸入字符串
     * @param list 選項列表
     * @param target 目標選項
     * @return boolean 是否無效
     */
    private boolean isInvalidInput(String input, List<String> list, String target) {
        return (input == null || input.isBlank()) && list != null && list.contains(target);
    }


}
