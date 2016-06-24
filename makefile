
JSRC = \
       Alarm.java \
       CountdownPanel.java \
       Deadman.java \

RESOURCES = \
            redalert.wav \

JARFILE = deadman.jar
MAINCLASS = Deadman

build: $(JARFILE)

run: build
	java -jar $(JARFILE)

clean:
	rm -rf tmp tmp.manifest $(JARFILE)

$(JARFILE): $(JSRC) $(RESOURCES)
	rm -rf tmp
	mkdir tmp
	javac -d tmp $(JSRC)
	cp $(RESOURCES) tmp/
	echo "Main-Class: $(MAINCLASS)" >tmp.manifest
	cd tmp && jar cmf ../tmp.manifest ../$@ .
	rm -rf tmp tmp.manifest

