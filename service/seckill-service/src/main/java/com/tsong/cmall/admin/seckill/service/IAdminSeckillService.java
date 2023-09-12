package com.tsong.cmall.admin.seckill.service;


import com.tsong.cmall.admin.seckill.web.params.SeckillAddParam;
import com.tsong.cmall.admin.seckill.web.params.SeckillEditParam;
import com.tsong.cmall.common.util.PageQueryUtil;
import com.tsong.cmall.common.util.PageResult;
import com.tsong.cmall.seckill.web.vo.SeckillVO;


public interface IAdminSeckillService {
    PageResult getSeckillPage(PageQueryUtil pageUtil);

    SeckillVO getSeckillVOById(Long id);

    boolean saveSeckill(SeckillAddParam seckillAddParam);

    boolean updateSeckill(SeckillEditParam seckillEditParam);


    boolean deleteSeckillById(Long id);

}
