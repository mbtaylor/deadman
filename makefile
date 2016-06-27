
JSRC = \
       Alert.java \
       CountdownPanel.java \
       Deadman.java \
       Status.java \

RESOURCES = \
            onscreen.wav \
            redalert.wav \

JARFILE = deadman.jar
MAINCLASS = Deadman

build: $(JARFILE)

run: build
	java -jar $(JARFILE) -limit 10 -warning 5

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

