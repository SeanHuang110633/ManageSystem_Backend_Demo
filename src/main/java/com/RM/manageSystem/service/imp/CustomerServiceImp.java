package com.RM.manageSystem.service.imp;

import com.RM.manageSystem.common.PageBean;
import com.RM.manageSystem.exception.BusinessException;
import com.RM.manageSystem.exception.SystemException;
import com.RM.manageSystem.mapper.CustomerMapper;
import com.RM.manageSystem.model.entity.Customer;
import com.RM.manageSystem.model.vo.CustomerVO;
import com.RM.manageSystem.service.CustomerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.RM.manageSystem.common.ErrorCode.*;

/**
 * Customer Service
 */
@Slf4j
@Service
public class CustomerServiceImp implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查詢客戶列表
     *
     * @param pageNum  頁碼
     * @param pageSize 每頁資料筆數
     * @param customer 客戶查詢參數封裝類
     * @return PageBean<Customer> 分頁查詢結果
     */
    @Override
    public PageBean<CustomerVO> listCustomers(Integer pageNum, Integer pageSize, Customer customer) {
        log.info("查詢客戶列表: pageNum={}, pageSize={}, customerQueryRequest={}",
                pageNum, pageSize, customer);

        // 參數檢查
        if (pageNum == null || pageNum <= 0) {
            throw new BusinessException(PARAMS_ERROR, "頁碼必須為正數");
        }
        if (pageSize == null || pageSize <= 0) {
            throw new BusinessException(PARAMS_ERROR, "每頁顯示數量必須為正數");
        }

        // 使用PageHelper分頁，傳入頁數和每頁顯示的數量
        PageMethod.startPage(pageNum, pageSize);
        Page<Customer> customerPage = (Page<Customer>) customerMapper.listCustomers(customer);
        if (customerPage == null) {
            log.error("客戶列表查詢返回值為空");
            throw new BusinessException(SYSTEM_ERROR);
        }

        // 將 Customer 列表轉換為 CustomerVO 列表
        List<CustomerVO> customerVOList = customerPage.stream()
                .map(this::convertToCustomerVO)
                .toList();

        // 創建一個新的 Page 對象，保留分頁信息並設置轉換後的數據
        Page<CustomerVO> customerVOPage = new Page<>(pageNum, pageSize);
        customerVOPage.setTotal(customerPage.getTotal());
        customerVOPage.addAll(customerVOList);

        // 創建一個 PageBean 對象來包裝分頁結果
        PageBean<CustomerVO> pageBean = new PageBean<>();
        pageBean.setTotal(customerVOPage.getTotal());
        pageBean.setItems(customerVOPage.getResult());

        log.info("查詢客戶列表成功: {}", pageBean);
        return pageBean;
    }

    /**
     * 依據姓名查詢客戶
     *
     * @param id 客戶id
     * @return CustomerVO 客戶對象安全封裝類
     */
    @Override
    public CustomerVO getCustomerById(Integer id) {
        Customer customerById = customerMapper.getCustomerById(id);
        if (customerById == null) {
            throw new BusinessException(NOT_FOUND_ERROR, "該客戶不存在");
        }
        return convertToCustomerVO(customerById);
    }


    /**
     * 新增客戶
     *
     * @param customer 客戶對象
     */
    @Override
    public void addCustomer(Customer customer) {
        log.info("service新增客戶");
        // 如果用戶
        int addedCustomerNum = customerMapper.addCustomer(customer);
        if (addedCustomerNum > 0) {
            log.info("新增客戶成功");
            return;
        }
        log.error("新增客戶失敗");
        throw new SystemException(SYSTEM_ERROR);
    }

    /**
     * 更新客戶資料
     *
     * @param customer 客戶對象
     */
    @Override
    public void updateCustomer(Customer customer) {
        log.info("更新客戶資料: {}", customer);
        if (!findCustomerById(customer.getId())) {
            throw new BusinessException(NOT_FOUND_ERROR, "更新客戶不存在");
        }
        int updatedCustomerNum = customerMapper.updateCustomer(customer);
        if (updatedCustomerNum > 0) {
            log.info("更新客戶資料成功: {}", customer);
            return;
        }
        log.error("更新客戶資料失敗: {}", customer);
        throw new SystemException(SYSTEM_ERROR);
    }


    /**
     * 刪除客戶
     *
     * @param id 客戶id
     */
    @Override

    public void deleteCustomerById(Integer id) {
        log.info("刪除客戶ID: {}", id);
        if (!findCustomerById(id)) {
            throw new BusinessException(NOT_FOUND_ERROR, "刪除客戶不存在");
        }
        int deletedCustomerNum = customerMapper.deleteCustomerById(id);
        if (deletedCustomerNum > 0) {
            log.info("刪除客戶成功ID: {}", id);
            return;
        }
        log.error("刪除客戶失敗ID: {}", id);
        throw new SystemException(SYSTEM_ERROR);
    }


    /**
     * 查詢所有客戶來源
     *
     * @return 客戶列表
     */
    @Override
    public ArrayList<Integer> listApproaches() {
        log.info("查詢所有客戶來源");
        List<String> approachList = customerMapper.listApproaches();
        if (approachList == null) {
            throw new BusinessException(SYSTEM_ERROR);
        }

        // 分別計算3個管道的數量
        int byFriend = 0;
        int byInternet = 0;
        int byOthers = 0;

        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {
        }.getType();

        for (String str : approachList) {
            if (str == null || str.isBlank()) {
                continue;
            }
            List<String> stringList = gson.fromJson(str, listType);
            if (stringList.contains("親友介紹")) {
                byFriend++;
            }
            if (stringList.contains("網路社群")) {
                byInternet++;
            }
            if (stringList.contains("其他")) {
                byOthers++;
            }
        }

        // 調整返回值(記得通知第一版的前端使用者)
        //Map<String, Integer> approachesAggregate = new HashMap<>();
        //approachesAggregate.put("親友介紹", byFriend);
        //approachesAggregate.put("網路社群", byInternet);
        //approachesAggregate.put("其他", byOthers);
        ArrayList<Integer> approachesAggregate = new ArrayList<>();
        approachesAggregate.add(byFriend);
        approachesAggregate.add(byInternet);
        approachesAggregate.add(byOthers);

        log.info("查詢客戶來源成功");
        return approachesAggregate;
    }


    /**
     * 查詢所有客戶初次來店日期(統計每月新增客戶數)
     *
     * @return Map<Integer, int [ ]>(key為年分，value為每月新增人數數組)
     */
    @Override
    public Map<Integer, int[]> listFirstLesson() {
        log.info("查詢所有客戶初次來店日期");
        List<String> dateList = customerMapper.listFirstLesson();
        log.info("查詢成功");

        //將數據轉換成Map格式
        Map<Integer, int[]> dateMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (String dateStr : dateList) {
            if (dateStr != null) {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                int year = date.getYear();
                int month = date.getMonthValue();

                dateMap.putIfAbsent(year, new int[12]);
                dateMap.get(year)[month - 1]++;
            }
        }

        return dateMap;
    }

    /**
     * 查詢客戶性別統計數據
     *
     * @return 數組[女性數量, 男性數量]
     */
    @Override
    public int[] listGender() {
        log.info("查詢客戶性別數據");
        List<Character> genderList = customerMapper.listGender();
        log.info("查詢成功");
        int countF = (int) genderList.stream().filter(ch -> ch != null && ch == 'F').count();
        return new int[]{countF, genderList.size() - countF};
    }


    /**
     * 根據Id判斷客戶是否存在
     *
     * @param id 客戶id
     * @return 存在與否
     */
    private boolean findCustomerById(Integer id) {
        String userName = customerMapper.findCustomerById(id);
        return userName != null;
    }

    /**
     * 轉換為 customerVO 封裝類
     *
     * @param customer 原始客戶數據
     * @return customerVO
     */
    private CustomerVO convertToCustomerVO(Customer customer) {
        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(customer, customerVO);
        return customerVO;
    }
}
