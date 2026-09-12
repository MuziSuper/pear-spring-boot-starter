package cn.muzisheng.pear.util;

import cn.muzisheng.pear.model.Result;
import cn.muzisheng.pear.vo.common.ApiResponseVO;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

/**
 * Converts legacy {@link Result} containers while public HTTP endpoints migrate to DTO/VO contracts.
 * HTTP status and headers are retained exactly; only the response body is mapped.
 */
public final class ApiResponseUtil {
    private ApiResponseUtil() {
    }

    /** Maps a legacy body value to a VO body value without changing transport metadata. */
    public static <S, T> ResponseEntity<ApiResponseVO<T>> map(ResponseEntity<Result<S>> source,
                                                                Function<S, T> mapper) {
        Result<S> body = source.getBody();
        ApiResponseVO<T> response = new ApiResponseVO<>();
        if (body != null) {
            response.setError(body.getError());
            response.setData(mapper.apply(body.getData()));
        }
        return new ResponseEntity<>(response, source.getHeaders(), source.getStatusCode());
    }
}
