import rwmidi.*;
import processing.core.*;


class Cuerda {
  static int attackThreshold; 
  static int minTimeBetweenAttacks;
  static int thres;
  
  static boolean useCustomCallibration; 
  static boolean doSmooth;
  
  int customThres;
  
  
  int note; 
  int velocity;
  int noteDuration;
  int minDuration;
  int maxDuration;
   
  
  int lastNoteOn;
  
  //int[] prevValues;
  int prevValue;
  int lookAt;
  int currentValue;
  int maxValue;
  int minValue;
  
  MidiOutput output;
  int channel;

  Cuerda() {
    lastNoteOn = 0;
    noteDuration = 100;
    minDuration = 70;
    maxDuration = 160;

    attackThreshold = 5; 
    minTimeBetweenAttacks = 200;
    thres = 80;
    customThres = thres;
    useCustomCallibration = true;
    
    //prevValues = new int[50];
  }

  boolean timeToTurnOff(int curTime) {
    return curTime - lastNoteOn > noteDuration;
  }
  
  void update(int curTime){
    if(timeToTurnOff(curTime)){
        output.sendNoteOn(channel, note, 0);
      }
  }
  
  int map(int inVal, int inMinVal, int inMaxVal, int outMinVal, int outMaxVal){
   int outVal = (int)inVal * (inMaxVal - inMinVal) / (outMaxVal - outMinVal);
   return outVal;
    
  }

  void setNewValue(int newValue, int curTime) {

    prevValue = currentValue;
    currentValue = newValue;

    if (newValue > maxValue)
    {
      maxValue = newValue;
    }
    if (newValue < minValue) {
      minValue = newValue;
    }
    velocity = (int) map(currentValue, minValue, maxValue, 50, 127);
    
    noteDuration = (int) map(currentValue, minValue, maxValue, minDuration, maxDuration);

    //boolean sendNoteOn =   currentValue - prevValue > attackThreshold
    //                    && parent.millis() - lastNoteOn > minTimeBetweenAttacks;
    
    boolean sendNoteOn =  currentValue > getThreshold()//getValue
                          && currentValue - prevValue > attackThreshold //ojo: esto rompe la idea del smooth?
                          && curTime - lastNoteOn > minTimeBetweenAttacks;

    if (sendNoteOn) {
      output.sendNoteOn(channel, note, 127); //ojo con el channel: antes le sumaba uno por alguna razón. //hack: velocity

      lastNoteOn = curTime;
    }
  }
  
  int getValue(){
    if(doSmooth){
      return currentValue + prevValue / 2;
    }
    else{
      return currentValue;
    }
  }
  
  int getThreshold(){
    int threshold;
    if(useCustomCallibration){
      threshold = customThres;
    }
    else{
      threshold = thres;
    }
    return threshold;
  }
}

