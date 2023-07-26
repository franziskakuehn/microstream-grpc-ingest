package orange.business.jhub.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class DataPageView {

  /**
   * Java collection as MicroStream base
   * key combination userid/time
   */
  private final Map<String, PageViewClient> userPageClicks = new HashMap<>();
  
}
