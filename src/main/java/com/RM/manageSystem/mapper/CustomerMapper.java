package com.RM.manageSystem.mapper;


import com.RM.manageSystem.model.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CustomerMapper {

    /**
     * 查詢客戶列表(預設為最後來店降序排列)
     *
     * @param customer  客戶查詢參數封裝類
     * @return List<Customer> 客戶列表
     */
    List<Customer> listCustomers(Customer customer);

    /**
     * 查詢所有客戶來源
     *
     * @return 客戶來源列表
     */
    @Select("select approaches from customertest where isDelete = 0")
    List<String> listApproaches();

    /**
     * 查詢所有客戶初次來店日期(統計每月新增客戶數)
     * @return 客戶初次來店日期列表
     */
    @Select("select first_lesson from customertest where isDelete = 0")
    List<String> listFirstLesson();

    @Select("select gender from customertest where isDelete = 0")
    List<Character> listGender();
    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     * @return int 新增的行數
     */
    int addCustomer(Customer customer);

    /**
     * 新增客戶
     *
     * @param customerName 客戶對象
     * @return int 新增的行數
     */
    @Select("select * from customertest where customer_name = #{customerName}")
    Customer getCustomerByName(String customerName);

    /**
     * 更新客戶資訊
     *
     * @param customer 客戶對象
     * @return int 更新的行數
     */
    int updateCustomer(Customer customer);

    /**
     * 依據id刪除客戶
     *
     * @param id 客戶id
     * @return int 刪除的行數
     */

    @Update("update customertest set isDelete = 1, update_time = now() where id = #{id}")
    int deleteCustomerById(Integer id);

    @Select("select customer_name from customertest where id = #{id} and isDelete = 0")
    String findCustomerById(Integer id);


    @Select("select * from customertest where id = #{id} and isDelete = 0")
    Customer getCustomerById(Integer id);

}
