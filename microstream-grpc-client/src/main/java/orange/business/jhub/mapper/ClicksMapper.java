package orange.business.jhub.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import jakarta.inject.Singleton;
import orange.business.jhub.PageView;
import orange.business.jhub.model.PageViewClient;


@Singleton
public class ClicksMapper {
  List<PageView> pageviews = new ArrayList<>();


  /**
   * map to pageviewclient record
   * 
   * @param pageView
   * @return
   */
  public PageViewClient map2PageViewClient(final PageView pageView) {
    return new PageViewClient(getYearMonthDayHour(pageView.getTimestamp()),
        pageView.getUserid(),
        pageView.getPageid(), pageView.getViewtime(), pageView.getTimestamp());
  }

  /**
   * extract hour from timestamp
   * 
   * @param timestamp
   * @return
   */
  private String getYearMonthDayHour(final long timestamp) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    return new SimpleDateFormat("yyyyMMddHH").format(cal.getTime());
  }


}
