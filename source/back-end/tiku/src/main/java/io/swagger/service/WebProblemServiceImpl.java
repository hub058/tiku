package io.swagger.service;

import io.swagger.pojo.ProblemFullData;
import io.swagger.pojo.dao.*;
import io.swagger.pojo.dao.repos.ExtDataRepository;
import io.swagger.pojo.dao.repos.ProblemRepository;
import io.swagger.pojo.dao.repos.ProblemTagRepository;
import io.swagger.pojo.dao.repos.TagRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WebProblemServiceImpl extends BasicService<Problem> implements WebProblemService {

    @Autowired
    private ProblemDataService problemDataService;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private WebAnswerServiceImpl webAnswerServiceImpl;

    @Autowired
    private WebStatusServiceImpl webStatusServiceImpl;

    @Autowired
    private WebExtDataServiceImpl webExtDataServiceImpl;

    @Autowired
    private WebProblemTagServiceImpl webProblemTagServiceImpl;

    @Autowired
    private ProblemTagRepository problemTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ExtDataRepository extDataRepository;

    @Override
    public List<ProblemFullData> getAll(Integer pageNumber, Integer pageSize) {

        List<Long> problemIdList = problemRepository.findIdList(PageRequest.of(pageNumber, pageSize));

        return problemDataService.getFullDataByIds(problemIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ProblemFullData problemFullData, Long createBy) throws Exception {

        Answer answer = problemFullData.getAnswer();
        List<Tag> tagList = problemFullData.getTags();
        Problem problem = problemFullData.getProblem();
        Status status = problemFullData.getStatus();
        Map<String, String> extData = problemFullData.getExtData();

        if (problem == null || answer == null) {
            throw new Exception("Param error : problem should not be null And answer should not be null");
        }

        /**
         * 新增问题答案
         */
        answer = webAnswerServiceImpl.add(answer, createBy);

        /**
         * 新增问题基础信息
         */
        problem.setAnswerId(answer.getId());
        problem = this.addBasicInfo(problem, createBy);

        /**
         * 新增题目状态
         */
        status = (status == null ? new Status() : status);
        status.setVerifyStatus(Status.UNCHECK);
        status.setProblemId(problem.getId());
        webStatusServiceImpl.add(status, createBy);

        /**
         * 新增题目标签
         */
        if (tagList != null && tagList.size() > 0) {
            List<ProblemTag> problemTagList = new ArrayList<>();
            for (Tag tag : tagList) {
                ProblemTag problemTag = new ProblemTag();
                // 这里假定只上传了标签的值，没有上传标签的id
                List<Tag> tagss = tagRepository.findByValueEquals(tag.getValue());
                if(tagss==null || tagss.size()==0){
                    // todo 如果标签不存在就新增标签
                    throw new Exception("所选标签不在数据库中");
                }
                problemTag.setTagId(tagss.get(0).getId());
                problemTag.setProblemId(problem.getId());
                problemTagList.add(problemTag);
            }
            webProblemTagServiceImpl.addAll(problemTagList, createBy);
        }


        /**
         * 新增题目扩展属性
         */
        if (extData != null && extData.size() > 0) {
            List<ExtData> extDataList = new ArrayList<>();
            for (String key : extData.keySet()) {
                ExtData extDataEntity = new ExtData();
                extDataEntity.setKeyname(key);
                extDataEntity.setValue(extData.get(key));
                extDataEntity.setProblemId(problem.getId());
                extDataList.add(extDataEntity);
            }
            webExtDataServiceImpl.addAll(extDataList, createBy);
        }
    }

    @Override
    public Problem addBasicInfo(Problem problem, Long createBy) {
        super.beforeAdd(problem, createBy);
        return problemRepository.save(problem);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {

        /**
         * 删除问题答案
         */
        Problem problem = problemRepository.findById(id).get();
        webAnswerServiceImpl.deleteById(problem.getAnswerId());

        /**
         * 删除问题基本信息
         */
        this.deleteBasicInfo(id);

        /**
         * 删除问题状态
         */
        webStatusServiceImpl.deleteByProblemId(id);

        /**
         * 删除问题标签
         */
        webProblemTagServiceImpl.deleteByProblemId(id);

        /**
         * 删除问题扩展属性
         */
        webExtDataServiceImpl.deleteByProblemId(id);

    }

    @Override
    public int deleteBasicInfo(Long id) {
        return problemRepository.updateIsDelById(id, Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ProblemFullData problemFullData, Long updateBy) throws Exception {

        Answer answer = problemFullData.getAnswer();
        List<Tag> tagList = problemFullData.getTags();
        Problem problem = problemFullData.getProblem();
        Status status = problemFullData.getStatus();
        Map<String, String> extData = problemFullData.getExtData();

        if (problem == null || problem.getId() == null) {
            throw new Exception("Param error : problem should not be null And problemId should not be null");
        }

        /**
         * 修改问题基本信息
         */
        problem = this.updateBasicInfo(problem, updateBy);

        /**
         * 修改问题答案
         */
        if (answer != null) {
            answer.setId(problem.getAnswerId());
            webAnswerServiceImpl.update(answer, updateBy);
        }

        /**
         * 修改问题状态
         */
        if (status != null) {
            status.setProblemId(problem.getId());
            webStatusServiceImpl.update(status, updateBy);
        }

        /**
         * 修改问题标签
         */
        if (tagList != null && tagList.size() > 0) {
            //先删除已关联的标签关系
            problemTagRepository.deleteAllByProblemIdEquals(problem.getId());

            //再添加重新关联的标签关系
            List<ProblemTag> problemTagList = new ArrayList<>();

            for (Tag tag : tagList) {
                ProblemTag problemTag = new ProblemTag();
                problemTag.setProblemId(problem.getId());
                problemTag.setTagId(tag.getId());
                problemTagList.add(problemTag);
            }
            webProblemTagServiceImpl.addAll(problemTagList, updateBy);
        }

        /**
         * 修改问题扩展属性
         */
        if (extData != null && extData.size() > 0) {
            //先删除已关联的扩展属性
            extDataRepository.deleteAllByProblemId(problem.getId());

            //再添加重新关联的扩展属性
            List<ExtData> extDataList = new ArrayList<>();
            for (String key : extData.keySet()) {
                ExtData extDataEntity = new ExtData();
                extDataEntity.setProblemId(problem.getId());
                extDataEntity.setKeyname(key);
                extDataEntity.setValue(extData.get(key));
                extDataList.add(extDataEntity);
            }
            webExtDataServiceImpl.addAll(extDataList, updateBy);
        }
    }

    @Override
    public Problem updateBasicInfo(Problem problem, Long updateBy) {
        Problem dbProblem = problemRepository.findById(problem.getId()).get();
        dbProblem.setProblemText(problem.getProblemText());
        dbProblem.setParentId(problem.getParentId());
        super.beforeUpdate(dbProblem, updateBy);
        return problemRepository.save(dbProblem);
    }
}
