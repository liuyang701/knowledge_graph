
import java.util.HashMap;
import java.util.Map;

import com.sciencescape.ds.utils.configuration.ConfigurationProvider;
import com.sciencescape.ds.utils.configuration.ConfigurationProviderException;

/**
 * Class to determine which configuration path to read, calls Akshay's
 * configuration utility after determining the local and global configuration
 * paths
 *
 * @author yh
 *
 */
public class ReadConfiguration {

    /**
     * reads SRCDIR and DEPLOY_ENV environment variables, constructs a
     * configuration utility object that reads configurations from the proper
     * local and global config files.
     *
     * @return ConfigurationProvider
     * @throws ConfigurationProviderException
     */
    public static ConfigurationProvider getConfigurationProvider() throws ConfigurationProviderException {

        String SRCDIR = "/Users/liuyang/Documents/git";
        String DEPLOY_ENV = "development";
//        String SRCDIR = System.getenv("SRCDIR");
//        String DEPLOY_ENV = System.getenv("DEPLOY_ENV");
        if (DEPLOY_ENV == null) {
            throw new RuntimeException("DEPLOY_ENV not set!");
        }
        if (SRCDIR == null) {
            throw new RuntimeException("SRCDIR not set!");
        }


        Map<String, String> confNames = new HashMap<String, String>();

        confNames.put("development", "development.cfg");
        confNames.put("staging", "staging.cfg");
        confNames.put("production", "production.cfg");
        confNames.put("test", "test.cfg");

        String configFileName = confNames.get(DEPLOY_ENV);
        if (configFileName == null){
            configFileName = DEPLOY_ENV + ".cfg";
        }
        String pathGlobalConfig = SRCDIR + "/ds-utils/configs/" + configFileName;

        String pathLocalConfig = SRCDIR + "/aggregation-engine/config/" + configFileName;

        ConfigurationProvider cp = new ConfigurationProvider(pathGlobalConfig, pathLocalConfig);

        return cp;

    }

}
