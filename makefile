
JSRC = \
       Alert.java \
       Alerts.java \
       Config.java \
       ConfigException.java \
       ConfigMap.java \
       ConfigKey.java \
       CountdownPanel.java \
       Deadman.java \
       Mailer.java \
       SoundAlert.java \
       Status.java \

RESOURCES = \
            onscreen.wav \
            redalert.wav \

JARFILE = deadman.jar
MAINCLASS = uk.ac.bristol.star.deadman.Deadman
JAVAMAIL_JAR = javax.mail-1.5.5.jar

build: $(JARFILE) javadocs

run: build
	java -jar $(JARFILE) reset=10 warning=5

javadocs: $(JSRC)
	rm -rf $@
	mkdir $@
	javadoc -classpath $(JAVAMAIL_JAR) -quiet -d $@ $(JSRC)

clean:
	rm -rf tmp tmp.manifest $(JARFILE) javadocs

$(JARFILE): $(JSRC) $(RESOURCES) $(JAVAMAIL_JAR)
	rm -rf tmp
	mkdir tmp
	javac -classpath $(JAVAMAIL_JAR) -d tmp $(JSRC)
	cp $(RESOURCES) tmp/uk/ac/bristol/star/deadman/
	cd tmp && jar xf ../$(JAVAMAIL_JAR) javax/mail com/sun/mail
	echo "Main-Class: $(MAINCLASS)" >tmp.manifest
	cd tmp && jar cmf ../tmp.manifest ../$@ .
	rm -rf tmp tmp.manifest

$(JAVAMAIL_JAR):
	curl 'https://maven.java.net/content/repositories/releases/com/sun/mail/javax.mail/1.5.5/javax.mail-1.5.5.jar' >$@

