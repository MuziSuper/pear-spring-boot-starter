package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.AdminObject;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@FunctionalInterface
public interface BeforeUpdate {
    /**
     * 更新数据前的处理逻辑
     * @param request 请求
     * @param adminObject AdminObject对象
     * @param objectsMap 待更新的数据集合
     **/
    void execute(HttpServletRequest request, AdminObject adminObject, Map<String, Object> objectsMap);
}
