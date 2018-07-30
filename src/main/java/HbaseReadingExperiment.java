import org.apache.commons.logging.Log;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class HbaseReadingExperiment {

    public static void main(String[] args) throws ClassNotFoundException,
            IOException, InterruptedException {

        Configuration config = HBaseConfiguration.create();
//        Log log = LogFactory.getLog("logger");
        Logger.getRootLogger().setLevel(Level.TRACE);

        // specify ZK setup
        config.set("hbase.zookeeper.quorum", "35.185.240.40,35.227.191.20,35.197.105.26");
        config.setInt("hbase.zookeeper.property.clientPort", 2181);

        // specify HBase setup
        config.set("hbase.master", "35.185.240.40:60000");
        config.set("hbase.cluster.distributed", "true");
        config.setInt("hbase.client.keyvalue.maxsize", 500000000);



        config.setInt("timeout", 5000);
        config.setInt("hbase.client.retries.number", 1);
        config.setInt("ipc.client.connect.max.retries", 1);
        config.setInt("rpc.client.connect.max.retries", 1);


        HTable table = new HTable(config, "paper_june_2011");
        Get g = new Get(Bytes.toBytes("100"));
        Result value = table.get(g);

        Map<byte[], byte[]> family = value.getFamilyMap(Bytes
                .toBytes("CO"));



        for (Map.Entry<byte[], byte[]> entry : family.entrySet()) {
            byte[] qualifierString = entry.getKey();

            Integer coID = Integer.parseInt(Bytes.toString(qualifierString).split(
                    "_")[1]);

            System.out.println(coID);
        }


        table.close();
    }
}
