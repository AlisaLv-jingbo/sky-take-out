package com.sky.service.impl;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    public void save(SetmealDTO setmealDTO) {
        log.info("新增套餐：{}", setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.save(setmeal);

        Long semealId = setmeal.getId();
        // 新增套餐和菜品的关联关系
        List<SetmealDish> dishs = setmealDTO.getSetmealDishes();
        if(dishs != null && dishs.size() > 0) {
            setmealDishMapper.saveBatch(dishs, semealId);
        }

    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public SetmealVO getById(long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        List<SetmealDish> setmealDishes = setmealDishMapper.queryBySetmealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 更新套餐
     * @param setmealDTO
     */
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        log.info("更新套餐：{}", setmealDTO);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        Long semealId = setmeal.getId();
        // 删除套餐和菜品的关联关系
        setmealDishMapper.deleteBySetmealId(semealId);
        // 新增套餐和菜品的关联关系
        List<SetmealDish> dishs = setmealDTO.getSetmealDishes();
        if(dishs != null && dishs.size() > 0) {
            setmealDishMapper.saveBatch(dishs, semealId);
        }
    }

    /**
     * 停售或启售套餐
     * @param status
     * @param id
     */
    @Override
    public void stopOrStart(Integer status, Long id) {

        if (status == StatusConstant.ENABLE)
        {
            //查询套餐下是否有停售菜品
            List<Dish> dishes = dishMapper.getBySetmealId(id);
            if(dishes != null && !dishes.isEmpty())
            {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);

    }

    /**
     * 根据id删除套餐  支持批量删除
     * @param ids
     * @return
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //首先查询状态 停止售卖才可以删除套餐
        int nums=setmealMapper.queryStatus(ids);
        if(nums != ids.size()){
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        // 删除套餐 setmeal 表 及setmealdish表
        setmealMapper.deleteBatch(ids);
        setmealDishMapper.deleteBatchBySetmealIds(ids);
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        List<SetmealVO> setmealVOList = setmealMapper.pageQuery(setmealPageQueryDTO);
        Page<SetmealVO> page = (Page<SetmealVO>) setmealVOList;
        return new PageResult(page.getTotal(), page.getResult());
    }

}
