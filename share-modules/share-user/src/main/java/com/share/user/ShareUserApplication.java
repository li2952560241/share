package com.share.user;

import com.share.common.security.annotation.EnableCustomConfig;
import com.share.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication
public class ShareUserApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ShareUserApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  会员模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
