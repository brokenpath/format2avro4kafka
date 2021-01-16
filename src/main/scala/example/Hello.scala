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
object Hello extends Greeting with App {
  val s = new SmallSchema()
  
  val f = new File("/home/maov/avro/small.csv")
  val mapper = new CsvMapper();
  mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)
  val schema = mapper.schemaFor(s.getClass())
  val reader = mapper.readerFor(s.getClass()).`with`(schema).readValues[SmallSchema](f)
  val datumWriter = new SpecificDatumWriter[SmallSchema]()
  val dataFileWriter = new DataFileWriter[SmallSchema](datumWriter)
  val codec = CodecFactory.snappyCodec()
  val writer = dataFileWriter
    .setCodec(codec)
    .create(s.getSchema(),new File("/home/maov/avro/tmp.avro"))
  val it = reader.asScala
  val before = System.currentTimeMillis()
  it.foreach(small => writer.append(small))
  val after = System.currentTimeMillis()
  println("Time take " + (after - before).toString())
  
}

trait Greeting {
  lazy val greeting: String = "hello"
}