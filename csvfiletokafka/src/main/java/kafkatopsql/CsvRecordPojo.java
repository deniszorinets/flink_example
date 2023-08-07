package kafkatopsql;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"ym", "importExport", "hsCode", "customs", "country", "q1", "q2", "value"})
public class CsvRecordPojo {
    public String ym;
    public short importExport;
    public int hsCode;
    public int customs;
    public String country;
    public long q1;
    public long q2;
    public long value;

    public CsvRecordPojo() {}
}