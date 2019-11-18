// This #include statement was automatically added by the Particle IDE.
#include <google-maps-device-locator.h>

// This #include statement was automatically added by the Particle IDE.
#include <ArduinoJson.h>

// This #include statement was automatically added by the Particle IDE.
#include "Adafruit_DHT_Particle.h"

// Example testing sketch for various DHT humidity/temperature sensors
// Written by ladyada, public domain

#define DHTPIN D2     // what pin we're connected to


// Uncomment whatever type you're using!
#define DHTTYPE DHT11		// DHT 11
//#define DHTTYPE DHT22		// DHT 22 (AM2302)
//#define DHTTYPE DHT21		// DHT 21 (AM2301)

// Connect pin 1 (on the left) of the sensor to +5V
// Connect pin 2 of the sensor to whatever your DHTPIN is
// Connect pin 4 (on the right) of the sensor to GROUND
// Connect a 10K resistor from pin 2 (data) to pin 1 (power) of the sensor
GoogleMapsDeviceLocator locator;
DHT dht(DHTPIN, DHTTYPE);
int LEDPIN = D6;
int SUCCESSPIN = D5;
int loopCount;
double humidity;
double temperatureC;
double temperature;
double hi;
double dp;
double k;


void setup() {
	Serial.begin(9600);
	//locator.withSubscribe(locationCallback).withLocatePeriodic(999);
	//locator.withEventName("deviceLocation-electron");

	Serial.println("DHT11 test!");
	Particle.publish("state", "DHT11 started with no errors");
	Particle.variable("temperature", temperature);
	Particle.variable("humidity", humidity);
	Particle.variable("temperatureC", temperatureC);
	Particle.variable("heatIndex", hi);
	Particle.variable("dewPoint", dp);
	Particle.variable("temperatureK", k);
	Particle.function("publish", pub);
	Particle.function("toggle-led", toggle);
	Particle.function("locate-device", locate);
	pinMode(LEDPIN, OUTPUT);
	pinMode(SUCCESSPIN, OUTPUT);
    digitalWrite(LEDPIN,LOW);
    digitalWrite(SUCCESSPIN, LOW);
	dht.begin();

}

void locationCallback(float lat, float lon, float accuracy) {
  // Handle the returned location data for the device. This method is passed three arguments:
  // - Latitude
  // - Longitude
  // - Accuracy of estimated location (in meters)
}


void loop() {
// Wait a few seconds between measurements.
	delay(3000);
	//locator.loop();

// Reading temperature or humidity takes about 250 milliseconds!
// Sensor readings may also be up to 2 seconds 'old' (its a
// very slow sensor)
	humidity = dht.getHumidity();
// Read temperature as Celsius
	temperatureC = dht.getTempCelcius();
// Read temperature as Farenheit
	temperature = dht.getTempFarenheit();

// Compute heat index
// Must send in temp in Fahrenheit!
	 hi = dht.getHeatIndex();
	 dp = dht.getDewPoint();
	 k = dht.getTempKelvin();

	 //Serial.println("loop iter");

	 //Add anything else you want the sensor to do continuously...


	}

int toggle(String state) {

    if (state=="ON"){
        digitalWrite(LEDPIN,HIGH);
        return 1;
    }

    if (state=="OFF"){
        digitalWrite(LEDPIN,LOW);
        return 0;
    }

    if (state=="SUCCESS"){
        digitalWrite(SUCCESSPIN, HIGH);
        delay(500);
        digitalWrite(SUCCESSPIN, LOW);
        Serial.println("Success");
        return 2;
    }
    else{
        digitalWrite(SUCCESSPIN, HIGH);
        delay(1000);
        digitalWrite(SUCCESSPIN, LOW);
        delay(200);
        digitalWrite(LEDPIN, HIGH);
        delay(200);
        digitalWrite(LEDPIN, LOW);
        delay(200);
        digitalWrite(LEDPIN, HIGH);
        delay(200);
        digitalWrite(LEDPIN, LOW);
        Serial.println("toggled");
        return 3;
    }


}

int locate(String user){
//may want to change user ...
    if (user=="vvagias"){
        locator.publishLocation();
        toggle("SUCCESS");
        return 9;
    }

    else {
        return 3;
    }

}


int pub(String user) {
    toggle("OFF");


    toggle("ON");
  //Print data
	String tStamp = Time.timeStr();
	String lmsg = String::format("%4.2f", temperature);


	Particle.publish("readings", lmsg);

	delay(1000);
	toggle("OFF");
	//toggle("SUCCESS");
	return 0;

}
