import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;

import com.sciencescape.ds.utils.configuration.ConfigurationProvider;
import com.sciencescape.ds.utils.configuration.ConfigurationProviderException;

/**
 * Class to generate hbase/mapreduce objects properly configured to our needs
 *
 * @author yh
 *
 */
public class StaticHBaseMapreduceObjects {

    /**
     * generates an hbase configuration object
     *
     * @return Configuration object
     * @throws ConfigurationProviderException
     */
    public static Configuration getAggregationEngineConfiguration() throws ConfigurationProviderException {
        Configuration config = HBaseConfiguration.create();

        ConfigurationProvider cp = ReadConfiguration.getConfigurationProvider();

        String quorum = cp.getString("HBASE", "zkQuorum");
        String port = cp.getString("HBASE", "zkPort");
        String master = cp.getString("HBASE", "hbaseMaster");
        String distMode = cp.getString("HBASE", "hbaseDistributedMode");

        // specify ZK setup
        config.set(InternalConstants.ZK_QUORUM, quorum);
        config.set(InternalConstants.ZK_PORT, port);

        // specify HBase setup
        config.set(InternalConstants.MASTER, master);
        config.set(InternalConstants.DISTRIBUTED_MODE, distMode);
        // config.setInt("hbase.client.keyvalue.maxsize", 500000000);

        return config;
    }

    /**
     * generates an aggregation engine map reduce job
     *
     * @param config
     * @param name
     * @return Job object
     * @throws IOException
     */
    public static Job getAggregationMapReduceJob(Configuration config, String name) throws IOException {

        @SuppressWarnings("deprecation")
        Job job = new Job(config, name);

        // other consistent configuration parameters here

        return job;
    }

    /**
     * returns hbase scanning object tuned for map reduce
     *
     * @return Scan object
     */
    public static Scan getHBaseMapReduceScan() {
        Scan scan = new Scan();
        scan.setCaching(500); // 1 is the default in Scan, which will be bad for
        // MapReduce jobs
        scan.setCacheBlocks(false); // don't set to true for MR jobs

        return scan;
    }

    /**
     * returns scanning object tuned for full table scan
     *
     * @return Scan object
     */
    public static Scan getLinearScan(String column, String prefix) {
        Scan scan = new Scan(Bytes.toBytes(prefix));
        scan.addFamily(Bytes.toBytes(column));
        return scan;
    }

    /**
     * returns hash map from integer to float of entity eigenfactors and
     * populates it
     *
     * @throws IOException
     * @throws ConfigurationProviderException
     */
    public static Int2FloatOpenHashMap getEntityEf(String tableName, String familyName, String valueName,
                                                   String prefix, String suffix, String keyDelimiter, int tokenCountWanted) throws IOException,
            ConfigurationProviderException {
        // initialize and set up reading scanners
        Int2FloatOpenHashMap mapToReturn = new Int2FloatOpenHashMap();

        Configuration config = StaticHBaseMapreduceObjects.getAggregationEngineConfiguration();

        HTable table = new HTable(config, tableName);

        Filter f = new PrefixFilter(Bytes.toBytes(prefix));

        Scan s = getLinearScan(familyName, prefix);

        s.setFilter(f);

        ResultScanner rs = table.getScanner(s);

        // read entire table, add to values to the hashmap
        for (Result r : rs) {
            String key = Bytes.toString(r.getRow());

            if (key.endsWith(suffix)) {

                String[] keyTokens = key.split("\\" + keyDelimiter);
                int entityId = Integer.parseInt(keyTokens[tokenCountWanted]);

                float ef = Bytes.toFloat(r.getValue(Bytes.toBytes(familyName), Bytes.toBytes(valueName)));
                mapToReturn.put(entityId, ef);

            }
        }
        rs.close();

        table.close();

        return mapToReturn;
    }

}

