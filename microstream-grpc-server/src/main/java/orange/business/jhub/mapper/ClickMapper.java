package orange.business.jhub.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.val;
import orange.business.jhub.PageView;
import orange.business.jhub.model.KafkaPageViewRecord;

@Singleton
public class ClickMapper {

    /**
     * map Kafka json record to PageView object
     * @param key
     * @param value
     * @return
     * @throws JsonProcessingException
     */
    public PageView map(long key, String value) throws JsonProcessingException {
        val kafkaPageViewRecord = new ObjectMapper()
                .readValue(value, KafkaPageViewRecord.class);

        return PageView.newBuilder()
                .setTimestamp(key)
                .setPageid(kafkaPageViewRecord.pageid())
                .setUserid(kafkaPageViewRecord.userid())
                .setViewtime(kafkaPageViewRecord.viewtime())
                .build();
    }
}
