package orange.business.jhub.repository;

import java.util.Map;
import orange.business.jhub.model.PageViewClient;

public interface DataPageViewRepository {
  
  void updateUserPageClicks(String key, PageViewClient pageView);

  Map<String, PageViewClient> readAllPageClicks();

}
