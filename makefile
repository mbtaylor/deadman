
JSRC = \
       Alert.java \
       Alerts.java \
       CountdownPanel.java \
       Deadman.java \
       Mailer.java \
       SoundAlert.java \
       Status.java \

RESOURCES = \
            onscreen.wav \
            redalert.wav \

JARFILE = deadman.jar
MAINCLASS = Deadman
JAVAMAIL_JAR = javax.mail-1.5.5.jar

build: $(JARFILE)

run: build
	java -jar $(JARFILE) -limit 10 -warning 5

clean:
	rm -rf tmp tmp.manifest $(JARFILE)

$(JARFILE): $(JSRC) $(RESOURCES) $(JAVAMAIL_JAR)
	rm -rf tmp
	mkdir tmp
	javac -classpath $(JAVAMAIL_JAR) -d tmp $(JSRC)
	cp $(RESOURCES) tmp/
	cd tmp && jar xf ../$(JAVAMAIL_JAR) javax/mail com/sun/mail
	echo "Main-Class: $(MAINCLASS)" >tmp.manifest
	cd tmp && jar cmf ../tmp.manifest ../$@ .
	rm -rf tmp tmp.manifest

$(JAVAMAIL_JAR):
	curl 'https://maven.java.net/content/repositories/releases/com/sun/mail/javax.mail/1.5.5/javax.mail-1.5.5.jar' >$@

