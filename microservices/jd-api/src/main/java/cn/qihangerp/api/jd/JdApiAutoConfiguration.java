package cn.qihangerp.api.jd;

//import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class ,MybatisAutoConfiguration.class})
//@ComponentScan
//@Configuration
//@EnableAutoConfiguration
@Configuration
//@EnableAutoConfiguration(exclude = MybatisAutoConfiguration.class)
@ComponentScan(basePackages = "cn.qihangerp.api.jd",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JdApiAutoConfiguration.class))
public class JdApiAutoConfiguration {
}
