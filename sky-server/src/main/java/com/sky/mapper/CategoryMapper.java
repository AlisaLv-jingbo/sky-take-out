package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("insert into sky_take_out.category(type, name, sort, create_time, update_time, create_user, update_user, status) " +
            "values(#{type}, #{name}, #{sort}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}, #{status})")
    @AutoFill(value=OperationType.INSERT)
    void insert(Category category);

    List<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @Delete("delete from sky_take_out.category where id = #{id}")
    void deleteById(Long id);

    @AutoFill(value=OperationType.UPDATE)
    void update(Category category);

    List<Category> list(Integer type);
}
