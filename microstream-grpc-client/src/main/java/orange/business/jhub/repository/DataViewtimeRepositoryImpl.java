package orange.business.jhub.repository;

import java.util.Map;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;
import orange.business.jhub.model.DataViewtime;
import orange.business.jhub.model.PageViewTime;

@Slf4j
@Singleton
public class DataViewtimeRepositoryImpl implements DataViewtimeRepository {

    private final StorageManager storageManager;

    public DataViewtimeRepositoryImpl(
            @Named("user-avg-time") final StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    /**
     * update pageviewtimerecords
     */
    @Override
    public void updateUserViewTime(final String userid, final PageViewTime pageViewTime) {
        val readAllViewTimes = readAllViewTimes();

        val key =
                pageViewTime.pageid().concat(pageViewTime.userid()).concat(pageViewTime.hour());

        log.info("going to store: key: {}, value {}", key, pageViewTime.toString());

        readAllViewTimes.put(key,
                pageViewTime);

        XThreads.executeSynchronized(() -> storageManager.store(readAllViewTimes));

        log.info("size avgs: {}", readAllViewTimes.size());

    }

    @Override
    public Map<String, PageViewTime> readAllViewTimes() {
        return dataUserAvgViewtime().getUserViewTimes();
    }


    private DataViewtime dataUserAvgViewtime() {
        return (DataViewtime) storageManager.root();
    }

}
