package kafkatopsql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
public class CsvRecordNormalizedPojo {
    @Getter
    @Setter
    private Date ym;
    @Getter
    @Setter
    private boolean importExport;
    @Getter
    @Setter
    private int hsCode;
    @Getter
    @Setter
    private int customs;
    @Getter
    @Setter
    private String country;
    @Getter
    @Setter
    private long q1;
    @Getter
    @Setter
    private long q2;
    @Getter
    @Setter
    private long value;
}