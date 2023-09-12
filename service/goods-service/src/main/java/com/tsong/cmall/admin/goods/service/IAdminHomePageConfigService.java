package com.tsong.cmall.admin.goods.service;

import com.tsong.cmall.admin.goods.web.params.HomePageConfigAddParam;
import com.tsong.cmall.admin.goods.web.params.HomePageConfigEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.entity.HomePageConfig;

public interface IAdminHomePageConfigService {
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveHomePageConfig(HomePageConfigAddParam homePageConfigAddParam);

    String updateHomePageConfig(HomePageConfigEditParam homePageConfigEditParam);

    HomePageConfig getHomePageConfigById(Long id);

    Boolean deleteBatch(Long[] ids);
}
