package com.tsong.cmall.shopping_cart.web.vo;

import com.tsong.cmall.vo.MyCouponVO;
import com.tsong.cmall.vo.ShoppingCartItemVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Tsong
 * @Date 2023/4/2 23:12
 */
@Data
public class ShoppingCartConfirmVO implements Serializable {
    @Schema(title = "参与结算的商品")
    List<ShoppingCartItemVO> itemsForConfirmPage;
    @Schema(title = "当前商品可用的优惠券")
    List<MyCouponVO> myCouponVOList;
}
