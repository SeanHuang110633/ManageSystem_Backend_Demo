package com.RM.manageSystem.service;

import com.RM.manageSystem.common.PageBean;
import com.RM.manageSystem.model.entity.Customer;
import com.RM.manageSystem.model.vo.CustomerVO;

import java.util.ArrayList;
import java.util.Map;

public interface CustomerService {

    /**
     * 查詢客戶列表
     *
     * @param pageNum 頁碼
     * @param pageSize 每頁資料筆數
     * @param customer  客戶查詢參數封裝類
     * @return PageBean<Customer> 分頁查詢結果
     */
    PageBean<CustomerVO> listCustomers(Integer pageNum, Integer pageSize, Customer customer);

    /**
     * 根據用戶名查詢客戶
     *
     * @param id 客戶id
     */
    CustomerVO getCustomerById(Integer id);

    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     */
    void addCustomer(Customer customer);

    /**
     * 更新客戶資料
     *
     * @param customer 客戶對象
     */
    void updateCustomer(Customer customer);

    /**
     * 刪除客戶
     *
     * @param id 客戶id
     */
    void deleteCustomerById(Integer id);


    /**
     * 查詢所有客戶來源
     * @return 客戶列表
     */
    ArrayList<Integer> listApproaches();

    /**
     * 查詢所有客戶初次來店日期(統計每月新增客戶數)
     * @return Map<Integer,int[]>(key為年分，value為每月新增人數數組)
     */
    Map<Integer,int[]> listFirstLesson();

    /**
     * 查詢客戶性別統計數據
     * @return 數組[女性數量,男性數量]
     */
    int[] listGender();
}
