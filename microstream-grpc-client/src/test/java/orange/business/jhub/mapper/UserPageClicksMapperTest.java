package orange.business.jhub.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.google.common.primitives.Ints;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import orange.business.jhub.model.PageViewClient;

@MicronautTest
public class UserPageClicksMapperTest {

        @Inject
        ClicksAggregator clicksaggregator;


        @Test
        void testGetViewTimesPerHour() {

                PageViewClient pageViewRecord =
                                new PageViewClient("20237143", "Page_81", "User_9",
                                                587551, System.currentTimeMillis());

                PageViewClient pageViewRecord2 =
                                new PageViewClient("20237143", "User_9", "Page_81", 472511,
                                                System.currentTimeMillis());

                List<PageViewClient> listPageViewRecord =
                                Arrays.asList(pageViewRecord, pageViewRecord2);

                HashMap<String, List<PageViewClient>> hashMap =
                                new HashMap<String, List<PageViewClient>>();
                hashMap.put("20237143", listPageViewRecord);

                HashMap<String, List<Integer>> hashMapExpected =
                                new HashMap<String, List<Integer>>();
                hashMapExpected.put("20237143", Arrays.asList(587551, 472511));

                Assertions.assertArrayEquals(Ints.toArray(hashMapExpected.get("20237143")),
                                Ints.toArray(clicksaggregator.getViewTimesPerHour(hashMap)
                                                .get("20237143")));
        }

        @Test
        void testGroupViewTimesPerHour() {

                PageViewClient pageViewRecord =
                                new PageViewClient("20237143", "Page_81", "User_9", 587551,
                                                System.currentTimeMillis());
                PageViewClient pageViewRecord2 =
                                new PageViewClient("20237143", "User_9", "Page_81", 472511,
                                                System.currentTimeMillis());

                List<PageViewClient> listPageViewRecord =
                                Arrays.asList(pageViewRecord, pageViewRecord2);

                HashMap<String, List<PageViewClient>> hashMap =
                                new HashMap<String, List<PageViewClient>>();
                hashMap.put("20237143", listPageViewRecord);

                Assertions.assertEquals(hashMap.size(),
                                clicksaggregator.groupViewTimesPerHour(listPageViewRecord).size());
        }

        @Test
        void TestAverageViewTime() {
                Assertions.assertEquals(2.5,
                                clicksaggregator.averageViewTime(Arrays.asList(2, 4, 1, 3)));
        }
}
