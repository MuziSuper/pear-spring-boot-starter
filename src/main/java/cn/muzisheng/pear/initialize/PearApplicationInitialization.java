package cn.muzisheng.pear.initialize;

import cn.muzisheng.pear.core.config.ConfigService;
import cn.muzisheng.pear.mapper.dao.UserDAO;
import cn.muzisheng.pear.model.AdminObject;
import cn.muzisheng.pear.properties.CacheProperties;
import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.UserException;
import cn.muzisheng.pear.utils.ExpiredCache;
import cn.muzisheng.pear.utils.ExpiredCacheFactory;
import org.apache.commons.cli.*;
import org.apache.commons.cli.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.util.*;

/**
 * pear框架初始化程序
 **/
@Component
@Order(1)
public class PearApplicationInitialization implements CommandLineRunner {
    private final UserDAO userDAO;
    private final ConfigService configService;
    private final static Logger logService= LoggerFactory.getLogger(PearApplicationInitialization.class);
    public static ExpiredCache<String,String> ConfigCache;
    public static ExpiredCache<String,String> EnvCache;

    @Autowired
    public PearApplicationInitialization(UserDAO userDAO, CacheProperties cacheProperties, ConfigService configService) {
        this.userDAO = userDAO;
        this.configService = configService;
        // 初始化缓存
        ConfigCache= ExpiredCacheFactory.newExpiredCacheFactory(cacheProperties.getCapacity(),cacheProperties.getExpire());
        EnvCache=ExpiredCacheFactory.newExpiredCacheFactory(cacheProperties.getCapacity(),cacheProperties.getExpire());
    }

    @Override
    public void run(String... args) {
        // 命令行参数
        Options options = new Options();
        List<Option> optionList=new ArrayList<>();
        optionList.add(new Option("u", "superuser", true, "username"));
        optionList.add(new Option("p", "password", true, "user password"));
        optionList.add(new Option("h", "help", false, "help Information"));
        optionList.add(new Option("d", "driver", true, "database driven"));
        optionList.add(new Option("m", "memory", true, "sqlite is memory-enabled"));
        for(Option option:optionList){
            options.addOption(option);
        }
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
            // 打印帮助信息
            if (cmd.hasOption("h")) {
                logService.info(String.format("%-15s %-15s%-15s %-15s", Constant.HELP_OPTION,Constant.HELP_LONG_OPTION,Constant.HELP_ARGUMENTS,Constant.HELP_DESCRIPTION));
                for (Option option : optionList) {
                    logService.info(String.format("-%-15s %-15s %-15s %-15s",option.getOpt(),option.getLongOpt(),option.getArgs()==1,option.getDescription()));
                }
                System.exit(1);
            }

            /*
             * 处理数据库参数，可以使用mysql与sqlite，并且配合memory参数使用sqlite内存模式
             */

            // 创建超级用户，不从这创建的，都是无R权限的用户
            if (cmd.hasOption("u")&&cmd.hasOption("p")) {
                String email = cmd.getOptionValue("u");
                String password=cmd.getOptionValue("p");
                User user=userDAO.getUserByEmail(email);
                if(user!=null){
                     if(!userDAO.setPassword(user,password)){
                         throw new UserException(user.getEmail(), "failed to update password");
                     }
                     logService.warn("superuser updates passwords, the superuser is "+email);
                     System.exit(1);
                }else{
                    user=userDAO.createUser(email,password);
                    if(user==null){
                        logService.error("failed to create a user: "+email);
                        System.exit(1);
                    }
                }
                user.setIsStaff(true);
                user.setIsSuperUser(true);
                user.setActivated(true);
                user.setEnabled(true);
                if(!userDAO.save(user)){
                    throw new UserException(user.getEmail(), "failed to save superuser");
                }
                logService.warn("create a super administrator: "+email);
                System.exit(1);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        // pear配置入库
        configService.checkValue(Constant.KEY_SITE_NAME, "pear", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.ICON_SVG_ADDRESS, "../static/favicon.svg", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.ICON_SVG_ADDRESS, "../static/favicon.png", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNIN_URL, "/auth/login", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNUP_URL, "/auth/register", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_LOGOUT_URL, "/auth/logout", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_RESET_PASSWORD_URL, "/auth/reset_password", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNIN_API, "/auth/login", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_SIGNUP_API, "/auth/register", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_RESET_PASSWORD_DONE_API, "/auth/reset_password_done", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_CHANGE_EMAIL_DONE_API, "/auth/change_email_done", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_LOGIN_NEXT, "/", Constant.ConfigFormatText, true, true);
        configService.checkValue(Constant.KEY_SITE_USER_ID_TYPE, "email", Constant.ConfigFormatText, true, true);
        // 构建入口
        AdminContainer.buildAdminObjects(AdminContainer.getAllAdminObjects());
        userAddFunc();
    }
    private void userAddFunc(){
        AdminObject.BuilderFactory  builderFactory=new AdminObject.BuilderFactory(User.class);
            builderFactory.setBeforeUpdate((request, adminObject, object) -> {
                if (object instanceof Map<?, ?>) {
                    Map<String, Object> objectMap = (Map<String, Object>) object;
                    if (objectMap.containsKey("password")) {
                        Object password = objectMap.get("password");
                        if (password instanceof String pwdStr && !pwdStr.isEmpty()) {
                            objectMap.put("password", userDAO.HashPassword(pwdStr));
                        }

                    }
                }
            });
    }

}
