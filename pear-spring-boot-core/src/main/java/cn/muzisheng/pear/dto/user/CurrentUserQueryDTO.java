package cn.muzisheng.pear.dto.user;

import cn.muzisheng.pear.dto.BaseDTO;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
/** Query input for the current-user endpoint. */
public class CurrentUserQueryDTO extends BaseDTO {
    private String withToken;
}
