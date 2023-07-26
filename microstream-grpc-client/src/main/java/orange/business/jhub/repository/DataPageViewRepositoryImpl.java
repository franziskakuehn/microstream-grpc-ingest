package orange.business.jhub.repository;

import java.util.Map;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;
import orange.business.jhub.model.DataPageView;
import orange.business.jhub.model.PageViewClient;

@Slf4j
@Singleton
public class DataPageViewRepositoryImpl implements DataPageViewRepository {

    // @Inject
    // @Named("user-page-clicks")
    private final StorageManager storageManager;

    public DataPageViewRepositoryImpl(
            @Named("user-page-clicks") final StorageManager storageManager) {

        this.storageManager = storageManager;
    }

    /**
     * Update pageview records
     * 
     * @param pageViewRecord
     */
    @Override
    public void updateUserPageClicks(final String key, final PageViewClient pageViewRecord) {
        val userPageClicks = readAllPageClicks();


        if (userPageClicks.containsKey(key)) {
            log.info("record already exists, key: {} value: {}", key, pageViewRecord);
        } else {
            log.info("going to store: key: {}, value {}", key, pageViewRecord.toString());
            userPageClicks.put(key, pageViewRecord);

            XThreads.executeSynchronized(() -> storageManager.store(userPageClicks));
        }

        log.info("list size: {}", userPageClicks.size());
    }


    /**
     *
     * @return
     */
    @Override
    public Map<String, PageViewClient> readAllPageClicks() {
        return dataUserPageClicks().getUserPageClicks();
    }

    private DataPageView dataUserPageClicks() {
        return (DataPageView) storageManager.root();
    }



}
