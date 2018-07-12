package example;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import example.model.ObjectToConsume;
import example.model.ObjectToProduce;
import example.util.MailSender;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import com.amazonaws.services.kinesis.AmazonKinesisClient;

@Component("kinesisFunction")
public class TheFunction implements Consumer<ObjectToConsume>{

    private static final String streamName = "KinesisToLambdaX242698";
    private static final String partitionKey = "partition-1";

    @Override
    public void accept(ObjectToConsume objectToConsume) {

        System.out.println(objectToConsume);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = "EMPTY";
        try {
             json = ow.writeValueAsString(objectToConsume);
        } catch (JsonProcessingException e) {
            System.out.println("Problem with json.");
        }
        System.out.println("Json: "+ json);

        PutRecordRequest request = new PutRecordRequest()
                .withStreamName(streamName)
                .withPartitionKey(partitionKey)
                .withData(ByteBuffer.wrap(json.getBytes()));

        AmazonKinesis client = AmazonKinesisClientBuilder.defaultClient();
        PutRecordResult result = client.putRecord(request);
        System.out.println("Wrote to kinesis...");
        System.out.println("Seq number: "+ result.getSequenceNumber());
        System.out.println("Shard id: "+ result.getShardId());
    }
}
