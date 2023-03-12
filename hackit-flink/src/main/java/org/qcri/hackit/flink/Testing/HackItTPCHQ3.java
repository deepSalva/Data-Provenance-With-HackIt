package org.qcri.hackit.flink.Testing;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.core.fs.FileSystem;
import org.qcri.hackit.core.tuple.HackItTuple;
import org.qcri.hackit.flink.HackItDataset.HackItDataset;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HackItTPCHQ3 {

    public static void runHackITTPCHQ3(String[] args, ExecutionEnvironment env, ParameterTool params, int iteration) throws ParseException {
        final String outputPath = "/Users/joschavonhein/Workspace/bdapro-ss20-dataprov/hackit-src/hackit-flink/Output/tpch_hackit";

        // get input data
        DataSet<HelperClasses.Lineitem> lineitems = HelperClasses.getLineitemDataSet(env, params.get("lineitem", "/Users/joschavonhein/Data/lineitem.tbl"));
        DataSet<HelperClasses.Customer> customers = HelperClasses.getCustomerDataSet(env, params.get("customer", "/Users/joschavonhein/Data/customer.tbl"));
        DataSet<HelperClasses.Order> orders = HelperClasses.getOrdersDataSet(env, params.get("orders", "/Users/joschavonhein/Data/orders.tbl"));

        // Filter market segment "AUTOMOBILE"
        HackItDataset<HelperClasses.Customer> hackItCustomer = HackItDataset.fromDataSet(customers).filter(
                new FilterFunction<HelperClasses.Customer>() {
                    @Override
                    public boolean filter(HelperClasses.Customer c) throws Exception {
                        return c.getMktsegment().equals("AUTOMOBILE");
                    }
                });

        // Filter all Order with o_orderdate < 12.03.1995
        HackItDataset<HelperClasses.Order> hackItOrder = HackItDataset.fromDataSet(orders).filter(
                new FilterFunction<HelperClasses.Order>() {
                    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    private final Date date = format.parse("1995-03-12");

                    @Override
                    public boolean filter(HelperClasses.Order o) throws Exception {
                        return format.parse(o.getOrderdate()).before(date);
                    }
                });

        // Filter all Lineitems with l_shipdate > 12.03.1995
        HackItDataset<HelperClasses.Lineitem> hackItLineitem = HackItDataset.fromDataSet(lineitems).filter(
                new FilterFunction<HelperClasses.Lineitem>() {
                    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    private final Date date = format.parse("1995-03-12");

                    @Override
                    public boolean filter(HelperClasses.Lineitem l) throws Exception {
                        return format.parse(l.getShipdate()).after(date);
                    }
                });

        // Manually unwrap the HackIt Dataset
        DataSet<HelperClasses.Order> orderDataSet = hackItOrder.getDataSet().map(new MapFunction<HackItTuple<Long, HelperClasses.Order>, HelperClasses.Order>() {
            @Override
            public HelperClasses.Order map(HackItTuple<Long, HelperClasses.Order> value) throws Exception {
                return value.getValue();
            }
        });
        DataSet<HelperClasses.Customer> customerDataSet = hackItCustomer.getDataSet().map(new MapFunction<HackItTuple<Long, HelperClasses.Customer>, HelperClasses.Customer>() {
            @Override
            public HelperClasses.Customer map(HackItTuple<Long, HelperClasses.Customer> value) throws Exception {
                return value.getValue();
            }
        });

        // Join customers with orders and package them into a ShippingPriorityItem
        // DataSet<HelperClasses.ShippingPriorityItem> shippingPriorityItemDataSet =
        DataSet<HelperClasses.ShippingPriorityItem> shippingPriorityItemDataSet = customerDataSet.join(orderDataSet).where(0).equalTo(1)
                .with(new customJoinFunction());

        DataSet<HelperClasses.Lineitem> lineitemDataSet = hackItLineitem.getDataSet().map(new MapFunction<HackItTuple<Long, HelperClasses.Lineitem>, HelperClasses.Lineitem>() {
            @Override
            public HelperClasses.Lineitem map(HackItTuple<Long, HelperClasses.Lineitem> value) throws Exception {
                return value.getValue();
            }
        });

        // Join the last join result with lineitems
        AggregateOperator secondJoin = shippingPriorityItemDataSet.join(lineitemDataSet)
                .where(0).equalTo(0)
                .with(
                        new JoinFunction<HelperClasses.ShippingPriorityItem, HelperClasses.Lineitem, HelperClasses.ShippingPriorityItem>() {
                            @Override
                            public HelperClasses.ShippingPriorityItem join(HelperClasses.ShippingPriorityItem i, HelperClasses.Lineitem l) {
                                i.setRevenue(l.getExtendedprice() * (1 - l.getDiscount()));
                                return i;
                            }
                        })
                // Group by l_orderkey, o_orderdate and o_shippriority and compute revenue sum
                .groupBy(0, 2, 3)
                .aggregate(Aggregations.SUM, 1);

        // emit result
        secondJoin.writeAsCsv(outputPath+"_"+iteration+".csv", "\n", "|", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        try {
            env.execute("TPCH Query 3 HACK IT");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static class customJoinFunction implements JoinFunction<HelperClasses.Customer, HelperClasses.Order, HelperClasses.ShippingPriorityItem> {
        @Override
        public HelperClasses.ShippingPriorityItem join(HelperClasses.Customer c, HelperClasses.Order o) throws Exception {
            return new HelperClasses.ShippingPriorityItem(o.getOrderKey(), 0.0, o.getOrderdate(),
                    o.getShippriority());
        }
    }

}
