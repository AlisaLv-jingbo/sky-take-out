package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
}
