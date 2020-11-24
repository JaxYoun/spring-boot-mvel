package com.yang.springbootmvel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@SpringBootApplication
public class SpringBootMvelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMvelApplication.class, args);
    }

}

/**
 * 后端接口
 *
 * @author: Yang
 * @date: 2019/11/14 20:39
 * @description:
 */
@Slf4j
@Controller
class MvelController {

    @RequestMapping("/")
    public String index() {
        /*
        return "verify_index";
        return "verify_index_layui";
        return "verify_index_layui_two";
        */
        return "verify_index_layui_three";
    }

    @PostMapping("/exec")
    @ResponseBody
    public Result exec(@RequestBody ExecReq req) {
        try {
            Map<String, Object> paramMap = this.toMap(req.getRule(), req.getParams());
            String result = MvelUtil.execStr(req.getRule(), paramMap);
            return Result.success(new ExecVO(result, this.paramToStr(paramMap)));
        } catch (Exception e) {
            return Result.fail(String.format("引擎执行错误,请检查规则或者参数是否正确,详见控制台=【%s】", e.getMessage()));
        }
    }

    private Map<String, Object> toMap(String rule, List<ParamsReq> calcField) {
        Map<String, Object> paramMap = new HashMap(16);
        Iterator var4 = calcField.iterator();

        while (true) {
            ParamsReq param;
            do {
                do {
                    if (!var4.hasNext()) {
                        return paramMap;
                    }

                    param = (ParamsReq) var4.next();
                } while (Objects.isNull(param));
            } while (Objects.isNull(param.getFieldName()) && Objects.isNull(param.getFieldValue()));

            paramMap.put(param.getFieldName().trim(), rule.contains("empty") && "0".equals(param.getFieldValue().toString()) ? param.getFieldValue().toString() : param.getFieldValue());
        }
    }

    private String paramToStr(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        Iterator var3 = paramMap.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) var3.next();
            sb.append((String) entry.getKey()).append(":").append(entry.getValue()).append("; ");
        }

        return sb.toString();
    }

}

@Data
class Result {

    private Object data;

    private String message;

    private Integer status;

    public Result(Object data, String message, Integer status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    private Result(String message, Integer status) {
        this.message = message;
        this.status = status;
    }

    private Result(Object data, Integer status) {
        this.data = data;
        this.status = status;
    }

    public static Result success(Object data) {
        return new Result(data, 200);
    }

    public static Result fail(String message) {
        return new Result(message, 4001);
    }

}

final class MvelUtil {

    static String execStr(String rule, Object ctx) {
        Object result = MVEL.eval(rule, ctx);
        return Objects.nonNull(result) ? result.toString() : "";
    }

}

@Data
class ParamsReq {

    private String fieldName;

    private Object fieldValue;

}

@Data
class ExecReq {

    private String rule;

    private List<ParamsReq> params;

}

@Data
@AllArgsConstructor
class ExecVO {

    private String result;

    private String param;

}
