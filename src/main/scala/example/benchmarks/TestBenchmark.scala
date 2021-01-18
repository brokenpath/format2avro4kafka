package example.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.{Setup, TearDown}
import org.openjdk.jmh.infra.Blackhole
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import scala.util.Random
import example.Hello
import org.example.SmallSchema
import org.example.WideSchema

@State(Scope.Thread)
class TestBenchmark() {
  private val lines = 2500 
  private val fields = 4
  private val pieces = Array("abe", "nul", "ae", "ma", "in", "et", "te", "ot", "ser", "nut", "ca", "it", "ti", "wav") 
  private val newline  = '\n'.toString()

  private var smallSchemaString : String = null
  private var fullWideSchemaString : String = null
  private var emptyWideSchemaString : String = null

  @Setup
  def prepare() = {
    if(smallSchemaString == null) {
          this.smallSchemaString = 1.to(lines).map( _ =>
        1.to(fields).map( i =>  randomCombine(4, 0.0d)).reduce((acc, v) => acc + "|" +v)
      ).reduce((acc, v) => acc + newline + v )
      println(smallSchemaString.length() * 2)
    }

    if(fullWideSchemaString == null) {
      this.fullWideSchemaString = 1.to(20).map( _ =>
        1.to(500).map( i =>  randomCombine(4, 0.0d)).reduce((acc, v) => acc + "|" +v)
      ).reduce((acc, v) => acc + newline + v )

      println(fullWideSchemaString.length() * 2)
    }

    if(emptyWideSchemaString == null) {
      this.emptyWideSchemaString = 1.to(100).map( _ =>
        1.to(500).map( i =>  randomCombine(4, 0.90d)).reduce((acc, v) => acc + "|" +v)
      ).reduce((acc, v) => acc + newline + v )
      println(emptyWideSchemaString.length() * 2)
    }
  }



  @TearDown
  def reset() = () //Nothing to teardown yet, streams are created and closed in each call to simulate files

  def randomCombine(parts: Int, emptyChance : Double) : String=
    1.to(parts).map( i =>
      if(Random.nextDouble() >= emptyChance) 
        pieces(Random.nextInt(pieces.length))
      else 
        "")
        .reduce( (acc, v) => acc + v )
  
  @Benchmark
  def smallSchemaFromUTF8(bh: Blackhole) : Unit = {
    var smallSchemaStream =  new ByteArrayInputStream(smallSchemaString.getBytes(StandardCharsets.UTF_8))
    
    val s = new SmallSchema()
    val reader = Hello.setReader(s, smallSchemaStream)
    while (reader.hasNext()){
      bh.consume(reader.next())
    }
    smallSchemaStream.close()
    
  }

  @Benchmark
  def wideSchemaFromUTF8(bh: Blackhole) : Unit = {
    val fullWideSchemaStream = new ByteArrayInputStream(fullWideSchemaString.getBytes(StandardCharsets.UTF_8))
    
    val s = new WideSchema()
    val reader = Hello.setReader(s, fullWideSchemaStream)
    while (reader.hasNext()){
      bh.consume(reader.next())
    }
    fullWideSchemaStream.close()
    
  }


  @Benchmark
  def emptyWideSchemaFromUTF8(bh: Blackhole) : Unit = {
    val emptyWideSchemaStream = new ByteArrayInputStream(emptyWideSchemaString.getBytes(StandardCharsets.UTF_8))
    val s = new WideSchema()
    val reader = Hello.setReader(s, emptyWideSchemaStream)
    while (reader.hasNext()){
      bh.consume(reader.next())
    }
    emptyWideSchemaStream.close()
   
  }


  @Benchmark
  def emptyStringReader(bh:Blackhole) : Unit = {
    val emptyWideSchemaStream = new ByteArrayInputStream(emptyWideSchemaString.getBytes(StandardCharsets.UTF_8))
    val reader = example.SimpleReader.getReader(emptyWideSchemaStream)
    while (reader.hasNext()){
      val arr = reader.next()
      var w = new WideSchema()
      var i = 0
      while ( i < 500) {  w.put(i,arr(i)); i = i + 1}
      bh.consume(w)
    }
    emptyWideSchemaStream.close()
  }

  
}
