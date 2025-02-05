package com.sky.controller.user;


import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户店铺管理接口")
public class ShopController {

    private static final String KEY = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取商铺状态")
    public Result<Integer> status() {
        int status= (int) redisTemplate.opsForValue().get(KEY);
        log.info("获取商铺状态:{}", status==1?"营业中":"打烊中");
        return Result.success(status);
    }

}
