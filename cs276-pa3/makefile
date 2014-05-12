CLASS_DIR = out/production/cs276-pa3

all:
	rm -rf $(CLASS_DIR)
	mkdir -p $(CLASS_DIR)
	find . -name '*.java' -and -not -name '.*' -print0 | xargs -0 javac -d $(CLASS_DIR)
clean:
	rm -rf $(CLASS_DIR)

