package orange.business.jhub.repo;

import java.util.stream.Stream;

import orange.business.jhub.PageView;

public interface ClickRepository {

  void savePageView(PageView pageview);

  Stream<PageView> readAllPages();

}
