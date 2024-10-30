package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.service.LogService;
import cn.muzisheng.pear.service.UserService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;
    @Override
    public void run(String... args) throws Exception {
        Options options = new Options();
        options.addOption("u", "superuser", true, "username");
        options.addOption("p", "password", true, "user password");
        options.addOption("h", "help", false, "Help Information");
        options.addOption("d", "driver", true, "database driven");
        options.addOption("m", "memory", true, "sqlite is memory-enabled");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;


        try {
            cmd = parser.parse(options, args, true);

            // 打印帮助信息
            if (cmd.hasOption("h")) {
                Option[] optionsList =cmd.getOptions();
                logService.info("longOpt\thasArg\tdescription");
                for (Option option : optionsList) {
                    logService.info("-"+option.getLongOpt()+"\t"+option.hasArg()+"\t"+option.getDescription());
                }
                System.exit(1);
            }

            // 处理数据库参数，可以使用mysql与sqlite，并且配合memory参数使用sqlite内存模式。


            // 创建超级用户
            if (cmd.hasOption("u")&&cmd.hasOption("p")) {
                String email = cmd.getOptionValue("u");
                String password=cmd.getOptionValue("p");
                User user=userService.getUserByEmail(email);
                if(user!=null){
                     userService.setPassword(user,password);
                     logService.warn(email+" 超级用户更新密码");
                }else{
                    user=userService.createUserByEmail(email,password);
                    if(user==null){
                        logService.error("创建用户失败");
                        System.exit(1);
                    }
                }
                user.setStaff(true);
                user.setSuperUser(true);
                user.setActivated(true);
                user.setEnabled(true);
                userService.save(user);
                logService.warn("创建超级用户: "+email);
                System.exit(1);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }
}
