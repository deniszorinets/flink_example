package kafkatopsql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.formats.csv.CsvReaderFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class CsvToKafkaJob {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		CsvReaderFormat<CsvRecordPojo> csvFormat = CsvReaderFormat.forPojo(CsvRecordPojo.class);
		FileSource<CsvRecordPojo> source = FileSource.forRecordStreamFormat(csvFormat, new Path("s3", "test", "/custom_1988_2020.csv")).build();
		ObjectMapper objectMapper = new ObjectMapper();
		KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
				.setBootstrapServers("kafka:9092")
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic("csvfile")
						.setValueSerializationSchema(new SimpleStringSchema())
						.build()
				)
				.setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
				.build();

		DataStream<CsvRecordPojo> csvRecords = env
				.fromSource(
						source,
						WatermarkStrategy.noWatermarks(),
						"File"
				);
		csvRecords
				.map(CsvToKafkaJob::normalizeCsvRecord)
				.map(value -> objectMapper.writeValueAsString(value))
				.sinkTo(kafkaSink);

		env.execute("Csv File To Kafka");
	}

	private static CsvRecordNormalizedPojo normalizeCsvRecord(CsvRecordPojo record) {
		CsvRecordNormalizedPojo result = new CsvRecordNormalizedPojo();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		try {
			result.setYm(new Date(formatter.parse(record.ym).getTime()));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		result.setImportExport(record.importExport == 1);
		result.setHsCode(record.hsCode);
		result.setCustoms(record.customs);
		result.setCountry(record.country);
		result.setQ2(record.q1);
		result.setQ2(record.q2);
		result.setValue(record.value);
		return result;
	}
}
