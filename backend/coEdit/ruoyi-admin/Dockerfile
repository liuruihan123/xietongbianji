#基础镜像
FROM findepi/graalvm:java17-native
# 设置时区为上海
ENV TZ Asia/Shanghai

MAINTAINER zuleed
#拷贝配置文件
COPY ./application-prod.yml /config/application-prod.yml

RUN mkdir -p /ruoyi/server/logs \
    /ruoyi/server/temp \
    /ruoyi/skywalking/agent


#工作空间
WORKDIR /ruoyi/server

ENV SERVER_PORT=8500

EXPOSE ${SERVER_PORT}

ADD ./ruoyi-admin.jar ./app.jar

ENTRYPOINT ["java", \
            "-Dfile.encoding=UTF-8",\
            "-Djava.security.egd=file:/dev/./urandom", \
            "-Dserver.port=${SERVER_PORT}", \
            # 应用名称 如果想区分集群节点监控 改成不同的名称即可
#            "-Dskywalking.agent.service_name=ruoyi-server", \
#            "-javaagent:/ruoyi/skywalking/agent/skywalking-agent.jar", \
            "-jar", "app.jar", "--spring.config.location=/config/application-prod.yml"]
