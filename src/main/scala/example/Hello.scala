package example
import org.example.SmallSchema
import org.apache.avro.util.Utf8
import scala.collection.JavaConverters._
import java.io.File
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.CodecFactory
import org.xerial.snappy.Snappy;
import com.fasterxml.jackson.dataformat.csv.CsvParser
import java.io.InputStream
import com.fasterxml.jackson.databind.DeserializationFeature
import com.linkedin.avro.fastserde.FastSpecificDatumWriter
import java.io.OutputStream
import org.apache.avro.Schema
import org.apache.avro.specific.SpecificRecordBase
import com.fasterxml.jackson.databind.MappingIterator
import com.linkedin.avroutil1.compatibility.AvroCompatibilityHelper
import java.io.ByteArrayOutputStream


object Hello extends Greeting with App {
  def setReader[T](obj : T, in : InputStream) = {
    val mapper = new CsvMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val schema = mapper.schemaFor(obj.getClass()).withColumnSeparator('|')
    mapper.readerFor(obj.getClass()).`with`(schema).readValues[T](in)
  }

  def write[T <: SpecificRecordBase](out: OutputStream, schema : Schema, iterator: MappingIterator[T]) = {
    val writer = new FastSpecificDatumWriter[T](schema)
    //needs resource management
    val encoder = AvroCompatibilityHelper.newBinaryEncoder(out, true, null)
    while(iterator.hasNext()){
      writer.write(iterator.next(), encoder)
    }
  }

  def stringReader(in: InputStream) = {
    val s =  Array[String]()
    val mapper = new CsvMapper()
    mapper.schema().withColumnSeparator('|')
    mapper.readerFor(s.getClass()).readValues[Array[String]] (in)
  }

  def test_file(csvPath: String, avroPath : String) : Unit = {
    val s = new SmallSchema()
    
    val f = new File(csvPath)
    val mapper = new CsvMapper();
    val schema = mapper.schemaFor(s.getClass())
    val reader = mapper.readerFor(s.getClass()).`with`(schema).readValues[SmallSchema](f)
    val datumWriter = new SpecificDatumWriter[SmallSchema]()
    val dataFileWriter = new DataFileWriter[SmallSchema](datumWriter)
    val codec = CodecFactory.snappyCodec()
    val writer = dataFileWriter
      .setCodec(codec)
      .create(s.getSchema(),new File(avroPath))
    val it = reader.asScala
    val before = System.currentTimeMillis()
    it.foreach(small => writer.append(small))
    val after = System.currentTimeMillis()
    println("Time take " + (after - before).toString())
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
