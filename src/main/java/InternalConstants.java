
import org.apache.hadoop.hbase.util.Bytes;

/**
 * internal global constants file
 *
 * @author yh
 */
public class InternalConstants {

    // hbase external zoo keeper and hbase configurations names
    public static final String ZK_QUORUM = "hbase.zookeeper.quorum";
    public static final String ZK_PORT = "hbase.zookeeper.property.clientPort";
    /**
     * TODO: This constant should really be HBASE_MASTER
     */
    public static final String MASTER = "hbase.master";
    public static final String DISTRIBUTED_MODE = "hbase.cluster.distributed";

    // special indices
    public static final int AUTHOR_LAST_INDEX_INT = Integer.MAX_VALUE;
    public static final int AUTHOR_ALL_INDEX_INT = Integer.MIN_VALUE;
    public static final int AUTHOR_FIRST_INDEX_INT = 0;

    // weights
    public static final double TIME_DECAY_LAMBDA = Math.log(2) / 24.0; // 24 months
    public static final double TIME_DECAY_LAMBDA_CONCEPT = Math.log(2) / 120.0; // 10 years
    public static final double TIME_DECAY_LAMBDA_JOURNAL = Math.log(2) / 60.0; // 5 years

    public static final float AUTHOR_INDEX_WEIGHT_LAST = 2f;
    public static final float AUTHOR_INDEX_WEIGHT_FIRST = 1.5f;
    public static final float AUTHOR_INDEX_WEIGHT_OTHER = 1f;

    // list lengths
    public static final int MENTION_LENGTH = 50;
    public static final int REC_LENGTH = 25;

    // special characters
    public static final String AUTHOR_INDEX_LAST_SPECIAL_CHAR = "L";
    public static final String AUTHOR_INDEX_ALL_SPECIAL_CHAR = "X";

    // hbase special column families/qualifiers from denorm paper table
    public static final byte[] DATE_COLUMN_FAMILY = Bytes.toBytes("D");
    public static final byte[] AUTHOR_COLUMN_FAMILY = Bytes.toBytes("A");
    public static final byte[] INSTITUTION_COLUMN_FAMILY = Bytes.toBytes("I");
    public static final byte[] EF_COLUMN_FAMILY = Bytes.toBytes("EF");
    public static final byte[] FIELD_COLUMN_FAMILY = Bytes.toBytes("CO");
    public static final byte[] JOURNAL_COLUMN_FAMILY = Bytes.toBytes("V");
    public static final byte[] CITATION_COLUMN_FAMILY = Bytes.toBytes("CI");
    public static final byte[] CITATION_COUNT_QUALIFIER = Bytes.toBytes("icount");
    public static final byte[] CONCEPT_COLUMN_FAMILY = Bytes.toBytes("CO");
    public static final byte[] IDENTIFIER_COLUMN_FAMILY = Bytes.toBytes("ID");


    // qualifier prefixes from denorm paper table
    public static final String AUTHOR_INDEX_QUALIFIER = "indx";
    public static final String GENERIC_IDENTIFIER_PREFIX = "id"; // for fields
    // and authors

    // qualifiers from denorm paper table
    public static final byte[] DATE_YEAR_QUALIFIER = Bytes.toBytes("y");
    public static final byte[] DATE_MONTH_QUALIFIER = Bytes.toBytes("m");
    public static final byte[] DATE_DAY_QUALIFIER = Bytes.toBytes("d");
    public static final byte[] EF_QUALIFIER = Bytes.toBytes("ef");
    public static final byte[] EF_DISPLAY_QUALIFIER = Bytes.toBytes("ef_display");
    public static final byte[] JOURNAL_EF_QUALIFIER = Bytes.toBytes("ef_med_n");
    //    public static final byte[] JOURNAL_EF_QUALIFIER = Bytes.toBytes("ef");
    public static final byte[] JOURNAL_QUALIFIER = Bytes.toBytes("id");
    public static final byte[] PMID_QUALIFIER = Bytes.toBytes("pmid");

    // generic for efp for authors
    public static final byte[] EFP_WRITE_COLUMN_FAMILY = Bytes.toBytes("A");
    public static final byte[] EFP_INST_WRITE_COLUMN_FAMILY = Bytes.toBytes("I");
    public static final byte[] EFP_VENUE_WRITE_COLUMN_FAMILY = Bytes.toBytes("V");

    // column family names for writing
    public static final byte[] LANDER_LIST_WRITE_FAMILY = Bytes.toBytes("RE");
    public static final byte[] STAT_WRITE_FAMILY = Bytes.toBytes("STATS");

