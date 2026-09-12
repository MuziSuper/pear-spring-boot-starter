package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "permission")
@Data
@lombok.EqualsAndHashCode(callSuper = false)
@TableName("permission")
@PearObject(
        desc = "Permission Information.",
        path = "permission",
        pluralName = "permissions"
)
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isEdit = false)
    private Long id;
    @Column(name = "permission_code", nullable = false)
    @PearField
    private String permissionCode;
    @Column(name = "description", nullable = false)
    @PearField
    private String description;
}
