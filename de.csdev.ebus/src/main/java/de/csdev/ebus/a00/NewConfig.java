package de.csdev.ebus.a00;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import de.csdev.ebus.cfg.datatypes.EBusTypes;
import de.csdev.ebus.cfg.json.v1.EBusConfigurationTelegram;
import de.csdev.ebus.cfg.json.v1.OH1ConfigurationReader;
import de.csdev.ebus.cfg.json.v2.OH2ConfigurationReader;
import de.csdev.ebus.command.EBusCommand;
import de.csdev.ebus.command.EBusCommandRegistry;
import de.csdev.ebus.core.EBusConnectorEventListener;
import de.csdev.ebus.core.EBusController;
import de.csdev.ebus.core.EBusDataException;
import de.csdev.ebus.core.connection.EBusEmulatorConnection;
import de.csdev.ebus.core.connection.IEBusConnection;
import de.csdev.ebus.utils.EBusUtils;

public class NewConfig {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            new NewConfig().run();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private EBusTypes registry;

    public List<EBusConfigurationTelegram> loadConfiguration() throws IOException {

        ClassLoader classLoader = this.getClass().getClassLoader();
        // URL resource = classLoader.getResource("other.json");
        URL resource = classLoader.getResource("common-configuration.json");

        final ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
        final InputStream inputStream = resource.openConnection().getInputStream();

        final List<EBusConfigurationTelegram> loadedTelegramRegistry = mapper.readValue(inputStream,
                new TypeReference<List<EBusConfigurationTelegram>>() {
                });

        return loadedTelegramRegistry;
    }

    public void run() throws InterruptedException {

        IEBusConnection connection = new EBusEmulatorConnection(new File("src/main/resources/test/replay.txt"));
        EBusController controller = new EBusController(connection);

        registry = new EBusTypes();

        final EBusCommandRegistry tregistry = new EBusCommandRegistry();

        OH1ConfigurationReader ohreader = new OH1ConfigurationReader();
        OH2ConfigurationReader oh2reader = new OH2ConfigurationReader(tregistry, registry);

        try {
            // oh2reader.aaaa();
            // ohreader.b(cfgs, registry)

            ClassLoader classLoader = this.getClass().getClassLoader();
            URL url = classLoader.getResource("common-configuration.json");

            ohreader.setEBusTypes(registry);
            List<EBusCommand> loadConfiguration = ohreader.loadConfiguration(url);
            tregistry.addTelegramConfigurationList(loadConfiguration);

            // List<EBusCommand> list = ohreader.b(loadConfiguration(), registry);
            // tregistry.addTelegramConfigurationList(list);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        controller.addEBusEventListener(new EBusConnectorEventListener() {

            @Override
            public void onTelegramReceived(byte[] receivedData, Integer sendQueueId) {
                // TODO Auto-generated method stub
                System.out.println("NewConfig.run().new EBusConnectorEventListener() {...}.onTelegramReceived()"
                        + EBusUtils.toHexDumpString(receivedData));
                // List<EBusCommand> find = tregistry.find(receivedData);
                // System.out
                // .println("NewConfig.run().new EBusConnectorEventListener() {...}.onTelegramReceived()" + find);
                //
                List<EBusCommand> find2 = tregistry.find(receivedData);
                // System.out
                // .println("NewConfig.run().new EBusConnectorEventListener() {...}.onTelegramReceived()" + find2);

                for (EBusCommand eBusCommand : find2) {
                    Map<String, Object> encode = eBusCommand.encode(receivedData);
                    System.out.println(encode);
                }
            }

            @Override
            public void onTelegramException(EBusDataException exception, Integer sendQueueId) {
                System.out.println(
                        "NewConfig.run().new EBusConnectorEventListener() {...}.onTelegramException()" + exception);
                // TODO Auto-generated method stub

            }
        });

        controller.start();
        // main thread wait
        controller.join();
    }

}
