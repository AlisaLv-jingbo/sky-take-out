package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        List<DishVO> dishVOList = dishMapper.pageQuery(dishPageQueryDTO);
        Page<DishVO> page = (Page<DishVO>) dishVOList;

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
//        ids dish ids 菜品id
        //检查是否在售状态 如果不在售状态下可以删除
        if (ids !=null && !ids.isEmpty()) {
            int nums = dishMapper.queryStatus(ids);
            if (nums != ids.size()) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //检查是否关联在套餐内
        List<Long> mealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(mealIds !=null && !mealIds.isEmpty())
        {
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }


            //删除菜品
            dishMapper.deleteBatchByIds(ids);
//        删除口味记录
            dishFlavorMapper.deleteBatchByDishId(ids);

    }

    /**
     * 根据id查询菜品和菜品口味
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);

        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除原有口味
        dishFlavorMapper.deleteByDishId(dish.getId());

        //插入新的口味
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (dishFlavorList != null && !dishFlavorList.isEmpty()) {
            dishFlavorMapper.insertBatch(dishFlavorList, dish.getId());
        }
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
//        更新菜品状态
        Dish dish=Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);


//        若关联套餐了，则套餐也要停售
        if(status == StatusConstant.DISABLE)
        {
            List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishId(id);
            if(setmealIds !=null && !setmealIds.isEmpty())
            {
                for(Long setmealId:setmealIds)
                {
                    Setmeal setmeal=Setmeal.builder()
                            .status(StatusConstant.DISABLE)
                            .id(setmealId)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


    /**
     * 新增菜品和口味
     *
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFlavor(DishDTO dishDTO) {
//        向dish表插入一条数据，向口味表插入n条数据  注意为事务型
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (dishFlavorList != null && dishFlavorList.size() > 0) {

            dishFlavorMapper.insertBatch(dishFlavorList, dishId);

        }


    }
}
