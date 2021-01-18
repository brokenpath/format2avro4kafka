package example.benchmarks

import java.util.concurrent.atomic.AtomicLong

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import scala.util.Random
import example.Hello
import org.example.SmallSchema

@State(Scope.Thread)
class TestBenchmark() {
  private val value = new AtomicLong()
  private val lines = 1000*10 
  private val fields = 4
  private val emptyChance = 0.0d
  private val pieces = Array("abe", "nul", "ae", "ma", "in", "et", "te", "ot", "ser", "nut", "ca", "it", "ti", "wav") 
  private val newline  = '\n'.toString()

  def randomCombine(parts: Int) =
    1.to(parts).map( i =>
      if(Random.nextDouble() >= emptyChance) 
        pieces(Random.nextInt(pieces.length))
      else 
        "")
        .reduce( (acc, v) => acc + v )
  
  val bigString = 1.to(lines).map( _ =>
    1.to(fields).map( i =>  randomCombine(4)).reduce((acc, v) => acc + "|" +v)
  ).reduce((acc, v) => acc + newline + v )

  private val input =  new ByteArrayInputStream(bigString.getBytes(StandardCharsets.UTF_8))
  println(bigString.length())
  @Benchmark
  def mappingbench(bh: Blackhole) : Unit = {
    val s = new SmallSchema()
    val reader = Hello.setReader(s, input)
    while (reader.hasNext()){
      bh.consume(reader.next())
    }
    input.reset()

  }

  
}
