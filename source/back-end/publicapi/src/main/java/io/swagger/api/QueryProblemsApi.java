/**
 * NOTE: This class is auto generated by the io.swagger code generator program (3.0.8).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.model.QuerryInfo;
import io.swagger.model.QuerryResult;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@javax.annotation.Generated(value = "io.io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-07-07T02:38:17.975Z[GMT]")
@Api(value = "queryProblems", description = "the queryProblems API")
public interface QueryProblemsApi {

    @ApiOperation(value = "查询题目信息", nickname = "queryProblems", notes = "按条件查询题目 ", response = QuerryResult.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "返回查询到的项目列表", response = QuerryResult.class),
        @ApiResponse(code = 401, message = "参数格式错误") })
    @RequestMapping(value = "/queryProblems",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<QuerryResult> queryProblems(@ApiParam(value = ""  )  @Valid @RequestBody QuerryInfo body);

}
