# 订单拆单功能实现说明

## 功能概述
实现了按商户拆分订单的功能，解决多商户商品在同一订单中无法独立发货的问题。

## 实现原理
当用户购物车中包含多个商户的商品时，系统会自动按商户拆分成多个独立订单。

### 示例
**购物车：**
- 商户A的苹果 x2
- 商户A的香蕉 x1
- 商户B的橙子 x3

**生成订单：**
- 订单1（商户A）：苹果x2 + 香蕉x1
- 订单2（商户B）：橙子x3

## 修改内容

### 1. 数据库变更
**文件：** `src/main/resources/sql/order_split_migration.sql`

**变更内容：**
- 为 `orders` 表添加 `merchant_id` 字段
- 为 `orders` 表添加 `merchant_name` 字段（商户名称快照）
- 添加索引 `idx_merchant_id`
- 为历史订单迁移数据

**执行方式：**
```sql
-- 在数据库中执行迁移脚本
source src/main/resources/sql/order_split_migration.sql;
```

### 2. 实体类变更
**修改文件：**
- `Entity/Orders.java` - 添加 merchantId 和 merchantName 字段
- `VO/OrderVO.java` - 添加 merchantId 和 merchantName 字段
- `VO/MerchantOrderVO.java` - 添加 merchantId 和 merchantName 字段

### 3. Mapper变更
**修改文件：**
- `mapper/OrdersMapper.xml` - insert语句添加merchant_id和merchant_name
- `mapper/OrderMapper.xml` - 简化商户订单查询SQL（直接按merchant_id查询，不再需要JOIN）

**优化效果：**
- 查询速度提升（减少JOIN操作）
- SQL逻辑更清晰

### 4. Service层变更
**修改文件：** `Service/impl/OrderServiceImpl.java`

**核心改动：**
1. `createOrder()` 方法返回值从 `String` 改为 `List<String>`
2. 新增 `createSingleMerchantOrder()` 私有方法
3. 实现按商户分组逻辑
4. 简化 `getMerchantOrders()` 和 `getMerchantOrderDetail()` 方法

**拆单逻辑：**
```java
// 1. 获取购物车商品
// 2. 按商户ID分组
Map<Long, List<CartVO>> merchantCartMap = new HashMap<>();
for (CartVO cart : cartList) {
    Product product = productMapper.selectById(cart.getProductId());
    merchantCartMap.computeIfAbsent(product.getMerchantId(), k -> new ArrayList<>())
                   .add(cart);
}

// 3. 为每个商户创建独立订单
List<String> orderNos = new ArrayList<>();
for (Map.Entry<Long, List<CartVO>> entry : merchantCartMap.entrySet()) {
    String orderNo = createSingleMerchantOrder(...);
    orderNos.add(orderNo);
}
```

### 5. Controller层变更
**修改文件：** `Controller/OrderController.java`

**返回格式变更：**
```json
// 原返回格式
{
  "code": 200,
  "message": "订单创建成功",
  "data": "202604091234560001"
}

// 新返回格式
{
  "code": 200,
  "message": "订单创建成功，共创建2个订单",
  "data": {
    "orderNos": ["202604091234560001", "202604091234560002"],
    "count": 2
  }
}
```

### 6. 前端变更（可选）
**修改文件：** `backend/src/api/order.ts`

**变更内容：**
- MerchantOrder接口添加 merchantId 和 merchantName 字段

## 使用说明

### 1. 数据库迁移
```bash
# 连接到MySQL数据库
mysql -u root -p your_database

# 执行迁移脚本
source FreshFoodMallBackend/src/main/resources/sql/order_split_migration.sql;
```

### 2. 重启后端服务
```bash
cd FreshFoodMallBackend
mvn clean package
java -jar target/your-app.jar
```

### 3. 测试
**测试场景1：单商户下单**
- 购物车只有一个商户的商品
- 预期：创建1个订单

**测试场景2：多商户下单**
- 购物车有2个商户的商品
- 预期：创建2个订单，每个订单只包含对应商户的商品

**测试场景3：商户查看订单**
- 商户A登录后台
- 预期：只能看到merchant_id=A的订单

## 兼容性说明

### 历史订单处理
迁移脚本会自动为历史订单设置merchant_id：
- 如果订单只包含一个商户的商品：设置为该商户ID
- 如果订单包含多个商户的商品：设置为第一个商户ID（这种情况在拆单后不会再出现）

### API兼容性
- 前端调用 `/order/create` 接口需要适配新的返回格式
- 订单列表接口返回数据增加了 merchantId 和 merchantName 字段

## 优势

1. **权限隔离**：商户只能看到自己的订单，无法看到其他商户的商品
2. **独立发货**：每个订单可以独立发货，互不影响
3. **物流追踪**：每个订单有独立的物流信息
4. **查询性能**：商户订单查询不再需要JOIN，性能提升
5. **逻辑清晰**：订单归属明确，便于管理和统计

## 注意事项

1. **商户名称快照**：merchant_name字段存储的是下单时的商户名称，即使商户后续改名也不影响历史订单显示
2. **支付流程**：当前每个订单需要独立支付，如需合并支付需要额外开发
3. **订单号生成**：使用时间戳+随机数，确保在同一事务中生成的订单号不重复
4. **库存扣减**：每个订单独立扣减库存，逻辑与原来一致

## TODO（可选优化）

1. 集成merchant表查询真实商户名称（当前使用"商户{id}"作为占位符）
2. 实现合并支付功能
3. 订单列表前端按商户分组显示
4. 添加订单拆分日志记录

## 问题排查

### 问题1：订单创建失败
**检查：**
- 数据库是否执行了迁移脚本
- orders表是否有merchant_id和merchant_name字段

### 问题2：商户看不到订单
**检查：**
- 历史订单的merchant_id是否已设置
- 商户ID是否正确传递

### 问题3：订单金额不对
**检查：**
- 每个订单的金额是否只计算了该商户的商品
- 运费计算逻辑是否正确

## 联系方式
如有问题，请查看代码注释或联系开发团队。
