
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


public class InternProject {

    public static void main(String[] args) throws ClassNotFoundException,
            IOException, InterruptedException {

        Configuration config = HBaseConfiguration.create();

//        // specify ZK setup
//        config.set("hbase.zookeeper.quorum", "10.138.0.4:2181,10.138.0.5:2181,10.138.0.6:2181");
//        config.set("hbase.zookeeper.property.clientPort", "2181");
//
//        // specify HBase setup
//        config.set("hbase.master", "10.138.0.4:60000");
//        config.set("hbase.cluster.distributed", "true");
//        config.setInt("hbase.client.keyvalue.maxsize", 500000000);

        // specify ZK setup
        config.set("hbase.zookeeper.quorum", "35.185.240.40,35.227.191.20,35.197.105.26");
        config.setInt("hbase.zookeeper.property.clientPort", 2181);

        // specify HBase setup
        config.set("hbase.master", "35.185.240.40:60000");
        config.set("hbase.cluster.distributed", "true");
        config.setInt("hbase.client.keyvalue.maxsize", 500000000);

//        @SuppressWarnings("deprecation")
        // define mapred job and table scanner
        Job job = Job.getInstance(config);
        job.setJarByClass(InternProject.class);
        job.setMapperClass(MyMapper.class);
        job.setReducerClass(MyTableReducer.class);

        Scan scan = new Scan();
        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, Bytes.toBytes("paper_dec_2014"));

        scan.setCaching(500); // 1 is the default in Scan, which will be bad for
        // MapReduce jobs
        scan.setCacheBlocks(false); // don't set to true for MR jobs
        List<Scan> scans = new ArrayList<Scan>();
        scans.add(scan);

        // input output type parameters
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FloatWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FloatWritable.class);

        TableMapReduceUtil.initTableMapperJob(scans, // Scan instance to control CF and attribute selection
                MyMapper.class, // mapper class
                Text.class, // mapper output key, redundant ?
                FloatWritable.class, // mapper output value, redundant?
                job);

        job.setNumReduceTasks(6); // at least one, adjust as required
        FileOutputFormat.setOutputPath(job, new Path("p2EF")); // output path into hdsf, adjust directories needed

        boolean b = job.waitForCompletion(true);

        // run job
        System.exit(b ? 0 : 1);

    }

    public static class MyMapper extends TableMapper<Text, FloatWritable> {

        private Text key = new Text();
        private FloatWritable eff = new FloatWritable(1);

        public void map(ImmutableBytesWritable row, Result value,
                        Context context) throws IOException, InterruptedException {
            String pmid = Bytes.toString(row.get());
            key = new Text(pmid);

            byte[] year = value.getValue(Bytes.toBytes("D"), Bytes.toBytes("y"));

            // only process papers that are published in 2011

            if (year != null && Bytes.toInt(year)==2011) {
                float ef = 0.f;
                byte[] efb = value.getValue(Bytes.toBytes("EF"), Bytes.toBytes("ef"));
                if (efb != null) {
                    ef = Bytes.toFloat(efb);
                }
                eff =new FloatWritable(ef);
                context.write(key, eff);

//

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
            Reducer<Text, FloatWritable, Text, FloatWritable> {

        public void reduce(Text key, Iterable<FloatWritable> values,
                           Context context) throws IOException, InterruptedException {

            float sum = 0;
            for (FloatWritable val : values) {

                sum += val.get();
            }

            FloatWritable sum_write = new FloatWritable(sum);
            context.write(key, sum_write);
        }
    }

}
