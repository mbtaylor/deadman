
JSRC = \
       Alert.java \
       Alerts.java \
       ConfigControl.java \
       ConfigException.java \
       ConfigMap.java \
       ConfigKey.java \
       ConfigPanel.java \
       CountdownLabel.java \
       CountdownModel.java \
       CountdownPanel.java \
       Deadman.java \
       DmConfig.java \
       DmPanel.java \
       ExitPanel.java \
       FormPanel.java \
       Logging.java \
       Mailer.java \
       ResetSlider.java \
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
	java -jar $(JARFILE)

deadman.props: $(JARFILE)
	java -jar $(JARFILE) -writeconfig >$@

javadocs: $(JSRC)
	rm -rf $@
	mkdir $@
	javadoc -classpath $(JAVAMAIL_JAR) -quiet -d $@ $(JSRC)

clean:
	rm -rf tmp tmp.manifest version.txt $(JARFILE) javadocs

$(JARFILE): $(JSRC) $(RESOURCES) version.txt $(JAVAMAIL_JAR)
	rm -rf tmp
	mkdir tmp
	javac -Xlint:unchecked -classpath $(JAVAMAIL_JAR) -d tmp $(JSRC)
	cp $(RESOURCES) version.txt tmp/uk/ac/bristol/star/deadman/
	cd tmp && jar xf ../$(JAVAMAIL_JAR) javax/mail com/sun/mail
	echo "Main-Class: $(MAINCLASS)" >tmp.manifest
	cd tmp && jar cmf ../tmp.manifest ../$@ .
	rm -rf tmp tmp.manifest version.txt

$(JAVAMAIL_JAR):
	curl 'https://maven.java.net/content/repositories/releases/com/sun/mail/javax.mail/1.5.5/javax.mail-1.5.5.jar' >$@

version.txt:
	echo `git show -s --format=%h` \
             "("`git show -s --format=%ci | sed 's/ .*//'`")" \
             >$@
	if git status --porcelain | grep -q '^[MADRU ][MADRU ]'; \
        then \
	   echo "`cat $@` [modified]" >$@; \
        fi
       

