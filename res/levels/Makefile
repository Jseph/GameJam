files=$(patsubst %.svg,%.dat,$(wildcard *.svg))

default: $(files)

%.dat: %.svg
	../../tools/lparse.py $< > $@

