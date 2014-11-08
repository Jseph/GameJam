SRC=src
BIN=bin
SRCFILES=$(wildcard $(SRC)/*.java)
JFLAGS=

default: build

# no attempt at an efficient build system is made
build:
	javac $(JFLAGS) -d $(BIN) $(SRCFILES)

run:
	java -cp $(BIN) MainWindow

clean:
	rm -f $(BIN)/*
