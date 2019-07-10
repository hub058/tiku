package io.swagger.pojo.dao.repos;

import io.swagger.pojo.dao.ExtData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 功能描述：题目扩展信息实体Jpa操作类
 *
 * @作者：黄宽波
 * @时间:2019-07-09
 */
@Repository
public interface ExtDataRepository extends JpaRepository<ExtData, Long> {

    List<ExtData> findAllByProblemId(List<Long> problemIdList);
}