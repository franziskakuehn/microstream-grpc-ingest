package orange.business.jhub.mapper;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Singleton;
import orange.business.jhub.model.PageViewClient;
import orange.business.jhub.model.PageViewTime;

@Singleton
public class ClicksAggregator {
  /**
   * find collect all viewtime per user on page
   */
  public List<PageViewTime> avgViewTimes(final String userId, final String pageId,
      final Map<String, PageViewClient> pageViews) {

    final List<PageViewClient> pageViewRecordsPerUser =
        pageViews.values().stream().filter(pageViewRecord -> userId.equals(pageViewRecord.userid()))
            .filter(pageViewRecord -> pageId.equals(pageViewRecord.pageid()))
            .collect(Collectors.toList());

    final Map<String, List<PageViewClient>> pageViewRecordsPerHour =
        groupViewTimesPerHour(pageViewRecordsPerUser);

    return pagesViewTimes(userId, pageId, pageViewRecordsPerHour);
  }

  
  @VisibleForTesting
  Map<String, List<PageViewClient>> groupViewTimesPerHour(
      final List<PageViewClient> pageViewRecordsPerUser) {
    return pageViewRecordsPerUser.stream()
        .collect(Collectors.groupingBy(PageViewClient::hour));
  }

  /**
   * calc average viewtime per hour from user on page
   * 
   * @param userId
   * @param pageId
   * @param pageViewRecordsPerHour
   * @return
   */
  private List<PageViewTime> pagesViewTimes(final String userId, final String pageId,
      final Map<String, List<PageViewClient>> pageViewRecordsPerHour) {
    final Map<String, List<Integer>> viewTimesPerHour = getViewTimesPerHour(pageViewRecordsPerHour);

    return viewTimesPerHour.entrySet()
        .stream()
        .map(entry -> new PageViewTime(pageId, userId, entry.getKey(),
            averageViewTime(entry.getValue())))
        .toList();
  }

  /**
   * getting a pure list with viewtimes
   * 
   * @param pageViewRecordsPerHour
   * @return
   */
  @VisibleForTesting
  Map<String, List<Integer>> getViewTimesPerHour(
      final Map<String, List<PageViewClient>> pageViewRecordsPerHour) {
    return pageViewRecordsPerHour.entrySet()
        .stream()
        .map(entry -> Map.entry(entry.getKey(), viewTimesPerHour(entry.getValue())))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  @VisibleForTesting
  double averageViewTime(final List<Integer> viewTimes) {
    return viewTimes.stream().mapToInt(Integer::intValue).average().orElse(Double.NaN);
  }

  private List<Integer> viewTimesPerHour(final List<PageViewClient> pageViewRecords) {
    return pageViewRecords.stream().map(PageViewClient::viewtime).toList();
  }
}
