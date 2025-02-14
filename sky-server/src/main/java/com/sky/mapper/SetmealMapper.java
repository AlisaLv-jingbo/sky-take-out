package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);


    /**
     * 根据套餐id进行更新
     * @param setmeal
     * @return
     */
    @AutoFill(value= OperationType.UPDATE)
    void update(Setmeal setmeal);

    @AutoFill(value= OperationType.INSERT)
    void save(Setmeal setmeal);

    List<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    int queryStatus(List<Long> ids);


    void deleteBatch(List<Long> ids);

    @Select("select * from sky_take_out.setmeal where id=#{id}")
    Setmeal getById(long id);

    List<Setmeal> list(Setmeal setmeal);

    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from sky_take_out.setmeal_dish sd left join sky_take_out.dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long id);

    /**
     * 根据条件统计套餐数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
