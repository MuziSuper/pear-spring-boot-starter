package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@lombok.EqualsAndHashCode(callSuper = false)
@TableName("user_role_ass")
@Entity(name = "user_role_ass")
public class UserRoleAss extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_id")
    private Long roleId;

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @PearField(isPrimaryKey = true,isEdit = false)
//    private Integer id;
//    @Column(name = "role_name")
//    @PearField
}
