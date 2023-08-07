package kafkatopsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;


public class KafkaToPsql {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		ObjectMapper objectMapper = new ObjectMapper();
		KafkaSource<String> source = KafkaSource.<String>builder()
				.setBootstrapServers("kafka:9092")
				.setTopics("csvfile")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new SimpleStringSchema())
				.build();

		SinkFunction<CsvRecordNormalizedPojo> sink = JdbcSink.sink(
				"insert into japan_trade_stats_from_1988_to_2019 (ym, import_export, hs_code, customs, country, q1, q2, value) values (?, ?, ?, ?, ?, ?, ?, ?)",
				(statement, record) -> {
					statement.setDate(1, record.getYm());
					statement.setBoolean(2, record.isImportExport());
					statement.setInt(3, record.getHsCode());
					statement.setInt(4, record.getCustoms());
					statement.setString(5, record.getCountry());
					statement.setLong(6, record.getQ1());
					statement.setLong(7, record.getQ2());
					statement.setLong(8, record.getValue());
				},
				JdbcExecutionOptions.builder()
						.withBatchSize(10000)
						.withBatchIntervalMs(300)
						.withMaxRetries(5)
						.build(),
				new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
						.withUrl("jdbc:postgresql://timescaledb:5432/postgres")
						.withDriverName("org.postgresql.Driver")
						.withUsername("postgres")
						.withPassword("postgres")
						.build()
		);

		DataStream<String> csvRecords = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka");
		csvRecords.map(value -> objectMapper.readValue(value, CsvRecordNormalizedPojo.class)).addSink(sink);

		env.execute("Kafka to Psql");
	}

}
