
import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import scala.Int;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;


public class ConceptGraph {

    public static void main(String[] args) throws ClassNotFoundException,
            IOException, InterruptedException {

        Configuration config = HBaseConfiguration.create();

        // specify ZK setup
        config.set("hbase.zookeeper.quorum", "10.138.0.4:2181,10.138.0.5:2181,10.138.0.6:2181");
        config.set("hbase.zookeeper.property.clientPort", "2181");

        // specify HBase setup
        config.set("hbase.master", "10.138.0.4:60000");
        config.set("hbase.cluster.distributed", "true");
        config.setInt("hbase.client.keyvalue.maxsize", 500000000);

//        // specify ZK setup
//        config.set("hbase.zookeeper.quorum", "35.185.240.40,35.227.191.20,35.197.105.26");
//        config.setInt("hbase.zookeeper.property.clientPort", 2181);
//
//        // specify HBase setup
//        config.set("hbase.master", "35.185.240.40:60000");
//        config.set("hbase.cluster.distributed", "true");
//        config.setInt("hbase.client.keyvalue.maxsize", 500000000);

//        @SuppressWarnings("deprecation")
        // define mapred job and table scanner
        Job job = Job.getInstance(config);
        job.setJarByClass(ConceptGraph.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyTableReducer.class);

        Scan scan = new Scan();
        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, Bytes.toBytes("paper_june_2011"));

        scan.setCaching(500); // 1 is the default in Scan, which will be bad for
        // MapReduce jobs
        scan.setCacheBlocks(false); // don't set to true for MR jobs
        List<Scan> scans = new ArrayList<Scan>();
        scans.add(scan);

        // input output type parameters
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        TableMapReduceUtil.initTableMapperJob(scans, // Scan instance to control CF and attribute selection
                MyMapper.class, // mapper class
                Text.class, // mapper output key, redundant ?
                IntWritable.class, // mapper output value, redundant?
                job);

        job.setNumReduceTasks(6); // at least one, adjust as required
        FileOutputFormat.setOutputPath(job, new Path("co_graph")); // output path into hdsf, adjust directories needed

        boolean b = job.waitForCompletion(true);

        // run job
        System.exit(b ? 0 : 1);

    }

    public static class MyMapper extends TableMapper<Text, IntWritable> {

        private Text key = new Text();
        private IntWritable count = new IntWritable(1);

        public void map(ImmutableBytesWritable row, Result value,
                        Context context) throws IOException, InterruptedException {
            String pmid = Bytes.toString(row.get());

            byte[] year = value.getValue(Bytes.toBytes("D"), Bytes.toBytes("y"));
            byte[] month = value.getValue(Bytes.toBytes("D"), Bytes.toBytes("m"));

            // only process papers that are published before the training set (june_2011)

            if (year != null && month != null && Bytes.toInt(year)>= 2011 && Bytes.toInt(month)>=6) {
                return;
            }

            Map<byte[], byte[]> familyCO = value.getFamilyMap(Bytes.toBytes("CO"));

            List<Integer> co_ids = new ArrayList<>();

            for (Map.Entry<byte[], byte[]> entry : familyCO.entrySet()) {
                byte[] qualifierString = entry.getKey();

                Integer coID = Integer.parseInt(Bytes.toString(qualifierString).split(
                        "_")[1]);
                co_ids.add(coID);
            }
//            System.out.println(pmid+" total co " + co_ids.size());

            if (co_ids.size() >1) {
                for (int i = 0; i< co_ids.size(); i++) {
                    for (int j = i+1; j<co_ids.size(); j++) {
                        Integer coID1 = co_ids.get(i);
                        Integer coID2 = co_ids.get(j);
                        key = new Text(String.format("%s|%s",coID1,coID2));
                        try{

                            context.write(key, count);
                        }catch (final Exception ex){
                            System.out.println(ex);
                        }

                    }
                }
            }

        }
    }

    /*
     * public static class MyTableReducer extends TableReducer<IntWritable,
     * IntWritable, ImmutableBytesWritable> {
     *
     * public void reduce(Text key, Iterable<IntWritable> values, Context
     * context) throws IOException, InterruptedException { int i = 0; for
     * (IntWritable val : values) { i += val.get(); } Put put = new
     * Put(Bytes.toBytes(key.toString())); put.add(Bytes.toBytes("cf"),
     * Bytes.toBytes("count"), Bytes.toBytes(i));
     *
     * context.write(null, put); } }
     */

    public static class MyTableReducer extends
            Reducer<Text, IntWritable, Text, IntWritable> {

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context) throws IOException, InterruptedException {

            int sum = 0;
            for (IntWritable val : values) {

                sum += val.get();
            }

            IntWritable sum_write = new IntWritable(sum);
            context.write(key, sum_write);
        }
    }

}
