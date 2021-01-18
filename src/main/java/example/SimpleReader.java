package example;


import java.io.InputStream;
import com.fasterxml.jackson.dataformat.csv.*;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader; 

import java.io.IOException;

public class SimpleReader {


    public static MappingIterator<String[]>  getReader(InputStream input) throws IOException{
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = getSchema(mapper);
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        return mapper.readerFor(String[].class).with(schema).readValues(input);
    }
        
    public static CsvSchema getSchema(CsvMapper mapper) {
    return mapper.typedSchemaFor(String[].class)
        .withColumnSeparator('|')
        .withoutHeader()
        .withoutQuoteChar();
    }

}