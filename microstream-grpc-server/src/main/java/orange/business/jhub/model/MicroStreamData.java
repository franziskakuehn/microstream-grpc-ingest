package orange.business.jhub.model;

import java.util.ArrayList;
import java.util.List;
import orange.business.jhub.PageView;

public class MicroStreamData {

  private final ArrayList<PageView> pageViews = new ArrayList<>();

  public List<PageView> getPageViews() {
    return this.pageViews;
  }
}
