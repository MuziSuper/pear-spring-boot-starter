package cn.muzisheng.pear.vo.common;

import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.vo.BaseVO;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
/** Standard HTTP success/failure envelope for DTO/VO endpoints. */
public class ApiResponseVO<T> extends BaseVO {
    private String error;
    private T data;

    /** Creates an API envelope from the legacy response body during the compatibility period. */
    public static <T> ApiResponseVO<T> from(Result<T> result) {
        ApiResponseVO<T> response = new ApiResponseVO<>();
        if (result != null) {
            response.setError(result.getError());
            response.setData(result.getData());
        }
        return response;
    }
}
