package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    @Select("select setmeal_id from sky_take_out.setmeal_dish where dish_id=#{id}")
    List<Long> getSetmealIdsByDishId(Long id);


    void saveBatch(List<SetmealDish> dishs, Long semealId);

    void deleteBatchBySetmealIds(List<Long> ids);

    @Select("select * from sky_take_out.setmeal_dish where setmeal_id=#{setmealId}")
    List<SetmealDish> queryBySetmealId(long setmealId);

    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id=#{semealId}")
    void deleteBySetmealId(Long semealId);
}
