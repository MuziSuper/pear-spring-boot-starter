package cn.muzisheng.pear.dto.admin;

import cn.muzisheng.pear.dto.BaseDTO;
import lombok.Data;

import java.util.Map;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
/** Dynamic field payload used to update an Admin-managed entity. */
public class AdminUpdateRequestDTO extends BaseDTO {
    private Map<String, Object> data;
}
