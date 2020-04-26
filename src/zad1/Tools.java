/**
 * @author Zaborowski Mateusz S19101
 */

package zad1;


import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class Tools {
    public static Options createOptionsFromYaml(String fileName) throws FileNotFoundException {
        Map<String, Object> map = (new Yaml()).load(new FileInputStream(fileName));
        return new Options(
                map.get("host").toString(),
                Integer.parseInt(map.get("port").toString()),
                Boolean.parseBoolean(map.get("concurMode").toString()),
                Boolean.parseBoolean(map.get("showSendRes").toString()),
                (Map<String, List<String>>)map.get("clientsMap")
        );
    }
}
