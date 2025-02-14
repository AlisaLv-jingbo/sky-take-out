package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(value= OperationType.INSERT)
    void insert(Dish dish);


    List<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);


    int queryStatus(List<Long> ids);

    @Delete("delete from sky_take_out.dish where id=#{id} ")
    void deleteById(Long id);

    void deleteBatchByIds(List<Long> ids);

    @Select("select * from sky_take_out.dish where id=#{id}")
    Dish getById(Long id);

    @AutoFill(value= OperationType.UPDATE)
    void update(Dish dish);

//    @Select("select * from sky_take_out.dish where category_id = #{categoryId} and status = 1")
//    List<Dish> getByCategoryId(Long categoryId);

    List<Dish> list(Dish dish);

    @Select("select a.* from sky_take_out.dish a, sky_take_out.setmeal_dish b where a.id = b.dish_id and b.setmeal_id = #{id} and a.status = 0")
    List<Dish> getBySetmealId(Long id);


    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
