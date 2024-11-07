package cn.muzisheng.pear.command;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.dao.UserDAO;
import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.exception.UserException;
import cn.muzisheng.pear.service.LogService;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class MyCommandLineRunner implements CommandLineRunner {

    private final LogService logService;

    private final UserDAO userDAO;
    @Autowired
    public MyCommandLineRunner(LogService logService, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.logService=logService;
    }

    @Override
    public void run(String... args) throws Exception {
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

            // 处理数据库参数，可以使用mysql与sqlite，并且配合memory参数使用sqlite内存模式。


            // 创建超级用户
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
                user.setStaff(true);
                user.setSuperUser(true);
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

    }
}
