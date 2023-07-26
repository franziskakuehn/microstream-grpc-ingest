package orange.business.jhub.repository;

import java.util.Map;
import orange.business.jhub.model.PageViewTime;

public interface DataViewtimeRepository {
  
  void updateUserViewTime(String userid, PageViewTime pageViewTime);

  Map<String,PageViewTime> readAllViewTimes();

}
