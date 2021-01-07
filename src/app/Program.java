package app;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import db.DB;
import entities.Order;
import entities.OrderStatus;
import entities.Product;

public class Program {

    public static void main(String[] args) throws SQLException {

        Connection conn = DB.getConnection();

        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM tb_order " +
                "INNER JOIN tb_order_product ON tb_order.id = tb_order_product.order_id " +
                "INNER JOIN tb_product ON tb_product.id = tb_order_product.product_id");

        Map<Long, Order> map = new HashMap<>();
        Map<Long, Product> prods = new HashMap<>();
        while (rs.next()) {
            Long orderId = rs.getLong("order_id");
            if (map.get(orderId) == null) {
                Order order = instantiateOrder(rs);
                map.put(orderId, order);
            }

            Long productId = rs.getLong("product_id");
            if(prods.get(productId) == null) {
                Product product = instantiateProduct(rs);
                prods.put(productId, product);
            }

            map.get(orderId).getProducts().add(prods.get(productId));
        }

        for (Long orderId : map.keySet()){
            System.out.println(map.get(orderId));
            for (Product product : map.get(orderId).getProducts()){
                System.out.println(product);
            }
            System.out.println();
        }
    }

    private static Order instantiateOrder(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getLong("order_id"));
        order.setLatitude(resultSet.getDouble("latitude"));
        order.setLongitude(resultSet.getDouble("longitude"));
        order.setMoment(resultSet.getTimestamp("moment").toInstant());
        order.setStatus(OrderStatus.values()[resultSet.getInt("status")]);
        return order;
    }

    private static Product instantiateProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getLong("product_id"));
        product.setDescription(resultSet.getString("description"));
        product.setName(resultSet.getString("name"));
        product.setImageUri(resultSet.getString("image_uri"));
        product.setPrice(resultSet.getDouble("price"));
        return product;
    }
}
