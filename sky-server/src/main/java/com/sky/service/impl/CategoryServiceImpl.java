package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import com.sky.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;



    @Override
    public void save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setStatus(0);

        String token = request.getHeader(jwtProperties.getAdminTokenName());
        Long empId = JwtUtil.getUserId(jwtProperties.getAdminSecretKey(), token);
//        Long userId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());

        category.setCreateUser(empId);
        category.setUpdateUser(empId);

        categoryMapper.insert(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        List<Category> categoryList = categoryMapper.pageQuery(categoryPageQueryDTO);
        Page<Category> page = (Page<Category>) categoryList;

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());

        String token = request.getHeader(jwtProperties.getAdminTokenName());
        Long empId = JwtUtil.getUserId(jwtProperties.getAdminSecretKey(), token);

        category.setUpdateUser(empId);

        categoryMapper.update(category);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .build();

        String token = request.getHeader(jwtProperties.getAdminTokenName());
        Long empId = JwtUtil.getUserId(jwtProperties.getAdminSecretKey(), token);

        category.setUpdateUser(empId);

        categoryMapper.update(category);
    }

    @Override
    public List<Category> getByType(Integer type) {
        return categoryMapper.list(type);
    }
}
