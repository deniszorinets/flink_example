FROM flink:latest

RUN mkdir /opt/flink/plugins/s3-fs-hadoop && \
    cp /opt/flink/opt/flink-s3-fs-hadoop-1.17.1.jar /opt/flink/plugins/s3-fs-hadoop/flink-s3-fs-hadoop-1.17.1.jar