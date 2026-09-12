package cn.muzisheng.pear.vo.common;

import cn.muzisheng.pear.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ErrorVO extends BaseVO {
    private int status;
    private String message;
    private LocalDateTime time;
}
