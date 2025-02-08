package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void update(ShoppingCart cart);

    @Insert("insert into sky_take_out.shopping_cart (user_id, dish_id, setmeal_id, name, image, amount, number,create_time,dish_flavor) " +
            "values (#{userId}, #{dishId}, #{setmealId}, #{name}, #{image}, #{amount}, #{number}, #{createTime},#{dishFlavor})")
    void insert(ShoppingCart shoppingCart);

    @Delete("delete from sky_take_out.shopping_cart where id = #{id}")
    void deleteById(Long id);

    @Delete("delete from sky_take_out.shopping_cart where user_id = #{id}")
    void deleteByUserId(Long id);
}
