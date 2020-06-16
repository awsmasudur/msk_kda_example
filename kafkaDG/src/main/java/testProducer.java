import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class testProducer {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        long events = 1000;
        Integer interval = 100;
        String topics = "sourcetopic"; //change your topic name
        //Change the broker lists
        String brokerlist="z-2.meetupdemo1.gx0l3f.c3.kafka.ap-southeast-2.amazonaws.com:2181,z-1.meetupdemo1.gx0l3f.c3.kafka.ap-southeast-2.amazonaws.com:2181,z-3.meetupdemo1.gx0l3f.c3.kafka.ap-southeast-2.amazonaws.com:2181";

        BufferedReader br = new BufferedReader(new InputStreamReader(testProducer.class.getResourceAsStream("/sampleset.txt")));
        //setting up properties to be used to communicate to kafka

        Properties props = new Properties();
        //props.put("metadata.broker.list", args[0].toString());

        //change to your broker list

        props.put("metadata.broker.list", brokerlist);

        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        String line = "";
        String csvSplitBy = ",";
        Random rdm = new Random();
        ArrayList<List<String>> userdata = new ArrayList<>();

        try{
            //loading the data as Arrays of Lists to be able to pick different cells for generating random data from sample set
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(csvSplitBy);
                userdata.add(Arrays.asList(temp));
            }
            //getting the size of the Array to use as random seed so that we wont run into ArrayIndexOutOfBounds Exception
            int size = userdata.size();

            //creating the data and sending it
            for (long nEvents = 1; nEvents != events; nEvents++)
            {
                String msg = "{"+
                        "\"StockSymbol\":"+userdata.get(rdm.nextInt(size)).get(0).toString().trim()+","+
                        "\"StockNumber\":"+userdata.get(rdm.nextInt(size)).get(1).toString().trim()+","+
                        "\"Mp\":"+userdata.get(rdm.nextInt(size)).get(2).toString().trim()+","+
                        "\"Bp\":"+userdata.get(rdm.nextInt(size)).get(3).toString().trim()+","+
                        "\"Ap\":"+userdata.get(rdm.nextInt(size)).get(4).toString().trim()+","+
                        "\"BQ\":"+userdata.get(rdm.nextInt(size)).get(5).toString().trim()+","+
                        "\"Aq\":"+userdata.get(rdm.nextInt(size)).get(6).toString().trim()+","+
                        "\"Vol\":"+userdata.get(rdm.nextInt(size)).get(7).toString().trim()+","+
                        "\"rowId\":"+userdata.get(rdm.nextInt(size)).get(8).toString().trim()
                        +"}";

                System.out.println(msg);
                KeyedMessage<String, String> data = new KeyedMessage<String, String>(topics, new Date().getTime()+"", msg);
                producer.send(data);
                Thread.sleep(interval);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        producer.close();
    }
}