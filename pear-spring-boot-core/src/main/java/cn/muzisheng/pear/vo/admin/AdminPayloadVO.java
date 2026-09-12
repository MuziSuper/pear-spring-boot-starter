package cn.muzisheng.pear.vo.admin;

import cn.muzisheng.pear.vo.BaseVO;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Preserves Admin's dynamic payload while keeping the API method output typed as a VO.
 * {@link JsonValue} serializes the wrapped value directly so existing Admin JSON clients do not gain
 * an extra {@code payload} nesting level.
 */
@Data
@lombok.EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class AdminPayloadVO extends BaseVO {
    private Object payload;

    @JsonValue
    public Object jsonValue() {
        return payload;
    }
}
