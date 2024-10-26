package cn.muzisheng.pear.service.impl;

import cn.muzisheng.pear.entity.User;
import cn.muzisheng.pear.mapper.UserMapper;
import cn.muzisheng.pear.service.LogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private LogService logService;
    @Autowired
    private UserMapper userMapper;
    @Override
    public void run(String... args) throws Exception {
        Options options = new Options();
        options.addOption("u", "superuser", true, "用户名称");
        options.addOption("p", "password", true, "用户密码");
        options.addOption("h", "help", false, "帮助信息");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args, true);
            if (cmd.hasOption("u")&&cmd.hasOption("p")) {
                String email = cmd.getOptionValue("u");
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("email", email);
                User user = userMapper.selectOne(queryWrapper);
                if(user!=null){

                }
                logService.info("username: "+cmd.getOptionValue("u"));
                return;
            }

            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("options: ", options);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }
}
