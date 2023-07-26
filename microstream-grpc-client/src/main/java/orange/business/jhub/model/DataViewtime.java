package orange.business.jhub.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DataViewtime {

  /**
   *  Java collection as MicroStream base
   */
  //key pageuseryyyymmddhh user spent time on page per hour / value avgviewtime
  private final Map<String, PageViewTime> userViewTimes = new HashMap<>();
}
