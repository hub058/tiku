/**
 * 题库外部接口
 * 题库系统供其他应用调用的接口
 *
 * OpenAPI spec version: 1.1.0
 * Contact: czfshine@outlook.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */import { OneOfExpressionArgument1 } from './oneOfExpressionArgument1';
import { OneOfExpressionArgument2 } from './oneOfExpressionArgument2';


/**
 * 要查询执行的操作，类型是一个 操作表达式对象 ，每个 操作表达式 对象包括 operator(操作名),argument1/2 参数。同时参数也可以是另外的一个表达式对象。样例的查询等价于 ((grade == \"一年级\") and (sroce <= 10))| 具体信息在：https://www.yuque.com/czfshine/olm1pa/mxeyum 
 */
export interface Expression { 
    operator: string;
    argument1: OneOfExpressionArgument1;
    argument2: OneOfExpressionArgument2;
}