package com.example.Service.impl;

import com.example.DTO.merchant.MerchantOrderListRequest;
import com.example.Entity.Admin;
import com.example.Entity.Orders;
import com.example.Entity.OrderItem;
import com.example.Entity.Product;
import com.example.Entity.UserAddress;
import com.example.Entity.LogisticsTrace;
import com.example.Mapper.CartMapper;
import com.example.Mapper.OrderItemMapper;
import com.example.Mapper.OrdersMapper;
import com.example.Mapper.ProductMapper;
import com.example.Mapper.UserAddressMapper;
import com.example.Mapper.LogisticsTraceMapper;
import com.example.Service.OrderService;
import com.example.Utils.AddressUtil;
import com.example.VO.CartVO;
import com.example.VO.DashboardStatsVO;
import com.example.VO.DashboardTodosVO;
import com.example.VO.MerchantOrderItemVO;
import com.example.VO.MerchantOrderVO;
import com.example.VO.OrderVO;
import com.example.VO.RecentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    @Autowired
    private com.example.Mapper.OrderMapper orderMapper;
    
    @Autowired
    private LogisticsTraceMapper logisticsTraceMapper;
    
    @Autowired
    private com.example.Mapper.admin.AdminMapper adminMapper;

    @Override
    @Transactional
    public List<String> createOrder(Long userId, Long addressId, String remark, List<com.example.DTO.CreateOrderRequest.OrderItemRequest> items) {
        // 验证并获取收货地址
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null) {
            throw new RuntimeException("收货地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new RuntimeException("无权使用该地址");
        }

        List<CartVO> cartList;
        boolean isDirectPurchase = (items != null && !items.isEmpty());
        
        if (isDirectPurchase) {
            // 立即购买模式：从传入的商品列表构建CartVO
            cartList = new ArrayList<>();
            for (com.example.DTO.CreateOrderRequest.OrderItemRequest item : items) {
                Product product = productMapper.selectById(item.getProductId());
                if (product == null) {
                    throw new RuntimeException("商品不存在");
                }
                if (product.getStatus() != 1) {
                    throw new RuntimeException("商品【" + product.getName() + "】已下架");
                }
                if (product.getStock() < item.getQuantity()) {
                    throw new RuntimeException("商品【" + product.getName() + "】库存不足，当前库存：" + product.getStock());
                }
                
                // 构建CartVO对象
                CartVO cartVO = new CartVO();
                cartVO.setProductId(product.getId());
                cartVO.setProductName(product.getName());
                cartVO.setProductImage(product.getMainImage());
                cartVO.setUnit(product.getUnit());
                cartVO.setPrice(product.getPrice());
                cartVO.setQuantity(item.getQuantity());
                cartList.add(cartVO);
            }
        } else {
            // 购物车结算模式：从购物车获取选中的商品
            cartList = cartMapper.selectSelectedCartListWithProduct(userId);
            if (cartList == null || cartList.isEmpty()) {
                throw new RuntimeException("请选择要结算的商品");
            }

            // 验证商品状态和库存
            for (CartVO cart : cartList) {
                Product product = productMapper.selectById(cart.getProductId());
                
                if (product == null) {
                    throw new RuntimeException("商品【" + cart.getProductName() + "】不存在");
                }
                
                if (product.getStatus() != 1) {
                    throw new RuntimeException("商品【" + cart.getProductName() + "】已下架");
                }
                
                if (product.getStock() < cart.getQuantity()) {
                    throw new RuntimeException("商品【" + cart.getProductName() + "】库存不足，当前库存：" + product.getStock());
                }
            }
        }

        // 按商户分组购物车商品
        Map<Long, List<CartVO>> merchantCartMap = new HashMap<>();
        Map<Long, String> merchantNameMap = new HashMap<>();
        
        for (CartVO cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            Long merchantId = product.getMerchantId();
            
            // 分组商品
            merchantCartMap.computeIfAbsent(merchantId, k -> new ArrayList<>()).add(cart);
            
            // 查询真实的商户名称
            if (!merchantNameMap.containsKey(merchantId)) {
                Admin merchant = adminMapper.findById(merchantId);
                if (merchant != null && merchant.getRole() == 1) {
                    merchantNameMap.put(merchantId, merchant.getNickname());
                } else {
                    merchantNameMap.put(merchantId, "商户" + merchantId);
                }
            }
        }

        // 为每个商户创建独立订单
        List<String> orderNos = new ArrayList<>();
        for (Map.Entry<Long, List<CartVO>> entry : merchantCartMap.entrySet()) {
            Long merchantId = entry.getKey();
            List<CartVO> merchantCarts = entry.getValue();
            String merchantName = merchantNameMap.get(merchantId);
            
            String orderNo = createSingleMerchantOrder(userId, addressId, remark, merchantId, merchantName, merchantCarts, address);
            orderNos.add(orderNo);
        }

        // 只有购物车结算模式才删除购物车商品
        if (!isDirectPurchase) {
            cartMapper.deleteSelected(userId);
        }

        return orderNos;
    }
    
    /**
     * 为单个商户创建订单
     */
    private String createSingleMerchantOrder(Long userId, Long addressId, String remark, 
                                            Long merchantId, String merchantName,
                                            List<CartVO> merchantCarts, UserAddress address) {
        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartVO cart : merchantCarts) {
            BigDecimal itemTotal = cart.getPrice().multiply(new BigDecimal(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Orders order = new Orders();
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setMerchantName(merchantName);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayType(1);
        order.setPayStatus(0);
        order.setOrderStatus(0);
        
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        
        order.setRemark(remark);

        ordersMapper.insert(order);

        // 创建订单商品并扣减库存
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cart : merchantCarts) {
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
            vo.setMerchantId(order.getMerchantId());
            vo.setMerchantName(order.getMerchantName());
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
    public OrderVO getOrderDetail(String orderNo, Long userId) {
        // 查询订单
        Orders order = ordersMapper.selectByOrderNo(orderNo);
        
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看该订单");
        }
        
        // 组装订单VO
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setMerchantId(order.getMerchantId());
        vo.setMerchantName(order.getMerchantName());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setPayAmount(order.getPayAmount());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setDeliveryCompany(order.getDeliveryCompany());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setDeliveryTime(order.getDeliveryTime());
        
        // 查询订单项
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        vo.setItems(items);
        
        return vo;
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

        // 恢复库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                // 恢复库存
                int newStock = product.getStock() + item.getQuantity();
                product.setStock(newStock);
                productMapper.updateStock(product);
            }
        }

        // 更新订单状态为已取消
        ordersMapper.updateOrderStatus(order.getId(), 4, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void confirmReceipt(String orderNo, Long userId) {
        // 查询订单
        Orders order = ordersMapper.selectByOrderNo(orderNo);

        // 验证订单
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }

        if (order.getOrderStatus() != 2) {
            throw new RuntimeException("只有待收货订单可以确认收货");
        }

        // 增加商品销量
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : orderItems) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                // 增加销量
                int newSoldCount = (product.getSoldCount() != null ? product.getSoldCount() : 0) + item.getQuantity();
                productMapper.updateSoldCount(product.getId(), newSoldCount);
            }
        }

        // 更新订单状态为已完成，并记录收货时间
        ordersMapper.updateOrderStatusWithReceiveTime(order.getId(), 3, LocalDateTime.now());
    }
    
    @Override
    public List<LogisticsTrace> getLogisticsTrace(String orderNo, Long userId) {
        // 验证订单归属
        Orders order = ordersMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在或无权限");
        }
        
        // 查询物流轨迹，按时间倒序
        return logisticsTraceMapper.selectByOrderNo(orderNo);
    }


    // 生成订单号：时间戳 + 随机数
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 10000);
        return timestamp + String.format("%04d", random);
    }
    
    @Override
    public Map<String, Object> getMerchantOrders(MerchantOrderListRequest request) {
        if (request.getMerchantId() == null) {
            throw new IllegalArgumentException("商家ID不能为空");
        }
        
        if (request.getPage() < 1) request.setPage(1);
        if (request.getSize() <= 0) request.setSize(10);
        
        int offset = (request.getPage() - 1) * request.getSize();
        
        // 查询订单列表
        List<Orders> orders = orderMapper.selectMerchantOrders(
                request.getMerchantId(),
                request.getOrderStatus(),
                request.getOrderNo(),
                request.getPhone(),
                request.getStartTime(),
                request.getEndTime(),
                offset,
                request.getSize()
        );
        
        // 查询总数
        int total = orderMapper.countMerchantOrders(
                request.getMerchantId(),
                request.getOrderStatus(),
                request.getOrderNo(),
                request.getPhone(),
                request.getStartTime(),
                request.getEndTime()
        );
        
        // 转换为VO
        List<MerchantOrderVO> voList = new ArrayList<>();
        for (Orders order : orders) {
            MerchantOrderVO vo = new MerchantOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserId(order.getUserId());
            vo.setMerchantId(order.getMerchantId());
            vo.setMerchantName(order.getMerchantName());
            vo.setReceiverName(order.getReceiverName());
            vo.setReceiverPhone(order.getReceiverPhone());
            vo.setReceiverAddress(order.getReceiverAddress());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setPayAmount(order.getPayAmount());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setDeliveryCompany(order.getDeliveryCompany());
            vo.setDeliveryNo(order.getDeliveryNo());
            vo.setCreateTime(order.getCreateTime());
            vo.setPayTime(order.getPayTime());
            vo.setDeliveryTime(order.getDeliveryTime());
            vo.setRemark(order.getRemark());
            
            // 查询订单项（拆单后，所有订单项都属于该商户，无需过滤）
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            List<MerchantOrderItemVO> itemVOList = new ArrayList<>();
            for (OrderItem item : items) {
                MerchantOrderItemVO itemVO = new MerchantOrderItemVO();
                itemVO.setId(item.getId());
                itemVO.setProductId(item.getProductId());
                itemVO.setProductName(item.getProductName());
                itemVO.setProductImage(item.getProductImage());
                itemVO.setUnit(item.getUnit());
                itemVO.setPrice(item.getPrice());
                itemVO.setQuantity(item.getQuantity());
                itemVO.setTotalAmount(item.getTotalAmount());
                itemVOList.add(itemVO);
            }
            vo.setItems(itemVOList);
            voList.add(vo);
        }
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", voList);
        result.put("total", total);
        result.put("page", request.getPage());
        result.put("size", request.getSize());
        
        return result;
    }
    
    @Override
    public MerchantOrderVO getMerchantOrderDetail(String orderNo, Long merchantId) {
        if (orderNo == null || merchantId == null) {
            throw new IllegalArgumentException("订单号和商家ID不能为空");
        }
        
        // 查询订单并验证权限
        Orders order = orderMapper.selectByOrderNoAndMerchantId(orderNo, merchantId);
        if (order == null) {
            throw new RuntimeException("订单不存在或无权限访问");
        }
        
        // 转换为VO
        MerchantOrderVO vo = new MerchantOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setMerchantId(order.getMerchantId());
        vo.setMerchantName(order.getMerchantName());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setDeliveryCompany(order.getDeliveryCompany());
        vo.setDeliveryNo(order.getDeliveryNo());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setDeliveryTime(order.getDeliveryTime());
        vo.setRemark(order.getRemark());
        
        // 查询订单项（拆单后，所有订单项都属于该商户）
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        List<MerchantOrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem item : items) {
            MerchantOrderItemVO itemVO = new MerchantOrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(item.getProductName());
            itemVO.setProductImage(item.getProductImage());
            itemVO.setUnit(item.getUnit());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            itemVO.setTotalAmount(item.getTotalAmount());
            itemVOList.add(itemVO);
        }
        vo.setItems(itemVOList);
        
        return vo;
    }
    
    @Override
    @Transactional
    public void deliverOrder(String orderNo, Long merchantId, String deliveryCompany, String deliveryNo) {
        if (orderNo == null || merchantId == null) {
            throw new IllegalArgumentException("订单号和商家ID不能为空");
        }
        
        if (deliveryCompany == null || deliveryCompany.trim().isEmpty()) {
            throw new IllegalArgumentException("请输入物流公司");
        }
        
        if (deliveryNo == null || deliveryNo.trim().isEmpty()) {
            throw new IllegalArgumentException("请输入物流单号");
        }
        
        // 验证订单权限
        Orders order = orderMapper.selectByOrderNoAndMerchantId(orderNo, merchantId);
        if (order == null) {
            throw new RuntimeException("订单不存在或无权限操作");
        }
        
        // 验证订单状态
        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("只有待发货订单可以发货");
        }
        
        // 更新发货信息
        int result = orderMapper.updateDeliveryInfo(orderNo, deliveryCompany, deliveryNo);
        if (result == 0) {
            throw new RuntimeException("发货失败");
        }
        
        // 生成物流轨迹
        generateLogisticsTrace(order, deliveryCompany);
    }
    
    /**
     * 生成物流轨迹
     */
    private void generateLogisticsTrace(Orders order, String deliveryCompany) {
        String receiverCity = AddressUtil.extractCity(order.getReceiverAddress());
        String orderNo = order.getOrderNo();
        LocalDateTime now = LocalDateTime.now();
        
        List<LogisticsTrace> traces = new ArrayList<>();
        
        if ("自配送".equals(deliveryCompany)) {
            // 自配送物流
            traces.add(createTrace(orderNo, "配送中", "商家正在配送", receiverCity, now));
            traces.add(createTrace(orderNo, "配送中", "配送员已出发", receiverCity, now.plusHours(1)));
            traces.add(createTrace(orderNo, "派送中", "配送员正在派送中，请保持电话畅通", receiverCity, now.plusHours(2)));
        } else {
            // 快递配送物流
            String shipCity = "深圳市"; // 假设发货城市为深圳
            
            traces.add(createTrace(orderNo, "已发货", "商家已发货", shipCity, now));
            traces.add(createTrace(orderNo, "已揽收", "快递已揽收", shipCity, now.plusHours(2)));
            traces.add(createTrace(orderNo, "运输中", "快递已到达" + shipCity + "转运中心", shipCity, now.plusHours(4)));
            
            // 如果收货城市不同，添加到达收货城市的记录
            if (!shipCity.equals(receiverCity)) {
                traces.add(createTrace(orderNo, "运输中", "快递已到达" + receiverCity + "转运中心", receiverCity, now.plusHours(8)));
            }
            
            traces.add(createTrace(orderNo, "派送中", "快递正在派送中，请保持电话畅通", receiverCity, now.plusHours(10)));
        }
        
        // 批量插入物流轨迹
        if (!traces.isEmpty()) {
            logisticsTraceMapper.batchInsert(traces);
        }
    }
    
    /**
     * 创建物流轨迹记录
     */
    private LogisticsTrace createTrace(String orderNo, String status, String desc, String location, LocalDateTime time) {
        LogisticsTrace trace = new LogisticsTrace();
        trace.setOrderNo(orderNo);
        trace.setTraceStatus(status);
        trace.setTraceDesc(desc);
        trace.setLocation(location);
        trace.setTraceTime(time);
        return trace;
    }

    @Override
    public DashboardStatsVO getMerchantDashboardStats(Long merchantId) {
        DashboardStatsVO stats = new DashboardStatsVO();
        
        // 今日订单数
        Integer todayOrders = orderMapper.countTodayOrders(merchantId);
        stats.setTodayOrders(todayOrders != null ? todayOrders : 0);
        
        // 昨日订单数
        Integer yesterdayOrders = orderMapper.countYesterdayOrders(merchantId);
        
        // 计算增长率
        if (yesterdayOrders != null && yesterdayOrders > 0) {
            double growth = ((todayOrders - yesterdayOrders) * 100.0) / yesterdayOrders;
            stats.setTodayOrdersGrowth(Math.round(growth * 10) / 10.0);
        } else {
            stats.setTodayOrdersGrowth(0.0);
        }
        
        // 今日销售额
        Double todaySales = orderMapper.sumTodaySales(merchantId);
        stats.setTodaySales(todaySales != null ? todaySales : 0.0);
        
        // 昨日销售额
        Double yesterdaySales = orderMapper.sumYesterdaySales(merchantId);
        
        // 计算销售额增长率
        if (yesterdaySales != null && yesterdaySales > 0) {
            double growth = ((todaySales - yesterdaySales) * 100.0) / yesterdaySales;
            stats.setTodaySalesGrowth(Math.round(growth * 10) / 10.0);
        } else {
            stats.setTodaySalesGrowth(0.0);
        }
        
        // 待处理订单（待发货）
        Integer pendingOrders = orderMapper.countPendingOrders(merchantId);
        stats.setPendingOrders(pendingOrders != null ? pendingOrders : 0);
        
        // 商品总数
        Integer totalProducts = productMapper.countMerchantProducts(merchantId, null, null, null, null, null);
        stats.setTotalProducts(totalProducts != null ? totalProducts : 0);
        
        // 在售商品数
        Integer onSaleProducts = productMapper.countMerchantProducts(merchantId, null, null, 1, null, null);
        stats.setOnSaleProducts(onSaleProducts != null ? onSaleProducts : 0);
        
        return stats;
    }
    
    @Override
    public DashboardTodosVO getMerchantDashboardTodos(Long merchantId) {
        DashboardTodosVO todos = new DashboardTodosVO();
        
        // 待发货订单
        Integer pendingShip = orderMapper.countPendingOrders(merchantId);
        todos.setPendingShipOrders(pendingShip != null ? pendingShip : 0);
        
        // 库存不足商品（库存 < 10）
        Integer lowStock = productMapper.countLowStockProducts(merchantId);
        todos.setLowStockProducts(lowStock != null ? lowStock : 0);
        
        // 待审核商品
        Integer pendingAudit = productMapper.countMerchantProducts(merchantId, null, null, 2, null, null);
        todos.setPendingAuditProducts(pendingAudit != null ? pendingAudit : 0);
        
        return todos;
    }
    
    @Override
    public List<RecentOrderVO> getRecentOrders(Long merchantId, Integer limit) {
        List<Orders> orders = orderMapper.selectRecentOrders(merchantId, limit);
        
        List<RecentOrderVO> result = new ArrayList<>();
        for (Orders order : orders) {
            RecentOrderVO vo = new RecentOrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setUserName(order.getUserName());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setStatus(order.getOrderStatus());
            vo.setCreateTime(order.getCreateTime());
            result.add(vo);
        }
        
        return result;
    }
}