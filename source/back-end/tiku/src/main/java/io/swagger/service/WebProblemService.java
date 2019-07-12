package io.swagger.service;

import io.swagger.pojo.ProblemFullData;
import io.swagger.pojo.dao.Problem;

import java.util.List;

public interface WebProblemService {

    List<ProblemFullData> getAll(Integer pageNumber, Integer pageSize);

    void add(ProblemFullData problemFullData, Long createBy) throws Exception;

    Problem addBasicInfo(Problem problem, Long createBy);

    void delete(Long id);

    int deleteBasicInfo(Long id);

    void update(ProblemFullData problemFullData, Long updateBy) throws Exception;

    Problem updateBasicInfo(Problem problem, Long updateBy);
}