    // column qualifier names for writing
    public static final byte[] TOP_PAPER_WRITE_QUALIFIER = Bytes.toBytes("top_pmid");
    public static final byte[] AFFILIATION_PRIMARY_QUALIFIER = Bytes.toBytes("aff_prime");
    public static final byte[] AFFILIATION_SECONDARY_QUALIFIER = Bytes.toBytes("aff_second");
    public static final String LANDER_LIST_QUALIFIER_ID_PREFIX = "id_";
    public static final String LANDER_LIST_QUALIFIER_SCORE_PREFIX = "s_";

    // key prefixes for writing
    public static final String AUTHOR_FIELD_RECS = "A$CO_r$";
    public static final String AUTHOR_INDEX_PAPEREF = "A$EF$";
    public static final String AUTHOR_TOPPAPER = "A$lander$";
    public static final String AUTHOR_JOURNAL_RECS = "A$V_r$";
    public static final String AUTHOR_AUTHOR_RECS = "A$A_r$";
    public static final String PAPER_AUTHOR_RECS = "P$A_r$";
    public static final String PAPER_JOURNAL_RECS = "P$V_r$";
    public static final String PAPER_CONCEPT_RECS = "P$CO_r$";
    public static final String CONCEPT_EF = "CO$EF$";
    public static final String INST_TOTAL_EF = "I$EF$";
    public static final String PAPER_EF = "P$EF$";

    // ef prediction stat qualifier prefix and suffixes
    public static final String[] STAT_SUFFIX = {"academic_lifespan", "total_paper_count", "total_field_count",
            "h_index", "g_index", "total_paper_citation", "max_paper_citation", "min_paper_citation",
            "mean_paper_citation", "median_paper_citation", "total_paper_ef", "max_paper_ef", "min_paper_ef",
            "mean_paper_ef", "median_paper_ef", "total_top10fields_ef", "max_top10fields_ef", "min_top10fields_ef",
            "mean_top10fields_ef", "median_top10fields_ef"};

    public static final String[] INST_VENUE_STAT_SUFFIX = {
            "total_paper_count",
            "total_field_count",
            "H_index",
            "G_index",
            "total_paper_ef",
            "max_paper_ef",
            "min_paper_ef",
            "mean_paper_ef",
            "median_paper_ef",
            "total_top10fields_ef",
            "max_top10fields_ef",
            "min_top10fields_ef",
            "mean_top10fields_ef",
            "median_top10fields_ef"};

    public static final String AUTHOR_COUNT_QUALIFIER = "author_count";
    public static final String MAIN_AUTHOR_PREFIX = "mainauths_";
    public static final String CO_AUTHOR_PREFIX = "coauths_";
    public static final String LAST_AUTHOR_PREFIX = "lastauth_";

    // paper entity recommendations column family
    public static final byte[] REC_COLUMN_FAMILY = Bytes.toBytes("RE");

    //paper paper rec constants
    public static final byte[] PAPER_PAPER_REC_COLUMN_FAMILY = Bytes.toBytes("A");
    public static final byte[] PAPER_PAPER_REC_FROM = Bytes.toBytes("f");
    public static final byte[] PAPER_PAPER_REC_TO = Bytes.toBytes("t");
    public static final byte[] PAPER_PAPER_REC_SCORE = Bytes.toBytes("s");

    //aggregation engine filtering for runtime boost
    public static final int EFP_MONTH_CUTOFF = 8;
    public static final int AUTHOR_LANDER_LIMIT = 100;
    public static final long YEAR_MILLISECONDS = 31104000000L;


    /**
     * Class for constants specific to paper-id to norm table.
     * Used by PaperNorm
     * @author akshay
     * @version 0.1
     */
    public static class P2NormTable {
        /**
         * !< 'all' column family
         */
        public static final byte[] ALL_CF = Bytes.toBytes("A");
        /**
         * !< 'all' column qualifier
         */
        public static final byte[] NORM_CQ = Bytes.toBytes("n");
        public static final byte[] WC_CQ = Bytes.toBytes("c");

    }

    /**
     * Class for constants specific to token to paper list table.
     * Used by PaperNorm
     * @author akshay
     * @version 0.1
     */
    public static class TokenToPaperListTable {
        /**
         * !< 'all' column family
         */
        public static final byte[] ALL_CF = Bytes.toBytes("A");
        /**
         * !< 'all' column qualifier
         */
        public static final byte[] ALL_CQ = Bytes.toBytes("a");
    }

    /**
     * Class for constants specific to word to document count/frequency table.
     * Used by InverseDocumentFrequency
     */
    public static class Word2DocCountTable {
        /**
         * !< 'all' column family
         */
        public static final byte[] ALL_CF = Bytes.toBytes("A");
        /**
         * !< 'all' column qualifier
         */
        public static final byte[] ALL_CQ = Bytes.toBytes("a");
    }


    public static final String PAPER_COUNT = "paper.count";
    public static final int MAX_PMID = 35000000;}
