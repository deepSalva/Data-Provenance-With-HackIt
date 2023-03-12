package org.qcri.hackit.flink.Testing;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;

public class HelperClasses {
    // *************************************************************************
    //     DATA TYPES
    // *************************************************************************

    /**
     * Lineitem.
     */
    public static class Lineitem extends Tuple4<Long, Double, Double, String> {

        public Long getOrderkey() {
            return this.f0;
        }

        public Double getDiscount() {
            return this.f2;
        }

        public Double getExtendedprice() {
            return this.f1;
        }

        public String getShipdate() {
            return this.f3;
        }
    }

    /**
     * Customer.
     */
    public static class Customer extends Tuple2<Long, String> {

        public Long getCustKey() {
            return this.f0;
        }

        public String getMktsegment() {
            return this.f1;
        }
    }

    /**
     * Order.
     */
    public static class Order extends Tuple4<Long, Long, String, Long> {

        public Long getOrderKey() {
            return this.f0;
        }

        public Long getCustKey() {
            return this.f1;
        }

        public String getOrderdate() {
            return this.f2;
        }

        public Long getShippriority() {
            return this.f3;
        }
    }

    /**
     * ShippingPriorityItem.
     */
    public static class ShippingPriorityItem extends Tuple4<Long, Double, String, Long> {

        public ShippingPriorityItem() {}

        public ShippingPriorityItem(Long orderkey, Double revenue,
                                    String orderdate, Long shippriority) {
            this.f0 = orderkey;
            this.f1 = revenue;
            this.f2 = orderdate;
            this.f3 = shippriority;
        }

        public Long getOrderkey() {
            return this.f0;
        }

        public void setOrderkey(Long orderkey) {
            this.f0 = orderkey;
        }

        public Double getRevenue() {
            return this.f1;
        }

        public void setRevenue(Double revenue) {
            this.f1 = revenue;
        }

        public String getOrderdate() {
            return this.f2;
        }

        public Long getShippriority() {
            return this.f3;
        }
    }

    // *************************************************************************
    //     UTIL METHODS
    // *************************************************************************

    static DataSet<Lineitem> getLineitemDataSet(ExecutionEnvironment env, String lineitemPath) {
        return env.readCsvFile(lineitemPath)
                .fieldDelimiter("|")
                .includeFields("1000011000100000")
                .tupleType(Lineitem.class);
    }

    static DataSet<Customer> getCustomerDataSet(ExecutionEnvironment env, String customerPath) {
        return env.readCsvFile(customerPath)
                .fieldDelimiter("|")
                .includeFields("10000010")
                .tupleType(Customer.class);
    }

    static DataSet<Order> getOrdersDataSet(ExecutionEnvironment env, String ordersPath) {
        return env.readCsvFile(ordersPath)
                .fieldDelimiter("|")
                .includeFields("110010010")
                .tupleType(Order.class);
    }
}
