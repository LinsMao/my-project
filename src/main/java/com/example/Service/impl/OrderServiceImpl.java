package com.example.Service.impl;

import com.example.Entity.Orders;
import com.example.Entity.OrderItem;
import com.example.Entity.Product;
import com.example.Entity.UserAddress;
import com.example.Mapper.CartMapper;
import com.example.Mapper.OrderItemMapper;
import com.example.Mapper.OrdersMapper;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.UserAddressMapper;
import com.example.Service.OrderService;
import com.example.VO.CartVO;
import com.example.VO.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional
    public String createOrder(Long userId, Long addressId, String remark) {
        // 验证并获取收货地址
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new RuntimeException("收货地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权使用该地址");
        }

        // 获取选中的购物车商品
        List<CartVO> cartList = cartMapper.selectSelectedCartListWithProduct(userId);
        if (cartList == null || cartList.isEmpty()) {
            throw new RuntimeException("请选择要结算的商品");
        }

        // 验商品状态和库存
        for (CartVO cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            
            // 商品是否存在
            if (product == null) {
                throw new RuntimeException("商品【" + cart.getProductName() + "】不存在");
            }
            
            // 商品是否已下架
            if (product.getStatus() != 1) {
                throw new RuntimeException("商品【" + cart.getProductName() + "】已下架");
            }
            
            // 库存是否充足
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("商品【" + cart.getProductName() + "】库存不足，当前库存：" + product.getStock());
            }
        }

        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartVO cart : cartList) {
            BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Orders order = new Orders();
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 优惠
        order.setFreightAmount(BigDecimal.ZERO); // 运费为0
        order.setPayType(1); // 1-微信支付
        order.setPayStatus(0); // 0-待支付
        order.setOrderStatus(0); // 0-待付款
        
        //收货地址
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        
        order.setRemark(remark);

        ordersMapper.insert(order);

        // 创建订单商品并扣减库存
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cart : cartList) {
            // 创建订单项
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductImage(cart.getProductImage());
            item.setUnit(cart.getUnit());
            item.setPrice(cart.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalAmount(cart.getPrice().multiply(new BigDecimal(cart.getQuantity())));
            orderItems.add(item);
            
            // 扣减库存
            Product product = productMapper.selectById(cart.getProductId());
            int newStock = product.getStock() - cart.getQuantity();
            product.setStock(newStock);
            productMapper.updateStock(product);
        }
        orderItemMapper.batchInsert(orderItems);

        // 删除购物车中已结算的商品
        cartMapper.deleteSelected(userId);

        return orderNo;
    }

    @Override
    public List<OrderVO> getOrderList(Long userId, Integer orderStatus) {
        // 查询订单列表
        List<Orders> ordersList = ordersMapper.selectByUserId(userId, orderStatus);
        
        // 组装订单VO
        List<OrderVO> result = new ArrayList<>();
        for (Orders order : ordersList) {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setPayAmount(order.getPayAmount());
            vo.setCreateTime(order.getCreateTime());
            
            // 查询订单项
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            vo.setItems(items);
            
            result.add(vo);
        }
        
        return result;
    }

    @Override
    @Transactional
    public void cancelOrder(String orderNo, Long userId) {
        // 查询订单
        Orders order = ordersMapper.selectByOrderNo(orderNo);
        
        // 验证订单
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("只有待付款订单可以取消");
        }

        ordersMapper.updateOrderStatus(order.getId(), 4, LocalDateTime.now());
    }

    // 生成订单号：时间戳 + 随机数
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return timestamp + String.format("%04d", random);
    }
}
