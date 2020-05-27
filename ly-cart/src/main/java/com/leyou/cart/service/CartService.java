package com.leyou.cart.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    public void addCart(Cart cart) {

        UserInfo userInfo = LoginInterceptor.get();
        // 查询
        BoundHashOperations<String, Object, Object> hashOperations =
                redisTemplate.boundHashOps(userInfo.getId().toString());
        String skuId = cart.getSkuId().toString();
        Integer num = cart.getNum();
        // 判断是否有
        if (hashOperations.hasKey(cart.getSkuId().toString())) {
            String cartJson = hashOperations.get(skuId).toString();
            cart = JsonUtils.toBean(cartJson, Cart.class);
            cart.setNum(num + cart.getNum());
            hashOperations.put(skuId, JsonUtils.serialize(cart));
        } else {
            // 没有，新增
            cart.setUserId(userInfo.getId());
            // 查询商品信息
            Sku sku = goodsClient.querySkuById(cart.getSkuId());
            cart.setPrice(sku.getPrice());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" :
                    StringUtils.split(sku.getImages(), ",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setTitle(sku.getTitle());
        }
        hashOperations.put(skuId, JsonUtils.serialize(cart));
    }

    public List<Cart> queryCarts() {
        // 获取用户的信息
        UserInfo userInfo = LoginInterceptor.get();

        // 判断hash操作对象是否存在
        if (!redisTemplate.hasKey(userInfo.getId().toString())) {
            return null;
        }

        // 查询
        BoundHashOperations<String, Object, Object> hashOperations =
                redisTemplate.boundHashOps(userInfo.getId().toString());
        //

        List<Object> cartJson = hashOperations.values();

        return cartJson.stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());
    }
}
