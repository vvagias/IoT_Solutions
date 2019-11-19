/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vv

import java.util.{Properties, StringTokenizer}

import org.apache.flink.api.common.functions.{FlatMapFunction, MapFunction}
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParseException
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.flink.util.Collector
import org.apache.nifi.remote.client.{SiteToSiteClient, SiteToSiteClientConfig}
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import scala.collection.mutable.ListBuffer


/**
 * Skeleton for a Flink Streaming Job.
 *
 * For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
object StreamingJob {

  def main(args: Array[String]) {
    // set up the streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment


    // get input data

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    // only required for Kafka 0.8
    //properties.setProperty("zookeeper.connect", "localhost:2181")
    properties.setProperty("group.id", "FlinkTemp")

    val myConsumer = new FlinkKafkaConsumer[ObjectNode]("highTemp", new JSONKeyValueDeserializationSchema(false), properties)

    val stream = env.addSource(myConsumer)




    // make parameters available in the web interface


    // get input data

    //example data
    /*

{"event":"readings","data":"59.80","published_at":"2019-11-14T15:40:51.983Z","coreid":"330032001247373333353132"}
{"event":"readings","data":"79.80","published_at":"2019-11-14T15:40:51.983Z","coreid":"330032001247373333353132"}
{"event":"readings","data":"99.80","published_at":"2019-11-14T15:40:51.983Z","coreid":"330032001247373333353132"}
    */

// Tumble Count Window
//(bytes.map(_.toChar)).mkString
    // Print incoming Kafka data

    // Sum over 3 reading windows

    //println(stream.toString())

    try {
      stream.map(new MapFunction[ObjectNode, (String, Double)]() {
        @throws[Exception]
        override def map(node: ObjectNode): (String, Double) = (node.get("value").get("coreid").asText(), node.get("value").get("data").asDouble())
      })
        .keyBy(0)
        .countWindow(3)
        .sum(1).print()

      //Print max value temperature in the window...
      stream.map(new MapFunction[ObjectNode, (String, Double)]() {
        @throws[Exception]
        override def map(node: ObjectNode): (String, Double) = (node.get("value").get("coreid").asText(), node.get("value").get("data").asDouble())
      })
        .keyBy(0)
        .countWindow(3)
        .maxBy(1).print()

      stream.map(new MapFunction[ObjectNode, (String, Double)]() {
        @throws[Exception]
        override def map(node: ObjectNode): (String, Double) = (node.get("value").get("coreid").asText(), node.get("value").get("data").asDouble())
      })
        .keyBy(0).print()

    }
    catch {
      case x: JsonParseException =>
      {

        // Display this if exception is found
        println("Exception: data does not contain valid value field... Try again.")
      }
    }



   // stream.keyBy("value").countWindow(100)
   // stream.keyBy("*").countWindow(100).sum("Temp").print()
   // val tempsCnt: DataStream[ObjectNode] = stream
     // .keyBy("User")
     // .countWindow(100)
     // .sum("Temps")



    //println("Printing result to stdout. Use --output to specify output path.")

    //tempsCnt.print()



    // execute program
    env.execute("Kafka Streaming Example")
  }


}
