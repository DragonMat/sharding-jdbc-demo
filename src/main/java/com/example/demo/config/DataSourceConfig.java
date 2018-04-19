package com.example.demo.config;


import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingjdbc.core.jdbc.core.datasource.ShardingDataSource;
import io.shardingjdbc.core.rule.BindingTableRule;
import io.shardingjdbc.core.rule.ShardingRule;
import io.shardingjdbc.core.rule.TableRule;
import io.shardingjdbc.core.util.DataSourceUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.*;

@Configuration
@MapperScan(basePackages = "com.example.demo.mapper", sqlSessionTemplateRef = "testSqlSessionTemplate")
public class DataSourceConfig {
//
//    @Bean(name = "dataSource0")
//    @ConfigurationProperties(prefix = "spring.datasource.user0")
//    public DataSource dataSource0() {
//        DruidDataSource result = new DruidDataSource();
//        result.setDriverClassName("com.mysql.jdbc.Driver");
//        result.setUrl(String.format("jdbc:mysql://localhost:3306/user_0"));
//        result.setUsername("root");
//        result.setPassword("123456");
//        return result;
//    }
//
//    @Bean(name = "dataSource1")
//    @ConfigurationProperties(prefix = "spring.datasource.user1")
//    public DataSource dataSource1() {
//        DruidDataSource result = new DruidDataSource();
//        result.setDriverClassName("com.mysql.jdbc.Driver");
//        result.setUrl(String.format("jdbc:mysql://localhost:3306/user_1"));
//        result.setUsername("root");
//        result.setPassword("123456");
//        return result;
//    }
//
//    /**
//     * 设置默认的数据源
//     *
//     * @param dataSource0
//     * @param dataSource1
//     * @return
//     */
//    @Bean(name = "dataSourceRule")
//    public DataSourceRule dataSourceRule(@Qualifier("dataSource0") DataSource dataSource0,
//                                         @Qualifier("dataSource1") DataSource dataSource1) {
//
//        Map<String, DataSource> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("dataSource0", dataSource0);
//        dataSourceMap.put("dataSource1", dataSource1);
//
//        return new DataSourceRule(dataSourceMap, "dataSource0");
//    }
//
//    /**
//     * 配置数据源策略和表策略，具体策略需要自己实现
//     *
//     * @param dataSourceRule
//     * @return
//     */
//    @Bean(name = "shardingRule")
//    public ShardingRule shardingRule(@Qualifier("dataSourceRule") DataSourceRule dataSourceRule) {
//
//        //具体的分库分表的策略
//        TableRule userTableRule = TableRule.builder("user_info")
//                .actualTables(Arrays.asList("user_info_1", "user_info_0", "user_info_0_1", "user_info_1_1"))
//                .tableShardingStrategy(new TableShardingStrategy("user_id",
//                        new DemoTableShardingAlgorithm()))
//                .dataSourceRule(dataSourceRule)
//                .build();
//        //绑定表策略，在查询时会使用主表策略计算路由的数据源，因此需要约定绑定表策略的表的规则需要一致，可以一定程度提高效率
//        List<BindingTableRule> bindingTableRules = new ArrayList<BindingTableRule>();
//        bindingTableRules.add(new BindingTableRule(Arrays.asList(userTableRule)));
//
//
//        return ShardingRule.builder()
//                .dataSourceRule(dataSourceRule)
//                .tableRules(Arrays.asList(userTableRule))
//                .bindingTableRules(bindingTableRules)
//                .databaseShardingStrategy(new DatabaseShardingStrategy("user_id",
//                        new DemoDatabaseShardingAlgorithm()))
//                .tableShardingStrategy(new TableShardingStrategy("user_id",
//                        new DemoTableShardingAlgorithm()))
//                .build();
//
//    }
//
//    /**
//     * 创建sharding-jdbc的数据源DataSource，MybatisAutoConfiguration会使用此数据源
//     *
//     * @param shardingRule
//     * @return
//     * @throws SQLException
//     */
//    @Bean(name = "dataSource")
//    public DataSource shardingDataSource(@Qualifier("shardingRule") ShardingRule shardingRule) throws SQLException {
//        return ShardingDataSourceFactory.createDataSource(shardingRule);
//    }
//
//    /**
//     * 需要手动配置事务管理器
//     *
//     * @param dataSource
//     * @return
//     */
//    @Bean
//    public DataSourceTransactionManager transactitonManager(@Qualifier("dataSource") DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean(name = "test1SqlSessionFactory")
//    @Primary
//    public SqlSessionFactory testSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        return bean.getObject();
//    }
//
//    @Bean(name = "test1SqlSessionTemplate")
//    @Primary
//    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }

    @Bean(name = "shardingDataSource")
    DataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig;
        shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getUserTableRuleConfiguration());
//        shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        shardingRuleConfig.getBindingTableGroups().add("user_info");
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", DemoDatabaseShardingAlgorithm.class.getName()));
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("user_id", DemoTableShardingAlgorithm.class.getName()));
//        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("order_id", DemoTableShardingAlgorithm.class.getName()));
        return new ShardingDataSource(shardingRuleConfig.build(createDataSourceMap()));
//        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig);
    }

    @Bean
    TableRuleConfiguration getUserTableRuleConfiguration() {
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("user_info");
        orderTableRuleConfig.setActualDataNodes("user_${0..1}.user_info_${0..1}");
        orderTableRuleConfig.setKeyGeneratorColumnName("user_id");
        return orderTableRuleConfig;
    }

//    TableRuleConfiguration getOrderItemTableRuleConfiguration() {
//        TableRuleConfiguration orderItemTableRuleConfig = new TableRuleConfiguration();
//        orderItemTableRuleConfig.setLogicTable("t_order_item");
//        orderItemTableRuleConfig.setActualDataNodes("demo_ds_${0..1}.t_order_item_${0..1}");
//        return orderItemTableRuleConfig;
//    }

    /**
     * 需要手动配置事务管理器
     *
     * @param shardingDataSource
     * @return
     */
    @Bean
    public DataSourceTransactionManager transactitonManager(DataSource shardingDataSource) {
        return new DataSourceTransactionManager(shardingDataSource);
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(DataSource shardingDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(shardingDataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return bean.getObject();
    }

    @Bean
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    private Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("user_0", createDataSource("user"));
        result.put("user_1", createDataSource("user_1"));
        return result;
    }

    private DataSource createDataSource(final String dataSourceName) {
        BasicDataSource result = new BasicDataSource();
        result.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
        result.setUrl(String.format("jdbc:mysql://localhost:3306/%s", dataSourceName));
        result.setUsername("root");
        result.setPassword("123456");
        return result;
    }

}
