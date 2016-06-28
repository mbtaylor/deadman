# deadman

Dead man's alarm 

Simple java application suggested by Ben Maughan.

Posts a GUI countdown timer on the screen, when it reaches zero it
sounds and alarm or something.

The intention is to use it in the observatory to guard against
people having an accident and not being able to raise the alarm.

## Build/run

A makefile is included.  On a Un*x system, just do
```
   make build
```
to create the jar file (`deadman.jar`).
Then you can run it with
```
   java -jar deadman.jar
```

There are various flags available from the command line.
do
```
   java -jar deadman.jar -h
```
to see what they all are.


