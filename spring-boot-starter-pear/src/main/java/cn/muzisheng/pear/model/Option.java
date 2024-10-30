package cn.muzisheng.pear.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Option {
    private String option;
    private String longOption;
    private String description;
}
